package com.example.christina.researchsuitedemo.studyManagement;

import android.support.annotation.NonNull;

import edu.cornell.tech.foundry.researchsuiteresultprocessor.RSRPBackEnd;
import edu.cornell.tech.foundry.researchsuiteresultprocessor.RSRPResultsProcessor;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by jameskizer on 4/12/17.
 */

public class RSResultsProcessorManager {

    private static RSRPResultsProcessor resultsProcessor;

    /**
     * @return singleton instance
     */
    @NonNull
    public static RSRPResultsProcessor getResultsProcessor() {
        checkState(resultsProcessor != null, "CTFResultsProcessorManager has not been initialized. ");
        return resultsProcessor;
    }

    public static void init(RSRPBackEnd backEnd) {
        RSRPResultsProcessor builder = new RSRPResultsProcessor(backEnd);
        RSResultsProcessorManager.resultsProcessor = builder;
    }
}
