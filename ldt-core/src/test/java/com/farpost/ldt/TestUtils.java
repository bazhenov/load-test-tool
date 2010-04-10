package com.farpost.ldt;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static java.lang.Math.abs;

class TestUtils {

  public static <T extends Number> Matcher<T> near(T value, float delta) {
    return new NearMatcher(value, delta);
  }

  private static class NearMatcher<T> extends BaseMatcher<Number> {
    private final Number expected;
    private final float delta;

    public NearMatcher(Number expected, float delta) {
      this.expected = expected;
      this.delta = delta;
    }

    public boolean matches(Object o) {
      if (o instanceof Number) {
        Number actual = (Number) o;
        return abs(actual.doubleValue() - expected.doubleValue()) <= delta;
      }
      throw new RuntimeException("Number should be given");
    }

    public void describeTo(Description description) {
      description.appendText("Value should be near " + expected + "+/-" + delta);
    }
  }
}
