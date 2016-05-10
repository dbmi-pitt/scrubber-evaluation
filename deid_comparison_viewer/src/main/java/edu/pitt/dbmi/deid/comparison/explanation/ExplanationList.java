package edu.pitt.dbmi.deid.comparison.explanation;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JList;
import javax.swing.SwingConstants;

public class ExplanationList extends JList<Explanation> {

	private static final long serialVersionUID = 1L;
	
	public ExplanationList(Explanation[] data) {
		super(data);
	}
	
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

}
