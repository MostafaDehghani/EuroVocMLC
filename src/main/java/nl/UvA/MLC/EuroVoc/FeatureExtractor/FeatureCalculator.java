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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import nl.UvA.MLC.EuroVoc.EuroVocDoc;
import nl.UvA.MLC.Settings.Config;
import org.apache.commons.io.FileUtils;
import static nl.UvA.MLC.Settings.Config.configFile;

/**
 *
 * @author mosi
 */
public class FeatureCalculator {
    
    private int k = Integer.parseInt(configFile.getProperty("K_IN_K-FOLD_CV"));
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FeatureCalculator.class.getName());
    
    public HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> normalize(HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> features){
        HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> RawFeatures = new HashMap<>();
        FeatureNormalizer fn = new FeatureNormalizer();
        for(Entry<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> ent : features.entrySet())
        {
            HashMap<EuroVocDoc,HashMap<String,Feature>> temp = new HashMap<>();
            for(Entry<EuroVocDoc,HashMap<String,Feature>> ent2 : ent.getValue().entrySet())
            {
                temp.put(ent2.getKey(), fn.normalize(ent2.getValue()));
            }
            RawFeatures.put(ent.getKey(), temp);
        }
        
        return RawFeatures;

    }
    public HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> calcFeatures(){
        RawFeatureCalculator rfc = new RawFeatureCalculator();
        HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> RawFeatures = rfc.conceptBaseFeatureCalc();
        HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> RawFeatures2 = normalize(RawFeatures);
        return RawFeatures2;
    }
    
    public HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> propagateAndConcatFeatures(String graphFilePath)
    {
        FeaturePropagator fp = new FeaturePropagator();
        fp.FeaturePropagator(graphFilePath);
        HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> tempRawFeatures = new HashMap<>();
        HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> FinaltempRawFeatures = new HashMap<>();
        HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> RawFeatures = calcFeatures();
        int fnumber = RawFeatures.size();
        for(Entry<Integer, HashMap<EuroVocDoc,HashMap<String,Feature>>> ent : RawFeatures.entrySet())
        {
            HashMap<EuroVocDoc,HashMap<String,Feature>> propagatedFeature = new HashMap<>();
            for(Entry<EuroVocDoc, HashMap<String,Feature>> ent2 : ent.getValue().entrySet())
            {
                HashMap<String, Feature> proFeatures = fp.propagator(ent2.getKey().getId(), ent2.getValue());
                propagatedFeature.put(ent2.getKey(), proFeatures);
            }
            tempRawFeatures.put( ++ fnumber, propagatedFeature);
        }
        
        FinaltempRawFeatures = normalize(tempRawFeatures);
        HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> FinalFeatures = new HashMap<>();
        for(Entry<Integer, HashMap<EuroVocDoc,HashMap<String,Feature>>> ent : RawFeatures.entrySet())
        {
            FinalFeatures.put(ent.getKey(), ent.getValue());
        }
        for(Entry<Integer, HashMap<EuroVocDoc,HashMap<String,Feature>>> ent : FinaltempRawFeatures.entrySet())
        {
            FinalFeatures.put(ent.getKey(), ent.getValue());
        }

        return FinalFeatures;
    }
    
    public void crossValidation(HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> features, String outDir, String inDir){
        try {
            File out = new File(outDir);
            FileUtils.forceMkdir(out);
            File in = new File(inDir);
            File[] inFiles = in.listFiles();
            int inNumFiles = inFiles.length;
            int numFilesPerFold = inNumFiles / k;
            Map<String, EuroVocDoc> docs = new HashMap<>();
            for(Entry<EuroVocDoc, HashMap<String, Feature>> ent : features.get(1).entrySet())
                docs.put(ent.getKey().getId(), ent.getKey());
            
            for(int i = 0; i < k; i++)
            {
                File fold = new File(outDir + "/fold" + i);
                FileUtils.forceMkdir(fold);
                File file = new File(outDir + "/fold" + i + "/test.txt");
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                
//
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
                                line += (l + 1) + ":" + features.get(l+1).get(docs.get(docID)).get(ent.getKey()).getfValue() + " ";
                            } 
                            line += "# " + ent.getKey() + " " + docID;
                            if(docs.get(docID).getClasses().contains(ent.getKey()))
                                line = "1" + line;
                            else
                                line = "0" + line;
                            bw.write(line + "\n");
                        }
                        qid ++;
                    }
                }
                bw.close();
                File foldTrain = new File(outDir + "/fold" + i + "/train.txt");
                FileWriter fwTrain = new FileWriter(foldTrain);
                BufferedWriter bwTrain = new BufferedWriter(fwTrain);
                
//            FileUtils.forceMkdir(foldTrain);
//            int qid = 0;
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
                                    line += (l + 1) + ":" + features.get(l+1).get(docs.get(docID)).get(ent.getKey()).getfValue() + " ";
                                }
                                line += "# " + ent.getKey() + " " + docID;
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
        } catch (IOException ex) {
            log.error(ex);
        }

        
    }
    public static void main(String[] args) throws IOException {
        FeatureCalculator fc = new FeatureCalculator();
        String inDir = Config.configFile.getProperty("CORPUS_Eval_PATH");;
        String outDir =Config.configFile.getProperty("FEATURE_K-FOLD_PATH");;
        String graphFilePath = Config.configFile.getProperty("CONCEPT_GRAPH_FILE_PATH");
        HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> features = fc.propagateAndConcatFeatures(graphFilePath);
        fc.crossValidation(features, outDir, inDir);
    }
    
}
