package edu.pitt.dbmi.deid.comparison.annotator;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class Annotation {
	
	private String redactor;
	private String annotationKind;
	private long sPos = -1L;
	private long ePos = -1L;
	private String spannedText = null;
	
	public String getRedactor() {
		return redactor;
	}
	public void setRedactor(String redactor) {
		this.redactor = redactor;
	}
	public String getAnnotationKind() {
		return annotationKind;
	}
	public void setAnnotationKind(String annotationKind) {
		this.annotationKind = annotationKind;
	}
	public long getsPos() {
		return sPos;
	}
	public void setsPos(long sPos) {
		this.sPos = sPos;
	}
	public long getePos() {
		return ePos;
	}
	public void setePos(long ePos) {
		this.ePos = ePos;
	}
	public String getSpannedText() {
		return spannedText;
	}
	public void setSpannedText(String spannedText) {
		this.spannedText = spannedText;
	}
	
	public String getRedactedText() {
		return generateRedactionString(getSpannedText().length());
	}
	
	private String generateRedactionString(int len) {
		final char[] redactingChrs = new char[len];
		for (int idx = 0; idx < redactingChrs.length; idx++) {
			redactingChrs[idx] = '\u25A0';
		}
		return new String(redactingChrs);
	}
	
	public String toString() {
	     return ReflectionToStringBuilder.toString(this);
	 }
	
	

}
