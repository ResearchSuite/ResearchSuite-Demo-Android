package com.example.christina.researchsuitedemo.studyManagement;

/**
 * Created by Christina on 8/11/17.
 */

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.storage.file.SimpleFileAccess;
import org.researchstack.backbone.utils.FormatHelper;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.researchsuite.rstb.RSTBStateHelper;
import org.researchsuite.rsuiteextensionscore.RSCredentialStore;

/**
 * Created by jameskizer on 4/12/17.
 */

public class RSFileAccess extends SimpleFileAccess implements RSTBStateHelper, RSCredentialStore {

    private String pathName;

    private static String SIGNED_IN_DATE = "SIGNED_IN_DATE";
    private static String CONFIGURED_LOCATIONS = "CONFIGURED_LOCATIONS";
    private static String HOME_LOCATION = "HOME_LOCATION";

    public static RSFileAccess getInstance()
    {
        return (RSFileAccess) StorageAccess.getInstance().getFileAccess();
    }

    public RSFileAccess(String pathName)
    {
        this.pathName = pathName;
    }

    private String pathForRSTBKey(String key) {
        StringBuilder pathBuilder = new StringBuilder(this.pathName);
        pathBuilder.append("/rstb/");
        pathBuilder.append(key);

        return pathBuilder.toString();
    }

    private String pathForOSDKKey(String key) {
        StringBuilder pathBuilder = new StringBuilder(this.pathName);
        pathBuilder.append("/osdk/");
        pathBuilder.append(key);

        return pathBuilder.toString();
    }

    @Override
    public byte[] valueInState(Context context, String key) {

        if (this.dataExists(context, this.pathForRSTBKey(key))) {
            return this.readData(context, this.pathForRSTBKey(key));
        }
        else {
            return null;
        }
    }


    @Override
    public void setValueInState(Context context, String key, byte[] value) {
        this.writeData(
                context, this.pathForRSTBKey(key),
                value
        );
    }

    @Override
    public byte[] get(Context context, String key) {
        if (this.dataExists(context, this.pathForOSDKKey(key))) {
            return this.readData(context, this.pathForOSDKKey(key));
        }
        else {
            return null;
        }
    }

    @Override
    public void set(Context context, String key, byte[] value) {
        this.writeData(
                context, this.pathForOSDKKey(key),
                value
        );
    }

    @Override
    public void remove(Context context, String key) {
        if (this.dataExists(context, this.pathForOSDKKey(key))) {
            this.clearData(context, this.pathForOSDKKey(key));
        }
    }

    @Override
    public boolean has(Context context, String key) {
        return false;
    }


    private void setDateInState(Context context, String key, Date date) {
        DateFormat isoFormatter = FormatHelper.DEFAULT_FORMAT;
        String dateString = isoFormatter.format(date);

        this.setValueInState(context, key, dateString.getBytes());
    }



    @Nullable
    private Date getDateInState(Context context, String key) {
        byte[] bytes = this.valueInState(context, key);
        if (bytes == null) {
            return null;
        }

        String dateString = new String(bytes);

        try {
            return FormatHelper.DEFAULT_FORMAT.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setSignedInDate(Context context, Date date) {
        this.setDateInState(context, SIGNED_IN_DATE, date);
    }

    public String getParticipantSinceDate(Context context) {
        Date participantSinceDate = this.getDateInState(context, SIGNED_IN_DATE);
        if (participantSinceDate != null) {
            return new SimpleDateFormat("MMM d, yyyy").format(participantSinceDate);
        }
        else return "";
    }

    public void clearFileAccess(Context context) {
        File rootPath = new File(context.getFilesDir() + this.pathName);
        deleteRecursive(rootPath);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    private void setLatLngInState(Context context, String key, LatLng position) {
        this.setValueInState(context, key+ ".lat", toByteArray(position.latitude));
        this.setValueInState(context, key+ ".lng", toByteArray(position.longitude));
    }

    @Nullable
    private LatLng getLatLngInState(Context context, String key) {

        byte[] latBytes = this.valueInState(context, key+".lat");
        if (latBytes == null) {
            return null;
        }

        byte[] lngBytes = this.valueInState(context, key+".lng");
        if (lngBytes == null) {
            return null;
        }

        return new LatLng(
                toDouble(latBytes),
                toDouble(lngBytes)
        );
    }
    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public static byte[] toByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }

    public void setConfiguredLocations(Context context, Boolean configuredLocations) {
        this.setBooleanInState(context, CONFIGURED_LOCATIONS, configuredLocations);
    }

    public boolean hasConfiguredLocations(Context context) {
        Boolean value = this.getBooleanInState(context, CONFIGURED_LOCATIONS);
        if (value != null) {
            return value;
        }
        else {
            return false;
        }
    }

    public void setHomeLocation(Context context, LatLng position) {
        this.setLatLngInState(context, HOME_LOCATION, position);
    }

    public LatLng getHomeLocation(Context context) {
        return this.getLatLngInState(context, HOME_LOCATION);
    }

    private void setBooleanInState(Context context, String key, Boolean b) {
        byte[] bytes = { b ? (byte) 1 : (byte) 0};
        this.setValueInState(context, key, bytes);
    }

    @Nullable
    private Boolean getBooleanInState(Context context, String key) {
        byte[] bytes = this.valueInState(context, key);
        if (bytes == null) {
            return null;
        }

        return bytes[0] == 1;
    }

}