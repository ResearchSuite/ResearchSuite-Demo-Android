package com.example.christina.researchsuitedemo.resultManagement;

import android.content.Context;
import android.support.annotation.Nullable;

import com.curiosityhealth.ls2sdk.omh.OMHAcquisitionProvenance;
import com.curiosityhealth.ls2sdk.omh.OMHDataPointBuilder;
import com.curiosityhealth.ls2sdk.omh.OMHSchema;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by jameskizer on 9/18/17.
 */

public class DemographicsSurveyOMHDatapoint extends OMHDataPointBuilder {

    private DemographicsSurveyResult surveyResult;
    private OMHAcquisitionProvenance acquisitionProvenance;

    public DemographicsSurveyOMHDatapoint(Context context, DemographicsSurveyResult surveyResult) {
        this.surveyResult = surveyResult;
        this.acquisitionProvenance = new OMHAcquisitionProvenance(context.getPackageName(), surveyResult.getStartDate(), OMHAcquisitionProvenance.OMHAcquisitionProvenanceModality.SELF_REPORTED);
    }

    public String getDataPointID() {
        return this.surveyResult.getUuid().toString();
    }

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

    public OMHSchema getSchema() {
        return new OMHSchema("rs-survey", "cornell", "1.0.0");
    }

    @Nullable
    public OMHAcquisitionProvenance getAcquisitionProvenance() {
        return this.acquisitionProvenance;
    }

    public JSONObject getBody() {
        HashMap map = new HashMap();
        map.put("gender", this.surveyResult.getGender());
        map.put("age", this.surveyResult.getAge());
        map.put("zipcode",this.surveyResult.getZip());
        return new JSONObject(map);
    }

}
