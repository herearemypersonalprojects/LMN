/*
 * Created on 4 nov. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.directives;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.setup.SetupServicesInterface;
import com.travelsoft.lastminute.catalog.util.Constants;

import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * <p>Titre : RetrieveConfigValueDirective.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class RetrieveConfigValueDirective implements TemplateDirectiveModel {

      /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(RetrieveConfigValueDirective.class);

    /** Public constructor. */
    public RetrieveConfigValueDirective() {
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

        /** The container parameter. */
        KEY("key");

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

        TemplateScalarModel nameModel = (TemplateScalarModel) params.get(ParameterName.KEY.getCode());
        if (nameModel == null) {
            throw new TemplateModelException("Missing expected 'name' attribute.");
        }

        String name = nameModel.getAsString();
        String value = this.getConfigValue(name);
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

    /**
     * Method getting configuration value in CONFIGURATION table for a key given.
     *
     * @param configKey the key for define the returned value.
     * @return String of configuration value.
     */
    private String getConfigValue(String configKey) {
        String value = null;
        if (configKey != null) {
            SetupServicesInterface productService = ServicesFactory.getSetupServices();
            try {
                value = productService.getConfigValue(configKey, Constants.CAMELEO_CACHE_MANAGER);
            } catch (TechnicalException e) {
                LOGGER.fatal("Technical Exception calling getConfigValue() :" + e, e);
            }
        }
        return value;
    }
}
