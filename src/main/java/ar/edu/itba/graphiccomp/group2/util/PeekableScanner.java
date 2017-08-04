package ar.edu.itba.graphiccomp.group2.util;

import java.util.Scanner;

public class PeekableScanner {

	private Scanner scan;
	private String next;

	public PeekableScanner(Scanner scanner) {
		scan = scanner;
		next = scan.hasNextLine() ? scan.nextLine() : null;
	}

	public boolean hasNext() {
		return (next != null);
	}

	public String nextLine() {
		String current = next;
		next = scan.hasNextLine() ? scan.nextLine() : null;
		return current;
	}

	public String peek() {
		return next;
	}

	public void consume() {
		nextLine();
	}
}
