package ar.edu.itba.graphiccomp.group2.app;

import java.util.concurrent.TimeUnit;

public class Threads {

	public static final void sleep(long value, TimeUnit unit) {
		try {
			Thread.sleep(unit.toMillis(value));
		} catch (InterruptedException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final Thread start(Thread t) {
		t.start();
		return t;
	}

	public static final void join(Thread t) {
		try {
			t.join();
		} catch (InterruptedException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
