// TipCalculator.java
// Calculates bills using 5, 10, 15 and custom percentage tips.
package com.deitel.tipcalculator;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

// main Activity class for the TipCalculator
public class TipCalculator extends Activity 
{
    private static final String TAG = "tipCalculator";
    
   // constants used when saving/restoring state
   private static final String BILL_TOTAL = "BILL_TOTAL";
   private static final String CUSTOM_PERCENT = "CUSTOM_PERCENT";
   private static final int NUM_PERCENTS = 4;
   
   private final double[] tipPercents = {0.1, 0.15, 0.2, 0.18};
   private final int CUSTOM_INDEX = 3;
   
   private final int[] tipEditIDS = {R.id.tip10EditText, R.id.tip15EditText,
		   R.id.tip20EditText, R.id.tipCustomEditText};
   private final int[] totalEditIDS = {R.id.total10EditText, R.id.total15EditText,
		   R.id.total20EditText, R.id.totalCustomEditText};
   
   private double currentBillTotal; 
   private EditText[] tipEditTexts;
   private EditText[] totalEditTexts;
   private EditText billEditText; 
   private TextView customTipTextView; 

   // Called when the activity is first created.
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState); // call superclass's version
      setContentView(R.layout.main); // inflate the GUI

      Log.d(TAG, "savedInstance state: " + savedInstanceState);
      // check if app just started or is being restored from memory
      if ( savedInstanceState == null ) // the app just started running
      {
         currentBillTotal = 0.0; 
         tipPercents[CUSTOM_INDEX] = 0.18;
         Log.d(TAG, "Custom percent: " + tipPercents[CUSTOM_INDEX] );
      } 
      else // app is being restored from memory, not executed from scratch
      {
         currentBillTotal = savedInstanceState.getDouble(BILL_TOTAL); 
         tipPercents[CUSTOM_INDEX] = 
                 savedInstanceState.getDouble(CUSTOM_PERCENT);
          Log.d(TAG, "Saved instance state: Custom percent: " + tipPercents[CUSTOM_INDEX] );
      } 
      
      // get references to the 10%, 15% and 20% tip and total EditTexts
      tipEditTexts = new EditText[NUM_PERCENTS];
      totalEditTexts = new EditText[NUM_PERCENTS];
      for(int i = 0; i < tipEditTexts.length; i++) {
    	  tipEditTexts[i] = (EditText) findViewById(tipEditIDS[i]);
    	  totalEditTexts[i] = (EditText) findViewById(totalEditIDS[i]);
      }
      
      customTipTextView = (TextView) findViewById(R.id.customTipTextView);

      // change whenever text changes
      billEditText = (EditText) findViewById(R.id.billEditText);
      billEditText.addTextChangedListener(billEditTextWatcher);
      
      billEditText.setFilters(new InputFilter[]{new DecimalInputFilter(5, 2)});

      // get the SeekBar used to set the custom tip amount
      SeekBar customSeekBar = (SeekBar) findViewById(R.id.custonTipAmountSeekBar);
      customSeekBar.setOnSeekBarChangeListener(customSeekBarListener);
   } 

   // updates 10, 15 and 20 percent tip EditTexts
   private void updateStandard() 
   {
	   for(int i = 0; i < NUM_PERCENTS - 1; i++) {
		   double tip = currentBillTotal * tipPercents[i];
		   double total = currentBillTotal + tip;
		   tipEditTexts[i].setText(String.format("%.02f", tip));
		   totalEditTexts[i].setText(String.format("%.02f", total));   
	   }

   } // end method updateStandard

   // updates the custom tip and total EditTexts
   private void updateCustom() 
   {
       Log.d(TAG, "Update custom. current custom percentL " + tipPercents[CUSTOM_INDEX] );

      // set customTipTextView's text to match the position of the SeekBar
      customTipTextView.setText((tipPercents[CUSTOM_INDEX] * 100)+ "%");

      double customTipAmount = 
         currentBillTotal * tipPercents[CUSTOM_INDEX];
      double customTotalAmount = currentBillTotal + customTipAmount;

      // display the tip and total bill amounts
      tipEditTexts[CUSTOM_INDEX].setText(String.format("%.02f", customTipAmount));
      totalEditTexts[CUSTOM_INDEX].setText(
         String.format("%.02f", customTotalAmount));
   } 

   // save values of billEditText and customSeekBar
   @Override
   protected void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);
      
      outState.putDouble(BILL_TOTAL, currentBillTotal);
      outState.putDouble(CUSTOM_PERCENT, tipPercents[CUSTOM_INDEX]);
   } 
   
   // called when the user changes the position of SeekBar
   private OnSeekBarChangeListener customSeekBarListener = 
      new OnSeekBarChangeListener() 
   {
      // update tipPercents[CUSTOM_INDEX], then call updateCustom
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress,
         boolean fromUser) 
      {
         // sets tipPercents[CUSTOM_INDEX] to position of the SeekBar's thumb
         tipPercents[CUSTOM_INDEX] = seekBar.getProgress() / 100.0;
         updateCustom();
      } 

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) 
      {
      } 

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) 
      {
      } 
   }; 

   // event-handling object that responds to billEditText's events
   private TextWatcher billEditTextWatcher = new TextWatcher() 
   {
      // called when the user enters a number
      @Override
      public void onTextChanged(CharSequence s, int start, 
         int before, int count) {  
         // convert billEditText's text to a double
         try {
        	 currentBillTotal = Double.parseDouble(s.toString());
         } catch (NumberFormatException e) {
            currentBillTotal = 0.0; // default if an exception occurs
         } 

         // update the standard and custom tip EditTexts
         updateStandard(); 
         updateCustom(); 
      } 

      @Override
      public void afterTextChanged(Editable s) { } 

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
         int after) { }
   };
}
   

// Modified by Mike Scott, use arrays for mutliple EditTexts
/*************************************************************************
* (C) Copyright 1992-2012 by Deitel & Associates, Inc. and               *
* Pearson Education, Inc. All Rights Reserved.                           *
*                                                                        *
* DISCLAIMER: The authors and publisher of this book have used their     *
* best efforts in preparing the book. These efforts include the          *
* development, research, and testing of the theories and programs        *
* to determine their effectiveness. The authors and publisher make       *
* no warranty of any kind, expressed or implied, with regard to these    *
* programs or to the documentation contained in these books. The authors *
* and publisher shall not be liable in any event for incidental or       *
* consequential damages in connection with, or arising out of, the       *
* furnishing, performance, or use of these programs.                     *
*************************************************************************/
