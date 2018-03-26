package com.example.christina.researchsuitedemo.resultManagement;

import android.support.annotation.Nullable;

import org.researchstack.backbone.result.StepResult;
import org.researchsuite.rsrp.RSRPFrontEndServiceProvider.spi.RSRPFrontEnd;
import org.researchsuite.rsrp.RSRPIntermediateResult;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by jameskizer on 9/18/17.
 */

public class DemographicsSurveyResult extends RSRPIntermediateResult {

    public static String TYPE = "DemographicsSurvey";

//    private String travelPlans;
//    private String[] daysOnCampus;

    private String gender;
    private Integer age;
    private String zipcode;


    public DemographicsSurveyResult(UUID uuid, String taskIdentifier, UUID taskRunUUID, String gender, Integer age, String zipcode) {
        super(TYPE, uuid, taskIdentifier, taskRunUUID);
//        this.travelPlans = travelPlans;
//        this.daysOnCampus = daysOnCampus;

        this.gender = gender;
        this.age = age;
        this.zipcode = zipcode;

    }

    public String getGender() {
        return gender;
    }
    public Integer getAge(){
        return age;
    }
    public String getZip(){
        return zipcode;
    }

//    public String getTravelPlans() {
//        return travelPlans;
//    }
//
//    public String[] getDaysOnCampus() {
//        return daysOnCampus;
//    }

    public class FrontEndTransformer implements RSRPFrontEnd {

        public FrontEndTransformer() {}

        @Nullable
        public <T> T extractResult(Map<String, Object> parameters, String identifier) {

            Object param = parameters.get(identifier);
            if (param != null && (param instanceof StepResult)) {
                StepResult stepResult = (StepResult)param;
                if (stepResult.getResult() != null) {
                    return (T)stepResult.getResult();
                }
            }
            return null;
        }

        @Nullable
        @Override
        public RSRPIntermediateResult transform(String taskIdentifier, UUID taskRunUUID, Map<String, Object> parameters) {

            String gender = extractResult(parameters,"gender");
            Integer age = extractResult(parameters,"age");
            String zipcode = extractResult(parameters,"zip_code");



            DemographicsSurveyResult result = new DemographicsSurveyResult(
                    UUID.randomUUID(),
                    taskIdentifier,
                    taskRunUUID,
                    gender,
                    age,
                    zipcode
            );

            StepResult firstStepResult = (StepResult) (parameters.get("gender") != null ? parameters.get("gender") : parameters.get("employment"));

//            StepResult firstStepResult = (StepResult) (parameters.get("days_on_campus") != null ? parameters.get("days_on_campus") : parameters.get("travel_plans"));
//            StepResult lastStepResult = (StepResult) (parameters.get("travel_plans") != null ? parameters.get("travel_plans") : parameters.get("days_on_campus"));

            if (firstStepResult != null) {
                result.setStartDate(firstStepResult.getStartDate());
            }
            else {
                result.setStartDate(new Date());
            }

            if (firstStepResult != null) {
                result.setEndDate(firstStepResult.getStartDate());
            }
            else {
                result.setEndDate(new Date());
            }

            return result;
        }

        @Override
        public boolean supportsType(String type) {
            return false;
        }

    }
}
