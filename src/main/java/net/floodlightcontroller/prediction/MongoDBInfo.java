package net.floodlightcontroller.prediction;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

/**
 * Created by Giovanni Liva on 02.06.15.
 * Class to store the connection info of MongoDB
 */
public class MongoDBInfo {

    /**
     * Constant List w/ the names of collection in MongoDB Server
     */
    public static String DataTime = "DataTime";
    public static String SwitchFlowData = "SwitchFlowData";

    /**
     * Connection Variables
     */
    private String IP = "192.168.56.1";
    private String PORT = "27017";
    private String dbname = "FloodLight";
    private MongoDatabase db;
    private MongoClient client;

    /**
     * Collection vars
     */
    private MongoCollection<Document> datatimeCollection;
    private MongoCollection<Document> switchflowdataCollection;

    /**
     * Different Constructors
     */
    public MongoDBInfo(){}
    public MongoDBInfo(String _ip){ this.IP = _ip; }
    public MongoDBInfo(String _ip, String _port){ this.IP = _ip; this.PORT = _port; }
    public MongoDBInfo(String _ip, String _port, String _dbname){ this.IP = _ip; this.PORT = _port; this.dbname = _dbname; }

    /**
     * Getter
     * @return IP of the MongoDB Server
     */
    public String getIP(){ return IP; }
    /**
     * Getter
     * @return PORT of the MongoDB Server
     */
    public String getPORT(){ return PORT; }
    /**
     * Getter
     * @return DB NAME of the MongoDB Server
     */
    public String getDBName(){ return dbname; }

    /**
     * Setter
     * @param _ip: IP of MongoDB Server
     */
    public void setIP(String _ip){
        this.IP = _ip;
    }
    /**
     * Setter
     * @param _port: PORT of MongoDB Server
     */
    public void setPORT(String _port){
        this.PORT =_port;
    }
    /**
     * Setter
     * @param _dbname: DB NAME of MongoDB Server
     */
    public void setDBName(String _dbname){
        this.dbname = _dbname;
    }

    /**
     * Get the url to connect to MongoDB Server
     * @return url structure of mongo
     */
    public MongoClientURI getUrlConnection() {
        return new MongoClientURI("mongodb://" + this.IP + ":" + this.PORT);// + "/" + this.dbname;
    }

    /**
     * Connect to the MongoDB Server
     */
    public void connect(){
        if(client != null) {
            client.close();
        }
        System.out.println("Try to connecting to mongo...");
        try{
            client = new MongoClient(this.getUrlConnection());
        }
        catch(Exception e){
            System.out.println("Error..." + e.getMessage());
            db = null;
            datatimeCollection = null;
            switchflowdataCollection = null;
            return;
        }
        System.out.println("DONE");
        db = client.getDatabase(this.dbname);
        datatimeCollection = db.getCollection(this.DataTime);
        switchflowdataCollection = db.getCollection(this.SwitchFlowData);
    }

    /**
     * Check if the server is still on
     * @return true if the server is on, otherwise false
     */
    public boolean isConnected(){
         return client.getAddress() != null;
    }

    /**
     * Not really useful, just a test method to show how export data from the DB
     * @return return all the DateTime info in json format
     */
    public String jsonDatatime(){
        String out = "[";
        MongoCursor<Document> cursor = datatimeCollection.find().iterator();
        try {
            while (cursor.hasNext()) {
                out += cursor.next().toJson();
                if(cursor.hasNext()){
                    out += ",\n";
                }
            }
        }
        catch(Exception e){
            out = e.getMessage();
        }
        finally {
            cursor.close();
            out += "]";
        }
        return out;
    }

    /**
     * Get the last <b>n</b> measurement of load of the <b>dpid</b> switch
     * @param dpid : Switch Identifier
     * @param n : Number of how many item retrive
     * @param revert : Defines if present the result in inverse order
     * @return an array of n element with the measurement
     */
    public String[] getSwitchLastMeasurement(String dpid, int n, boolean revert){
        if( n < 1 ){
            return new String[0];
        }

        String[] out = new String[n];
        //Increase by One so we can do the subtraction and have exactly n element
        String[] dt = getLastDataTime(n+1,revert);
        int j = 0;
        int prevByte = 0;
        for(int i = 0; i < dt.length; i++){
            MongoCursor<Document> cursor = switchflowdataCollection.
                    find(
                         Filters.and(
                             Filters.eq("DPID", dpid),
                             Filters.eq("_time", Integer.parseInt(dt[i]))
                         )
                    )
                    .sort(Sorts.descending("_time"))
                    .limit(n)
                    .iterator();
            try {
                int _byte = 0;
                while (cursor.hasNext()) {
                    String byteCount = cursor.next().get("byteCount").toString();
                    _byte += Integer.parseInt(byteCount);
                }
                if(i > 0) { //Skip the first
                    out[j++] = (_byte - prevByte) + "";
                }
                prevByte = _byte;
            }
            catch(Exception e){
                out[0] = e.getMessage();
            }
            finally {
                cursor.close();
            }
        }

        return out;
    }
    /**
     * Get the last <b>n</b> measurement of load of the <b>dpid</b> switch
     * @param dpid : Switch Identifier
     * @param n : Number of how many item retrive
     * @return an array of n element with the measurement
     */
    public String[] getSwitchLastMeasurement(String dpid, int n){
        return getSwitchLastMeasurement(dpid, n, false);
    }

    /**
     * Get the last <b>n</b> DataTime values
     * @param n
     * @return Array of <b>n</b> elements with the values of last time measurements
     */
    public String[] getLastDataTime(int n){
        if( n < 1 ){
            return new String[0];
        }
        String[] out = new String[n];
        int i = 0;
        MongoCursor<Document> cursor = datatimeCollection
                .find()
                .sort(Sorts.descending("_time"))
                .limit(n)
                .iterator();
        try {
            while (cursor.hasNext()) {
                out[i++] = cursor.next().get("_time").toString();
            }
        }
        catch(Exception e){
            out[0] = e.getMessage();
        }
        finally {
            cursor.close();
        }
        return out;
    }

    /**
     * Get the last <b>n</b> DataTime values
     * @param n
     * @param revert : Defines if reverse the order of output
     * @return Array of <b>n</b> elements with the values of last time measurements
     */
    public String[] getLastDataTime(int n, boolean revert){
        if(!revert)
            return getLastDataTime(n);
        String[] tmp = getLastDataTime(n);
        String[] out = new String[tmp.length];
        int j = 0;
        for(int i = tmp.length - 1; i >= 0; i--){
            out[j++] = tmp[i];
        }
        return out;
    }

}
