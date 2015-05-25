package net.floodlightcontroller.prediction;


import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class ClassifierInfoResource extends ServerResource {
    private PredictionHandler ph;

    @Get("json")
    public String retrieve() throws FileNotFoundException, UnsupportedEncodingException {
        INetTopologyService service = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        ph = service.getPredictionStructure();
        String statType = (String) getRequestAttributes().get(BindUrlWebRoutable.PRED_STATS);
        return getInfo(statType);
    }

    private String getInfo(String stat){
        switch (stat){
            case "all": return getAllInfo();
            default: return getSwitchInfo(stat);
        }
    }

    private String getAllInfo() {
        return "";
    }

    /**
     * @return
     */
    private String getSwitchInfo(String switch){
        PredictionHandler.PredictionNode node = ph.getSwitch(switch);
        if(node == null){
            return "{}";
        }
        return "[]";
    }
}
