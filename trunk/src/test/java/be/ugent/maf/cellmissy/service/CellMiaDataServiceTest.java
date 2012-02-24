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
        
        File microscopeFolder = new File(CellMiaDataServiceTest.class.getClassLoader().getResource("position_list_files").getPath());
        File obsepFile = new File(CellMiaDataServiceTest.class.getClassLoader().getResource("gffp.obsep").getPath());

        microscopeDataService.init(microscopeFolder, obsepFile);

        File cellMiaFolder = new File(PropertiesConfigurationHolder.getInstance().getString("cellmiafolder"));
        cellMiaDataService.init(cellMiaFolder);
        Map<ImagingType, List<WellHasImagingType>> imagingTypeListOfWellHasImagingTypeMap = cellMiaDataService.processCellMiaData();

        Collection<List<WellHasImagingType>> values = imagingTypeListOfWellHasImagingTypeMap.values();

        for (List<WellHasImagingType> list : values) {

            WellHasImagingType wellHasImagingType = list.get(7);
            Collection<TimeStep> timeStepCollection = wellHasImagingType.getTimeStepCollection();
            assertEquals(timeStepCollection.size(), 108);

            Collection<Track> trackCollection = wellHasImagingType.getTrackCollection();
            assertEquals(trackCollection.size(), 80);
        }

    }
}
