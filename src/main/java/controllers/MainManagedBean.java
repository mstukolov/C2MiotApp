package controllers;

import jpa.crud.EventsService;
import jpa.model.Events;
import model.ScaleMessage;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.primefaces.model.chart.*;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
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
    public ScaleMessage scaleMessage;

    private Boolean isStarted;
    private boolean reboot;

    public String BROKER_URL;   // = "tcp://omzkv9.messaging.internetofthings.ibmcloud.com:1883";
    public String clientId;     // = "a:omzkv9:app1";
    public String authmethod;   // = "a-omzkv9-lzukrtsqgg";
    public String authtoken;    // = "iFUwTmeySz52iW6r-X";
    public String topic;

    //ADD MARKER ON GOOGLE MAP
    private MapModel emptyModel;
    private String title;
    private double lat;
    private double lng;

    //Primefaces Dashboard Widget
    private DashboardModel dashboardModel;

    @PostConstruct
    public void init() {
        createLineModels();
        eventsList = new LinkedList<Events>();
        scaleMessage = ScaleMessage.create("", 0f, 0f);

        meterGaugeChartModel.setValue(0);
        dryWeightMeterGaugeChartModel.setValue(0);
        series1.set((new Date()).getTime(), 0);

        isStarted = true;

        BROKER_URL = "tcp://r88jpm.messaging.internetofthings.ibmcloud.com:1883";
        topic = "iot-2/type/Myweight/id/MV1/evt/+/fmt/json";
        clientId ="a:r88jpm:app1";
        authmethod = "a-r88jpm-rkppgbamza";
        authtoken = "4JE9nayAE+!2s4o_4T";

        //ADD MARKER ON GOOGLE MAP
        title = "SmartCooler01";
        lat = 55.739732;
        lng = 37.605508;
        emptyModel = new DefaultMapModel();
        Marker marker = new Marker(new LatLng(lat, lng), title);
        emptyModel.addOverlay(marker);

        //Generate Dashboard wizard
        dashboardModel = new DefaultDashboardModel();
        DashboardColumn column1 = new DefaultDashboardColumn();
        DashboardColumn column2 = new DefaultDashboardColumn();
        DashboardColumn column3 = new DefaultDashboardColumn();

        column1.addWidget("gauge");
        column1.addWidget("chart");

        column2.addWidget("map");
        column2.addWidget("eventspanel");

        column3.addWidget("manage");

        dashboardModel.addColumn(column1);
        dashboardModel.addColumn(column2);
        dashboardModel.addColumn(column3);
    }

    public void handleReorder(DashboardReorderEvent event) {
        addMessage("Reordered");
    }
    public void sendEmail(){
        final String username = "maxim.stukolov@gmail.com";
        final String password = "carter2014!";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("maks@center2m.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("maxim.stukolov@gmail.com, io@center2m.com"));
            message.setSubject("Event trigger on Smart Cooler 01");
            message.setText("ALARM: "
                    + "\n\n \n" +
                    "It disturbs the device. Please check device manually!");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        addMessage("Email was sent Successfully");
    }
    public void getMySqlData(){

       /* EntityManager em = Persistence.createEntityManagerFactory("mySQL").createEntityManager();
        TypedQuery<jpa.model.Events> query = em.createNamedQuery("Events.getAll", jpa.model.Events.class);
        eventsList = query.getResultList();
*/

        EventsService service = new EventsService();
        eventsList = service.getAll();


        for(Events evt : eventsList){
            addMessage(evt.toString());
        }

        //em.getEntityManagerFactory().close();
        service.close();
        addMessage("MySQL Data OK");
    }

    private void createLineModels() {
        curWeightlineModel = initLinearModel();
        curWeightlineModel.setTitle("Device Monitoring");
        curWeightlineModel.setLegendPosition("e");
        curWeightlineModel.setExtender("chartExtender");
    }

    public void rebootDevice() throws MqttException {
        //Bluemix connection
        /*String BROKER_URL = "tcp://omzkv9.messaging.internetofthings.ibmcloud.com:1883";
        String clientId = "a:omzkv9:app1";
        String authmethod = "a-omzkv9-lzukrtsqgg";
        String authtoken = "iFUwTmeySz52iW6r-X";*/

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(authmethod);
        options.setPassword(authtoken.toCharArray());

        mqttClient = new MqttClient(BROKER_URL, clientId);
        mqttClient.connect(options);
        String json;

            if (reboot){
                json = "{\"rel\":1}";
            }else{
                json = "{\"rel\":0}";
            }

        String topic = "iot-2/type/Myweight/id/MV1/cmd/rele/fmt/json";

        MqttMessage message = new MqttMessage(json.getBytes());
        message.setQos(2);

        mqttClient.publish(topic, message);
        mqttClient.disconnect();

        addMessage("Device is rebooted");
    }

    private LineChartModel initLinearModel() {
        model = new LineChartModel();
        series1 = new LineChartSeries();
        series1.setLabel("Device");
        model.addSeries(series1);
        model.getAxis(AxisType.Y).setLabel("Values");
        return model;
    }


    public MainManagedBean() {
        List<Number> intervals = new ArrayList<Number>(){
            {
                add(5);
                add(10);
                add(20);
                add(40);
            }
        };
        meterGaugeChartModel = new MeterGaugeChartModel(140, intervals);
        meterGaugeChartModel.setTitle("Current Weight");
        meterGaugeChartModel.setGaugeLabel("kg.");

        dryWeightMeterGaugeChartModel = new MeterGaugeChartModel(140, intervals);
        dryWeightMeterGaugeChartModel.setTitle("Dry Weight");
        dryWeightMeterGaugeChartModel.setGaugeLabel("kg.");

        startMonitoring();
    }

    public void startMonitoring(){
        startSubscribing();
        addMessage("Monitoring is started!");
    }


    public void startSubscribing(){
        //Bluemix connection to Max Account
        /*String BROKER_URL = "tcp://omzkv9.messaging.internetofthings.ibmcloud.com:1883";
        String topic = "iot-2/type/Weight/id/WeightDevice1/evt/+/fmt/json";
        String clientId ="a:omzkv9:app1";
        String authmethod = "a-omzkv9-lzukrtsqgg";
        String authtoken = "iFUwTmeySz52iW6r-X";*/

        BROKER_URL = "tcp://r88jpm.messaging.internetofthings.ibmcloud.com:1883";
        topic = "iot-2/type/Myweight/id/MV1/evt/+/fmt/json";
        clientId ="a:r88jpm:app1";
        authmethod = "a-r88jpm-rkppgbamza";
        authtoken = "4JE9nayAE+!2s4o_4T";

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
            isStarted = true;
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    public void stopMonitoring(){
        try {
            mqttClient.disconnect();
            mqttClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        isStarted = false;
        addMessage("Service is closed");
    }

    public void updateEventsLog(){

        Float curWeight = scaleMessage.messages.getLast().getCurrentWeight();
        Float dryWeight = scaleMessage.messages.getLast().getDryWeight();
        String deviceid = scaleMessage.messages.getLast().getDeviceid();

        if (curWeight > 30) {
            sendEmail();
        }
        if(curWeight < 19){
            EventsService service = new EventsService();
            if(curWeight > 4 && curWeight < 18){

                service.add(new Events(    "Warning",
                        "SmartCooler01",
                        "Volume <= 75%",
                        new Date()));
            }
            if(curWeight < 4){
                service.add(new Events(    "Critical",
                        "SmartCooler01",
                        "Volume <= 20%",
                        new Date()));
            }
            eventsList = service.getAll();
            service.close();
        }
    }
    public void refreshCurrentValues(){
        if(isStarted) {
            Float curWeight = scaleMessage.messages.getLast().getCurrentWeight();
            Float dryWeight = scaleMessage.messages.getLast().getDryWeight();
            String deviceid = scaleMessage.messages.getLast().getDeviceid();

            meterGaugeChartModel.setValue(curWeight);
            series1.set((new Date()).getTime(), curWeight);
        }
    }

    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public MeterGaugeChartModel getMeterGaugeChartModel() {
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

    public boolean isReboot() {
        return reboot;
    }

    public void setReboot(boolean reboot) {
        this.reboot = reboot;
    }

    public MapModel getEmptyModel() {
        return emptyModel;
    }

    public void setEmptyModel(MapModel emptyModel) {
        this.emptyModel = emptyModel;
    }

    public DashboardModel getDashboardModel() {
        return dashboardModel;
    }
}
