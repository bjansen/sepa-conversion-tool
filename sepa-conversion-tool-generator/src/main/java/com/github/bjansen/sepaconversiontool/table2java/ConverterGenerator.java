package com.github.bjansen.sepaconversiontool.table2java;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates a subclass of {@code SepaCharacterConverter} from a map of character conversions.
 */
public class ConverterGenerator {

	private static final Pattern RANGE_PATTERN = Pattern.compile("U\\+(\\p{XDigit}+)\\.\\.U\\+(\\p{XDigit}+)");
	private static final String TARGET_PKG = "com.github.bjansen.sepaconversiontool.converters";

	private final String className;
	private final String packageName;
	private final Map<Character, String> conversions;
	private final List<String> suppressions;

	/**
	 * Builds a new generator from the given conversions map.
	 *
	 * @param className    the target class name
	 * @param packageName  the target class package
	 * @param conversions  a map of character conversions applied by the generated converter
	 * @param suppressions a list of character ranges (e.g. "U+0460..U+20AB") that should always be replaced with a "."
	 */
	public ConverterGenerator(String className, String packageName,
							  Map<Character, String> conversions,
							  List<String> suppressions) {
		if (conversions.isEmpty()) {
			throw new IllegalStateException("Conversion map is empty!");
		}

		this.className = className;
		this.packageName = packageName;
		this.conversions = conversions;
		this.suppressions = suppressions;
	}

	/**
	 * Generates the target class in the given {@code targetDirectory}. Sub-directories will automatically be
	 * created for the target package.
	 *
	 * @param targetDirectory the base directory where the class and its package folders will be created
	 * @throws IOException if the class cannot be generated
	 */
	public void generate(File targetDirectory) throws IOException {
		TypeSpec.Builder builder = TypeSpec.classBuilder(ClassName.get(packageName, className))
				.superclass(ClassName.get(TARGET_PKG, "SepaCharacterConverter"))
				.addField(generateReplacementArray())
				.addStaticBlock(generateReplacementBlock())
				.addMethod(generateGetReplacementsMethod());

		if (!suppressions.isEmpty()) {
			builder.addField(generateSuppressedField());
			builder.addMethod(generateGetSuppressedRangesMethod());
		}

		TypeSpec typeSpec = builder
				.build();

		JavaFile.builder(packageName, typeSpec)
				.skipJavaLangImports(true)
				.build()
				.writeTo(targetDirectory);
	}

	private FieldSpec generateReplacementArray() {
		char maxChar = getMaxNonNullConversion();

		return FieldSpec.builder(String[].class, "REPLACEMENTS", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer("new String[$L]", maxChar + 1)
				.build();
	}

	private Character getMaxNonNullConversion() {
		Character maxNonNullEntry = 0;

		for (Map.Entry<Character, String> entry : conversions.entrySet()) {
			if (entry.getValue() != null && entry.getKey() > maxNonNullEntry) {
				maxNonNullEntry = entry.getKey();
			}
		}

		return maxNonNullEntry;
	}

	private CodeBlock generateReplacementBlock() {
		CodeBlock.Builder builder = CodeBlock.builder();

		for (Map.Entry<Character, String> entry : conversions.entrySet()) {
			if (entry.getValue() != null) {
				String unicodeKey = String.format("0x%04x", (int) entry.getKey());
				builder.addStatement("REPLACEMENTS[$L] = $S", unicodeKey, entry.getValue());
			}
		}

		return builder.build();
	}

	private MethodSpec generateGetReplacementsMethod() {
		return MethodSpec.methodBuilder("getReplacements")
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC)
				.returns(String[].class)
				.addCode("return REPLACEMENTS;\n")
				.build();
	}

	private FieldSpec generateSuppressedField() {
		ClassName range = ClassName.get(TARGET_PKG, "Range");
		ParameterizedTypeName listRange = ParameterizedTypeName.get(ClassName.get(List.class), range);

		CodeBlock ranges = suppressions.stream()
				.map(s -> {
					Matcher matcher = RANGE_PATTERN.matcher(s);

					matcher.matches();

					return CodeBlock.builder()
							.indent()
							.add("new Range(0x$L, 0x$L)", matcher.group(1), matcher.group(2))
							.unindent()
							.build();
				})
				.collect(CodeBlock.joining(",\n"));

		CodeBlock initializer = CodeBlock.builder()
				.add("$T.asList(\n$L\n)", Arrays.class, ranges)
				.build();

		return FieldSpec.builder(listRange, "SUPPRESSIONS", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer(initializer)
				.build();
	}

	private MethodSpec generateGetSuppressedRangesMethod() {
		ClassName range = ClassName.get(TARGET_PKG, "Range");

		return MethodSpec.methodBuilder("getSuppressedRanges")
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC)
				.returns(ParameterizedTypeName.get(ClassName.get(List.class), range))
				.addCode("return SUPPRESSIONS;\n")
				.build();
	}
}
