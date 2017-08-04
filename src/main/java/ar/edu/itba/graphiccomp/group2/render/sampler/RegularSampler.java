package ar.edu.itba.graphiccomp.group2.render.sampler;

import javax.vecmath.Vector2d;

public class RegularSampler extends Sampler {

	public RegularSampler(final int numSamples) {
		super(numSamples);
	}

	public void generateSquareSamples() {
		final int n = (int) Math.sqrt(numSamples);

		for (int i = 0; i < numSets; i ++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					squareSamples.add(new Vector2d((k + 0.5f) / n, (j + 0.5f) / n));
				}
			}
		}
	}

}
