package ar.edu.itba.graphiccomp.group2.tree.octree;

import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Spatial;

public class SpatialOctreeStrategy implements NodeStrategy<Spatial> {

	public static Octree<Spatial> build(Box bounds) {
		return new Octree<Spatial>(bounds, new SpatialOctreeStrategy());
	}

	@Override
	public boolean test(Spatial object, Line line, CollisionContext collision) {
		return object.trace(line, collision);
	}

	@Override
	public boolean test(Spatial object, Box bounds) {
		return object.boundingVolume().intersects(bounds);
	}
}
