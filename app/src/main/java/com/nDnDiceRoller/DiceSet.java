package com.nDnDiceRoller;


        import java.io.Serializable;
        import java.util.ArrayList;
        import java.util.Iterator;
        import java.util.List;

public class DiceSet implements Serializable, Iterable<Die> {
    String mName;
    List<Die> mDice;

    DiceSet()
    {
        mName = null;
        mDice = new ArrayList<Die>(20);
    }

    DiceSet(String name)
    {
        mName = name;
        mDice = new ArrayList<Die>(20);
    }

    public void addDie(Die d){
        mDice.add(d);
    }

    public String getName(){
        return mName;
    }

    public Iterator<Die> iterator(){
        return mDice.iterator();
    }

    public void setName(String name) {
        mName = name;

    }
}
