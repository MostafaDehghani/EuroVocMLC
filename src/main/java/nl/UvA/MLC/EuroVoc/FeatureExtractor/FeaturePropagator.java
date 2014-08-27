/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.UvA.MLC.EuroVoc.FeatureExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import nl.UvA.MLC.Settings.Config;

/**
 *
 * @author Mostafa Dehgani
 */
public class FeaturePropagator {
    
    HashSet<Integer> propagationBlackList = null;
    Integer numIteration = Integer.parseInt(Config.configFile.getProperty("ITERATION_NUM"));
    Double lambda = Double.parseDouble(Config.configFile.getProperty("LAMBDA"));
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FeaturePropagator.class.getName());
    public Map<String, Map<String, Double>> conceptGraph = new HashMap<String, Map<String, Double>>();

    public FeaturePropagator(String graphPath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(graphPath)));
            String str = "";
            while ((str = br.readLine()) != null) {
                String[] parts = str.split("\t");
                if (conceptGraph.containsKey(parts[0])) {
                    Map<String, Double> temp = conceptGraph.get(parts[0]);
                    temp.put(parts[1], Double.valueOf(parts[2]));
                    conceptGraph.put(parts[0], temp);
                } else {
                    Map<String, Double> temp = new HashMap<>();
                    temp.put(parts[1], Double.valueOf(parts[2]));
                    conceptGraph.put(parts[0], temp);
                }
                if (conceptGraph.containsKey(parts[1])) {
                    Map<String, Double> temp = conceptGraph.get(parts[1]);
                    temp.put(parts[0], Double.valueOf(parts[2]));
                    conceptGraph.put(parts[1], temp);
                } else {
                    Map<String, Double> temp = new HashMap<>();
                    temp.put(parts[0], Double.valueOf(parts[2]));
                    conceptGraph.put(parts[1], temp);
                }

            }
        } catch (FileNotFoundException ex) {
            log.error(ex);
        } catch (IOException ex) {
            log.error(ex);
        }
        
        //load blacklist
        this.propagationBlackList = new HashSet<Integer>();
        for(String s: Config.configFile.getProperty("PROPAGATION_BLACKLIST").split(",")){
            this.propagationBlackList.add(Integer.parseInt(s));
        }
    }

    public HashMap<String, Feature> propagator(String qId, HashMap<String, Feature> features) {
        int itr = 0;
        FeatureNormalizer fn = new FeatureNormalizer();
        HashMap<String, Feature> oldValues = features;
        HashMap<String, Feature> newValues = new HashMap<>();
        while (itr < numIteration) {
            for (Entry<String, Feature> ent : oldValues.entrySet()) {
                double newValue = lambda * ent.getValue().getfValue();
                double sumLinks = 0;
                for (Entry<String, Double> ent2 : conceptGraph.get(ent.getKey()).entrySet()) {
                    sumLinks += ent2.getValue();
                }
                for (Entry<String, Double> ent2 : conceptGraph.get(ent.getKey()).entrySet()) {
                    if (oldValues.containsKey(ent2.getKey())) {
                        newValue += (1 - lambda) * oldValues.get(ent2.getKey()).getfValue() * (ent2.getValue() / sumLinks);
                    }
                }
                Feature f = new Feature(ent.getValue().getfName(), newValue, ent.getValue().getdId(), ent.getValue().getdId(), ent.getValue().getdRankq());
                f.setfValue(newValue);
                newValues.put(ent.getKey(), f);
            }
            oldValues = fn.normalize(newValues);
            itr++;
        }
        return oldValues;
    }

    public TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> propagateAndConcatFeatures() {
        TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> RawFeatures = this.readRawFeatures();
        FeatureNormalizer fn = new FeatureNormalizer();
        TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> tempRawFeatures = new TreeMap<>();
        TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> FinaltempRawFeatures = new TreeMap<>();
        int fnumber = RawFeatures.size();
        for (Entry<Integer, HashMap<String, HashMap<String, Feature>>> ent : RawFeatures.entrySet()) {
            if(this.propagationBlackList.contains(ent.getKey()))
                continue;
            HashMap<String, HashMap<String, Feature>> propagatedFeature = new HashMap<>();
            for (Entry<String, HashMap<String, Feature>> ent2 : ent.getValue().entrySet()) {
                HashMap<String, Feature> proFeatures = this.propagator(ent2.getKey(), ent2.getValue());
                propagatedFeature.put(ent2.getKey(), proFeatures);
            }
            tempRawFeatures.put(++fnumber, propagatedFeature);
        }

        FinaltempRawFeatures = fn.normalize(tempRawFeatures);
        TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> FinalFeatures = new TreeMap<>();
        for (Entry<Integer, HashMap<String, HashMap<String, Feature>>> ent : RawFeatures.entrySet()) {
            FinalFeatures.put(ent.getKey(), ent.getValue());
        }
        for (Entry<Integer, HashMap<String, HashMap<String, Feature>>> ent : FinaltempRawFeatures.entrySet()) {
            FinalFeatures.put(ent.getKey(), ent.getValue());
        }

        return FinalFeatures;
    }

    public TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> readRawFeatures() {
        BufferedReader br = null;
        TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> features = new TreeMap<>();
        try {
            String Kfold_path = Config.configFile.getProperty("FEATURE_K-FOLD_PATH") + "/all_folds.txt";
            br = new BufferedReader(new FileReader(new File(Kfold_path)));
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineParts1 = line.split(" # ");
                String[] lineParts2 = lineParts1[0].split("\\s+");
                String[] lineParts3 = lineParts1[1].split("\\s+");
                for (int i = 2; i < lineParts2.length ; i++) {
                    Integer fNum = Integer.parseInt(lineParts2[i].split(":")[0]);
                    Double fVal = Double.parseDouble(lineParts2[i].split(":")[1]);
                    String qId = lineParts3[0];
                    String dId = lineParts3[1];
                    Feature f = new Feature(fNum.toString(), fVal, qId, dId, lineParts2[0]);
                    HashMap<String, HashMap<String, Feature>> allq_AllD_oneF = features.get(fNum);
                    HashMap<String, Feature> oneQ_allD = null;
                    if (allq_AllD_oneF == null) {
                        allq_AllD_oneF = new HashMap<String, HashMap<String, Feature>>();
                        oneQ_allD = new HashMap<String, Feature>();
                    } else {
                        oneQ_allD = allq_AllD_oneF.get(qId);
                        if (oneQ_allD == null) {
                            oneQ_allD = new HashMap<String, Feature>();
                        }
                    }
                    oneQ_allD.put(dId, f);
                    allq_AllD_oneF.put(qId, oneQ_allD);
                    features.put(fNum, allq_AllD_oneF);
                }
            }
            br.close();
        } catch (FileNotFoundException ex) {
            log.error(ex);
        } catch (IOException ex) {
            log.error(ex);
        }
        return features;
    }

    public static void main(String[] args) {
        String graphFilePath = Config.configFile.getProperty("CONCEPT_GRAPH_FILE_PATH");
        FeaturePropagator fp = new FeaturePropagator(graphFilePath);
        TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> features = fp.propagateAndConcatFeatures();
        //K-Fold CV
        String inDir = Config.configFile.getProperty("CORPUS_Eval_PATH");;
        String outDir = Config.configFile.getProperty("FEATURE_PROPAGATED_K-FOLD_PATH");
        K_Fold_CrossValidation KFCV = new K_Fold_CrossValidation();
        KFCV.crossValidation(features, outDir, inDir);
    }

}
