package controllers;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.primefaces.model.chart.MeterGaugeChartModel;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Maxim on 23.12.2016.
 */
@ManagedBean(name="mainManagedBean")
@SessionScoped
public class MainManagedBean implements Serializable {

    private Date date1;
    private MeterGaugeChartModel meterGaugeChartModel;
    private MqttClient mqttClient;


    public MainManagedBean() {
        List<Number> intervals = new ArrayList<Number>(){
            {
                add(20);
                add(50);
                add(120);
                add(200);
            }
        };
        meterGaugeChartModel = new MeterGaugeChartModel(140, intervals);
        meterGaugeChartModel.setTitle("Weight IOT Device");
        meterGaugeChartModel.setGaugeLabel("kg.");
    }

    public void startMonitoring(){
        addMessage("Monitoring is starting....");

        startSubscribing();
        addMessage("Monitoring is started!");
    }


    public void startSubscribing(){
        //Bluemix connection
        String BROKER_URL = "tcp://omzkv9.messaging.internetofthings.ibmcloud.com:1883";
        String topic = "iot-2/type/Weight/id/WeightDevice1/evt/+/fmt/json";
        String clientId ="a:omzkv9:app1";
        String authmethod = "a-omzkv9-lzukrtsqgg";
        String authtoken = "iFUwTmeySz52iW6r-X";

        try {
            mqttClient = new MqttClient(BROKER_URL, clientId);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(authmethod);
        options.setPassword(authtoken.toCharArray());

        try {

            mqttClient.setCallback(new SubscribeCallback());
            mqttClient.connect(options);
            mqttClient.subscribe(topic);
            System.out.println("Subscriber is now listening to " + topic);

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    public void setMeterGaugeChartModel(){
        meterGaugeChartModel.setValue(getRandomValue(0, 200));
    }
    public int getRandomValue(int Low, int High){
        Random r = new Random();
        return r.nextInt(High-Low) + Low;
    }

    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public MeterGaugeChartModel getMeterGaugeChartModel() {
        return meterGaugeChartModel;
    }
}
