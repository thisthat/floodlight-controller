package net.floodlightcontroller.prediction;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class TopologyForceCreationResource extends ServerResource {
	@Get("json")
	public String retrieve() throws FileNotFoundException, UnsupportedEncodingException {
		INetTopologyService pihr = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
	   pihr.createTopology();
	   return "{ 'msg' : 'topology created!' }";
	}
}