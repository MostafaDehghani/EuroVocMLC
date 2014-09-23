/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.mlc.eurovoc.analyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uva.mlc.eurovoc.EuroVocDoc;
import nl.uva.mlc.eurovoc.featureextractor.Feature;
import nl.uva.mlc.eurovoc.featureextractor.FeatureNormalizer;
import nl.uva.mlc.eurovoc.featureextractor.FeaturePropagator;
import nl.uva.mlc.eurovoc.featureextractor.FeaturesDefinition;
import nl.uva.mlc.eurovoc.featureextractor.RawFeatureCalculator;
import nl.uva.mlc.settings.Config;
import static nl.uva.mlc.settings.Config.configFile;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 *
 * @author Mosi
 */
public class PropagationAnalyzer {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PropagationAnalyzer.class.getName());
    private ArrayList<Integer> fNum = null;
    private ArrayList<Integer> itNums = null;
    private ArrayList<Double> alphas = null;

    private IndexReader trainIreader = null;
    private FeaturesDefinition fd = null;
    private RawFeatureCalculator rfc = null;
    private FeatureNormalizer fn = null;
    private FeaturePropagator fp = null;
    private String outDir;

    public PropagationAnalyzer() {

        this.fNum = new ArrayList<Integer>();
        for (String s : Config.configFile.getProperty("FEATURE_NUM_FOR_ANALYSIS").split(",")) {
            this.fNum.add(Integer.parseInt(s.trim()));
        }
        log.info("Feature for analyze:" + this.fNum.toString());
        this.itNums = new ArrayList<Integer>();
        for (String s : Config.configFile.getProperty("ITERATION_NUMS").split(",")) {
            this.itNums.add(Integer.parseInt(s.trim()));
        }
        log.info("Iteration numbers for analyze:" + this.itNums.toString());
        this.alphas = new ArrayList<Double>();
        for (String s : Config.configFile.getProperty("ALPHAS").split(",")) {
            this.alphas.add(Double.parseDouble(s.trim()));
        }
        log.info("Alphas for analyze:" + this.alphas.toString());
        try {
            this.trainIreader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("CONCEPT_INDEX_PATH"))));
        } catch (IOException ex) {
            log.error(ex);
        }
        this.fd = new FeaturesDefinition(trainIreader);
        this.rfc = new RawFeatureCalculator();
        this.rfc.setFd(this.fd);
        this.fp = new FeaturePropagator();
        this.fn = new FeatureNormalizer();

        //Cleaning
        File f;
        for (int fnum : this.fNum) {
            f = new File(this.outDir
                    + "/all_folds_F-" + fnum + ".txt");
            if (f.exists()) {
                f.delete();
                log.info("Deletting the existing files on: " + f.getPath());
            }

        }
        for (int fnum : this.fNum) {
            for (int itNum : itNums) {
                for (double alpha : alphas) {
                    String fileName = this.outDir
                            + "/all_folds_F-" + fnum + "_Alpha-" + alpha
                            + "_itNum-" + itNum + ".txt";
                    f = new File(fileName);
                    if (f.exists()) {
                        f.delete();
                        log.info("Deletting the existing files on: " + f.getPath());
                    }
                }
            }
        }
    }

    public void Quering(EuroVocDoc docAsQuery) {
        addQueryToJudgmentFile(docAsQuery);

        for (int fnum : this.fNum) {
            HashMap<String, Feature> oneQ_allD = new HashMap<String, Feature>();
            oneQ_allD = rfc.calculateFeatures(docAsQuery, fnum);
            HashMap<String, Feature> oneQ_allD_Normalized = this.fn.normalize(oneQ_allD);
            String raw_fileName = this.outDir
                    + "/all_folds_F-" + fnum + ".txt";
            addQueryToResultsFile(docAsQuery, oneQ_allD_Normalized, raw_fileName);
            for (int itNum : itNums) {
                for (double alpha : alphas) {
                    String fileName = this.outDir
                            + "/all_folds_F-" + fnum + "_Alpha-" + alpha
                            + "_itNum-" + itNum + ".txt";
                    fp.setAlpha(alpha);
                    fp.setNumIteration(itNum);
                    HashMap<String, Feature> oneQ_allD_propagated = this.fp.propagator(oneQ_allD_Normalized);
                    addQueryToResultsFile(docAsQuery, oneQ_allD_propagated, fileName);
                }
            }
        }
        log.info("All cases has been calculated for document " + docAsQuery.getId());
    }

    private void addQueryToResultsFile(EuroVocDoc docAsQ, HashMap<String, Feature> oneQ_allD, String fileName) {
        ValueComparator bvc = new ValueComparator(oneQ_allD);
        TreeMap<String, Feature> sorted_map = new TreeMap<String, Feature>(bvc);
        sorted_map.putAll(oneQ_allD);
        int rank = 1;
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(fileName, true));
            for (Entry<String, Feature> ent : sorted_map.entrySet()) {
                String line = docAsQ.getId() + " 0 " + ent.getKey() + " " + rank++ + " " + ent.getValue().getfValue();
                pw.write(line + " :D\n");
            }
            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(PropagationAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addQueryToJudgmentFile(EuroVocDoc docAsQuery) {
        PrintWriter pw = null;
        try {
            String jugeFileName = this.outDir + "/judg.txt";
            pw = new PrintWriter(new FileWriter(jugeFileName, true));
            for (String classlbl : docAsQuery.getClasses()) {
                String line = docAsQuery.getId() + " 0 " + classlbl + " 1";
                pw.write(line + "\n");
            }
            pw.close();
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    private void testIndexDocReader() {
        try {
            IndexReader testIreader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("TEST_INDEX_PATH"))));
            for (int i = 0; i < testIreader.numDocs(); i++) {
                String id = testIreader.document(i).get("ID");
                String title = testIreader.document(i).get("TITLE");
                String text = testIreader.document(i).get("TEXT");
                String namedEntities = testIreader.document(i).get("NAMEDENTITIES");
                String[] classes = testIreader.document(i).get("CLASSES").split("\\s+");
                EuroVocDoc doc = new EuroVocDoc(id, title, text, namedEntities, new ArrayList<String>(Arrays.asList(classes)));
                Quering(doc);

            }
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    public void main(String outDir) {
        this.outDir = outDir;
        this.testIndexDocReader();
    }

}

class ValueComparator implements Comparator<String> {

    Map<String, Feature> base;

    public ValueComparator(Map<String, Feature> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a).getfValue() >= base.get(b).getfValue()) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
