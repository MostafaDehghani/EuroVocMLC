package nl.uva.mlc.eurovoc.irengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import nl.uva.lucenefacility.MyAnalyzer;
import nl.uva.mlc.eurovoc.EuroVocDoc;
import nl.uva.mlc.eurovoc.EuroVocParser;
import static nl.uva.mlc.settings.Config.configFile;
import nl.uva.utilities.StanfordNamedEntityRecognizer;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Mostafa Dehghani
 */
public class TestDataIndexer extends EuroVocParser {

    private final Integer minDocLength = Integer.parseInt(configFile.getProperty("MIN_DOC_LENGTH"));
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TestDataIndexer.class.getName());
    private IndexWriter writer;
    public TestDataIndexer() {

        try {
            log.info("-----------------------INDEXING--------------------------");

            preIndexerCleaning();
            
            //Without Stopwords (In order to make list of common words)
            //
            //
            MyAnalyzer myAnalyzer_noStoplist = new MyAnalyzer(false);
            Analyzer analyzer = myAnalyzer_noStoplist.getAnalyzer(configFile.getProperty("CORPUS_LANGUAGE"));
            IndexWriterConfig irc = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
            this.writer = new IndexWriter(new SimpleFSDirectory(new File(configFile.getProperty("TEST_INDEX_PATH"))), irc);
            fileReader(new File(configFile.getProperty("CORPUS_Eval_PATH")));
            this.writer.commit();
            this.writer.close();
            analyzer.close();
            log.info("-------------------------------------------------");
            log.info("Test index is created successfully...");
            log.info("-------------------------------------------------");
        } catch (FileNotFoundException ex) {
            log.error(ex);
        } catch (IOException ex) {
            log.error(ex);
        }

    }
    @Override
    public void doSomeAction(EuroVocDoc EVdoc) {
        Document doc = new Document();
        if(EVdoc.getText().split("\\s+").length < minDocLength)  //Filtering small documents
            return;
        doc.add(new Field("ID", EVdoc.getId(), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("TITLE", EVdoc.getTitle(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        doc.add(new Field("TEXT", EVdoc.getText(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        String nes="";
        for (String s : StanfordNamedEntityRecognizer.NER(EVdoc.getText())) {
            nes += s.trim() + "\n";
        }
        doc.add(new Field("NAMEDENTITIES", nes, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
        String Classes = "";
        for (String s : EVdoc.getClasses()) {
            Classes += s + " ";
        }
        doc.add(new Field("CLASSES", Classes.trim(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));
        try {
            this.writer.addDocument(doc);
        } catch (IOException ex) {
            log.error(ex);
        }
        log.info("Document " + EVdoc.getId() + " has been indexed successfully...");
    }
    
    private void preIndexerCleaning() {
        try {
            File tmpIndex = new File(configFile.getProperty("TEST_INDEX_PATH"));
            if (tmpIndex.exists()) {
                FileUtils.deleteDirectory(tmpIndex);
                log.info("Deletting the existing test_index directory on: " + configFile.getProperty("TEST_INDEX_PATH"));
                FileUtils.forceMkdir(new File(configFile.getProperty("TEST_INDEX_PATH")));
                log.info("Making test_index directory on: " + configFile.getProperty("TEST_INDEX_PATH"));
            }

        } catch (IOException ex) {
            log.error(ex);
        }
        log.info("\n\n -----------------------CLeaning Finished--------------------------\n");
    }
}
