/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.kdtree.KDTree;
import be.ugent.maf.cellmissy.analysis.kdtree.exception.KeyDuplicateException;
import be.ugent.maf.cellmissy.analysis.kdtree.exception.KeyMissingException;
import be.ugent.maf.cellmissy.analysis.kdtree.exception.KeySizeException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This is a unit test for KDtree-based NN.
 *
 * @author Paola
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class KDTreeTest {

    private static KDTree<String> tree;
    private static final double[] A = {2, 5};
    private static final double[] B = {1, 1};
    private static final double[] C = {3, 9};
    private static final double[] T = {1, 10};

    @BeforeClass
    public static void creteKDTree() {
        try {
            // create a new KD Tree with two dimensions and add the first 3 points to it
            tree = new KDTree(2);
            tree.insert(A, "A");
            tree.insert(B, "B");
            tree.insert(C, "C");
        } catch (KeySizeException | KeyDuplicateException ex) {
            Logger.getLogger(KDTreeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testNN() {
        try {
            String n = tree.search(B);
            Assert.assertEquals("B", n);

            // find T's nearest neighbor, which should be C
            String nearest = tree.nearest(T);
            Assert.assertEquals("C", nearest);

            List<String> nearestEuclidean = tree.nearestEuclidean(T, 1.0);
            Assert.assertEquals(0, nearestEuclidean.size());
            
            nearestEuclidean = tree.nearestEuclidean(T, 5.9);
            Assert.assertEquals(2, nearestEuclidean.size());
            
            try {
                tree.delete(C);
            } catch (KeyMissingException ex) {
                Logger.getLogger(KDTreeTest.class.getName()).log(Level.SEVERE, null, ex);
            }

            // now T's nearest neighbor should be A
            nearest = tree.nearest(T);
            Assert.assertEquals("A", nearest);

        } catch (KeySizeException ex) {
            Logger.getLogger(KDTreeTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
