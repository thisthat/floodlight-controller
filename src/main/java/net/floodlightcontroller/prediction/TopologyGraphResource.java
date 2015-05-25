package net.floodlightcontroller.prediction;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class TopologyGraphResource extends ServerResource {
	@Get("json")
    public String retrieve() throws FileNotFoundException, UnsupportedEncodingException {
		INetTopologyService pihr = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        return pihr.getTopologyGraph("json");
    }
	
	@Post
	public String store(String in) {
		INetTopologyService pihr = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        return pihr.getTopologyGraph(in);
	}
	
}
