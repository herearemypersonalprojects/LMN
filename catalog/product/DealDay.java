/**
 *
 */
package com.travelsoft.lastminute.catalog.product;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchResponse;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.search.PublishedProductSearchServicesInterface;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.data.SmallProductDisplayable;
import com.travelsoft.nucleus.cache.generic.CacheManager;
import com.travelsoft.nucleus.cache.jboss.implementations.JbossCacheManagerFactory;

/**
 * @author zouhair
 *
 */
public class DealDay extends AbstractFillProduct {

    /** The cache manager. */
    private static final String CAMELEO_TREE_CACHE = "CameleoTreeCache";

    /** The logger . */
    private static final Logger LOGGER = Logger.getLogger(DealDay.class);

	  /**
     * Process business logic and produce the data model to construct comparator list.
     * @param context the component's context
     * @throws PageNotFoundException If the page is not found
     */
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {

		PublishedProductSearchCriteria criteria = new PublishedProductSearchCriteria();
        criteria.setTtcMode(true);
        Criterion criterion = new Criterion();
        CriterionValue cv = new CriterionValue();
        criterion.setCode("dealDay");
        cv.setCode("true");
        criterion.setValue(new CriterionValue[]{cv});
        criteria.setCustomCriteria(new Criterion[]{criterion});
        criteria.setResultWithBaseAvail(true);
        criteria.setFirstResultIndex(0);
        criteria.setLastResultIndex(1);
        criteria.setDepartureCityCode(new String[]{"PAR"});
        PublishedProductSearchResponse response = null;
        PublishedProductSearchServicesInterface service = ServicesFactory.getPublishedProductSearchServices();
        if (service == null) {
            LOGGER.error("Failed to retrieve search products service.");
            return;
        }
        try {
        	CacheManager cacheManager = JbossCacheManagerFactory.getCacheManager(CAMELEO_TREE_CACHE);
            response = service.searchProducts(criteria, cacheManager);
            if (response != null && response.getProducts() != null && response.getProducts().length > 0
                    && response.getProducts(0).getBasePriceDisponibility() != null) {

                PublishedProduct pProduct = response.getProducts()[0];
                if (pProduct != null) {
                	SmallProductDisplayable dealDayDisplayable = this.constructProductDisplayable(pProduct, context);
                	context.write("dealDayDisplayable", dealDayDisplayable);
                }
            }
        } catch (TechnicalException e) {
            LOGGER.error("An technical error occured while searching for products.", e);
            return;
        }  catch (Exception e) {
            LOGGER.error("An general error occured while searching for products.", e);
            return;
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
