/*
 * Created on 13 oct. 2011
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
import com.travelsoft.lastminute.data.CompareProductDisplayable;
import com.travelsoft.lastminute.data.CompareProductDisplayableList;
import com.travelsoft.lastminute.data.CriterionValueDisplayable;
import com.travelsoft.lastminute.data.SmallProductDisplayable;

/**
 * <p>Titre : ComparatorSelectListConstructor.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class ComparatorSelectListConstructor extends AbstractController<ContentLayoutComponent, WebProcessEnvironment> {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(ComparatorSelectListConstructor.class);

    /**
     * Process business logic and produce the data model to get select criterion value displayable list in the page compare.
     * @param context the component's context
     */
    public void process(IComponentContext<ContentLayoutComponent> context) {
        CompareProductDisplayableList compareDisplayableList = (CompareProductDisplayableList) context
                .lookup(Constants.Context.COMPARE_DISPLAYABLE_LIST);

        boolean showLight = (Boolean) context.lookup(Constants.Context.COMPARE_SHOW_LIGHT);
        CriterionValueDisplayable criterionValueDisplayable = (CriterionValueDisplayable) context
                .lookup(Constants.Context.CRITERE_VALUE);
        String critereParentCode = criterionValueDisplayable.getCode();

        if (compareDisplayableList != null) {
            CompareProductDisplayable[] compareDisplayableTable = compareDisplayableList
                    .getCompareProductDisplayables();
            boolean showCheckedImage = false;
            int index = 0;
            for (CompareProductDisplayable compareProductDisplayable : compareDisplayableTable) {
                IContainerContext<?> containerContext = context
                        .appendContainerContext(Constants.Containers.COMPARE_CRITERE_SELECT_CONTAINER);
                showCheckedImage = false;
                String[] criterionValueDisplayableCodeTable = compareProductDisplayable.getCritereValueCode();
                if (criterionValueDisplayableCodeTable != null && criterionValueDisplayableCodeTable.length > 0) {
                    for (String valueCode : criterionValueDisplayableCodeTable) {
                        if (critereParentCode.equals(valueCode)) {
                            showCheckedImage = true;
                            break;
                        }
                    }
                }
                containerContext.write("productIndex", ++index);
                containerContext.write(Constants.Context.COMPARE_SHOW_CHECKED_IMAGE, showCheckedImage);
                containerContext.write(Constants.Context.CRITERE_VALUE, criterionValueDisplayable);
            }
        }
        context.write(Constants.Context.COMPARE_SHOW_LIGHT, showLight);
        try {
            this.processContainers(context);
        } catch (PageNotFoundException e) {
            LOGGER.error("Can not find the page for compare critere select block", e);
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
