package com.spelder.tagyourit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.os.Parcel;
import com.spelder.tagyourit.pitch.Pitch;
import java.util.Date;
import org.junit.Test;

public class TestTagModel {
  @Test
  public void testTagParcelable() {
    Tag tag = new Tag("1234");
    tag.setTitle("test");
    tag.setVersion("ver");
    tag.setArranger("arr");
    tag.setRating("2.4");
    tag.setKey("Cb");
    tag.setNumberOfParts(4);
    tag.setLyrics("Lyrics");
    tag.setSheetMusicLink("http://barbershoptags.org");
    tag.setSheetMusicType("mp3");
    tag.setPostedDate(new Date());
    tag.addTrack("Bass", "http://barbershoptags.org", "pdf", false);
    tag.addTrack("Bari", "http://barbershoptags.org", "pdf", false);
    tag.addTrack("Lead", "http://barbershoptags.org", "pdf", false);
    tag.addTrack("Tenor", "http://barbershoptags.org", "pdf", false);

    VideoComponents video1 = new VideoComponents();
    video1.setId(83);
    video1.setDescription("");
    video1.setSungKey(Pitch.determinePitch("Major:Ab"));
    video1.setMultitrack(true);
    video1.setVideoCode("uxzJHrj0-eI");
    video1.setSungBy("Ronnie Spann (aka BBSTENOR419)");
    video1.setSungWebsite("");
    video1.setPostedDate("Mon, 30 Mar 2009");
    tag.addVideo(video1);

    VideoComponents video2 = new VideoComponents();
    video2.setId(84);
    video2.setDescription("Desc");
    video2.setSungKey(Pitch.determinePitch("Bb"));
    video2.setMultitrack(false);
    video2.setVideoCode("uxzJHrj0-e2");
    video2.setSungBy("Ronnie Span (aka BBSTENOR419)");
    video2.setSungWebsite("none");
    video2.setPostedDate("Mon, 31 Mar 2009");
    tag.addVideo(video2);

    Parcel parcel = Parcel.obtain();
    tag.writeToParcel(parcel, tag.describeContents());
    parcel.setDataPosition(0);

    Tag createdFromParcel = Tag.CREATOR.createFromParcel(parcel);
    VideoComponents createdFromParcelV1 = createdFromParcel.getVideos().get(0);
    VideoComponents createdFromParcelV2 = createdFromParcel.getVideos().get(1);

    assertEquals(tag.getId(), createdFromParcel.getId());
    assertEquals(tag.getTitle(), createdFromParcel.getTitle());
    assertEquals(tag.getArranger(), createdFromParcel.getArranger());
    assertEquals(tag.getArrangerDisplay(), createdFromParcel.getArrangerDisplay());
    assertEquals(tag.getKey(), createdFromParcel.getKey());
    assertEquals(tag.getLyrics(), createdFromParcel.getLyrics());
    assertEquals(tag.getSheetMusicLink(), createdFromParcel.getSheetMusicLink());
    assertEquals(tag.getSheetMusicFileName(), createdFromParcel.getSheetMusicFileName());
    assertEquals(tag.getVersion(), createdFromParcel.getVersion());
    assertEquals(tag.getNumberOfParts(), createdFromParcel.getNumberOfParts());
    assertEquals(tag.getRating(), createdFromParcel.getRating(), 0.001);
    assertEquals(
        tag.getTrack("Bass").getTagTitle(), createdFromParcel.getTrack("Bass").getTagTitle());
    assertEquals(tag.getTrack("Bass").getLink(), createdFromParcel.getTrack("Bass").getLink());
    assertEquals(tag.getTrack("Bass").getPart(), createdFromParcel.getTrack("Bass").getPart());
    assertEquals(
        tag.getTrack("Bass").getTrackFileName(),
        createdFromParcel.getTrack("Bass").getTrackFileName());
    assertEquals(tag.getTrack("Bass").getType(), createdFromParcel.getTrack("Bass").getType());
    assertEquals(
        tag.getTrack("Bari").getTagTitle(), createdFromParcel.getTrack("Bari").getTagTitle());
    assertEquals(tag.getTrack("Bari").getLink(), createdFromParcel.getTrack("Bari").getLink());
    assertEquals(tag.getTrack("Bari").getPart(), createdFromParcel.getTrack("Bari").getPart());
    assertEquals(
        tag.getTrack("Bari").getTrackFileName(),
        createdFromParcel.getTrack("Bari").getTrackFileName());
    assertEquals(tag.getTrack("Bari").getType(), createdFromParcel.getTrack("Bari").getType());
    assertEquals(
        tag.getTrack("Lead").getTagTitle(), createdFromParcel.getTrack("Lead").getTagTitle());
    assertEquals(tag.getTrack("Lead").getLink(), createdFromParcel.getTrack("Lead").getLink());
    assertEquals(tag.getTrack("Lead").getPart(), createdFromParcel.getTrack("Lead").getPart());
    assertEquals(
        tag.getTrack("Lead").getTrackFileName(),
        createdFromParcel.getTrack("Lead").getTrackFileName());
    assertEquals(tag.getTrack("Lead").getType(), createdFromParcel.getTrack("Lead").getType());
    assertEquals(
        tag.getTrack("Tenor").getTagTitle(), createdFromParcel.getTrack("Tenor").getTagTitle());
    assertEquals(tag.getTrack("Tenor").getLink(), createdFromParcel.getTrack("Tenor").getLink());
    assertEquals(tag.getTrack("Tenor").getPart(), createdFromParcel.getTrack("Tenor").getPart());
    assertEquals(
        tag.getTrack("Tenor").getTrackFileName(),
        createdFromParcel.getTrack("Tenor").getTrackFileName());
    assertEquals(tag.getTrack("Tenor").getType(), createdFromParcel.getTrack("Tenor").getType());
    assertTrue(createdFromParcel.toString().contains("" + tag.getId()));
    assertEquals(video1.getId(), createdFromParcelV1.getId());
    assertEquals(video1.getDescription(), createdFromParcelV1.getDescription());
    assertEquals(video1.getSungBy(), createdFromParcelV1.getSungBy());
    assertEquals(video1.getSungKey(), createdFromParcelV1.getSungKey());
    assertEquals(video1.getSungWebsite(), createdFromParcelV1.getSungWebsite());
    assertEquals(video1.isMultitrack(), createdFromParcelV1.isMultitrack());
    assertEquals(video1.getVideoCode(), createdFromParcelV1.getVideoCode());
    assertEquals(video1.getPostedDate(), createdFromParcelV1.getPostedDate());
    assertEquals(video1.getTagTitle(), createdFromParcelV1.getTagTitle());
    assertEquals(video2.getId(), createdFromParcelV2.getId());
    assertEquals(video2.getDescription(), createdFromParcelV2.getDescription());
    assertEquals(video2.getSungBy(), createdFromParcelV2.getSungBy());
    assertEquals(video2.getSungKey(), createdFromParcelV2.getSungKey());
    assertEquals(video2.getSungWebsite(), createdFromParcelV2.getSungWebsite());
    assertEquals(video2.isMultitrack(), createdFromParcelV2.isMultitrack());
    assertEquals(video2.getVideoCode(), createdFromParcelV2.getVideoCode());
    assertEquals(video2.getPostedDate(), createdFromParcelV2.getPostedDate());
    assertEquals(video2.getTagTitle(), createdFromParcelV2.getTagTitle());
  }
}
