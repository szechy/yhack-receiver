package com.example.colin.receiver;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ListView;

public class DeviceListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        final ListView listview = (ListView)findViewById(R.id.device_list);
        String[] names = {"Ariel", "Jasmine", "Belle", "Eve", "Edward", "Jasper"};
        long[] addresses = {890456783, 2342342, 23904895, 234234, 98043843, 1};
        int[] strengths = {-57, -89, -42, -35, -120, -105};

        DeviceInfo[] examples = new DeviceInfo[6];

        for(int i = 0; i < 6; ++i) {
            examples[i] = new DeviceInfo(names[i], addresses[i], strengths[i]);
        }

        DeviceArrayAdapter adapter = new DeviceArrayAdapter(this, examples);
        listview.setAdapter(adapter);
    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.device_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_device_list, container, false);
            return rootView;
        }
    }
}
