package com.example.christina.researchsuitedemo.studyManagement;

/**
 * Created by Christina on 8/11/17.
 */

import android.content.Context;
import android.support.annotation.NonNull;

import org.researchstack.backbone.ResourcePathManager;

import org.researchsuite.rstb.RSTBConsent.spi.RSTBConsentDocumentGenerator;
import org.researchsuite.rstb.RSTBConsent.spi.RSTBConsentSectionGenerator;
import org.researchsuite.rstb.RSTBConsent.spi.RSTBConsentSignatureGenerator;
import org.researchsuite.rstb.RSTBResourcePathManager;
import org.researchsuite.rstb.RSTBStateHelper;
import org.researchsuite.rstb.RSTBTaskBuilder;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by jameskizer on 4/12/17.
 */

public class RSTaskBuilderManager {

    private static RSTBTaskBuilder builder;

    /**
     * @return singleton instance
     */
    @NonNull
    public static RSTBTaskBuilder getBuilder() {
        checkState(builder != null, "CTFResultsProcessorManager has not been initialized. ");

        return builder;
    }

    public static void init(
            Context context,
            RSTBResourcePathManager resourcePathManager,
            RSTBStateHelper stateHelper
    ) {
        RSTBTaskBuilder builder = new RSTBTaskBuilder(
                context,
                resourcePathManager,
                stateHelper,
                new ArrayList<RSTBConsentDocumentGenerator>(),
                new ArrayList<RSTBConsentSectionGenerator>(),
                new ArrayList<RSTBConsentSignatureGenerator>());

        builder.getStepBuilderHelper().setDefaultResourceType(ResourcePathManager.Resource.TYPE_JSON);

        RSTaskBuilderManager.builder = builder;
    }

}