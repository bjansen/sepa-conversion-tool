package com.github.bjansen.sepaconversiontool.table2java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class ConversionTableToGenerator {

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			generateBuiltinConverters();
			return;
		}

		if (args.length != 7) {
			System.out.println("Usage : " + ConversionTableToGenerator.class.getName()
					+ " <xlsFile> <sheetName> <sourceColumn> <destColumn> <packageName> <className> <outputDirectory>");
			return;
		}

		int idx = 0;
		String xlsFile = args[idx++];
		String sheetName = args[idx++];
		String sourceColumn = args[idx++];
		String destColumn = args[idx++];
		String packageName = args[idx++];
		String className = args[idx++];
		String outputDirectory = args[idx++];

		ConversionTableParser parser = new ConversionTableParser(
				new FileInputStream(new File(xlsFile)),
				sheetName,
				sourceColumn,
				destColumn
		);

		new ConverterGenerator(
				className,
				packageName,
				parser.getConversions(),
				Collections.emptyList()
		).generate(new File(outputDirectory));
	}

	private static void generateBuiltinConverters() throws IOException {
		generateBuiltinConverter("BasicLatinConverter", "E", "U+0460..U+20AB");
		generateBuiltinConverter("LongTermSepaConverter", "G", "U+0460..U+20AB");
	}

	private static void generateBuiltinConverter(String className, String destColumn, String... suppressions) throws IOException {
		ConversionTableParser parser = new ConversionTableParser(
				ConversionTableToGenerator.class.getResourceAsStream("/EPC217-08-SEPA-Conversion-Table.xls"),
				"Tabelle1",
				"C",
				destColumn
		);

		new ConverterGenerator(
				className,
				"com.github.bjansen.sepaconversiontool.converters",
				parser.getConversions(),
				suppressions == null ? Collections.emptyList() : Arrays.asList(suppressions)
		).generate(new File("./sepa-conversion-tool/src/main/java"));
	}
}
