package net.floodlightcontroller.prediction;
 
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
 
public interface INetTopologyService extends IFloodlightService {
    public ILinkDiscoveryService getTopology();
    public String getTopologyGraph(String format);
    public void createTopology();
    public void setTimeout(int time);
    public int getTimeout();
    public PredictionHandler getPredictionStructure();
}