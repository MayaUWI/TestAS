package uno.caribeam.user_vep;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Taxitab extends Fragment {

    ListView list;
    TextView company;
    TextView number;
    TextView specialty;

    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();

    //URL to get JSON Array
    private static String url = "http://52.26.108.194/VEP/taxilist.php";
    private static final String LOG_TAG = "VEP";


    //JSON Node Names
    private static final String TAG_ARRAY = "taxis";

    private static final String Company = "company";
    private static final String Nomber = "phone number";
    private static final String Specialty = "Island Tour ";
    private ProgressDialog pDialog;


    JSONArray taxis = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_rated, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        oslist = new ArrayList<HashMap<String, String>>();
        new SendValuesTask().execute();


    }

    private class SendValuesTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            company = (TextView) getView().findViewById(R.id.company);
            number = (TextView) getView().findViewById(R.id.number);
            specialty = (TextView) getView().findViewById(R.id.specialty);


            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();



        }

        @Override
        protected String doInBackground(Void... unused) {
            pDialog.dismiss();
            HttpURLConnection conn = null;
            final StringBuilder json = new StringBuilder();
            try {
                // Connect to the web service
                URL ur = new URL(url);
                conn = (HttpURLConnection) ur.openConnection();
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


        void createMarkersFromJson(String json) throws JSONException {

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                // Create a marker for each city in the JSON data.
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                String co = jsonObj.getString("Company");
                String no = jsonObj.getString("Number");
                String sp = jsonObj.getString("Specialty");
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(Company, co);
                map.put(Nomber, no);
                map.put(Specialty, sp);

                oslist.add(map);
            }

            list = (ListView) getView().findViewById(R.id.listView1);
            registerForContextMenu(list);

            String[] to={Company, Nomber, Specialty};
            int[]from = {R.id.company, R.id.number, R.id.specialty};

            list.setAdapter(new SimpleAdapter(getActivity().getBaseContext(),
                    oslist,
                    R.layout.tab2, to, from
            ));
            // list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                   /* String i= String.valueOf(position);
                    Toast.makeText(getActivity(), i, Toast.LENGTH_LONG).show();*/
                    HashMap <String, String> product = oslist.get(position);
                    String phone = product.get(Company);
                    Toast.makeText(getActivity(), "Company: " +phone, Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }


        protected void onPostExecute(String result) {
            try {
                createMarkersFromJson(result);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error processing JSON", e);
            }
        }
    }
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listView1) {
            Toast.makeText(getActivity(), "Held down ", Toast.LENGTH_SHORT)
                    .show();
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            menu.setHeaderTitle("Call Driver");
            menu.add(Menu.NONE, 0, 0, "Call");
        }
    }


    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        HashMap <String, String> product = oslist.get(info.position);
        String phone = product.get(Nomber);
        Toast.makeText(getActivity(), "Number: " +phone, Toast.LENGTH_SHORT)
                .show();

        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
            startActivity(intent);
        } catch (Exception e) {
            Log.e("Demo application", "Failed to invoke call", e);
        }
        return true;
    }

}
