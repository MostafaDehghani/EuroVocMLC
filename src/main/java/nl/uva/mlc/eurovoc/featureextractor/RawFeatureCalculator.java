/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.mlc.eurovoc.featureextractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import nl.uva.mlc.eurovoc.EuroVocDoc;
import nl.uva.mlc.eurovoc.EuroVocParser;
import nl.uva.mlc.settings.Config;
import static nl.uva.mlc.settings.Config.configFile;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 *
 * @author mosi
 */
public class RawFeatureCalculator extends EuroVocParser {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RawFeatureCalculator.class.getName());
    private String field = null;
    private String queriesPath = null;
    private IndexReader ireader = null;
    private HashMap<EuroVocDoc, HashMap<String, Feature>> feature_allQ_allD = new HashMap<EuroVocDoc, HashMap<String, Feature>>();
    private HashMap<Integer, HashMap<EuroVocDoc, HashMap<String, Feature>>> allfeature_allQ_allD = new HashMap<Integer, HashMap<EuroVocDoc, HashMap<String, Feature>>>();
    private Integer featureNumber = null;
    private Integer[] featureNumbers = null;
    private FeaturesDefinition fd = null;
    
    
    /**
     * <code>featureNumbers</code>: Features numbers are defined as follow: 1-
     * Retrieval based on Language Model using Dirichlet smoothing 2- Retrieval
     * based on Language Model using Jelinek Mercer smoothing 3- Retrieval based
     * on Okapi BM25
     *
     *
     *
     */

    
    
    @Override
    public void doSomeAction(EuroVocDoc doc) {
        HashMap<String, Feature> f = null;
        List<Float> params = null;
        switch (featureNumber) {
            case 1:
                params= Arrays.asList(2000F);
                f = fd.F_1_2_3_retrievalBased(doc,"TEXT", "TEXT", "LMD", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 2:
                params= Arrays.asList(0.6F);
                f = fd.F_1_2_3_retrievalBased(doc,"TEXT", "TEXT", "LMJM", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 3:
                params= Arrays.asList(0.75F);
                f = fd.F_1_2_3_retrievalBased(doc,"TEXT", "TEXT", "BM25", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 4:
                params= Arrays.asList(2000F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "TITLE", "LMD", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 5:
                params= Arrays.asList(0.6F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "TITLE", "LMJM", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 6:
                params= Arrays.asList(0.75F);
                f = fd.F_1_2_3_retrievalBased(doc,"TEXT", "TITLE","BM25", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 7:
                params= Arrays.asList(2000F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "DESC","LMD", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 8:
                params= Arrays.asList(0.6F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "DESC", "LMJM", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 9:
                params= Arrays.asList(0.75F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "DESC","BM25", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 10:
                params= Arrays.asList(2000F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "UNDESC", "LMD", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 11:
                params= Arrays.asList(0.6F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "UNDESC", "LMJM", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 12:
                params= Arrays.asList(0.75F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "UNDESC", "BM25", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 13:
                params= Arrays.asList(2000F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "CUMDESC","LMD", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 14:
                params= Arrays.asList(0.6F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "CUMDESC", "LMJM", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 15:
                params= Arrays.asList(0.75F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "CUMDESC", "BM25", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 16:
                params= Arrays.asList(2000F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "CUMUNDESC", "LMD", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 17:
                params= Arrays.asList(0.6F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "CUMUNDESC", "LMJM", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 18:
                params= Arrays.asList(0.75F);
                f = fd.F_1_2_3_retrievalBased(doc, "TEXT", "CUMUNDESC", "BM25", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 19:
                params= Arrays.asList(1000F);
                f = fd.F_1_2_3_retrievalBased(doc,"TITLE", "TEXT", "LMD", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 20:
                params= Arrays.asList(0.2F);
                f = fd.F_1_2_3_retrievalBased(doc,"TITLE", "TEXT", "LMJM", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 21:
                params= Arrays.asList(0.65F);
                f = fd.F_1_2_3_retrievalBased(doc,"TITLE", "TEXT", "BM25", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 22:
                params= Arrays.asList(1000F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "TITLE", "LMD", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 23:
                params= Arrays.asList(0.2F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "TITLE", "LMJM", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 24:
                params= Arrays.asList(0.65F);
                f = fd.F_1_2_3_retrievalBased(doc,"TITLE", "TITLE","BM25", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 25:
                params= Arrays.asList(1000F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "DESC","LMD", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 26:
                params= Arrays.asList(0.2F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "DESC", "LMJM", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 27:
                params= Arrays.asList(0.65F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "DESC","BM25", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 28:
                params= Arrays.asList(1000F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "UNDESC", "LMD", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 29:
                params= Arrays.asList(0.2F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "UNDESC", "LMJM", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 30:
                params= Arrays.asList(0.65F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "UNDESC", "BM25", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 31:
                params= Arrays.asList(1000F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "CUMDESC","LMD", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 32:
                params= Arrays.asList(0.2F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "CUMDESC", "LMJM", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 33:
                params= Arrays.asList(0.65F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "CUMDESC", "BM25", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 34:
                params= Arrays.asList(1000F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "CUMUNDESC", "LMD", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 35:
                params= Arrays.asList(0.2F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "CUMUNDESC", "LMJM", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 36:
                params= Arrays.asList(0.65F);
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "CUMUNDESC", "BM25", params);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 37:
                f = fd.F_4_degreeInHierarchy(doc,"p");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 38:
                f = fd.F_4_degreeInHierarchy(doc,"c");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 39:
                f = fd.F_5_docNum(doc);
                this.feature_allQ_allD.put(doc, f);
                break;

            default:
                log.info("Not valid feature number: " + featureNumber);
        }
        feature_allQ_allD.put(doc, f);
        log.info("feature " + featureNumber + " has been calculated for document " + doc.getId());
    }

    private void calculateFeatures() {
        for (int fnum : featureNumbers) {
            this.feature_allQ_allD = new HashMap<>();
            this.featureNumber = fnum;
            fileReader(new File(queriesPath));
            this.allfeature_allQ_allD.put(fnum, feature_allQ_allD);
        }
    }

    public HashMap<Integer, HashMap<EuroVocDoc, HashMap<String, Feature>>> conceptBaseFeatureCalc() {
        this.allfeature_allQ_allD = new HashMap<>();
        try {
            this.featureNumbers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39};
            this.queriesPath = configFile.getProperty("CORPUS_Eval_PATH");
            this.ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("CONCEPT_INDEX_PATH"))));
            this.fd = new FeaturesDefinition(this.ireader);
            calculateFeatures();
        } catch (IOException ex) {
            log.error(ex);
        }
        return this.allfeature_allQ_allD;
    }

    public HashMap<Integer, HashMap<EuroVocDoc, HashMap<String, Feature>>> docBaseFeatureCalc() {
        allfeature_allQ_allD = new HashMap<>();
        try {
            featureNumbers = new Integer[]{1, 2, 3};
            this.queriesPath = configFile.getProperty("CORPUS_Eval_PATH");
            this.ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("DOC_INDEX_PATH"))));
            calculateFeatures();
        } catch (IOException ex) {
            log.error(ex);
        }
        return this.allfeature_allQ_allD;
    }
    
     public void main(){
        RawFeatureCalculator rfc = new RawFeatureCalculator();
        HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> rawFeatures = rfc.conceptBaseFeatureCalc();
        //Normalizing Features
        FeatureNormalizer fn = new FeatureNormalizer();
        HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> normalizedRawFeatures = fn.normalize(rawFeatures);
        //K-Fold CV
        String inDir = Config.configFile.getProperty("CORPUS_Eval_PATH");;
        String outDir =Config.configFile.getProperty("FEATURE_K-FOLD_PATH");
        K_Fold_CrossValidation_Original KFCV = new K_Fold_CrossValidation_Original();
        KFCV.crossValidation(rawFeatures, outDir, inDir);
    }

}
