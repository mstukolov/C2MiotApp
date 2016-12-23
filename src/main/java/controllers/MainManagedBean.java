package controllers;

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
