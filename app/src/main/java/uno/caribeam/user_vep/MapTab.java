package uno.caribeam.user_vep;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;


/**
 * Created by MIB on 29/02/2016.
 */
public class MapTab extends Fragment {

    private final LatLng Dominica = new LatLng(15.3700, -61.3633);

    private GoogleMap map;
    private static final String LOG_TAG = "VEP";

    private static final String SERVICE_URL = "http://52.26.108.194/VEP/markers.php";

    private ScheduledExecutorService pollService;

    public MapTab() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_maps, container, false);

        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();


        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMapToolbarEnabled(false);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Dominica, 15));
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        setUpBGTask();
        return rootView;
    }
    @Override
    public void onPause() {
        super.onPause();
        if (pollService != null) pollService.shutdown();
        pollService = null;
    }
    private void setUpBGTask() {
        // Retrieve the city data from the web service
        // In a worker thread since it's a network operation.

        pollService = newSingleThreadScheduledExecutor();

        pollService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                final SendValuesTask checkTask = new SendValuesTask();
                checkTask.execute();

            }
        }, 5, 120, TimeUnit.SECONDS);

    }

    void createMarkersFromJson(String json) throws JSONException {
        // De-serialize the JSON string into an array of city objects
        map.clear();
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            // Create a marker for each city in the JSON data.
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            double LAT= jsonObj.getDouble("LAT");
            double LNG=jsonObj.getDouble("LNG");
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(LAT, LNG))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("BUS"));


        }
    }

    /**
     * The background task for communicating with the server - you can't
     * block the UI thread with http requests
     */
    private class SendValuesTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            Log.i("jsonUpdate", "Polling for new bus data...");
        }

        @Override
        protected String doInBackground(Void... unused) {

            HttpURLConnection conn = null;
            final StringBuilder json = new StringBuilder();
            try {
                // Connect to the web service
                URL url = new URL(SERVICE_URL);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Read the JSON data into the StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    json.append(buff, 0, read);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to service", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return json.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                createMarkersFromJson(result);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error processing JSON", e);
            }
        }
    }

}
