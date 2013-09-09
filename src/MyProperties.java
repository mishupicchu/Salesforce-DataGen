import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
 
/**
 * The Constants class provides properties for SalesforceClient.java,  Record.java, WorkRecord.java and Bug.java
 * @author  Mishika Vora
 */
public class MyProperties {
    public String environment;
    public String callbackUrl;
    public String CLIENT_ID;
    public String CLIENT_SECRET;
    public String USERNAME;
    public String PASSWORD;
    public int numRecs;
	public int numThreads;
    private static Logger logger = Logger.getLogger(MyProperties.class);
    
    
    /**
     * Creates a new MyProperties object and sets all fields
     * @param propFile - properties file to refer to
     */
    public MyProperties(String propFile) {
    	Properties prop = new Properties();
    	try {
            //load a properties file
    		prop.load(new FileInputStream(propFile));
    		this.environment = prop.getProperty("environment");
    		this.callbackUrl = prop.getProperty("callbackUrl");
    		this.CLIENT_ID = prop.getProperty("CLIENT_ID");
    		this.CLIENT_SECRET = prop.getProperty("CLIENT_SECRET");
    		this.USERNAME = prop.getProperty("USERNAME");
    		this.PASSWORD = prop.getProperty("PASSWORD");
    		this.numRecs = Integer.parseInt((prop.getProperty("numRecs")));
    		this.numThreads = Integer.parseInt((prop.getProperty("numThreads")));
    		logger.info("Added Properties from: "+ propFile);
    	} catch (IOException ex) {
    		logger.error(ex);
    		ex.printStackTrace();
        }
    }
    
 
   
    
    
}