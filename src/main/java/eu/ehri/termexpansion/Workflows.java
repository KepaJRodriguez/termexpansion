package eu.ehri.termexpansion;

import java.util.List;


//import eu.ehri.search.ProcessQuery;

//import eu.ehri.search.TermIntegration;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import eu.ehri.termexpansion.GetQuery;

@Service
// @Autowired
public class Workflows {

	

	public GetQuery getUserInput(GetQuery userinput) {
		return userinput;
	}

	/*
	 * It takes as argument a user query and returns a JSON object with the
	 * expanded terms in a list.
	 */
	public JSONObject workflowExp(GetQuery userinput) throws JSONException {
		// solrOut = null;
		System.out.println();
		System.out.println("userinput: " + userinput.getQuery());

		ExpandQuery expandedTerms = new ExpandQuery();

		List<String> expandedTermList = expandedTerms.expandTerms(userinput
				.getQuery());

		String expandedQuery = AuxiliaryMethods.listToString(expandedTermList);
		System.out.println("QUERY TO PRODUCE JSON: "
				+ AuxiliaryMethods.listToString(expandedTermList));

		JSONObject response = new JSONObject();
		String query = userinput.getQuery().toString();
		try {
			response.put("result", expandedTermList);
			response.put("q", query);
		} catch (JSONException e) {

		}
		System.out.println(response.toString());

		return response;
	}

}