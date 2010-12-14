package com.farpost.ldt;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Utils {

	public static double calculateStdDev(long[][] numbers) {
		long sum = 0;
		long mean = 0;
		int count = 0;
		for (long[] row : numbers) {
			count += row.length;
			for (long number : row) {
				sum += number;
				mean += pow(number, 2);
			}
		}
		double avarage = sum / count;
		return sqrt(mean / count - pow(avarage, 2));
	}
}
