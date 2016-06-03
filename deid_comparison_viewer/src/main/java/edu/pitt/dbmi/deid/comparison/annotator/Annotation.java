package edu.pitt.dbmi.deid.comparison.annotator;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import edu.pitt.dbmi.deid.comparison.explanation.Explanation;
import edu.pitt.dbmi.deid.comparison.util.ConversionUtils;

public class Annotation {
	
	private String report = "UNDEFINED";
	private String redactor = "UNDEFINED";
	private String annotationKind;
	private long sPos = -1L;
	private long ePos = -1L;
	private String spannedText = "UNDEFINED";
	private Explanation explanation;
	
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
	
	public String getReport() {
		return report;
	}
	public void setReport(String report) {
		this.report = report;
	}
	public Explanation getExplanation() {
		return explanation;
	}
	public void setExplanation(Explanation explanation) {
		this.explanation = explanation;
	}
	
	public String getKey() {
		StringBuilder sb = new StringBuilder();
		sb.append(report);
		sb.append(":");
		sb.append(redactor);
		sb.append(":");
		sb.append(ConversionUtils.formatLongAsString(sPos));
		sb.append(":");
		sb.append(ConversionUtils.formatLongAsString(ePos));
		return sb.toString();	
	}
	
	public String toString() {
	     return ReflectionToStringBuilder.toString(this);
	 }


}
