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
import java.util.Collections;
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
            ArrayList<String> fileNames = new ArrayList<>();
            try {
                while((str = bf.readLine()) != null)
                {
                    fileNames.add(str.split(" # ")[1].split(" ")[0]);
                }
                Collections.sort(fileNames);
                Map<String , Integer> mp = new HashMap<String, Integer>();
                for(int i = 0; i < fileNames.size(); i++)
                    mp.put(fileNames.get(i), i);
                int filesPerFold = fileNames.size() / k;
                for(int i = 0; i < k; i++)
                {
                    File fold = new File(outDir + "/fold" + (i+1));
                    FileUtils.forceMkdir(fold);
                    File testFile = new File(outDir + "/fold" + (i+1) + "/test.txt");
                    FileWriter fwTest = new FileWriter(testFile);
                    BufferedWriter bwTest = new BufferedWriter(fwTest);
                    File trainFile = new File(outDir + "/fold" + (i+1) + "/train.txt");
                    FileWriter fwTrain = new FileWriter(trainFile);
                    BufferedWriter bwTrain = new BufferedWriter(fwTrain);

                    int maxFile = (i + 1) * filesPerFold;
                    if(i == k-1)
                        maxFile = fileNames.size();

                    bf = new BufferedReader(new FileReader(new File(InputFeatureFile)));
                    while((str = bf.readLine()) != null)
                    {
                        String name = str.split(" # ")[1].split(" ")[0];
                        if(mp.get(name) >= i * filesPerFold && mp.get(name) < maxFile)
                        {
                            bwTest.write(str + "\n");
                        }
                        else
                            bwTrain.write(str + "\n");
                    }
                    bwTest.close();
                    bwTrain.close();
                    log.info("Fold " + (i+1) + " is created on: " + fold.getPath());
                }
                
            } catch (IOException ex) {
                log.error(ex);
            }
        } catch (FileNotFoundException ex) {
            log.error(ex);
        }
    }
    public void main(String InputFeatureFile, String outDir, Integer k) {
        if(k!=null)
            this.k = k;
        log.info(this.k + "-fold generating...");
        this.FoldCreator(InputFeatureFile, outDir);
    }
}
