/**
 *
 */
package com.travelsoft.lastminute.catalog.serp;

import java.util.Map;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.catalog.data.LayoutComponentModelRef;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchResponse;
import com.travelsoft.cameleo.cms.processor.controller.AbstractController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.IContainerContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.tripadvisor.ReviewsRetriever;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.Place;

/**
 * <p>Titre : GetProductsList.</p>
 * Description : Get products list
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class GetProductsList extends AbstractController<ContentLayoutComponent, WebProcessEnvironment> {

    /**
     * Process business logic and produce the data model to get products list in the page results.
     * @param context the component's context
     * @throws PageNotFoundException If the page is not found
     */
    public void process(IComponentContext<ContentLayoutComponent> context) throws PageNotFoundException {
        PublishedProductSearchResponse publishedProductSearchResponse = (PublishedProductSearchResponse) context
                .lookup(Constants.Context.PRODUCT_SEARCH_RESPONSE);

        PublishedProductSearchResponse preferredPublishedProductSearchResponse =
            (PublishedProductSearchResponse) context.lookup(Constants.Context.PREFERRED_PRODUCT_SEARCH_RESPONSE);

        int nbExpertList = 0;
        boolean showExpertBlock = false;
        LayoutComponentModelRef modelRef = context.getAssociatedLayout().getModelRef();

        if (modelRef != null) {
            String modelRefCode = modelRef.getCode();
            PublishedProduct[] productsList = null;
            if (this.showPreferredProducts(modelRefCode, preferredPublishedProductSearchResponse, context)) {
                productsList = preferredPublishedProductSearchResponse.getProducts();
                showExpertBlock = true;
                nbExpertList = productsList.length;
            } else if (modelRefCode.indexOf("expertList") == -1) {
                if (preferredPublishedProductSearchResponse != null &&
                                preferredPublishedProductSearchResponse.getProducts() != null) {

                    nbExpertList = preferredPublishedProductSearchResponse.getProducts().length;
                }
                productsList = publishedProductSearchResponse.getProducts();
            }

            /** add TripAdvisor reviews */
            Map<String, Place> map = new ReviewsRetriever().getTripAdvisorReviews();

            if (productsList != null && productsList.length > 0) {
                Integer index = 0;
                for (PublishedProduct publishedProduct : productsList) {
                    IContainerContext<?> containerContext = context
                            .appendContainerContext(Constants.Containers.CARTOUCHE_CONTAINER);
                    boolean flyAndDrive = Util.isExistingFlyAndDriveCriteria(publishedProduct);

                    if (containerContext != null) {
                        if (map != null && !map.isEmpty()) {
                            Place place = map.get(publishedProduct.getCode());
                            if (place != null) {
                                containerContext.write("avgrating", place.getAvgrating());
                                containerContext.write("ratingimage", place.getRatingimage());
                            } else {
                                containerContext.write("avgrating", "");
                                containerContext.write("ratingimage", "");
                            }
                        }
                        containerContext.write(Constants.Context.PUBLISHED_PRODUCT, publishedProduct);
                        containerContext.write(Constants.Context.PUBLISHED_PRODUCT_INDEX, ++index);
                        containerContext.write(Constants.Context.FLY_AND_DRIVE, flyAndDrive);
                    }
                }
            }
            int adsIndex = Integer.parseInt(Util.getConfigValue("ADS_INDEX"));
            context.write(Constants.Context.NB_EXPERT_LIST, nbExpertList);
            context.write(Constants.Context.ADS_INDEX, adsIndex);
            context.write(Constants.Context.DISPLAY_EXPERT_PRODUCT_BLOCK, showExpertBlock);

            WebProcessEnvironment webEnvironment = this.getEnvironment();

            String finParam = webEnvironment.getRequestParameter(Constants.PartitionerConstants.FIN);
            String linParam = webEnvironment.getRequestParameter(Constants.PartitionerConstants.LIN);

            if (finParam != null && linParam != null) {
                context.write(Constants.PartitionerConstants.FIN, finParam);
                context.write(Constants.PartitionerConstants.LIN, linParam);
            }

            this.processContainers(context);
        }
    }


    /**
     * Checks if the preferred product is displayed.
     * @param modelRefCode The model code reference in the page description
     * @param preferredPublishedProductSearchResponse The preferred products response
     * @param context The current context
     * @return true if the preferred products are displayed
     */
    private boolean showPreferredProducts(String modelRefCode,
            PublishedProductSearchResponse preferredPublishedProductSearchResponse,
            IComponentContext<ContentLayoutComponent> context) {

        if (preferredPublishedProductSearchResponse != null) {
            PublishedProductSearchCriteria finalCriteria = (PublishedProductSearchCriteria) context
                    .lookup(Constants.Context.FINAL_CRITERIA_CONSTRAINTS);
            if (finalCriteria != null
                    && modelRefCode.indexOf("expertList") != -1
                    && finalCriteria.getFirstResultIndex() == 0
                    && finalCriteria.getUser() != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Produces a data model mock.
     *
     * @param context
     *            the component's context
     * @param injectionData
     *            the injection data
     */
    public void preview(IComponentContext<ContentLayoutComponent> context, InjectionData injectionData) {
    }
}
