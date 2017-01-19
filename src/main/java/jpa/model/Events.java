package jpa.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Maxim on 09.01.2017.
 */
@Entity
@Table(name = "events")
@NamedQuery(name = "Events.getAll", query = "SELECT c from Events c")
public class Events {
    private int recid;
    private String type;
    private String deviceid;
    private String message;
    private Date date;

    @Id
    @Column(name = "recid")
    public int getRecid() {
        return recid;
    }

    public void setRecid(int recid) {
        this.recid = recid;
    }

    @Basic
    @Column(name = "date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "deviceid")
    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    @Basic
    @Column(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Events events = (Events) o;

        if (recid != events.recid) return false;
        if (type != null ? !type.equals(events.type) : events.type != null) return false;
        if (deviceid != null ? !deviceid.equals(events.deviceid) : events.deviceid != null) return false;
        if (message != null ? !message.equals(events.message) : events.message != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = recid;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (deviceid != null ? deviceid.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Events{" +
                "recid=" + recid +
                ", type='" + type + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public Events(String type, String deviceid, String message, Date date) {
        this.type = type;
        this.deviceid = deviceid;
        this.message = message;
        this.date = date;
    }

    public Events() {
    }
}
