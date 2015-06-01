package net.floodlightcontroller.prediction;

import org.json.JSONObject;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class TopologyGraphResource extends ServerResource {
	@Get("json")
    public String retrieve() throws FileNotFoundException, UnsupportedEncodingException {
		INetTopologyService pihr = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        return pihr.getTopologyGraph("json");
    }
	
	@Post
	public String store(String in) {
		INetTopologyService service = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
		try {
			JSONObject jp = new JSONObject(in);
			String type = jp.getString("type");
			return service.getTopologyGraph(type);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			return "{ \"status\" : \"err\" }\n";
		}
	}
	
}
