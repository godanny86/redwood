package com.adobe.ags.livetrial;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * A simple service interface
 */
public interface ImportService {

    /**
     * @return importPages from sql db
     */
    public String importPages(ResourceResolver resourceResolver, String limit);

    public String importAssets(ResourceResolver resourceResolver, String limit);

}