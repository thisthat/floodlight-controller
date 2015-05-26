package net.floodlightcontroller.prediction;

import org.restlet.resource.Post;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class TopologySetTimeoutResource extends ServerResource {

	@Get("json")
    public String retrieve() throws FileNotFoundException, UnsupportedEncodingException {
		INetTopologyService pihr = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        return "{ \"timeout\" : \"" + pihr.getTimeout() + "\"}";
    }
	
	@Post
	public String store(String in) {
		int time = Integer.parseInt(in);
		INetTopologyService pihr = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        pihr.setTimeout(time);
        return "{ 'status' : 'ok' }\n";
	}
}
