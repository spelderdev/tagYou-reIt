package com.spelder.tagyourit.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import org.junit.Test;

public class TestTagModel {
  @Test
  public void testTagValid_Valid() {
    Tag tag = new Tag();
    tag.setId(1);
    tag.setPostedDate(new Date());
    tag.setTitle("Test");

    assertTrue(tag.isTagValid());
  }

  @Test
  public void testTagValid_Invalid() {
    Tag tag = new Tag();
    tag.setId(1);
    tag.setPostedDate(new Date());

    assertFalse(tag.isTagValid());
  }
}
