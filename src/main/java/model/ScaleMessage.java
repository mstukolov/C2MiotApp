package model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Maxim on 23.12.2016.
 */
public class ScaleMessage extends AbstractMessage{

    private Float currentWeight;
    private Float dryWeight;

    public static LinkedList<ScaleMessage> messages = new LinkedList<ScaleMessage>();

    public ScaleMessage(String deviceid, Float currentWeight, Float dryWeight) {
        super(deviceid);
        this.currentWeight = currentWeight;
        this.dryWeight = dryWeight;
    }

    public static ScaleMessage create(String deviceid, Float currentWeight, Float dryWeight){
        ScaleMessage msg = new ScaleMessage(deviceid, currentWeight, dryWeight);
        messages.add(msg);
        return msg;
    }

    public static ScaleMessage getLast(){
        return messages.getLast();
    }

    public Integer getCountMessages(){
        return messages.size();
    }

    public static List<ScaleMessage> getMessages(){
        return messages;
    }

    public Float getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(Float currentWeight) {
        this.currentWeight = currentWeight;
    }

    public Float getDryWeight() {
        return dryWeight;
    }

    public void setDryWeight(Float dryWeight) {
        this.dryWeight = dryWeight;
    }
}
