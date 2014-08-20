/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.UvA.MLC.EuroVoc.FeatureExtractor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.UvA.MLC.EuroVoc.EuroVocDoc;
import nl.UvA.MLC.EuroVoc.EuroVocParser;
import nl.UvA.MLC.EuroVoc.IREngine.Retrieval;
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
    private Retrieval ret = null;
    private Integer featureNumber = null;
    private Integer[] featureNumbers = null;
    private TreeMap<Integer, String> docsMap = null;
    
    /**
     * <code>featureNumbers</code>: Features numbers are defined as follow: 1-
     * Retrieval based on Language Model using Dirichlet smoothing 2- Retrieval
     * based on Language Model using Jelinek Mercer smoothing 3- Retrieval based
     * on Okapi BM25
     *
     *
     *
     */
    private Retrieval getRetriever() {
        if (this.ret == null) {
            this.ret = new Retrieval();
        }
        return ret;
    }

    @Override
    public void doSomeAction(EuroVocDoc doc) {
        FeaturesDefinition fd = new FeaturesDefinition();
        HashMap<String, Feature> f = null;
        Retrieval retr = null;
        switch (featureNumber) {
            case 1:
                retr = this.getRetriever();
                retr.setIreader(this.ireader);
                retr.setField(this.field);
                retr.setSimFName("LMD");
                retr.setDocsMap(this.docsMap);
                f = fd.F_1_2_3_retrievalBased(doc, retr);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 2:
                retr = this.getRetriever();
                retr.setIreader(this.ireader);
                retr.setField(this.field);
                retr.setSimFName("LMJM");
                retr.setDocsMap(this.docsMap);
                f = fd.F_1_2_3_retrievalBased(doc, retr);
                this.feature_allQ_allD.put(doc, f);
                break;
            case 3:
                retr = this.getRetriever();
                retr.setIreader(this.ireader);
                retr.setField(this.field);
                retr.setSimFName("BM25");
                retr.setDocsMap(this.docsMap);
                f = fd.F_1_2_3_retrievalBased(doc, retr);
                this.feature_allQ_allD.put(doc, f);
                break;

            default:
                System.out.println("Not valid number");
        }
        feature_allQ_allD.put(doc, f);
        System.out.println("doc " + doc.getId() + "is processed...");
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
        this.allfeature_allQ_allD.clear();
        try {
            this.featureNumbers = new Integer[]{1, 2, 3};
//            featureNumbers = new Integer[]{1, 2, 3};
            this.queriesPath = configFile.getProperty("CORPUS_Eval_PATH");
            this.ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("CONCEPT_INDEX_PATH"))));
            this.loadIndexDocs();
            this.field = "TEXT";
            calculateFeatures();
        } catch (IOException ex) {
            log.error(ex);
        }
        return this.allfeature_allQ_allD;
    }

    public HashMap<Integer, HashMap<EuroVocDoc, HashMap<String, Feature>>> docBaseFeatureCalc() {
        allfeature_allQ_allD.clear();
        try {
            featureNumbers = new Integer[]{1, 2, 3};
            this.queriesPath = configFile.getProperty("CORPUS_Eval_PATH");
            this.ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("DOC_INDEX_PATH"))));
            this.loadIndexDocs();
            this.field = "TEXT";
            calculateFeatures();
        } catch (IOException ex) {
            log.error(ex);
        }
        return this.allfeature_allQ_allD;
    }
    private void loadIndexDocs() {
        this.docsMap = new TreeMap();

        for (int i = 0; i < this.ireader.numDocs(); i++) {
            try {
                this.docsMap.put(i, this.ireader.document(i).get("ID"));
            } catch (IOException ex) {
                Logger.getLogger(Retrieval.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
