package model;

/**
 * Created by Maxim on 26.12.2016.
 */
public class Events {
    String type;
    String deviceId;
    String eventMessage;

    public Events(String type, String deviceId, String eventMessage) {
        this.type = type;
        this.deviceId = deviceId;
        this.eventMessage = eventMessage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }
}
