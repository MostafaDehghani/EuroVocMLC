/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.main;

import nl.uva.mlc.eurovoc.analyzer.PropagationAnalyzer;
import nl.uva.mlc.eurovoc.dataprocessor.CV_kFoldGenerator;
import nl.uva.mlc.eurovoc.dataprocessor.DataSeperator;
import nl.uva.mlc.eurovoc.featureextractor.FeaturePropagator;
import nl.uva.mlc.eurovoc.featureextractor.RawFeatureCalculator;
import nl.uva.mlc.eurovoc.irengine.Indexer;

/**
 *
 * @author mosi
 */
public class main {
    
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(main.class.getName());
    public static void main(String[] args) {
        
       if(args == null){
           log.info("The parameter should be set: "
                   + "\n 0:data seperation"
                   + "\n 1:Indesing"
                   + "\n 2:Raw feature claculating"
                   + "\n 3:Propagating  -> alongs with the all_folds"
                   + "\n 4:Kfold  generating -> alongs with the all_folds kFoldDir and k"
                   + "\n 5:Analysing");
           return;
       }
       String choice = args[0];
       if(choice.equals("0")){
        DataSeperator ds = new DataSeperator();
        ds.main();
        log.info("Data Seperation is finished...");
       }
       else if(choice.equals("1")){
            new Indexer();
            log.info("Indexing is finished....");
       }
       else if(choice.equals("2")){
        RawFeatureCalculator rfc = new RawFeatureCalculator();
        rfc.main();
        log.info("Raw feature extraction is finished...");
       }
       else if(choice.equals("3")){
        FeaturePropagator fp = new FeaturePropagator();
        fp.main(args[1]);
        log.info("freature propagation is finished...");
       }
       else if(choice.equals("4")){
        CV_kFoldGenerator cvkfg = new CV_kFoldGenerator();
        cvkfg.main(args[1],args[2],Integer.parseInt(args[3]));
        log.info("k-fold generation is finished...");
       }
       else if(choice.equals("5")){
        PropagationAnalyzer pa = new PropagationAnalyzer();
        pa.main();
        log.info("Analysing is finished...");
       }
       else{
           log.info("The parameter should be set correctly...");
       }
    }
    
}
