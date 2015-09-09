package org.tud.reneviz.data;

/**
 * This class is used to store constants of Reneviz in one place.
 *
 */
public final class Constants {
	/**
	 * the maximum number of results (graph edges) to be returned in a query.
	 */
	public static final int MAX_RESULT_LIST_SIZE = 20;
	
	public static final String SPARQL_IP = "localhost";
	public static final String SPARQL_PORT = "3131";
	public static final String SPARQL_DATASET_PATH = "/cornell";

	public static final int FOCUS_NEIGHBORHOOD = 2;
	
	public static String getSparqlService() {
		return "http://"
				+ SPARQL_IP
				+ ":"
				+ SPARQL_PORT
				+ SPARQL_DATASET_PATH
				+ "/query";
	}
	
	public static String getSparqlData() {
		return "http://"
				+ SPARQL_IP
				+ ":"
				+ SPARQL_PORT
				+ SPARQL_DATASET_PATH
				+ "/data?default";
	}
	
	/**
	 * private constructor: This class should not be instantiated.
	 */
	private Constants() {
		// intentionally empty
	}
}
