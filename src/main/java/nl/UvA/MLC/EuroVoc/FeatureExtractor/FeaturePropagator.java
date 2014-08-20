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

/**
 *
 * @author admin
 */
public class FeaturePropagator {
    int numIteration = 2;
    double lambda = 0.8;
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FeaturePropagator.class.getName());
    public Map<String, Map<String, Double>> conceptGraph = new HashMap<String, Map<String, Double>>();
    
    public void FeaturePropagator(String graphPath){
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(graphPath)));
            String str = "";
            while((str = br.readLine()) != null)
            {
                String[] parts = str.split("\t");
                if(conceptGraph.containsKey(parts[0]))
                {
                    Map<String, Double> temp = conceptGraph.get(parts[0]);
                    temp.put(parts[1], Double.valueOf(parts[2]));
                    conceptGraph.put(parts[0], temp);
                }
                else{
                    Map<String, Double> temp = new HashMap<>();
                    temp.put(parts[1], Double.valueOf(parts[2]));
                    conceptGraph.put(parts[0], temp);
                }
                if(conceptGraph.containsKey(parts[1]))
                {
                    Map<String, Double> temp = conceptGraph.get(parts[1]);
                    temp.put(parts[0], Double.valueOf(parts[2]));
                    conceptGraph.put(parts[1], temp);
                }
                else{
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
    }
    
    public HashMap<String, Feature> propagator(String qId, HashMap<String, Feature> features){
        int itr = 0;
        HashMap<String, Feature> oldValues = features;
        HashMap<String, Feature> newValues = new HashMap<>();
        while(itr < numIteration)
        {
            for(Entry<String, Feature> ent : oldValues.entrySet())
            {
                double newValue = lambda * ent.getValue().getfValue();
                double sumLinks = 0;
                for(Entry<String, Double> ent2 : conceptGraph.get(ent.getKey()).entrySet())
                    sumLinks += ent2.getValue();
                for(Entry<String, Double> ent2 : conceptGraph.get(ent.getKey()).entrySet())
                {
                    if(oldValues.containsKey(ent2.getKey()))
                        newValue += (1 - lambda) * oldValues.get(ent2.getKey()).getfValue() * (ent2.getValue() / sumLinks);
                }
                Feature f = new Feature(ent.getValue().getfName(), newValue, ent.getValue().getdId(), ent.getValue().getdId(), ent.getValue().getdRankq());
                f.setfValue(newValue);
                newValues.put(ent.getKey(), f);
            }
            //oldValues.clear();
            oldValues = newValues;
            itr ++;
        }
        return newValues;
    }
    
}
