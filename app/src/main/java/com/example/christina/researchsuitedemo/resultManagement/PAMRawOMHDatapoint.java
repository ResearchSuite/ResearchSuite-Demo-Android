package com.example.christina.researchsuitedemo.resultManagement;

import android.content.Context;
import android.support.annotation.Nullable;

import com.curiosityhealth.ls2sdk.omh.OMHAcquisitionProvenance;
import com.curiosityhealth.ls2sdk.omh.OMHDataPoint;
import com.curiosityhealth.ls2sdk.omh.OMHDataPointBuilder;
import com.curiosityhealth.ls2sdk.omh.OMHSchema;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.cornell.tech.foundry.sdl_rsx_rsrpsupport.PAMRaw;

/**
 * Created by christinatsangouri on 3/26/18.
 */

public class PAMRawOMHDatapoint extends OMHDataPointBuilder {


    private PAMRaw pamRaw;
    private OMHAcquisitionProvenance acquisitionProvenance;

    public PAMRawOMHDatapoint(Context context, PAMRaw pamRaw) {
        this.pamRaw = pamRaw;
        this.acquisitionProvenance = new OMHAcquisitionProvenance(
                context.getPackageName(),
                pamRaw.getStartDate(),
                OMHAcquisitionProvenance.OMHAcquisitionProvenanceModality.SELF_REPORTED
        );
    }

    @Override
    public String getDataPointID() {
        return this.pamRaw.getUuid().toString();
    }

    @Override
    public Date getCreationDateTime() {
        return this.pamRaw.getStartDate() != null ? this.pamRaw.getStartDate() : new Date();
    }

    @Override
    public OMHSchema getSchema() {

        return new OMHSchema(
                "photographic-affect-meter-scores",
                "cornell",
                "1.0.0"
        );
    }

    @Nullable
    @Override
    public OMHAcquisitionProvenance getAcquisitionProvenance() {
        return this.acquisitionProvenance;
    }

    @Override
    public JSONObject getBody() {
        Map<String, Serializable> map = new HashMap<>(this.pamRaw.getPamChoice());
        HashMap<String, Serializable> effectiveTimeMap = new HashMap<>();
        effectiveTimeMap.put("date_time", OMHDataPoint.stringFromDate(this.getCreationDateTime()));
        map.put("effective_time_frame", effectiveTimeMap);
        return new JSONObject(map);
    }

}
