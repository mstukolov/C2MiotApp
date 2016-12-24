package model;

/**
 * Created by Maxim on 23.12.2016.
 */
public class Message {
    private String lightSwarm;
    private Long sampleCount;
    private Long lightValue;

    public Message(String lightSwarm, Long sampleCount, Long lightValue) {
        this.lightSwarm = lightSwarm;
        this.sampleCount = sampleCount;
        this.lightValue = lightValue;
    }

    public String getLightSwarm() {
        return lightSwarm;
    }

    public void setLightSwarm(String lightSwarm) {
        this.lightSwarm = lightSwarm;
    }

    public Long getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(Long sampleCount) {
        this.sampleCount = sampleCount;
    }

    public Long getLightValue() {
        return lightValue;
    }

    public void setLightValue(Long lightValue) {
        this.lightValue = lightValue;
    }
}
