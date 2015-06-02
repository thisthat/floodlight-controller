package net.floodlightcontroller.prediction;

/**
 * Test the new resource methods
 * Created by Giovanni Liva on 03.06.15.
 */

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class TestResource extends ServerResource {
    @Get("json")
    public String retrieve() throws FileNotFoundException, UnsupportedEncodingException {
        INetTopologyService service = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        return service.test();
    }

}
