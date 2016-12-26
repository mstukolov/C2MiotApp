package controllers;

import model.Events;
import model.ScaleMessage;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.primefaces.model.chart.*;
import util.DateAdapter;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Maxim on 23.12.2016.
 */
@ManagedBean(name="mainManagedBean")
@SessionScoped
public class MainManagedBean implements Serializable {

    private Date date1;
    private MeterGaugeChartModel meterGaugeChartModel;
    private MeterGaugeChartModel dryWeightMeterGaugeChartModel;

    private MqttClient mqttClient;
    private List<ScaleMessage> messages;
    private Integer countMessages;
    private LineChartModel curWeightlineModel;

    private LineChartModel model;
    private LineChartSeries series1;

    private List<Events> eventsList;

    @PostConstruct
    public void init() {
        createLineModels();
        eventsList = new LinkedList<Events>();
    }

    private void createLineModels() {
        curWeightlineModel = initLinearModel();
        curWeightlineModel.setTitle("Device Monitoring");
        curWeightlineModel.setLegendPosition("e");
        Axis yAxis = curWeightlineModel.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(200);

    }

    private LineChartModel initLinearModel() {
        model = new LineChartModel();
        series1 = new LineChartSeries();
        series1.setLabel("Device");
        model.addSeries(series1);
        model.getAxis(AxisType.Y).setLabel("Values");

        /*DateAxis axis = new DateAxis("Dates");
        axis.setTickAngle(-50);
        axis.setMax("2017-01-01 00:10:56");
        axis.setTickFormat("%H:%#M:%S");
        model.getAxes().put(AxisType.X, axis);*/

        return model;
    }


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
        meterGaugeChartModel.setTitle("Current Weight");
        meterGaugeChartModel.setGaugeLabel("kg.");

        dryWeightMeterGaugeChartModel = new MeterGaugeChartModel(140, intervals);
        dryWeightMeterGaugeChartModel.setTitle("Dry Weight");
        dryWeightMeterGaugeChartModel.setGaugeLabel("kg.");
    }

    public void startMonitoring(){
        //startSubscribing();
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

    public void updateCurrentValues(){

        /*meterGaugeChartModel.setValue(ScaleMessage.getLast().getCurrentWeight());
        dryWeightMeterGaugeChartModel.setValue(ScaleMessage.getLast().getDryWeight());
        series1.set((new Date()).getTime(), ScaleMessage.getLast().getCurrentWeight());
        */

        int curWeight = getRandomValue(0, 150);
        int dryWeight = getRandomValue(0, 50);

        if(curWeight < 20){
            eventsList.add(new Events("Warning", "ESP8266", "The water level is low. Replace Balon."));
        }
        meterGaugeChartModel.setValue(curWeight);
        dryWeightMeterGaugeChartModel.setValue(dryWeight);
        series1.set((new Date()).getTime(), curWeight);

        addMessage("Data is updated");
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
        //meterGaugeChartModel.setValue(ScaleMessage.getLast().getCurrentWeight());
        return meterGaugeChartModel;
    }
    public LineChartModel getCurWeightlineModel() {
        return curWeightlineModel;
    }

    public List<ScaleMessage> getMessages() {
        return ScaleMessage.getMessages();
    }

    public Integer getCountMessages() {
        return countMessages;
    }

    public MeterGaugeChartModel getDryWeightMeterGaugeChartModel() {
        return dryWeightMeterGaugeChartModel;
    }

    public List<Events> getEventsList() {
        return eventsList;
    }
}
