# FloodLight Controller Extension

* Use weka to predict feature of the network
* Add REST interface to get more features from the controller

# REST URL
| URL        | METHOD | Output        | Description  |
| -----------|:------:|:-------------:| -----:|
| /wm/controller/topology               | GET/POST     | dot/json | Output a graph in dot|json format of the network. If no parameter is passed to the POST request, *json* is used |
| /wm/controller/topology/create        | GET          | bool  | Force the update/rebuild of the network graph |
| /wm/controller/topology/timeout       | POST         | void  | Change the timeout of rebuild the network topology. Pass the ms w/ the POST request |
| /wm/controller/prediction/{stat}/json | GET          | json  | stat: *all* or *dpid* of a switch. Return the prediction info for the switches  |
| /wm/controller/prediction/{dpid}/reload | GET          | json  | Force the reload of the classifier for a switch |

For more information about the original project, please visit: http://github.com/floodlight/