package com.spelder.tagyourit.networking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.pitch.Pitch;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

public class TestTagXmlParser {
  @Test
  public void testParseTag() throws XmlPullParserException, IOException {
    InputStream stream = this.getClass().getClassLoader().getResourceAsStream("tag.xml");

    TagXmlParser parser = new TagXmlParser();
    ArrayList<Tag> tagList = parser.parse(stream);

    assertEquals(1, tagList.size());
    Tag tag = tagList.get(0);
    assertEquals(31, tag.getId());
    assertEquals("After Today", tag.getTitle());
    assertEquals("Jay Giallombardo", tag.getArranger());
    assertEquals("Major:F", tag.getKey());
    assertNull(tag.getLyrics());
    assertEquals(
        "http://www.barbershoptags.com/dbaction.php?action=DownloadFile&dbase=tags&id=31&fldname=SheetMusic",
        tag.getSheetMusicLink());
    assertEquals("gif", tag.getSheetMusicType());
    assertEquals("", tag.getVersion());
    assertEquals(4, tag.getNumberOfParts());
    assertEquals(3.1490384615385, tag.getRating(), .0001);
    assertEquals(
        "http://www.barbershoptags.com/dbaction.php?action=DownloadFile&dbase=tags&id=31&fldname=Bari",
        tag.getTrack("Bari").getLink());
    assertEquals(
        "http://www.barbershoptags.com/dbaction.php?action=DownloadFile&dbase=tags&id=31&fldname=Bass",
        tag.getTrack("Bass").getLink());
    assertEquals(
        "http://www.barbershoptags.com/dbaction.php?action=DownloadFile&dbase=tags&id=31&fldname=Lead",
        tag.getTrack("Lead").getLink());
    assertEquals(
        "http://www.barbershoptags.com/dbaction.php?action=DownloadFile&dbase=tags&id=31&fldname=Tenor",
        tag.getTrack("Tenor").getLink());
    assertEquals(
        "http://www.barbershoptags.com/dbaction.php?action=DownloadFile&dbase=tags&id=31&fldname=AllParts",
        tag.getTrack("AllParts").getLink());
    assertEquals("mp3", tag.getTrack("AllParts").getType());
    assertEquals("Description", tag.getVideos().get(0).getDescription());
    assertEquals(2, tag.getVideos().size());
    assertEquals(83, tag.getVideos().get(0).getId());
    assertEquals(84, tag.getVideos().get(1).getId());
    assertEquals("uxzJHrj0-eI", tag.getVideos().get(0).getVideoCode());
    assertEquals("Ronnie Spann (aka BBSTENOR419)", tag.getVideos().get(0).getSungBy());
    assertEquals("Mon, 30 Mar 2009", tag.getVideos().get(0).getPostedDate());
    assertEquals("", tag.getVideos().get(0).getSungWebsite());
    assertEquals("After Today", tag.getVideos().get(0).getTagTitle());
    assertEquals(Pitch.A_FLAT, tag.getVideos().get(0).getSungKey());
    assertTrue(tag.getVideos().get(0).isMultitrack());
  }
}
