package ar.edu.itba.graphiccomp.group2.app;

import java.util.concurrent.Semaphore;

public class Semaphores {

	public static void acquire(Semaphore semaphore) {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	public static void release(Semaphore semaphore) {
		semaphore.release();
	}
}
