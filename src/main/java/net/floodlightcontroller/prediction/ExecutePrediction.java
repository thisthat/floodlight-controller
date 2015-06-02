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
        String out = "[\n";
        if(dpid.equals("all")){
            Map<String, PredictionHandler.PredictionNode> m = service.getPredictionStructure().getSwitches();
            //Foreach switch
            for(Map.Entry<String, PredictionHandler.PredictionNode> entry : m.entrySet()) {
                //Get the objs that contains the structures
                DataSetInfo ds = entry.getValue().getDatasetInfo();
                String _dpid = entry.getKey();
                //Get data
                String[] data = info.getSwitchLastMeasurement(_dpid, ds.getLags());
                try {
                    //Generate file
                    String path = ds.generateARFFFromData(data);
                    //Execute the prediction
                    String classPredicted = entry.getValue().executePredictionClassName(path);
                    out += "{ \"DPID\" : \"" + _dpid + "\", \"prediction\" : \"" + classPredicted + "\"},";
                } catch (Exception e) {
                    return "{ \"status\" : \"err\", \"message\" : \"" + e.getMessage() + "::" +  e.getClass() + "\" }";
                }
            }
            out = removeLastComma(out);
        }
        //Signle switch
        else {
            PredictionHandler.PredictionNode node = service.getPredictionStructure().getSwitch(dpid);
            DataSetInfo ds = node.getDatasetInfo();
            String[] data = info.getSwitchLastMeasurement(dpid, ds.getLags());
            try {
                //Generate file
                String path = ds.generateARFFFromData(data);
                //Execute the prediction
                String classPredicted = node.executePredictionClassName(path);
                return "{ \"DPID\" : \"" + dpid + "\", \"prediction\" : \"" + classPredicted + "\"}\n";
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
