/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author Paola Masuzzo
 */
public class ResourceUtils {

    /**
     * Gets a resource by its relative path. If the resource is not found on the
     * file system, the class path is searched. If nothing is found, null is
     * returned.
     *
     * @param relativePath the relative path of the resource
     * @return the found resource
     */
    public static Resource getResourceByRelativePath(String relativePath) {
        Resource resource = new FileSystemResource(relativePath);
        if (!resource.exists()) {
            //try to find it on the classpath
            resource = new ClassPathResource(relativePath);
            if (!resource.exists()) {
                resource = null;
            }
        }
        return resource;
    }
}
