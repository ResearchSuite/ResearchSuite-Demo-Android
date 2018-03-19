package com.example.christina.researchsuitedemo.studyManagement;

import com.google.gson.JsonElement;

import java.util.List;

import edu.cornell.tech.foundry.researchsuiteresultprocessor.RSRPResultTransform;

/**
 * Created by jameskizer on 1/19/17.
 */
public class CTFScheduleItem {

    public String type;
    public String identifier;
    public String title;
    public String guid;
    public JsonElement activity;
    public List<RSRPResultTransform> resultTransforms;

}
