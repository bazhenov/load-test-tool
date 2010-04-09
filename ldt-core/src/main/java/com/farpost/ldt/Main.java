package com.farpost.ldt;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.lang.reflect.*;

import static java.lang.Integer.parseInt;
import static java.lang.System.exit;

public class Main {

  private static final Logger log = Logger.getLogger(Main.class);

  public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException {

    Options options = new Options();
    options.addOption("z", "clazz", true, "full qualified test class name");
    options.addOption("c", "concurrency-level", true, "concurrency level");
    options.addOption("r", "result-printer", true, "result printer type (plain, log)");
    options.addOption("n", "count", true, "sample count");
    String fqnClass = null;
    try {
      CommandLineParser parser = new PosixParser();
      CommandLine arg = parser.parse(options, args);

      fqnClass = readString(arg, 'z');
      int concurrencyLevel = readInt(arg, 'c', 1);
      int sampleCount = readInt(arg, 'n', 1);

      ResultFormatter formatter = createFormatter(arg.getOptionValue('r'));

      Task task = createTask(fqnClass);

      TestRunner runner = new TestRunner();
      runner.setConcurrencyLevel(concurrencyLevel);
      runner.setThreadSamplesCount(sampleCount);
      log.debug("Running tests for type: " + fqnClass);
      TestResult result = runner.run(task);


      formatter.format(result);

    } catch (ClassNotFoundException e) {
      error("Class " + fqnClass + " not found");
    } catch (NoSuchMethodException e) {
      error("Class " + fqnClass + " doesn't have no argument constructor");
    } catch (ParseException e) {
      usage(options);
      exit(1);
    } catch (Exception e) {
      error(e.getMessage());
    }
  }

  private static ResultFormatter createFormatter(String type) {
    if ("plain".equalsIgnoreCase(type) || type == null) {
      return new PlainResultFormatter(System.out);
    } else {
      throw new RuntimeException("Invalid formatter type: " + type);
    }
  }

  private static Task createTask(String fqnClass) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    Class<?> type = Class.forName(fqnClass);

    if (!Task.class.isAssignableFrom(type)) {
      error(type.getName() + " should implement interface " + Task.class.getName());
    }
    if (isAbstract(type)) {
      error(type.getName() + " should not be not abstract class not interface");
    }

    Constructor<Task> constructor = ((Class<Task>) type).getConstructor();
    return constructor.newInstance();
  }

  private static boolean isAbstract(Class<?> type) {
    return (type.getModifiers() & Modifier.ABSTRACT) != 0;
  }

  private static int readInt(CommandLine arg, char name, int defaultValue) throws ParseException {
    int value = arg.getOptionValue(name) != null
      ? parseInt(arg.getOptionValue(name))
      : defaultValue;
    if (value <= 0) {
      throw new ParseException("Invalid value");
    }
    return value;
  }

  private static String readString(CommandLine arg, char name) throws ParseException {
    String value = arg.getOptionValue(name);
    if (value == null) {
      throw new ParseException("Class name should be given");
    }
    return value;
  }

  private static void usage(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("ltd", options);
  }

  private static void error(String message) {
    System.out.println("fatal: " + message);
    exit(1);
  }
}
