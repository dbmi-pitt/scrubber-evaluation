package edu.pitt.dbmi.deid.comparison.explanation;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class HomogenousButtonPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final List<JButton> buttons = new ArrayList<JButton>();
	private double preferredWidth = 0.0d;
	private double preferredHeight = 0.0d;

	public HomogenousButtonPanel() {
	}

	public void layoutButtons() {
		adjustPreferredWidthToMax();
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
		add(Box.createHorizontalGlue());
		for (int idx = 0; idx < buttons.size(); idx++) {
			add(buttons.get(idx));
			add(Box.createRigidArea(new Dimension(10, 0)));
		}

		// setLayout(new GridBagLayout());
		// GridBagConstraints gbc = new GridBagConstraints();
		// gbc.weightx = 1.0d / buttons.size();
		// gbc.weighty = 1.0d;
		// gbc.fill = GridBagConstraints.HORIZONTAL;
		// gbc.ipadx = 5;
		// gbc.insets = new Insets(5, 5, 5, 5);

	}

	public void addButton(JButton aButton) {
		buttons.add(aButton);
	}

	private void calculatePreferredWidth() {
		preferredWidth = Double.MIN_VALUE;
		for (JButton aButton : buttons) {
			if (aButton.getPreferredSize().getWidth() > preferredWidth) {
				preferredWidth = aButton.getPreferredSize().getWidth();
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
