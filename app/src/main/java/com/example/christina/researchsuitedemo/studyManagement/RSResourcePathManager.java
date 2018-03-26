package com.example.christina.researchsuitedemo.studyManagement;


import android.text.TextUtils;

import org.researchstack.backbone.ResourcePathManager;
import org.researchsuite.rstb.RSTBResourcePathManager;

/**
 * Created by jameskizer on 4/12/17.
 */

public class RSResourcePathManager extends RSTBResourcePathManager {

    public static final String BASE_PATH_JSON = "json";
    public static final String BASE_PATH_HTML = "html";

    @Override
    public String generatePath(int type, String name) {
        String dir;
        switch (type) {
            default:
                dir = null;

                break;
            case Resource.TYPE_HTML:
                dir = BASE_PATH_HTML;
                break;

            case Resource.TYPE_JSON:
                dir = BASE_PATH_JSON;
                break;
        }

        StringBuilder path = new StringBuilder();
        if (!TextUtils.isEmpty(dir)) {
            path.append(dir).append("/");
        }

        return path.append(name).append(".").append(getFileExtension(type)).toString();
    }

    @Override
    public String generatePath(String name, String assetDirectoryPath, String extension) {
        StringBuilder path = new StringBuilder();
        if (!TextUtils.isEmpty(assetDirectoryPath)) {
            path.append(assetDirectoryPath).append("/");
        }

        return path.append(name).append(".").append(extension).toString();
    }
}

