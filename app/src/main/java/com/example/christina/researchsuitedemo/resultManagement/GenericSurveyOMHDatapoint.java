package com.example.christina.researchsuitedemo.resultManagement;

import android.content.Context;
import android.support.annotation.Nullable;

import com.curiosityhealth.ls2sdk.omh.OMHAcquisitionProvenance;
import com.curiosityhealth.ls2sdk.omh.OMHDataPointBuilder;
import com.curiosityhealth.ls2sdk.omh.OMHSchema;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by christinatsangouri on 5/1/18.
 */

public class GenericSurveyOMHDatapoint extends OMHDataPointBuilder {

    private GenericSurveyResult surveyResult;
    private OMHAcquisitionProvenance acquisitionProvenance;

    public GenericSurveyOMHDatapoint(Context context, GenericSurveyResult surveyResult) {
        this.surveyResult = surveyResult;
        this.acquisitionProvenance = new OMHAcquisitionProvenance(context.getPackageName(), surveyResult.getStartDate(), OMHAcquisitionProvenance.OMHAcquisitionProvenanceModality.SELF_REPORTED);
    }

    @Override
    public String getDataPointID() {
        return this.surveyResult.getUuid().toString();
    }

    @Override
    public Date getCreationDateTime() {
        if (this.surveyResult.getStartDate() != null) {
            return this.surveyResult.getStartDate();
        }
        else if (this.surveyResult.getEndDate() != null) {
            return this.surveyResult.getEndDate();
        }
        else {
            return new Date();
        }
    }

    @Override
    public OMHSchema getSchema() {
        return new OMHSchema("rs-survey", "cornell", "1.0.0");
    }

    @Nullable
    @Override
    public OMHAcquisitionProvenance getAcquisitionProvenance() {
        return this.acquisitionProvenance;
    }

    @Override
    public JSONObject getBody() {
        return new JSONObject(this.surveyResult.getSurveyResults());
    }
}
