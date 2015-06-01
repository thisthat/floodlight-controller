# FloodLight Controller Extension

* Use weka to predict feature of the network
* Add REST interface to get more features from the controller

# REST URL
| URL        | METHOD | Output        | Description  |
| -----------|:------:|:-------------:| -----:|
| /wm/controller/topology               | GET/POST     | dot/json | Output a graph in dot|json format of the network. If no parameter is passed to the POST request, *json* is used |
| /wm/controller/topology/create        | GET          | bool  | Force the update/rebuild of the network graph |
| /wm/controller/topology/timeout       | GET/POST     | void  | Change the timeout of rebuild the network topology. Pass the ms w/ the POST request |
| /wm/controller/prediction/{stat}/json | GET          | json  | stat: *all* or *dpid* of a switch. Return the prediction info for the switches  |
| /wm/controller/prediction/{dpid}/reload | GET        | json  | Force the reload of the classifier for a switch |
| /wm/controller/info/mongoDB           | GET/POST     | json | Get/Set IP and PORT of mongoDB connection |
| /wm/controller/prediction/{dpid}/dataset | GET/POST  | json | dpid: *all* or *dpid* of a switch. Get/Set features for the dataset |

For more information about the original project, please visit: http://github.com/floodlight/

# Example REST POST

## /wm/controller/topology
```bash
curl -s http://localhost:8080/wm/controller/topology -d '{ "type" : "json" }'
curl -s http://localhost:8080/wm/controller/topology -d '{ "type" : "dot" }'
```
## /wm/controller/topology/timeout 
```bash
curl -s http://localhost:8080/wm/controller/topology/timeout -d '{ "time" : "300" }'
```
## /wm/controller/info/mongoDB 
```bash
curl -s http://localhost:8080/wm/controller/info/mongoDB -d '{ "ip" : "127.0.0.1" , "port" : "27071" }'
```
## /wm/controller/prediction/{dpid}/dataset
```bash
curl -s http://localhost:8080/wm/controller/prediction/all/dataset -d '[ { "dpid" : "00:00:00:00:00:00:00:02", "lags" : "8", "derivative" : "false", "classSize" : "800" } , { "dpid" : "00:00:00:00:00:00:00:01", "lags" : "10", "derivative" : "false", "classSize" : "1000" }]'       
curl -s http://localhost:8080/wm/controller/prediction/00:00:00:00:00:00:00:02/dataset -d '{ "lags" : "8", "derivative" : "false", "classSize" : "800" }'
```
