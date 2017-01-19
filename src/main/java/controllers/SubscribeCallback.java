package controllers;

import com.mongodb.*;
import model.ScaleMessage;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.UnknownHostException;

/**
 * Created by Maxim on 20.12.2016.
 */
public class SubscribeCallback implements MqttCallback {
    @Override
    public void connectionLost(Throwable cause) {
        //This is called when the connection is lost. We could reconnect here.
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        parseJsonReply(message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //no-op
    }

    public void parseJsonReply(String message){

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(message);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject d= (JSONObject) jsonObject.get("d");

        String deviceid = (String) d.get("deviceid");
        Float currentWeight = Float.valueOf((String)d.get("currentWeight"));
        Float dryWeight = Float.valueOf((String) d.get("dryWeight"));

        System.out.printf("Get message: %s,%s,%s\n", deviceid, currentWeight, dryWeight);

        Float physCurWeight = (currentWeight - dryWeight)/21880L;
        Float physDryWeight = dryWeight/-21880L;

        ScaleMessage msg = ScaleMessage.create(deviceid, physCurWeight, physDryWeight);

        //Writing data to Mongo Database
        /*try
        {
             saveMessageToMongoDB(msg);
        }
        catch (UnknownHostException ex){
            System.out.println("Error writing to MongoDB: " + ex.getMessage());
        } finally {
            System.out.println("Message is processed");
        }*/

    }

    public void saveMessageToMongoDB(ScaleMessage message) throws UnknownHostException {
        MongoClient mongo = new MongoClient("localhost", 27017);
        DB db = mongo.getDB("mqtt");
        DBCollection col = db.getCollection("messages");
        DBObject msg = createDBObject(message);
        WriteResult result = col.insert(msg);

    }
    private static DBObject createDBObject(ScaleMessage scaleMessage) {
        BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();
        docBuilder.append("deviceid", scaleMessage.getDeviceid());
        docBuilder.append("currentWeight", scaleMessage.getCurrentWeight());
        docBuilder.append("dryWeight", scaleMessage.getDryWeight());
        return docBuilder.get();
    }
}

