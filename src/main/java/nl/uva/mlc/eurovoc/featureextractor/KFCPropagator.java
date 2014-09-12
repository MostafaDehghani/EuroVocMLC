/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.featureextractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import nl.uva.mlc.settings.Config;
import static nl.uva.mlc.settings.Config.configFile;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author admin
 */
public class KFCPropagator {
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(KFCPropagator.class.getName());
    private String KFoldPath = null;
    private int k = Integer.parseInt(configFile.getProperty("K_IN_K-FOLD_CV"));
    public FeaturePropagator fp = null;
    private ArrayList<Integer> itNums = null;
    private ArrayList<Double> alphas = null;

    public KFCPropagator() {
        this.itNums = new ArrayList<Integer>();
        for (String s : Config.configFile.getProperty("ITERATION_NUMS").split(",")) {
            this.itNums.add(Integer.parseInt(s.trim()));
        }
        log.info("Iteration numbers for analyze:" + this.itNums.toString());
        this.alphas = new ArrayList<Double>();
        for (String s : Config.configFile.getProperty("ALPHAS").split(",")) {
            this.alphas.add(Double.parseDouble(s.trim()));
        }
        log.info("Alphas for analyze:" + this.alphas.toString());

    }
    
    
    
    public void propagator()
    {
        this.fp = new FeaturePropagator();
        Map<String, HashMap<String, Double>> scores = new HashMap<>();
        for(int i = 0; i < k; i++)
        {
            try {
                    File file2 = new File(KFoldPath + "/fold" + (i+1) + "/judg.txt");
                    if(file2.exists())
                    	FileUtils.forceDelete(file2);
                    file2.createNewFile();
	            FileWriter fw2;
                    fw2 = new FileWriter(file2);
                    BufferedWriter judg = new BufferedWriter(fw2);

                    BufferedReader testFile = new BufferedReader(new FileReader(new File(KFoldPath + "/fold" + (i+1) + "/test.txt")));
                    BufferedReader scoresFile = new BufferedReader(new FileReader(new File(KFoldPath + "/fold" + (i+1) + "/scores.txt")));

                    String str, str2;
                    while((str = testFile.readLine()) != null)
                    {
                        String[] parts = str.split(" # ")[1].split(" ");
                        if(str.split(" ")[0].equals("1"))
                            judg.write(parts[0] + " 0 " + parts[1] + " 1" + "\n");
                        str2 = scoresFile.readLine();
                        if(scores.containsKey(parts[0]))
                            scores.get(parts[0]).put(parts[1], Double.valueOf(str2));
                        else
                        {
                            HashMap<String, Double> temp = new HashMap<>();
                            temp.put(parts[1], Double.valueOf(str2));
                            scores.put(parts[0], temp);
                        }
                    }
      	            judg.close();
           for(int itr : itNums)
              for(double alpha : this.alphas)
                 {
                    this.fp.alpha = alpha;
                    this.fp.numIteration = itr;
                    File f = new File(KFoldPath + "/fold" + (i+1) + "/anal");
                    if(f.exists())
                       f.delete();
                    else
                       FileUtils.forceMkdir(f);
                    File file = new File(KFoldPath + "/fold" + (i+1)+ "/anal/propagatedRes_" + "alpha" + alpha + "_itrNum" + itr + ".txt");
                    if(file.exists())
                       FileUtils.forceDelete(file);
                    file.createNewFile();

                    FileWriter fw;
                    fw = new FileWriter(file);
                    BufferedWriter propRes = new BufferedWriter(fw);              
                    for(Entry<String, HashMap<String, Double>> ent : scores.entrySet())
                    {
                                FileUtils.forceMkdir(f);
                                HashMap<String, Double> propScores = fp.finalScorePropagator(ent.getValue());
                                MyValueComparator bvc = new MyValueComparator(propScores);
                                TreeMap<String, Double> sorted_map = new TreeMap<>(bvc);
                                sorted_map.putAll(propScores);
                                int rank = 1;
                                for(Entry<String, Double> ent2 : propScores.entrySet())
                                {
                                    String line = ent.getKey() + " 0 " + ent2.getKey() + " " + rank++ + " " + ent2.getValue();
                                    propRes.write(line + " RUN" + "\n");
                                }
                     }
		     log.info("alpha:" + alpha + "iteration:" + itr + " is finished");
		     propRes.close();
                 }
                
            } catch (IOException ex) {
                log.error(ex);
            }
            
        }
    }
        
    public void main(String kFoldPath) {
        this.KFoldPath = kFoldPath;
        this.propagator();
    }
}

class MyValueComparator implements Comparator<String> {

    Map<String, Double> base;

    public MyValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    @Override
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
