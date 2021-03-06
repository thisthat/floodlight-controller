package net.floodlightcontroller.prediction;

import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;
 
public class BindUrlWebRoutable implements RestletRoutable {

    public static final String PRED_STATS = "pred_stats";
    public static final String DPID = "dpid";
    public static final String TYPE = "type";

    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/topology", TopologyGraphResource.class);
        router.attach("/topology/create", TopologyForceCreationResource.class);
        router.attach("/topology/timeout", TopologySetTimeoutResource.class);
        router.attach("/info/mongoDB", MongoDBResource.class);
        router.attach("/prediction/{" + DPID + "}/dataset", DatasetNodeInfoResource.class);
        router.attach("/prediction/{" + PRED_STATS + "}/json", ClassifierInfoResource.class);
        router.attach("/prediction/{" + DPID + "}/reload", ClassifierReloadResource.class);
        router.attach("/prediction/{" + DPID + "}/{" + TYPE + "}/execute", ExecutePrediction.class);
        router.attach("/prediction/{" + DPID + "}/{" + TYPE + "}/execute/all", ExecutePredictionAll.class);
        router.attach("/behaviour/time", BehaviourManagerResource.class);
        return router;
    }
 
    @Override
    public String basePath() {
        return "/wm/controller";
    }
}