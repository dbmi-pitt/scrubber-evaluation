package edu.pitt.dbmi.deid.comparison.viewer;

import java.awt.Color;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentViewerNlm extends ContentViewer {

	private static final long serialVersionUID = 1L;

	@Override
	public void constructFileNameFromReportName() {
		fileName = getRelativeDirectoryPath() + File.separator + reportBaseName.replaceAll("\\.txt", ".nphi.txt");
	}

	@Override
	public void annotate() {
		String nlnPattern = "[\\[][A-Z]+[\\]]";
		int patternControls = 0;
		patternControls |= Pattern.DOTALL;
		patternControls |= Pattern.MULTILINE;
		patternControls |= Pattern.UNIX_LINES;
		Pattern pattern = Pattern.compile(nlnPattern,  patternControls);
		Matcher matcher = pattern.matcher(content);		
		while (matcher.find()) {
			String underLyingText = matcher.group();
			int sPos = matcher.start();
			int ePos = matcher.end();
			int calculatedEPos = sPos + underLyingText.length();
			if (ePos != calculatedEPos) {
				System.out.println("Blown assertion " + ePos + " is not " + calculatedEPos);
			}
//			System.out.println("NLM: (" + sPos + "," + ePos + ") = " + underLyingText + "");
			annotateText(sPos, underLyingText, Color.MAGENTA);
		}
	}

}
