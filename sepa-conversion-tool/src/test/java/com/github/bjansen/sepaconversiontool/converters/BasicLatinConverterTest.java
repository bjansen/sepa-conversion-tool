package com.github.bjansen.sepaconversiontool.converters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicLatinConverterTest {

	@Test
	void convertSample() {
		SepaCharacterConverter converter = SepaCharacterConverter.basicLatin();

		assertEquals("hello", converter.convertToSepaCharacters("hello"));
		assertEquals("Hello", converter.convertToSepaCharacters("Ħĕŀŀœ"));
		assertEquals("suka blaty", converter.convertToSepaCharacters("сука блять"));
		assertEquals("outside of range", converter.convertToSepaCharacters("outsi�de o�f r≧an∰g℗e"));
	}
}
