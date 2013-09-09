import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.runner.JUnitCore;

public class RunAllTests {
	 public static void main(String[] args) throws Exception {
		Logger l = Logger.getRootLogger();
		l.setLevel(Level.OFF);
		junit.textui.TestRunner.run(SalesForceClientTest.class);
		junit.textui.TestRunner.run(BugTest.class);
		junit.textui.TestRunner.run(RecordTest.class);
		
	 }
}