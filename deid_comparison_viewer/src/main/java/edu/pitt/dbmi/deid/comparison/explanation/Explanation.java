package edu.pitt.dbmi.deid.comparison.explanation;

import java.util.ArrayList;
import java.util.List;

import edu.pitt.dbmi.deid.comparison.annotator.Annotation;

public class Explanation implements Comparable<Explanation> {
	

	private String header;
	private String content;
	private List<String> reports = new ArrayList<String>();
	private List<Annotation> annotations = new ArrayList<Annotation>();
	
	public Explanation(String header, String content) {
		this.header = header;
		this.content = content;
	}
	
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
//	public String getXmlContent() {
//		return content.replaceAll("\n", CARRIAGE_RETURN_LINE_FEED);
//	}
//	
//	public void setXmlContent(String xmlContent) {
//		content = xmlContent.replaceAll(CARRIAGE_RETURN_LINE_FEED, "\n");
//	}

	public List<String> getReports() {
		return reports;
	}

	public void setReports(List<String> reports) {
		this.reports = reports;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

	public String toString() {
		return getHeader();
	}

	@Override
	public int compareTo(Explanation otherExplanation) {
		return getHeader().compareTo(otherExplanation.getHeader());
	}
	
}
