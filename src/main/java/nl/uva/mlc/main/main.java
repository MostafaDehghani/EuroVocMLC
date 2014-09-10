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
    public static void main(String[] args) {
        
       String choice = args[0];
       if(choice.equals("0")){
        DataSeperator ds = new DataSeperator();
        ds.main();
       }
       else if(choice.equals("1")){
            new Indexer();
       }
       else if(choice.equals("2")){
        RawFeatureCalculator rfc = new RawFeatureCalculator();
        rfc.main();
       }
       else if(choice.equals("3")){
        FeaturePropagator fp = new FeaturePropagator();
        fp.main();
       }
       else if(choice.equals("4")){
        CV_kFoldGenerator cvkfg = new CV_kFoldGenerator();
        cvkfg.main(args[1],args[2],Integer.parseInt(args[3]));
       }
       else if(choice.equals("5")){
        PropagationAnalyzer pa = new PropagationAnalyzer();
        pa.main();
       }
    }
    
}
