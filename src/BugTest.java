import static org.junit.Assert.*;

import java.io.IOException;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.*;


public class BugTest extends TestCase{
	
	SalesForceClient goodClient = new SalesForceClient("config.properties");
	Bug bug = new Bug(goodClient);
	
	@Test
	public void testClient() {
		assertFalse("FAIL - Client is Bad", goodClient.isBadClient);
		System.out.println("SUCCESS - Client is good");
	}
	
	@Test
	public void testLists() {
		assertFalse("Found in Build list is empty",bug.allFoundInBuildIds.isEmpty());
		assertFalse("Freq Ids list is empty",bug.allFreqIds.isEmpty());
		assertFalse("Impact Ids list is empty",bug.allImpactIds.isEmpty());
		assertFalse("Priority list is empty",bug.allPriorityIds.isEmpty());
		assertFalse("Product Tag Ids list is empty",bug.allProdTagIds.isEmpty());
		assertFalse("Status list is empty",bug.allStatus.isEmpty());
		assertFalse("User Id list is empty",bug.allUserIds.isEmpty());
		System.out.println("SUCCESS - Preprocess Lists are not empty");
	}
	
	
	@Test
	public void testCreateRec() {
		try {
			bug.createRecord();
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		System.out.println("SUCCESS - Create Record successful");
	}
	
	@Test
	public void testValidatePost() {
		boolean success = false;
		JSONObject jobj = new JSONObject();
		try {
			jobj.put("Subject__c", "dhjshdjas");
			success = bug.validatePost(jobj);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		assertTrue("FAIL - Validate Post unsuccessful",success);
		System.out.println("SUCCESS - Validate Post successful");
	}
	
	@Test
	public void testGenRec() {
		boolean success = false;
		try {
			success = bug.generateRecord();
		}
		catch (Exception e) {
			fail(e.getMessage());	
		}
		assertTrue("FAIL - Generate Record unsuccessful",success);
		System.out.println("SUCCESS - Generate Record successful");
	}
}
