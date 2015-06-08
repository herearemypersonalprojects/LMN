/*
 * Created on 19 sept. 2012
 *
 * Copyright Travelsoft, 2012.
 */
package com.travelsoft.lastminute.catalog.directives;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.CodeLabel;

import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;

/**
 * <p>
 * Titre : codeLabel.
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
 * @author minmin
 */
public class FormatDateDirective implements TemplateDirectiveModel {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(FormatDateDirective.class);

    /**
     *
     * <p>
     * Titre : ParameterName.
     * </p>
     * <p>
     * Description : .
     * </p>
     * <p>
     * Copyright: Copyright (c) 2011
     * </p>
     * <p>
     * Company: Travelsoft
     * </p>
     *
     * @author zouhair.mechbal
     */
    public static enum ParameterName {
        /** The code chain to find the document zone. */
        FORMATBEFORE("formatBefore"),
        /** The code chain to find the document zone. */
        FORMAT("format"),

        /** The name of document in context. */
        DATE("date");

        /** The parameter code. */
        private final String code;

        /**
         * Constructor.
         *
         * @param code
         *            the parameter code
         */
        ParameterName(String code) {
            this.code = code;
        }

        /** @return the code */
        public String getCode() {
            return this.code;
        }
    }

    /** the product. */

    /** Public default constructor. */
    public FormatDateDirective() {
        super();
    }

    /**
     * @see freemarker.template.TemplateDirectiveModel#execute(freemarker.core.Environment,
     *      java.util.Map, freemarker.template.TemplateModel[],
     *      freemarker.template.TemplateDirectiveBody)
     * @param env
     *            the Freemarker environment
     * @param params
     *            the directive parameters
     * @param loopVars
     *            the loop variables (only one used)
     * @param body
     *            the body
     * @throws TemplateException
     *             in case of a template execution failure
     * @throws IOException
     *             in case of an I/O failure
     */
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        String date = "";
        String format = "";
        String formatBefore="yyyy-MM-dd HH:mm";
        TemplateScalarModel formatBeforeModel = (TemplateScalarModel) params.get(ParameterName.FORMATBEFORE.getCode());
        if (formatBeforeModel != null) {
            formatBefore=formatBeforeModel.getAsString();
        }
        TemplateScalarModel formatModel = (TemplateScalarModel) params.get(ParameterName.FORMAT.getCode());
        if (formatModel != null) {
            format=formatModel.getAsString();
        }

        TemplateScalarModel dateModel = (TemplateScalarModel) params.get(ParameterName.DATE.getCode());

        if (dateModel != null) {
            date = dateModel.getAsString();
        }
        String formattedDate = "";
        if (!StringUtils.isBlank(format)) {
            try {
                Date dateObj = new Date();
                if (!StringUtils.isBlank(date)) {
                    SimpleDateFormat FORMAT_BEFORE = new SimpleDateFormat(formatBefore);
                    dateObj = FORMAT_BEFORE.parse(date);
                }
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(format);
                formattedDate = DATE_FORMAT.format(dateObj);
            } catch (Exception e) {
                LOGGER.error("Invalide departure date: " + date);
            }
        }
        // Execute body
        TemplateModel valueModel = BeansWrapper.getDefaultInstance().wrap(formattedDate);
        if (loopVars != null && loopVars.length != 0) {
            loopVars[0] = valueModel;
        }
        if (body != null) {
            body.render(env.getOut());
        } else {
            env.getOut().write(formattedDate);
        }

    }

    /**
     * @param data
     *            data
     * @return string
     */
    protected String getDataString(Object data) {
        CodeLabel cl = (CodeLabel) data;
        return cl.getLabel();
    }

}
