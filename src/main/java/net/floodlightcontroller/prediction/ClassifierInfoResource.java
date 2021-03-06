package net.floodlightcontroller.prediction;


import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
            default: return this.getSwitchInfo(stat);
        }
    }

    /**
     * Get the info for all the switches
     * @return json array
     */
    private String getAllInfo() {
        String out = "[";
        Map<String, PredictionHandler.PredictionNode> m = ph.getSwitches();
        List<String> keys = new ArrayList<>();
        for(Map.Entry<String, PredictionHandler.PredictionNode> entry : m.entrySet()) {
            String key = entry.getKey();
            keys.add(key);
        }
        Collections.sort(keys);
        for(int i = 0; i < keys.size(); i++){
            String key = keys.get(i);
            out += getSwitchInfo(key) + ",";
        }
        out = removeLastComma(out);
        out += "]\n";
        return out;
    }

    /**
     * Remove last char of a string if is a comma
     * @param str: Input String
     * @return String without a last char if it is a comma
     */
    private String removeLastComma(String str) {
        if (str.length() > 0 && str.charAt(str.length()-1)==',') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

    /**
     * Get the info for single switch
     * @return json object
     */
    private String getSwitchInfo(String dpid){
        PredictionHandler.PredictionNode node = ph.getSwitch(dpid);
        if(node == null){
            return "{}";
        }
        String out = "{";
        out += "\"dpid\" : \"" + dpid + "\",";
        out += "\"classifier\" : \"" + node.getClassifierName() + "\",";
        out += "\"index\" : \"" + node.getIndexModel() + "\",";
        out += "\"modelPath\" : \"" + node.getModelPath() + "\",";
        out += "\"loadedAt\" : \"" + node.getLoadedTimeStr() + "\",";
        out += "\"avaiableClassifiers\" : [\n";
        List<String> models = node.getAllModelsPath();
        for(int i = 0; i < models.size(); i++){
            out += "{\"" + i + "\" : \"" + models.get(i) + "\"}";
            if(i != models.size()-1){
                out += ",\n";
            }
        }
        out += "]\n";
        out += "}\n";
        return out;
    }
}
