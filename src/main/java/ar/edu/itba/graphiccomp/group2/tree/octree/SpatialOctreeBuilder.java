package ar.edu.itba.graphiccomp.group2.tree.octree;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.scene.Spatial;
import ar.edu.itba.graphiccomp.group2.tree.base.TreeStructure;

public class SpatialOctreeBuilder implements Supplier<TreeStructure<Spatial>> {

	private List<Spatial> _spatials = new LinkedList<>();
	private Box _bounds;

	public SpatialOctreeBuilder(Box bounds) {
		_bounds = bounds;
	}

	public SpatialOctreeBuilder add(Spatial spatial) {
		_spatials.add(spatial);
		return this;
	}

	public SpatialOctreeBuilder add(Spatial... spatials) {
		for (Spatial s : spatials) {
			add(s);
		}
		return this;
	}

	public SpatialOctreeBuilder add(Iterable<Spatial> spatials) {
		for (Spatial s : spatials) {
			add(s);
		}
		return this;
	}

	@Override
	public TreeStructure<Spatial> get() {
		return SpatialOctreeStrategy.build(_bounds).checkRepeated().addAll(_spatials);
	}

}
