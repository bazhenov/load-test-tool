package com.farpost.loadTest;

/**
 * Result formatters are used to present results of testing to end user.
 * <p/>
 * Algorithms of formatting and writing data to output streams (file or console)
 * are specified by implementing classes.
 */
public interface ResultFormatter {

  /**
   * Format results of testing and write it to output stream. So this method should have some side effects
   * on environment.
   * <p/>
   * Output stream is not part of this interface. You should read documentation on concrete implemntation
   * for additional information on providing output stream.
   *
   * @param result result of testing
   */
  void format(TestResult result);
}
