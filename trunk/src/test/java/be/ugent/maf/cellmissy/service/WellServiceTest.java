/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.ImagingType;
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

    }
}
