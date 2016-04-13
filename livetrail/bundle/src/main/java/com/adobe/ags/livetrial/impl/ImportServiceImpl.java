package com.adobe.ags.livetrial.impl;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.ags.livetrial.ImportService;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

/**
 * One implementation of the {@link ImportService}. Note that the repository is
 * injected, not retrieved.
 */
@Service
@Component(metatype = false)
public class ImportServiceImpl implements ImportService {

    @Reference
    private SlingRepository repository;

    @Reference
    DataSourcePool dspService;

    private PageManager pageManager;

    private static final Logger log = LoggerFactory.getLogger(ImportServiceImpl.class);

    public String importPages(ResourceResolver resourceResolver, String limit) {

        pageManager = resourceResolver.adaptTo(PageManager.class);

        try {
            DataSource ds = (DataSource) dspService.getDataSource(getDataSrc());
            if (ds != null) {
                final Connection connection = ds.getConnection();
                final Statement statement = connection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT * from pages LIMIT "
                        + limit);
                int r = 0;
                String contentRoot = "/content/geometrixx/en/content-migration";
                String templatePath = "/apps/livetrail/templates/contentpage";

                while (resultSet.next()) {
                    createPage(resultSet, pageManager, templatePath, contentRoot);
                    r = r + 1;
                }
                resultSet.close();

            }
        } catch (Exception e) {
            return "oh nos";
        }
        return "yayy finished";
    }

    private String getDataSrc() {
        return "livetrialmigration";
    }

    private void createPage(ResultSet rs, PageManager pageManager, String templatePath,
            String contentRoot) throws SQLException, WCMException {
        String title = rs.getString("title");
        String pageId = rs.getInt("id") + "";

        Page parentPage = getParentPage(pageId, pageManager, templatePath, contentRoot);

        pageManager.create(parentPage.getPath(), pageId, templatePath, title);

    }

    private Page getParentPage(String pageId, PageManager pageManager, String templatePath,
            String contentRoot) throws WCMException {

        String parentId = pageId.substring(0, 1);
        Page parentPage = pageManager.getPage(contentRoot + "/" + parentId);
        if (null == parentPage) {
            Page contentRootPage = pageManager.getPage(contentRoot);
            parentPage = pageManager.create(contentRootPage.getPath(), parentId, templatePath,
                    parentId);
        }
        return parentPage;
    }

    public String importAssets(ResourceResolver resourceResolver, String limit) {
        AssetManager assetMgr = resourceResolver.adaptTo(AssetManager.class);
        try {
            DataSource ds = (DataSource) dspService.getDataSource("livetrialmigration");
            if (ds != null) {
                final Connection connection = ds.getConnection();
                final Statement statement = connection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT * FROM assets LIMIT "
                        + limit);
                int r = 0;
                while (resultSet.next()) {
                    InputStream input = resultSet.getBinaryStream("content");
                    // resultSet.getString("type");
                    assetMgr.createAsset("/content/dam/asset" + resultSet.getInt("id"), input,
                            resultSet.getString("type"), true);
                    r = r + 1;
                }
                resultSet.close();
                log.info("Number of results: {}", r);
            }
        } catch (Exception e) {
            log.error("Exception occurred", e);
        }
        return "Finished!";
    }

}
