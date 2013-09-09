import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Record class enables one to use the RESTApi along with the secure
 * httpclient from SalesforceClient to CRUD Salesforce apps.
 * @author  Mishika Vora
 * Note that the default record being created is Account -- specified in Constants.java
 * Note that the default record created is an empty record.
 */
public class Record {
	protected SalesForceClient my_sf;
	protected String sObjectRec;
	private static Logger logger = Logger.getLogger(Record.class);
	private static Logger recIdsLogger = Logger.getLogger("recIdsLogger");
	/*
	* this is the API name for the field we want to serve as the unique 'name' for our records
	* which contains the thread name and record id
	*/
	protected String uniqueNameField;

	
	/**
     * Creates a new Record object with default being Account record
     * @param cli - SalesForceClient used for this record
     */
	public Record (SalesForceClient cli) {
		my_sf = cli;	
		if (cli.isBadClient)
			logger.error("Bad Client");
		sObjectRec = Constants.defaultRec;
		uniqueNameField = Constants.dummyUniqueNameField;
	}
	
	/**
     * Queries and filters record based on the sql query passed
     * @param String soqlQuery - soql query to query and filter records
     * @return JSONObject representing results of query
     * @exception IOException
     * @exception generic exception 
     */
	public JSONObject queryRecords(String soqlQuery) throws  IOException, Exception {
		logger.info("Query: " + soqlQuery);
		HttpGet get = new HttpGet(my_sf.prop.environment + Constants.queryUrl);
		get.addHeader("Authorization", "Bearer " + my_sf.accessToken);
	    ((HttpRequestBase) get).setURI(new URIBuilder(get.getURI()).addParameter("q",soqlQuery).build());
	    JSONObject jobj = new JSONObject();
		try {
		    HttpResponse httpResponse = my_sf.httpclient.execute(get);
		    String response = new String();
       	 	HttpEntity responseEntity = httpResponse.getEntity();
       	 	if(responseEntity !=null) {
       	 		response = EntityUtils.toString(responseEntity);
       	 	}
       	 	//TODO hardcoded records...
       	 	if (response.contains("records")) {
       	 		jobj = new JSONObject(response);
       	 	}
       	 	//error in query
       	 	else {
       	 		logger.error(response);
       	 	}
		}
		 finally {
			get.releaseConnection();
		}
		return jobj;
	}
	
	/**
     * Creates default Account record, and puts Name and dummyName into JSONObject
     * @return JSONObject representing structure of record, fields and values
     * @exception IOException
     * @exception generic exception 
     */
	public JSONObject createRecord() throws IOException, Exception {
		logger.info("Attempting to Create Record");
		JSONObject rec = new JSONObject();
		rec.put("Name", Constants.dummyName);
		return rec;
	}
	
	/**
     * calls createRecord() and changes name - using it as unique id
     * @param String s  - unique name for field
     * @exception IOException
     * @exception generic exception 
     */
	public JSONObject createRecord(String s) throws IOException, Exception {
		JSONObject rec = createRecord();
		rec.remove("Name");
		rec.put("Name", Constants.dummyName + s);
		return rec;
	}
	/**
     * helper method to retrieve the first value from the array of records returned
     * (due to no distinct keyword in soql)
     * @return String a value from records
     * @exception IOException
     * @exception generic exception 
     */
	//TODO records is hardcoded.
	public String getFromRecords (JSONObject orig, String label) throws JSONException {
		logger.info("Accessing records from result of query (getFromRecords())");
		return ((String)((JSONArray) (orig.get("records"))).getJSONObject(0).get(label)).toString();
	}
	
	/**
     * posts record with structure of JSON object passed and into category
     * of specified sObjectRec
     * @param JSONObject representing structure of record, fields and values
	 * @throws Exception 
     */
	protected boolean postRecord(JSONObject jobj) throws Exception {
		//  logger.info("Attempting to post record (postRecord(JSONObject j))");
		  HttpPost post = new HttpPost(my_sf.prop.environment + Constants.postUrl  + sObjectRec +"/");
		 post.addHeader("Authorization", "Bearer " + my_sf.accessToken);
		  post.setEntity(new StringEntity(jobj.toString(), 
                  ContentType.create("application/json")));
	        try {
	            HttpResponse httpResponse = my_sf.httpclient.execute(post);
	            String response = new String();
	        	 HttpEntity responseEntity = httpResponse.getEntity();
	        
	        	 //error occurred
	        	 if(responseEntity !=null) {
	        	     response = EntityUtils.toString(responseEntity);
	        	     if (response.contains("error") && !response.contains("success")) {
	        	    	 logger.error(response);
	        	    	 if (response.contains("INVALID_SESSION_ID")) {
	 	        	    	my_sf.retrieveAccessToken();
	 	        	     }
	        	    	 return false;
	        	     }
	        	     else {
	        	    	 JSONObject j = new JSONObject(response);
	        	    	 String recId = (String) j.get("id");
	        	    	 /*String name = "";
	        	    	 JSONObject jj = queryRecords("Select Name from ADM_Work__c where Id = '" + j.get("id") + "'");
	        	    	 String name = getFromRecords(jj, "Name");*/
	        	    	 String recordName = "";
	        	    	 if (uniqueNameField != "") {
	        	    		 if (jobj.has(uniqueNameField)) {
	        	    			 recIdsLogger.info(recId);
	        	    			 recordName = jobj.getString(uniqueNameField);
	        	    		 }
	        	    	 }
	        	    	 logger.info("Record " + recordName + " Posted - Post Successful");
	        	     }	 
	        	 }
	        	 return true;
	        } finally {
	            post.releaseConnection();
	        }		
	}
	
	/**
     * calls postRecord with structure of JSON object passed and into category
     * of specified field sObjectRec, is post is unsuccessful, retries x num. of times
     * @param JSONObject representing structure of record, fields and values
	 * @throws Exception 
     */
	public boolean validatePost(JSONObject jobj) throws Exception {
		//logger.info("Attempting to Validate Post");
		int trial = 0;
		boolean success = postRecord(jobj);
		while (!success && trial < Constants.numTries) {
			if (!success) {
		    	 String recordName = "";
    	    	 if (uniqueNameField != "")
    	    		 recordName = jobj.getString(uniqueNameField);
				logger.error("Post " + recordName + " failed: Retry # " + (trial+1));
				success = postRecord(jobj);
			}
			else
				break;
			trial++;
		}
		return success;
	}
	
	/**
     * calls updateRecord with structure of JSON object passed and into category
     * of specified field sObjectRec, is update is unsuccessful, retries x num. of times
     * @param JSONObject representing structure of record, fields and values
	 * @throws Exception 
     */
	public boolean validateUpdate(JSONObject jobj, String recId) throws Exception {
		logger.info("Attempting to Validate Update");
		int trial = 0;
		boolean success = updateRecord(jobj, recId);
		while (trial < Constants.numTries) {
			if (!success) {
				logger.error("Trial number " + (trial+1));
				success = updateRecord(jobj, recId);
			}	
			trial++;
		}
		return success;
	}
	
	/**
     * updates Record (id specified in param) with info from JSON Object passed
     * updates specifically the record that is sObjectRec i.e. Account, ADM_Work__c
     * @param JSONObject representing structure of record, fields and values
     * @param id of record specified
     * @exception IllegalStateException
     * @exception JSONException
     * @exception IOException
     */
	public boolean updateRecord(JSONObject jobj, String recId) throws IllegalStateException, JSONException, IOException {
		logger.info("Attempting to Update Record");
		HttpPost post = new HttpPost(my_sf.prop.environment + Constants.postUrl + "User" + "/" + recId) {
			@Override public String getMethod() { return "PATCH"; }
		};
		post.addHeader("Authorization", "Bearer " + my_sf.accessToken);
		  post.setEntity(new StringEntity(jobj.toString(), 
                  ContentType.create("application/json")));
	        try {
	        	 HttpResponse httpResponse =  my_sf.httpclient.execute(post);
	        	 String response = new String();
	        	 HttpEntity responseEntity = httpResponse.getEntity();
	        	 if(responseEntity !=null) {
	        	     response = EntityUtils.toString(responseEntity);
	        	     logger.error(response);
	        	     return false;
	        	 }
	        	 else {
	        		 logger.info(response);
	        		 return true;
	        	 }
	        } finally {
	            post.releaseConnection();
	        }	
		}
	
	/**
     * gets all the ids that exist for a particular object..i.e. id from user table
     * @param the name of the id/lookup field..i.e. id, product_tag__c, 
     * @param string from -- the sobject/table that id can be found
     * @exception IOException
     * @exception generic exception
     */
	public LinkedList<String> getAllSObjectIds(String table) throws IOException, Exception {
		logger.info("Generating List of Unique Ids for " + table);
		LinkedList<String> allIds = new LinkedList<String>();
		JSONObject j = queryRecords(getIdQuery("Id",table));
		//TODO hardcoded records
		if (j.has("records")) {
		JSONArray records = ((JSONArray) (j.get("records")));
		int length = records.length();
		for (int i = 0; i < length; i++) {
			allIds.add((records.getJSONObject(i).get("Id")).toString());
			//TODO clean - System.out.println(allIds.get(i));
		}
		}
		else {
			logger.error("No records exist to create list of Lookup Values");
		}
		//TODO clean - System.out.println(j.toString(2));
		return allIds;
	}
	
	/**
     * generates soql query to get id value
     * @param the name of the id/lookup field..i.e. id, product_tag__c, 
     * @param string from -- the sobject/table that id can be found
     */
	public String getIdQuery(String lookupField, String from) {
		return "Select " + lookupField + " from " + from +  " where " + lookupField +  " !=null ";
	}
	
	/**
     * gets random id from list of ids
     * @param the name of the id/lookup field..i.e. id, product_tag__c, 
     * @param string from -- the sobject/table that id can be found
     * @exception IOException
     * @exception generic exception
     */
	public String getRandVal(LinkedList<String> idList) throws IOException, Exception {
		Random generator = new Random();
		if (idList.size() == 1)
			return idList.get(0);
		else if (idList.size() > 0) {
	    	int n = generator.nextInt(idList.size());
	    	return idList.get(n);}
	    else {
	    	logger.error("getRandVal - List is empty");
	    	return "";
	    }
	}
	
	/**
     * creates and posts record
     * @exception IOException
     * @exception generic exception
     */
	public boolean generateRecord() throws IOException, Exception {
		boolean success = false;
		logger.info("Generating Record: ");
		JSONObject j = createRecord();
		success = validatePost(j);
		return success;
	}
	
	/**
     * calls createRecord(subj) and passes subject - using it as unique id
     * @param String s  - unique name for field
     * @exception IOException
     * @exception generic exception 
     */
	public boolean generateRecord(String subj) throws Exception {
		boolean success = false;
		logger.info("GenerateRecord: " + subj);
		JSONObject j = createRecord(subj);
		success = validatePost(j);
		return success;
	}
	
}