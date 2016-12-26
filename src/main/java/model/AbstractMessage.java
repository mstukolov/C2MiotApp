package model;

/**
 * Created by Maxim on 26.12.2016.
 */
public class AbstractMessage {
    private String deviceid;

    public AbstractMessage(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }
}
