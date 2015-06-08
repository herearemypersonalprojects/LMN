/*
² * Created on 19 sept. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.directives;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.travelsoft.cameleo.cms.processor.model.ComponentContext;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.view.FtlNames;
import com.travelsoft.lastminute.catalog.util.Constants;

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
 * <p>Titre : PreselectedValueDirective.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class PreselectedValueDirective implements TemplateDirectiveModel {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(PreselectedValueDirective.class);

    /** The query parameters as map. */
    private Map<String, String> queryMap;

    /**
     *
     * <p>Titre : ParameterName.</p>
     * <p>Description : .</p>
     * <p>Copyright: Copyright (c) 2011</p>
     * <p>Company: Travelsoft</p>
     * @author zouhair.mechbal
     */
    public static enum ParameterName {

        /** The request parameter. */
        REQUEST_PARAMETER("requestParameter"),

        /** The name of document in context. */
        CURRENT_VALUE("currentValue"),

        /** The value to return.*/
        RENDRER_VALUE("renderValue"),

        /** The default value.*/
        DEFAULT_VALUE("defaultValue");

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

    /** Public default constructor. */
    public PreselectedValueDirective() {
        super();
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

        TemplateScalarModel requestParameter = (TemplateScalarModel) params.get(ParameterName.REQUEST_PARAMETER
                .getCode());
        TemplateScalarModel currentValue = (TemplateScalarModel) params.get(ParameterName.CURRENT_VALUE.getCode());
        TemplateScalarModel rendrerValue = (TemplateScalarModel) params.get(ParameterName.RENDRER_VALUE.getCode());
        TemplateScalarModel defaultValue = (TemplateScalarModel) params.get(ParameterName.DEFAULT_VALUE.getCode());
        if (requestParameter == null || currentValue == null || rendrerValue == null) {
            throw new TemplateModelException("Missing expected 'key' or 'fileName' attribute.");
        }
        AdapterTemplateModel componentContextModel = (AdapterTemplateModel) env.getVariable(FtlNames.CONTEXT_NAME);
        IComponentContext<?> componentContext = (IComponentContext<?>) componentContextModel
                .getAdaptedObject(ComponentContext.class);
        Object value = componentContext.lookup("queryString");
        // Execute body
        String requestParameterSelected = isRequestParameterSelected(value, requestParameter, currentValue,
                rendrerValue, defaultValue);

        TemplateModel valueModel = BeansWrapper.getDefaultInstance().wrap(requestParameterSelected);
        if (loopVars != null && loopVars.length != 0) {
            loopVars[0] = valueModel;
        }
        if (body != null) {
            body.render(env.getOut());
        } else {
            env.getOut().write((String) value);
        }
    }

    /**
     * Method checks if the current value is in the request parameters.
     * @param urlParameters The url parameters
     * @param requestParameter The request parameter
     * @param currentValue The value to check
     * @param rendrerValue The value to return
     * @param defaultValue The default value
     * @return true id the  current value is in the request parameters
     */
    private String isRequestParameterSelected(Object urlParameters, TemplateScalarModel requestParameter,
            TemplateScalarModel currentValue, TemplateScalarModel rendrerValue, TemplateScalarModel defaultValue) {
        String result = "";
        try {
            if (urlParameters != null) {
                String requestParameterAsString = requestParameter.getAsString();
                String currentValueAsString = currentValue.getAsString();
                if (queryMap != null) {
                    String requestParameterValue = queryMap.get(requestParameterAsString);
                    if (currentValueAsString.equals(requestParameterValue)) {
                        result = rendrerValue.getAsString();
                    }
                } else {
                    this.fillQueryMap((String) urlParameters);
                    String requestParameterValue = queryMap.get(requestParameterAsString);
                    if (currentValueAsString.equals(requestParameterValue)) {
                        result = rendrerValue.getAsString();
                    }
                }
            } else if (defaultValue != null) {
                result = rendrerValue.getAsString();
            }
        } catch (TemplateModelException e) {
            LOGGER.error("Error while trying to check if the request paremeter is selected.", e);
        }

        return result;
    }

    /**
     * Builds the query map.
     * @param query The query parameters
     */
    private void fillQueryMap(String query) {
        queryMap = new HashMap<String, String>();
        String[] params = query.split(Constants.Common.SEPARATOR_AMP);
        for (String param : params) {
            String[] split = param.split("=");
            if (split != null && split.length >= 2) {
                String name = split[0];
                String value = split[1];
                queryMap.put(name, value);
            } else {
                if (LOGGER.isEnabledFor(Level.WARN)) {
                    LOGGER.warn("Malformed parameter value " + param + " found when processing query " + query
                            + " will be ignored.");
                }
            }
        }
    }
}
