package net.floodlightcontroller.prediction;

import org.json.JSONObject;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class MongoDBResource extends ServerResource {

    @Get("json")
    public String retrieve() throws FileNotFoundException, UnsupportedEncodingException {
        INetTopologyService service = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        MongoDBInfo info = service.getMongoDBConnection();
        String out = "{\n";
        out += "\"ip\" : \"" + info.getIP() + "\",\n";
        out += "\"port\" : \"" + info.getPORT() + "\"\n";
        out += "}\n";
        return out;
    }

    @Post
    public String store(String in) {
        INetTopologyService service = (INetTopologyService)getContext().getAttributes().get(INetTopologyService.class.getCanonicalName());
        try {
            JSONObject jp = new JSONObject(in);
            String ip = jp.getString("ip");
            String port = jp.getString("port");
            service.setMongoDBConnection(ip,port);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return "{ \"status\" : \"err\" }\n";
        }
        return "{ \"status\" : \"ok\" }\n";
    }

}
