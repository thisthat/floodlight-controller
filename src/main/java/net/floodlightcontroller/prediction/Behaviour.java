package net.floodlightcontroller.prediction;

/**
 * Class to store information about the behaviour
 * Created by Giovanni Liva on 07.06.15.
 */
public class Behaviour {

    protected String sw;
    protected String symbol;
    protected int load;
    protected Rule rule;
    protected int createdAtMS;

    public final String LESS = "<";
    public final String GREATER = ">";

    /**
     * Construct a behaviour
     * @param sw : Switch DPID
     * @param symbol : &le; or &ge;
     * @param load : KB of load of the network
     * @param rule : The Rule to fire
     * @param createdAtMS : Time when the behaviour is created
     */
    public Behaviour(String sw, String symbol, int load, Rule rule, int createdAtMS) {
        this.sw = sw;
        this.symbol = symbol;
        this.load = load;
        this.rule = rule;
        this.createdAtMS = createdAtMS;
    }

    /**
     * Return if the behaviour is applicable or not with the load of the network
     * @param classIndex : Index of the class that rappresent the load of the network
     * @return true or fasle
     */
    public boolean isApplicable(int classIndex){
        if(this.symbol.equals(LESS))  return load <= classIndex;
        else return load >= classIndex;
    }

    /**
     * Convert the behaviour in json to be used with the static flow api
     * @param classIndex: The load of the system to check if the rule has to be applyed or not!
     * @return the String in json conform with the Static Flow PUSH API
     */
    public String toJSON(int classIndex){
        String out = "{\n";
        out += "\"switch\" : \"" + this.rule.getDpid() + "\", \n";
        out += "\"name\" : \"" + this.rule.getName()  + "\", \n";
        out += "\"priority\" : \"" + this.rule.getPriority() + "\", \n";
        out += "\"in_port\" : \"" + this.rule.getInPort() + "\", \n";
        out += "\"active\" : \"" + ( isApplicable(classIndex) ? "true" : "false" )  + "\", \n";
        out += "\"actions\" : \"" + this.rule.getAction() + "\" \n";
        out += "}";
        return out;
    }

    public int getCreatedAtMS() {
        return createdAtMS;
    }

    public void setCreatedAtMS(int createdAtMS) {
        this.createdAtMS = createdAtMS;
    }

    public String getDPID() {
        return sw;
    }

    public String getSymbol() {
        return symbol;
    }


    public int getLoad() {
        return load;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Behaviour behaviour = (Behaviour) o;

        if (load != behaviour.load) return false;
        if (createdAtMS != behaviour.createdAtMS) return false;
        if (sw != null ? !sw.equals(behaviour.sw) : behaviour.sw != null) return false;
        if (symbol != null ? !symbol.equals(behaviour.symbol) : behaviour.symbol != null) return false;
        return !(rule != null ? !rule.equals(behaviour.rule) : behaviour.rule != null);

    }

    @Override
    public int hashCode() {
        int result = sw != null ? sw.hashCode() : 0;
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + load;
        result = 31 * result + (rule != null ? rule.hashCode() : 0);
        result = 31 * result + createdAtMS;
        return result;
    }
}
