package scottm.examples;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class HelloItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> mOverlays;
	private Context mContext;

	public HelloItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mOverlays = new ArrayList<OverlayItem>();
		mContext = context;
		
		// should add to resource file!!!! Not hard code!
		int[] parlin = {(int) (30.284882 * 1e6), (int) (-97.740127 * 1e6)};
        int[] mcdonald = {(int) (30.671581 * 1e6), (int) (-104.022431 * 1e6)};
        int[] marine = {(int) (27.835926 * 1e6), (int) (-97.050372 * 1e6)};
        int[] painter = {(int) (30.287149 * 1e6), (int) (-97.738651 * 1e6)};
        int[] gdc = {(int) (30.286336 * 1e6), (int) (-97.736693 * 1e6)}; 
        int[][] points = {parlin, mcdonald, marine, painter, gdc};

        String[][] titlesAndSnippets = {{"UT", 
        "Parlin Hall, The University of Texas at Austin"},
        {"McDonald", 
        "McDonald Observatory - University of Texas - West Texas"},
        {"Marine Biology", 
        "University of Texas Marine Sciennce Institute - Port Aransas"},
        {"Painter Hall", "The University of Texas at Austin"},
        {"Bill and Melinda Gates Computer Science Complex", "UT Austin"}};

        for(int i = 0; i < points.length; i++) {
            GeoPoint g = new GeoPoint(points[i][0], points[i][1]);
            OverlayItem oi = new OverlayItem(g, 
                    titlesAndSnippets[i][0], titlesAndSnippets[i][1]);
            mOverlays.add(oi); 
        }
        
        populate();
	}
	
	
//	public void addOverlay(OverlayItem overlay) {
//	    mOverlays.add(overlay);
//	    // inherited method to prepare overlays to be drawn
//	    populate(); 
//	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) { 
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}

}
