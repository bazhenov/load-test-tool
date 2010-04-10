package com.farpost.ldt;

import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MapParserTest {

  @Test
  public void parserCanParseStringInotMaps() {
    Map<String, String> map = MapParser.parse("url=http://host.com/path/to/file.jpg,port=80");
    assertThat(map.size(), equalTo(2));
    assertThat(map.get("url"), equalTo("http://host.com/path/to/file.jpg"));
    assertThat(map.get("port"), equalTo("80"));
  }
}
