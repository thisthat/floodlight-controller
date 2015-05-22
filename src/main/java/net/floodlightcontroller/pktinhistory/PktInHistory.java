package net.floodlightcontroller.pktinhistory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.DatapathId;

import weka.classifiers.functions.MultilayerPerceptron;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.types.SwitchMessagePair;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.linkdiscovery.LinkInfo;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.topology.ITopologyService;

// AllSwitchStatisticsResource
public class PktInHistory implements IFloodlightModule,IPktinHistoryService, IOFMessageListener {
	
	protected IFloodlightProviderService floodlightProvider;
	protected ConcurrentCircularBuffer<SwitchMessagePair> buffer;
	protected IRestApiService restApi;
	protected MultilayerPerceptron mp;
	protected ILinkDiscoveryService topology;
	protected List<SwitchNode> switches = new ArrayList<SwitchNode>();
	protected List<SwitchEdge> graph = new ArrayList<SwitchEdge>();
	public class SwitchNode {
		private String dpid;
		public SwitchNode(String n){
			dpid = n;
		}
		public String getName(){
			return dpid;
		}
		@Override
		public String toString(){
			return dpid;
		}
	}
	public class SwitchEdge {
		private SwitchNode n1;
		private SwitchNode n2;
		public SwitchEdge(SwitchNode _n1, SwitchNode _n2){
			n1 = _n1;
			n2 = _n2;
		}
		public SwitchNode getFrom(){
			return n1;
		}
		public SwitchNode getTo(){
			return n2;
		}
		@Override
		public String toString(){
			return n1 + " --> " + n2;
		}
	}
	
	
	@Override
	public String getName() {
		return "PktInHistory_Test_Module";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		switch (msg.getType()) {
			case PACKET_IN:
				buffer.add(new SwitchMessagePair(sw, msg));
				break;
			default: 
				break;
		}
		return Command.CONTINUE;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
	    l.add(IPktinHistoryService.class);
	    return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
	    Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
	    m.put(IPktinHistoryService.class, this);
	    return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IRestApiService.class);
		l.add(ITopologyService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		topology = context.getServiceImpl(ILinkDiscoveryService.class);
		buffer = new ConcurrentCircularBuffer<SwitchMessagePair>(SwitchMessagePair.class, 100);
		
	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		restApi.addRestletRoutable(new PktInHistoryWebRoutable());
	}
	
	@Override
	public ILinkDiscoveryService getTopology(){
		return topology;
	}
	/*
	 ILinkDiscoveryService ld = (ILinkDiscoveryService)getContext().getAttributes().
             get(ILinkDiscoveryService.class.getCanonicalName());
     Map<Link, LinkInfo> links = new HashMap<Link, LinkInfo>();
     Set<LinkWithType> returnLinkSet = new HashSet<LinkWithType>();

     if (ld != null) {
         links.putAll(ld.getLinks());
         for (Link link: links.keySet()) {
             LinkInfo info = links.get(link);
             LinkType type = ld.getLinkType(link, info);
             if (type == LinkType.DIRECT_LINK || type == LinkType.TUNNEL) {
                 LinkWithType lwt;

                 DatapathId src = link.getSrc();
                 DatapathId dst = link.getDst();
                 OFPort srcPort = link.getSrcPort();
                 OFPort dstPort = link.getDstPort();
                 Link otherLink = new Link(dst, dstPort, src, srcPort);
                 LinkInfo otherInfo = links.get(otherLink);
                 LinkType otherType = null;
                 if (otherInfo != null)
                     otherType = ld.getLinkType(otherLink, otherInfo);
                 if (otherType == LinkType.DIRECT_LINK ||
                         otherType == LinkType.TUNNEL) {
                     // This is a bi-direcitonal link.
                     // It is sufficient to add only one side of it.
                     if ((src.getLong() < dst.getLong()) || (src.getLong() == dst.getLong()
                     		&& srcPort.getPortNumber() < dstPort.getPortNumber())) {
                         lwt = new LinkWithType(link,
                                 type,
                                 LinkDirection.BIDIRECTIONAL);
                         returnLinkSet.add(lwt);
                     }
                 } else {
                     // This is a unidirectional link.
                     lwt = new LinkWithType(link,
                             type,
                             LinkDirection.UNIDIRECTIONAL);
                     returnLinkSet.add(lwt);

                 }
             }
         }
     }*/
	private void createTopology(){
		Map<Link, LinkInfo> links = new HashMap<Link, LinkInfo>();
		links = topology.getLinks();
		graph.clear();
		for (Link link: links.keySet()) {
			DatapathId src = link.getSrc();
            DatapathId dst = link.getDst();
            SwitchNode n1 = new SwitchNode(src.toString());
            SwitchNode n2 = new SwitchNode(dst.toString());
            SwitchEdge e = new SwitchEdge(n1, n2);
            graph.add(e);
            if(!switches.contains(n1)){
            	switches.add(n1);
            }
            if(!switches.contains(n2)){
            	switches.add(n2);
            }
		}
	}
	
	@Override
	public String getTopologyGraph(){
		createTopology();
		String out = "";
		for(SwitchEdge e : graph){
			out += e.toString() + "\n";
		}
		return out;
	}

	@Override
	public ConcurrentCircularBuffer<SwitchMessagePair> getBuffer() {
	    return buffer;
	}

}
