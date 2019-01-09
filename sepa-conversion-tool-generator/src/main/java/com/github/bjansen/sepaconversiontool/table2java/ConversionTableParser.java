package com.github.bjansen.sepaconversiontool.table2java;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConversionTableParser {

	private final HSSFWorkbook workbook;
	private final String inputSheet;
	private final int sourceColumn;
	private final int destColumn;

	ConversionTableParser(InputStream xlsInput, String inputSheet, String sourceColumn, String destColumn)
			throws IOException {
		workbook = new HSSFWorkbook(xlsInput);

		this.inputSheet = inputSheet;
		this.sourceColumn = CellReference.convertColStringToIndex(sourceColumn);
		this.destColumn = CellReference.convertColStringToIndex(destColumn);
	}

	Map<Character, String> getConversions() {
		HSSFSheet sheet = workbook.getSheet(inputSheet);
		Iterator<Row> rows = sheet.iterator();
		Map<Character, String> conversions = new LinkedHashMap<>();

		while (rows.hasNext()) {
			Row row = rows.next();

			Cell source = row.getCell(sourceColumn);

			if (containsUnicodeCharacter(source)) {
				Character sourceValue = parseUnicodeCharacter(source.getStringCellValue());
				String destValue = null;

				Cell dest = row.getCell(destColumn);
				if (containsUnicodeCharacter(dest)) {
					destValue = parseUnicodeString(dest.getStringCellValue());
				}

				conversions.put(sourceValue, destValue);
			}
		}

		return conversions;
	}

	private boolean containsUnicodeCharacter(Cell cell) {
		return cell != null
				&& cell.getCellType() == CellType.STRING
				&& cell.getStringCellValue().startsWith("U+");
	}

	private Character parseUnicodeCharacter(String cellValue) {
		int hexCode = Integer.parseInt(cellValue.trim().replace("U+", ""), 16);
		return (char) hexCode;
	}

	private String parseUnicodeString(String cellValue) {
		String[] characters = cellValue.replace(" ", "").split(",");
		StringBuilder builder = new StringBuilder();

		for (String character : characters) {
			builder.append(parseUnicodeCharacter(character));
		}

		return builder.toString();
	}
}
