/*
 * Created on 23 nov. 2011
 *
 * Copyright Travelsoft 2011</p>
 */
package com.travelsoft.lastminute.catalog.directives;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * <p>Titre : CheckCharacterNumberDirective.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class CheckCharacterNumberDirective implements TemplateDirectiveModel {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(CheckCharacterNumberDirective.class);

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

        /** The character number. */
        CHARACTER_NUMBER("characterNumber"),

        /** The character value. */
        CHARACTER_VALUE("characterValue"),

        /** The check type. */
        CHECK_TYPE("checkType");

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
        TemplateScalarModel characterNumberModel = (TemplateScalarModel) params.get(ParameterName.CHARACTER_NUMBER
                .getCode());
        TemplateScalarModel characterValueModel = (TemplateScalarModel) params.get(ParameterName.CHARACTER_VALUE
                .getCode());
        TemplateScalarModel checkTypeModel = (TemplateScalarModel) params.get(ParameterName.CHECK_TYPE.getCode());
        String characterNumber = null;
        String characterValue = null;
        String checkType = null;
        if (characterNumberModel == null) {
            throw new TemplateModelException("Missing expected 'characterNumber' attribute.");
        } else {
            characterNumber = characterNumberModel.getAsString();
        }
        if (characterValueModel == null) {
            throw new TemplateModelException("Missing expected 'characterValue' attribute.");
        } else {
            characterValue = characterValueModel.getAsString();
        }
        if (checkTypeModel == null) {
            throw new TemplateModelException("Missing expected 'checkType' attribute.");
        } else {
            checkType = checkTypeModel.getAsString();
        }
        int characterNumberInt = Integer.valueOf(characterNumber);
        String value = "";
        if (characterValue != null && !"".equals(characterValue)) {
            int currentLength = characterValue.length();
            if (currentLength > characterNumberInt) {
                if ("pension".equals(checkType)) {
                    value = characterValue.substring(0, characterNumberInt - 1) + "...";
                } else if ("city".equals(checkType)) {
                    value = "general";
                }
            } else {
                value = characterValue;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("The format character is " + characterValue);
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
