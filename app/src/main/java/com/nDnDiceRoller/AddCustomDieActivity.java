

package com.nDnDiceRoller;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.view.KeyEvent;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemSelectedListener;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.CompoundButton;
        import android.widget.CompoundButton.OnCheckedChangeListener;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.TextView.OnEditorActionListener;
/**
 * Activity used to create and edit dice by changing the colors, number of sides, and whether the
 * die should explode (be rerolled when the maximum value is rerolled).
 */
public class AddCustomDieActivity extends Activity {

    private EditText mSidesField;
    private CheckBox mIsExploding;
    private int mMaxSides;
    private Die mOldDie;
    private ColorSpinner mTextColorSpinner, mDieColorSpinner;

    private TextView mErrorTextView;
    private DieView mPreview;

    /**
     * Create the activity. The intent may contain "OldDie", a Die object that should be used as the
     * starting point rather than default die values.
     * @param savedInstanceState - If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_die_menu);

        mErrorTextView = (TextView)findViewById(R.id.textViewErrorMessage);

        mPreview = (DieView)findViewById(R.id.dieViewPreview);

        mMaxSides = getResources().getInteger(R.integer.max_die_sides);

        mSidesField = (EditText)findViewById(R.id.editTextSides);
        mSidesField.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                updatePreview();
                return true;
            }
        });


        mIsExploding = (CheckBox)findViewById(R.id.checkBoxExploding);
        mIsExploding.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updatePreview();

            }

        });

        OnItemSelectedListener onColorChangeListener = new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                updatePreview();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                updatePreview();

            }
        };

        mTextColorSpinner = (ColorSpinner)findViewById(R.id.spinnerTextColor);
        mTextColorSpinner.setOnItemSelectedListener(onColorChangeListener);

        mDieColorSpinner = (ColorSpinner)findViewById(R.id.spinnerDieColor);
        mDieColorSpinner.setOnItemSelectedListener(onColorChangeListener);


        //get the old die if it is part of the intent
        mOldDie = null;
        Bundle extras = getIntent().getExtras();
        if(extras  != null){
            mOldDie = new Die((Die) extras.getSerializable("OldDie"));
            initInputElements(mOldDie);
        }
        else {
            initInputElements(null);
        }

        Button addDieButton = (Button) findViewById(R.id.buttonAddDie);
        addDieButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                String errorString = checkForErrors();
                if(errorString != null){
                    showErrorDialog(errorString);
                    return;
                }

                Die newDie = mPreview.getDie();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("CustomDie", newDie);
                if(mOldDie != null){
                    resultIntent.putExtra("OldDie", new Die(mOldDie));
                }
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }


        });



        Button cancelButton = (Button) findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }

        });

        updatePreview();
    }

    /**
     * Sets the input elements (the spinners and exploding checkbox) to either default values or to
     * match a Die object.
     * @param die - the die to match. Can be null.
     */
    private void initInputElements(Die die)
    {
        if(die != null){
            //fill the fields with the values of the die
            mSidesField.setText(Integer.toString(die.sides));
            mIsExploding.setChecked(die.isExploding());

            String textColorName = mTextColorSpinner.lookupColorName(die.textColor);
            mTextColorSpinner.setSelectedColor(textColorName);

            String dieColorName = mDieColorSpinner.lookupColorName(die.dieColor);
            mDieColorSpinner.setSelectedColor(dieColorName);
        }else{
            //set sane defaults
            mTextColorSpinner.setSelectedColor("White");
            mDieColorSpinner.setSelectedColor("Red");
        }

    }

    /**
     * Update the die preview and the error message, if necessary.
     */
    protected void updatePreview() {

        String errorString = checkForErrors();
        if(errorString != null){
            mErrorTextView.setText(errorString);
            Die errorDie = new Die(0);
            mPreview.setDie(errorDie);
            return;
        }else{
            mErrorTextView.setText("");
        }

        Boolean exploding = mIsExploding.isChecked();
        int sides = Integer.parseInt(mSidesField.getText().toString());
        int textColor = mTextColorSpinner.getSelectedColor();
        int dieColor = mDieColorSpinner.getSelectedColor();


        Die newDie = new Die(sides);
        newDie.setExploding(exploding);

        if(textColor != Spinner.INVALID_POSITION){
            newDie.textColor = textColor;
        }
        if(dieColor != Spinner.INVALID_POSITION){
            newDie.dieColor = dieColor;
        }
        mPreview.setDie(newDie);
        mPreview.invalidate();

    }

    /**
     * Show a popup error message.
     * @param message - the message to display.
     */
    private void showErrorDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Check the die settings for errors.
     * @return - error string describing the error, or null if there is no error
     */
    private String checkForErrors(){
        int sides = 0;
        if(mSidesField.getText().length() > 0){
            sides = Integer.parseInt(mSidesField.getText().toString());
        }else{
            return "Enter a value for sides";
        }
        if(sides < 1 || sides > mMaxSides){
            return "Sides must be between 1 and " + Integer.toString(mMaxSides);
        }

        int textColor = mTextColorSpinner.getSelectedColor();
        int dieColor = mDieColorSpinner.getSelectedColor();
        if(textColor == dieColor){
            return "The die and text colors must be different";
        }

        if(dieColor == Color.BLACK && !DieView.IsStandardDie(sides)) {
            return "Black is not an allowed color for nonstandard dice";
        }
        return null;
    }


}
