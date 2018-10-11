package uno.caribeam.user_vep;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MIB on 29/02/2016.
 */
public class Ratetab extends Fragment {

    private String[] village = {"Mahaut to Canefield", "Roseau to Portsmouth", "Layou to Tarrou", "St Joseph to Roseau", "Kingshill to Roseau",
             "Pitite Savanne to Point Casse", "Woodford Hill to Portmouth", "Roseau to Rosallie", "Bathestate to St Aroma", "Emshall to Roseau",
             "Vielle Case to Portsmouth", "Tane Tane to Baroui"};


    private String[] Cost = {"$1.00","$7.00","$2.00","$3.50","$1.25","$5.00","$2.50","$10.00","$0.75","$2.25","$3.50","$4.25"};




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 10; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("Village", village[i]);
            hm.put("Cost", Cost[i]);
            aList.add(hm);
        }


        String[] from = {"Village","Cost"};

        int[] to = {R.id.Village, R.id.price};

        View v = inflater.inflate(R.layout.fragment_top_rated, container, false);
        ListView list = (ListView) v.findViewById(R.id.listView1);
        registerForContextMenu(list);
        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.activity_rate, from, to);
        list.setAdapter(adapter);


        return v;
    }
}