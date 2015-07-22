package org.itri.tomato;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by heiruwu on 7/17/15.
 */
public class Utilities {
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String USER_ID = "userId";
    public static final String USER_TOKEN = "userToken";
    /**
     * For Server API
     */
    public static final String ACTION = "&action=";
    public static final String PARAMS = "&params=";
    public static final String API_URL = "http://210.61.209.197/~n100/Tomato/tomato_api.php";
    public static final String SENDER_ID = "948528150442";
    public static final String TYPE = "android";

    public static JSONObject API_CONNECT(String Action, String Params, boolean hasInput) {
        try {
            URL url = new URL(Utilities.API_URL);
            String out = Action+Params;
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(out.getBytes());
            outputStream.flush();
            outputStream.close();
            if(hasInput) {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String temp;
                while ((temp = bReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                inputStream.close();
                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                Log.i("ResponseCode",String.valueOf(httpURLConnection.getResponseCode()));
                if (httpURLConnection.getResponseCode() == 200) {
                    return jsonObject;
                } else {
                    return null;
                }
            } else {
                return  null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
