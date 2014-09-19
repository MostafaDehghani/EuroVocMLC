/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.dataprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

/**
 *
 * @author Mosi
 */
public class FoldsConcatinator {
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FoldsConcatinator.class.getName());
    public void DirFilesConcatinator(String[] folds) {
        String outDirPath = folds[folds.length-1];
        File[] files = new File(folds[1]).listFiles();
        for(File f:files){
            HashSet<String> ids = new HashSet<String>();
            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outDirPath +"/"+f.getName())));
                for(int i=1;i<folds.length-1;i++){
                    String currentFile = folds[i]+"/"+ f.getName();
                    BufferedReader br = new BufferedReader(new FileReader(currentFile));
                    log.error("...File " + currentFile + "is proccessed...");
                    String line;
                    while((line=br.readLine())!=null){
                            String[] parts = line.split("\\s+");
                            String tmpId = parts[0]+ "\t" +parts[2];
                            if(ids.contains(tmpId)){
                                log.error("ERROR in merging, duplicate for: \"" + tmpId + "\" on " + currentFile);
                                return;
                            }
                            ids.add(tmpId);
                        pw.println(line);
                    }
                }
                pw.close();
            } catch (IOException ex) {
                log.error(ex);
            }
            log.info("Merged file of all " + f.getName() + " in all folds is generated....");
        }
    }
    public void FilesConcatinator(String[] folds) {
        String outFilePath = folds[folds.length-1];
        HashSet<String> ids = new HashSet<String>();
            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outFilePath)));
                for(int i=1;i<folds.length-1;i++){
                    BufferedReader br = new BufferedReader(new FileReader(folds[i]));
                    log.error("...File " + folds[i] + "is proccessed...");
                    String line;
                    while((line=br.readLine())!=null){
                            String[] parts = line.split("\\s+");
                            String tmpId = parts[0]+ "\t" +parts[2];
                            if(ids.contains(tmpId)){
                                log.error("ERROR in merging, duplicate for: " + tmpId + " on " + folds[i]);
                                return;
                            }
                            ids.add(tmpId);
                        pw.println(line);
                    }
                }
                pw.close();
            } catch (IOException ex) {
                log.error(ex);
            }
            log.info("File  " + outFilePath + " is generated....");
    }
    public void main(String[] args){
        if(new File(args[1]).isDirectory()){
            this.DirFilesConcatinator(args);
        }
        else{
            this.FilesConcatinator(args);
        }
    }
    
}
