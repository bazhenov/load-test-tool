package com.farpost.ldt.formatter;

import java.io.PrintStream;

public class PlainResultFormatter extends AbstractPlainResultFormatter {

	private final PrintStream out;


	public PlainResultFormatter(PrintStream out) {
		this.out = out;
	}

	@Override
	protected void writeLine(String message) {
		out.println(message);
	}
}
