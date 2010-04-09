package com.farpost.loadTest;

public class TestExecutionException extends RuntimeException {

  public TestExecutionException(Exception cause) {
    super(cause);
  }
}
