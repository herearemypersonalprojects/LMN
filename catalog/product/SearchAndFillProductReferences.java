/*
 * Copyright Travelsoft, 2013.
 */
package com.travelsoft.lastminute.catalog.product;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchResponse;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.IContainerContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.serp.AbstractSearchProducts;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;



/**
 * <p>Titre : SearchAndFillProductReferences.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2013</p>
 * <p>Company : Travelsoft</p>
 * @author Quoc-Anh Le
 * @version
 *
 */
public class SearchAndFillProductReferences
    extends AbstractSearchProducts {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SearchAndFillProductReferences.class);

    /** The search product criteria. */
    private PublishedProductSearchCriteria searchCriteria;

    /**
     * Process business logic and produce the data model to get the product.
     * @param context the component's context
     * @throws PageNotFoundException If the page is not found
     */
    @Override
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context)
        throws PageNotFoundException {
        /** Step 1: récupérer criteres de produit.xml */
        /** Step 2: récupérer criterion de produit dans le context (pays) */
        /** Step 3: récupérer le code et en excluant après (!285) */
        /** Step 4: passer les params 2 et 3 dans searchcriteria(1) */

        searchCriteria = Util.retrieveSearchCriteria(context, "services.product.searchCriteriaById");

        PublishedProduct product = (PublishedProduct) context.lookup(Constants.Context.PUBLISHED_PRODUCT);
        if (product == null) {
            return;
        }

        Criterion[] criterions = product.getTechnicalInfo().getCriterion();
        for (Criterion criterion : criterions) {
            if (criterion.getCode().equals(Constants.CriterionConstants.CRITERION_DESTIONATION_CODE)) {
                searchCriteria.setCustomCriteria(new Criterion[]{criterion});
            }
        }
        String code = "!" + product.getCode();
        searchCriteria.setCode(new String[]{code});

        try {
            PublishedProductSearchResponse relatedSearchResponse = getPublishedProductSearchResponse(searchCriteria);
            PublishedProduct[] productsList = null;
            if (relatedSearchResponse != null) {
                productsList = relatedSearchResponse.getProducts();
            }

            if (productsList != null && productsList.length > 0) {
                Integer index = 0;
                for (PublishedProduct publishedProduct : productsList) {
                    IContainerContext<?> containerContext = context
                            .appendContainerContext(Constants.Containers.RELATED_CARTOUCHE_CONTAINER);
                    if (containerContext != null) {
                        containerContext.write(Constants.Context.RELATED_PUBLISHED_PRODUCT, publishedProduct);
                        containerContext.write(Constants.Context.RELATED_PUBLISHED_PRODUCT_INDEX, ++index);
                    }
                }
            }
            //this.processContainers(context);

        } catch (TechnicalException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(e.getMessage());
            }
        }
    }


    /**
     * Produces a data model mock.
     *
     * @param context
     *            the component's context
     * @param injectionData
     *            the injection data
     * @throws PageNotFoundException If the page is not found
     */
    public void preview(IComponentContext<PageLayoutComponent> context, InjectionData injectionData)
        throws PageNotFoundException {
    }

}
