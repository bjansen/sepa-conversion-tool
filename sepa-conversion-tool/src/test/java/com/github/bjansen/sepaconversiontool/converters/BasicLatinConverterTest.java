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
		assertEquals("outsi.de o.f r.an.g.e", converter.convertToSepaCharacters("outsi�de o�f r≧an∰g℗e"));
		assertEquals("hello.z.z.world", converter.convertToSepaCharacters("helloय़zॠzॡworld"));
		assertEquals(".", converter.convertToSepaCharacters("\u3000"));
		assertEquals(".........E.", converter.convertToSepaCharacters("љњћќѝўџ\u0500\u20ab€\u20ad"));
	}
}
