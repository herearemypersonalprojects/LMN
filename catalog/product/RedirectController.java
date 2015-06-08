/**
 *
 */
package com.travelsoft.lastminute.catalog.product;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.search.PublishedProductSearchServicesInterface;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.seo.SeoTool;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.nucleus.cache.generic.CacheManager;
import com.travelsoft.nucleus.cache.jboss.implementations.JbossCacheManagerFactory;

/**
 * <p>
 * Titre : RedirectController.
 * </p>
 * <p>
 * Description : .
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: Travelsoft
 * </p>
 *
 * @author zouhair.mechbal
 */
public class RedirectController extends AbstractStructuredController<PageLayoutComponent, WebProcessEnvironment> {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(RedirectController.class);

    /** The cache manager. */
    private static final String CAMELEO_TREE_CACHE = "CameleoTreeCache";

	public void preview(IComponentContext<PageLayoutComponent> arg0, InjectionData arg1) throws PageNotFoundException {

	}

	@Override
	public void fillComponentContext(IComponentContext<PageLayoutComponent> context)
			throws PageNotFoundException {
		try {
			HttpServletRequest request = this.getEnvironment().getRequest();
			String pid = request.getParameter("pid");
			String queryString = buildQueryQtring(request);
			String seoUrl = "";
			if (pid != null) {
				PublishedProductSearchServicesInterface publishedProductSearchServices = ServicesFactory.getPublishedProductSearchServices();
				PublishedProductSearchCriteria publishedProductSearchCriteria = new PublishedProductSearchCriteria();
				//publishedProductSearchCriteria.setResponseFormat(Constants.VERY_LIGHT_RESPONSE_FORMAT);
				publishedProductSearchCriteria.setResultWithDocuments(true);
				CacheManager cacheManager = JbossCacheManagerFactory.getCacheManager(CAMELEO_TREE_CACHE);
				PublishedProduct publishedProduct = publishedProductSearchServices.getPublishedProduct(pid, publishedProductSearchCriteria, cacheManager);
				if (publishedProduct != null) {
					 Document edito = publishedProduct.getEdito();
				        if (edito != null) {
				            MainZone titleZone = Util.getEditoMainZone(edito, "title");
				            seoUrl = SeoTool.getProductNameSeoUrl(Util.getZoneContent(titleZone));
				        }
				}
			}
			HttpServletResponse response = this.getEnvironment().getResponse();
			String productUrl = "/".concat(seoUrl).concat("/").concat(pid);
			if (!queryString.equals("")) {
				productUrl = productUrl.concat("?").concat(queryString);
			}
			response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
			response.setHeader("Location", productUrl);
			response.setHeader("Connection", "close");
			return;
		} catch (Exception e) {
			LOGGER.error("Error while trying to retirve product : " + e.toString());
			HttpServletResponse response = this.getEnvironment().getResponse();
			response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", "/serp.cms?s_aj=2");
			response.setHeader("Connection", "close");
			return;
		}
	}

	/**
	 * Retrieves the query string from the url in order to redirect to the new url.
	 * @param request The servlet request
	 * @return The quesry string
	 */
	private String buildQueryQtring(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer("");
		Map<String, String[]> map = request.getParameterMap();
		Set set = map.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
            Map.Entry<String, String[]> entry = (Entry<String, String[]>) it.next();
            String paramName = entry.getKey();
            if (paramName != null && !paramName.equals("pid")) {
                String[] paramValues = entry.getValue();
                if (paramValues != null && paramValues.length > 0) {
                	String value = paramValues[0];
                	if (sb.toString().length() != 0) {
                		sb.append("&");
                	}
                	sb.append(paramName).append("=").append(value);
                }
            }

        }
		return sb.toString();
	}
}
