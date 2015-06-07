package net.floodlightcontroller.prediction;

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
         * @param behaviourManager : The RuleManager object
         */
        public GetBehaviourAsync(BehaviourManager behaviourManager) {
            this.bm =  behaviourManager;
        }

        public void run() {

            while(bm.isRunning()){
                //Query the DB
                bm.setBehaviours();
                try {
                    Thread.sleep(bm.getSleepTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    protected List<Behaviour> behaviours = new ArrayList<>();
    protected MongoDBInfo mongodb;
    protected Thread getRulesThread;
    protected boolean isRunning = true;
    //5 seconds of default sleep time
    protected int sleepTime =  5000;


    public BehaviourManager(MongoDBInfo info) {
        this.mongodb = info;
        startThread();
    }
    public BehaviourManager(MongoDBInfo info, int sT) {
        this.sleepTime = sT;
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
