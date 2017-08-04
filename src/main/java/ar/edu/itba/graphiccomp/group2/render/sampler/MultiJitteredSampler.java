package ar.edu.itba.graphiccomp.group2.render.sampler;

import javax.vecmath.Vector2d;

public class MultiJitteredSampler extends Sampler {

	public MultiJitteredSampler(final int numSamples) {
		super(numSamples);
	}

	// Based on the "Ray tracing from the Ground up" implementation by Kevin Suffern
	public void generateSquareSamples() {
		final int n = (int) Math.sqrt(numSamples);
		final double subcellWidth = 1.0f / numSamples;

		for (int j = 0; j < numSamples * numSets; j++) {
			squareSamples.add(new Vector2d(0, 0));
		}

		// distribute points in the initial patterns
		for (int p = 0; p < numSets; p++) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					Vector2d point = new Vector2d(0, 0);
					point.x = (i * n + j) * subcellWidth + (float) Math.random() * subcellWidth;
					point.y = (j * n + i) * subcellWidth + (float) Math.random() * subcellWidth;
					squareSamples.add(i * n + j + p * numSamples, point);
				}
			}
		}

		for (int p = 0; p < numSets; p++) {
			for (int i = 0; i < n; i++)	{
				for (int j = 0; j < n; j++) {
					// shuffle x coordinates
					int k = (int) (Math.random() * (n - j - 1) + j);
					double t = squareSamples.get(i * n + j + p * numSamples).x;
					squareSamples.get(i * n + j + p * numSamples).x = squareSamples.get(i * n + k + p * numSamples).x;
					squareSamples.get(i * n + k + p * numSamples).x = t;
					
					// shuffle y coordinates
					k = (int) (Math.random() * (n - j - 1) + j);
					t = squareSamples.get(j * n + i + p * numSamples).y;
					squareSamples.get(j * n + i + p * numSamples).y = squareSamples.get(k * n + i + p * numSamples).y;
					squareSamples.get(k * n + i + p * numSamples).y = t;
				}
			}
		}
	}

}
