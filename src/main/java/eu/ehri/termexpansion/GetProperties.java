package eu.ehri.termexpansion;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GetProperties {

	// get cypher query point of neo4j
	public String getCypherQueryPoint() {
		Properties prop = new Properties();	
		try {
			prop.load(getClass().getResourceAsStream("config.properties"));
		}catch (IOException ex) {
			ex.printStackTrace();
	    }
		String WEB_RESOURCE = prop.getProperty("neo4j_cypher");
		return WEB_RESOURCE;
	}
	
	//get the Solr server
	public String getSolrServer(){
		Properties prop = new Properties();	
		try {
			prop.load(getClass().getResourceAsStream("config.properties"));
		}catch (IOException ex) {
			ex.printStackTrace();
	    }
		String WEB_RESOURCE = prop.getProperty("solr");
		return WEB_RESOURCE;
	}
	
}
