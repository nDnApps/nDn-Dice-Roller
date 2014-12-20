package com.nDnDiceRoller;

        import android.content.Context;
        import android.util.AttributeSet;
        import android.widget.ArrayAdapter;
        import android.widget.Spinner;

public  class NumberRangeSpinner extends Spinner {
    private ArrayAdapter<CharSequence> mAdapter;
    public NumberRangeSpinner(Context context) {
        super(context);
        initialize(context);

    }
    public NumberRangeSpinner(Context context, AttributeSet attribs) {
        super(context, attribs);
        initialize(context);

    }

    private void initialize( Context context)
    {
        mAdapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(mAdapter);
        mAdapter.add("0");
    }


    public int getValue(){
        int targetPosition = getSelectedItemPosition();
        if(targetPosition == INVALID_POSITION){
            return INVALID_POSITION;
        }
        Integer target = Integer.valueOf(mAdapter.getItem(targetPosition).toString());
        if( target == null){
            return 0;
        }

        return target;
    }

    public void setRange(int lowInclusive, int highInclusive){
        int oldValue = getValue();
        this.setSelection(0);
        mAdapter.clear();
        for(int i = lowInclusive; i <= highInclusive; i++ ){
            mAdapter.add(Integer.toString(i));
        }
        int newPosition;
        if(oldValue > highInclusive){
            newPosition = highInclusive - lowInclusive;
        }else if( oldValue < lowInclusive){
            newPosition = 0;
        }else{
            newPosition = mAdapter.getPosition(Integer.toString(oldValue));
        }
        setSelection(newPosition);
    }
}
