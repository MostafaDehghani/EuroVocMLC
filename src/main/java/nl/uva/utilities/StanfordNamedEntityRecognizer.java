/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.utilities;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 **      به نام خدا     ** 
 * @author Mostafa Dehghani
 *
 **/
public class StanfordNamedEntityRecognizer {
    public AbstractSequenceClassifier<CoreLabel> Classifier[];  
    public StanfordNamedEntityRecognizer() {
        URL c1url  = this.getClass().getClassLoader().getResource("stanford-ner/classifiers/english.all.3class.distsim.crf.ser.gz");
        URL c2url  = this.getClass().getClassLoader().getResource("stanford-ner/classifiers/english.conll.4class.distsim.crf.ser.gz");
        URL c3url  = this.getClass().getClassLoader().getResource("stanford-ner/classifiers/english.muc.7class.distsim.crf.ser.gz");
        String classifierPath[] ={c1url.getPath(), c2url.getPath(),c3url.getPath()};
        this.Classifier = new AbstractSequenceClassifier[classifierPath.length];
        for(int i=0; i<Classifier.length ;i++){
            Classifier[i]= CRFClassifier.getClassifierNoExceptions(classifierPath[i]);
        }
       
    }
    public  List<String> NER(String Input){
      List<String> Output = new ArrayList<String>();
      for(int i=0; i<Classifier.length ;i++){
      String Text_NER;            
          try {
              Text_NER = Classifier[i].classifyWithInlineXML(Input);
          } catch (Exception e) {
              continue;
          }
        Matcher m = Pattern.compile("<([A-Za-z0-9]+?)>(.*?)<(/[A-Za-z0-9]+?)>").matcher(Text_NER);
       List<String> tmpOut = new ArrayList<String>();
        while(m.find()){
            if(!Output.contains(m.group(2)))
                    tmpOut.add(m.group(2));
        }
        Output.addAll(tmpOut);
        
      }
      return Output;
    }
}
