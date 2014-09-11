package nl.uva.mlc.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author  Mostafa Dehghani
 */
public class Config {
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Config.class.getName());
    public static Properties configFile = new Properties();
    static{
        try {
	      InputStream stream = Config.class.getResourceAsStream("/Config.properties");
              log.info("Confog file path: " + Config.class.getResource("/Config.properties").getPath());
              configFile.load(stream);  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
