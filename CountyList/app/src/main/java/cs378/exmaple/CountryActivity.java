package cs378.exmaple;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CountryActivity extends ListActivity {

    private static final String TAG = "CountryList";

    private ListView view;
    private List<String> countries;
    private ArrayAdapter<String> adapter;

    // For debugging. Count actual number of row views created.
    private int viewCountForDebug;

    // for regular version
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        createModel();
//        view = getListView();
//        setAdapter();
//        createOnItemClickListener();
//    }

    private void setAdapter() {
//        // for layout that is simply a TextViews
//        adapter
//                = new ArrayAdapter<String>(
//                this,
//                R.layout.list_item, // android.R.layout.simple_list_item_1,
//                countries);


        // for layout with TextView in more complex layout
        adapter
                = new ArrayAdapter<String>(
                this, // context
                R.layout.complex_list_item, // layout of list items / rows
                R.id.countryTextView, // sub layout to place text
                countries); // model of text

        // don't comment out!
        setListAdapter(adapter);
    }

    private void createModel() {
        String[] rawData
                = getResources().getStringArray(R.array.countries);

        countries = new ArrayList(Arrays.asList(rawData));
    }

    private void createOnItemClickListener() {

        view.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {

                Log.d(TAG, "Selected view: " + v);

                String country = countries.get(position);

                String toastString = "position: " + position +
                        ", id: " + id + "\ndata: "
                        + country;

                // example if creating and showing a Toast. Cheers!
                Toast.makeText(CountryActivity.this,
                        toastString,
                        Toast.LENGTH_SHORT).show();

//                // remove item selected from arraylist
//                countries.remove(position);
//                adapter.notifyDataSetChanged();


                // if we want to perform web search for country
                // searchWeb(country);
            }
        });
    }

    // from https://developer.android.com/guide/components/intents-common.html#Browser
    public void searchWeb(String countryName) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, countryName);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // ******************** COMPLEX ROW VERSION **************************
    // for version with switches

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        List<CountryRowData> list
                = new ArrayList<CountryRowData>();
        String[] countries
                = getResources().getStringArray(R.array.countries);
        for (String s : countries) {
            list.add(new CountryRowData(s, true));
        }
        Log.d(TAG, "number of countries = " + list.size());
        setListAdapter(new SafeAdapter(list));
    }

    private CountryRowData getModel(int position) {
        return (((SafeAdapter) getListAdapter()).getItem(position));
    }

    // code adapted from The Busy Coder's Guide to Android Development
    // pages 1139 - 1140.
    private class SafeAdapter extends ArrayAdapter<CountryRowData> {

        SafeAdapter(List<CountryRowData> list) {
            super(CountryActivity.this,
                    R.layout.complex_list_item,
                    R.id.countryTextView,
                    list);
        }

        public View getView(int position, View convertView,
                            ViewGroup parent) {

            View row = super.getView(position, convertView, parent);
            Switch theSwitch = (Switch) row.getTag();
            if (theSwitch == null) {
                // debugging, count number of row Views we are creating
                viewCountForDebug++;
                Log.d(TAG, "Created another View for row. Total Views created = " + viewCountForDebug);


                theSwitch = (Switch) row.findViewById(R.id.countrySafeSwitch);
                row.setTag(theSwitch);

                CompoundButton.OnCheckedChangeListener l =
                        new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView,
                                                         boolean isChecked) {
                                Integer myPosition = (Integer) buttonView.getTag();
                                CountryRowData model = getModel(myPosition);
                                model.safe = isChecked;
//                                // Following code not needed. Handled by adapter already.
//                                LinearLayout parent = (LinearLayout) buttonView.getParent();
//                                TextView label =
//                                        (TextView) parent.findViewById(R.id.countryTextView);
//                                label.setText(model.toString());
                            }
                        };
                theSwitch.setOnCheckedChangeListener(l);
            }

            CountryRowData model = getModel(position);
            theSwitch.setTag(position);
            theSwitch.setChecked(model.safe);
            return (row);
        }
    }

    private static class CountryRowData {
        private String name;
        private boolean safe;

        private CountryRowData(String n, boolean s) {
            name = n;
            safe = s;
        }

        public String toString() {
            return name;
        }
    }


}
