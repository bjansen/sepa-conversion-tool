package com.github.bjansen.sepaconversiontool.table2java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
				parser.getConversions()
		).generate(new File(outputDirectory));
	}

	private static void generateBuiltinConverters() throws IOException {
		generateBuiltinConverter("BasicLatinConverter", "E");
		generateBuiltinConverter("LongTermSepaConverter", "G");
	}

	private static void generateBuiltinConverter(String className, String destColumn) throws IOException {
		ConversionTableParser parser = new ConversionTableParser(
				ConversionTableToGenerator.class.getResourceAsStream("/EPC217-08-SEPA-Conversion-Table.xls"),
				"Tabelle1",
				"C",
				destColumn
		);

		new ConverterGenerator(
				className,
				"com.github.bjansen.sepaconversiontool.converters",
				parser.getConversions()
		).generate(new File("./sepa-conversion-tool/src/main/java"));
	}
}
