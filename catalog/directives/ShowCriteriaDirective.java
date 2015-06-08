/*
 * Created on 20 oct. 2011
 *
 * Copyright Travelsoft 2011</p>
 */
package com.travelsoft.lastminute.catalog.directives;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.cms.processor.model.ComponentContext;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.view.FtlNames;

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
 * <p>Titre : ShowCriteriaDirective.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class ShowCriteriaDirective implements TemplateDirectiveModel {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(ShowCriteriaDirective.class);

    /**
     * <p>Titre : ParameterName.</p>
     * Description :
     * <p>Copyright : Copyright (c) 2011</p>
     * <p>Company : Travelsoft</p>
     * @author MengMeng
     * @version
     *
     */
    public static enum ParameterName {

        /** The criteria name in the url parameters. */
        CRITERIA_NAME("criteriaName");

        /** The criteria name. */
        private final String criteriaName;

        /**
         * Constructor.
         * @param criteriaName the parameter criteria name
         */
        ParameterName(String criteriaName) {
            this.criteriaName = criteriaName;
        }

        /**
         * Getter.
         * @return the criteriaName.
         */
        public String getCriteriaName() {
            return criteriaName;
        }

    }

    /**
     * @see freemarker.template.TemplateDirectiveModel#execute(freemarker.core.Environment, java.util.Map,
     *  freemarker.template.TemplateModel[], freemarker.template.TemplateDirectiveBody)
     * @param env the Freemarker environment
     * @param params the directive parameters
     * @param loopVars the loop variables (only one used)
     * @param body the body
     * @throws TemplateException in case of a template execution failure
     * @throws IOException in case of an I/O failure
     */
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        TemplateScalarModel criteriaNameModel = (TemplateScalarModel) params.get(ParameterName.CRITERIA_NAME
                .getCriteriaName());
        String criteriaName = null;
        if (criteriaNameModel == null) {
            throw new TemplateModelException("Missing expected 'criteriaName' attribute.");
        } else {
            criteriaName = criteriaNameModel.getAsString();
        }
        AdapterTemplateModel componentContextModel = (AdapterTemplateModel) env.getVariable(FtlNames.CONTEXT_NAME);
        IComponentContext<?> componentContext = (IComponentContext<?>) componentContextModel
                .getAdaptedObject(ComponentContext.class);
        String urlParameters = (String) componentContext.lookup("firstQueryString");
        int value = 0;
        if (urlParameters != null && urlParameters.indexOf(criteriaName) != -1) {
            value = 1;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("The criteria name is " + criteriaName + ", it's value is " + value);
        }
        // Execute body
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
