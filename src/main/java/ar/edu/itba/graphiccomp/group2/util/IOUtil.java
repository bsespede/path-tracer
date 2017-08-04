package ar.edu.itba.graphiccomp.group2.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class IOUtil {

	public static final InputStream resource(String name) {
		return IOUtil.class.getClassLoader().getResourceAsStream(name);
	}

	public static final InputStream file(String name) {
		try {
			return new FileInputStream(name);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
}
