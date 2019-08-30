package com.spelder.tagyourit.networking;

import android.util.Xml;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.model.TrackParts;
import com.spelder.tagyourit.model.VideoComponents;
import com.spelder.tagyourit.pitch.Pitch;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/** Parser used for converting the tag API xml format to the internal tag model. */
class TagXmlParser {
  // We don't use namespaces
  private static final String ns = null;
  private static final SimpleDateFormat postedDateFormatter =
      new SimpleDateFormat("E, dd MMM yyyy", Locale.US);

  ArrayList<Tag> parse(InputStream in) throws XmlPullParserException, IOException {
    try {
      XmlPullParser parser = Xml.newPullParser();
      parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
      parser.setInput(in, null);
      parser.nextTag();
      return readFeed(parser);
    } finally {
      in.close();
    }
  }

  private ArrayList<Tag> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
    ArrayList<Tag> entries = new ArrayList<>();
    // parser.require( XmlPullParser.START_TAG, ns, "tags" );
    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() != XmlPullParser.START_TAG) {
        continue;
      }
      String name = parser.getName();
      // Starts by looking for the entry tag
      if (name.equals("tag")) {
        entries.add(readEntry(parser));
      } else {
        skip(parser);
      }
    }
    return entries;
  }

  // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
  // to their respective "read" methods for processing. Otherwise, skips the tag.
  private Tag readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, ns, "tag");
    Tag tag = new Tag();
    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() != XmlPullParser.START_TAG) {
        continue;
      }
      String name = parser.getName();
      switch (name) {
        case "id":
          tag.setId(readSimple(parser, "id"));
          break;
        case "Title":
          tag.setTitle(readSimple(parser, "Title"));
          break;
        case "Parts":
          try {
            tag.setNumberOfParts(Integer.parseInt(readSimple(parser, "Parts")));
          } catch (NumberFormatException e) {
            tag.setNumberOfParts(4);
          }
          break;
        case "Version":
          tag.setVersion(readSimple(parser, "Version"));
          break;
        case "Arranger":
          tag.setArranger(readSimple(parser, "Arranger"));
          break;
        case "Rating":
          tag.setRating(readSimple(parser, "Rating"));
          break;
        case "Classic":
          try {
            tag.setClassicTagNumber(Integer.parseInt(readSimple(parser, "Classic")));
          } catch (NumberFormatException e) {
            tag.setNumberOfParts(-1);
          }
          break;
        case "Downloaded":
          try {
            tag.setDownloadCount(Integer.parseInt(readSimple(parser, "Downloaded")));
          } catch (NumberFormatException e) {
            tag.setNumberOfParts(0);
          }
          break;
        case "Posted":
          try {
            Date postedDate = postedDateFormatter.parse(readSimple(parser, "Posted"));
            tag.setPostedDate(postedDate);
          } catch (ParseException e) {
            tag.setNumberOfParts(-1);
          }
          break;
        case "SheetMusic":
          handleSheetMusic(parser, tag);
          break;
        case "AllParts":
          handleTracks(parser, tag, TrackParts.ALL.getKey());
          break;
        case "Bass":
          handleTracks(parser, tag, TrackParts.BASS.getKey());
          break;
        case "Bari":
          handleTracks(parser, tag, TrackParts.BARI.getKey());
          break;
        case "Lead":
          handleTracks(parser, tag, TrackParts.LEAD.getKey());
          break;
        case "Tenor":
          handleTracks(parser, tag, TrackParts.TENOR.getKey());
          break;
        case "Other1":
          handleTracks(parser, tag, TrackParts.OTHER1.getKey());
          break;
        case "Other2":
          handleTracks(parser, tag, TrackParts.OTHER2.getKey());
          break;
        case "Other3":
          handleTracks(parser, tag, TrackParts.OTHER3.getKey());
          break;
        case "Other4":
          handleTracks(parser, tag, TrackParts.OTHER4.getKey());
          break;
        case "WritKey":
          tag.setKey(readSimple(parser, "WritKey"));
          break;
        case "Type":
          tag.setType(readSimple(parser, "Type"));
          break;
        case "videos":
          tag.setVideos(readVideos(parser, tag.getId(), tag.getTitle()));
          break;
        default:
          skip(parser);
      }
    }
    return tag;
  }

  // Processes title tags in the feed.
  private String readSimple(XmlPullParser parser, String tag)
      throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, ns, tag);
    String title = readText(parser);
    parser.require(XmlPullParser.END_TAG, ns, tag);
    return title;
  }

  // Processes link tags in the feed.
  private void handleSheetMusic(XmlPullParser parser, Tag tag)
      throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, ns, "SheetMusic");
    String linkType = parser.getAttributeValue(null, "type");
    String link = readText(parser);
    parser.require(XmlPullParser.END_TAG, ns, "SheetMusic");
    tag.setSheetMusicLink(link);
    tag.setSheetMusicType(linkType);
  }

  // Processes link tags in the feed.
  private void handleTracks(XmlPullParser parser, Tag tag, String part)
      throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, ns, part);
    String linkType = parser.getAttributeValue(null, "type");
    String link = readText(parser);
    parser.require(XmlPullParser.END_TAG, ns, part);
    if (link.length() > 0) {
      tag.addTrack(part, link, linkType);
    }
  }

  private ArrayList<VideoComponents> readVideos(XmlPullParser parser, int tagId, String tagTitle)
      throws XmlPullParserException, IOException {
    ArrayList<VideoComponents> entries = new ArrayList<>();
    parser.require(XmlPullParser.START_TAG, ns, "videos");
    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() != XmlPullParser.START_TAG) {
        continue;
      }
      String name = parser.getName();
      // Starts by looking for the entry tag
      if (name.equals("video")) {
        VideoComponents video = readVideo(parser);
        video.setTagTitle(tagTitle);
        video.setTagId(tagId);
        entries.add(video);
      } else {
        skip(parser);
      }
    }
    return entries;
  }

  private VideoComponents readVideo(XmlPullParser parser)
      throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, ns, "video");
    VideoComponents video = new VideoComponents();
    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() != XmlPullParser.START_TAG) {
        continue;
      }
      String name = parser.getName();
      switch (name) {
        case "id":
          video.setId(Integer.parseInt(readSimple(parser, "id")));
          break;
        case "Desc":
          video.setDescription(readSimple(parser, "Desc"));
          break;
        case "SungKey":
          video.setSungKey(Pitch.determinePitch(readSimple(parser, "SungKey")));
          break;
        case "Multitrack":
          video.setMultitrack(readSimple(parser, "Multitrack").equals("Yes"));
          break;
        case "Code":
          video.setVideoCode(readSimple(parser, "Code"));
          break;
        case "SungBy":
          video.setSungBy(readSimple(parser, "SungBy"));
          break;
        case "SungWebsite":
          video.setSungWebsite(readSimple(parser, "SungWebsite"));
          break;
        case "Posted":
          video.setPostedDate(readSimple(parser, "Posted"));
          break;
        default:
          skip(parser);
      }
    }
    return video;
  }

  // For the tags title and summary, extracts their text values.
  private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
    String result = "";
    if (parser.next() == XmlPullParser.TEXT) {
      result = parser.getText();
      parser.nextTag();
    }
    return result;
  }

  private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
    if (parser.getEventType() != XmlPullParser.START_TAG) {
      throw new IllegalStateException();
    }
    int depth = 1;
    while (depth != 0) {
      switch (parser.next()) {
        case XmlPullParser.END_TAG:
          depth--;
          break;
        case XmlPullParser.START_TAG:
          depth++;
          break;
      }
    }
  }
}
