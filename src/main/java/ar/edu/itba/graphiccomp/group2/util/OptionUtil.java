package ar.edu.itba.graphiccomp.group2.util;

import org.apache.commons.cli.Option;

public class OptionUtil {

	public static final Option required(Option option) {
		option.setRequired(true);
		return option;
	}
}
