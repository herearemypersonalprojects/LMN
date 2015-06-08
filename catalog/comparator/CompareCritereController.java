/*
 * Created on 14 oct. 2011
 *
 * Copyright Travelsoft 2011</p>
 */
package com.travelsoft.lastminute.catalog.comparator;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.cms.processor.controller.AbstractController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.IContainerContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.data.CompareProductDisplayableList;
import com.travelsoft.lastminute.data.CriterionValueDisplayable;
import com.travelsoft.lastminute.data.CriterionValueDisplayableList;
import com.travelsoft.lastminute.data.SmallProductDisplayable;

/**
 * <p>Titre : CompareCritereController.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class CompareCritereController extends AbstractController<ContentLayoutComponent, WebProcessEnvironment> {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(CompareCritereController.class);

    /**
     * Process business logic and produce the data model to control the criterion block.
     * @param context the component's context
     */
    public void process(IComponentContext<ContentLayoutComponent> context) {
        CompareProductDisplayableList compareDisplayableList = (CompareProductDisplayableList) context
                .lookup(Constants.Context.COMPARE_DISPLAYABLE_LIST);

        SmallProductDisplayable smallProductDisplayable = (SmallProductDisplayable) context
                        .lookup(Constants.Context.SMALL_PRODUCT_DISPLAYABLE);

        CriterionValueDisplayableList criterionValueDisplayableList = (CriterionValueDisplayableList) context
                .lookup(Constants.Context.CRITERE_VALUE_LIST);
        if (criterionValueDisplayableList != null) {
            CriterionValueDisplayable[] criterionValueDisplayableTable = criterionValueDisplayableList
                    .getCriterionValueDisplayables();
            boolean showLight = false;
            if (criterionValueDisplayableTable != null && criterionValueDisplayableTable.length > 0) {
                for (int i = 0; i < criterionValueDisplayableTable.length; i++) {
                    IContainerContext<?> containerContext = context
                            .appendContainerContext(Constants.Containers.COMPARE_CRITERE_CONTAINER);

                    containerContext.write(Constants.Context.CRITERE_VALUE, criterionValueDisplayableTable[i]);
                    if (i % 2 == 0) {
                        showLight = true;
                    } else {
                        showLight = false;
                    }
                    containerContext.write(Constants.Context.COMPARE_SHOW_LIGHT, showLight);
                }
            }
        }
        context.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProductDisplayable);
        context.write(Constants.Context.COMPARE_DISPLAYABLE_LIST, compareDisplayableList);
        try {
            this.processContainers(context);
        } catch (PageNotFoundException e) {
            LOGGER.error("Can not find the page for compare critere block", e);
        }
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
