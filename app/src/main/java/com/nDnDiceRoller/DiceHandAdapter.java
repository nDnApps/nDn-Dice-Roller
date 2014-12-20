package com.nDnDiceRoller;


        import java.util.ArrayList;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.os.Bundle;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;

public class DiceHandAdapter extends BaseAdapter implements View.OnClickListener {
    private ArrayList<DieView> mDiceButtons;
    private DiceDatabase mDiceDb;
    private Activity mParent;

    public DiceHandAdapter(Activity parent, DiceDatabase diceDatabase, Bundle savedInstanceState) {
        mParent = parent;
        mDiceDb = diceDatabase;
        mDiceButtons = new ArrayList<DieView>();

        if(savedInstanceState != null){
            mDiceDb = (DiceDatabase)(savedInstanceState.getSerializable("DiceDb"));
            for(int i = 0; i < mDiceDb.size(); i++){
                Die d;
                try {
                    d = mDiceDb.getDie(i);
                    mDiceButtons.add(new DieView(mParent, d));
                } catch (Exception e) {
                    AlertDialog.Builder dieLoadError = new AlertDialog.Builder(this.mParent);
                    dieLoadError.setTitle("An error occurred while loading saved data.");
                    dieLoadError.setMessage(e.getMessage());
                    dieLoadError.setPositiveButton("OK", null);
                    dieLoadError.create().show();
                    mDiceDb = new DiceDatabase();
                    break;
                }
            }
        }else{
            mDiceDb = new DiceDatabase();
        }


    }


    public int getCount() {
        return mDiceButtons.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        DieView currDieView = mDiceButtons.get(position);
        Die d = mDiceDb.getDie(position);
        currDieView.setValue(d.value);
        return currDieView;
    }

    public void addDie(Die dieToAdd){

        if( dieToAdd.sides == 1){
            Boolean existingBonus = false;
            for(int i = 0; i < mDiceDb.size(); i++){
                Die tempDie = mDiceDb.getDie(i);
                if(tempDie.sides == 1){
                    tempDie.value = tempDie.value + dieToAdd.value;
                    mDiceDb.setDie(i, tempDie);
                    existingBonus = true;
                    break;
                }
            }
            if(!existingBonus){
                addDieInternal(dieToAdd);
            }

        }else{
            addDieInternal(dieToAdd);
        }
        notifyDataSetInvalidated();
    }


    private void addDieInternal(Die d)
    {
        assert(mDiceDb.size() == mDiceButtons.size());

        DieView newImageView = new DieView(mParent, d);
        mDiceButtons.add(newImageView);
        mDiceDb.addDie(d);
    }

    public void removeDie(int index)
    {
        Die d = mDiceDb.getDie(index);
        if(d.sides  == 1 && d.value > 1){
            d.value -= 1;
            mDiceDb.setDie(index, d);
            mDiceButtons.get(index).setDie(d);
        }else{
            mDiceDb.removeDie(index);
            mDiceButtons.remove(index);
        }
        notifyDataSetInvalidated();

    }

    public void clearDice()
    {
        mDiceDb = new DiceDatabase();
        mDiceButtons.clear();
        notifyDataSetInvalidated();

    }

    public int rollDice()
    {
        int result = mDiceDb.roll();
        //link rolls to DieViews
        notifyDataSetInvalidated();

        return result;
    }

    public int getTotal()
    {
        return mDiceDb.getTotal();
    }

    public void onClick(View view) {
        if (!mDiceButtons.remove(view)) {
            System.err.println("Error 321 - Tried to remove a die view that didn't exist.");
        }
        notifyDataSetInvalidated();
    }

    public int getMinRoll(){
        return mDiceDb.getMinRoll();
    }

    public int getMaxRoll(){
        return mDiceDb.getMaxRoll();
    }

    public double getProbabilityBeatOrTie(int n){
        return mDiceDb.getProbabilityBeatOrTie(n);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("DiceDb", mDiceDb);

    }

    public String diceStringRep(){
        return mDiceDb.getDiceNotation();
    }

    public boolean isEmpty() {
        return mDiceDb.isEmpty();
    }

    public IHandProbability getHandProbability()
    {
        return mDiceDb;
    }



}

