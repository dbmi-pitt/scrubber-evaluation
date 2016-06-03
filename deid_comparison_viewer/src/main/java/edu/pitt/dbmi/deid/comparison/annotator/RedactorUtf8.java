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
		testOne();
	}

	public static void testOne() {
		try {
			RedactorUtf8 redactor = new RedactorUtf8();
			redactor.addScrubPattern("[\\[][A-Z]+[\\]]");
			redactor.setScrubControls(Pattern.MULTILINE | Pattern.DOTALL);
			String fileNamePhi = "cartoon/report00004.txt";
			String fileNameMach = "nlm160518/report00004.nphi.txt";
			String contentPhi = IOUtils.toString(DeidComparisonViewer.class.getResourceAsStream(fileNamePhi), "UTF-8");
			String contentMach = IOUtils.toString(DeidComparisonViewer.class.getResourceAsStream(fileNameMach),
					"UTF-8");
			contentPhi = getPhi();
			contentMach = getNlm();
			redactor.setPhiContent(contentPhi);
			redactor.setScrubbedContent(contentMach);
			redactor.execute();

			System.out.println(redactor);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getPhi() {
		StringBuffer sb = new StringBuffer();
		sb.append("BILATERAL DIAGNOSTIC MAMMOGRAM: 01/14/2010 11:19 AM\n");
		sb.append("HISTORY: LEFT BREAST CA.\n");
		sb.append("TECHNIQUE: Bilateral filmscreen mammography was performed in both the\n");
		sb.append("mediolateral oblique and craniocaudal projections. Linear scar\n");
		sb.append("markers were used.\n");
		sb.append("COMPARISON: 01/14/09, left breast 06/06/08, right 06/04/08, needle\n");
		sb.append("localization films and post-biopsy films from 07/18 and 06/13/07,\n");
		sb.append("left breast 05/14/07, and 09/29/06 (additional views 11/13/06).\n");
		sb.append("FINDINGS: The breasts remain approximately 50% glandular.\n");
		sb.append("Post-treatment and postoperative deformity again noted involving the\n");
		sb.append("left breast especially in its upper central area.\n");
		sb.append("There is no evidence of new mass or clustered microcalcifications. No\n");
		sb.append("new worrisome or suspicious finding is identified on either side.\n");
		sb.append("IMPRESSION:\n");
		sb.append("STABLE POST-TREATMENT AND POSTOPERATIVE CHANGES INVOLVING THE LEFT\n");
		sb.append("BREAST. NO EVIDENCE OF NEW OR RECURRENT MALIGNANCY AND NO SIGNIFICANT\n");
		sb.append("CHANGE SINCE LAST YEAR.\n");
		sb.append("CONTINUING ANNUAL MAMMOGRAPHIC SURVEILLANCE ADVISED.\n");
		sb.append("BIRAD CATEGORY:  2 - BENIGN FINDINGS (ROUTINE FOLLOW-UP).\n");
		sb.append("Dictated by:    Wally Walrus Signed by:  Wally Walrus Signed on: 01/14/2010 at 2:35 PM\n");
		return sb.toString();
	}

	private static String getNlm() {
		StringBuffer sb = new StringBuffer();
		sb.append("BILATERAL DIAGNOSTIC MAMMOGRAM: [DATE] 11:19 AM\n");
		sb.append("HISTORY: LEFT BREAST CA.\n");
		sb.append("TECHNIQUE: Bilateral filmscreen mammography was performed in both the\n");
		sb.append("mediolateral oblique and craniocaudal projections. Linear scar\n");
		sb.append("markers were used.\n");
		sb.append("COMPARISON: [DATE], left breast [DATE], right [DATE], needle\n");
		sb.append("localization films and post-biopsy films from [DATE] and [DATE],\n");
		sb.append("left breast [DATE], and [DATE] (additional views [DATE]).\n");
		sb.append("FINDINGS: The breasts remain approximately 50% glandular.\n");
		sb.append("Post-treatment and postoperative deformity again noted involving the\n");
		sb.append("left breast especially in its upper central area.\n");
		sb.append("There is no evidence of new mass or clustered microcalcifications. No\n");
		sb.append("new worrisome or suspicious finding is identified on either side.\n");
		sb.append("IMPRESSION:\n");
		sb.append("STABLE POST-TREATMENT AND POSTOPERATIVE CHANGES INVOLVING THE LEFT\n");
		sb.append("BREAST. NO EVIDENCE OF [ADDRESS] RECURRENT MALIGNANCY AND NO SIGNIFICANT\n");
		sb.append("CHANGE SINCE LAST YEAR.\n");
		sb.append("CONTINUING ANNUAL MAMMOGRAPHIC SURVEILLANCE ADVISED.\n");
		sb.append("BIRAD CATEGORY:  2 - BENIGN FINDINGS (ROUTINE FOLLOW-UP).\n");
		sb.append("Dictated by:    [PERSONALNAME] by:  [PERSONALNAME] Walrus Signed on: [DATE] at 2:35 PM\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("##### DOCUMENT #############################################################\n");
		sb.append("##### ConfigFile:nlmconfig_2016_05_18.txt\n");
		sb.append("##### Outfile=E:/work/deid/corpora/deepphe_reidentified/nlm160518/report00004.nphi.txt\n");
		sb.append("###########################################################################\n");
		sb.append("#Date:2016.05.18_10.33.40\n");
		sb.append("#NLM-Scrubber v.2016.0512w\n");
		sb.append("###########################################################################\n");
		return sb.toString();
	}

	public static void testTwo() {
		try {
			RedactorUtf8 redactor = new RedactorUtf8();
			// redactor.addScrubPattern("[\\[][A-Z]+[\\]]");
			redactor.addScrubPattern("[\\*]{2,}[A-Z][A-Z-]+[\\[][^\\]]+[\\]]|[\\*]{2,}PLACE");
			redactor.setScrubControls(Pattern.MULTILINE | Pattern.DOTALL);
			String fileNamePhi = "cartoon/report00000.txt";
			String fileNameMach = "deid/report00000.txt";
			String contentPhi = IOUtils.toString(DeidComparisonViewer.class.getResourceAsStream(fileNamePhi), "UTF-8");
			String contentMach = IOUtils.toString(DeidComparisonViewer.class.getResourceAsStream(fileNameMach),
					"UTF-8");
			redactor.setPhiContent(contentPhi);
			redactor.setScrubbedContent(contentMach);
			redactor.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void execute() {
		truncateScrubbedContent();
		redactedContent = scrubbedContent;
		for (String scrubPattern : scrubPatterns) {
			redactUsingRegex(scrubPattern);
		}
		annotations.clear();
		diffMatch();
		redactPhi();
	}
	
	private void truncateScrubbedContent() {
		Pattern truncatationDetectingPattern = Pattern.compile("#{5} DOCUMENT #############################################################$", Pattern.MULTILINE | Pattern.DOTALL);
		Matcher matcher = truncatationDetectingPattern.matcher(scrubbedContent);
		if (matcher.find()) {
			scrubbedContent = scrubbedContent.substring(0, matcher.start());
		}
	}

	private void redactPhi() {
		redactedPhi = phiContent;
		for (Annotation annotation : annotations) {
			String prefix = redactedPhi.substring(0, (int) annotation.getsPos());
			String spannedText = annotation.getSpannedText();
			String redactedSpan = generateRedactionString(spannedText.length());
			String suffix = redactedPhi.substring((int) annotation.getePos(), redactedPhi.length());
			redactedPhi = prefix + redactedSpan + suffix;
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
//		displayDiffs(diffs);
		for (Diff diff : diffs) {
			if (diff.operation == diff_match_patch.Operation.EQUAL) {
				cursor += diff.text.length();
			} else if (diff.operation == diff_match_patch.Operation.DELETE) {

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
	
	@SuppressWarnings("unused")
	private void displayDiffs(LinkedList<Diff> diffs ) {
		for (Diff diff : diffs) {
			System.out.println(diff);
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

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nPhi:\n");
		sb.append(getPhiContent());
		sb.append("\nScrubbed:\n");
		sb.append(getScrubbedContent());
		sb.append("\nRedacted Content:\n");
		sb.append(getRedactedContent());
		sb.append("\nRedacted Phi Content:\n");
		sb.append(getRedactedPhi());
		for (Annotation annot : annotations) {
			sb.append(annot.toString() + "\n");
		}
		return sb.toString();
	}
}
