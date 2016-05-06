package edu.pitt.dbmi.deid.comparison.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import edu.pitt.dbmi.deid.comparison.annotator.Annotation;
import edu.pitt.dbmi.deid.comparison.annotator.RedactorUtf8;

public class RedactorViewer extends JFrame implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private JPanel mainPanel = null;
	private int currentNoteNameIndex = -1;
	
	private int currentAnnotationIndex = -1;
	
	private int hilitedAnnotationPos = -1;
	private String hilitedUnderLyingText = "";
	
	private Color[] colorArray;

	private static void createAndShowGUI() {
		new RedactorViewer("Redactor Viewer");
	}

	private WindowAdapter windowAdapter;
	private JTable noteNameTable;
	private JScrollPane noteNameTableScrollPane;
	
	private JTable annotationTable;
	private JScrollPane annotationTableScrollPane;
	
	private String contentPhi = "None loaded";
	private String contentDeid = "None loaded";
	private String contentNlm = "None loaded";
	
	private JTextPane viewerPhi = new JTextPane();
	
	private JScrollPane scrollerPhi = new JScrollPane(viewerPhi);
	
	public RedactorViewer(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		establishWindowControls();
		buildNlmAnnotationAttributes();
		buildMainPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void buildMainPanel() {
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setPreferredSize(new Dimension(1500, 900));
		
		buildNoteNameScrollableTable();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;	
		gbc.weightx = 0.1;
		gbc.weighty = 1.0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = new Insets(0,0,0,0);
		
		mainPanel.add(noteNameTableScrollPane, gbc);
		
		viewerPhi = new JTextPane();
		viewerPhi.setText(contentPhi);
		
		scrollerPhi = new JScrollPane();
	
		scrollerPhi.getViewport().add(viewerPhi);
		
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = .6;
		
		gbc.ipadx = 10;
		gbc.ipady = 10;
		int insetAllDimensions = 10;
		gbc.insets = new Insets(insetAllDimensions,insetAllDimensions,
				insetAllDimensions,insetAllDimensions);
		
		mainPanel.add(scrollerPhi, gbc);
		
		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = .3;
		
		buildAnnotationScrollableTable();
		
		mainPanel.add(annotationTableScrollPane, gbc);
		
	}
	
	private void buildNoteNameScrollableTable() {
		String columnNames[] = { "Report Name" };
		String dataValues[][] = new String[1036][1];
		for (int rdx = 0; rdx < 1036; rdx++) {
			dataValues[rdx][0] = buildReportNameFromIndex(rdx);
		}
		noteNameTable = new JTable(dataValues, columnNames);
		noteNameTable.getSelectionModel().addListSelectionListener(this);
		noteNameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);    
		noteNameTableScrollPane = new JScrollPane(noteNameTable);
		noteNameTableScrollPane.setPreferredSize(new Dimension(75,900));	
	}
	
	private void buildAnnotationScrollableTable() {
		String columnNames[] = { "sPos", "ePos", "Redactor", "Kind",  "Spanned" };
		String dataValues[][] = new String[100][5];
		for (int rdx = 0; rdx < 100; rdx++) {
			for (int cdx = 0; cdx < 5; cdx++) {
				dataValues[rdx][cdx] = "";
			}
		}
		annotationTable = new JTable(dataValues, columnNames);
		annotationTable.getSelectionModel().addListSelectionListener(this);
		annotationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);    
		annotationTableScrollPane = new JScrollPane(annotationTable);
		annotationTableScrollPane.setPreferredSize(new Dimension(75,900));	
	}

	private String buildReportNameFromIndex(int rdx) {
		return "report" + StringUtils.leftPad(rdx + "", 5, "0") + ".txt";
	}


	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {	
		try {
			if (e.getSource().equals(noteNameTable.getSelectionModel())) {
				processNoteNameSelection(e);
			}
			else if (e.getSource().equals(annotationTable.getSelectionModel())) {
				processAnnotationSelection(e);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void processAnnotationSelection(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			;
		}
		else if (e.getFirstIndex() != currentAnnotationIndex) {
			processChangedAnnotation(e.getFirstIndex());
		}
		else if (e.getLastIndex() != currentAnnotationIndex) {
			processChangedAnnotation(e.getLastIndex());
		}
		
	}

	private void processChangedAnnotation(int annotationIndex) {
		System.out.println("Changing annotation to " + annotationIndex);
		currentAnnotationIndex = annotationIndex;
		Annotation annotation = new Annotation();
		
		annotation.setsPos((new Long((String)annotationTable.getModel().getValueAt(annotationIndex, 0))).longValue());
		annotation.setePos((new Long((String)annotationTable.getModel().getValueAt(annotationIndex, 1))).longValue());
		annotation.setRedactor((String) annotationTable.getModel().getValueAt(annotationIndex, 2));
		annotation.setAnnotationKind((String) annotationTable.getModel().getValueAt(annotationIndex, 3));
		annotation.setSpannedText((String) annotationTable.getModel().getValueAt(annotationIndex, 4));
		if (annotation.getAnnotationKind() != null && annotation.getAnnotationKind().length() > 0) {
			if (hilitedAnnotationPos >= 0 && hilitedUnderLyingText != null) {	
				boolean isBold = false;
				annotateText(viewerPhi,hilitedAnnotationPos,hilitedUnderLyingText,Color.WHITE,isBold);
			}
			int sPos = (int) annotation.getsPos();
			boolean isBold = true;
			Color hiliteColor = Color.MAGENTA;
			if (annotation.getRedactor().equals("NLM")) {
				hiliteColor = Color.CYAN;
			}
			annotateText(viewerPhi,sPos,annotation.getRedactedText(),hiliteColor,isBold);
			hilitedAnnotationPos = sPos;
			hilitedUnderLyingText = annotation.getRedactedText();
			viewerPhi.setCaretPosition(sPos);
		}
	}



	private void processNoteNameSelection(ListSelectionEvent e) throws IOException {
		if (e.getValueIsAdjusting()) {
			;
		}
		else if (e.getFirstIndex() != currentNoteNameIndex) {
			processChangedSelection(e.getFirstIndex());
		}
		else if (e.getLastIndex() != currentNoteNameIndex) {
			processChangedSelection(e.getLastIndex());
		}
	}

	private void processChangedSelection(int selectedIndex) throws IOException {
		System.out.println("Changing selection to " + selectedIndex);;
		
		currentNoteNameIndex = selectedIndex;
		String reportBaseName = buildReportNameFromIndex(currentNoteNameIndex);
		String fileNamePhi = "cartoon/" + reportBaseName;
		String fileNameDeid = "deid/" + reportBaseName;
		String fileNameNlm = "nlm160405/" + reportBaseName.replaceAll("\\.txt", ".nphi.txt");
		
		contentPhi = IOUtils.toString(getClass().getResourceAsStream(fileNamePhi), "UTF-8");
		contentDeid = IOUtils.toString(getClass().getResourceAsStream(fileNameDeid), "UTF-8");
		contentNlm = IOUtils.toString(getClass().getResourceAsStream(fileNameNlm), "UTF-8");
		
		annotationTable.getSelectionModel().clearSelection();
		hilitedAnnotationPos = -1;
		hilitedUnderLyingText = null;
		
		RedactorUtf8 redactorNlm = new RedactorUtf8();
		redactorNlm.addScrubPattern("[\\[][A-Z]+[\\]]");
		redactorNlm.setScrubControls(Pattern.MULTILINE | Pattern.DOTALL);
		redactorNlm.setPhiContent(contentPhi);
		redactorNlm.setScrubbedContent(contentNlm);
		redactorNlm.execute();
		
		RedactorUtf8 redactorDeid = new RedactorUtf8();
		redactorDeid.addScrubPattern("[\\*]{2,}[A-Z][A-Z-]+[\\[][^\\]]+[\\]]|[\\*]{2,}PLACE");
		redactorDeid.setScrubControls(Pattern.MULTILINE | Pattern.DOTALL);
		redactorDeid.setPhiContent(contentPhi);
		redactorDeid.setScrubbedContent(contentDeid);
		redactorDeid.execute();
		
		RedactedContentMerger merger = new RedactedContentMerger();
		merger.setContentOne(redactorNlm.getRedactedPhi());
		merger.setContentTwo(redactorDeid.getRedactedPhi());
		merger.merge();
		colorArray = merger.getColorArray();
		contentPhi = merger.getContentMerged();
		
		final TreeSet<Annotation> sortedAnnotations = new TreeSet<Annotation>(new Comparator<Annotation>(){
			@Override
			public int compare(Annotation annotOne, Annotation annotTwo) {
				long result = annotOne.getsPos() - annotTwo.getsPos();
				result = (result == 0L) ? annotOne.getePos() - annotTwo.getePos() : result;
				result = (result == 0L) ? annotOne.getRedactor().compareTo(annotTwo.getRedactor()) : result;
				return (int) result;
			}});
		for (Annotation annotation : redactorNlm.getAnnotations()) {
			annotation.setRedactor("NLM");
			sortedAnnotations.add(annotation);
		}
		for (Annotation annotation : redactorDeid.getAnnotations()) {
			annotation.setRedactor("DeId");
			sortedAnnotations.add(annotation);
		}
		
		int rdx = 0;
		for (Annotation annotation : sortedAnnotations) {
			annotationTable.getModel().setValueAt(annotation.getsPos()+"", rdx, 0);
			annotationTable.getModel().setValueAt(annotation.getePos()+"", rdx, 1);
			annotationTable.getModel().setValueAt(annotation.getRedactor(), rdx, 2);
			annotationTable.getModel().setValueAt(annotation.getAnnotationKind(), rdx, 3);	
			annotationTable.getModel().setValueAt(annotation.getSpannedText(), rdx, 4);
			rdx++;
		}
		for (int rdx2 = rdx; rdx2 < 100; rdx2++) {
			for (int cdx = 0; cdx < 5; cdx++) {
				annotationTable.getModel().setValueAt("", rdx2, cdx);
			}
		}
	
		scrollerPhi.getViewport().remove(viewerPhi);	
		viewerPhi = new JTextPane();
		viewerPhi.setText(contentPhi);
	
		scrollerPhi.getViewport().add(viewerPhi);
		viewerPhi.setCaretPosition(0);
		
		mainPanel.repaint();
	}
	
	private void buildNlmAnnotationAttributes() {
		SimpleAttributeSet nlmAnnotationAttributes = new SimpleAttributeSet();
		StyleConstants.setForeground(nlmAnnotationAttributes, Color.BLACK);
		StyleConstants.setBackground(nlmAnnotationAttributes, Color.MAGENTA);
		StyleConstants.setBold(nlmAnnotationAttributes, true);
	}
	
	public void annotateText(JTextPane textPane, int sPos, String underLyingText,
			Color annotationColor, boolean isBold) {
		try {
			System.out.println("Styling with " + sPos + ", " + underLyingText + ", " + annotationColor + ", " + isBold);
			
			SimpleAttributeSet annotationAttributes = new SimpleAttributeSet();
			StyleConstants.setForeground(annotationAttributes, Color.BLACK);
			StyleConstants.setBackground(annotationAttributes, annotationColor);
			StyleConstants.setBold(annotationAttributes, isBold);
		
			StyledDocument doc = textPane.getStyledDocument();
			doc.remove(sPos, underLyingText.length());
			doc.insertString(sPos, underLyingText, annotationAttributes);
			
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	private void annotateText(JTextPane textPane, int sPos, String underLyingText, Color[] colorArray,
			boolean isBold) {
		try {
			for (int idx = 0; idx < underLyingText.length(); idx++) {
				SimpleAttributeSet annotationAttributes = new SimpleAttributeSet();
				StyleConstants.setForeground(annotationAttributes, Color.BLACK);
				StyleConstants.setBackground(annotationAttributes, colorArray[sPos + idx]);
				StyleConstants.setBold(annotationAttributes, isBold);
				StyledDocument doc = textPane.getStyledDocument();
				doc.remove(sPos + idx, 1);
				doc.insertString(sPos + idx, underLyingText.substring(sPos + idx, sPos + idx + 1), annotationAttributes);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
	}

	private void establishWindowControls() {
		windowAdapter = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				// You can still stop closing if you want to
				int res = JOptionPane.showConfirmDialog(RedactorViewer.this, "Are you sure you want to close?",
						"Close?", JOptionPane.YES_NO_OPTION);
				if (res == 0) {
					// dispose method issues the WINDOW_CLOSED event
					RedactorViewer.this.dispose();
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				System.exit(0);
			}
		};

		addWindowListener(windowAdapter);
	}

}
