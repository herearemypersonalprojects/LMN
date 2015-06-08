/*
 * Created on 3 oct. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.serp;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.ProductBase;
import com.travelsoft.cameleo.catalog.ejb.util.ProductCriterionRankComparator;

/**
 * <p>Titre : ProductCriterionValueCodeComparator.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class ProductCriterionValueCodeComparator extends ProductCriterionRankComparator {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(ProductCriterionValueCodeComparator.class);

    /** <code>serialVersionUID</code> field. */
    private static final long serialVersionUID = 1L;

    /** The criterion code use for sort. */
    private String criterionCode;

    /**
     * Constructor for <code>ProductCriterionValueCodeComparator</code>.
     *
     * @param inputPrefProductCodes An array of preferred products codes (can be null).
     * @param criterionCode the criterion code use for sort
     */
    public ProductCriterionValueCodeComparator(String[] inputPrefProductCodes, String criterionCode) {
        super(inputPrefProductCodes);
        this.criterionCode = criterionCode;
    }

    /**
     * getProductIndexingFields.
     *
     * @param product the ProductBase from which the sorting fields are got.
     * @return the fields Object.
     */
    protected Object[] getProductIndexingFields(ProductBase product) {
        Object[] result = new Object[2];
        try {
            boolean findValue = false;
            Criterion[] criterionList = product.getTechnicalInfo()
                    .getCriterion();
            for (int i = 0; i < criterionList.length; i++) {
                if (criterionList[i].getCode().equals(this.criterionCode)) {
                    CriterionValue[] criterionValueList = criterionList[i].getValue();
                    String code = criterionValueList[0].getCode();
                    if (code != null && code.split("\\.") != null && code.split("\\.").length > 1) {
                        result[0] = new Integer(code.split("\\.")[1]);
                        findValue = true;
                        i = criterionList.length;
                    }
                }
            }
            if (!findValue) {
                result[0] = Integer.MAX_VALUE;
            }
            result[1] = product.getBasePrice();
        } catch (Exception e) {
            if (product.getCode() != null) {
                LOGGER.error(
                        "Error while trying to call getProductIndexingFields for the product code : "
                                + product.getCode(), e);
            }
        }
        return result;
    }
}
