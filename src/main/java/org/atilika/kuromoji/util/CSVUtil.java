package org.atilika.kuromoji.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVUtil {
	private static final char QUOTE = '"';
	
	private static final char COMMA = ',';

	private static final Pattern QUOTE_REPLACE_PATTERN = Pattern.compile("^\"([^\"]+)\"$");

	private static final String ESCAPED_QUOTE = "\"\"";
	
	/**
	 * Parse CSV line
	 * @param line
	 * @return Array of values
	 */
	public static String[] parse(String line) {
		boolean insideQuote = false;
		ArrayList<String> result = new ArrayList<String>();		
		int quoteCount = 0;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			if(c == QUOTE) {
				insideQuote = !insideQuote;
				quoteCount++;
			}
			
			if(c == COMMA && !insideQuote) {
				String value = sb.toString();
				value = unQuoteUnEscape(value);
				result.add(value);
				sb = new StringBuilder();
				continue;
			}
			
			sb.append(c);
		}
		
		result.add(sb.toString());

		// Validate
		if(quoteCount % 2 != 0) {
			return new String[0];
		}
		
		return result.toArray(new String[result.size()]);
	}
	
	private static String unQuoteUnEscape(String original) {
		String result = original;
		
		// Unquote
		Matcher m = QUOTE_REPLACE_PATTERN.matcher(original);
		if(m.matches()) {
			result = m.group(1);
		}
		
		// Unescape
		result = result.replaceAll(ESCAPED_QUOTE, "\"");
		
		return result;
		
	}
	
	/**
	 * Quote and escape input value for CSV
	 * @param original
	 * @return
	 */
	public static String quoteEscape(String original) {
		String result = original.replaceAll("\"", ESCAPED_QUOTE);
		if(result.indexOf(COMMA) >= 0) {
			result = "\"" + result + "\"";
		}
		return result;
	}

}
