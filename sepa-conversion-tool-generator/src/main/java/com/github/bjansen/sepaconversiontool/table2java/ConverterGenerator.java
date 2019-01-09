package com.github.bjansen.sepaconversiontool.table2java;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Generates a subclass of {@code SepaCharacterConverter} from a map of character conversions.
 */
public class ConverterGenerator {

	private final String className;
	private final String packageName;
	private final Map<Character, String> conversions;

	/**
	 * Builds a new generator from the given conversions map.
	 *
	 * @param className   the target class name
	 * @param packageName the target class package
	 * @param conversions a map of character conversions applied by the generated converter
	 */
	public ConverterGenerator(String className, String packageName, Map<Character, String> conversions) {
		if (conversions.isEmpty()) {
			throw new IllegalStateException("Conversion map is empty!");
		}

		this.className = className;
		this.packageName = packageName;
		this.conversions = conversions;
	}

	/**
	 * Generates the target class in the given {@code targetDirectory}. Sub-directories will automatically be
	 * created for the target package.
	 *
	 * @param targetDirectory the base directory where the class and its package folders will be created
	 * @throws IOException if the class cannot be generated
	 */
	public void generate(File targetDirectory) throws IOException {
		TypeSpec typeSpec = TypeSpec.classBuilder(ClassName.get(packageName, className))
				.superclass(ClassName.get("com.github.bjansen.sepaconversiontool.converters", "SepaCharacterConverter"))
				.addField(generateReplacementArray())
				.addStaticBlock(generateReplacementBlock())
				.addMethod(generateGetReplacementsMethod())
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
}
