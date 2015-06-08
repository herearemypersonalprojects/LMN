/*
 * Created on 4 nov. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.directives;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.setup.SetupServicesInterface;
import com.travelsoft.cameleo.cms.processor.messages.MessageFileCacheKey;
import com.travelsoft.cameleo.cms.processor.tool.CacheTool;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.nucleus.cache.generic.Cache;
import com.travelsoft.nucleus.cache.generic.CacheException;
import com.travelsoft.nucleus.cache.generic.CacheManager;
import com.travelsoft.nucleus.cache.generic.CacheValue;
import com.travelsoft.nucleus.cache.jboss.implementations.JbossCacheManagerFactory;

import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * Include static file content into the page.
 * @author dusan.spaic
 */
public class IncludeStaticFileDirective implements TemplateDirectiveModel {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(IncludeStaticFileDirective.class);

    /** Public constructor. */
    public IncludeStaticFileDirective() {
        super();
    }

    /**
     * <p>Title: ParameterName.</p>
     * <p>Description:</p>
     * <p>Copyright: Copyright (c) 2010</p>
     * <p>Company: Travelsoft</p>
     * @author fernando.mendez
     */
    public static enum ParameterName {

        /** The file name, ex. /cs/web/css/print.css. */
        FILE_NAME("fileName");

        /** The parameter code. */
        private final String code;

        /**
         * Constructor.
         * @param code the parameter code
         */
        ParameterName(String code) {
            this.code = code;
        }

        /** @return the code */
        public String getCode() {
            return this.code;
        }
    }

    /**
     * Method getting configuration value in CONFIGURATION table for a key given.
     *
     * @param env the Freemarker environment
     * @param params the directive parameters
     * @param loopVars the loop variables (only one used)
     * @param body the body
     * @throws TemplateException in case of a template execution failure
     * @throws IOException in case of an I/O failure
     */
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {

        TemplateScalarModel nameModel = (TemplateScalarModel) params.get(ParameterName.FILE_NAME.getCode());
        if (nameModel == null) {
            throw new TemplateModelException("Missing expected 'name' attribute.");
        }

        String resultString = null;
        String filePath = nameModel.getAsString();

        MessageFileCacheKey key = new MessageFileCacheKey(filePath);
        CacheManager cacheManager = CacheTool.getCameleoCacheManager();
        Cache cache = cacheManager.getCache(Constants.Context.STATIC_FILE_CACHE_NAME);
        if (cache != null) {
            try {
                CacheValue cacheValue = cache.retrieve(key);
                if (cacheValue instanceof StringCacheValue) {
                    // value found in cache
                    StringCacheValue stringCacheValue = (StringCacheValue) cacheValue;
                    resultString = stringCacheValue.getString();
                }
            } catch (CacheException ce) {
                LOGGER.error("Failed to retrieve a cache entry for key " + key, ce);
            }
        }

        if (resultString == null) {
            // value not found in cache
            String staticFileString = this.getFileContent(filePath);
            if (staticFileString != null) {
                StringCacheValue stringCacheValue = new StringCacheValue(staticFileString);
                try {
                    if (cache != null) {
                        cache.addOrUpdate(key, stringCacheValue, null);
                    }
                } catch (CacheException e) {
                    LOGGER.error("Failed to update cache with " + key, e);
                }
                resultString = staticFileString;
            }
        }

        // Execute body
        if (resultString != null) {
            TemplateModel valueModel = BeansWrapper.getDefaultInstance().wrap(resultString);
            if (loopVars != null && loopVars.length != 0) {
                loopVars[0] = valueModel;
            }
            if (body != null) {
                body.render(env.getOut());
            } else {
                env.getOut().write(resultString);
            }
        }
    }

    /**
     * Method getting configuration value in CONFIGURATION table for a key given.
     *
     * @param filePath the key for define the returned value.
     * @return String of configuration value.
     */
    private String getFileContent(String filePath) {
        CacheManager cacheManager = JbossCacheManagerFactory.getCacheManager(Constants.Common.CAMELEO_CACHE);
        String value = null;
        if (filePath != null) {

            // get full file path
            String fullPath = "";
            SetupServicesInterface productService = ServicesFactory.getSetupServices();
            try {
                fullPath = productService.getConfigValue(Constants.Config.STATIC_GLOBAL_ROOT, cacheManager);
            } catch (TechnicalException e) {
                LOGGER.fatal("Technical Exception calling getFileContent() :" + e, e);
            }
            fullPath = fullPath + filePath;
            value = readFile(fullPath);
        }
        return value;
    }

    /**
     * Read the content from a file into the string.
     *
     * @param filePath the path to the file
     * @return {@link String}
     */
    public String readFile(String filePath) {
        BufferedReader bufferedReader = null;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            bufferedReader = new BufferedReader(new FileReader(file));
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append('\n');
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Failed to read file " + filePath);
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LOGGER.error("Failed to close reader when processing file " + filePath, e);
                }
            }
        }
        return null;
    }
}
