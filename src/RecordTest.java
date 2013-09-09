import static org.junit.Assert.*;

import java.io.IOException;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.junit.Test;


public class RecordTest extends TestCase {
	
	SalesForceClient goodClient = new SalesForceClient("config.properties");
	Record rec = new Record(goodClient);
	
	@Test
	public void testClient() {
		assertFalse("FAIL - Client is Bad",goodClient.isBadClient);
	}
	
	@Test
	public void testQueryRec1() throws IOException, Exception {
		JSONObject jobj = new JSONObject();
		try {
			jobj = rec.queryRecords("dskdjskjd");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		String jobjString = "";
		try {
			jobjString = jobj.toString(2);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals("FAIL - Wrong query is not detected - False positive",jobjString, "{}");
		System.out.println("SUCCESS - Wrong query is detected - checking for false positives");
	}
	
	@Test
	public void testQueryRec2() {
		JSONObject jobj = new JSONObject();
		try {
			jobj = rec.queryRecords("Select dskdjs from User");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		String jobjString = "";
		try {
			jobjString = jobj.toString(2);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals("FAIL - Wrong query is not detected - False positive",jobjString, "{}");
		System.out.println("SUCCESS - Wrong query is detected - checking for false positives");
	}
	
	@Test
	public void testPostRec() {
		JSONObject jobj = new JSONObject();
		boolean success = false;
		try {
			jobj.put("Name", "hdshdsj");
			success = rec.postRecord(jobj);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		assertTrue("FAIL - Post Record unsuccessful",success);
		System.out.println("SUCCESS - Post record is successful");
	}
	
	@Test
	public void testBadPostRec() {
		JSONObject jobj = new JSONObject();
		boolean success = true;
		try {
			jobj.put("hjsdhsh","shdaskhd");
			success = rec.postRecord(jobj);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		assertFalse("FAIL - Wrong JSONObject for post is not detected - False positive",success);
		System.out.println("SUCCESS - Wrong JSONObject for post is detected - checking for false positives");
	}
	
	@Test
	public void testUpdateRec() {
		JSONObject jobj = new JSONObject();
		boolean success = true;
		try {
			success = rec.updateRecord(jobj,"49384328");
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		assertFalse("FAIL - Wrong JSONObject for update is not detected - False positive",success);
		System.out.println("SUCCESS - Wrong JSONObject for update is detected - checking for false positives");
	}
	
	@Test
	public void testValidatePost() {
		boolean success = false;
		JSONObject jobj = new JSONObject();
		try {
			jobj.put("Name", "dsdsjds");
			success = rec.validatePost(jobj);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		assertTrue("FAIL - Validate Post is unsuccessful",success);
		System.out.println("SUCCESS - Validate post is successful");
	}
	
	@Test
	public void testGenRec() {
		boolean success = false;
		try {
			success = rec.generateRecord();
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		assertTrue("FAIL - Generate Record is unsuccessful",success);
		System.out.println("SUCCESS - Generate Record is successful");
	}
	
}
