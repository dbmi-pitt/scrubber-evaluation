package edu.pitt.dbmi.deid.comparison.viewer;

import java.awt.Color;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentViewerDeid extends ContentViewer {

	private static final long serialVersionUID = 1L;

	@Override
	public void constructFileNameFromReportName() {
		fileName = getRelativeDirectoryPath() + File.separator  + reportBaseName;	
	}

	@Override
	public void annotate() {
		String deIdPatternString = "[\\*]{2,}[A-Z][A-Z-]+[\\[][^\\]]+[\\]]|[\\*]{2,}PLACE";
		Pattern pattern = Pattern.compile(deIdPatternString, Pattern.UNICODE_CHARACTER_CLASS);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String underLyingText = matcher.group();
			int sPos = matcher.start();
//			int ePos = matcher.end();
//			System.out.println("DEID: (" + sPos + "," + ePos + ") = '" + underLyingText + "'");
			annotateText(sPos, underLyingText, Color.CYAN);
		}
		
	}

}
