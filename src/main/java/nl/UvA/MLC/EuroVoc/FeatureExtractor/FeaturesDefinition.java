/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.UvA.MLC.EuroVoc.FeatureExtractor;

import java.io.IOException;
import java.util.HashMap;
import nl.UvA.MLC.EuroVoc.EuroVocDoc;
import static nl.UvA.MLC.EuroVoc.FeatureExtractor.RawFeatureCalculator.log;
import nl.UvA.MLC.EuroVoc.IREngine.Retrieval;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author mosi
 */
public class FeaturesDefinition {
        
    
        /**
     * 
     * @param doc
     * @param simF: 
     *      LMD, // for LMDirichletSimilarity
     *      LMJM, //for LMJelinekMercerSimilarity
     *      BM25, // for Okapi-BM25Similarity
     * @param field
     * @return 
     */
    public HashMap<String,Feature> F_1_2_3_retrievalBased(EuroVocDoc doc, Retrieval retriver) {
        HashMap<String,Feature> feature_oneQ_allD =  new HashMap<String, Feature>();
        try {
            feature_oneQ_allD = retriver.searchAndReturnResults(doc.getText(), doc.getId());
        } catch (IOException ex) {
            log.error(ex);
        } catch (ParseException ex) {
            log.error(ex);
        }
        return feature_oneQ_allD;
    }
    
    public HashMap<String,Feature> F_4_retrievalBased(EuroVocDoc doc, Retrieval retriver) {
        HashMap<String,Feature> feature_oneQ_allD =  new HashMap<String, Feature>();
        try {
            feature_oneQ_allD = retriver.searchAndReturnResults(doc.getText(), doc.getId());
        } catch (IOException ex) {
            log.error(ex);
        } catch (ParseException ex) {
            log.error(ex);
        }
        return feature_oneQ_allD;
    }
}
