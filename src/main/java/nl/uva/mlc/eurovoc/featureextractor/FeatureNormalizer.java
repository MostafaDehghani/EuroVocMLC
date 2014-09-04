/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.featureextractor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import nl.uva.mlc.eurovoc.EuroVocDoc;

/**
 *
 * @author admin
 */
public class FeatureNormalizer {
    
    public HashMap<String, Feature> normalize(Map<String, Feature> features)
    {
        HashMap<String, Feature> normalizeFeatures = new HashMap<>();
        double sum = 0;
        for(Entry<String, Feature> ent : features.entrySet())
            sum += ent.getValue().getfValue();
        for(Entry<String, Feature> ent : features.entrySet())
        {
            Feature f = ent.getValue();
            if(sum > 0)
                f.setfValue(ent.getValue().getfValue() / sum);
            else
                f.setfValue(0D);
            normalizeFeatures.put(ent.getKey(), f);
        }
        return normalizeFeatures;
    }
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
    public TreeMap<Integer,HashMap<String,HashMap<String,Feature>>> normalize(TreeMap<Integer,HashMap<String,HashMap<String,Feature>>> features){
        TreeMap<Integer,HashMap<String,HashMap<String,Feature>>> RawFeatures = new TreeMap<>();
        FeatureNormalizer fn = new FeatureNormalizer();
        for(Entry<Integer,HashMap<String,HashMap<String,Feature>>> ent : features.entrySet())
        {
            HashMap<String,HashMap<String,Feature>> temp = new HashMap<>();
            for(Entry<String,HashMap<String,Feature>> ent2 : ent.getValue().entrySet())
            {
                temp.put(ent2.getKey(), fn.normalize(ent2.getValue()));
            }
            RawFeatures.put(ent.getKey(), temp);
        }
        
        return RawFeatures;
    }
    
}
