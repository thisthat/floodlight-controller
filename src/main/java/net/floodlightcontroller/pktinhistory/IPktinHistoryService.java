package net.floodlightcontroller.pktinhistory;
 
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.types.SwitchMessagePair;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.topology.ITopologyService;
 
public interface IPktinHistoryService extends IFloodlightService {
    public ConcurrentCircularBuffer<SwitchMessagePair> getBuffer();
    public ILinkDiscoveryService getTopology();
    public String getTopologyGraph();
}