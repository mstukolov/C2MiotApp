package model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Maxim on 23.12.2016.
 */
public class ScaleMessage extends AbstractMessage{

    private Long currentWeight;
    private Long dryWeight;

    private static LinkedList<ScaleMessage> messages = new LinkedList<ScaleMessage>();

    public ScaleMessage(String deviceid, Long currentWeight, Long dryWeight) {
        super(deviceid);
        this.currentWeight = currentWeight;
        this.dryWeight = dryWeight;
    }

    public static ScaleMessage create(String deviceid, Long currentWeight, Long dryWeight){
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

    public Long getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(Long currentWeight) {
        this.currentWeight = currentWeight;
    }

    public Long getDryWeight() {
        return dryWeight;
    }

    public void setDryWeight(Long dryWeight) {
        this.dryWeight = dryWeight;
    }
}
