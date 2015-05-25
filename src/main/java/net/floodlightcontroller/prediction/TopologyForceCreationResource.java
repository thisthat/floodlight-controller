package net.floodlightcontroller.prediction;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class TopologyForceCreationResource extends ServerResource {
		@Get("json")
		public String retrieve() throws FileNotFoundException, UnsupportedEncodingException {
			INetTopologyService pihr = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
	       pihr.createTopology();
	       return "{ 'msg' : 'topology created!' }";
		}
	}