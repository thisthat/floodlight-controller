package net.floodlightcontroller.pktinhistory;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class TopologySetTimeoutResource extends ServerResource {
	/*
	@Get("json")
    public String retrieve() throws FileNotFoundException, UnsupportedEncodingException {
		String strTime = (String) getRequestAttributes().get("time");
		int time = Integer.parseInt(strTime);
		INetTopologyService pihr = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        pihr.setTimeout(time);
        return "[]";
    }*/
	
	@Post
	public String store(String in) {
		int time = Integer.parseInt(in);
		INetTopologyService pihr = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        pihr.setTimeout(time);
        return "[]\n";
	}
}
