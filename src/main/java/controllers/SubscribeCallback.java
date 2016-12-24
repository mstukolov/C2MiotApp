package controllers;

import com.google.gson.Gson;
import model.Message;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
        /*System.out.println("Message arrived. Topic: " + topic + "  Message: " + message.toString());*/

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

        String lightSwarm = (String) d.get("lightSwarm");
        Long sampleCount = (Long) d.get("sampleCount");
        Long lightValue = (Long) d.get("lightValue");

        Message msg = new Message(lightSwarm, sampleCount, lightValue);
        System.out.printf("Get message: %s,%s,%s",
                msg.getLightSwarm(), msg.getLightValue(), msg.getSampleCount());

    }
}

