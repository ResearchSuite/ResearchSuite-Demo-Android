package com.example.christina.researchsuitedemo.resultManagement;

import org.researchsuite.rsrp.RSRPIntermediateResult;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by christinatsangouri on 5/1/18.
 */

public class GenericSurveyResult extends RSRPIntermediateResult {

    public static String TYPE = "SurveyResult";

    private HashMap surveyResults;

    public GenericSurveyResult(UUID uuid, String taskIdentifier, UUID taskRunUUID, HashMap<String, Object> surveyResults) {
        super(TYPE, uuid, taskIdentifier, taskRunUUID);

        this.surveyResults = surveyResults;

    }


    public HashMap getSurveyResults() {
        return this.surveyResults;
    }


}
