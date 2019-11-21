package com.github.bjansen.sepaconversiontool.converters;

public class Range {

	private final int minOffset;
	private final int maxOffset;

	public Range(int minOffset, int maxOffset) {
		this.minOffset = minOffset;
		this.maxOffset = maxOffset;
	}

	public boolean contains(char codePoint) {
		return minOffset <= codePoint && codePoint <= maxOffset;
	}
}
