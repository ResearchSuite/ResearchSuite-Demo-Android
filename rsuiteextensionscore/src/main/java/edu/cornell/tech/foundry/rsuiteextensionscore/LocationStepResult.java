package edu.cornell.tech.foundry.rsuiteextensionscore;

/**
 * Created by Christina on 6/22/17.
 */

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;

public class LocationStepResult extends StepResult {

    private Double longitute;
    private Double latitude;
    private String userInput;
    private String address;

    public LocationStepResult(Step step) {
        super(step);

    }

    public void setLongLat(Double longitute,Double latitude){
        this.longitute = longitute;
        this.latitude = latitude;
    }

    public void setUserInput(String userInput){
        this.userInput = userInput;
    }

    public Double getLatitude(){
        return this.latitude;
    }

    public Double getLongitute(){
        return this.longitute;
    }

    public String getUserInput(){
        return this.userInput;
    }

    public void setAddress(String address) { this.address = address;}

    public String getAddress() { return this.address;}



}
