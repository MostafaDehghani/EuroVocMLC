/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.dataprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static nl.uva.mlc.settings.Config.configFile;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author admin
 */
public class CV_kFoldGenerator {
    
    private int k = Integer.parseInt(configFile.getProperty("K_IN_K-FOLD_CV"));
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CV_kFoldGenerator.class.getName());
    
    public void FoldCreator(String InputFeatureFile, String outDir)
    {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(new File(InputFeatureFile)));
            String str;
            ArrayList<String> names = new ArrayList<>();
            try {
                while((str = bf.readLine()) != null)
                {
                    names.add(str.split(" # ")[1].split(" ")[0]);
                }
                String[] fileNames = (String[]) names.toArray();
                Arrays.sort(fileNames);
                Map<String , Integer> mp = new HashMap<String, Integer>();
                for(int i = 0; i < fileNames.length; i++)
                    mp.put(fileNames[i], i);
                int filesPerFold = fileNames.length / k;
                for(int i = 0; i < k; i++)
                {
                    File fold = new File(outDir + "/fold" + i);
                    FileUtils.forceMkdir(fold);
                    File file = new File(outDir + "/fold" + i + "/test.txt");
                    FileWriter fw = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(fw);
                    File foldTrain = new File(outDir + "/fold" + i + "/train.txt");
                    FileWriter fwTrain = new FileWriter(foldTrain);
                    BufferedWriter bwTrain = new BufferedWriter(fwTrain);

                    int maxFile = (i + 1) * filesPerFold;
                    if(i == k-1)
                        maxFile = fileNames.length;

                    bf = new BufferedReader(new FileReader(new File(InputFeatureFile)));
                    while((str = bf.readLine()) != null)
                    {
                        String name = str.split(" # ")[1].split(" ")[0];
                        if(mp.get(name) >= i * filesPerFold && mp.get(name) < maxFile)
                        {
                            bw.write(str);
                        }
                        else
                            bwTrain.write(str);
                    }
                }
            } catch (IOException ex) {
                log.error(ex);
            }
        } catch (FileNotFoundException ex) {
            log.error(ex);
        }
    }
    public void main() {
    }
}
