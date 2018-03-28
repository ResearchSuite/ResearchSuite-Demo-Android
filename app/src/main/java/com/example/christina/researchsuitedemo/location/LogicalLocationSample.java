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
 * Created by jameskizer on 10/2/17.
 */

public class LogicalLocationSample extends OMHDataPointBuilder {

    private String regionIdentifier;
    private RSTransitionEvent.Action action;
    private UUID datapointId;
    private OMHAcquisitionProvenance acquisitionProvenance;
    private Date creationDate;

    public LogicalLocationSample(Context context, String regionIdentifier, RSTransitionEvent.Action action, UUID uuid, long timestamp) {
        this.regionIdentifier = regionIdentifier;
        this.action = action;
        this.datapointId = uuid;
        this.acquisitionProvenance = new OMHAcquisitionProvenance(context.getPackageName(), new Date(), OMHAcquisitionProvenance.OMHAcquisitionProvenanceModality.SENSED);
        this.creationDate = new Date(timestamp);
    }

    public String getDataPointID() {
        return this.datapointId.toString();
    }

    public Date getCreationDateTime() {
        return this.creationDate;
    }

    public OMHSchema getSchema() {
        return new OMHSchema("logical-location", "cornell", "1.0");
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
        map.put("location", this.regionIdentifier);
        switch (this.action) {
            case ENTER:
                map.put("action", "enter");
                break;
            case EXIT:
                map.put("action", "exit");
                break;
            default:
                map.put("action", "unknown");
                break;
        }

        return new JSONObject(map);
    }

}
