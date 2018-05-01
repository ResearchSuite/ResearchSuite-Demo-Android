package com.example.christina.researchsuitedemo.resultManagement;

import android.support.annotation.Nullable;

import org.researchstack.backbone.result.StepResult;
import org.researchsuite.rsrp.RSRPFrontEndServiceProvider.spi.RSRPFrontEnd;
import org.researchsuite.rsrp.RSRPIntermediateResult;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by christinatsangouri on 5/1/18.
 */

public class GenericSurveyResultTransformer  implements RSRPFrontEnd {

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

        String firstKey;

        HashMap<String, Object> map = new HashMap<String, Object>();
        for (String key : parameters.keySet()) {
            map.put(key, extractResult(parameters,key));

        }


        GenericSurveyResult result = new GenericSurveyResult(
                UUID.randomUUID(),
                taskIdentifier,
                taskRunUUID,
                map
        );

//        StepResult firstStepResult = (StepResult) (parameters.get(firstKey) != null ? parameters.get("demography1") : parameters.get("demography1"));

        StepResult firstStepResult = null;

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
        return GenericSurveyResult.TYPE.equals(type);
    }
}
