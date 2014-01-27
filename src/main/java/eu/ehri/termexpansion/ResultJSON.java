package eu.ehri.termexpansion;

import org.json.JSONObject;

public class ResultJSON {

	private JSONObject resultJSON;
	

	public ResultJSON() {
		
	}
	
	public ResultJSON(JSONObject obj) {
		resultJSON = obj;
	}
	
	public JSONObject getResult() {
		return resultJSON;
	}

	public void setResult(JSONObject result) {
		this.resultJSON = result;
	}

	
}