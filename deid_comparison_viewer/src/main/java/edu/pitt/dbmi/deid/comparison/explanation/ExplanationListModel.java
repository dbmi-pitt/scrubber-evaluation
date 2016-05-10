package edu.pitt.dbmi.deid.comparison.explanation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class ExplanationListModel implements ListModel<Explanation> {
	
	private List<Explanation> explanations = new ArrayList<Explanation>();
	
	public ExplanationListModel(Explanation[] explanations) {
		for (Explanation explanation : explanations) {
			this.explanations.add(explanation);
		}
		Collections.sort(this.explanations);
	}

	public void addExplanation(Explanation explanation) {
		explanations.add(explanation);
		Collections.sort(explanations);
	}

	@Override
	public Explanation getElementAt(int arg0) {
		return explanations.get(arg0);
	}

	@Override
	public int getSize() {
		return explanations.size();
	}
	
	@Override
	public void addListDataListener(ListDataListener arg0) {
		
	}

	@Override
	public void removeListDataListener(ListDataListener arg0) {
		
	}


}
