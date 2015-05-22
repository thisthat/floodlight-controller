package net.floodlightcontroller.pktinhistory;
 
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
 
public class PktInHistoryResource extends ServerResource {
	
	
    @Get("json")
    public String retrieve() throws FileNotFoundException, UnsupportedEncodingException {
        IPktinHistoryService pihr = (IPktinHistoryService)getContext().getAttributes().get(IPktinHistoryService.class.getCanonicalName());
        return pihr.getTopologyGraph();
    }
    
    
	/*
    @Get("json")
    public Set<LinkWithType> retrieve() {
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
        }
        return returnLinkSet;
    }*/
}