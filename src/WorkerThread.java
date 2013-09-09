import java.io.IOException;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * The WorkerThread class generates a thread by implementing Runnable
 * and overrides run and toString
 * When you run this thread it will generate a Record
 * @author  Mishika Vora
 */

public class WorkerThread implements Runnable {

    private String command;
    private Record myRec;
    private static Logger logger = Logger.getLogger(WorkerThread.class);

    /**
     * creates WorkerThread object where command is number passed in
     * i.e. 5 would represent 5th record and thus appended to name in record
     * Each thread has it's reference to a Record object to call generateRecord etc.
     * @author  Mishika Vora
     */
    public WorkerThread(Record r, String s){
        this.command=s;
        myRec = r;
    }

    @Override
    public void run() {
        try {
			myRec.generateRecord(Thread.currentThread().getName() + "_rec#" + this.command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}