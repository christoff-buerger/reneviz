package org.tud.reneviz.util;

import java.util.Comparator;

import org.tud.reneviz.model.graphs.SimilarityNode;

public class SimilarityNodeComparator implements Comparator<SimilarityNode>{

		private SimilarityNodeComparator() {
			// singleton
		};
	
		public int compare(SimilarityNode e1, SimilarityNode e2) {

			// descending order
			return Double.compare(e2.getCentrality(), e1.getCentrality());
		}
		
		private static SimilarityNodeComparator instance_ = new SimilarityNodeComparator();
		
		public static SimilarityNodeComparator getInstance() {
			return instance_;
		}


}
