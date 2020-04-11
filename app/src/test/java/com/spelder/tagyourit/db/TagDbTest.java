package com.spelder.tagyourit.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.Color;
import androidx.test.core.app.ApplicationProvider;
import com.spelder.tagyourit.model.ListIcon;
import com.spelder.tagyourit.model.ListProperties;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.model.TrackComponents;
import com.spelder.tagyourit.model.VideoComponents;
import com.spelder.tagyourit.networking.api.SortBuilder;
import com.spelder.tagyourit.networking.api.SortBy;
import com.spelder.tagyourit.networking.api.filter.Collection;
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;
import com.spelder.tagyourit.networking.api.filter.Key;
import com.spelder.tagyourit.pitch.Pitch;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class TagDbTest {
  private Context context = ApplicationProvider.getApplicationContext();
  private TagDb db;
  private static int tagId = 0;

  @Before
  public void before() {
    tagId++;
    db = new TagDb(context);
  }

  @After
  public void after() {
    db.close();
    context.deleteDatabase(TagDbHelper.DATABASE_NAME);
  }

  @Test
  public void addTag_MinimalData() {
    // Given created tag
    Tag tag = createTestTag_MinimalData("Test");
    tag.addVideo(createTestVideo_MinimalData());
    addTestTrack(tag);

    // When tag inserted and retrieved
    ListProperties defaultList = db.getDefaultList();
    long result = db.insertDefaultList(tag);

    List<Tag> resultTagList = db.getTagsForList(defaultList.getDbId());
    Tag resultTag = getTagWithId(resultTagList, tagId);
    boolean entriesDefaultList = db.hasEntriesInDefaultList();

    // Then resulting data is the same
    assertTrue(result > -1);
    assertNotNull(resultTag);
    checkTestTag_MinimalData(resultTag, "Test");
    assertEquals(1, resultTag.getVideos().size());
    checkTestVideo_MinimalData(resultTag.getVideos().get(0));
    assertEquals(1, resultTag.getTracks().size());
    checkTestTrack(resultTag.getTracks().get(0));
    assertTrue(entriesDefaultList);
  }

  @Test
  public void updateTag_MinimalData() {
    // Given created tag
    Tag tag = createTestTag_MinimalData("Test");

    long result = db.insertDefaultList(tag);

    ListProperties defaultList = db.getDefaultList();

    // When tag updated and retrieved
    List<Tag> updateTags = new ArrayList<>();
    tag = createTestTag_MinimalData("Test2");
    tag.addVideo(createTestVideo_MinimalData());
    addTestTrack(tag);
    updateTags.add(tag);
    db.updateTags(updateTags);

    List<Tag> resultTagList = db.getTagsForList(defaultList.getDbId());
    Tag resultTag = getTagWithId(resultTagList, tagId);

    // Then resulting data is the same
    assertTrue(result > -1);
    assertNotNull(resultTag);
    checkTestTag_MinimalData(resultTag, "Test2");
    assertEquals(1, resultTag.getVideos().size());
    checkTestVideo_MinimalData(resultTag.getVideos().get(0));
    assertEquals(1, resultTag.getTracks().size());
    checkTestTrack(resultTag.getTracks().get(0));
  }

  @Test
  public void addTag_NormalData() {
    // Given created tag
    Tag tag = createTestTag_NormalData("Test");
    tag.addVideo(createTestVideo_NormalData());
    addTestTrack(tag);

    // When tag inserted and retrieved
    long result = db.insertDefaultList(tag);

    ListProperties defaultList = db.getDefaultList();
    List<Tag> resultTagList = db.getTagsForList(defaultList.getDbId());
    Tag resultTag = getTagWithId(resultTagList, tagId);

    // Then resulting data is the same
    assertTrue(result > -1);
    assertNotNull(resultTag);
    checkTestTag_NormalData(resultTag, "Test");
    assertEquals(1, resultTag.getVideos().size());
    checkTestVideo_NormalData(resultTag.getVideos().get(0));
    assertEquals(1, resultTag.getTracks().size());
    checkTestTrack(resultTag.getTracks().get(0));
  }

  @Test
  public void updateTag_NormalData() {
    // Given created tag
    Tag tag = createTestTag_NormalData("Test");

    long result = db.insertDefaultList(tag);

    ListProperties defaultList = db.getDefaultList();

    // When tag updated and retrieved
    List<Tag> updateTags = new ArrayList<>();
    tag = createTestTag_NormalData("Test2");
    tag.addVideo(createTestVideo_NormalData());
    addTestTrack(tag);
    updateTags.add(tag);
    db.updateTags(updateTags);

    List<Tag> resultTagList = db.getTagsForList(defaultList.getDbId());
    Tag resultTag = getTagWithId(resultTagList, tagId);

    // Then resulting data is the same
    assertTrue(result > -1);
    assertNotNull(resultTag);
    checkTestTag_NormalData(resultTag, "Test2");
    assertEquals(1, resultTag.getVideos().size());
    checkTestVideo_NormalData(resultTag.getVideos().get(0));
    assertEquals(1, resultTag.getTracks().size());
    checkTestTrack(resultTag.getTracks().get(0));
  }

  @Test
  public void updateVideo() {
    // Given created tag
    Tag tag = createTestTag_MinimalData("TestVideoUpdate");
    tag.addVideo(createTestVideo_MinimalData());

    ListProperties defaultList = db.getDefaultList();
    db.insertDefaultList(tag);

    List<Tag> resultTagList = db.getTagsForList(defaultList.getDbId());
    Tag resultTag = getTagWithId(resultTagList, tagId);
    assertNotNull(resultTag);

    // When video is updated
    List<VideoComponents> videos = resultTag.getVideos();
    videos.get(0).setVideoTitle("newTitle");

    db.updateVideos(videos);

    resultTagList = db.getTagsForList(defaultList.getDbId());
    resultTag = getTagWithId(resultTagList, tagId);

    // Then resulting data is the same
    assertNotNull(resultTag);
    checkTestTag_MinimalData(resultTag, "TestVideoUpdate");
    assertEquals(1, resultTag.getVideos().size());
    assertEquals("newTitle", resultTag.getVideos().get(0).getVideoTitle());
  }

  @Test
  public void addList_Default() {
    // Given created list
    ListProperties properties = createList("name", true);

    // When list inserted and retrieved
    db.updateListProperties(properties);
    List<ListProperties> result = db.getListProperties();
    ListProperties resultList = getListWithName(result, "name");
    ListProperties defaultList = db.getDefaultList();
    boolean entriesDefaultList = db.hasEntriesInDefaultList();

    // Then resulting data is the same
    assertNotNull(resultList);
    checkTestList(resultList, "name", true);
    assertEquals("name", defaultList.getName());
    assertFalse(entriesDefaultList);
  }

  @Test
  public void addList_NonDefault() {
    // Given created list
    ListProperties properties = createList("name2", false);

    // When list inserted and retrieved
    db.updateListProperties(properties);
    List<ListProperties> result = db.getListProperties();
    ListProperties resultList = getListWithName(result, "name2");
    ListProperties defaultList = db.getDefaultList();

    // Then resulting data is the same
    assertNotNull(resultList);
    checkTestList(resultList, "name2", false);
    assertNotEquals("name2", defaultList.getName());
  }

  @Test
  public void updateList_NonDefault() {
    // Given created list
    ListProperties properties = createList("name3", false);
    db.updateListProperties(properties);

    List<ListProperties> result = db.getListProperties();
    properties = getListWithName(result, "name3");
    assertNotNull(properties);
    properties.setName("name4");

    // When list inserted and retrieved
    db.updateListProperties(properties);
    result = db.getListProperties();
    ListProperties resultList = getListWithName(result, "name4");

    // Then resulting data is the same
    assertNotNull(resultList);
    checkTestList(resultList, "name4", false);
  }

  @Test
  public void addTagToLists() {
    // Given created tag
    Tag tag = createTestTag_MinimalData("Test");
    ListProperties defaultList = db.getDefaultList();
    db.insertDefaultList(tag);

    List<Tag> resultTagList = db.getTagsForList(defaultList.getDbId());
    tag = getTagWithId(resultTagList, tagId);
    assertNotNull(tag);

    // Given created list
    ListProperties properties = createList("tagList", false);
    db.updateListProperties(properties);

    List<ListProperties> lists = db.getListProperties();
    properties = getListWithName(lists, "tagList");
    assertNotNull(properties);

    List<Long> listIds = new ArrayList<>();
    listIds.add(defaultList.getDbId());
    listIds.add(properties.getDbId());

    // When tag is inserted into lists
    db.updateTagList(tag, listIds);
    List<Long> resultIds = db.getListsForTag(tag);
    Long isInDefaultId = db.isInDefaultList(tag);

    // Then the tag is is both lists
    assertEquals(2, resultIds.size());
    assertTrue(resultIds.contains(defaultList.getDbId()));
    assertTrue(resultIds.contains(properties.getDbId()));
    assertNotNull(isInDefaultId);
  }

  @Test
  public void addTagToLists_NoListSelected() {
    // Given created tag
    Tag tag = createTestTag_MinimalData("Test");
    ListProperties defaultList = db.getDefaultList();
    db.insertDefaultList(tag);

    List<Tag> resultTagList = db.getTagsForList(defaultList.getDbId());
    tag = getTagWithId(resultTagList, tagId);
    assertNotNull(tag);

    List<Long> listIds = new ArrayList<>();

    // When tag is inserted into lists
    db.updateTagList(tag, listIds);
    List<Long> resultIds = db.getListsForTag(tag);

    // Then the tag is is both lists
    assertEquals(0, resultIds.size());
  }

  @Test
  public void deleteList_Default() {
    // Given created list
    ListProperties properties = createList("deleteList", true);
    db.updateListProperties(properties);
    ListProperties defaultList = db.getDefaultList();

    // When list is deleted
    db.deleteList(defaultList);
    defaultList = db.getDefaultList();
    List<ListProperties> result = db.getListProperties();
    ListProperties deletedList = getListWithName(result, "deleteList");

    // Then deletedList is deleted and default list favorites
    assertNotNull(defaultList);
    assertNull(deletedList);
    assertEquals("Favorites", defaultList.getName());
  }

  @Test
  public void updateRating() {
    // Given created tag
    Tag tag = createTestTag_MinimalData("Test");
    db.insertDefaultList(tag);

    // When tag rated
    db.insertUserRating(tagId, 2.2);
    double rating = db.getUserRating(tagId);

    // Then resulting rating is set
    assertEquals(2.2, rating, 0.0001);
  }

  @Test
  public void getOutdatedTags_None() throws InterruptedException {
    // Given test tag inserted
    Tag tag = createTestTag_MinimalData("Test");
    db.insertDefaultList(tag);
    Calendar lastModifiedDate = Calendar.getInstance();
    lastModifiedDate.add(Calendar.DAY_OF_MONTH, -14);
    db.updateTag(tag, lastModifiedDate.getTime());

    // When outdated tag list retrieved
    List<Integer> ids = db.getOutdatedTagIdList(15);

    // Then list is empty
    assertEquals(0, ids.size());
  }

  @Test
  public void getOutdatedTags_All() {
    // Given test tag inserted
    Tag tag = createTestTag_MinimalData("Test");
    db.insertDefaultList(tag);
    Calendar lastModifiedDate = Calendar.getInstance();
    lastModifiedDate.add(Calendar.DAY_OF_MONTH, -14);
    db.updateTag(tag, lastModifiedDate.getTime());

    // When outdated tag list retrieved
    List<Integer> ids = db.getOutdatedTagIdList(13);

    // Then list is empty
    assertEquals(1, ids.size());
  }

  @Test
  public void deleteTagFromDefaultList() {
    // Given test tag inserted
    Tag tag = createTestTag_MinimalData("Test");
    db.insertDefaultList(tag);

    Tag tag2 = createTestTag_MinimalData("Test2");
    db.insertDefaultList(tag2);

    ListProperties defaultList = db.getDefaultList();
    int initialTagNumber = db.getTagsForList(defaultList.getDbId()).size();

    long dbId = db.getDbIdFromTagId(tag.getId());
    tag.setDbId(dbId);

    // When tag is deleted
    db.deleteFromDefaultList(tag);

    // Then tag is not in db
    int afterTagNumber = db.getTagsForList(defaultList.getDbId()).size();
    assertEquals(initialTagNumber - 1, afterTagNumber);
  }

  @Test
  public void getFilteredTags() {
    // Given test tag inserted
    Tag tag = createTestTag_NormalData("Test");
    tag.setKey("D");
    tag.setId(22);
    db.insertDefaultList(tag);

    Tag tag2 = createTestTag_NormalData("Test2");
    tag2.setKey("C");
    tag2.setId(23);
    db.insertDefaultList(tag2);

    ListProperties defaultList = db.getDefaultList();

    FilterBuilder filterBuilder = new FilterBuilder(context);
    filterBuilder.setKey(Key.C);

    SortBuilder sortBuilder = new SortBuilder(context);

    // When
    List<Tag> filteredList =
        db.getTagsForList(filterBuilder.build(), sortBuilder.build(), defaultList.getDbId());

    // Then
    assertEquals(1, filteredList.size());
    assertEquals("Test2", filteredList.get(0).getTitle());
  }

  @Test
  public void getSortedTags() {
    // Given test tag inserted
    Tag tag = createTestTag_NormalData("Test1");
    tag.setDownloadCount(50);
    tag.setCollection("easytags");
    tag.setId(20);
    db.insertDefaultList(tag);

    Tag tag2 = createTestTag_NormalData("Test2");
    tag2.setDownloadCount(100);
    tag2.setCollection("easytags");
    tag2.setId(21);
    db.insertDefaultList(tag2);

    ListProperties defaultList = db.getDefaultList();

    FilterBuilder filterBuilder = new FilterBuilder(context);
    filterBuilder.setCollection(Collection.EASY);

    SortBuilder sortBuilder = new SortBuilder(context);
    sortBuilder.setSortBy(SortBy.DOWNLOAD);

    // When
    List<Tag> filteredList =
        db.getTagsForList(filterBuilder.build(), sortBuilder.build(), defaultList.getDbId());

    // Then
    assertEquals(2, filteredList.size());
    assertEquals("Test2", filteredList.get(0).getTitle());
    assertEquals("Test1", filteredList.get(1).getTitle());
  }

  private Tag getTagWithId(List<Tag> tagList, int id) {
    for (Tag tag : tagList) {
      if (tag.getId() == id) {
        return tag;
      }
    }

    return null;
  }

  private Tag createTestTag_MinimalData(String title) {
    Tag tag = new Tag();
    tag.setId(tagId);
    tag.setTitle(title);
    tag.setPostedDate(new Date());
    return tag;
  }

  private void checkTestTag_MinimalData(Tag tag, String title) {
    assertEquals(tagId, tag.getId());
    assertEquals(title, tag.getTitle());
    assertNotNull(tag.getPostedDate());
  }

  private Tag createTestTag_NormalData(String title) {
    Tag tag = new Tag();
    tag.setId(tagId);
    tag.setTitle(title);
    tag.setPostedDate(new Date());
    tag.setArranger("Arranger");
    tag.setClassicTagNumber(140);
    tag.setCollection("Classic");
    tag.setDownloadCount(5000);
    tag.setKey("Cmajor");
    tag.setLyrics("Lyrics");
    tag.setNumberOfParts(4);
    tag.setRating("4.3");
    tag.setSheetMusicLink("http://barbershoptags.org");
    tag.setSheetMusicType("type");
    tag.setType("BB");
    tag.setVersion("1");
    return tag;
  }

  private void checkTestTag_NormalData(Tag tag, String title) {
    assertEquals(tagId, tag.getId());
    assertEquals(title, tag.getTitle());
    assertNotNull(tag.getPostedDate());
    assertEquals("Arranger", tag.getArranger());
    assertEquals(140, tag.getClassicTagNumber());
    assertEquals("Classic", tag.getCollection());
    assertEquals(5000, tag.getDownloadCount());
    assertEquals("Cmajor", tag.getKey());
    assertEquals("Lyrics", tag.getLyrics());
    assertEquals(4, tag.getNumberOfParts());
    assertEquals(4.3, tag.getRating(), 0.0001);
    assertEquals("http://barbershoptags.org", tag.getSheetMusicLink());
    assertEquals("type", tag.getSheetMusicType());
    assertEquals("BB", tag.getType());
    assertEquals("1", tag.getVersion());
  }

  private VideoComponents createTestVideo_MinimalData() {
    VideoComponents video = new VideoComponents();
    video.setId(1);
    return video;
  }

  private void checkTestVideo_MinimalData(VideoComponents video) {
    assertEquals(1, video.getId());
  }

  private VideoComponents createTestVideo_NormalData() {
    VideoComponents video = new VideoComponents();
    video.setId(1);
    video.setCommentCount(BigInteger.valueOf(3));
    video.setDescription("desc");
    video.setDislikeCount(BigInteger.valueOf(2));
    video.setFavoriteCount(BigInteger.valueOf(4));
    video.setLikeCount(BigInteger.valueOf(5));
    video.setMultitrack(true);
    video.setPostedDate("date");
    video.setSungBy("me");
    video.setSungKey(Pitch.A);
    video.setSungWebsite("web");
    video.setVideoCode("code");
    video.setVideoTitle("title");
    video.setViewCount(BigInteger.valueOf(6));
    return video;
  }

  private void checkTestVideo_NormalData(VideoComponents video) {
    assertEquals(1, video.getId());
    assertEquals(3, video.getCommentCount().intValue());
    assertEquals("desc", video.getDescription());
    assertEquals(2, video.getDislikeCount().intValue());
    assertEquals(4, video.getFavoriteCount().intValue());
    assertEquals(5, video.getLikeCount().intValue());
    assertTrue(video.isMultitrack());
    assertEquals("date", video.getPostedDate());
    assertEquals("me", video.getSungBy());
    assertEquals(Pitch.A, video.getSungKey());
    assertEquals("web", video.getSungWebsite());
    assertEquals("code", video.getVideoCode());
    assertEquals("title", video.getVideoTitle());
    assertEquals(6, video.getViewCount().intValue());
  }

  private void addTestTrack(Tag tag) {
    tag.addTrack("part", "link", "type", false);
  }

  private void checkTestTrack(TrackComponents track) {
    assertEquals("part", track.getPart());
    assertFalse(track.isDownloaded());
    assertEquals("link", track.getLink());
    assertEquals("type", track.getType());
  }

  private ListProperties createList(String name, boolean defaultList) {
    ListProperties properties = new ListProperties();
    properties.setColor(Color.BLACK);
    properties.setDefaultList(defaultList);
    properties.setDownloadSheet(false);
    properties.setDownloadTrack(false);
    properties.setIcon(ListIcon.DEFAULT);
    properties.setName(name);
    properties.setUserCreated(true);
    return properties;
  }

  private void checkTestList(ListProperties list, String name, boolean isDefault) {
    assertEquals(name, list.getName());
    assertEquals(Color.BLACK, list.getColor());
    assertEquals(isDefault, list.isDefaultList());
    assertFalse(list.isDownloadSheet());
    assertFalse(list.isDownloadTrack());
    assertEquals(ListIcon.DEFAULT, list.getIcon());
    assertTrue(list.isUserCreated());
    assertNotNull(list.getDbId());
    assertEquals(0, list.getListSize());
  }

  private ListProperties getListWithName(List<ListProperties> listProperties, String name) {
    for (ListProperties list : listProperties) {
      if (list.getName().equals(name)) {
        return list;
      }
    }

    return null;
  }
}
