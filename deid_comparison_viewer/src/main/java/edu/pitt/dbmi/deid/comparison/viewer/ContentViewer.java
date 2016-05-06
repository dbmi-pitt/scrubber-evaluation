package edu.pitt.dbmi.deid.comparison.viewer;

import java.awt.Color;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.commons.io.IOUtils;

public abstract class ContentViewer extends JScrollPane {

	private static final long serialVersionUID = 1L;
	
	protected String content = "None loaded";
	protected JTextPane viewer = new JTextPane();
	protected JScrollPane scroller = new JScrollPane(viewer);
	protected String relativeDirectoryPath;
	protected String fileName;
	protected String reportBaseName;
	
	public ContentViewer() {
		getViewport().add(viewer);
	}
	
	protected void readContentFromFileName() {
		try {
			content = IOUtils.toString(getClass().getResourceAsStream(fileName), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void refreshSelection() {
		constructFileNameFromReportName();
		readContentFromFileName();
		getViewport().remove(viewer);
		viewer = new JTextPane();
		viewer.setText(content);
		viewer.setText(content);
		annotate();
		getViewport().add(viewer);
		viewer.setCaretPosition(0);
	}
	
	public abstract void constructFileNameFromReportName();

	public abstract void annotate();
	
	public void annotateText(int sPos, String underLyingText,
			Color annotationColor) {
		try {
			SimpleAttributeSet annotationAttributes = new SimpleAttributeSet();
			StyleConstants.setForeground(annotationAttributes, Color.BLACK);
			StyleConstants.setBackground(annotationAttributes, annotationColor);
			StyleConstants.setBold(annotationAttributes, true);
		
			StyledDocument doc = viewer.getStyledDocument();
			doc.remove(sPos, underLyingText.length());
			doc.insertString(sPos, underLyingText, annotationAttributes);
			
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}	
	public String getRelativeDirectoryPath() {
		return relativeDirectoryPath;
	}

	public void setRelativeDirectoryPath(String relativeDirectoryPath) {
		this.relativeDirectoryPath = relativeDirectoryPath;
	}

	public String getContent() {
		return content;
	}
	public JTextPane getViewer() {
		return viewer;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setViewer(JTextPane viewer) {
		this.viewer = viewer;
	}
	public String getReportBaseName() {
		return reportBaseName;
	}
	public void setReportBaseName(String reportBaseName) {
		this.reportBaseName = reportBaseName;
	}
}
