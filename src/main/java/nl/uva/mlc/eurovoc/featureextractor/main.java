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
        
        RawFeatureCalculator rfc = new RawFeatureCalculator();
        rfc.main();
        FeaturePropagator fp = new FeaturePropagator();
        fp.main();
        PropagationAnalyzer pa = new PropagationAnalyzer();
        pa.main();
    }
    
}
