package com.example.christina.researchsuitedemo.location;

import android.content.Context;
import android.support.annotation.Nullable;

import com.curiosityhealth.ls2sdk.omh.OMHAcquisitionProvenance;
import com.curiosityhealth.ls2sdk.omh.OMHDataPoint;
import com.curiosityhealth.ls2sdk.omh.OMHDataPointBuilder;
import com.curiosityhealth.ls2sdk.omh.OMHSchema;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


/**
 * Created by jameskizer on 10/9/17.
 */

public class DistanceSample extends OMHDataPointBuilder {

    private String description;
    private Float distance;
    private UUID datapointId;
    private OMHAcquisitionProvenance acquisitionProvenance;
    private Date creationDate;

    public DistanceSample(Context context, String description, Float distance) {
        this.description = description;
        this.distance = distance;
        this.datapointId = UUID.randomUUID();
        this.acquisitionProvenance = new OMHAcquisitionProvenance(context.getPackageName(), new Date(), OMHAcquisitionProvenance.OMHAcquisitionProvenanceModality.SENSED);
        this.creationDate = new Date();
    }

    public String getDataPointID() {
        return this.datapointId.toString();
    }

    public Date getCreationDateTime() {
        return this.creationDate;
    }

    public OMHSchema getSchema() {
        return new OMHSchema("distance", "cornell", "1.0");
    }

    @Nullable
    public OMHAcquisitionProvenance getAcquisitionProvenance() {
        return this.acquisitionProvenance;
    }

    public JSONObject getBody() {
        HashMap map = new HashMap();
        HashMap effectiveTimeMap = new HashMap();
        effectiveTimeMap.put("date_time", OMHDataPoint.stringFromDate(this.getCreationDateTime()));
        map.put("effective_time_frame", effectiveTimeMap);
        map.put("descrition", this.description);
        map.put("distance", this.distance);

        return new JSONObject(map);
    }

}
