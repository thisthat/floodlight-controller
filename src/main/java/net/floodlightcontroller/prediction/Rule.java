package net.floodlightcontroller.prediction;

/**
 * Class to Store a single Rule
 */
public class Rule{

    protected String name;
    protected String dpid;
    protected String priority;
    protected String inPort;
    protected String action;
    protected int createdAtMS;

    /**
     * Construct a rule
     * @param _name : Name of the rule
     * @param _dpid : Switch DPID
     * @param _priority : Priority of the rule
     * @param _inPort : Port of the switch in which apply the rule
     * @param _action : Action to apply
     * @param _createdAtMS : time when the rule is created
     */
    public Rule(String _name, String _dpid,String _priority, String _inPort, String _action, int _createdAtMS){
        this.name = _name;
        this.dpid = _dpid;
        this.priority = _priority;
        this.inPort = _inPort;
        this.action = _action;
        this.createdAtMS = _createdAtMS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDpid() {
        return dpid;
    }

    public void setDpid(String dpid) {
        this.dpid = dpid;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getInPort() {
        return inPort;
    }

    public void setInPort(String inPort) {
        this.inPort = inPort;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getCreatedAtMS() {
        return createdAtMS;
    }

    public void setCreatedAtMS(int createdAtMS) {
        this.createdAtMS = createdAtMS;
    }
}