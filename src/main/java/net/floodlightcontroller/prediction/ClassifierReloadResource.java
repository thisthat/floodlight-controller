package net.floodlightcontroller.prediction;


import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;


public class ClassifierReloadResource extends ServerResource {
    private PredictionHandler ph;

    @Get("json")
    public String retrieve() throws FileNotFoundException, UnsupportedEncodingException {
        INetTopologyService service = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        ph = service.getPredictionStructure();
        String dpid = (String) getRequestAttributes().get(BindUrlWebRoutable.DPID);
        return reloadModel(dpid);
    }

    private String reloadModel(String dpid){
        //Switch exists?
        if(ph.getSwitches().containsKey(dpid)){
            PredictionHandler.PredictionNode node = ph.getSwitches().get(dpid);
            node.loadClassifierFromFile();
            return "{ \"msg\" : \"model reloaded!\"}\n";
        }
        return "{ \"msg\" : \"DPID not in memory!\"}\n";
    }


}
