package net.floodlightcontroller.prediction;

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
        return "{ 'status' : 'ok' }\n";
	}
}
