/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.UvA.MLC.Train;

import java.io.IOException;
import nl.UvA.Utilities.Runner;

/**
 *
 * @author mosi
 */
public class Train {
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Train.class.getName());
    public static void main(String[] args) {
        try {
                        String Command_chmod = "chmod +x " + FileManager.svmRankDir.getPath() +"/svm_rank_learn";
            String Command_svmRank = FileManager.svmRankDir.getPath()+ "/svm_rank_learn -c 20.0 " + svmRankFilesDir.getPath()
                    + "/train.dat " + svmRankFilesDir.getPath() + "/model.dat";
            String Command_chmod = "";
            String Command_svmRank = "";
            Runner.runCommand(Command_chmod);
            Runner.runCommand(Command_svmRank);
        } catch (IOException ex) {
            log.error(ex);
        }
    }
    
    
    
}
