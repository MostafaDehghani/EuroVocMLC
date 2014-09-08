/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.featureextractor;

/**
 *
 * @author mosi
 */
public class main {
    public static void main(String[] args) {
        
       //String choice = args[0];
       String choice = "2";
       if(choice.equals("1")){
        RawFeatureCalculator rfc = new RawFeatureCalculator();
        rfc.main();
       }
       else if(choice.equals("2")){
        FeaturePropagator fp = new FeaturePropagator();
        fp.main();
       }
       else if(choice.equals("3")){
        PropagationAnalyzer pa = new PropagationAnalyzer();
        pa.main();
       }
    }
    
}
