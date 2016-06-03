package edu.pitt.dbmi.deid.comparison.util;

import org.apache.commons.lang.StringUtils;

public class ConversionUtils {
	
	public static String formatLongAsString(Long longValue) {
		return  StringUtils.leftPad(longValue + "", 10, "0");
	}
	
	public static Long convertStringToLong(String longValueAsString) {
		return new Long(longValueAsString);
	}

}
