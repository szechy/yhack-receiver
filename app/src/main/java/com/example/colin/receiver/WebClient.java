package com.example.colin.receiver;
import android.content.Context;

import com.loopj.android.http.*;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Sasawat on 11/01/2014.
 */
public class WebClient {

    String Location;

    public static AsyncHttpClient client = new AsyncHttpClient(8080);

    AsyncHttpResponseHandler rhnull = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            //fuck it ship it
            return;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            //fuck it ship it
            return;
        }
    };

    public WebClient(String location)
    {
        Location = location;
    }

    public void sendActive(Context context, ArrayList<DeviceInfo> active)
    {
        JSONObject jsonParameters = new JSONObject();
        JSONArray jsonArray  = new JSONArray();
        for(DeviceInfo deviceInfo : active)
        {
            jsonArray.put(""+deviceInfo.getMAC());
        }
        try {
            jsonParameters.put("locname", Location);
            jsonParameters.put("current_users", jsonArray);
        }catch (Exception ex)
        {
            //yolo
            return;
        }
        StringEntity entity;
        try {
             entity = new StringEntity(jsonParameters.toString());
        }catch (Exception ex)
        {
            //yolo
            return;
        }
        client.post(context, "http://104.236.63.179:8080/api/galileos", entity, "application/json", rhnull);

    }

    public void setLocation(String location)
    {
        Location = location;
    }
}
