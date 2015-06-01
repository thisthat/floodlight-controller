package net.floodlightcontroller.prediction;

/**
 * Created by Giovanni Liva on 02.06.15.
 * Class to store the connection info of MongoDB
 */
public class MongoDBInfo {
    private String IP = "127.0.0.1";
    private String PORT = "27017";
    public MongoDBInfo(){}
    public MongoDBInfo(String _ip){ this.IP = _ip; }
    public MongoDBInfo(String _ip, String _port){ this.IP = _ip; this.PORT = _port; }

    public String getIP(){ return IP; }
    public String getPORT(){ return PORT; }
}
