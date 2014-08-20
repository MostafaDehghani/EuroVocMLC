package nl.UvA.MLC.EuroVoc.IREngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import nl.UvA.LuceneFacility.IndexInfo;
import nl.UvA.LuceneFacility.MyAnalyzer;
import nl.UvA.MLC.EuroVoc.EuroVocConcept;
import nl.UvA.MLC.EuroVoc.EuroVocDoc;
import nl.UvA.MLC.EuroVoc.EuroVocParser;
import static nl.UvA.MLC.Settings.Config.configFile;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.xml.sax.SAXException;

/**
 *
 * @author Mostafa Dehghani
 */
public class Indexer extends EuroVocParser {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Indexer.class.getName());
    private IndexWriter writer;
    private final Boolean stemming = Boolean.valueOf(configFile.getProperty("IF_STEMMING"));
    private final Boolean commonWordsRemoving = Boolean.valueOf(configFile.getProperty("IF_STOPWORD_REMOVING"));
    private Map<String, Analyzer> analyzerMap = new HashMap<String, Analyzer>();
    public Indexer() {

        try {
            log.info("-----------------------INDEXING--------------------------");

            preIndexerCleaning();
            analyzerMap.put("ID", new StandardAnalyzer(Version.LUCENE_CURRENT));
            analyzerMap.put("CLASSES", new StandardAnalyzer(Version.LUCENE_CURRENT));
            analyzerMap.put("DOCS", new StandardAnalyzer(Version.LUCENE_CURRENT));
            
            //Without Stopwords (In order to make list of common words)
            MyAnalyzer myAnalyzer_noStoplist = new MyAnalyzer(stemming);
            Analyzer analyzer_1 = myAnalyzer_noStoplist.getAnalyzer(configFile.getProperty("CORPUS_LANGUAGE"));
            PerFieldAnalyzerWrapper prfWrapper_1 = new PerFieldAnalyzerWrapper(analyzer_1, analyzerMap);
            IndexWriterConfig irc_1 = new IndexWriterConfig(Version.LUCENE_CURRENT, prfWrapper_1);
            this.writer = new IndexWriter(new SimpleFSDirectory(new File(configFile.getProperty("DOC_TMP_INDEX_PATH"))), irc_1);
            fileReader(new File(configFile.getProperty("CORPUS_Con_PATH")));
            this.writer.commit();
            this.writer.close();
            analyzer_1.close();
            prfWrapper_1.close();
            log.info("-------------------------------------------------");
            log.info("Temp index is created successfully...");
            log.info("-------------------------------------------------");

            if (!commonWordsRemoving) {
                File index = new File(configFile.getProperty("DOC_TMP_INDEX_PATH"));
                File newIndex = new File(configFile.getProperty("DOC_INDEX_PATH"));
                index.renameTo(newIndex);
                IndexReader ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("DOC_INDEX_PATH"))));
                concepptIndexer(ireader, null);
            } else {
                IndexReader tmp_ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("DOC_TMP_INDEX_PATH"))));
                IndexInfo iInfo = new IndexInfo(tmp_ireader);
                ArrayList<String> commonWs = iInfo.getTopTerms_TF("TEXT", 50);
                MyAnalyzer myAnalyzer_Stoplist = new MyAnalyzer(stemming, commonWs);
                Analyzer analyzer_2 = myAnalyzer_Stoplist.getAnalyzer(configFile.getProperty("CORPUS_LANGUAGE"));
                PerFieldAnalyzerWrapper prfWrapper_2 = new PerFieldAnalyzerWrapper(analyzer_2, analyzerMap);
                IndexWriterConfig irc_2 = new IndexWriterConfig(Version.LUCENE_CURRENT, prfWrapper_2);
                this.writer = new IndexWriter(new SimpleFSDirectory(new File(configFile.getProperty("DOC_INDEX_PATH"))), irc_2);
                fileReader(new File(configFile.getProperty("CORPUS_Con_PATH")));
                this.writer.commit();
                this.writer.close();
                analyzer_2.close();
                prfWrapper_2.close();
                IndexReader ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("DOC_INDEX_PATH"))));
                concepptIndexer(ireader, commonWs);
            }
        } catch (FileNotFoundException ex) {
            log.error(ex);
        } catch (IOException ex) {
            log.error(ex);
        }

    }

    private void concepptIndexer(IndexReader docsIReader, ArrayList<String> commonWs) {
        try {
            MyAnalyzer myAnalyzer = null;
            if (commonWs == null) {
                myAnalyzer = new MyAnalyzer(stemming, commonWs);
            } else {
                myAnalyzer = new MyAnalyzer(stemming);
            }
            Analyzer analyzer = myAnalyzer.getAnalyzer(configFile.getProperty("CORPUS_LANGUAGE"));
            PerFieldAnalyzerWrapper prfWrapper = new PerFieldAnalyzerWrapper(analyzer, analyzerMap);
            IndexWriterConfig irc = new IndexWriterConfig(Version.LUCENE_CURRENT, prfWrapper);
            this.writer = new IndexWriter(new SimpleFSDirectory(new File(configFile.getProperty("CONCEPT_INDEX_PATH"))), irc);
            TermsEnum te = MultiFields.getTerms(docsIReader, "CLASSES").iterator(null);
            BytesRef term;
            while ((term = te.next()) != null) {
                DocsEnum docsEnum = te.docs(null, null);
                EuroVocConcept evc = ConceptGenerator(term, docsEnum, docsIReader);
                IndexConcept(evc);
            }
            this.writer.commit();
            this.writer.close();
            analyzer.close();
            prfWrapper.close();
        } catch (IOException ex) {
            log.error(ex);
        }
        log.info("-------------------------------------------------");
        log.info("Concept index is created successfully...");
        log.info("-------------------------------------------------");

    }

    private void IndexConcept(EuroVocConcept evc) {
        
        Document doc = new Document();
        doc.add(new Field("ID", evc.getId(), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("TITLE", evc.getTitle(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        doc.add(new Field("TEXT", evc.getText(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        String docs = "";
        for (String s : evc.getDocs()) {
            docs += s + " ";
        }
        doc.add(new Field("DOCS", docs.trim(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));
        try {
            this.writer.addDocument(doc);
        } catch (IOException ex) {
            log.error(ex);
        }
        log.info("Concept " + evc.getId() + " has been indexed successfully...");
    }

    private EuroVocConcept ConceptGenerator(BytesRef cID, DocsEnum docsEnum, IndexReader iReader) {
        EuroVocConcept evc = null;
        try {
            ArrayList<String> docs = new ArrayList<>();
            StringBuilder textSB = new StringBuilder();
            StringBuilder titleSB = new StringBuilder();
            int docIdEnum;
            while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
                Document doc = iReader.document(docIdEnum);
                docs.add(doc.get("ID"));
                textSB.append(doc.get("TEXT") + "\n");
                titleSB.append(doc.get("TITLE") + "\n");
            }
            evc = new EuroVocConcept(cID.utf8ToString(), textSB.toString().trim(), titleSB.toString().trim(), docs);
        } catch (IOException ex) {
            log.error(ex);
        }
        return evc;
    }

    @Override
    public void doSomeAction(EuroVocDoc EVdoc) {
        Document doc = new Document();
        doc.add(new Field("ID", EVdoc.getId(), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("TITLE", EVdoc.getTitle(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        doc.add(new Field("TEXT", EVdoc.getText(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        String Classes = "";
        for (String s : EVdoc.getClasses()) {
            Classes += s + " ";
        }
        doc.add(new Field("CLASSES", Classes.trim(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));
        try {
            this.writer.addDocument(doc);
        } catch (IOException ex) {
            log.error(ex);
        }
        log.info("Document " + EVdoc.getId() + " has been indexed successfully...");
    }
    
        private void preIndexerCleaning() {
        try {
            File tmpIndex = new File(configFile.getProperty("DOC_TMP_INDEX_PATH"));
            if (tmpIndex.exists()) {
                FileUtils.deleteDirectory(tmpIndex);
                log.info("Deletting the existing tmp_index directory on: " + configFile.getProperty("DOC_TMP_INDEX_PATH"));
                FileUtils.forceMkdir(new File(configFile.getProperty("DOC_TMP_INDEX_PATH")));
                log.info("Making tmp_index directory on: " + configFile.getProperty("DOC_TMP_INDEX_PATH"));
            }

            File Index = new File(configFile.getProperty("DOC_INDEX_PATH"));
            if (Index.exists()) {
                FileUtils.deleteDirectory(Index);
                log.info("Deletting the existing index directory on: " + configFile.getProperty("DOC_INDEX_PATH"));
                FileUtils.forceMkdir(new File(configFile.getProperty("DOC_INDEX_PATH")));
                log.info("Making tmp_index directory on: " + configFile.getProperty("DOC_INDEX_PATH"));
            }

            File concept_Index = new File(configFile.getProperty("CONCEPT_INDEX_PATH"));
            if (concept_Index.exists()) {
                FileUtils.deleteDirectory(concept_Index);
                log.info("Deletting the existing index directory on: " + configFile.getProperty("CONCEPT_INDEX_PATH"));
                FileUtils.forceMkdir(new File(configFile.getProperty("CONCEPT_INDEX_PATH")));
                log.info("Making tmp_index directory on: " + configFile.getProperty("CONCEPT_INDEX_PATH"));
            }
        } catch (IOException ex) {
            log.error(ex);
        }
        log.info("\n\n -----------------------CLeaning Finished--------------------------\n");
    }
        
        
    public static void main(String[] args) throws ParserConfigurationException, SAXException, SQLException {
        new Indexer();
    }
}
