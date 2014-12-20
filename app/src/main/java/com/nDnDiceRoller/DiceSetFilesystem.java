package com.nDnDiceRoller;

        import android.content.Context;
import android.content.res.AssetManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//class handles opening and saving files from both assets and local filesystem
public class DiceSetFilesystem {
    private Context mContext;
    

    public static String FILENAME_LAST =  ".last";
    public static String ASSETS_FOLDER =  "dice_sets";
    static String[] mReadOnlyFilesList = { FILENAME_LAST }; //Built in die sets that cannot be edited


    DiceSetFilesystem(Context context)
    {

        mContext = context;

    }

    List<String> getDiceSetList(){
        AssetManager assetManager = mContext.getAssets();
        String[] diceSetsFilenames;
        List<String> filesList = new ArrayList<String>();
        try {
            diceSetsFilenames = mContext.fileList();
            filesList =  filterDiceSets(diceSetsFilenames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<String>(filesList);
    }

    public DiceSet getDiceSet(String name) throws IOException{

        DiceSet retDieSet = new DiceSet(name);

        InputStreamReader diceFileReader;
        if(Arrays.asList(mReadOnlyFilesList).contains(name)){
            InputStream iS = mContext.getAssets().open(ASSETS_FOLDER + "/" + name);
            diceFileReader = new InputStreamReader(iS);
        }else{
            FileInputStream fis = mContext.openFileInput(name);
            diceFileReader = new InputStreamReader(fis);
        }
        int lastChar = 1;
        while(lastChar != -1){
            //read the dice text representation from file
            StringBuilder sB = new StringBuilder();
            lastChar = diceFileReader.read();
            while(lastChar != -1 && lastChar != '\n'){
                sB.append((char)lastChar);
                lastChar = diceFileReader.read();
            }
            String dieRep = sB.toString();

            Die d = new Die(dieRep);
            if(d.sides != 0){
                retDieSet.addDie(d);
            }
        }

        try {
            diceFileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retDieSet;
    }



    public void saveDiceSet(DiceSet dS) throws IOException{

        String localFilename = dS.getName() == null ? FILENAME_LAST : dS.getName();
        FileOutputStream fos;

        fos = mContext.openFileOutput(dS.getName(), Context.MODE_PRIVATE);

        for(Die d: dS){
            String s = d.saveRepresentation() + "\n";
            fos.write(s.getBytes());
        }
        fos.close();
    }


    public boolean isReadOnly(String filename){
        return Arrays.asList(mReadOnlyFilesList).contains(filename);
    }

    private List<String> filterDiceSets(String[] rawFilenames)
    {
        List<String> filteredNames = new ArrayList<String>(rawFilenames.length);
        //remove hidden files
        for (String rawFilename : rawFilenames) {
            if (rawFilename.charAt(0) != '.') {
                filteredNames.add(rawFilename);
            }
        }
        return filteredNames;
    }

    public DiceSet getLastDiceSet() throws IOException {
        return getDiceSet(FILENAME_LAST);
    }



}
