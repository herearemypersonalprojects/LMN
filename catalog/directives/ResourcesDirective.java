/*
 * Created on 2 janv. 2012 Copyright Travelsoft, 2012.
 */
package com.travelsoft.lastminute.catalog.directives;

import java.io.IOException;
import java.util.Map;

import com.travelsoft.cameleo.cms.processor.model.ComponentContext;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.view.FtlNames;
import com.travelsoft.lastminute.catalog.resource.ResourceService;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.data.BrandData;

import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * <p>
 * Titre : ResourcesDirective.
 * </p>
 * <p>
 * Description : .
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: Travelsoft
 * </p>
 *
 * @author zouhair.mechbal
 */
public class ResourcesDirective implements TemplateDirectiveModel {


    /** The resource service. */
    private ResourceService resourceService;


    /**
     * <p>
     * Title: ParameterName.
     * </p>
     * <p>
     * Description:
     * </p>
     * <p>
     * Copyright: Copyright (c) 2010
     * </p>
     * <p>
     * Company: Travelsoft
     * </p>
     *
     * @author zouhair.mechbal
     */
    public static enum ParameterName {

        /** The key to process. */
        CANONICAL_ADDRESS("canonicalAddress"),

        /** The file name to process. */
        FILE_NAME("fileName"),

        /** The file extension. */
        EXTENSION("fileExtension"),

        /** The retrieve type, physical or relative(default). */
        TYPE("type");


        /** The parameter code. */
        private final String code;


        /**
         * Constructor.
         *
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
     * @param env the Freemarker environment
     * @param params the directive parameters
     * @param loopVars the loop variables (only one used)
     * @param body the body
     * @throws TemplateException in case of a template execution failure
     * @throws IOException in case of an I/O failure
     */
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {

        TemplateScalarModel canonicalAddressModel = (TemplateScalarModel) params.get(ParameterName.CANONICAL_ADDRESS
            .getCode());
        TemplateScalarModel fileNameModel = (TemplateScalarModel) params.get(ParameterName.FILE_NAME.getCode());
        TemplateScalarModel extensionModel = (TemplateScalarModel) params.get(ParameterName.EXTENSION.getCode());

        if (canonicalAddressModel == null || fileNameModel == null || extensionModel == null) {
            throw new TemplateModelException(
                "Missing expected 'canonicalAddress' or 'fileName' or 'extension file' attribute.");
        }

        AdapterTemplateModel componentContextModel = (AdapterTemplateModel) env.getVariable(FtlNames.CONTEXT_NAME);
        IComponentContext<?> componentContext = (IComponentContext<?>) componentContextModel
            .getAdaptedObject(ComponentContext.class);

        BrandData brandData = (BrandData) componentContext.lookup(Constants.Context.BRAND_DATA);
        if (brandData != null) {
            String brandServerName = brandData.getBrandServerName();
            String canonicalAddress = canonicalAddressModel.getAsString();
            String fileName = fileNameModel.getAsString();
            String fileExtension = extensionModel.getAsString();

            ResourceService resourceService = this.getResourceService();
            TemplateScalarModel typeModel = (TemplateScalarModel) params.get(ParameterName.TYPE.getCode());
            String value = null;
            if (typeModel == null) {
                value = resourceService.buildWebFileResourceUrl(brandServerName, canonicalAddress, fileName,
                    fileExtension);
            } else {
                value = resourceService.buildCompleteLocalFileResourceUrl(brandServerName, canonicalAddress, fileName,
                    fileExtension);
            }

            // Execute body
            if (value != null) {
                TemplateModel valueModel = BeansWrapper.getDefaultInstance().wrap(value);
                if (loopVars != null && loopVars.length != 0) {
                    loopVars[0] = valueModel;
                }
                if (body != null) {
                    body.render(env.getOut());
                } else {
                    env.getOut().write(value);
                }
            }
        }
    }

    /**
     * Gets the ResourceService instance.
     *
     * @return the ResourceService instance
     */
    private ResourceService getResourceService() {
        if (resourceService == null) {
            resourceService = new ResourceService();
        }
        return resourceService;
    }
}
