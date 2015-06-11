package net.floodlightcontroller.prediction;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle all the behaviour System
 * Created by Giovanni Liva on 07.06.15.
 */
public class BehaviourManager {

    /**
     * Thread class to async query the database
     */
    public class GetBehaviourAsync implements Runnable {

        BehaviourManager bm;

        /**
         * Create the thread
         * @param behaviourManager : The Behaviour Manager object
         */
        public GetBehaviourAsync(BehaviourManager behaviourManager) {
            this.bm =  behaviourManager;
        }

        public void run() {

            while(bm.isRunning()){
                //Query the DB
                bm.setBehaviours();
                bm.checkBehaviours();
                try {
                    Thread.sleep(bm.getSleepTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //List of behaviours
    protected List<Behaviour> behaviours = new ArrayList<>();
    //Prediction Module
    protected PredictionHandler prediction;

    protected MongoDBInfo mongodb;
    protected Thread getRulesThread;
    protected boolean isRunning = true;
    //5 seconds of default sleep time
    protected int sleepTime =  5000;


    public BehaviourManager(MongoDBInfo info, PredictionHandler pred) {
        this.mongodb = info;
        this.prediction = pred;
        startThread();
    }
    public BehaviourManager(MongoDBInfo info, PredictionHandler pred, int sT) {
        this.sleepTime = sT;
        this.prediction = pred;
        this.mongodb = info;
        startThread();
    }

    private void startThread(){
        GetBehaviourAsync myRunnable = new GetBehaviourAsync(this);
        getRulesThread = new Thread(myRunnable);
        getRulesThread.start();
    }

    /**
     * Set the current active behaviours
     * @param dbBehaviours: List of  the behaviours stored in the db
     */
    public void setBehaviours(List<Behaviour> dbBehaviours){
        //Search for new elements
        for(Behaviour b : dbBehaviours){
            //Add new behaviour
            if(!behaviours.contains(b)){
                behaviours.add(b);
            }
        }
        //Delete the not more existing elements
        boolean exists;
        Behaviour bList;
        for(int i = 0; i < behaviours.size(); i++){
            exists = false;
            bList = behaviours.get(i);
            for(Behaviour b : dbBehaviours){
                if(bList.equals(b)){
                    exists = true;
                }
            }
            if(!exists){
                behaviours.remove(i);
            }
        }
        System.out.println("======");
        System.out.println("Behaviour Manager loaded " + behaviours.size() + " behaviours");
        System.out.println("======");
    }

    /**
     * Check whenever a Behaviour is applyable and fires it in case!
     */
    public void checkBehaviours(){
        //Going through the current behaviours
        Behaviour b;
        for(int i = 0; i < behaviours.size(); i++){
            b = behaviours.get(i);
            PredictionHandler.PredictionNode sw = prediction.getSwitch(b.getDPID());
            try {
                int classPredicted = sw.executePredictionClassIndex();
                String POSTString = b.toJSON(classPredicted);
                sendData(POSTString);
            }
            catch(Exception e){
                System.out.println("[ERR] Failed behaviour :: " + e.getMessage() + " :: " + e.getClass());
            }
        }
    }

    private void sendData(String data) throws Exception {
        String url = "http://127.0.0.1::8080/wm/staticflowpusher/json";
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("POST");
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        out.write(data);
        out.close();
    }



    /**
     * Set the current active behaviours gettin the info from the database
     */
    public void setBehaviours(){
        //Search for new elements
        this.setBehaviours(mongodb.getBehavioursList());
    }



    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }


}
