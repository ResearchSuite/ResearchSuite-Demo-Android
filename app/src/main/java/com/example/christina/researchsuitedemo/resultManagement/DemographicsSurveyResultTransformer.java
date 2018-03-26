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

public class DemographicsSurveyResultTransformer implements RSRPFrontEnd {

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

        StepResult firstStepResult = (StepResult) (parameters.get("gender") != null ? parameters.get("gender") : parameters.get("zipcode"));
        //StepResult lastStepResult = (StepResult) (parameters.get("employment") != null ? parameters.get("employment") : parameters.get("gender"));

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
        return DemographicsSurveyResult.TYPE.equals(type);
    }

}
