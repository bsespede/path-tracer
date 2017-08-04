package ar.edu.itba.graphiccomp.group2.render.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import ar.edu.itba.graphiccomp.group2.app.Threads;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;

public class MultithreadedFrameBuilder implements FrameBuilder, Supplier<Integer> {

	private int _threadsCount;
	private final int rowsDelta = 10;
	private final AtomicInteger row = new AtomicInteger(0);

	public MultithreadedFrameBuilder() {
		_threadsCount = Runtime.getRuntime().availableProcessors();
		System.out.println("[INFO] Detected cores: " + _threadsCount);
		if (_threadsCount < 1) {
			_threadsCount = 4;
			System.out.println("[WARN] Error counting number of cores. Using default " + _threadsCount);
		}
	}

	@Override
	public void build(RayTracer rayTracer) {
		row.set(0);
		List<Thread> threads = new ArrayList<>(_threadsCount);
		for (int i = 0; i < _threadsCount; i++) {
			Thread thread = new Thread(() -> {
				new SimpleFrameBuilder(this, rowsDelta).build(rayTracer);
			}, String.format("raytracer-thread-%d", i));
			threads.add(Threads.start(thread));
		}
		threads.stream().forEach(Threads::join);
	}

	public Integer get() {
		return row.getAndAdd(rowsDelta);
	}
}
