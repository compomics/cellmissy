/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static junit.framework.Assert.*;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Paola
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringTestXMLConfig.xml")
public class CellMiaDataServiceTest {
    
    @Autowired
    private CellMiaDataService cellMiaDataService;
    
    @Test
    public void testCellMiaDataService() {
        
        MicroscopeDataService microscopeDataService = cellMiaDataService.getMicroscopeDataService();
        
        File microscopeFolder = new File(PropertiesConfigurationHolder.getInstance().getString("microscopeFolder"));
        File obsepFile = new File(PropertiesConfigurationHolder.getInstance().getString("obsepFile"));
        
        microscopeDataService.init(microscopeFolder, obsepFile);
        
        File cellMiaFolder = new File(PropertiesConfigurationHolder.getInstance().getString("cellMiaFolder"));
        cellMiaDataService.init(cellMiaFolder);
        Map<ImagingType, List<WellHasImagingType>> imagingTypeMap = cellMiaDataService.processCellMiaData();
        
        for (ImagingType imagingType : imagingTypeMap.keySet()) {
            List<WellHasImagingType> list = imagingTypeMap.get(imagingType);
            WellHasImagingType wellHasImagingType = list.get(0);
            assertTrue(!wellHasImagingType.getTrackCollection().isEmpty());            
        }
    }
}
