package be.ugent.maf.cellmissy.config;

import be.ugent.maf.cellmissy.utils.ResourceUtils;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

/**
 * Properties Configuration Holder
 * @author Paola Masuzzo
 */
public class PropertiesConfigurationHolder extends PropertiesConfiguration {

    private static final Logger LOG = Logger.getLogger(PropertiesConfigurationHolder.class);
    private static PropertiesConfigurationHolder ourInstance;

    static {
        try {
            Resource propertiesResource = ResourceUtils.getResourceByRelativePath("cell_missy.properties");
            ourInstance = new PropertiesConfigurationHolder(propertiesResource);
        } catch (IOException | ConfigurationException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * get instance of the Properties Configuration Holder
     * @return
     */
    public static PropertiesConfigurationHolder getInstance() {
        return ourInstance;
    }

    private PropertiesConfigurationHolder(Resource propertiesResource) throws ConfigurationException, IOException {
        super(propertiesResource.getURL());
    }
}
