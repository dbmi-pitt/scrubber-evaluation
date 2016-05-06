package edu.pitt.dbmi.deid.comparison.annotator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import edu.pitt.dbmi.deid.comparison.viewer.DeidComparisonViewer;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;


public class RedactorUtf8 {
	
	private final char redactingChar = '\u25A0';
	
	private String phiContent;
	private String scrubbedContent;
	
	private List<String> scrubPatterns = new ArrayList<String>();
	private int scrubControls = 0;
	
	private String redactedContent;
	private String redactedPhi;
	private final List<Annotation> annotations = new ArrayList<>();
	private final List<String> annotationKinds = new ArrayList<>();
	
	public static void main(String[] args) {	
		try {
			RedactorUtf8 redactor = new RedactorUtf8();
//			redactor.addScrubPattern("[\\[][A-Z]+[\\]]");
			redactor.addScrubPattern("[\\*]{2,}[A-Z][A-Z-]+[\\[][^\\]]+[\\]]|[\\*]{2,}PLACE");
			redactor.setScrubControls(Pattern.MULTILINE | Pattern.DOTALL);
			String fileNamePhi = "cartoon/report00000.txt";
			String fileNameMach = "deid/report00000.txt";
			String contentPhi = IOUtils.toString(DeidComparisonViewer.class.getResourceAsStream(fileNamePhi), "UTF-8");
			String contentMach = IOUtils.toString(DeidComparisonViewer.class.getResourceAsStream(fileNameMach), "UTF-8");
			redactor.setPhiContent(contentPhi);
			redactor.setScrubbedContent(contentMach);
			redactor.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void execute() {
		redactedContent = scrubbedContent;
		for (String scrubPattern : scrubPatterns) {
			redactUsingRegex(scrubPattern);
		}
		annotations.clear();
		diffMatch();
		redactPhi();
	}
	
	private void redactPhi() {
		redactedPhi = phiContent;
		for (Annotation annotation : annotations) {
			redactedPhi = redactedPhi.substring(0, (int)annotation.getsPos()) +
					generateRedactionString(annotation.getSpannedText().length()) +
					redactedPhi.substring((int)annotation.getePos(),redactedPhi.length());
			
		}
		
	}

	private void redactUsingRegex(String scrubPattern) {
		Pattern pattern = Pattern.compile(scrubPattern, scrubControls);
		Matcher matcher = pattern.matcher(scrubbedContent);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			annotationKinds.add(matcher.group());
			String redactionString = generateRedactionString(matcher.group().length());
			matcher.appendReplacement(sb, redactionString);
		}
		matcher.appendTail(sb);
		redactedContent = sb.toString();	
	}
	
	private String generateRedactionString(int len) {
		final char[] redactingChrs = new char[len];
		for (int idx = 0; idx < redactingChrs.length; idx++) {
			redactingChrs[idx] = redactingChar;
		}
		return new String(redactingChrs);
	}
	
	private void diffMatch() {
		diff_match_patch dmp = new diff_match_patch();
		LinkedList<Diff> diffs = dmp.diff_main(phiContent, redactedContent, false);
		int cursor = 0;
		for (Diff diff : diffs) {
			if (diff.operation == diff_match_patch.Operation.EQUAL) {
				cursor += diff.text.length();
			}
			else if (diff.operation == diff_match_patch.Operation.DELETE) {
				
				if (!annotationKinds.isEmpty()) {
					String annotationKind = annotationKinds.remove(0);
					Annotation annotation = new Annotation();
					annotation.setAnnotationKind(annotationKind);
					annotation.setsPos(cursor);
					cursor += diff.text.length();
					annotation.setePos(cursor);
					annotation.setSpannedText(diff.text);
					annotations.add(annotation);
				}
				
			}
		}
	}

	public String getPhiContent() {
		return phiContent;
	}

	public String getScrubbedContent() {
		return scrubbedContent;
	}

	public String getRedactedContent() {
		return redactedContent;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public void setPhiContent(String phiContent) {
		this.phiContent = phiContent;
	}

	public void setScrubbedContent(String scrubbedContent) {
		this.scrubbedContent = scrubbedContent;
	}
	
	public void addScrubPattern(String scrubPattern) {
		scrubPatterns.add(scrubPattern);
	}

	public int getScrubControls() {
		return scrubControls;
	}

	public void setScrubControls(int scrubControls) {
		this.scrubControls = scrubControls;
	}

	public String getRedactedPhi() {
		return redactedPhi;
	}
}
