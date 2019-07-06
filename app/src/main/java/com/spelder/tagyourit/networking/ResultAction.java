package com.spelder.tagyourit.networking;

import com.spelder.tagyourit.model.Tag;
import java.util.List;

@FunctionalInterface
public interface ResultAction {
  void run(List<Tag> result);
}
