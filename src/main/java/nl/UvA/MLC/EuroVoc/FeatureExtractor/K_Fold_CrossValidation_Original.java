/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.UvA.MLC.EuroVoc.FeatureExtractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import nl.UvA.MLC.EuroVoc.EuroVocDoc;
import static nl.UvA.MLC.Settings.Config.configFile;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author mosi
 */
public class K_Fold_CrossValidation_Original {
    
    private int k = Integer.parseInt(configFile.getProperty("K_IN_K-FOLD_CV"));
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(K_Fold_CrossValidation_Original.class.getName());
    
    public void crossValidation(HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> features, String outDir, String inDir){
        try {
            File out = new File(outDir);
            FileUtils.forceMkdir(out);
            File in = new File(inDir);
            File[] inFiles = in.listFiles();
            Arrays.sort(inFiles);
            int inNumFiles = inFiles.length;
            int numFilesPerFold = inNumFiles / k;
            Map<String, EuroVocDoc> docs = new HashMap<>();
            for(Entry<EuroVocDoc, HashMap<String, Feature>> ent : features.get(1).entrySet())
                docs.put(ent.getKey().getId(), ent.getKey());
            
            File allFold = new File(outDir + "/all_folds.txt" );
            FileWriter all_fw = new FileWriter(allFold);
            BufferedWriter all_bw = new BufferedWriter(all_fw);
            Integer overallQId =0;
           
            for(int i = 0; i < k; i++)
            {
                File fold = new File(outDir + "/fold" + (i+1));
                FileUtils.forceMkdir(fold);
                File file = new File(outDir + "/fold" + (i+1) + "/test.txt");
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                
                int qid = 0;
                for(int j = i * numFilesPerFold; j < (i + 1) * numFilesPerFold; j++)
                {
                    if(inFiles[j].getName().startsWith("jrc")&& inFiles[j].getName().endsWith(".xml")){
                        String docID = inFiles[j].getName().split("\\.")[0];
                        Map<String, Feature> firstFeature = features.get(1).get(docs.get(docID));
                        for(Entry<String, Feature> ent : firstFeature.entrySet())
                        {
                            String line = " " + "qid:" + qid + " " + "1:" + ent.getValue().getfValue() + " ";
                            for(int l = 1; l < features.size(); l++)
                            {
                                if(features.get(l+1).get(docs.get(docID)).containsKey(ent.getKey()))
                                    line += (l + 1) + ":" + features.get(l+1).get(docs.get(docID)).get(ent.getKey()).getfValue() + " ";
                                else
                                    line += (l + 1) + ":0 "; 
                            } 
                            line += "# " + docID + " " + ent.getKey();
                            if(docs.get(docID).getClasses().contains(ent.getKey()))
                                line = "1" + line;
                            else
                                line = "0" + line;
                            bw.write(line + "\n");
                            all_bw.write(line.replace(" qid:"+ qid, " qid:"+overallQId) + "\n");
                        }
                        qid ++;
                        overallQId++;
                    }
                }
                bw.close();
                File foldTrain = new File(outDir + "/fold" + (i+1) + "/train.txt");
                FileWriter fwTrain = new FileWriter(foldTrain);
                BufferedWriter bwTrain = new BufferedWriter(fwTrain);
                
                for(int j = 0; j < inFiles.length; j++)
                {
                    if(j < i * numFilesPerFold || j >= (i + 1) * numFilesPerFold)
                    {
                        if(inFiles[j].getName().startsWith("jrc")&& inFiles[j].getName().endsWith(".xml")){
                            String docID = inFiles[j].getName().split("\\.")[0];
                            Map<String, Feature> firstFeature = features.get(1).get(docs.get(docID));
                            for(Entry<String, Feature> ent : firstFeature.entrySet())
                            {
                                String line = " " + "qid:" + qid + " " + "1:" + ent.getValue().getfValue() + " ";
                                
                                for(int l = 1; l < features.size(); l++)
                                {
                                    if(features.get(l+1).get(docs.get(docID)).containsKey(ent.getKey()))
                                        line += (l + 1) + ":" + features.get(l+1).get(docs.get(docID)).get(ent.getKey()).getfValue() + " ";
                                    else
                                        line += (l + 1) + ":0 "; 
                                }
                                line += "# " + docID + " " + ent.getKey();
                                if(docs.get(docID).getClasses().contains(ent.getKey()))
                                    line = "1" + line;
                                else
                                    line = "0" + line;
                                bwTrain.write(line + "\n");
                            }
                            qid ++;
                        }
                    }
                }
                bwTrain.close();
                
            }
            all_bw.close();
        } catch (IOException ex) {
            log.error(ex);
        }
    }
    
    public void crossValidation(TreeMap<Integer,HashMap<String,HashMap<String,Feature>>> features, String outDir, String inDir){
        try {
            File out = new File(outDir);
            FileUtils.forceMkdir(out);
            File in = new File(inDir);
            File[] inFiles = in.listFiles();
            Arrays.sort(inFiles);
            int inNumFiles = inFiles.length;
            int numFilesPerFold = inNumFiles / k;

            File allFold = new File(outDir + "/all_folds.txt" );
            FileWriter all_fw = new FileWriter(allFold);
            BufferedWriter all_bw = new BufferedWriter(all_fw);
            Integer overallQId =0;
           
            for(int i = 0; i < k; i++)
            {
                File fold = new File(outDir + "/fold" + (i+1));
                FileUtils.forceMkdir(fold);
                File file = new File(outDir + "/fold" + (i+1) + "/test.txt");
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                
                int qid = 0;
                for(int j = i * numFilesPerFold; j < (i + 1) * numFilesPerFold; j++)
                {
                    if(inFiles[j].getName().startsWith("jrc")&& inFiles[j].getName().endsWith(".xml")){
                        String docID = inFiles[j].getName().split("\\.")[0];
                        Map<String, Feature> firstFeature = features.get(1).get(docID);
                        for(Entry<String, Feature> ent : firstFeature.entrySet())
                        {
                            String line = " " + "qid:" + qid + " " + "1:" + ent.getValue().getfValue() + " ";
                            for(int l = 1; l < features.size(); l++)
                            {
                                if(features.get(l+1).get(docID).containsKey(ent.getKey()))
                                    line += (l + 1) + ":" + features.get(l+1).get(docID).get(ent.getKey()).getfValue() + " ";
                                else
                                    line += (l + 1) + ":0 "; 
                            } 
                            line += "# " + docID + " " + ent.getKey();
                            line = ent.getValue().getLabel() + line;
                            bw.write(line + "\n");
                            all_bw.write(line.replace(" qid:"+ qid, " qid:"+overallQId) + "\n");
                        }
                        qid ++;
                        overallQId++;
                    }
                }
                bw.close();
                File foldTrain = new File(outDir + "/fold" + (i+1) + "/train.txt");
                FileWriter fwTrain = new FileWriter(foldTrain);
                BufferedWriter bwTrain = new BufferedWriter(fwTrain);
                
                for(int j = 0; j < inFiles.length; j++)
                {
                    if(j < i * numFilesPerFold || j >= (i + 1) * numFilesPerFold)
                    {
                        if(inFiles[j].getName().startsWith("jrc")&& inFiles[j].getName().endsWith(".xml")){
                            String docID = inFiles[j].getName().split("\\.")[0];
                            Map<String, Feature> firstFeature = features.get(1).get(docID);
                            for(Entry<String, Feature> ent : firstFeature.entrySet())
                            {
                                String line = " " + "qid:" + qid + " " + "1:" + ent.getValue().getfValue() + " ";
                                
                                for(int l = 1; l < features.size(); l++)
                                {
                                    if(features.get(l+1).get(docID).containsKey(ent.getKey()))
                                        line += (l + 1) + ":" + features.get(l+1).get(docID).get(ent.getKey()).getfValue() + " ";
                                    else
                                        line += (l + 1) + ":0 "; 
                                }
                                line += "# " + docID + " " + ent.getKey();
                                line = ent.getValue().getLabel() + line;
                                bwTrain.write(line + "\n");
                            }
                            qid ++;
                        }
                    }
                }
                bwTrain.close();
                
            }
            all_bw.close();
        } catch (IOException ex) {
            log.error(ex);
        }
    }

}
