/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.kdtree;

import be.ugent.maf.cellmissy.analysis.kdtree.distance.DistanceMetric;
import be.ugent.maf.cellmissy.analysis.kdtree.distance.EuclideanDistance;
import be.ugent.maf.cellmissy.analysis.kdtree.distance.HammingDistance;
import be.ugent.maf.cellmissy.analysis.kdtree.exception.KeyDuplicateException;
import be.ugent.maf.cellmissy.analysis.kdtree.exception.KeyMissingException;
import be.ugent.maf.cellmissy.analysis.kdtree.exception.KeySizeException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * An implementation of a KD (k-dimensional)-tree for quick nearest-neighbor
 * lookup.
 *
 * @author Paola
 * @param <T>
 */
public class KDTree<T> implements Serializable {

    // number of milliseconds
    final long m_timeout;
    // K = number of dimensions
    final private int m_K;
    // root of KD-tree
    private KDNode<T> m_root;
    // count of nodes
    private int m_count;

    /**
     * Creates a KD-tree with specified number of dimensions.
     *
     * @param k number of dimensions
     */
    public KDTree(int k) {
        this(k, 0);
    }

    public KDTree(int k, long timeout) {
        this.m_timeout = timeout;
        m_K = k;
        m_root = null;
    }

    /**
     * Insert a node in the tree. Uses algorithm from Gonnet and R. Baeza-Yates.
     * {Handbook of Algorithms and Data Structures}.
     *
     * @param key key for KD-tree node
     * @param value value at that key
     * @throws KeySizeException if key.length mismatches K
     * @throws KeyDuplicateException if key is already in tree
     */
    public void insert(double[] key, T value) throws KeySizeException, KeyDuplicateException {
        this.edit(key, new Editor.Inserter<>(value));
    }

    /**
     * Edit a node in the tree.
     *
     * @param key key for KD-tree node
     * @param editor object to be used in order to edit the value at that key
     * @throws KeySizeException if key.length mismatches K
     * @throws KeyDuplicateException if key is already in the tree
     */
    public void edit(double[] key, Editor<T> editor) throws KeySizeException, KeyDuplicateException {

        if (key.length != m_K) {
            throw new KeySizeException();
        }

        synchronized (this) {
            // the first insert has to be synchronized
            if (null == m_root) {
                // make first insert: root node
                m_root = KDNode.create(new HPoint(key), editor);
                m_count = m_root.deleted ? 0 : 1;
                return;
            }
        }

        m_count += KDNode.edit(new HPoint(key), editor, m_root, 0, m_K);
    }

    /**
     * Find KD-tree node whose key is identical to the provided key.
     *
     * @param key key for KD-tree node
     * @return object at key, or null if not found
     * @throws KeySizeException if key.length mismatches K
     */
    public T search(double[] key) throws KeySizeException {

        if (key.length != m_K) {
            throw new KeySizeException();
        }

        KDNode<T> kd = KDNode.srch(new HPoint(key), m_root, m_K);

        return (kd == null ? null : kd.v);
    }

    /**
     * Delete a node with a specific key from the tree.
     *
     * @param key
     * @throws KeySizeException
     * @throws KeyMissingException
     */
    public void delete(double[] key) throws KeySizeException, KeyMissingException {
        delete(key, false);
    }

    /**
     * Delete a node from a KD-tree. Instead of actually deleting node and
     * rebuilding tree, marks node as deleted. Hence, it is up to the caller to
     * rebuild the tree as needed for efficiency.
     *
     * @param key key for KD-tree node
     * @param optional if false and node not found, throw an exception, that the
     * key is missing
     * @throws KeySizeException if key.length mismatches K
     * @throws KeyMissingException if no node in tree has key
     */
    public void delete(double[] key, boolean optional) throws KeySizeException, KeyMissingException {

        if (key.length != m_K) {
            throw new KeySizeException();
        }
        // search for the node
        KDNode<T> t = KDNode.srch(new HPoint(key), m_root, m_K);
        if (t == null) {
            if (optional == false) {
                throw new KeyMissingException();
            }
        } else {
            if (KDNode.del(t)) {
                m_count--;
            }
        }
    }

    /**
     * Find KD-tree node whose key is nearest neighbor to key.
     *
     * @param key key for KD-tree node
     * @return object at node nearest to key, or null on failure
     * @throws KeySizeException if key.length mismatches K
     *
     */
    public T nearest(double[] key) throws KeySizeException {
        // call the method for a number n and get the first in the list
        List<T> nbrs = nearest(key, 1, null);
        return nbrs.get(0);
    }

    /**
     * Find KD-tree nodes whose keys are <i>n</i> nearest neighbors to key.
     *
     * @param key key for KD-tree node
     * @param n number of nodes to return
     * @return objects at nodes nearest to key, or null on failure
     * @throws KeySizeException if key.length mismatches K
     *
     */
    public List<T> nearest(double[] key, int n) throws KeySizeException, IllegalArgumentException {
        return nearest(key, n, null);
    }

    /**
     * Find KD-tree nodes whose keys are within a given Euclidean distance of a
     * given key.
     *
     * @param key key for KD-tree node
     * @param dist: the distance to search for
     * @return objects at nodes with distance of key, or null on failure
     * @throws KeySizeException if key.length mismatches K
     *
     */
    public List<T> nearestEuclidean(double[] key, double dist) throws KeySizeException {
        return nearestDistance(key, dist, new EuclideanDistance());
    }

    /**
     * Find KD-tree nodes whose keys are within a given Hamming distance of a
     * given key.
     *
     * @param key key for KD-tree node
     * @param dist: the distance to search for
     * @return objects at nodes with distance of key, or null on failure
     * @throws KeySizeException if key.length mismatches K
     *
     */
    public List<T> nearestHamming(double[] key, double dist) throws KeySizeException {
        return nearestDistance(key, dist, new HammingDistance());
    }

    /**
     * Find KD-tree nodes whose keys are <I>n</I> nearest neighbors to key. Uses
     * algorithm above. Neighbors are returned in ascending order of distance to
     * key, for this a LinkedList is provided.
     *
     * @param key key for KD-tree node
     * @param n how many neighbors to find
     * @param checker an optional object to filter matches
     * @return objects at node nearest to key, or null on failure
     * @throws KeySizeException if key.length mismatches K
     * @throws IllegalArgumentException if <I>n</I> is negative or exceeds tree
     * size
     */
    public List<T> nearest(double[] key, int n, Checker<T> checker) throws KeySizeException, IllegalArgumentException {

        if (n <= 0) {
            return new LinkedList<>();
        }

        NearestNeighborList<KDNode<T>> nnl = getnbrs(key, n, checker);
        n = nnl.getSize();
        // put them on a stack
        Stack<T> nbrs = new Stack<>();

        for (int i = 0; i < n; ++i) {
            KDNode<T> kd = nnl.removeHighest();
            nbrs.push(kd.v);
        }

        return nbrs;
    }

    /**
     * Range search in a KD-tree.
     *
     * @param low lower-bounds for key
     * @param up upper-bounds for key
     * @return array of Objects whose keys fall in range [low,up]
     * @throws KeySizeException on mismatch among low.length, up.length, or K
     */
    public List<T> range(double[] low, double[] up) throws KeySizeException {

        if (low.length != up.length) {
            throw new KeySizeException();
        } else if (low.length != m_K) {
            throw new KeySizeException();
        } else {
            List<KDNode<T>> found = new LinkedList<>();
            KDNode.rsearch(new HPoint(low), new HPoint(up),
                      m_root, 0, m_K, found);
            List<T> o = new LinkedList<>();
            for (KDNode<T> node : found) {
                o.add(node.v);
            }
            return o;
        }
    }

    /**
     * Simply get the size (depth) of the tree.
     *
     * @return
     */
    public int size() {
        return m_count;
    }

    @Override
    public String toString() {
        // just the root node
        return m_root.toString(0);
    }

    private NearestNeighborList<KDNode<T>> getnbrs(double[] key) throws KeySizeException {
        return getnbrs(key, m_count, null);
    }

    private NearestNeighborList<KDNode<T>> getnbrs(double[] key, int n, Checker<T> checker) throws KeySizeException {

        if (key.length != m_K) {
            throw new KeySizeException();
        }

        NearestNeighborList<KDNode<T>> nnl = new NearestNeighborList<>(n);

        // initial call is with infinite hyper-rectangle and max distance
        HRect hr = HRect.infiniteHRect(key.length);
        double max_dist_sqd = Double.MAX_VALUE;
        HPoint keyp = new HPoint(key);

        if (m_count > 0) {
            long timeout = (this.m_timeout > 0)
                      ? (System.currentTimeMillis() + this.m_timeout)
                      : 0;
            KDNode.nnbr(m_root, keyp, hr, max_dist_sqd, 0, m_K, nnl, checker, timeout);
        }

        return nnl;
    }

    /**
     * For a given key and a certain distance (upper bound), find the nearest
     * neighbors.
     *
     * @param key: the key node to search the neighbors for.
     * @param dist: the upper distance bound to look for.
     * @param metric: the metric to use (Euclidean? Hamming?)
     * @return: a list of neighbors
     * @throws KeySizeException
     */
    private List<T> nearestDistance(double[] key, double dist, DistanceMetric metric) throws KeySizeException {

        NearestNeighborList<KDNode<T>> nnl = getnbrs(key);
        int n = nnl.getSize();
        Stack<T> nbrs = new Stack<>();

        for (int i = 0; i < n; ++i) {
            KDNode<T> kd = nnl.removeHighest();
            HPoint p = kd.k;
            if (metric.distance(kd.k.coord, key) < dist) {
                nbrs.push(kd.v);
            }
        }

        return nbrs;
    }

}
