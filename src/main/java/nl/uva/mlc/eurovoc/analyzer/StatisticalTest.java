/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.analyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uva.utilities.Runner;

/**
 *
 * @author Mosi
 */
public class StatisticalTest {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StatisticalTest.class.getName());
    private String inFile1;
    private String inFile2;
    private String outDir;
    private HashMap<String,String> ids = null;
    
    
    
//    
//    private void runEvalScript() throws IOException{
//        try {
//            File inF1 = new File(inFile1);
//            File inF2 = new File(inFile2);
//            if(inF1.isDirectory()){
//                inF1 = concatKfolds(inF1);
//            }
//            else{
//                
//            }
//            if(inF2.isDirectory()){
//                inF2 = concatKfolds(inF2);
//            }
//            else{
//                
//            }
//            Runner.runCommand("perl /SigTest/eval-score-mslr.pl " + inF1.getPath() + " ");
//        } catch (IOException ex) {
//            log.error(ex);
//            throw new IOException(ex.getMessage(), ex.getCause());
//        }
//    }
//    private String[] concatKfolds(File foldsDir){
//        log.info("Concatinating all folds in " + foldsDir.getPath());
//        File allFolds_test = new File(this.outDir +"/tmp_test_all_folds_["+foldsDir.getPath()+"].txt");
//        File allFolds_pred = new File(this.outDir +"/tmp_pred_all_folds_["+foldsDir.getPath()+"].txt");
//        
//        log.info("Temp test/pred files are saveded on\n" + allFolds_test.getPath() +"\n and\n" + allFolds_pred.getPath());
//        return new String[]{allFolds_test.getPath(),allFolds_pred.getPath()};
//    }
//    
//    private String[] trecEval_to_L2R(File trecEvalFile){
//        log.info("converting " + trecEvalFile.getPath() + "to L2R format...");
//        BufferedReader br = null;
//        File allFolds_test = new File(this.outDir +"/tmp_test_all_folds_["+trecEvalFile.getPath()+"].txt");
//        File allFolds_pred = new File(this.outDir +"/tmp_pred_all_folds_["+trecEvalFile.getPath()+"].txt");
//        try {
//            BufferedWriter bw_test = new BufferedWriter(new FileWriter(allFolds_test));
//            BufferedWriter bw_pred = new BufferedWriter(new FileWriter(allFolds_pred));
//            br = new BufferedReader(new FileReader(trecEvalFile));
//            String line;
//            if(this.ids==null){
//                ids = new HashMap<>();
//                Integer nid=0;
//                while((line=br.readLine())!=null){
//                String[] parts = line.split("\\s+");
//                    String mid = ids.get(parts[0]);
//                    if(mid==null){
//                        ids.put(mid, nid.toString());
//                        nid++;
//                    }
//                }
//            }
//            while((line=br.readLine())!=null){
//                String[] parts = line.split("\\s+");
//                bw_test.write("\n");
//                
//            }
//            //jrcC2006#060#94-en 0 2525 1450 2.2412117209010628E-4 :
//            //0 qid:1470 1:0.0 2:0.0 3:0.0 4:0.0 5:0.0 6:0.0 7:7.702784216259735E-4 8:3.311740041588372E-4 9:0.0011367466145656411 10:8.967677019159746E-4 11:0.0 12:0.0 # jrc32006R1368-en 2731
//            bw_test.close();
//            bw_pred.close();
//            log.info("Temp test/pred files are saveded on\n" + allFolds_test.getPath() +"\n and\n" + allFolds_pred.getPath());
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(StatisticalTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(StatisticalTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return new String[]{allFolds_test.getPath(),allFolds_pred.getPath()};
//    }
}
