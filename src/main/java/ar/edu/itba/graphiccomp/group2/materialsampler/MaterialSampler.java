package ar.edu.itba.graphiccomp.group2.materialsampler;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.material.Material;
import ar.edu.itba.graphiccomp.group2.math.Line;

public interface MaterialSampler {

	void sample(Material material, Line r, Vector3d p0, Vector3d normal, Line result);

}
