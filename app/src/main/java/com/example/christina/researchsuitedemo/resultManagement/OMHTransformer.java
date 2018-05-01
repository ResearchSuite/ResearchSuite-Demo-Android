package com.example.christina.researchsuitedemo.resultManagement;

import android.content.Context;

import com.curiosityhealth.ls2sdk.omh.OMHDataPoint;
import com.curiosityhealth.ls2sdk.omh.OMHIntermediateResultTransformer;

import org.researchsuite.rsrp.RSRPIntermediateResult;

import edu.cornell.tech.foundry.sdl_rsx_rsrpsupport.PAMRaw;
import edu.cornell.tech.foundry.sdl_rsx_rsrpsupport.YADLFullRaw;
import edu.cornell.tech.foundry.sdl_rsx_rsrpsupport.YADLSpotRaw;



public class OMHTransformer implements OMHIntermediateResultTransformer {
    @Override
    public OMHDataPoint transform(Context context, RSRPIntermediateResult intermediateResult) {

        if(intermediateResult.getType() == "YADLFullRaw"){
            return new YADLFullRawOMHDatapoint(context, (YADLFullRaw)intermediateResult);
        }

        if(intermediateResult.getType() == "YADLSpotRaw"){
            return new YADLSpotRawOMHDatapoint(context,(YADLSpotRaw)intermediateResult);
        }

        if(intermediateResult.getType() == "PAMRaw"){
            return new PAMRawOMHDatapoint(context,(PAMRaw)intermediateResult);
        }

        if(intermediateResult.getType() == "SurveyResult"){
            return new GenericSurveyOMHDatapoint(context,(GenericSurveyResult) intermediateResult);
        }

        else{
            return null;
        }

    }
}
