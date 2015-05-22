package net.floodlightcontroller.pktinhistory;
 
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;
 
import net.floodlightcontroller.restserver.RestletRoutable;
 
public class PktInHistoryWebRoutable implements RestletRoutable {
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/topology", TopologyGraphResource.class);
        router.attach("/topology/create", TopologyForceCreationResource.class);
        router.attach("/topology/timeout", TopologySetTimeoutResource.class);
        return router;
    }
 
    @Override
    public String basePath() {
        return "/wm/controller";
    }
}