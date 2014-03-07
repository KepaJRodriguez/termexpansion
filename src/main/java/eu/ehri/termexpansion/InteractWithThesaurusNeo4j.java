package eu.ehri.termexpansion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/*
 * Methods used to interact with the EHRI thesaurus in Neo4j
 * and if possible to expand the term. 
 */
public class InteractWithThesaurusNeo4j {

	/*
	 * it checks whether an string corresponds to a Thesaurus term. It sends a
	 * Cypher query and returns a boolean response
	 */
	public static boolean inThesaurus(String input) throws JSONException {
		boolean inThesaurus = false;
		String query = "START n=node(*)  WHERE has(n.prefLabel) AND"
				+ " (n.prefLabel =~ \"(?i)"
				+ input
				+ "\") OR (has(n.altLabel) AND ANY(x in n.altLabel WHERE x =~ \"(?i)"
				+ input + "\")) RETURN n";
		JSONObject jobject = new JSONObject();
		try {
			Map<String, String> params = new HashMap<String, String>();
			jobject.put("query", query);
			jobject.put("params", params);
		} catch (JSONException e) {
		}

		Client client = Client.create();
		WebResource webResource = client
				.resource("http://localhost:7474/db/data/cypher");

		String response = webResource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(jobject.toString())
				.post(String.class);

		JSONObject jsonObj = new JSONObject(response);
		JSONArray dataArray = jsonObj.getJSONArray("data");

		if (dataArray.length() > 0) {
			inThesaurus = true;
		}
		return inThesaurus;
	}

	/*
	 * This method takes as argument a node in the database represented as a
	 * String and search the broader terms in the thesaurus. The implemented
	 * deep is 10. The method returns a list of preferred and alternative terms
	 * in the different languages.
	 */
	public static List<String> expandWithThesaurusBroader1(String node)
			throws JSONException {
		HashMap<String, List<String>> result = new HashMap();

		List<String> resultsList = new ArrayList<String>();
		List<String> expandedNodes = new ArrayList<String>();
		List<String> expandedTerms = new ArrayList<String>();
		List<String> broader = searchBroader(node);
		List<String> toExpand = broader;
		expandedNodes.add(node);
		expandedNodes.addAll(toExpand);
		List<String> queryList = toExpand;

		// List of concept nodes are expanded into their narrower nodes
		for (int i = 1; i <= 10; i++) {
			if (queryList.size() > 0) {
				for (int a = 0; a < queryList.size(); a++) {
					resultsList.addAll(searchBroader(queryList.get(a)));
				}
			} else {
				break;
			}
			queryList.clear();
			queryList.addAll(resultsList);
			expandedNodes.addAll(resultsList);
			resultsList.clear();
		}
		AuxiliaryMethods.removeDuplicates(expandedNodes);
		// Here we extract the labeled nodes corresponding for each concept
		// node and the preferred and alternative terms of the labeled nodes
		for (int idx = 0; idx < expandedNodes.size(); idx++) {
			List<String> expanded = extractTerms(getLabeledNodes(expandedNodes
					.get(idx)));
			expandedTerms.addAll(expanded);
		}
		AuxiliaryMethods.removeDuplicates(expandedTerms);
		return expandedTerms;
	}

	/*
	 * It takes as argument a term of the Thesaurus and returns the node of the
	 * term in the database. Node are represented as Strings.
	 */
	public static String getConceptNode(String input) throws JSONException {

		// get neo4j cypher querypoing from config.properties
		GetProperties property = new GetProperties();
		String neo4jCypher = property.getCypherQueryPoint();

		// cypher query to get the concept node from an string
		// the string is the user input
		String query = "START n=node(*)  MATCH n -[:describes]-> n1 WHERE has(n.prefLabel) AND (n.prefLabel =~ \"(?i)"
				+ input
				+ "\") OR (has(n.altLabel) AND ANY(x in n.altLabel WHERE x =~ \"(?i)"
				+ input + "\")) RETURN n1";
		JSONObject jobject = new JSONObject();
		try {
			Map<String, String> params = new HashMap<String, String>();
			jobject.put("query", query);
			jobject.put("params", params);
		} catch (JSONException e) {
		}
		Client client = Client.create();
		WebResource webResource = client.resource(neo4jCypher);
		String response = webResource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(jobject.toString())
				.post(String.class);
		return extractNodeIDs(response).get(0);
	}

	/*
	 * This method takes as argument a node in the database represented as a
	 * String and search the narrower terms in the thesaurus. The implemented
	 * deep is 10. The method returns a list of preferred and alternative terms
	 * in the different languages.
	 */
	public static List<String> expandWithThesaurus(String node)
			throws JSONException {
		List<String> resultsList = new ArrayList<String>();
		List<String> expandedNodes = new ArrayList<String>();
		List<String> expandedTerms = new ArrayList<String>();
		List<String> narrower = searchNarrower(node);
		List<String> toExpand = narrower;
		expandedNodes.add(node);
		expandedNodes.addAll(toExpand);
		List<String> queryList = toExpand;

		// List of concept nodes are expanded into their narrower nodes
		for (int i = 1; i <= 10; i++) {
			if (queryList.size() > 0) {
				for (int a = 0; a < queryList.size(); a++) {
					resultsList.addAll(searchNarrower(queryList.get(a)));
				}
			} else {
				break;
			}
			queryList.clear();
			queryList.addAll(resultsList);
			expandedNodes.addAll(resultsList);
			resultsList.clear();
		}
		AuxiliaryMethods.removeDuplicates(expandedNodes);
		// Here we extract the labeled nodes corresponding for each concept
		// node and the preferred and alternative terms of the labeled nodes
		for (int idx = 0; idx < expandedNodes.size(); idx++) {
			List<String> expanded = extractTerms2(getEnglishPreferred(expandedNodes
					.get(idx)));
			expandedTerms.addAll(expanded);
		}
		AuxiliaryMethods.removeDuplicates(expandedTerms);
		return expandedTerms;
	}

	/*
	 * This method takes as argument a node in the database represented as a
	 * String and search the narrower terms in the thesaurus. The implemented
	 * deep is 10. The method returns a list of preferred and alternative terms
	 * in the different languages.
	 */
	public static List<String> expandWithThesaurusBroader(String node)
			throws JSONException {
		List<String> resultsList = new ArrayList<String>();
		List<String> expandedNodes = new ArrayList<String>();
		List<String> expandedTerms = new ArrayList<String>();
		List<String> broader = searchBroader(node);
		List<String> toExpand = broader;
		expandedNodes.add(node);
		expandedNodes.addAll(toExpand);
		List<String> queryList = toExpand;

		// List of concept nodes are expanded into their narrower nodes
		for (int i = 1; i <= 10; i++) {
			if (queryList.size() > 0) {
				for (int a = 0; a < queryList.size(); a++) {
					resultsList.addAll(searchBroader(queryList.get(a)));
				}
			} else {
				break;
			}
			queryList.clear();
			queryList.addAll(resultsList);
			expandedNodes.addAll(resultsList);
			resultsList.clear();
		}
		AuxiliaryMethods.removeDuplicates(expandedNodes);
		// Here we extract the labeled nodes corresponding for each concept
		// node and the preferred and alternative terms of the labeled nodes
		for (int idx = 0; idx < expandedNodes.size(); idx++) {
			List<String> expanded = extractTerms(getLabeledNodes(expandedNodes
					.get(idx)));
			expandedTerms.addAll(expanded);
		}
		AuxiliaryMethods.removeDuplicates(expandedTerms);
		return expandedTerms;
	}

	/*
	 * Only multilingual expansion in one level
	 */
	public static List<String> expandLingThesaurus(String node) {
		new ArrayList<String>();
		// Here we extract the labeled nodes corresponding for the concept
		// node
		List<String> expanded = extractTerms(getLabeledNodes(node));
		AuxiliaryMethods.removeDuplicates(expanded);
		return expanded;
	}

	/*
	 * It takes a neo4j node as json-like string and returns the node IDs.
	 */
	public static List<String> extractNodeIDs(String inputNodes)
			throws JSONException {
		List<String> nodes = new ArrayList<String>();
		List<String> splittedInput = Arrays.asList(inputNodes.split("\n"));

		JSONObject jsonObj = new JSONObject(inputNodes);
		JSONArray dataArray = jsonObj.getJSONArray("data");

		if (dataArray.length() > 0) {
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject record = dataArray.getJSONArray(i).getJSONObject(0);
				String id = record.getString("self");
				System.out.println("SELF " + id);
				String node = id.split("node/")[1].split("\"")[0];
				System.out.println("NODE " + node);
				nodes.add(node);
			}
		}
		return nodes;
	}

	/*
	 * It takes as argument the ID of the node and returns a list of IDs of
	 * narrower nodes
	 */
	public static List<String> searchNarrower(String nodeID)
			throws JSONException {
		JSONObject jobject = new JSONObject();
		String query = "START n=node(" + nodeID
				+ ") MATCH n -[:narrower]-> n1 RETURN n1";
		try {
			Map<String, String> params = new HashMap<String, String>();
			jobject.put("query", query);
			jobject.put("params", params);
		} catch (JSONException e) {
		}
		Client client = Client.create();
		WebResource webResource = client
				.resource("http://localhost:7474/db/data/cypher");
		String response = webResource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(jobject.toString())
				.post(String.class);
		return extractNodeIDs(response);
	}

	/*
	 * It takes as argument the ID of the node and returns a list of IDs of
	 * broader nodes
	 */
	public static List<String> searchBroader(String nodeID)
			throws JSONException {
		JSONObject jobject = new JSONObject();
		String query = "START n=node(" + nodeID
				+ ") MATCH n1 -[:narrower]-> n RETURN n1";
		try {
			Map<String, String> params = new HashMap<String, String>();
			jobject.put("query", query);
			jobject.put("params", params);
		} catch (JSONException e) {
		}
		Client client = Client.create();
		WebResource webResource = client
				.resource("http://localhost:7474/db/data/cypher");
		String response = webResource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(jobject.toString())
				.post(String.class);
		return extractNodeIDs(response);
	}

	/*
	 * it takes as argument the node ID of a concept node and returns the list
	 * of the labeled nodes which describe it (labeled nodes have prefLabel and
	 * altLabel). It returns a json-like string.
	 */
	// public static String getLabeledNodes(String nodeID) {
	// public static JSONObject getLabeledNodes2(String nodeID) {
	public static JSONObject getLabeledNodes2(String nodeID) {
		JSONObject jobject = new JSONObject();
		String query = "START n=node("
				+ nodeID
				+ ") MATCH n <-[:describes]- n1 WHERE n1.languageCode='en' RETURN n1";
		try {
			Map<String, String> params = new HashMap<String, String>();
			jobject.put("query", query);
			jobject.put("params", params);
		} catch (JSONException e) {
		}
		Client client = Client.create();
		WebResource webResource = client
				.resource("http://localhost:7474/db/data/cypher");
		JSONObject response = webResource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(jobject)
				.post(JSONObject.class);
		return response;
	}

	public static String getLabeledNodes(String nodeID) {
		JSONObject jobject = new JSONObject();
		String query = "START n=node("
				+ nodeID
				+ ") MATCH n <-[:describes]- n1 WHERE n1.languageCode='en' RETURN n1";
		try {
			Map<String, String> params = new HashMap<String, String>();
			jobject.put("query", query);
			jobject.put("params", params);
		} catch (JSONException e) {
		}
		Client client = Client.create();
		WebResource webResource = client
				.resource("http://localhost:7474/db/data/cypher");
		String response = webResource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(jobject.toString())
				.post(String.class);
		return response;
	}

	/*
	 * Get English labels: new method
	 */

	public static String getEnglishPreferred(String nodeID) {
		JSONObject jobject = new JSONObject();
		String query = "START n=node("
				+ nodeID
				+ ") MATCH n <-[:describes]- n1 WHERE n1.languageCode='en' RETURN n1.prefLabel";
		try {
			Map<String, String> params = new HashMap<String, String>();
			jobject.put("query", query);
			jobject.put("params", params);
		} catch (JSONException e) {
		}
		Client client = Client.create();
		WebResource webResource = client
				.resource("http://localhost:7474/db/data/cypher");
		String response = webResource.accept(MediaType.TEXT_PLAIN)
				.type(MediaType.TEXT_PLAIN).entity(jobject.toString())
				.post(String.class);
		return response;
	}

	/*
	 * It takes a labeled node (with prefLabel/altLabel) and returns the labels
	 * (thesaurus terms). Constrained to English!!
	 */
	public static List<String> extractTerms(String responseString) {
		List<String> terms = new ArrayList<String>();
		List<String> splittedInput = Arrays.asList(responseString.split("\n"));
		for (int i = 0; i < splittedInput.size(); i++) {
			Pattern pattern1 = Pattern.compile(".+prefLabel.+");
			Pattern pattern2 = Pattern.compile(".+altLabel.+");
			Matcher matcher1 = pattern1.matcher(splittedInput.get(i));
			Matcher matcher2 = pattern2.matcher(splittedInput.get(i));
			if (matcher1.find()) {
				String term = matcher1.group(0).toString().split("\"")[3];
				terms.add(term);
			}
			if (matcher2.find()) {
				String[] termList = matcher2.group(0).toString().split(":")[1]
						.split("\"");
			}
		}
		return terms;
	}

	public static List<String> extractTerms2(String response)
			throws JSONException {
		JSONObject jsonObj = new JSONObject(response);
		List<String> terms = new ArrayList<String>();
		JSONArray jsonarray = jsonObj.getJSONArray("data");
		String term = jsonarray.getJSONArray(0).get(0).toString();
		terms.add(term);
		return terms;
	}

}
