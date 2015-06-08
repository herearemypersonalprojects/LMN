package com.travelsoft.lastminute.catalog.tripadvisor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.review.FTPClientModel;
import com.travelsoft.lastminute.catalog.review.ReviewUtils;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.TripAdvisorReviews;


/**
 *
 * Controlling TripAdvisor reviews.
 *
 */
public class ReviewsController
    extends AbstractStructuredController
        <PageLayoutComponent, WebProcessEnvironment> {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(ReviewsController.class);

    /**
     *
     * Overriding method.
     *
     * @see com.travelsoft.cameleo.cms.processor.controller.IController#preview(com.travelsoft.cameleo.cms.processor.model.IComponentContext, com.travelsoft.cameleo.cms.processor.model.InjectionData)
     * @param context the component's context
     * @param injectionData the injection data
     * @throws PageNotFoundException If the page is not found
     */
    public void preview(IComponentContext<PageLayoutComponent> context, InjectionData injectionData)
                    throws PageNotFoundException {
    }

    @Override
    public void fillComponentContext(
        IComponentContext<PageLayoutComponent> context) throws PageNotFoundException {
        /** get product ID */
        PublishedProduct product = (PublishedProduct) context.lookup(Constants.Context.PUBLISHED_PRODUCT);
        String pid = String.valueOf(product.getCode());
        String fixedId = Util.getConfigValue("TRIPADVISOR_PARTNERID_REPLACED");
        String testedId = Util.getConfigValue("TRIPADVISOR_PARTNERID_TEST");

        if (StringUtils.isNotBlank(fixedId) && StringUtils.isNotBlank(testedId) && testedId.equalsIgnoreCase(pid)) {
            pid = fixedId;
        }

        String key = Util.getConfigValue("TRIPADVISOR_JSONKEY");
        String url = Util.getConfigValue("TRIPADVISOR_JSONURL") + pid;

        String reviewsUrl = url + "?key=" + key;
        String opinionsUrl = url + "/reviews?key=" + key;
        String awardsUrl = url + "/awards?key=" + key;


        /** display crawable reviews /var/data/static/lastminute/shared/cs/web/lastminute-catalog/reviews/ */
        String folderFullPath = Util.getConfigValue("TRIPADVISOR_FOLDERFULLPATH");
        String folderName = Util.getConfigValue("TRIPADVISOR_FOLDERNAME");
        String jsonSource = folderFullPath + folderName + product.getCode() + "/crawableReviewsList.txt";
        String crawableList =  new ReviewsRetriever().getReviewFilesList(jsonSource);
        context.write("crawableList", crawableList);


        String htmlFolder = folderFullPath + folderName + product.getCode();
        FTPClientModel.createDir(htmlFolder);

        // check whether jsons exist in the local disk
        String reviewsJson =  htmlFolder + "/reviews.json";
        String opinionsJson = htmlFolder + "/opinions.json";
        String awardsJson = htmlFolder + "/awards.json";

        Util.getJson(reviewsUrl, reviewsJson);
        Util.getJson(opinionsUrl, opinionsJson);
        Util.getJson(awardsUrl, awardsJson);

        TripAdvisorReviews reviews = new ReviewsRetriever().getReviews(reviewsJson, opinionsJson, awardsJson);
        context.write("reviews", reviews);

        // noindexable reviews
        if (reviews != null && reviews.getData() != null && reviews.getData().getReview() != null) {
            Map<String, Object> dataModel = new HashMap<String, Object>();
            dataModel = new HashMap<String, Object>();
            dataModel.put("reviews", reviews);
            String ftlFolder = "/var/data/static/lastminute/shared/ts/local/cms/lastminute/main/templates/product/";
            String ftlFile = "tripAdvisorReviews.ftl";

            String htmlFile = htmlFolder + "/tripAdvisorReviews.html";
            ReviewUtils.ftlToHtml(ftlFolder, ftlFile, htmlFile, dataModel);
        }
    }
}
