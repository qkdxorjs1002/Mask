package kr.publicm.mask;

import android.os.AsyncTask;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetJSONTask extends AsyncTask<Void, Void, JSONObject> {
    private String method, target;

    public GetJSONTask(String method, String target) {
        this.method = method;
        this.target = target;
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        JSONObject json = null;

        try{
            URL url = new URL(target);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("content-type", "application/json");
            connection.setRequestMethod(method);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(10000);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = stream.readLine()) != null) {
                    response.append(inputLine);
                }
                stream.close();
                json = new JSONObject(new JSONTokener(response.toString()));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return json;
    }
}
