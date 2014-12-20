package com.nDnDiceRoller;

        import java.io.Serializable;
        import java.util.ArrayList;
        import java.util.Random;

        import android.graphics.Color;

class Die implements Serializable, Comparable<Die>
{
    /**
     *
     */
    private static final long serialVersionUID = -6586340583252169148L;


    private boolean mExploding;
    private static int SAVE_STRING_PARTS_COUNT = 3;
    private Random mRandomGen;

    int sides, value;
    int textColor, dieColor;

    Die(int sides)
    {
        init(sides, sides);
    }
    Die(int sides, int value)
    {
        init(sides, value);

    }
    Die(Die d)
    {
        this.sides = d.sides;
        this.value = d.value;
        this.mExploding = d.mExploding;
        this.textColor = d.textColor;
        this.dieColor = d.dieColor;
        mRandomGen = new Random();
    }

    public Die(String dieRep) {
        mRandomGen= new Random();
        if(dieRep == null || dieRep.length() == 0){
            init(0,0);
            return;
        }

        //get leading letter
        if(dieRep.charAt(0) == 'd' ){
            mExploding = false;
        }else if(dieRep.charAt(0) == 'e'){
            mExploding = true;
        }else if(dieRep.charAt(0) == '+'){
            init(1,1);
            return;
        }else{
            //malformed string, put to error values
            init(0,0);
            return;
        }
        dieRep = dieRep.trim();
        String[] partsRep = dieRep.split(",");
        //if not the correct number of parts in the string, put to error values
        if(SAVE_STRING_PARTS_COUNT != partsRep.length){
            init(0,0);
            return;
        }
        String dieName = partsRep[0];
        String numberString = dieName.substring(1);
        sides = value = Integer.valueOf(numberString);


        //read die and text color
        try{
            String dieColorString = partsRep[1];
            this.dieColor = Color.parseColor(dieColorString);
        }catch (IllegalArgumentException e) {
            this.dieColor = Color.RED;
        }
        try{
            String textColorString = partsRep[2];
            this.textColor = Color.parseColor(textColorString);
        }catch (IllegalArgumentException e) {
            this.textColor = Color.WHITE;
        }
    }
    void init(int sides, int value){
        this.sides = sides;
        this.value = value;
        this.dieColor = Color.RED;
        this.textColor = Color.WHITE;
        mExploding = false;
        mRandomGen=new Random();
    }

    int roll(){

        if(sides != 1){
            value = mRandomGen.nextInt(sides) + 1;
            while(mExploding && value == sides){
                value += mRandomGen.nextInt(sides) + 1;
            }
        }
        return value;
    }

    int getMaxRoll(){
        if(sides == 1){
            return value;
        }else if(mExploding){
            return (getNumExplosions()+1) * sides;
        }else{
            return sides;
        }
    }

    int getMinRoll(){
        if(sides == 1){
            return value;
        }else{
            return 1;
        }
    }

    private int getNumExplosions(){
        int explosions;

        if(!mExploding){
            explosions = 0;
        }else{
            double pMax = 1.0/sides;
            explosions = 1;
            while(pMax >= 0.01){
                explosions++;
                pMax *= 1/sides;
            }
        }
        return explosions;
    }

    ArrayList<Double> getHistogram(){
        //make a slot for every value from [0,maxRoll]
        ArrayList<Double> histogram = new ArrayList<Double>(getMaxRoll() + 1);
        if(sides == 1){
            for(int i = 0; i < value; i++){
                histogram.add(0.0);
            }
            histogram.add(1.0);
        }else if(mExploding){
            int maxExplosions = getNumExplosions();
            double pRoll = 1.0;
            for(int e = 0; e <= maxExplosions; e++){
                pRoll *= 1.0/sides;
                histogram.add( 0.0); // 0% chance of getting exploding roll as final value
                for(int i = 1; i < sides; i++){
                    histogram.add(pRoll);
                }
            }
            histogram.add(pRoll);
        }else{
            histogram.add( 0.0);
            for(int i = 1; i <= sides; i++){
                histogram.add(1.0/sides);
            }
        }
        return histogram;
    }

    public void setExploding(Boolean explodingBool){
        mExploding = explodingBool;
    }

    public Boolean isExploding(){
        return mExploding;
    }

    public String getName() {
        String name;
        if(sides == 1){
            name = new String("+") + Integer.toString(value);
        }else{
            name = mExploding ? "e" : "d";
            name += Integer.toString(sides);
        }
        return name;
    }

    public String toString(){
        return getName();
    }

    public String saveRepresentation(){
        StringBuilder saveStringBuilder = new StringBuilder();
        saveStringBuilder.append(getName());
        saveStringBuilder.append(",#");
        saveStringBuilder.append(Integer.toHexString(dieColor));
        saveStringBuilder.append(",#");
        saveStringBuilder.append(Integer.toHexString(textColor));
        return saveStringBuilder.toString();
    }
    /*
     * Dies are sorted based on lowest to highest number of sides, with normal
     * dice coming before exploding dice, and an arbitrary but consistent
     * ordering based on color.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Die another) {
        if(this.sides != another.sides){
            //return value based on integer comparison
            //upgrade to integer in order to use compareTo

            return intCompare(this.sides, another.sides);
        }else if(this.isExploding() != another.isExploding()){
            //the non exploding die should go first
            return this.isExploding() ? 1 : -1;
        }else if(this.dieColor != another.dieColor){
            //return value based on dieColor integer
            return intCompare(this.dieColor, another.dieColor);
        }else{
            //return value based on textColor integer
            int comparisonResult = intCompare(this.textColor, another.textColor);
            if(comparisonResult == 0){
                int breakpoint = 0;
                breakpoint ++;
            }
            return comparisonResult;
        }
    }
    private int intCompare(int int1, int int2){
        //upgrade to integer in order to use compareTo
        Integer integer1 = new Integer(int1);
        return integer1.compareTo(int2);
    }

}