    
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.text.NumberFormat;
import org.apache.log4j.Logger;

/**
 * The RecordGen class uses Record.java, SalesforceClient.java
 * and WorkerThread.java to push numBugs # of Records (Bugs) to your Salesforce app.
 * @author  Mishika Vora
 */
public class RecordGen {
	private static Logger recIdsLogger = Logger.getLogger("recIdsLogger");
	private static Logger logger = Logger.getLogger(RecordGen.class);
	public static long startTime;
    public static long endTime;


    public static void main(String[] args) throws Exception {
    	String propFile;
    	if (args.length > 0) {
    	    propFile = args[0];
    	}
    	else 
    		propFile = Constants.defaultPropFile;    
   	 	 SalesForceClient client = new SalesForceClient(propFile);
    	 ExecutorService executor = Executors.newFixedThreadPool(client.prop.numThreads);
    	 Record rec = new Bug(client);
    	 startTime = System.nanoTime();
    	 for (int i = 0; i < client.prop.numRecs ; i++) {
    		 Runnable worker = new WorkerThread(rec,"" + i);
    		 executor.execute(worker);
    	 }
    	 executor.shutdown();
    	 while (!executor.isTerminated()) {
    	    
    	 }
      	 endTime = System.nanoTime();
    	 logger.info("Finished all Threads");
    	 LineNumberReader  lnr = new LineNumberReader(new FileReader(new File("bugIds.log")));
     	 lnr.skip(Long.MAX_VALUE);
     	 int numLines = lnr.getLineNumber();
     	 double percent = numLines/client.prop.numRecs;
     	 NumberFormat percentFormat = NumberFormat.getPercentInstance();
     	 percentFormat.setMaximumFractionDigits(1);
     	 String result = percentFormat.format(percent);
     	 String succRec = numLines + "/" + client.prop.numRecs +  " (" + result + ") " + "number of records successfully created";
     	 long msTime = (endTime - startTime) / 1000000;
     	 long minTime = TimeUnit.MILLISECONDS.toMinutes(msTime);
     	 long secTime = TimeUnit.MILLISECONDS.toSeconds(msTime - TimeUnit.MINUTES.toMillis(minTime));
     	 String elapsedTime = 
     			 String.format("%d min, %d sec", minTime, secTime);
     	 logger.info(succRec);
     	 logger.info("Total Elapsed Time: " + elapsedTime);
     	 lnr.close();
    	 
   }
    
    
    

   

}