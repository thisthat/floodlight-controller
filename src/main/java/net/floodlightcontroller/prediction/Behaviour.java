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


    public int getCreatedAtMS() {
        return createdAtMS;
    }

    public void setCreatedAtMS(int createdAtMS) {
        this.createdAtMS = createdAtMS;
    }

    public String getSw() {
        return sw;
    }

    public void setSw(String sw) {
        this.sw = sw;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
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
