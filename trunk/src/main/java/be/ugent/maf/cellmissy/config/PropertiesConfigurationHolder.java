package be.ugent.maf.cellmissy.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Created by IntelliJ IDEA.
 * User: niels
 * Date: 9/02/12
 * Time: 14:41
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesConfigurationHolder extends PropertiesConfiguration {

    private static PropertiesConfigurationHolder ourInstance;

    static {
        try {
            ourInstance = new PropertiesConfigurationHolder("lims.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static PropertiesConfigurationHolder getInstance() {
        return ourInstance;
    }

    private PropertiesConfigurationHolder(String propertiesFileName) throws ConfigurationException {
        super(propertiesFileName);
    }

}
