package edu.pitt.dbmi.deid.comparison.viewer;

import java.awt.Color;
import java.io.File;

import edu.pitt.dbmi.deid.comparison.annotator.Annotation;
import edu.pitt.dbmi.deid.comparison.annotator.CartoonAnnotator;

public class ContentViewerPhi extends ContentViewer {

	private static final long serialVersionUID = 1L;

	@Override
	public void constructFileNameFromReportName() {
		fileName = getRelativeDirectoryPath() + File.separator + reportBaseName;
	}

	@Override
	public void annotate() {
		CartoonAnnotator cartoonAnnotator = new CartoonAnnotator();
		cartoonAnnotator.setReportBaseName(getReportBaseName());
		cartoonAnnotator.annotate(content);
		for (Annotation cartoonAnnotation : cartoonAnnotator.getAnnotations()) {
			int sPos = (new Integer(cartoonAnnotation.getsPos()+"")).intValue();
			String underLyingText = cartoonAnnotation.getSpannedText();
			if (underLyingText.length()>20) {
				System.out.println("Why so long!");
			}
			annotateText(sPos,underLyingText,Color.LIGHT_GRAY);
		}	
	}

}
