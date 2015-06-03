package net.floodlightcontroller.prediction;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.util.Map;

public class ExecutePrediction extends ServerResource {

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
                try {
                    String _Predicted = "";
                    if(type.equals("class")){
                        _Predicted = prediction.getSwitch(_dpid).executePredictionClassName();
                    }
                    else {
                        _Predicted = prediction.getSwitch(_dpid).executePredictionClassIndex() + "";
                    }
                    out += "{ \"DPID\" : \"" + _dpid + "\", \"prediction\" : \"" + _Predicted + "\"},";
                } catch (Exception e) {
                    return "{ \"status\" : \"err\", \"message\" : \"" + e.getMessage() + "::" +  e.getClass() + "\" }";
                }
            }
            out = removeLastComma(out);
        }
        //Signle switch
        else {
            try {
                String _Predicted = "";
                if(type.equals("class")){
                    _Predicted = prediction.getSwitch(dpid).executePredictionClassName();
                }
                else {
                    _Predicted = prediction.getSwitch(dpid).executePredictionClassIndex() + "";
                }
                return "{ \"DPID\" : \"" + dpid + "\", \"prediction\" : \"" + _Predicted + "\"}\n";
            } catch (Exception e) {
                return "{ \"status\" : \"err\", \"message\" : \"" + e.getMessage() + "\" }";
            }
        }

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

}
