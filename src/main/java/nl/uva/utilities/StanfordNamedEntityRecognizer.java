/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 *
 **      به نام خدا     ** 
 * @author Mostafa Dehghani
 *
 **/
public class StanfordNamedEntityRecognizer {
    public static String Classifieradd[] ={"src/main/resources/stanford-ner/classifiers/english.all.3class.distsim.crf.ser.gz"
    , "src/main/resources/stanford-ner/classifiers/english.conll.4class.distsim.crf.ser.gz"
    , "src/main/resources/stanford-ner/classifiers/english.muc.7class.distsim.crf.ser.gz"};
    public static final AbstractSequenceClassifier<CoreLabel> Classifier[] =  new AbstractSequenceClassifier[Classifieradd.length];
    
    static{
        for(int i=0; i<Classifieradd.length ;i++){
            Classifier[i]= CRFClassifier.getClassifierNoExceptions(Classifieradd[i]);
        }
    }

    
    public static List<String> NER(String Input){

      List<String> Output = new ArrayList<String>();
      for(int i=0; i<Classifier.length ;i++){
      String Text_NER;            
          try {
              Text_NER = Classifier[i].classifyWithInlineXML(Input);
          } catch (Exception e) {
              continue;
          }
        Matcher m = Pattern.compile("<([A-Za-z0-9]+?)>(.*?)<(/[A-Za-z0-9]+?)>").matcher(Text_NER);
        while(m.find()){
                    Output.add(m.group(2));
        }
      }
      return Output;
    }
}
