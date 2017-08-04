package ar.edu.itba.graphiccomp.group2.render.processor;

import java.util.function.Supplier;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.image.ViewPort;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;

public final class SimpleFrameBuilder implements FrameBuilder {

	private Supplier<Integer> _rowsSupplier;
	private int _rowSize;

	public SimpleFrameBuilder(Supplier<Integer> rowsSupplier, int rowSize) {
		_rowsSupplier = rowsSupplier;
		_rowSize = rowSize;
	}

	@Override
	public void build(RayTracer rayTracer) {
		rayTracer.rayResolver().initialize(rayTracer);
		final ViewPort viewPort = rayTracer.camera().viewPort();
		final Line ray = new Line(rayTracer.camera().position(), new Vector3d());
		boolean ended = false;
		CollisionContext collision = new CollisionContext();
		do {
			final int startRow = _rowsSupplier.get();
			final int endRow = Math.min(startRow + _rowSize, viewPort.height());
			ended = viewPort.height() <= startRow;
			if (!ended) {
				System.out.println(startRow + " => " + endRow);
				for (int row = startRow; row < endRow; row++) {
					for (int column = 0; column < viewPort.width(); column++) {
						rayTracer.rayResolver().solve(rayTracer, ray, row, column, collision);
						rayTracer.renderer().draw(collision, viewPort.pixel(column, row), rayTracer.scene());
						collision.restart();
					}
				}
			}
		} while (!ended);
		System.out.println(String.format("Thread %s finished", Thread.currentThread().getName()));
	}

}
