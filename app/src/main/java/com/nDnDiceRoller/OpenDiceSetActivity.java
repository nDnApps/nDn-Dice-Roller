package com.nDnDiceRoller;


        import java.util.List;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.widget.AdapterView.OnItemSelectedListener;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.TextView;



public class OpenDiceSetActivity extends Activity {
    Button mOkButton;
    Button mDeleteButton;

    TextView mPreviewLabel;
    DiceSetPreview mPreview;

    DiceSetFilesystem mDiceSetFilesystem;
    List<String> mFilesList;


    ListView mDiceSetList;
    DiceSet mSelectedDieSet;

    /**
     * Create the activity.
     * @param savedInstanceState - If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_dice_set);

        mDiceSetFilesystem = new DiceSetFilesystem(this);
        mFilesList = mDiceSetFilesystem.getDiceSetList();


        mPreviewLabel = (TextView)findViewById(R.id.textViewPreview);
        LinearLayout previewLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutPreview);
        mPreview = new DiceSetPreview(this, previewLinearLayout);


        mDiceSetList = (ListView) findViewById(R.id.listViewDiceSet);

        mSelectedDieSet = null;

        mDiceSetList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item,mFilesList));

        mDiceSetList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long id) {
                select(mFilesList.get(position));

            }
        });

        mDiceSetList.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                select(mFilesList.get(arg2));

            }



            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                deselect();
            }});

        mOkButton = (Button)findViewById(R.id.buttonOk);
        mOkButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                //return with filename
                Intent resultIntent = new Intent();
                if(mSelectedDieSet != null){
                    resultIntent.putExtra("diceSet", mSelectedDieSet);
                    setResult(Activity.RESULT_OK, resultIntent);
                }else{
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                }
                finish();
            }

        });

        mDeleteButton = (Button)findViewById(R.id.buttonDelete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                confirmFileDelete();
            }

        });


        Button cancelButton = (Button)findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }


        });

    }

    private void confirmFileDelete(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete " + mSelectedDieSet.getName() + "?")
                .setCancelable(true)

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        String selectedDiceSetName = mSelectedDieSet.getName();
                        deleteFile(selectedDiceSetName);
                        mFilesList.remove(selectedDiceSetName);
                        mDiceSetList.setAdapter(new ArrayAdapter<String>(OpenDiceSetActivity.this, R.layout.list_item, mFilesList));
                        mDiceSetList.invalidate();
                        deselect();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void select(String selectedFilename) {

        mOkButton.setEnabled(true);

        boolean isReadOnlyFile = mDiceSetFilesystem.isReadOnly(selectedFilename);
        mDeleteButton.setEnabled(!isReadOnlyFile);


        try {
            mSelectedDieSet = mDiceSetFilesystem.getDiceSet(selectedFilename);
            mPreview.loadDiceSet(mSelectedDieSet);
            mPreviewLabel.setText("Preview: " + selectedFilename);
        }
        catch (Exception e)
        {
            mSelectedDieSet = null;
            mPreview.clear();
            mPreviewLabel.setText("Could not open dice set.");
        }

    }

    private void deselect(){
        mOkButton.setEnabled(false);
        mSelectedDieSet = null;
        mDeleteButton.setEnabled(false);
        mPreview.clear();
        mPreviewLabel.setText("Preview: ");
    }


}

