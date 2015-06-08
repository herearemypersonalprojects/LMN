/**
 *
 */
package com.travelsoft.lastminute.catalog.serp;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.catalog.data.Disponibility;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchResponse;
import com.travelsoft.cameleo.cms.processor.controller.AbstractController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.IContainerContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;

/**
 * @author xubo
 *
 */
public class GetLandingPageProductsList  extends AbstractController<ContentLayoutComponent, WebProcessEnvironment> {

    /** departure date format. */
    private static SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * Process business logic and produce the data model to get products list in the page results.
     * @param context the component's context
     * @throws PageNotFoundException If the page is not found
     */
    @SuppressWarnings("unchecked")
    public void process(IComponentContext<ContentLayoutComponent> context) throws PageNotFoundException {
        PublishedProductSearchResponse publishedProductSearchResponse = (PublishedProductSearchResponse) context
                .lookup(Constants.Context.PRODUCT_SEARCH_RESPONSE);

        String destination = (String) context.lookup(Constants.Context.DESTINATION);

        PublishedProductSearchResponse filteredResponse = new PublishedProductSearchResponse();
        PublishedProduct[] productsList = publishedProductSearchResponse.getProducts();

        if (productsList != null && productsList.length > 0) {

            //sort
            Arrays.sort(productsList, new ProductCriterionValueCodeComparator(null,
                    Constants.CriterionConstants.LANDING_PAGE_CRITERION_CODE));

            //filter
            Integer index = 0;
            for (PublishedProduct publishedProduct : productsList) {
                Disponibility[] dispoList = publishedProduct.getTechnicalInfo().getDisponibility();
                Disponibility selectDispo = null;
                Integer daysOffset = 15;
                for (int i = daysOffset; i >= 3; i--) {
                    selectDispo = findCheapestDisponibility(dispoList, i, i);
                    if (selectDispo != null) {
                        daysOffset = i;
                        break;
                    }
                }

                filteredResponse.addProducts(publishedProduct);
                IContainerContext<?> containerContext = context
                        .appendContainerContext(Constants.Containers.CARTOUCHE_CONTAINER);
                if (containerContext != null) {
                    containerContext.write(Constants.Context.PUBLISHED_PRODUCT, publishedProduct);
                    containerContext.write(Constants.Context.PUBLISHED_PRODUCT_INDEX, ++index);
                    containerContext.write(Constants.Context.DESTINATION, destination);
                    if (selectDispo != null) {
                        containerContext.write(Constants.Context.LANDING_PAGE_PRODUCT_SELECTED_AVAILABLE, selectDispo);
                        containerContext.write(Constants.Context.LANDING_PAGE_PRODUCT_SELECTED_AVAILABLE_DAYS_OFFSET, daysOffset);
                    }
                }
            }
            context.write(Constants.Context.LANDING_PAGE_PRODUCT_SEARCH_RESPONSE, filteredResponse);
        }
        this.processContainers(context);
    }

    /**
     * @param dispoList list
     * @param beginDateStr begin date str
     * @param endDateStr end date str
     * @return selected dispo
     */
    private Disponibility findCheapestDisponibility(Disponibility[] dispoList, int beginDateOffset, int endDateOffset) {
        Disponibility selectedDispo = null;
        BigDecimal cheapestPrice = new BigDecimal(Integer.MAX_VALUE);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar beginDateC = (Calendar) today.clone();
        beginDateC.add(Calendar.DAY_OF_MONTH, beginDateOffset);
        Date beginDate = beginDateC.getTime();
        Calendar endDateC = (Calendar) today.clone();
        endDateC.add(Calendar.DAY_OF_MONTH, endDateOffset + 1);
        Date endDate = endDateC.getTime();

        for (Disponibility dispo : dispoList) {
            Date departureDate = dispo.getDepartureDate().toDate();
            if (!departureDate.before(beginDate) && departureDate.before(endDate)) {
                BigDecimal ttcPrice = dispo.getTtcPrice();
                if (ttcPrice.compareTo(cheapestPrice) < 0) {
                    cheapestPrice = ttcPrice;
                    selectedDispo = dispo;
                }
            }
        }

        return selectedDispo;
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
