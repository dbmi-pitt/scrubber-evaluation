package edu.pitt.dbmi.deid.comparison.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;

public class ContentComparisonViewer extends JFrame implements ActionListener, ListSelectionListener {

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
		new ContentComparisonViewer("Deid Comparison Viewer");
	}

	private WindowAdapter windowAdapter;
	private JTable table;
	private JScrollPane tableScrollPane;
	
	private int numberOfViewers = 3;
	private ContentViewer[] viewers = new ContentViewer[numberOfViewers];
	
	public ContentComparisonViewer(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		establishWindowControls();
//		buildNlmAnnotationAttributes();
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
		
//		viewers[0] = new ContentViewerPhi();
//		viewers[0].setRelativeDirectoryPath("cartoon");
//		viewers[1] = new ContentViewerDeid();
//		viewers[1].setRelativeDirectoryPath("deid");
//		viewers[2] = new ContentViewerNlm();
//		viewers[2].setRelativeDirectoryPath("nlm160218");
		
		viewers[0] = new ContentViewerPhi();
		viewers[0].setRelativeDirectoryPath("cartoon");
		viewers[1] = new ContentViewerDeid();
		viewers[1].setRelativeDirectoryPath("deid");
		viewers[2] = new ContentViewerNlm();
		viewers[2].setRelativeDirectoryPath("nlm160405");
		
//		viewers[0] = new ContentViewerDeid();
//		viewers[0].setRelativeDirectoryPath("deid");
//		viewers[1] = new ContentViewerNlm();
//		viewers[1].setRelativeDirectoryPath("nlm160405");
//		viewers[2] = new ContentViewerNlm();
//		viewers[2].setRelativeDirectoryPath("nlm160218");
		
		
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = .31;
		
		gbc.ipadx = 10;
		gbc.ipady = 10;
		int insetAllDimensions = 10;
		gbc.insets = new Insets(insetAllDimensions,insetAllDimensions,
				insetAllDimensions,insetAllDimensions);
		
		for (int idx = 0; idx < viewers.length; idx++) {
			gbc.gridx = idx + 1;
			mainPanel.add(viewers[idx], gbc);
		}
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
		
		for (ContentViewer viewer : viewers) {
			viewer.setReportBaseName(reportBaseName);
			viewer.refreshSelection();
			
		}
		
		
		
		mainPanel.repaint();
	}
	
	
//	private void buildNlmAnnotationAttributes() {
//		SimpleAttributeSet nlmAnnotationAttributes = new SimpleAttributeSet();
//		StyleConstants.setForeground(nlmAnnotationAttributes, Color.BLACK);
//		StyleConstants.setBackground(nlmAnnotationAttributes, Color.MAGENTA);
//		StyleConstants.setBold(nlmAnnotationAttributes, true);
//	}
	
	

	private void establishWindowControls() {
		windowAdapter = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				// You can still stop closing if you want to
				int res = JOptionPane.showConfirmDialog(ContentComparisonViewer.this, "Are you sure you want to close?",
						"Close?", JOptionPane.YES_NO_OPTION);
				if (res == 0) {
					// dispose method issues the WINDOW_CLOSED event
					ContentComparisonViewer.this.dispose();
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
