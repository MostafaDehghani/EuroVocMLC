/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.featureextractor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import nl.uva.lucenefacility.IndexInfo;
import nl.uva.mlc.eurovoc.EuroVocDoc;
import nl.uva.mlc.eurovoc.irengine.Retrieval;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author mosi
 */
public class FeaturesDefinition {
        
    
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FeaturesDefinition.class.getName());
    private HashMap<String,Feature> classParentsNum = null;
    private HashMap<String,Feature> classChildrenNum = null;
    private HashMap<String,Feature> classDocNum = null;
    private HashMap<String,Feature> classLevel = null;
    private TreeMap<Integer, String> indexId_docId_map = null;
    private IndexReader ireader = null;
    private Retrieval retriver = null;
    
   private Retrieval getRetriever() {
        if (this.retriver == null) {
            this.retriver = new Retrieval();
        }
        return retriver;
    }

    public FeaturesDefinition(IndexReader ireader) {
        this.ireader = ireader;
        this.loadIndexDocs();
    }
    
    private void loadIndexDocs() {
        this.indexId_docId_map = new TreeMap();
        for (int i = 0; i < this.ireader.numDocs(); i++) {
            try {
                this.indexId_docId_map.put(i, this.ireader.document(i).get("ID"));
            } catch (IOException ex) {
                log.error(ex);
            }
        }
    }
        
    public HashMap<String,Feature> F_retrievalBased(EuroVocDoc doc, String queryField, String docField, String simFunc, List<Float> simFuncParams) {
        HashMap<String,Feature> feature_oneQ_allD =  new HashMap<String, Feature>();
        retriver = this.getRetriever();
        retriver.setIreader(this.ireader);
        retriver.setField(docField);
        retriver.setSimFName(simFunc);
        retriver.setParams(simFuncParams);
        retriver.setIndexId_docID_Map(indexId_docId_map);
        try {
            if(queryField.equals("TEXT")){
                feature_oneQ_allD = retriver.searchAndReturnResults(doc.getText(), doc.getId());
            }
            else if(queryField.equals("TITLE")){
                feature_oneQ_allD = retriver.searchAndReturnResults(doc.getTitle(), doc.getId());
            }
        } catch (IOException ex) {
            log.error(ex);
        } catch (ParseException ex) {
            log.error(ex);
        }
        return feature_oneQ_allD;
    } 
    
    public HashMap<String,Feature> F_classDegreeInHierarchy(String pORc){
        HashMap<String,Feature> feature_oneQ_allD =  new HashMap<String, Feature>();
        if(pORc.equals("p")){
            if(this.classParentsNum == null){
                classParentsNum = new HashMap<>();
                IndexInfo iInfo = new IndexInfo(this.ireader);
                for (int i = 0; i < this.ireader.numDocs(); i++) {
                        Feature f = new Feature("pNum", iInfo.getDocumentLength(i, "PARENTS").doubleValue() , this.indexId_docId_map.get(i));
                        classParentsNum.put(this.indexId_docId_map.get(i), f);
                }
            }
            else{
                feature_oneQ_allD = classParentsNum;
            }
        }
        else if(pORc.equals("c")){
            if(this.classChildrenNum == null){
                classChildrenNum = new HashMap<>();
                IndexInfo iInfo = new IndexInfo(this.ireader);
                for (int i = 0; i < this.ireader.numDocs(); i++) {
                        Feature f = new Feature("cNum", iInfo.getDocumentLength(i, "CHILDREN").doubleValue() , this.indexId_docId_map.get(i));
                        classChildrenNum.put(this.indexId_docId_map.get(i), f);
                }
            }
            else{
                feature_oneQ_allD = classChildrenNum;
            }
        }
        return feature_oneQ_allD;
    }
    
    public HashMap<String,Feature> F_classDocNum() {
        if(this.classDocNum == null){
            classDocNum = new HashMap<>();
            for (int i = 0; i < this.ireader.numDocs(); i++) {
                try {
                    String[] docs = ireader.document(i).get("DOCS").split("\\s+");
                    Feature f = new Feature("docNum", (double)docs.length , this.indexId_docId_map.get(i));
                    classDocNum.put(this.indexId_docId_map.get(i), f);
                } catch (IOException ex) {
                    log.error(ex);
                }
            }
        }
        return classDocNum;
    }
    
    public HashMap<String,Feature> F_classLevelInHierarchy()
    {
        if(this.classLevel == null){
            classLevel = new HashMap<>();
            for (int i = 0; i < this.ireader.numDocs(); i++) {
                try {
                    String level = ireader.document(i).get("LEVEL");
                    Feature f = new Feature("level", Double.parseDouble(level), this.indexId_docId_map.get(i));
                    classLevel.put(this.indexId_docId_map.get(i), f);
                } catch (IOException ex) {
                    log.error(ex);
                }
            }
        }
        return classLevel;
    }    
    
    
}
