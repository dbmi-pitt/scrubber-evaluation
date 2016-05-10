package edu.pitt.dbmi.deid.comparison.explanation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class HomogenousButtonPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private GridBagConstraints gbc = new GridBagConstraints();
	private final List<JButton> buttons = new ArrayList<JButton>();
	private double preferredWidth = 0.0d;
	private double preferredHeight = 0.0d;

	public HomogenousButtonPanel() {
		setBorder(BorderFactory.createLineBorder(Color.black, 2));
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
	}

	public void layoutButtons() {
		adjustPreferredWidthToMax();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(0,0,0,0);
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.weightx = 1.0d / ((double) buttons.size());
		gbc.weighty = 1.0;
		for (int idx = 1; idx <= buttons.size(); idx++) {
			gbc.gridx = idx;
			add(buttons.get(idx-1), gbc);
		}
	}

	public void addButton(JButton aButton) {
		buttons.add(aButton);
	}

	private void calculatePreferredWidth() {
		preferredWidth = Double.MIN_VALUE;
		for (JButton aButton : buttons) {
			if (aButton.getPreferredSize().getWidth() > preferredWidth) {
				preferredWidth = aButton.getPreferredSize().getWidth();
				preferredHeight = aButton.getPreferredSize().getHeight();
			}
		}
	}

	private void adjustPreferredWidthToMax() {
		calculatePreferredWidth();
		for (JButton aButton : buttons) {
			Dimension preferredSize = new Dimension();
			preferredSize.setSize(preferredWidth, preferredHeight);
			aButton.setPreferredSize(preferredSize);
		}
	}

}
