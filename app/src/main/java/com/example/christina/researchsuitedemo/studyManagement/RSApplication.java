package com.example.christina.researchsuitedemo.studyManagement;

/**
 * Created by Christina on 8/11/17.
 */

import android.app.Application;
import android.content.Context;

import com.curiosityhealth.ls2sdk.core.manager.LS2Manager;
import com.curiosityhealth.ls2sdk.rsrp.LS2ResultBackend;
import com.curiosityhealth.ls2sdk.omh.OMHIntermediateResultTransformer;
import com.example.christina.researchsuitedemo.MainActivity;
import com.example.christina.researchsuitedemo.R;

import net.sqlcipher.database.SQLiteDatabase;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.database.sqlite.SqlCipherDatabaseHelper;
import org.researchstack.backbone.storage.database.sqlite.UpdatablePassphraseProvider;
import org.researchstack.backbone.storage.file.UnencryptedProvider;
import org.researchstack.skin.DataResponse;
import org.researchsuite.rstb.RSTBStateHelper;

import com.example.christina.researchsuitedemo.resultManagement.OMHTransformer;
import com.example.christina.researchsuitedemo.notificationManagement.NotificationTime;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import rx.Single;
import rx.SingleSubscriber;


public class RSApplication extends Application {

    //public static float GEOFENCE_RADIUS = 300.0f;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate()
    {
        super.onCreate();
        this.initializeSingletons(this);
    }



    public void initializeSingletons(Context context) {

        //TODO: Change to pin encrypted
        UnencryptedProvider encryptionProvider = new UnencryptedProvider();

        AppDatabase dbAccess = createAppDatabaseImplementation(context);
        dbAccess.setEncryptionKey(encryptionProvider.getEncrypter().getDbKey());

        RSFileAccess fileAccess = createFileAccessImplementation(context);
        fileAccess.setEncrypter(encryptionProvider.getEncrypter());

        StorageAccess.getInstance().init(
                null,
                new UnencryptedProvider(),
                fileAccess,
                createAppDatabaseImplementation(context)
        );

        LS2Manager.config(context,
                getString(R.string.ls2_base_url),
                fileAccess,
                getString(R.string.ls2_queue_directory)
        );


        String directory = context.getApplicationInfo().dataDir;



        RSResourcePathManager resourcePathManager = new RSResourcePathManager();
        ResourcePathManager.init(resourcePathManager);
//        //config task builder singleton
//        //task builder requires ResourceManager, ImpulsivityAppStateManager
        RSTaskBuilderManager.init(context, resourcePathManager, fileAccess);
        LS2ResultBackend.config(LS2Manager.getInstance(), this.getOMHIntermediateResultTransformers());
        RSResultsProcessorManager.init(LS2ResultBackend.getInstance());


    }

    public List<OMHIntermediateResultTransformer> getOMHIntermediateResultTransformers() {
        ArrayList<OMHIntermediateResultTransformer> transformers = new ArrayList<>();

        transformers.add(new OMHTransformer());

        return transformers;
    }


    public void resetSingletons(Context context) {



        this.initializeSingletons(context);

    }



    protected RSFileAccess createFileAccessImplementation(Context context)
    {
        String pathName = "/rsuite";
        return new RSFileAccess(pathName);
    }

    protected AppDatabase createAppDatabaseImplementation(Context context) {
        SQLiteDatabase.loadLibs(context);

        return new SqlCipherDatabaseHelper(
                context,
                SqlCipherDatabaseHelper.DEFAULT_NAME,
                null,
                SqlCipherDatabaseHelper.DEFAULT_VERSION,
                new UpdatablePassphraseProvider()
        );
    }

    public Single<DataResponse> signOut(Context context) {

        return Single.create(new Single.OnSubscribe<DataResponse>() {
            @Override
            public void call(final SingleSubscriber<? super DataResponse> singleSubscriber) {

                LS2Manager.getInstance().signOut(new LS2Manager.Completion() {
                    @Override
                    public void onCompletion(Exception e) {

                        RSFileAccess.getInstance().clearFileAccess(RSApplication.this);

                        //clear notification, too!!
                        NotificationTime notificationTime = new NotificationTime(RSApplication.this);
                        notificationTime.setNotificationTime(null, null);

                        if (e != null) {
                            singleSubscriber.onError(e);
                        }
                        else {
                            singleSubscriber.onSuccess(new DataResponse(true, "success"));
                        }

                    }
                });

            }
        });

    }

    public void initializeGeofenceManager() {
        RSTBStateHelper stateHelper = RSTaskBuilderManager.getBuilder().getStepBuilderHelper().getStateHelper();

        double homeLat = 0;
        double homeLng = 0;
        double workLat = 0;
        double workLng = 0;

        byte[] homeLatByte = stateHelper.valueInState(this, "latitude_home");
        byte[] homeLngByte = stateHelper.valueInState(this, "longitude_home");
        byte[] workLatByte = stateHelper.valueInState(this, "latitude_work");
        byte[] workLngByte = stateHelper.valueInState(this, "longitude_work");

        if (homeLatByte != null && homeLngByte != null && workLatByte != null && workLngByte != null) {
            try {
                String homeLatString = new String(homeLatByte, "UTF-8");
                String homeLngString = new String(homeLngByte, "UTF-8");
                String workLatString = new String(workLatByte, "UTF-8");
                String workLngString = new String(workLngByte, "UTF-8");

                homeLat = Double.parseDouble(homeLatString);
                homeLng = Double.parseDouble(homeLngString);
                workLat = Double.parseDouble(workLatString);
                workLng = Double.parseDouble(workLngString);

                RSGeofenceManager.init(this, homeLat, homeLng, workLat, workLng);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            RSGeofenceManager.init(this, homeLat, homeLng, workLat, workLng);
        }
    }


    @Override
    protected void attachBaseContext(Context base)
    {
        // This is needed for android versions < 5.0 or you can extend MultiDexApplication
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

}
