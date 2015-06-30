package net.floodlightcontroller.prediction;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.util.List;
import java.util.Map;

public class ExecutePredictionAll extends ServerResource {

    @Get("json")
    public String retrieve() {
        INetTopologyService service = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        MongoDBInfo info = service.getMongoDBConnection();
        PredictionHandler prediction = service.getPredictionStructure();
        String dpid = (String) getRequestAttributes().get(BindUrlWebRoutable.DPID);
        String type = (String) getRequestAttributes().get(BindUrlWebRoutable.TYPE);
        String out = "[\n";
        if(dpid.equals("all")){
            Map<String, PredictionHandler.PredictionNode> m = service.getPredictionStructure().getSwitches();
            //Foreach switch
            for(Map.Entry<String, PredictionHandler.PredictionNode> entry : m.entrySet()) {
                String _dpid = entry.getKey();
                out += generateSingleNode(_dpid, type, prediction) + ",";
            }
            out = removeLastComma(out);
        }
        //Signle switch
        else {
            out += generateSingleNode(dpid, type, prediction);
        }
        out += "]\n";
        return out;
    }

    private String generateSingleNode(String _dpid, String type,PredictionHandler prediction){
        String out = "";
        try {
            List<String> _Predicted;
            PredictionHandler.PredictionNode sw = prediction.getSwitch(_dpid);
            if(type.equals("class")){
                _Predicted = sw.executePredictionListClassName();
            }
            else {
                _Predicted = sw.executePredictionListIndex();
            }
            out += "{ \"DPID\" : \"" + _dpid
                    + "\", \"ClassSize\" : \"" + sw.getDatasetInfo().getClassSize()
                    + "\", \"ValueList\" : " + serializeList2Json(sw.getLastData())
                    + ", \"PredictionList\" : " + serializeList2Json(_Predicted) + "}";
        } catch (Exception e) {
            return "{ \"status\" : \"err\", \"message\" : \"" + e.getMessage() + "::" +  e.getClass() + "\" }";
        }
        return out;
    }

    private String serializeList2Json(List<String> l){
        String out = "[";
        for(int i = 0; i < l.size(); i++){
            out += "\"" + l.get(i) + "\",";
        }
        out = removeLastComma(out);
        out += "]";
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

}
