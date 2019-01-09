# sepa-conversion-tool
A Java library that implements the [SEPA conversion table](https://www.europeanpaymentscouncil.eu/document-library/guidance-documents/sepa-requirements-extended-character-set-unicode-subset-best)
provided by the [EPC](https://www.europeanpaymentscouncil.eu/)

## Example

```java
@Test
void convertSample() {
	SepaCharacterConverter converter = SepaCharacterConverter.basicLatin();

	// characters that don't need conversion are preserved
	assertEquals("hello", converter.convertToSepaCharacters("hello"));

	// characters that need conversion are replaced
	assertEquals("Hello", converter.convertToSepaCharacters("Ħĕŀŀœ"));

	// characters that are outside of the range of the convertion table are dropped
	assertEquals("outside of range", converter.convertToSepaCharacters("outsi�de o�f r≧an∰g℗e"));
}

```

## Usage

The library can be imported to Maven projects like this:

```xml
<dependency>
	<groupId>com.github.bjansen</groupId>
	<artifactId>sepa-conversion-tool</artifactId>
	<version>0.1.0</version>
</dependency>
```

The next step is to choose your converter, and use it:

```java
SepaCharacterConverter converter = SepaCharacterConverter.basicLatin(); 
// or SepaCharacterConverter.longTermSepa()

String converted = converter.convertToSepaCharacters("Ħĕŀŀœ"); // will be converted to "Hello"
```
