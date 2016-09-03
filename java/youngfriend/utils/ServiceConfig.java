

package youngfriend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class ServiceConfig {

    private static final Logger logger = LoggerFactory.getLogger(ServiceConfig.class);
    private static final String CONFIFILE = "config.property";

    private Properties pro = new Properties();

    public ServiceConfig() {
        try {
            File confiFile = new File(CONFIFILE);
            if (!confiFile.exists()) {
                confiFile.createNewFile();
            } else {
                pro.load(new FileInputStream(CONFIFILE));
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    public void addConfig(String key, String value) {
        pro.setProperty(key, value);
    }

    public void writeConfig() {
        try {
            pro.store(new FileOutputStream(CONFIFILE), "");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getPro(String key) {
        if (!pro.containsKey(key)) {
            return "";
        }
        return pro.getProperty(key);
    }


}
