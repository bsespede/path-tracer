package ar.edu.itba.graphiccomp.group2.render.sampler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public abstract class Sampler {

	private static final int DEFAULT_SAMPLES_SETS = 83;
	protected final List<Vector2d> squareSamples;
	protected final List<Vector2d> diskSamples;
	protected final List<Vector3d> hemisphereSamples;
	protected final List<Vector3d> sphereSamples;
	protected final List<Integer> shuffledIndices;
	protected final int numSamples;
	protected final int numSets;
	protected int count;
	protected int jump;

	public Sampler(final int numSamples) {
		this.numSamples = numSamples;
		this.numSets = DEFAULT_SAMPLES_SETS;
		this.squareSamples = new ArrayList<Vector2d>(numSamples * DEFAULT_SAMPLES_SETS);
		this.diskSamples = new ArrayList<Vector2d>(numSamples * DEFAULT_SAMPLES_SETS);
		this.hemisphereSamples = new ArrayList<Vector3d>(numSamples * DEFAULT_SAMPLES_SETS);
		this.sphereSamples = new ArrayList<Vector3d>(numSamples * DEFAULT_SAMPLES_SETS);
		this.shuffledIndices = new ArrayList<Integer>(numSamples * DEFAULT_SAMPLES_SETS);
		generateSquareSamples();
		mapSquareSamplesToDisk();
		mapSquareSamplesToHemisphere();
		mapSquareSamplesToSphere();
		shuffleIndices();
	}

	protected abstract void generateSquareSamples();

	protected void shuffleIndices() {
		List<Integer> indices = new ArrayList<Integer>(numSamples);

		for (int i = 0; i < numSamples; i++) {
			indices.add(i);
		}

		for (int j = 0; j < numSets; j++) {
			Collections.shuffle(indices);
			for (int k = 0; k < numSamples; k++) {
				shuffledIndices.add(indices.get(k));
			}
		}
	}

	public Vector2d sampleUnitSquare() {
		if (count % numSamples == 0) {
			jump = (int) (Math.random() * numSets) * numSamples;
		}

		return squareSamples.get(jump + shuffledIndices.get(jump + count++ % numSamples));
	}

	public Vector2d sampleUnitDisk() {
		if (count % numSamples == 0) {
			jump = (int) (Math.random() * numSets) * numSamples;
		}

		return diskSamples.get(jump + shuffledIndices.get(jump + count++ % numSamples));
	}

	public Vector3d sampleUnitHemisphere() {
		if (count % numSamples == 0) {
			jump = (int) (Math.random() * numSets) * numSamples;
		}

		return hemisphereSamples.get(jump + shuffledIndices.get(jump + count++ % numSamples));
	}

	public Vector3d sampleUnitSphere() {
		if (count % numSamples == 0) {
			jump = (int) (Math.random() * numSets) * numSamples;
		}

		return sphereSamples.get(jump + shuffledIndices.get(jump + count++ % numSamples));
	}

	// Based on "Realistic Ray tracing" algorithm by Shirley/Morley
	private void mapSquareSamplesToDisk() {
		double r, phi;
		Vector2d sp = new Vector2d(0, 0);

		for (int i = 0; i < squareSamples.size(); i ++) {

			sp.x = 2 * squareSamples.get(i).x - 1;
			sp.y = 2 * squareSamples.get(i).y - 1;

			if (sp.x > -sp.y) {
				if (sp.x > sp.y) {
					r = sp.x;
					phi = sp.y / sp.x;
				} else {
					r = sp.y;
					phi = 2 - sp.x / sp.y;
				}
			} else {
				if (sp.x < sp.y) {
					r = -sp.x;
					phi = 4 + sp.y / sp.x;
				} else {
					r = -sp.y;
					if (sp.y != 0) {
						phi = 6 - sp.x / sp.y;
					} else {
						phi = 0;
					}
				}
			}

			phi *= Math.PI / 4.0f;
			diskSamples.add(i, new Vector2d(r * Math.cos(phi), r *  Math.sin(phi)));
		}
	}

	// Based on "Realistic Ray tracing" algorithm by Shirley/Morley
	private void mapSquareSamplesToHemisphere() {
		for (int i = 0; i < squareSamples.size(); i ++) {
			double cosPhi = Math.cos(2 * Math.PI * squareSamples.get(i).x);
			double sinPhi = Math.sin(2 * Math.PI * squareSamples.get(i).x);

			double cosTheta = Math.pow(1 - squareSamples.get(i).y, 1 / ( Math.E + 1));
			double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);

			hemisphereSamples.add(new Vector3d(sinTheta * cosPhi, sinTheta * sinPhi, cosTheta));			
		}
	}

	// Formulas from "Graphic gems III" by Shirley
	private void mapSquareSamplesToSphere() {
		for (int i = 0; i < squareSamples.size(); i ++) {

			double theta = Math.acos(1 - (2 * squareSamples.get(i).x));
			double phi = 2 * Math.PI * squareSamples.get(i).y;

			double cosPhi = Math.cos(phi);
			double sinPhi = Math.sin(phi);

			double cosTheta = Math.cos(theta);
			double sinTheta = Math.sin(theta);

			sphereSamples.add(new Vector3d(sinTheta * cosPhi, sinTheta * sinPhi, cosTheta));
		}
	}

	public int numSamples() {
		return numSamples;
	}

}
