/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.UvA.MLC.EuroVoc.GraphEngine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static nl.UvA.MLC.Settings.Config.configFile;

/**
 *
 * @author admin
 */
public class HierarchyGraphConstructor {
    
public static void hierarchyConstructor() throws FileNotFoundException, IOException
{
    BufferedReader bf = new BufferedReader(new FileReader(new File("/Users/admin/Desktop/relation_bt.xml")));
    File file = new File("hiearchy");
    FileWriter fw = new FileWriter(file);
    BufferedWriter bw = new BufferedWriter(fw);

    String str;
    boolean sflg = false;
    boolean dflg = false;
    String source = "";
    String destination = "";
    while((str = bf.readLine()) != null)
    {
        if(str.contains("SOURCE_ID"))
        {
            sflg = true;
            source = str.split(">")[1].split("<")[0];
        }
        if(str.contains("CIBLE_ID"))
        {
            dflg = true;
            destination = str.split(">")[1].split("<")[0];
        }
        
        if(sflg && dflg)
        {
            bw.write(source + "\t" + destination + "\n");
            sflg = false;
            dflg = false;
        }
    }
    
    bw.close();
}

    public static void main(String[] args) throws IOException {
        hierarchyConstructor();
    }
    
}
