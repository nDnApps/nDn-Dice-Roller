
package com.nDnDiceRoller;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.Map;
import java.util.TreeMap;

/**
 * Specialized spinner used for selecting colors.
 */
public  class ColorSpinner extends Spinner {
    private ArrayAdapter<CharSequence> mAdapter;

    private Context mContext;

    Map<String, Integer> mColors = new TreeMap<String, Integer>();

    /**
     * Initialize mColors - mapping of color names to color RGB values
     */
    private void initColors(){
        mColors.put("Black",Color.BLACK);
        mColors.put("Blue", Color.BLUE);
        mColors.put("Green", Color.GREEN);
        mColors.put("Orange", 0xFFFF6600);
        mColors.put("Pink", 0xFFFFC0CB);
        mColors.put("Purple", 0xFF551A8B);
        mColors.put("Red", Color.RED );
        mColors.put("Yellow", Color.YELLOW);
        mColors.put("White", Color.WHITE);

    }

    /**
     * Constructor for the color spinner. Handles adding all default colors.
     * @param context
     */
    public ColorSpinner(Context context, Spinner spinner) {
        super(context);
        mContext = context;
        init();


    }

    /**
     * Constructor for the color spinner. Handles adding all default colors.
     * @param context
     * @param attribs
     */
    public ColorSpinner(Context context, AttributeSet attribs) {
        super(context, attribs);
        mContext = context;
        init();

    }

    /**
     * Shared constructor code.
     */
    private void init(){
        initColors();
        mAdapter = new ArrayAdapter<CharSequence>(mContext, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(mAdapter);
        for (String colorString : mColors.keySet()) {
            mAdapter.add(colorString);
        }
    }

    /**
     * returns the RGB value of the color selected by the spinner.
     * @return RGB color
     */
    public int getSelectedColor(){
        int targetPosition = getSelectedItemPosition();
        if(targetPosition == INVALID_POSITION){
            return INVALID_POSITION;
        }
        String colorString = mAdapter.getItem(targetPosition).toString();

        return mColors.get(colorString);
    }

    /**
     * Remove a color as an option from the spinner.
     * @param colorName - the name of the color to remove, e.g. "Red"
     */
    public void removeColor(String colorName){
        mAdapter.remove(colorName);
        mColors.remove(colorName);
    }

    /**
     * Programmatically set the color selected by the spinner.
     * @param colorName - The name of the color to select, e.g. "Red"
     */
    public void setSelectedColor(String colorName){
        int position = mAdapter.getPosition(colorName);
        if(position > 0 && position < mAdapter.getCount()){
            setSelection(position);
        }
    }

    /**
     * Given a color RGB value, find the name of that color.
     * @param color - RGB color value, e.g. 0xFF00FF
     * @return the name of the color, e.g. "Red"
     */
    public String lookupColorName(int color) {
        for (String colorString : mColors.keySet()) {
            int currColor = mColors.get(colorString);
            if (color == currColor) {
                return colorString;
            }
        }
        return null;
    }
}
