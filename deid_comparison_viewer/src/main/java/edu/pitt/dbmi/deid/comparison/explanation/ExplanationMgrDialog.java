package edu.pitt.dbmi.deid.comparison.explanation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ExplanationMgrDialog extends JDialog implements ActionListener {
    
	private static final long serialVersionUID = 1L;
	private static ExplanationMgrDialog dialog;
    private static String value = "";
    private JList<String> explanationsList;
    private JTextField currentTitleField = new JTextField();
    private JTextPane currentExplanation = new JTextPane();
    private JButton newButton = new JButton("New");
    private JButton clearButton = new JButton("Clear");
    private JButton cancelButton = new JButton("Cancel");
    private JButton selectButton = new JButton("Select");
    private HomogenousButtonPanel actionPanel = new HomogenousButtonPanel();
    

    /**
     * Set up and show the dialog.  The first Component argument
     * determines which frame the dialog depends on; it should be
     * a component in the dialog's controlling frame. The second
     * Component argument should be null if you want the dialog
     * to come up with its left corner in the center of the screen;
     * otherwise, it should be the component on top of which the
     * dialog should appear.
     */
    public static String showDialog(Component frameComp,
                                    Component locationComp,
                                    String labelText,
                                    String title,
                                    String[] possibleValues,
                                    String initialValue,
                                    String longValue) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new ExplanationMgrDialog(frame,
                                locationComp,
                                labelText,
                                title,
                                possibleValues,
                                initialValue,
                                longValue);
        dialog.setVisible(true);
        return value;
    }

    private void setValue(String newValue) {
        value = newValue;
        explanationsList.setSelectedValue(value, true);
    }

    @SuppressWarnings({ "unchecked", "serial" })
	private ExplanationMgrDialog(Frame frame,
                       Component locationComp,
                       String labelText,
                       String title,
                       Object[] data,
                       String initialValue,
                       String longValue) {
        super(frame, title, true);

        newButton.addActionListener(this);
        cancelButton.addActionListener(this);
        clearButton.addActionListener(this);
        selectButton.addActionListener(this);
        
        actionPanel.add(newButton);
        actionPanel.add(cancelButton);
        actionPanel.add(clearButton);
        actionPanel.add(selectButton);
        actionPanel.layoutButtons();
        
        //Create and initialize the buttons.
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        //
        final JButton setButton = new JButton("Set");
        setButton.setActionCommand("Set");
        setButton.addActionListener(this);
        getRootPane().setDefaultButton(setButton);

        //main part of the dialog
        explanationsList = new JList(data) {
            //Subclass JList to workaround bug 4832765, which can cause the
            //scroll pane to not let the user easily scroll up to the beginning
            //of the list.  An alternative would be to set the unitIncrement
            //of the JScrollBar to a fixed value. You wouldn't get the nice
            //aligned scrolling, but it should work.
            public int getScrollableUnitIncrement(Rectangle visibleRect,
                                                  int orientation,
                                                  int direction) {
                int row;
                if (orientation == SwingConstants.VERTICAL &&
                      direction < 0 && (row = getFirstVisibleIndex()) != -1) {
                    Rectangle r = getCellBounds(row, row);
                    if ((r.y == visibleRect.y) && (row != 0))  {
                        Point loc = r.getLocation();
                        loc.y--;
                        int prevIndex = locationToIndex(loc);
                        Rectangle prevR = getCellBounds(prevIndex, prevIndex);

                        if (prevR == null || prevR.y >= r.y) {
                            return 0;
                        }
                        return prevR.height;
                    }
                }
                return super.getScrollableUnitIncrement(
                                visibleRect, orientation, direction);
            }
        };

        explanationsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (longValue != null) {
            explanationsList.setPrototypeCellValue(longValue); //get extra space
        }
        explanationsList.setLayoutOrientation(JList.VERTICAL);
        explanationsList.setVisibleRowCount(-1);
        explanationsList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    setButton.doClick(); //emulate button click
                }
            }
        });
        JScrollPane listScroller = new JScrollPane(explanationsList);
        listScroller.setPreferredSize(new Dimension(250, 80));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);

        //Create a container so that we can add a title around
        //the scroll pane.  Can't add a title directly to the
        //scroll pane because its background would be white.
        //Lay out the label and scroll pane from top to bottom.
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel(labelText);
        label.setLabelFor(explanationsList);
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Lay out the buttons from left to right.
//        JPanel buttonPane = new JPanel();
//        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
//        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
//        buttonPane.add(Box.createHorizontalGlue());
//        buttonPane.add(cancelButton);
//        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
//        buttonPane.add(setButton);

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(actionPanel, BorderLayout.PAGE_END);

        //Initialize values.
        setValue(initialValue);
        pack();
        setLocationRelativeTo(locationComp);
    }

    //Handle clicks on the Set and Cancel buttons.
    public void actionPerformed(ActionEvent e) {
        if ("Set".equals(e.getActionCommand())) {
            ExplanationMgrDialog.value = (explanationsList.getSelectedValue());
        }
        ExplanationMgrDialog.dialog.setVisible(false);
    }
}