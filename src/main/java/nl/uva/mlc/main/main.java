/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.main;

import nl.uva.mlc.eurovoc.analyzer.PropagationAnalyzer;
import nl.uva.mlc.eurovoc.dataprocessor.CV_kFoldGenerator;
import nl.uva.mlc.eurovoc.dataprocessor.DataSeperator;
import nl.uva.mlc.eurovoc.dataprocessor.FoldsConcatinator;
import nl.uva.mlc.eurovoc.featureextractor.FeaturePropagator;
import nl.uva.mlc.eurovoc.featureextractor.KFCPropagator;
import nl.uva.mlc.eurovoc.featureextractor.RawFeatureCalculator;
import nl.uva.mlc.eurovoc.irengine.TestDataIndexer;
import nl.uva.mlc.eurovoc.irengine.TrainDataIndexer;
import nl.uva.mlc.learning.TrecEvalInputProvider;

/**
 *
 * @author mosi
 */
public class main {
    
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(main.class.getName());
    public static void main(String[] args) {
        
       if(args.length <1){
           log.info("The parameter should be set: "
                   + "\n 0:data seperation"
                   + "\n 1:Concepts indexing"
                   + "\n 2:Test/Train indexing"
                   + "\n 3:Raw feature claculating -> param alongs with the output directery"
                   + "\n 4:Propagating  -> param alongs with the all_folds out directory"
                   + "\n 5:Kfold  generating -> param alongs with the all_folds kFoldDir and k"
                   + "\n 6:Analysing -> param alongs with the output directery"
                   + "\n 7:K-Fold propagator -> param alongs with the path of directory contains folds directories"
                   + "\n 8:TrecEval data Provider -> param alongs with textfile, scoresFile, resultFile, judgeFile"
                   + "\n 9:Folds Concatinator -> param alongs with path of dirs/files to be concatinate and output dir/file ");
           return;
       }
       String choice = args[0];
       if(choice.equals("0")){
        DataSeperator ds = new DataSeperator();
        ds.main();
        log.info("Data Seperation is finished...");
       }
       else if(choice.equals("1")){
            new TrainDataIndexer();
            log.info("Train indexing is finished....");
       }
       else if(choice.equals("2")){
            new TestDataIndexer();
            log.info("Test indexing is finished....");
       }
       else if(choice.equals("3")){
        RawFeatureCalculator rfc = new RawFeatureCalculator();
        rfc.main(args[1]);
        log.info("Raw feature extraction is finished...");
       }
       else if(choice.equals("4")){
        FeaturePropagator fp = new FeaturePropagator();
        fp.main(args[1],args[2]);
        log.info("freature propagation is finished...");
       }
       else if(choice.equals("5")){
        CV_kFoldGenerator cvkfg = new CV_kFoldGenerator();
        cvkfg.main(args[1],args[2],Integer.parseInt(args[3]));
        log.info("k-fold generation is finished...");
       }
       else if(choice.equals("6")){
        PropagationAnalyzer pa = new PropagationAnalyzer();
        pa.main(args[1]);
        log.info("Analysing is finished...");
       }
       else if(choice.equals("7")){
           KFCPropagator kfcp = new KFCPropagator();
           kfcp.main(args[1]);
        log.info("K-Fold propagation is finished...");
       }
       else if(choice.equals("8")){
           TrecEvalInputProvider teip = new TrecEvalInputProvider();
           teip.main(args[1], args[2], args[3], args[4]);
           log.info("Input data for treceval are provided...");
       }
       else if(choice.equals("9")){
           FoldsConcatinator fc = new FoldsConcatinator();
           fc.main(args);
           log.info("Folds concatination is finished...");
       }
       else{
           log.info("The parameter should be set correctly...");
       }
    }
}
