package scottm.utexas.sensorlist;

import android.app.ListActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SensorList extends ListActivity {
    
    private ArrayAdapter<String> adapter;
    private SensorManager sensorManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = 
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        setAdapter();
    }
    
    private void setAdapter() {
        List<Sensor> sensors 
            = sensorManager.getSensorList(Sensor.TYPE_ALL);
        
        List<String> sensorsAsStrings = new ArrayList<String>(sensors.size());
        for(Sensor s : sensors)
            sensorsAsStrings.add(s.toString());
                
        Collections.sort(sensorsAsStrings);
        
        adapter 
            = new ArrayAdapter<String>(this, R.layout.list_item, sensorsAsStrings);
        
        setListAdapter(adapter);
    }

}
