package com.nDnDiceRoller;

        import java.io.IOException;
        import java.io.ObjectInputStream;
        import java.io.ObjectOutputStream;
        import java.io.Serializable;
        import java.util.ArrayList;
        import java.util.Iterator;
        import java.util.Map;
        import java.util.TreeMap;

/**
 * Stores a group of dice and maintains probability information for them.
 */
public class DiceDatabase implements Serializable, IHandProbability, Iterable<Die> {

    private HistogramLazyProxy mHistogram;
    private ArrayList<Die> mDice;

    /**
     * Constructor
     */
    DiceDatabase(){
        mDice = new ArrayList<Die>();
        mHistogram = new HistogramLazyProxy(this);
    }

    /**
     * Add a die to the database
     * @param die
     */
    public void addDie(Die die){
        mHistogram.addDie(die);
        mDice.add(new Die(die));

    }

    /**
     * Replace a die with a new die.
     * @param position - the position of the die to replace in the database.
     * @param die - the new Die.
     */
    public void setDie(int position, Die die){
        if(mDice.isEmpty() ){
            System.err.println("No die to remove");
        }else if(mDice.size() > position){
            mHistogram.removeDie(die);
            mHistogram.addDie(die);
            mDice.set(position, die);
        }else{
            System.err.println("Error setting die: die does not exist");
        }
    }

    /**
     * Remove the Die at position from the database.
     * @param position - the position of the die to remove in the database.
     */
    public void removeDie(int position){

        if(mDice.isEmpty() ){
            System.err.println("No die to remove");
        }else if(mDice.size() > position){
            Die d = mDice.get(position);
            //rebuild histogram
            mHistogram.removeDie(d);
            mDice.remove(position);
        }else{
            System.err.println("Error removing die: die does not exist");
        }
    }

    /**
     * Format the contents of the DiceDatabase as a string in dice notation, e.g. "2d4 + 5e6 + 5"
     * @return the contents of the DiceDatabase in dice notation.
     */
    public String getDiceNotation(){
        Map<String,Integer> sidesDiceCount = new TreeMap<String,Integer>();

        int bonus = 0;

        //Count each type of die
        for (Die die : mDice) {
            int sides = die.sides;
            String dieString = die.getName();
            if (sides == 1) {
                bonus = die.value;
            } else if (!sidesDiceCount.containsKey(dieString)) {
                sidesDiceCount.put(dieString, 1);
            } else {
                int nDiceSides = sidesDiceCount.get(dieString) + 1;
                sidesDiceCount.put(dieString, nDiceSides);
            }
        }

        //get a sorted iterator
        Iterator<String> sidesIt = sidesDiceCount.keySet().iterator();

        //build the string
        String result = new String();
        while( sidesIt.hasNext()){
            String dieString = sidesIt.next();
            int quantity = sidesDiceCount.get(dieString);
            result += Integer.toString(quantity) + dieString;
            if(sidesIt.hasNext()){
                result += " + ";
            }
        }
        //add the bonus at the end
        if(bonus != 0){
            if(result.length() != 0){
                result += " + ";
            }
            result += Integer.toString(bonus);
        }

        return result;
    }

    /**
     *
     * @return whether the database contains zero dice
     */
    public Boolean isEmpty(){
        return mDice.size() == 0;
    }

    /**
     *
     * @return Unsorted iterator of the dice in the database
     */
    public Iterator<Die> iterator()
    {
        return mDice.iterator();
    }

    /**
     *
     * @return the number of dice in the database
     */
    public int size()
    {
        return mDice.size();
    }

    /**
     * Simulate a dice roll for all of the dice in the database.
     * @return the sum of the dice values after performing the roll.
     */
    public int roll()
    {
        int result = 0;

        for (Die currDie : mDice) {
            result += currDie.roll();
        }
        return result;
    }

    /**
     * @return the sum of the current dice values
     */
    public int getTotal()
    {
        int result = 0;
        for (Die d : mDice) {
            result += d.value;
        }
        return result;
    }



    //Serializable methods

    /**
     * Serialize the object.
     * @param out - serialization stream to add this object to.
     * @throws IOException - Only throws an error if the stream cannot be closed.
     */
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        //Don't save the histogram because it can be recreated from mDice.
        try {
            out.writeObject(mDice);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally {
            out.close();
        }
    }

    /**
     * Deserialize the object.
     * @param in - stream containing the serialized object
     * @throws IOException - only throws an io exception if the stream cannot be closed.
     */
    private void readObject(ObjectInputStream in) throws IOException
    {
        ArrayList<Die> tempDice;
        try {
            tempDice = (ArrayList<Die>) in.readObject();
        }catch(Exception e) {
            e.printStackTrace();
            tempDice = new ArrayList<Die>();
        }finally{
            in.close();
        }
        for(Die d: tempDice)
        {
            addDie(d);
        }
    }

    /**
     *
     * @param position - position of the die to get.
     * @return the Die at position.
     */
    public Die getDie(int position) {
        return mDice.get(position);
    }

    //IHandProbability methods

    /**
     *
     * @return The maximum possible value that could be rolled by the dice in the database.
     */
    public int getMaxRoll() {
        return mHistogram.getMaxRoll();
    }

    /**
     *
     * @return The minimum possible value that could be rolled by the dice in the database.
     */
    public int getMinRoll() {
        return mHistogram.getMinRoll();
    }

    /**
     *
     * @param target - the target value to beat.
     * @return The probability as a value between 0 and 1, inclusive.
     */
    public double getProbabilityBeatOrTie(int target){
        return mHistogram.probabilityRollBetween(target, getMaxRoll());
    }
}
