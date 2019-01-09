package com.github.bjansen.sepaconversiontool.table2java;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConversionTableParserTest {

	@Test
	void checkThatOfficialTableIsCorrectlyParsed() throws IOException {
		ConversionTableParser parser = new ConversionTableParser(
				getClass().getResourceAsStream("/EPC217-08-SEPA-Conversion-Table.xls"),
				"Tabelle1",
				"C",
				"E"
		);

		Map<Character, String> conversions = parser.getConversions();

		assertEquals(1089, conversions.keySet().size());

		// entries should be sorted
		Map.Entry<Character, String> firstEntry = conversions.entrySet().iterator().next();
		assertEquals('\u0020', (char) firstEntry.getKey());

		// check some values
		assertNull(conversions.get('\u0020'));
		assertEquals("\u002E", conversions.get('\u0021'));
		assertNull(conversions.get('\u0022'));
		assertEquals("\u002E", conversions.get('\u0023'));
		assertNull(conversions.get('\u002B'));
		assertEquals("\u002C", conversions.get(';'));
		assertEquals("\u002E", conversions.get('\u0084'));

		// both the same
		assertEquals("TH", conversions.get('Θ'));
		assertEquals("\u0054\u0048", conversions.get('\u0398'));

		assertEquals("\u0050\u0053", conversions.get('Ψ'));
		assertEquals("\u0070\u0073", conversions.get('ψ'));
		assertEquals("E", conversions.get('€'));
	}
}
