/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.UvA.MLC.EuroVoc.FeatureExtractor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
            f.setfValue(ent.getValue().getfValue() / sum);
            normalizeFeatures.put(ent.getKey(), f);
        }
        return normalizeFeatures;
    }
    
}
