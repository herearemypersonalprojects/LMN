/*
 *
 */
package com.travelsoft.lastminute.catalog.serp;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.cms.processor.controller.AbstractController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.IContainerContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.Partitioner;
import com.travelsoft.lastminute.data.TrackingData;

/**
 * <p>Titre : GetPartitionner.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class GetPartitioners extends AbstractController<ContentLayoutComponent, WebProcessEnvironment> {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(GetPartitioners.class);

    /**
     * Process business logic and produce the data model to get partitioner for the page results.
     * @param context the component's context
     */
    public void process(IComponentContext<ContentLayoutComponent> context) {
        try {

            int pageSizeNumber = this.getPageSizeProductsCapacity(context);

            WebProcessEnvironment webEnvironment = this.getEnvironment();

            String query = (String) context.lookup("queryString");
          
            if (query == null) {
                query = "s_aj=2";
            }

            Integer resultsNumber  = (Integer) context.lookup(Constants.Context.RESULTS_NUMBER);
            double doubleResultsNumber = (double) resultsNumber / (double) pageSizeNumber;
            double nbPartitionerDouble = Math.ceil(doubleResultsNumber);
            String nbPartitionerString = String.valueOf(nbPartitionerDouble);
            int dotIndex = 0;
            dotIndex = nbPartitionerString.indexOf(".");
            if (dotIndex != -1) {
                nbPartitionerString = nbPartitionerString.substring(0, dotIndex);
            }
            int nbPartitioners = Integer.parseInt(nbPartitionerString);

            this.writePartitionInContext(webEnvironment, query, Constants.PartitionerConstants.LAST_PAGE_NUMBER,
                    pageSizeNumber, nbPartitioners, resultsNumber, context);

            int currentPageNumber = currentPageNumber(webEnvironment, pageSizeNumber);

            // save current page number in global context
            Object tData = context.lookup(Constants.Context.TRACKING_DATA);
            if (tData instanceof TrackingData) {
                TrackingData trackingData = (TrackingData) tData;
                trackingData.getSerp().setCurrentPageNmb(currentPageNumber);
            } else {
                LOGGER.warn("I was expecting a TrackingData instance in the context, but found " + tData
                    + ". TrackingData will not be collected at this moment.");
            }

            int startPageIndex = calculateStartPageIndex(currentPageNumber, nbPartitioners);
            int endPageIndex = calculateEndPageIndex(currentPageNumber, startPageIndex, nbPartitioners);
            for (int i = startPageIndex; i < endPageIndex; i++) {
                writePartitionInContext(webEnvironment, query, i,
                        pageSizeNumber, nbPartitioners, resultsNumber,
                        context);
            }

            this.writePartitionInContext(webEnvironment, query, Constants.PartitionerConstants.NEXT_PAGE_NUMBER,
                    pageSizeNumber, nbPartitioners, resultsNumber, context);


            this.processContainers(context);
        } catch (PageNotFoundException e) {
            LOGGER.error("Can not find this page for partitioner : ", e);
        }
    }

    /**
     * Gets the page size products size capacity.
     * @param context The context
     * @return the page size products size capacity
     */
    private int getPageSizeProductsCapacity(IComponentContext<ContentLayoutComponent> context) {
        int result = 20;
        try {
            String produtcsMaxPage = this.getEnvironment().getRequest()
                    .getParameter(Constants.PartitionerConstants.PRODUCTS_MAX_PAGE);
            if (produtcsMaxPage != null) {
                result = Integer.valueOf(produtcsMaxPage);
            } else {
                return (Integer) context.lookup("defaultLastIndex");
            }
        } catch (Exception e) {
            LOGGER.error("Error with trying to get the maximum products per page.", e);
        }
        return result;
    }

    /**
     * Calculate the start page index.
     * @param currentPageNumber the current number
     * @param totalPartitioners the total partitioner number
     * @return startPage the start page number
     */
    private int calculateStartPageIndex(int currentPageNumber, int totalPartitioners) {
        int startPage = 1;
        int showPageIndex = Constants.PartitionerConstants.SHOW_MAX_PAGES / 2;
        int lastStartIndex = totalPartitioners - Constants.PartitionerConstants.SHOW_MAX_PAGES + 1;
        if (currentPageNumber > showPageIndex + 1) {
            if (currentPageNumber >= lastStartIndex + showPageIndex) {
                startPage = lastStartIndex;
            } else {
                startPage = currentPageNumber - showPageIndex;
            }
        }
        return startPage;
    }

    /**
     * Calculate the end page index.
     * @param currentPageNumber the current number
     * @param startPageIndex the start page index
     * @param totalPartitioners the total partitioner number
     * @return endPage the end page number
     */
    private int calculateEndPageIndex(int currentPageNumber, int startPageIndex, int totalPartitioners) {
        int endPageIndex = 1;
        endPageIndex = startPageIndex + Constants.PartitionerConstants.SHOW_MAX_PAGES;
        if (endPageIndex > totalPartitioners) {
            endPageIndex = totalPartitioners + 1;
        }
        return endPageIndex;
    }

    /**
     * Write the partitioner by the given condition.
     * @param webEnvironment the web container environment
     * @param query the query in the web container environment
     * @param pageNumber the page number
     * @param pageSize the page size
     * @param nbPartitioners the partitioner number
     * @param resultsNumber the results total number
     * @param context the component context
     */
    private void writePartitionInContext(WebProcessEnvironment webEnvironment, String query, int pageNumber,
            int pageSize, int nbPartitioners, int resultsNumber, IComponentContext<ContentLayoutComponent> context) {
        Partitioner partitioner = getPartitioner(webEnvironment, query, pageNumber, pageSize, nbPartitioners,
                resultsNumber);
        if (partitioner != null) {
            IContainerContext<?> containerContext = context
                    .appendContainerContext(Constants.Containers.PAGINATION_CONTAINER);
            containerContext.write(Constants.Context.PARTITIONER, partitioner);
        }
    }

    /**
     * Write the partitioner by the given condition.
     * @param webEnvironment the web container environment
     * @param query the query in the web container environment
     * @param pageNumber the page number
     * @param pageSize the page size
     * @param nbPartitioners the partitioner number
     * @param resultsNumber the results total number
     * @return partitioner the partitioner
     */
    private Partitioner getPartitioner(WebProcessEnvironment webEnvironment, String query, int pageNumber,
            int pageSize, int nbPartitioners, int resultsNumber) {
        Partitioner partitioner = new Partitioner();
        String finParam = webEnvironment.getRequestParameter(Constants.PartitionerConstants.FIN);
        String linParam = webEnvironment.getRequestParameter(Constants.PartitionerConstants.LIN);
        int currentPageNumber = currentPageNumber(webEnvironment, pageSize);
        if (currentPageNumber == pageNumber) {
            partitioner.setCurrent(true);
        }
        String finQuery = Util.appendString(Constants.PartitionerConstants.FIN,
                Constants.CriterionConstants.CRITERION_EQUAL, finParam);

        String linQuery = Util.appendString(Constants.PartitionerConstants.LIN,
                Constants.CriterionConstants.CRITERION_EQUAL, linParam);

        String deletedQuery = deleteQueryParams(query, finQuery, linQuery);
        int fin = 0;
        int lin = 0;
        String pageLabel = "";
        if (pageNumber > 0) {
            fin = (pageNumber - 1) * pageSize;
            if (pageNumber == nbPartitioners) {
                lin = resultsNumber;
            } else {
                lin = pageNumber * pageSize;
            }
            pageLabel = String.valueOf(pageNumber);
        } else if (pageNumber == Constants.PartitionerConstants.NEXT_PAGE_NUMBER) {
            if (currentPageNumber == nbPartitioners) {
                return null;
            } else {
                fin = currentPageNumber * pageSize;
                lin = (currentPageNumber + 1) * pageSize;
                pageLabel = String.valueOf(Constants.PartitionerConstants.NEXT_PAGE_NUMBER);
            }

        } else if (pageNumber == Constants.PartitionerConstants.LAST_PAGE_NUMBER) {
            if (currentPageNumber == 1) {
                return null;
            } else {
                fin = (currentPageNumber - 2) * pageSize;
                lin = (currentPageNumber - 1) * pageSize;
                pageLabel = String.valueOf(Constants.PartitionerConstants.LAST_PAGE_NUMBER);
            }
        }
        String url = Util.appendString(Constants.Common.SERP_PAGE_PART_URL,
                deletedQuery, Constants.CriterionConstants.CRITERION_SEPARATOR,
                Constants.PartitionerConstants.FIN,
                Constants.CriterionConstants.CRITERION_EQUAL, String.valueOf(fin),
                Constants.CriterionConstants.CRITERION_SEPARATOR,
                Constants.PartitionerConstants.LIN,
                Constants.CriterionConstants.CRITERION_EQUAL, String.valueOf(lin));
        partitioner.setLabel(pageLabel);
        partitioner.setUrl(url);
        return partitioner;
    }

    /**
     * Get the current page number by web container environment.
     * @param myEnvironment the web container environment
     * @param pageSize the page size
     * @return currentPageNumber the current page number
     */
    private int currentPageNumber(WebProcessEnvironment myEnvironment, int pageSize) {
        int currentPageNumber = 1;
        String finParam = myEnvironment.getRequestParameter(Constants.PartitionerConstants.FIN);
        String linParam = myEnvironment.getRequestParameter(Constants.PartitionerConstants.LIN);
        if (finParam != null && linParam != null) {
            int currentFin = Integer.valueOf(finParam);
            currentPageNumber = (currentFin / pageSize) + 1;
        }
        return currentPageNumber;
    }

    /**
     * Delete the certain parameters in the query.
     * @param query the query from url
     * @param params the dynamic parameters
     * @return query the deleted query
     */
    private String deleteQueryParams(String query, String... params) {
        String deletedQuery = query;
        for (String param : params) {
            int paramIndex = deletedQuery.indexOf(param);
            if (paramIndex == -1) {
                break;
            } else {
                char compareChar = deletedQuery.charAt(paramIndex - 1);
                String[] queryTable = null;
                char separator = Constants.CriterionConstants.CRITERION_SEPARATOR.charAt(0);
                if (compareChar == separator) {
                    queryTable = deletedQuery.split(Constants.CriterionConstants.CRITERION_SEPARATOR + param);
                } else {
                    queryTable = deletedQuery.split(param);
                }
                if (queryTable.length == 2) {
                    deletedQuery = queryTable[0] + queryTable[1];
                } else if (queryTable.length == 1) {
                    deletedQuery = queryTable[0];
                }
            }
        }
        return deletedQuery;
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
