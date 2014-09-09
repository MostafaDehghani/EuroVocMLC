/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.dataprocessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static nl.uva.mlc.settings.Config.configFile;
import org.apache.commons.io.FileUtils;


/**
 *
 * @author admin
 */
public class DataSeperator {
        
        static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DataSeperator.class.getName());
        private String conceptDir = configFile.getProperty("CORPUS_Con_PATH");
        private String evalDir = configFile.getProperty("CORPUS_Eval_PATH");
        private String maindocPath = configFile.getProperty("MAIN_DOC_PATH");
        private Double conceptPercentage = Double.parseDouble(configFile.getProperty("CONCEPT_PERCENTAGE"))/100;
        private Integer conceptPerc;
        public int fileCount = 0;

        public DataSeperator() {
            File f = new File(maindocPath);
            conceptPerc = (int)(f.listFiles().length * conceptPercentage);
        }
        
        public void Seperator(File mainFile, String ConceptDir, String TrainDir){
        File[] files = mainFile.listFiles();
        for(File folder:files){
            if(folder.isDirectory())
            {
                for(File file : folder.listFiles())
                    if(file.getName().startsWith("jrc")&& file.getName().endsWith(".xml"))
                    {

                        if(fileCount <= conceptPerc)
                        {
                            File f = new File(ConceptDir + "/" + file.getName());
                            try {
                                Files.copy(file.toPath(), f.toPath(), REPLACE_EXISTING);
                            } catch (IOException ex) {
                                log.error(ex);
                            }
                        }
                        else
                        {
                            File f = new File(TrainDir + "/" + file.getName());
                            try {
                                Files.copy(file.toPath(), f.toPath(), REPLACE_EXISTING);
                            } catch (IOException ex) {
                               log.error(ex);
                            }
                        }
                        fileCount ++;
                    }
            }
            else
                {
                    if(folder.getName().startsWith("jrc")&& folder.getName().endsWith(".xml"))
                    {

                        if(fileCount <= conceptPerc)
                        {
                            File f = new File(ConceptDir + "/" + folder.getName());
                            try {
                                Files.copy(folder.toPath(), f.toPath(), REPLACE_EXISTING);
                            } catch (IOException ex) {
                               log.error(ex);
                            }
                        }
                        else
                        {
                            File f = new File(TrainDir + "/" + folder.getName());
                            try {
                                Files.copy(folder.toPath(), f.toPath(), REPLACE_EXISTING);
                            } catch (IOException ex) {
                               log.error(ex);
                            }
                        }
                        fileCount ++;
                    }

                }
        }
        
        
    }
        
    public void foldCreator(int k, String inDir, String outDir){
            try {
                File out = new File(outDir);
                FileUtils.forceMkdir(out);
                File in = new File(inDir);
                File[] inFiles = in.listFiles();
                int inNumFiles = inFiles.length;
                int numFilesPerFold = inNumFiles / k;
                for(int i = 0; i < k; i++)
                {
                    File fold = new File(outDir + "/fold" + i + "/test");
                    FileUtils.forceMkdir(fold);
                    for(int j = i * numFilesPerFold; j < (i + 1) * numFilesPerFold; j++)
                    {
                        if(inFiles[j].getName().startsWith("jrc")&& inFiles[j].getName().endsWith(".xml")){
                            File f = new File(fold.getAbsoluteFile() + "/" + inFiles[j].getName());
                            Files.copy(inFiles[j].toPath(), f.toPath(), REPLACE_EXISTING);
                        }
                    }
                    File foldTrain = new File(outDir + "/fold" + i + "/train");
                    FileUtils.forceMkdir(foldTrain);
                    for(int j = 0; j < inFiles.length; j++)
                    {
                        if(j < i * numFilesPerFold || j >= (i + 1) * numFilesPerFold)
                        {
                            if(inFiles[j].getName().startsWith("jrc")&& inFiles[j].getName().endsWith(".xml")){
                                File f = new File(foldTrain.getAbsoluteFile() + "/" + inFiles[j].getName());
                                Files.copy(inFiles[j].toPath(), f.toPath(), REPLACE_EXISTING);
                            }
                        }
                    }
                    
                }   } catch (IOException ex) {
                    log.error(ex);
                }
    }
    public void main(){
        this.Seperator(new File(this.maindocPath), this.conceptDir, this.evalDir);
    }
    
}
