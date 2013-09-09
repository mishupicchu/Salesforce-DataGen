import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Bug class extends Work and overrides createRecord
 * so instead of creating an empty record by default it fills in all fields for
 * WorkRecord type - Bug
 * @author  Mishika Vora
 * Note that the default record being created is Work -- specified in Constants.java
 * Note that the default record created is an empty record.
 */
public class Bug extends WorkRecord {
	private static Logger logger = Logger.getLogger(Bug.class);
	public LinkedList<String> allStatus;
	public LinkedList<String> allImpactIds;
	public LinkedList<String> allFreqIds;
	public LinkedList<String> allFoundInBuildIds;
	public LinkedList<String> allProdTagIds;
	public LinkedList<String> allUserIds;
	public LinkedList<String> allPriorityIds;
	private String recTypeId;
	
	/**
     * Creates a new Bug object and setting sObject to be Work
     * @param cli - SalesForceClient used for this record
     */
	public Bug(SalesForceClient cli) {
		super(cli);
		uniqueNameField = "Subject__c";
		try {
			preProcess();
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	
	/**
     * Creates bug Record, and inserts fields specific for bug record
     * @return JSONObject representing structure of record, fields and values
     * @exception IOException
     * @exception generic exception 
     */
	public JSONObject createRecord() throws IOException, Exception {
		JSONObject workRec = new JSONObject();
		if (my_sf.isBadClient) {
			logger.error("Bad Client for createRecord()");
		}
		else {
			workRec.put("RecordTypeId",recTypeId);
			workRec.put("Subject__c", Constants.dummyBugSubj);
			workRec.put("Type__c",Constants.dummyBugType);
			workRec.put("Status__c",getRandVal(this.allStatus));
			workRec.put("Impact__c", getRandVal(this.allImpactIds));
			workRec.put("Frequency__c", getRandVal(this.allFreqIds));
			workRec.put("Found_in_Build__c", getRandVal(this.allFoundInBuildIds));		
			workRec.put("Product_Tag__c", getRandVal(this.allProdTagIds));
			workRec.put("Priority__c", getRandVal(this.allPriorityIds));
			workRec.put("Assignee__c", getRandVal(this.allUserIds));
			workRec.put("QA_Engineer__c", getRandVal(this.allUserIds));
			workRec.put("UE_Engineer__c", getRandVal(this.allUserIds));
			workRec.put("Product_Owner__c", getRandVal(this.allUserIds));
			workRec.put("Details_and_Steps_to_Reproduce__c", Constants.dummyDetails);
			workRec.put("Description__c", Constants.dummyComment);
		}
		return workRec;
	}
	
	/**
     * calls createRecord() and changes subject - using it as unique id
     * @param String s  - unique name for field
     * @exception IOException
     * @exception generic exception 
     */
	public JSONObject createRecord(String s) throws IOException, Exception {
		JSONObject bugRec = createRecord();
		bugRec.put("Subject__c", Constants.dummyBugSubj + s);
		return bugRec;
	}
	

	/**
     * Initializes all values needed for bug creation
     * @exception IOException
     * @exception generic exception 
     */
	public void preProcess() throws IOException, Exception {
		logger.info("Generating idLists (preProcess())");
		if (my_sf.isBadClient) {
			logger.error("Bad Client for preProcess()");
		}
		else {
		JSONObject value = queryRecords("SELECT Id FROM RecordType where Name = 'Bug'");
		this.recTypeId = getFromRecords(value,"Id");
		this.allFreqIds = getAllSObjectIds("ADM_Frequency__c");
		this.allImpactIds = getAllSObjectIds("ADM_Impact__c");
		this.allFoundInBuildIds = getAllSObjectIds("ADM_Build__c");
		this.allUserIds = getAllSObjectIds("User");
		this.allProdTagIds = getAllSObjectIds("ADM_Product_Tag__c");
		this.allPriorityIds = new LinkedList<String>(Arrays.asList("P0", 
				"P1", "P2", "P3", "P4"));
		this.allStatus = new LinkedList<String>(Arrays.asList("Closed", 
				"Duplicate", "In Progress", "Integrate", "Never", "New",
				"Not Reproducible", "Not a bug",
				"Pending release", "QA in Progress",
				"Ready for Review", "Triaged", "Waiting"));
		}
		if (this.allProdTagIds.isEmpty())
			addProductTag();
	}
	
	/**
     * Creates initial product tag required for bug creation
     * @exception IOException
     * @exception generic exception 
     */
	public void addProductTag() throws IOException, Exception {
		//temporarily change sObject rec
		String temp = sObjectRec;
		sObjectRec = "ADM_Product_Tag__c";
		JSONObject jobj = new JSONObject();
		jobj.put("Name", "dummyProdTag");
		LinkedList<String> teamIds = getAllSObjectIds("ADM_Scrum_Team__c");
		String val = getRandVal(teamIds);
		jobj.put("Team__c", val);
		postRecord(jobj);
		sObjectRec = temp;
	}

	
	
	
}