package com.farpost.maven.plugins;

import com.farpost.ldt.formatter.AbstractPlainResultFormatter;
import com.farpost.ldt.formatter.ResultFormatter;
import org.apache.maven.plugin.logging.Log;

/**
 * {@link ResultFormatter} implementation similar to {@link com.farpost.ldt.formatter.PlainResultFormatter}
 * but {@link Log} class using for output.
 */
public class MavenLogFormatter extends AbstractPlainResultFormatter {

	private final Log out;

	public MavenLogFormatter(Log out) {
		this.out = out;
	}

	@Override
	protected void writeLine(String message) {
		out.info(message);
	}
}