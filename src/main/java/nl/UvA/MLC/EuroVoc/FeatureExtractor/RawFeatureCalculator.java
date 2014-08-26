/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.UvA.MLC.EuroVoc.FeatureExtractor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import nl.UvA.MLC.EuroVoc.EuroVocDoc;
import nl.UvA.MLC.EuroVoc.EuroVocParser;
import static nl.UvA.MLC.Settings.Config.configFile;
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
        switch (featureNumber) {
            case 1:
                f = fd.F_1_2_3_retrievalBased(doc,"TEXT", "LMD");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 2:
                f = fd.F_1_2_3_retrievalBased(doc,"TEXT", "LMJM");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 3:
                f = fd.F_1_2_3_retrievalBased(doc,"TEXT", "BM25");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 4:
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "LMD");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 5:
                f = fd.F_1_2_3_retrievalBased(doc, "TITLE", "LMJM");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 6:
                f = fd.F_1_2_3_retrievalBased(doc,"TITLE","BM25");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 7:
                f = fd.F_1_2_3_retrievalBased(doc, "DESC","LMD");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 8:
                f = fd.F_1_2_3_retrievalBased(doc, "DESC", "LMJM");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 9:
                f = fd.F_1_2_3_retrievalBased(doc, "DESC","BM25");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 10:
                f = fd.F_1_2_3_retrievalBased(doc, "UNDESC", "LMD");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 11:
                f = fd.F_1_2_3_retrievalBased(doc, "UNDESC", "LMJM");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 12:
                f = fd.F_1_2_3_retrievalBased(doc, "UNDESC", "BM25");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 13:
                f = fd.F_1_2_3_retrievalBased(doc, "CUMDESC","LMD");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 14:
                f = fd.F_1_2_3_retrievalBased(doc, "CUMDESC", "LMJM");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 15:
                f = fd.F_1_2_3_retrievalBased(doc, "CUMDESC", "BM25");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 16:
                f = fd.F_1_2_3_retrievalBased(doc, "CUMUNDESC", "LMD");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 17:
                f = fd.F_1_2_3_retrievalBased(doc, "CUMUNDESC", "LMJM");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 18:
                f = fd.F_1_2_3_retrievalBased(doc, "CUMUNDESC", "BM25");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 19:
                f = fd.F_4_degreeInHierarchy(doc,"p");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 20:
                f = fd.F_4_degreeInHierarchy(doc,"c");
                this.feature_allQ_allD.put(doc, f);
                break;
            case 21:
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
            this.featureNumbers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
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
    
    public static void main(String[] args) {
        RawFeatureCalculator rfc = new RawFeatureCalculator();
        HashMap<Integer,HashMap<EuroVocDoc,HashMap<String,Feature>>> RawFeatures = rfc.conceptBaseFeatureCalc();
        System.out.println("==");
    }

}
