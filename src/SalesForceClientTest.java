import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;


public class SalesForceClientTest extends TestCase{

	@Test
	public void testClientCredFalsePositive() throws Exception {
		//badClients make sure errors should be thrown with wrong client credentials
		SalesForceClient badClient1 = new SalesForceClient("config.properties");
		//Testing wrong client credentials
		badClient1.prop.CLIENT_ID = "dshdj";
		badClient1.prop.CLIENT_SECRET = "wieuwue";
		try {
			badClient1.retrieveAccessToken();
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals("FAIL - Client should have errored based on bad credentials", badClient1.accessToken,"Error");
		System.out.println("SUCCESS - Wrong client credentials are detected - checking for false positives");
	}
	
	@Test
	public void testUnPwFalsePositive() {
		//testing client with wrong username/password
		SalesForceClient badClient2 = new SalesForceClient("config.properties");
		badClient2.prop.USERNAME = "dshdj";
		badClient2.prop.PASSWORD = "wieuwue";
		try {
			badClient2.retrieveAccessToken();
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals("FAIL - Client should have errored based on bad username/password",badClient2.accessToken,"Error");
		System.out.println("SUCCESS - Wrong client credentials are detected - checking for false positives");
	}
	
	@Test
	public void testClientCreds() {
		//will fail on any exceptions with the client i.e. incorrect host/environment
		SalesForceClient client1 = new SalesForceClient("config.properties");
		try {
			client1.retrieveAccessToken();
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		//making sure config.properties client credentials are correct
		assertTrue("FAIL - Specified client credentials file has incorrect client id/username & pw",!client1.accessToken.equals("Error"));
		System.out.println("SUCCESS - Your client credentials have successfully passed authentication");
	}
	
	@Test
	public void testSpecifiedProps() {
		SalesForceClient client2 = new SalesForceClient("config.properties");
		try {
			client2.retrieveAccessToken();
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		//making sure config.properties client credentials are correct
		assertTrue("FAIL - numBugs should be >= 0",client2.prop.numRecs >= 0);
		assertTrue("FAIL - numThreads should be > 0",client2.prop.numThreads > 0);
		//This doesn't seem to matter as much but put it just in case.
		assertTrue("FAIL - callbackUrl is not https",client2.prop.callbackUrl.contains("https"));
		assertFalse("FAIL - Bad Client", client2.isBadClient);
		System.out.println("SUCCESS - Properties file looks good!");
		
	}

}
