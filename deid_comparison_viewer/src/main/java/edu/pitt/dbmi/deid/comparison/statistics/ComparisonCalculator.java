package edu.pitt.dbmi.deid.comparison.statistics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import edu.pitt.dbmi.deid.comparison.annotator.Annotation;
import edu.pitt.dbmi.deid.comparison.annotator.RedactorUtf8;
import edu.pitt.dbmi.deid.comparison.viewer.ContentViewer;

public class ComparisonCalculator {
	
//	private static final String NLM_VERSION = "nlm160405";
	private static final String NLM_VERSION = "nlm160518";

	private final HashMap<String, Annotation> deidAnnotationMap = new HashMap<String, Annotation>();
	private final HashSet<String> deidAnnotations = new HashSet<String>();
	private final HashSet<String> nlmAnnotations = new HashSet<String>();
	private final HashSet<String> misses = new HashSet<String>();
	private double tp = 0.0d;
	private double fp = 0.0d;
	private double fn = 0.0d;
	private double prec = 0.0d;
	private double reca = 0.0d;
	private int totalBytesRead = 0;

	public static void main(String[] args) {
		try {
			ComparisonCalculator calculator = new ComparisonCalculator();
			calculator.calculate();
			System.out.println(calculator);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void calculate() throws IOException {

		generateRedactionSpans();

		for (String deidAnnot : deidAnnotations) {
			if (nlmAnnotations.contains(deidAnnot)) {
				tp++;
			} else {
				misses.add(deidAnnot);
				fn++;
			}
		}

		int tpComparison = 0;
		for (String nlmAnnot : nlmAnnotations) {
			if (deidAnnotations.contains(nlmAnnot)) {
				tpComparison++;
			} else {
				fp++;
			}
		}

		if (tp == tpComparison) {
			System.out.println("True positive invariant confirmed.");
		} else {
			System.out.println("True positive invariant fails, tp = " + tp + " tp-prime = " + tpComparison);
		}

		prec = tp / (tp + fp);
		reca = tp / (tp + fn);
	}

	private void generateRedactionSpans() throws IOException {

		RedactorUtf8 deidRedactor = new RedactorUtf8();
		String deidScrubberPattern = "[\\*]{2,}[A-Z][A-Z-]+[\\[][^\\]]+[\\]]|[\\*]{2,}PLACE";
		deidRedactor.addScrubPattern(deidScrubberPattern);
		deidRedactor.setScrubControls(Pattern.MULTILINE | Pattern.DOTALL);

		RedactorUtf8 nlmRedactor = new RedactorUtf8();
		String nlmScrubberPattern = "[\\[][A-Z]+[\\]]";
		nlmRedactor.addScrubPattern(nlmScrubberPattern);
		nlmRedactor.setScrubControls(Pattern.MULTILINE | Pattern.DOTALL);

		for (int idx = 0; idx < 1037; idx++) {

			String reportBaseNumber = StringUtils.leftPad(idx + "", 5, "0");

			String phiReportName = "report" + reportBaseNumber + ".txt";
			phiReportName = "cartoon" + File.separator + phiReportName;

			String deidReportName = "report" + reportBaseNumber + ".txt";
			deidReportName = "deid" + File.separator + deidReportName;

			String nlmReportName = "report" + reportBaseNumber + ".nphi.txt";
			nlmReportName = NLM_VERSION + File.separator + nlmReportName;

			StringBuilder sb = new StringBuilder();
			sb.append(reportBaseNumber + ":\n");
			sb.append("\t phi:" + phiReportName + "\n");
			sb.append("\tdeid:" + deidReportName + "\n");
			sb.append("\t nlm:" + nlmReportName + "\n");
			System.out.println(sb.toString());

			String contentPhi = IOUtils.toString(ContentViewer.class.getResourceAsStream(phiReportName), "UTF-8");
			String contentDeid = IOUtils.toString(ContentViewer.class.getResourceAsStream(deidReportName), "UTF-8");
			String contentNlm = IOUtils.toString(ContentViewer.class.getResourceAsStream(nlmReportName), "UTF-8");

			totalBytesRead += contentPhi.getBytes().length;
			totalBytesRead += contentDeid.getBytes().length;
			totalBytesRead += contentNlm.getBytes().length;

			deidRedactor.setPhiContent(contentPhi);
			deidRedactor.setScrubbedContent(contentDeid);
			deidRedactor.execute();

			nlmRedactor.setPhiContent(contentPhi);
			nlmRedactor.setScrubbedContent(contentNlm);
			nlmRedactor.execute();

			for (Annotation annot : deidRedactor.getAnnotations()) {
				String annotKey = "report" + reportBaseNumber;
				annotKey = annotKey + ":" + StringUtils.leftPad(annot.getsPos() + "", 7);
				annotKey = annotKey + ":" + StringUtils.leftPad(annot.getePos() + "", 7);
				deidAnnotations.add(annotKey);
				deidAnnotationMap.put(annotKey, annot);
			}

			for (Annotation annot : nlmRedactor.getAnnotations()) {
				String annotKey = "report" + reportBaseNumber;
				annotKey = annotKey + ":" + StringUtils.leftPad(annot.getsPos() + "", 7);
				annotKey = annotKey + ":" + StringUtils.leftPad(annot.getePos() + "", 7);
				if (!annot.getAnnotationKind().equals("Undefined")) {
					nlmAnnotations.add(annotKey);
				}
			}
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("total bytes read: " + totalBytesRead + "\n");
		sb.append("Deid redactions number = " + deidAnnotations.size() + "\n");
		sb.append("Nlm redactions number = " + nlmAnnotations.size() + "\n");
		sb.append("True positives = " + tp + "\n");
		sb.append("False positives = " + fp + "\n");
		sb.append("False negatives = " + fn + "\n");
		sb.append("Precision = " + prec + "\n");
		sb.append("Recall = " + reca + "\n");

		final List<String> missesList = new ArrayList<String>();
		missesList.addAll(misses);
		Collections.sort(missesList);
		int missIdx = 0;
		while (missIdx < missesList.size()) {
			if (missIdx % 100 == 0) {
				String annotKey = missesList.get(missIdx);
				Annotation annot = deidAnnotationMap.get(annotKey);
				String annotAsString = StringUtils.substringAfter(annot.toString(), "[");
				sb.append("\tMissed " + missesList.get(missIdx) + " ==> " + annotAsString + "\n");
			}
			missIdx++;
		}

		return sb.toString();
	}

}
