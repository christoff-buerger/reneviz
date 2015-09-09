package org.tud.reneviz.model.graphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.tud.reneviz.util.SimilarityNodeComparator;

public class SimilarityGraph extends Graph<SimilarityNode, SimilarityEdge> {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	public SimilarityGraph getSubgraph(int size) {

		SimilarityGraph subGraph = new SimilarityGraph();

		size = Math.min(size, this.getNodes().size());
		
		if (size > 0) {
			List<SimilarityNode> nodeList = new ArrayList<SimilarityNode>(
					this.getNodes());

			// TODO check if sort oder is okay
			Collections.sort(nodeList, SimilarityNodeComparator.getInstance());

			List<SimilarityNode> nodeSublist = nodeList.subList(0, size);

			for (SimilarityNode node : nodeSublist) {
				subGraph.addNode(node);
			}

			for (SimilarityEdge edge : this.getEdges()) {
				if (nodeSublist.contains(edge.getNodes().get(0))
						&& nodeSublist.contains(edge.getNodes().get(1))) {
					subGraph.addEdge(edge);
				}
			}

		}
		
		String infoString = "subngraph " + subGraph;
		
		for(SimilarityNode node : subGraph.getNodes()) {
			infoString += "\n\t\t" + node + " - centrality:" + node.getCentrality();
		}

		logger.info(infoString);
		
		return subGraph;

	}
	
	public void computeCentrality() {
		for(SimilarityNode node : this.getNodes()) {

			Collection<SimilarityEdge> adjacentEdges = this.getAdjacentEdges(node);
			
			double centrality = 0;
			
			double degree = adjacentEdges.size();
			
			if(degree != 0) {
				double weightSum = 0.0D;
				for(SimilarityEdge edge : adjacentEdges) {
					weightSum += edge.getSimilarityFromNode(node);
				}
				
				centrality = degree * Math.pow((weightSum/degree),0.8D);
			}
			
			node.setCentrality(centrality);
		}
	}
}
