package com.travelsoft.lastminute.catalog.product;

import javax.servlet.http.HttpServletRequest;

import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.model.IContextDataValue;


/**
 *
 * Class to access url's parameters from freemarker.
 *
 */
public class MyEnvironment implements IContextDataValue {
    //HttpServletRequest request = this.getEnvironment().getRequest();
    /** The current web environment. */
    private WebProcessEnvironment webEnvironment;

    /**
     *
     * Constructor ..
     *
     * @param webEnvironment given environment
     */
    public MyEnvironment(WebProcessEnvironment webEnvironment) {
        this.webEnvironment = webEnvironment;
    }

    /**
     *
     * Method to return request from the working web environment.
     *
     * @return httpServletRequest
     */
    private HttpServletRequest getRequest() {
        return this.webEnvironment.getRequest();
    }

    /**
     *
     * Method ..
     *
     * @param param given param's name
     * @return string
     */
    public String getAttribute(String param) {
        return this.getString(this.getRequest().getParameter(param));
    }

    /**
    *
    * Method to check and return a string value.
    *
    * @param v value
    * @return string
    */
    private String getString(Object v) {
        if (v != null && v.getClass().equals(String.class)) {
            return (String) v;
        } else {
            return "";
        }
    }
    /**
     *
     * Overriding method.
     *
     * @see com.travelsoft.cameleo.cms.processor.model.IContextDataValue#convertToString()
     * @return ok
     */
    public String convertToString() {
        return "ok";
    }
}
