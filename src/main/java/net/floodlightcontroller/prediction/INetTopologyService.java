package net.floodlightcontroller.prediction;
 
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
 
public interface INetTopologyService extends IFloodlightService {
    ILinkDiscoveryService getTopology();
    String getTopologyGraph(String format);
    void createTopology();
    void setTimeout(int time);
    int getTimeout();
    PredictionHandler getPredictionStructure();
    MongoDBInfo getMongoDBConnection();
    void setMongoDBConnection(String ip, String port);
    String test();
}