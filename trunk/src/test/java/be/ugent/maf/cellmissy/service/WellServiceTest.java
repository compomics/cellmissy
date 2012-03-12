/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static junit.framework.Assert.*;

/**
 *
 * @author Paola
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringTestXMLConfig.xml")
public class WellServiceTest {

    @Autowired
    private WellService wellService;

    @Test
    public void testWellService() {

        wellService.init();

        PlateFormat plateFormat = new PlateFormat();
        plateFormat.setFormat(96);
        plateFormat.setWellSize(8991.880909);

        Well firstWell = new Well();
        firstWell.setColumnNumber(4);
        firstWell.setRowNumber(2);

        List<ImagingType> imagingTypeList = wellService.getImagingTypes();
        for (ImagingType imagingType : imagingTypeList) {
            assertTrue(!imagingTypeList.isEmpty());
        }


    }
}
