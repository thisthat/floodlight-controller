package net.floodlightcontroller.prediction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Handle the managment of the Rules
 * Created by Giovanni Liva on 07.06.15.
 */
public class RuleManager {

    /**
     * Thread class to async query the database
     */
    public class GetRuleAsync implements Runnable {

        RuleManager rm;

        /**
         * Create the thread
         * @param ruleManager : The RuleManager object
         */
        public GetRuleAsync(RuleManager ruleManager) {
            this.rm =  ruleManager;
        }

        public void run() {

            while(rm.isRunning()){
                //Query the DB
                rm.setRules();
                try {
                    Thread.sleep(rm.getSleepTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //Map the name of the rule with the rule itself
    protected Map<String, Rule> rules = new HashMap<String, Rule>();
    protected MongoDBInfo mongodb;
    protected Thread getRulesThread;
    protected boolean isRunning = true;
    //5 seconds of default sleep time
    protected int sleepTime =  5000;

    public RuleManager(MongoDBInfo info) {
        this.mongodb = info;
        startThread();
    }
    public RuleManager(MongoDBInfo info, int sT) {
        this.sleepTime = sT;
        this.mongodb = info;
        startThread();
    }

    private void startThread(){
        GetRuleAsync myRunnable = new GetRuleAsync(this);
        getRulesThread = new Thread(myRunnable);
        getRulesThread.start();
    }


    /**
     * Set the current active rules
     * @param dbRules: List of  the rules stored in the db
     */
    public void setRules(List<Rule> dbRules){
        //Search for new elements
        for(Rule r : dbRules){
            String name = r.getName();
            //Add new rule
            if(!rules.containsKey(name)){
                rules.put(name, r);
            }
        }
        //Delete the not more existing elements
        boolean exists;
        for(Iterator<Map.Entry<String,Rule>> it = rules.entrySet().iterator(); it.hasNext();){
            Map.Entry<String, Rule> entry = it.next();
            exists = false;
            for(Rule r : dbRules){
                String name = r.getName();
                if(entry.getKey().equals(name)){
                    exists = true;
                }
            }
            if(!exists){
                it.remove();
            }
        }
        System.out.println("======");
        System.out.println("Rule Manager loaded " + rules.size() + " rules");
        System.out.println("======");
    }
    /**
     * Set the current active rules gettin the info from the database
     */
    public void setRules(){
        //Search for new elements
        this.setRules(mongodb.getRulesList());
    }

    /**
     * Get the rule
     * @param ruleName: Name of the rule to get
     * @return The rule selected if it exists, otherwise null
     */
    public Rule getRule(String ruleName){
        if(rules.containsKey(ruleName)){
            return rules.get(ruleName);
        }
        return null;
    }

    /**
     * Get the list of rules
     * @return the object that hold all the rules
     */
    public Map<String, Rule> getRules() {
        return rules;
    }

    public void setRunning(boolean v){
        isRunning = v;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }
}
