package com.deitel.tipcalculator;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

public class DecimalInputFilter implements InputFilter {

	private static final String TAG = "DecimalInputFilter";
	
	private final int NUM_DECIMALS;
    private final int MAX_DIGITS;

	// pre: numPlaces >= 0
	public DecimalInputFilter(int maxDigits, int numPlaces) {
		if(numPlaces < 0)
			throw new IllegalArgumentException("numPlaces " +
					"must be >= 0: " + numPlaces);
		
		NUM_DECIMALS = numPlaces;
        MAX_DIGITS = maxDigits;

	}
	
//	Log.d(TAG, "data: source, " + source + ", start: " + start + ", end: " + end);
//	Log.d(TAG, "dest: " + dest + ", dstart: " + dstart + ", dend: " + dend);
	
	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		
		Log.d(TAG, "data: source, " + source + ", start: " + start + ", end: " + end);
		Log.d(TAG, "dest: " + dest + ", dstart: " + dstart + ", dend: " + dend);

        // accept original replacement, if digits do not exceed limit
        CharSequence result = null;

		String destAsString = dest.toString();
	    int dotPos = destAsString.indexOf('.');
        if(dotPos < 0) {
            // no decimal
            if(destAsString.length() >= MAX_DIGITS)
                result = ""; // no change
        }
	    else {
	    	// has a decimal, so check number of digits after decimal
	    	String decimals = destAsString.substring(dotPos + 1);
            // and check whole number portion
            String digits = destAsString.substring(0, dotPos);
	    	// if already max number of digits after decimal and input
	    	// is after decimal then don't allow
	    	if(decimals.length() >= NUM_DECIMALS && dstart > dotPos)
	    		result = "";
            else if(digits.length() >= MAX_DIGITS && dstart < dotPos)
                result = "";
	    }


	    return result;
	  }
}
