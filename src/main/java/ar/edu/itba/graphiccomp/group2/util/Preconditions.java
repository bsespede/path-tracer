package ar.edu.itba.graphiccomp.group2.util;

public class Preconditions {

	public static final void checkIsTrue(boolean b) {
		if (!b) {
			throw new IllegalStateException("This expression must be true");
		}
	}

	public static final void checkIsTrue(boolean b, String message) {
		if (!b) {
			throw new IllegalStateException();
		}
	}
}
