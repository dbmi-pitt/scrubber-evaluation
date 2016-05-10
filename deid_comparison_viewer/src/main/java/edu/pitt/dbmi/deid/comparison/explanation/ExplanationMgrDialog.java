package edu.pitt.dbmi.deid.comparison.explanation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ExplanationMgrDialog extends JDialog implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;
	private static ExplanationMgrDialog dialog;
	private static Explanation value = null;

	private JPanel mainPanel = new JPanel();

	private JPanel contextPanel = new JPanel();
	private JPanel explanationPanel = new JPanel();
	private HomogenousButtonPanel mainActionPanel = new HomogenousButtonPanel();
	private JTextPane contextTextPane = new JTextPane();

	private JTextField explanationHeader = new JTextField();
	private JList<Explanation> explanationPicker;
	private JTextPane explanationContent = new JTextPane();
	private HomogenousButtonPanel explanationActionPanel = new HomogenousButtonPanel();

	private JButton saveButton = new JButton("Save");
	private JButton clearButton = new JButton("Clear");
	private JButton deleteButton = new JButton("Delete");
	private JButton selectButton = new JButton("Select");
	private JButton cancelButton = new JButton("Cancel");
	
	private int selectedIdx = 0;
	
	private Explanation currentExplanation = new Explanation("TBD", "TBD");
	
	private String labelText;
	private Explanation initialValue;
	private Explanation longValue;

	/**
	 * Set up and show the dialog. The first Component argument determines which
	 * frame the dialog depends on; it should be a component in the dialog's
	 * controlling frame. The second Component argument should be null if you
	 * want the dialog to come up with its left corner in the center of the
	 * screen; otherwise, it should be the component on top of which the dialog
	 * should appear.
	 */
	public static Explanation showDialog(Component frameComp, Component locationComp, String labelText, String title,
			Explanation[] possibleValues, Explanation initialValue, Explanation longValue) {
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		dialog = new ExplanationMgrDialog(frame, locationComp, labelText, title, possibleValues, initialValue,
				longValue);
		dialog.setVisible(true);
		return value;
	}

	private void setValue(Explanation newValue) {
		value = newValue;
		explanationPicker.setSelectedValue(value, true);
	}

	private ExplanationMgrDialog(Frame frame, Component locationComp, String labelText, String title,
			Explanation[] data, Explanation initialValue, Explanation longValue) {
		super(frame, title, true);

		setPreferredSize(new Dimension(1200, 700));

		/*
		 * Context
		 */
		contextPanel.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Context"));
		contextPanel.setLayout(new GridBagLayout());
		GridBagConstraints contextGbc = new GridBagConstraints();
		contextGbc.anchor = GridBagConstraints.NORTHWEST;
		contextGbc.fill = GridBagConstraints.BOTH;
		contextGbc.weightx = 1.0;
		contextGbc.weighty = 1.0;
		contextGbc.gridheight = 1;
		contextGbc.gridwidth = 1;
		contextGbc.gridx = 0;
		contextGbc.gridy = 0;
		contextGbc.insets = new Insets(5,5,5,5);
		contextPanel.add(contextTextPane, contextGbc);

		/*
		 * Explanation
		 */
		explanationPanel.setBorder(BorderFactory
				.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Explanation"));
		explanationPanel.setLayout(new GridBagLayout());
		GridBagConstraints explanationGbc = new GridBagConstraints();
		explanationGbc.anchor = GridBagConstraints.WEST;
		explanationGbc.fill = GridBagConstraints.HORIZONTAL;
		explanationGbc.gridx = 0;
		explanationGbc.gridy = 0;
		explanationGbc.gridheight = 1;
		explanationGbc.gridwidth = 1;
		explanationGbc.weightx = 1.0;
		explanationGbc.weighty = 0.0;
		explanationGbc.ipadx = 0;
		explanationGbc.ipady = 0;
		explanationGbc.insets = new Insets(5, 5, 5, 5);
		explanationPanel.add(explanationHeader, explanationGbc);

		explanationGbc.gridy = 1;
		explanationGbc.weighty = 1.0;
		explanationGbc.fill = GridBagConstraints.BOTH;
	    explanationPicker = buildExplanationPicker(labelText, data, initialValue, longValue);
		
	    explanationPanel.add(explanationPicker, explanationGbc);

		explanationGbc.gridx = 1;
		explanationGbc.gridy = 0;
		explanationGbc.gridheight = 2;
		explanationPanel.add(explanationContent, explanationGbc);
			
		explanationGbc.gridx = 0;
		explanationGbc.gridy = 2;
		explanationGbc.gridwidth = 2;
		explanationGbc.gridheight = 1;
		explanationGbc.weightx = 1.0;
		explanationGbc.weighty = 0.0;
		explanationGbc.fill = GridBagConstraints.HORIZONTAL;
		explanationPanel.add(explanationActionPanel, explanationGbc);

		explanationPicker = buildExplanationPicker(labelText, data, initialValue, longValue);

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

		/*
		 * Main panel
		 */
		mainPanel.setLayout(new GridLayout(1, 2));
		mainPanel.add(contextPanel);
		mainPanel.add(explanationPanel);

		// Put everything together, using the content pane's BorderLayout.
		Container contentPane = getContentPane();
		contentPane.add(mainPanel, BorderLayout.CENTER);
		contentPane.add(mainActionPanel, BorderLayout.PAGE_END);

		// Initialize values.
		setValue(initialValue);
		pack();
		setLocationRelativeTo(locationComp);
	}

	private JList<Explanation> buildExplanationPicker(String labelText, Explanation[] data, Explanation initialValue,
			Explanation longValue) {
		// main part of the dialog
		JList<Explanation>  explanationPicker = new JList<Explanation>(new ExplanationListModel(data));

		explanationPicker.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		explanationPicker.setLayoutOrientation(JList.VERTICAL);
		explanationPicker.setVisibleRowCount(-1);
		explanationPicker.addListSelectionListener(this);
		JScrollPane listScroller = new JScrollPane(explanationPicker);
		listScroller.setPreferredSize(new Dimension(250, 80));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);

		// Create a container so that we can add a title around
		// the scroll pane. Can't add a title directly to the
		// scroll pane because its background would be white.
		// Lay out the label and scroll pane from top to bottom.
		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		JLabel label = new JLabel(labelText);
		label.setLabelFor(explanationPicker);
		listPane.add(label);
		listPane.add(Box.createRigidArea(new Dimension(0, 5)));
		listPane.add(listScroller);
		listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		return explanationPicker;
	}

	// Handle clicks on the Set and Cancel buttons.
	public void actionPerformed(ActionEvent e) {
		if ("Save".equals(e.getActionCommand())) {
			String currentHeader = explanationHeader.getText();
			String currentContent = explanationContent.getText();
			currentExplanation = new Explanation(currentHeader, currentContent);
			if (checkUnique(currentExplanation)) {
				processSave();			
			}
		}
		else if ("Clear".equals(e.getActionCommand())) {
			explanationPicker.removeListSelectionListener(this);
			explanationHeader.setText("");
			explanationContent.setText("");
			explanationPicker.clearSelection();
			explanationPicker.addListSelectionListener(this);
			selectedIdx = 0;
			
		}
		else if ("Select".equals(e.getActionCommand())) {
			ExplanationMgrDialog.dialog.setVisible(false);
		}
		else if ("Cancel".equals(e.getActionCommand())) {
			ExplanationMgrDialog.dialog.setVisible(false);
		}
		
	}
	
	private void processSave() {
		explanationPicker.clearSelection();
		ExplanationListModel model = (ExplanationListModel) explanationPicker.getModel();
		model.addExplanation(currentExplanation);
		repaint();
	}
	
	private Explanation[] explanationListToArray(List<Explanation> explanationList) {
		final Explanation[] sortedExplanations = new Explanation[explanationList.size()];
		int idx = 0;
		for (Explanation explanation : explanationList) {
			sortedExplanations[idx++] = explanation;
		}
		return sortedExplanations;
	}
	
	private boolean checkUnique(Explanation insertionCandidate) {
		boolean isUnique = true;
		ListModel<Explanation> model = explanationPicker.getModel();
		for (int idx = 0; idx < model.getSize(); idx++) {
			Explanation existingExplanation = explanationPicker.getModel().getElementAt(idx);
			if (existingExplanation.getHeader().equals(insertionCandidate.getHeader())) {
				isUnique = false;
				break;
			}
		}
		return isUnique;
	}

	@Override
	public void valueChanged(ListSelectionEvent selectEvent) {
		if (!selectEvent.getValueIsAdjusting()) {
			System.out.println("Called valueChanged");
			int firstIndex = selectEvent.getFirstIndex();
			int lastIndex = selectEvent.getLastIndex();
			if (firstIndex != selectedIdx) {
				selectedIdx = firstIndex;
			}
			else if (lastIndex != selectedIdx) {
				selectedIdx = lastIndex;
			}
			currentExplanation = explanationPicker.getModel().getElementAt(selectedIdx);
			if (currentExplanation != null) {
				explanationHeader.setText(currentExplanation.getHeader());
				explanationContent.setText(currentExplanation.getContent());
			}
		}
	
	}

}