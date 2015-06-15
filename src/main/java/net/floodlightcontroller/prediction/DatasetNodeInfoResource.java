/**
 * Created by Giovanni Liva on 02.06.15.
 * Class that handle the request of the dataset info
 */


package net.floodlightcontroller.prediction;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DatasetNodeInfoResource extends ServerResource {
    @Get("json")
    public String retrieve() throws FileNotFoundException, UnsupportedEncodingException {
        INetTopologyService service = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        String dpid = (String) getRequestAttributes().get(BindUrlWebRoutable.DPID);
        String out = "[\n";
        List<String> keys = new ArrayList<>();
        if(dpid.equals("all")){
            Map<String, PredictionHandler.PredictionNode> m = service.getPredictionStructure().getSwitches();
            for(Map.Entry<String, PredictionHandler.PredictionNode> entry : m.entrySet()) {
                //DataSetInfo ds = entry.getValue().getDatasetInfo();
                String _dpid = entry.getKey();
                keys.add(_dpid);
                //out += convert2json(_dpid,ds) + ",";
            }
            Collections.sort(keys);
            for(int i = 0; i < keys.size(); i++){
                String _dpid = keys.get(i);
                DataSetInfo ds = m.get(_dpid).getDatasetInfo();
                out += convert2json(_dpid,ds) + ",";
            }
            out = removeLastComma(out);
        }
        else {
            DataSetInfo ds = service.getPredictionStructure().getSwitch(dpid).getDatasetInfo();
            return convert2json(ds);
        }
        out += "]\n";
        return out;
    }

    /**
     * Convert the information of the dataset of a single switch into json
     * @param ds: The dataset
     * @return the convertion in json of the dataset object
     */
    public String convert2json(DataSetInfo ds){
        String out = "{\n";
        out += "\"lags\" : \"" + ds.getLags() + "\",\n";
        out += "\"derivative\" : \"" + ds.getDerivative() + "\",\n";
        out += "\"classSize\" : \"" + ds.getClassSize() + "\"\n";
        out += "}\n";
        return out;
    }
    /**
     * Convert the information of the dataset of a single switch into json
     * @param dpid: The identifier of the switch
     * @param ds: The dataset
     * @return the convertion in json of the dataset object
     */
    public String convert2json(String dpid, DataSetInfo ds){
        String out = "{\n";
        out += "\"dpid\" : \"" + dpid + "\",\n";
        out += "\"lags\" : \"" + ds.getLags() + "\",\n";
        out += "\"derivative\" : \"" + ds.getDerivative() + "\",\n";
        out += "\"classSize\" : \"" + ds.getClassSize() + "\"\n";
        out += "}\n";
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

    @Post
    public String store(String in) {
        INetTopologyService service = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        String dpid = (String) getRequestAttributes().get(BindUrlWebRoutable.DPID);
        if(dpid.equals("all")){
            try {
                JSONArray arr = new JSONArray(in);
                for (int i = 0; i < arr.length(); i++)
                {
                    JSONObject jp = arr.getJSONObject(i);
                    String _dpid = jp.getString("dpid");
                    int lags = Integer.parseInt(jp.getString("lags"));
                    int classSize = Integer.parseInt(jp.getString("classSize"));
                    boolean derivative = jp.getString("derivative").equals("true");
                    service.getPredictionStructure().getSwitch(_dpid).setDatasetInfo(lags, derivative, classSize);
                }

            }
            catch(Exception e) {
                System.out.println(e.getMessage());
                return "{ \"status\" : \"err\" }\n";
            }
        }
        else {
            try {
                JSONObject jp = new JSONObject(in);
                int lags = Integer.parseInt(jp.getString("lags"));
                int classSize = Integer.parseInt(jp.getString("classSize"));
                boolean derivative = jp.getString("derivative").equals("true");
                service.getPredictionStructure().getSwitch(dpid).setDatasetInfo(lags, derivative, classSize);
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
                return "{ \"status\" : \"err\" }\n";
            }
        }
        return "{ \"status\" : \"ok\" }\n";
    }

}
