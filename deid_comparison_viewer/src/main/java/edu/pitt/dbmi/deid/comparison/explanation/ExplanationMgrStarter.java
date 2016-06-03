
package edu.pitt.dbmi.deid.comparison.explanation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.pitt.dbmi.deid.comparison.viewer.ExplanationData;

/**
 * A 1.4 application that brings up a ListDialog.
 */
public class ExplanationMgrStarter {

	private static Explanation[] data = { new Explanation("Arlo", "Cosmo"), new Explanation("Elmo", "Hugo"),
			new Explanation("Jethro", "Laszlo"), new Explanation("Milo", "Nemo"), new Explanation("Otto", "Ringo"),
			new Explanation("Rocco", "Rollo") };

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("Explanation");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		JButton button = new JButton("View Explanation Dialog...");
		buttonPanel.add(button);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final boolean isModel = false;
				final ExplanationData explanationData = new ExplanationData(data);
				final ExplanationMgrDialog dialog = new ExplanationMgrDialog(frame, "Scrubber Evaluation Explanation Dialog", isModel, explanationData);
				dialog.setSize(new Dimension(1200,900));
				dialog.setLocationRelativeTo(frame);
				dialog.setVisible(true);
			}
		});
		frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);

		// Display the window.
		frame.getContentPane().setPreferredSize(new Dimension(900, 400));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}