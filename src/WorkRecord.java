import org.apache.log4j.Logger;

/**
 * The WorkRecord class enables one to use the RESTApi along with the secure
 * httpclient from SalesforceClient to crud Salesforce apps for Work Specific records.
 * @author  Mishika Vora
 * Note that the default record being created is Work -- specified in Constants.java
 * Note that the default record created is an empty record.
 */
public class WorkRecord extends Record {
	private static Logger logger = Logger.getLogger(WorkRecord.class);
	

	/**
     * Creates a new Work Record object and setting sObject to be Work
     * @param cli - SalesForceClient used for this record
     */
	public WorkRecord(SalesForceClient cli) {
		super(cli);	
		sObjectRec = Constants.API_WorkRec;
		uniqueNameField = "";
	}	
}