package com.example.christina.researchsuitedemo.resultManagement;

import android.content.Context;
import android.support.annotation.Nullable;

import com.curiosityhealth.ls2sdk.omh.OMHAcquisitionProvenance;
import com.curiosityhealth.ls2sdk.omh.OMHDataPointBuilder;
import com.curiosityhealth.ls2sdk.omh.OMHSchema;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.cornell.tech.foundry.sdl_rsx_rsrpsupport.YADLFullRaw;

/**
 * Created by jameskizer on 3/15/18.
 */

public class YADLFullRawOMHDatapoint extends OMHDataPointBuilder {

    private YADLFullRaw yadlFullRaw;
    private OMHAcquisitionProvenance acquisitionProvenance;

    public YADLFullRawOMHDatapoint(Context context, YADLFullRaw yadlFullRaw) {
        this.yadlFullRaw = yadlFullRaw;
        this.acquisitionProvenance = new OMHAcquisitionProvenance(
                OMHAcquisitionProvenance.defaultSourceName(context),
                yadlFullRaw.getStartDate(),
                OMHAcquisitionProvenance.OMHAcquisitionProvenanceModality.SELF_REPORTED
        );
    }

    @Override
    public String getDataPointID() {
        return this.yadlFullRaw.getUuid().toString();
    }

    @Override
    public Date getCreationDateTime() {
        return this.yadlFullRaw.getStartDate() != null ? this.yadlFullRaw.getStartDate() : new Date();
    }

    @Override
    public OMHSchema getSchema() {

        Map<String, Object> schemaID = this.yadlFullRaw.getSchemaID();

        if (schemaID.get("name") != null && (schemaID.get("name") instanceof String) &&
                schemaID.get("namespace") != null && (schemaID.get("namespace") instanceof String) &&
                schemaID.get("version") != null && (schemaID.get("version") instanceof String)
                ) {
            return new OMHSchema(
                    (String)schemaID.get("name") ,
                    (String)schemaID.get("namespace"),
                    (String)schemaID.get("version")
            );
        }

        return new OMHSchema(
                "yadl-full-assessment",
                "Cornell",
                "2.0"
        );
    }

    @Nullable
    @Override
    public OMHAcquisitionProvenance getAcquisitionProvenance() {
        return this.acquisitionProvenance;
    }

    @Override
    public JSONObject getBody() {
        Map<String, Object> map = new HashMap<>();
        map.put("selections", this.yadlFullRaw.getResultMap());
        return new JSONObject(map);
    }
}
