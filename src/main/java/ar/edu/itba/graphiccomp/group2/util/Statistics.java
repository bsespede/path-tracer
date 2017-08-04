package ar.edu.itba.graphiccomp.group2.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Statistics {

	private List<Double> data = new LinkedList<>();

	public void addData(double d) {
		data.add(d);
	}

	public int size() {
		return data.size();
	}

	public double mean() {
		double sum = 0.0;
		for (double a : data)
			sum += a;
		return sum / size();
	}

	double variance() {
		double mean = mean();
		double temp = 0;
		for (double a : data) {
			temp += (mean - a) * (mean - a);
		}
		return temp / size();
	}

	public double stdDev() {
		return Math.sqrt(variance());
	}

	public double median() {
		List<Double> b = new LinkedList<>(data);
		Collections.sort(b);
		if (size() % 2 == 0) {
			return b.get((b.size() / 2) - 1) + b.get(b.size() / 2) / 2.0;
		} else {
			return b.get(b.size() / 2);
		}
	}

	public void removeData(int index) {
		data.remove(index);
	}

	public double sum() {
		double sum = 0;
		for (double a : data) {
			sum += a;
		}
		return sum;
	}
}
