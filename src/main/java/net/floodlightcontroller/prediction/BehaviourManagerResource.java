package net.floodlightcontroller.prediction;

import org.json.JSONObject;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;


public class BehaviourManagerResource extends ServerResource {

    @Get("json")
    public String retrieve() {
        INetTopologyService pihr = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        return "{ \"timeout\" : \"" + pihr.getBehaviourStructure().getSleepTime() + "\"}\n";
    }

    @Post
    public String store(String in) {
        INetTopologyService service = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        try {
            JSONObject jp = new JSONObject(in);
            String time = jp.getString("time");
            service.getBehaviourStructure().setSleepTime(Integer.parseInt(time));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return "{ \"status\" : \"err\" }\n";
        }
        return "{ \"status\" : \"ok\" }\n";

    }
}