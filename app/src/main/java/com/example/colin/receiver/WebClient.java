package com.example.colin.receiver;
import com.loopj.android.http.*;

/**
 * Created by Sasawat on 11/01/2014.
 */
public class WebClient {

    String Location;
    int Uniqid;

    public static AsyncHttpClient client = new AsyncHttpClient();

    public WebClient(String location, int uniqid)
    {
        Location = location;
        Uniqid = uniqid;
    }
}
