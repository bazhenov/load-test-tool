package com.farpost.ldt;

import java.util.HashMap;
import java.util.Map;

public class MapParser {
  public static Map<String, String> parse(String string) {
    Map<String, String> result = new HashMap<String, String>();
    for ( String part : string.split(",") ) {
      String[] kv = part.split("=", 2);
      if ( kv.length != 2 ) {
        throw new IllegalArgumentException("Parameter string should have format: key=value,key2=value2...");
      }
      result.put(kv[0].trim(), kv[1].trim());
    }
    return result;
  }
}
