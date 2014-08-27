/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.UvA.MLC.EuroVoc.FeatureExtractor;

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
    }
    
}
