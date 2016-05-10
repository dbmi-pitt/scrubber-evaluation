package edu.pitt.dbmi.deid.comparison.explanation;

import java.util.ArrayList;
import java.util.List;

public class Explanation implements Comparable<Explanation> {
	
	private String header;
	private String content;
	private List<String> reports = new ArrayList<String>();
	
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

	public List<String> getReports() {
		return reports;
	}

	public void setReports(List<String> reports) {
		this.reports = reports;
	}

	public String toString() {
		return getHeader();
	}

	@Override
	public int compareTo(Explanation otherExplanation) {
		return getHeader().compareTo(otherExplanation.getHeader());
	}
	
}
