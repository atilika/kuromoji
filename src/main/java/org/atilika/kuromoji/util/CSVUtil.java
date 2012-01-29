/**
 * Copyright © 2010-2012 Atilika Inc.  All rights reserved.
 *
 * Atilika Inc. licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License.  A copy of the License is distributed with this work in the
 * LICENSE.txt file.  You may also obtain a copy of the License from
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
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
		if (quoteCount % 2 != 0) {
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
