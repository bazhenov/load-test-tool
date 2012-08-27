package com.farpost.ldt;

import com.farpost.ldt.formatter.ElapsedTimeLogResultFormatter;
import com.farpost.ldt.formatter.PlainResultFormatter;
import com.farpost.ldt.formatter.ResultFormatter;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.PrintStream;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.System.exit;

public class Main {

	private static final Logger log = Logger.getLogger(Main.class);
	private static final PrintStream out = System.out;

	public static void main(String[] arg) throws ClassNotFoundException, NoSuchMethodException {
		Options options = prepareOptions();
		try {
			CommandLineParser parser = new PosixParser();
			CommandLine args = parser.parse(options, arg);

			String fqnClass = readString(args, 'z');
			int concurrencyLevel = readPositiveInt(args, 'c', 1);
			int sampleCount = readPositiveInt(args, 'n', 0);
			int warmupThreshold = readNonNegativeInt(args, 'w', 10);
			int timeframe = readNonNegativeInt(args, 't', 0);

			if (timeframe > 0 && sampleCount > 0) {
				error("You specified both the timeframe (-t) and sample count (-c). Only one of them " +
					"should be given at any time");
			}

			ResultFormatter formatter = createFormatter(args.getOptionValue('r'));

			Task task = TaskFactory.createTask(fqnClass);
			if (args.hasOption('p')) {
				injectParameters(task, args.getOptionValue('p'));
			}

			TestRunner runner = new TestRunner();
			runner.setConcurrencyLevel(concurrencyLevel);
			runner.setWarmUpThreshold(warmupThreshold);

			if (timeframe > 0) {
				runner.setTestInterruptionStarategy(new TimeFrameInterruptionStrategy(timeframe));
			} else {
				runner.setTestInterruptionStarategy(new CallCountInterruptionStrategy(sampleCount));
			}

			log.debug("Running tests for type: " + fqnClass);
			TestResult result = runner.run(task);

			formatter.format(result);

		} catch (ClassNotFoundException e) {
			error("Class " + e.getMessage() + " not found");
		} catch (NoSuchMethodException e) {
			error("Method not found: " + e.getMessage());
		} catch (ParseException e) {
			usage(options);
			exit(1);
		} catch (Exception e) {
			error(e);
		}
	}

	private static Options prepareOptions() {
		Options options = new Options();
		options.addOption("z", "clazz", true, "full qualified test class name");
		options.addOption("c", "concurrency-level", true, "concurrency level");
		options.addOption("r", "result-printer", true, "result printer type (plain, log)");
		options.addOption("n", "count", true, "sample count");
		options.addOption("w", "warmup-threshold", true, "warmup test execution count");
		options.addOption("p", "parameters", true, "task parameters");
		options.addOption("t", "timeframe", true, "timeframe testing range (in milliseconds)");
		return options;
	}

	private static void injectParameters(Task task, String parameters) {
		Map<String, String> map = MapParser.parse(parameters);
		task.setParameters(map);
	}

	private static ResultFormatter createFormatter(String type) {
		if ("plain".equalsIgnoreCase(type) || type == null) {
			return new PlainResultFormatter(out);
		} else if ("log".equalsIgnoreCase(type)) {
			return new ElapsedTimeLogResultFormatter(out);
		} else {
			throw new RuntimeException("Invalid formatter type: " + type);
		}
	}

	private static int readPositiveInt(CommandLine arg, char name, int defaultValue) throws ParseException {
		if (arg.hasOption(name)) {
			int value = parseInt(arg.getOptionValue(name));
			if (value < 0) {
				throw new ParseException("Invalid value");
			}
			return value;
		} else {
			return defaultValue;
		}
	}

	private static int readNonNegativeInt(CommandLine arg, char name, int defaultValue) throws ParseException {
		return (arg.hasOption(name))
			? parseInt(arg.getOptionValue(name))
			: defaultValue;
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
		formatter.printHelp("ldt", options);
	}

	private static void error(String message) {
		out.println("fatal: " + message);
		exit(1);
	}

	private static void error(Exception e) {
		out.println("fatal: " + e.getMessage());
		e.printStackTrace(out);
		exit(1);
	}
}
