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
import java.util.regex.Matcher;
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
import edu.pitt.dbmi.deid.comparison.annotator.CartoonAnnotator;

public class DeidComparisonViewer extends JFrame implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private JPanel mainPanel = null;
	private int currentIndex = -1;

	private static void createAndShowGUI() {
		new DeidComparisonViewer("Deid Comparison Viewer");
	}

	private WindowAdapter windowAdapter;
	private JTable table;
	private JScrollPane tableScrollPane;
	
	private String contentPhi = "None loaded";
	private String contentDeid = "None loaded";
	private String contentNlm = "None loaded";
	
	private JTextPane viewerPhi = new JTextPane();
	private JTextPane viewerDeid = new JTextPane();
	private JTextPane viewerNlm = new JTextPane();
	
	private JScrollPane scrollerPhi = new JScrollPane(viewerPhi);
	private JScrollPane scrollerDeid = new JScrollPane(viewerDeid);
	private JScrollPane scrollerNlm = new JScrollPane(viewerNlm);
	
	public DeidComparisonViewer(String title) {
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
		
		buildScrollableTable();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;	
		gbc.weightx = 0.066;
		gbc.weighty = 1.0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = new Insets(0,0,0,0);
		
		mainPanel.add(tableScrollPane, gbc);
		
		viewerPhi = new JTextPane();
		viewerDeid = new JTextPane();
		viewerNlm = new JTextPane();
		
		viewerPhi.setText(contentPhi);
		viewerDeid.setText(contentDeid);
		viewerNlm.setText(contentNlm);
		
		scrollerPhi = new JScrollPane();
		scrollerDeid = new JScrollPane();
		scrollerNlm = new JScrollPane();
		
		scrollerPhi.getViewport().add(viewerPhi);
		scrollerDeid.getViewport().add(viewerDeid);
		scrollerNlm.getViewport().add(viewerNlm);
		
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = .31;
		
		gbc.ipadx = 10;
		gbc.ipady = 10;
		int insetAllDimensions = 10;
		gbc.insets = new Insets(insetAllDimensions,insetAllDimensions,
				insetAllDimensions,insetAllDimensions);
		
		mainPanel.add(scrollerPhi, gbc);
		
		gbc.gridx = 2;
		mainPanel.add(scrollerDeid, gbc);
		
		gbc.gridx = 3;	
		mainPanel.add(scrollerNlm, gbc);
		
	}
	
	@SuppressWarnings("unused")
	private GridBagConstraints copyGridBagConstraints(GridBagConstraints srcGbc) {
		GridBagConstraints tgtGbc = new GridBagConstraints();
		tgtGbc.anchor = srcGbc.anchor;
		tgtGbc.fill = srcGbc.fill;
		tgtGbc.gridheight = srcGbc.gridheight;
		tgtGbc.gridwidth = srcGbc.gridwidth;
		tgtGbc.gridx = srcGbc.gridx;
		tgtGbc.gridy = srcGbc.gridy;
		tgtGbc.ipadx = srcGbc.ipadx;
		tgtGbc.ipady = srcGbc.ipady;
		tgtGbc.weightx = srcGbc.weightx;
		tgtGbc.weighty = srcGbc.weighty;
		Insets srcInsets = srcGbc.insets;
		Insets tgtInsets = new Insets(srcInsets.top,
				srcInsets.left, srcInsets.bottom, srcInsets.right);	
		tgtGbc.insets = tgtInsets;
		return tgtGbc;
	}

	private void buildScrollableTable() {
		String columnNames[] = { "Report Name" };
		String dataValues[][] = new String[1036][1];
		for (int rdx = 0; rdx < 1036; rdx++) {
			dataValues[rdx][0] = buildReportNameFromIndex(rdx);
		}
		table = new JTable(dataValues, columnNames);
		table.getSelectionModel().addListSelectionListener(this);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);    
		tableScrollPane = new JScrollPane(table);
		tableScrollPane.setPreferredSize(new Dimension(75,900));	
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
			
			if (e.getValueIsAdjusting()) {
				;
			}
			else if (e.getFirstIndex() != currentIndex) {
				processChangedSelection(e.getFirstIndex());
			}
			else if (e.getLastIndex() != currentIndex) {
				processChangedSelection(e.getLastIndex());
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void processChangedSelection(int selectedIndex) throws IOException {
		System.out.println("Changing selection to " + selectedIndex);;
		
		currentIndex = selectedIndex;
		String reportBaseName = buildReportNameFromIndex(currentIndex);
		String fileNamePhi = "cartoon/" + reportBaseName;
		String fileNameDeid = "deid/" + reportBaseName;
		String fileNameNlm = "nlm160405/" + reportBaseName.replaceAll("\\.txt", ".nphi.txt");
		
		contentPhi = IOUtils.toString(getClass().getResourceAsStream(fileNamePhi), "UTF-8");
		contentDeid = IOUtils.toString(getClass().getResourceAsStream(fileNameDeid), "UTF-8");
		contentNlm = IOUtils.toString(getClass().getResourceAsStream(fileNameNlm), "UTF-8");
	
		scrollerPhi.getViewport().remove(viewerPhi);
		scrollerDeid.getViewport().remove(viewerDeid);
		scrollerNlm.getViewport().remove(viewerNlm);
		
		viewerPhi = new JTextPane();
		viewerDeid = new JTextPane();
		viewerNlm = new JTextPane();
		
		viewerPhi.setText(contentPhi);
		viewerDeid.setText(contentDeid);
		viewerNlm.setText(contentNlm);
		
		annotatePhi(contentPhi);
		annotateDeId(contentDeid);
		annotateNlm(contentNlm);
	
		scrollerPhi.getViewport().add(viewerPhi);
		scrollerDeid.getViewport().add(viewerDeid);
		scrollerNlm.getViewport().add(viewerNlm);
		
		viewerPhi.setCaretPosition(0);
		viewerDeid.setCaretPosition(0);
		viewerNlm.setCaretPosition(0);
		
		mainPanel.repaint();
	}
	
	public void annotatePhi(String contentPhi) {
		CartoonAnnotator cartoonAnnotator = new CartoonAnnotator();
		cartoonAnnotator.annotate(contentPhi);
		for (Annotation cartoonAnnotation : cartoonAnnotator.getAnnotations()) {
			int sPos = (new Integer(cartoonAnnotation.getsPos()+"")).intValue();
			String underLyingText = cartoonAnnotation.getSpannedText();
			if (underLyingText.length()>20) {
				System.out.println("Why so long!");
			}
			annotateText(viewerPhi,sPos,underLyingText,Color.LIGHT_GRAY);
		}
	}
	
	public void annotateDeId(String content) {
		String deIdPatternString = "[\\*]{2,}[A-Z]+[\\[][^\\]]+[\\]]";
		Pattern pattern = Pattern.compile(deIdPatternString, Pattern.UNICODE_CHARACTER_CLASS);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String underLyingText = matcher.group();
			int sPos = matcher.start();
//			int ePos = matcher.end();
//			System.out.println("DEID: (" + sPos + "," + ePos + ") = '" + underLyingText + "'");
			annotateText(viewerDeid, sPos, underLyingText, Color.CYAN);
		}
	}
	
	public void annotateNlm(String content) {
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
			annotateText(viewerNlm, sPos, underLyingText, Color.MAGENTA);
		}
	}
	
	private void buildNlmAnnotationAttributes() {
		SimpleAttributeSet nlmAnnotationAttributes = new SimpleAttributeSet();
		StyleConstants.setForeground(nlmAnnotationAttributes, Color.BLACK);
		StyleConstants.setBackground(nlmAnnotationAttributes, Color.MAGENTA);
		StyleConstants.setBold(nlmAnnotationAttributes, true);
	}
	
	public void annotateText(JTextPane textPane, int sPos, String underLyingText,
			Color annotationColor) {
		try {
			SimpleAttributeSet annotationAttributes = new SimpleAttributeSet();
			StyleConstants.setForeground(annotationAttributes, Color.BLACK);
			StyleConstants.setBackground(annotationAttributes, annotationColor);
			StyleConstants.setBold(annotationAttributes, true);
		
			StyledDocument doc = textPane.getStyledDocument();
			doc.remove(sPos, underLyingText.length());
			doc.insertString(sPos, underLyingText, annotationAttributes);
			
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
				int res = JOptionPane.showConfirmDialog(DeidComparisonViewer.this, "Are you sure you want to close?",
						"Close?", JOptionPane.YES_NO_OPTION);
				if (res == 0) {
					// dispose method issues the WINDOW_CLOSED event
					DeidComparisonViewer.this.dispose();
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
