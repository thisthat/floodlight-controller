package net.floodlightcontroller.pktinhistory;
 
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.types.SwitchMessagePair;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.topology.ITopologyService;
 
public interface INetTopologyService extends IFloodlightService {
    public ILinkDiscoveryService getTopology();
    public String getTopologyGraph(String format);
    public void createTopology();
    public void setTimeout(int time);
}