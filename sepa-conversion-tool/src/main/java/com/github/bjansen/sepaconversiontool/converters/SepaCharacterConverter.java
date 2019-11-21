package com.github.bjansen.sepaconversiontool.converters;

import java.util.Collections;
import java.util.List;

/**
 * Converts a string to a limited subset of characters in compliance with recommendations
 * made by the European Payments Council.
 *
 * Use one of the static methods to instantiate a converter.
 */
public abstract class SepaCharacterConverter {

	/**
	 * Creates a converter based on a limited set of Latin characters.
	 */
	public static SepaCharacterConverter basicLatin() {
		return new BasicLatinConverter();
	}

	/**
	 * Creates a converter that extends the Latin character set to cater for all
	 * European local languages. Also know as the "long-term SEPA character set".
	 */
	public static SepaCharacterConverter longTermSepa() {
		return new LongTermSepaConverter();
	}

	/**
	 * Creates a converter based on a custom set of character replacements.
	 *
	 * @param replacementTable the character replacement table. The table index refers to source
	 *                         characters, and the value refers to replacement characters. Use a
	 *                         {@code null} value to keep the source character, use an empty string
	 *                         to drop it. Source characters outside of the range of this table will
	 *                         be dropped.
	 */
	public static SepaCharacterConverter fromCustomReplacements(String[] replacementTable) {
		return new SepaCharacterConverter() {
			@Override
			public String[] getReplacements() {
				return replacementTable;
			}
		};
	}

	/**
	 * A list of ranges of characters that should always be replaced with {@code .}. This can be used to
	 * make the replacement array much shorter.
	 */
	public List<Range> getSuppressedRanges() {
		return Collections.emptyList();
	}

	public abstract String[] getReplacements();

	/**
	 * Converts the given {@code source} string using the character replacement table.
	 *
	 * @param source the string to convert. Can be {@code null}.
	 * @return the converted string, or {@code null} if the {@code source} was {@code null}.
	 */
	public String convertToSepaCharacters(String source) {
		if (source == null) {
			return null;
		}

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < source.length(); i++) {
			char sourceChar = source.charAt(i);
			builder.append(getReplacement(sourceChar));
		}

		return builder.toString();
	}

	private String getReplacement(char sourceChar) {
		String[] replacements = getReplacements();

		if (sourceChar >= replacements.length) {
			return ".";
		}

		for (Range range : getSuppressedRanges()) {
			if (range.contains(sourceChar)) {
				return ".";
			}
		}

		String replacement = replacements[sourceChar];

		return replacement == null ? String.valueOf(sourceChar) : replacement;
	}
}
