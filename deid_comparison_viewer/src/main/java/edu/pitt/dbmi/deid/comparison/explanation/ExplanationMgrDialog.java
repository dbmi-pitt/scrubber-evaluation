package edu.pitt.dbmi.deid.comparison.explanation;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.pitt.dbmi.deid.comparison.annotator.Annotation;
import edu.pitt.dbmi.deid.comparison.viewer.ExplanationData;

public class ExplanationMgrDialog extends JDialog implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;
	
	private JPanel contextPanel = new JPanel();
	private JPanel explanationPanel = new JPanel();
	private HomogenousButtonPanel mainActionPanel = new HomogenousButtonPanel();
	private JTextPane contextTextPane = new JTextPane();

	private JTextField explanationHeaderTextField = new JTextField();

	private DefaultListModel<Explanation> explanationListModel;
	private JList<Explanation> explanationList;
	private JTextPane explanationContentTextPane = new JTextPane();
	private HomogenousButtonPanel explanationActionPanel = new HomogenousButtonPanel();
	
	private JLabel reportNameLabel = new JLabel("report:");
	private JTextField reportNameField = new JTextField();
	private JLabel sPosLabel = new JLabel("sPos:");
	private JTextField sPosTextField = new JTextField();
	private JLabel ePosLabel = new JLabel("ePos:");
	private JTextField ePosTextField = new JTextField();

	private JButton saveButton = new JButton("Save");
	private JButton clearButton = new JButton("Clear");
	private JButton deleteButton = new JButton("Delete");
	private JButton selectButton = new JButton("Select");
	private JButton cancelButton = new JButton("Cancel");

	private int selectedIdx = 0;
	private Explanation currentExplanation = new Explanation("TBD", "TBD");
	private ExplanationData explanationData = null;

	private Annotation currentAnnotation;

	public ExplanationMgrDialog(Frame frame, String labelText, boolean isModel, ExplanationData explanationData) {
		super(frame, labelText, isModel);

		this.explanationData = explanationData;
		explanationListModel = new DefaultListModel<Explanation>();
		for (Explanation explanation : explanationData.asExplanationArray()) {
			explanationListModel.addElement(explanation);
		}
		explanationList = new JList<Explanation>(explanationListModel);
		setLayout(new BorderLayout());

		/*
		 * Context
		 */
		contextPanel.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Context"));
		contextPanel.setLayout(new GridBagLayout());

		GridBagConstraints contextGbc = new GridBagConstraints();
		contextGbc.anchor = GridBagConstraints.NORTHWEST;
		contextGbc.fill = GridBagConstraints.NONE;
		contextGbc.weightx = 0.0;
		contextGbc.weighty = 0.0;
		contextGbc.gridheight = 1;
		contextGbc.gridwidth = 1;
		contextGbc.gridx = 0;
		contextGbc.gridy = 0;
		contextGbc.insets = new Insets(5, 5, 5, 5);
		contextPanel.add(reportNameLabel, contextGbc);
		
		contextGbc.gridx = 1;
		contextGbc.weightx = 1.0;
		contextGbc.fill = GridBagConstraints.HORIZONTAL;
		contextPanel.add(reportNameField, contextGbc);
		
		contextGbc.gridx = 0;
		contextGbc.gridy = 1;
		contextGbc.fill = GridBagConstraints.NONE;
		contextGbc.weightx = 0.0;
		contextPanel.add(sPosLabel, contextGbc);
		
		contextGbc.gridx = 1;
		contextGbc.weightx = 1.0;
		contextGbc.fill = GridBagConstraints.HORIZONTAL;
		contextPanel.add(sPosTextField, contextGbc);
		
		contextGbc.gridx = 0;
		contextGbc.gridy = 2;
		contextGbc.fill = GridBagConstraints.NONE;
		contextGbc.weightx = 0.0;
		contextPanel.add(ePosLabel, contextGbc);
		
		contextGbc.gridx = 1;
		contextGbc.weightx = 1.0;
		contextGbc.fill = GridBagConstraints.HORIZONTAL;
		contextPanel.add(ePosTextField, contextGbc);
		
		contextGbc.anchor = GridBagConstraints.NORTHWEST;
		contextGbc.fill = GridBagConstraints.BOTH;
		contextGbc.weightx = 1.0;
		contextGbc.weighty = 1.0;
		contextGbc.gridheight = 1;
		contextGbc.gridwidth = 2;
		contextGbc.gridx = 0;
		contextGbc.gridy = 3;
		contextGbc.insets = new Insets(5, 5, 5, 5);
		contextPanel.add(contextTextPane, contextGbc);
		
		contextGbc.fill = GridBagConstraints.HORIZONTAL;
		contextGbc.weighty = 0.0;
		contextGbc.gridy = 4;
		contextPanel.add(mainActionPanel, contextGbc);

		/*
		 * Explanation
		 */
		explanationPanel.setBorder(BorderFactory
				.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Explanation"));

		layoutExplanationPanel();

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contextPanel, explanationPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(350);

		add(splitPane, BorderLayout.CENTER);
//		add(mainActionPanel, BorderLayout.PAGE_END);
	}

	private void layoutExplanationPanel() {
		
		explanationPanel.setLayout(new GridBagLayout());
		GridBagConstraints explanationGbc = new GridBagConstraints();
		explanationGbc.anchor = GridBagConstraints.WEST;
		explanationGbc.fill = GridBagConstraints.HORIZONTAL;
		explanationGbc.gridx = 0;
		explanationGbc.gridy = 0;
		explanationGbc.gridheight = 1;
		explanationGbc.gridwidth = 1;
		explanationGbc.weightx = 0.5;
		explanationGbc.weighty = 0.0;
		explanationGbc.ipadx = 0;
		explanationGbc.ipady = 0;
		explanationGbc.insets = new Insets(5, 5, 5, 5);
		explanationPanel.add(explanationHeaderTextField, explanationGbc);

		explanationGbc.gridy = 1;
		explanationGbc.weighty = 1.0;
		explanationGbc.fill = GridBagConstraints.BOTH;
		explanationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		explanationList.setLayoutOrientation(JList.VERTICAL);
		explanationList.setSelectedIndex(0);
		explanationList.addListSelectionListener(this);
		explanationList.setVisibleRowCount(5);
		JScrollPane listScrollPane = new JScrollPane(explanationList);
		explanationPanel.add(listScrollPane, explanationGbc);

		explanationGbc.gridx = 1;
		explanationGbc.gridy = 0;
		explanationGbc.gridheight = 2;
		JScrollPane contentScrollPane = new JScrollPane(explanationContentTextPane);
		explanationPanel.add(contentScrollPane, explanationGbc);

		explanationGbc.gridx = 0;
		explanationGbc.gridy = 2;
		explanationGbc.gridwidth = 2;
		explanationGbc.gridheight = 1;
		explanationGbc.weightx = 1.0;
		explanationGbc.weighty = 0.0;
		explanationGbc.fill = GridBagConstraints.HORIZONTAL;
		explanationPanel.add(explanationActionPanel, explanationGbc);

		/*
		 * Explanation actions
		 */
		saveButton.addActionListener(this);
		clearButton.addActionListener(this);
		deleteButton.addActionListener(this);
		explanationActionPanel.addButton(saveButton);
		explanationActionPanel.addButton(clearButton);
		explanationActionPanel.addButton(deleteButton);
		explanationActionPanel.layoutButtons();

		/*
		 * Main actions
		 */
		selectButton.addActionListener(this);
		cancelButton.addActionListener(this);
		mainActionPanel.addButton(selectButton);
		mainActionPanel.addButton(cancelButton);
		mainActionPanel.layoutButtons();

	}

	@Override
	public void valueChanged(ListSelectionEvent selectEvent) {
		if (!selectEvent.getValueIsAdjusting()) {
			System.out.println("Called valueChanged");
			int firstIndex = selectEvent.getFirstIndex();
			int lastIndex = selectEvent.getLastIndex();
			if (firstIndex != selectedIdx) {
				selectedIdx = firstIndex;
			} else if (lastIndex != selectedIdx) {
				selectedIdx = lastIndex;
			}
			currentExplanation = explanationList.getModel().getElementAt(selectedIdx);
			if (currentExplanation != null) {
				explanationHeaderTextField.setText(currentExplanation.getHeader());
				explanationContentTextPane.setText(urlDecode(currentExplanation.getContent()));
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("Save".equals(e.getActionCommand())) {
			processSave();
		} else if ("Clear".equals(e.getActionCommand())) {
			processClear();
		} else if ("Delete".equals(e.getActionCommand())) {
			processDelete();
		} else if ("Select".equals(e.getActionCommand())) {
			processSelect();
		} else if ("Cancel".equals(e.getActionCommand())) {
			setVisible(false);
		}

	}

	private void processSelect() {
		Explanation selectedExplanation = explanationList.getSelectedValue();
		if (selectedExplanation != null) {
			currentAnnotation.setExplanation(selectedExplanation);
		}
		setVisible(false);
	}

	private void processClear() {
		explanationList.removeListSelectionListener(this);
		int size = explanationListModel.getSize();
		if (size > 0) {
			explanationHeaderTextField.setText("");
			explanationContentTextPane.setText("");
			explanationList.clearSelection();
		}
		explanationList.addListSelectionListener(this);
		selectedIdx = -1;
	}

	private void processSave() {
//		explanationList.removeListSelectionListener(this);
		int index = explanationList.getSelectedIndex(); // get selected index
		if (index == -1) { // no selection, so insert at beginning
			processInsert();
		} else { // add after the selected item
			processUpdate();
		}
//		explanationList.addListSelectionListener(this);
	}

	private void processUpdate() {
		int index = explanationList.getSelectedIndex();
		Explanation explanationToUpdate = explanationListModel.getElementAt(index);
		if (explanationToUpdate.getHeader().equals(explanationHeaderTextField.getText())) {
			explanationToUpdate.setContent(urlEncode(explanationContentTextPane.getText()));
		}
	}

	private String urlEncode(String text) {
		String result = null;
		try {
			result = URLEncoder.encode(text, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			result = "URL Encoding failed";
		}
		return result;
	}
	
	private String urlDecode(String text) {
		String result = null;
		try {
			result = URLDecoder.decode(text, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			result = "URL Encoding failed";
		}
		return result;
	}

	private void processInsert() {
		Explanation explanationToInsert = new Explanation(explanationHeaderTextField.getText(), urlEncode(explanationContentTextPane.getText()));
		if (isValidHeader(explanationToInsert) && checkUnique(explanationToInsert)) {
			explanationListModel.insertElementAt(explanationToInsert, 0);
			explanationData.getExplanationMap().put(explanationToInsert.getHeader(), explanationToInsert);

			// Reset the text field.
			explanationHeaderTextField.requestFocusInWindow();
			explanationHeaderTextField.setText("");
			explanationContentTextPane.setText("");

			// Select the new item and make it visible.
			explanationList.setSelectedIndex(0);
			explanationList.ensureIndexIsVisible(0);
		}
		
	}
	
	private boolean isValidHeader(Explanation insertionCandidate) {
		return insertionCandidate.getHeader().length() > 0;
	}

	private void processDelete() {
		explanationList.removeListSelectionListener(this);
		int index = explanationList.getSelectedIndex();
		explanationListModel.remove(index);

		int size = explanationListModel.getSize();

		if (size == 0) { // Nobody's left, disable firing.
			deleteButton.setEnabled(false);
			explanationHeaderTextField.setText("");
			explanationContentTextPane.setText("");
		} else { // Select an index.
			if (index == explanationListModel.getSize()) {
				// removed item in last position
				index--;
			}

			explanationList.setSelectedIndex(index);
			explanationList.ensureIndexIsVisible(index);
		}
		explanationList.addListSelectionListener(this);
	}

	private boolean checkUnique(Explanation insertionCandidate) {
		boolean isUnique = true;
		ListModel<Explanation> model = explanationList.getModel();
		for (int idx = 0; idx < model.getSize(); idx++) {
			Explanation existingExplanation = explanationList.getModel().getElementAt(idx);
			if (existingExplanation.getHeader().equals(insertionCandidate.getHeader())) {
				isUnique = false;
				break;
			}
		}
		return isUnique;
	}
	
	public void setCurrentAnnotation(Annotation annotation) {
		currentAnnotation = annotation;
		reportNameField.setText(currentAnnotation.getReport());
		sPosTextField.setText(currentAnnotation.getsPos()+"");
		ePosTextField.setText(currentAnnotation.getePos()+"");
		contextTextPane.setText(currentAnnotation.getSpannedText());
		int explanationIndex = 0;
		if (currentAnnotation.getExplanation() != null) {
			while (explanationIndex < explanationListModel.size()) {
				Explanation explanation = explanationListModel.getElementAt(explanationIndex);
				if (explanation.getHeader().equals(currentAnnotation.getExplanation().getHeader())) {
					break;
				}
				else {
					explanationIndex++;
				}
			}	
			explanationList.setSelectedIndex(explanationIndex);
		}
		else {
			explanationList.clearSelection();
			explanationHeaderTextField.setText("");
			explanationContentTextPane.setText("");
		}
	}

}