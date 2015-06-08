/*
 * Created on 12 oct. 2011
 *
 * Copyright Travelsoft 2011</p>
 */
package com.travelsoft.lastminute.catalog.comparator;

import java.util.ArrayList;
import java.util.List;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.cms.processor.controller.AbstractController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.IContainerContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.CompareProductDisplayable;
import com.travelsoft.lastminute.data.CompareProductDisplayableList;
import com.travelsoft.lastminute.data.CriterionValueDisplayable;
import com.travelsoft.lastminute.data.CriterionValueDisplayableList;
import com.travelsoft.lastminute.data.SmallProductDisplayable;
import com.travelsoft.lastminute.data.SmallProductDisplayableList;

/**
 * <p>Titre : ComparatorListContructor.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class ComparatorListContructor extends AbstractController<ContentLayoutComponent, WebProcessEnvironment> {

    /**
     * Process business logic and produce the data model to construct comparator list.
     * @param context the component's context
     * @throws PageNotFoundException If the page is not found
     */
    public void process(IComponentContext<ContentLayoutComponent> context) throws PageNotFoundException {
        SmallProductDisplayableList productDisplayableList = (SmallProductDisplayableList) context
            .lookup(Constants.Context.PRODUCT_DISPLAYABLE_LIST);
        CompareProductDisplayableList compareDisplayableList = (CompareProductDisplayableList) context
            .lookup(Constants.Context.COMPARE_DISPLAYABLE_LIST);
        if (productDisplayableList != null && compareDisplayableList != null) {
            context.write(Constants.Context.COMPARE_DISPLAYABLE_LIST, compareDisplayableList);
            SmallProductDisplayable[] smallProdctDisplayables = productDisplayableList.getSmallProductDisplayables();
            CompareProductDisplayable[] compareDisplayables = compareDisplayableList.getCompareProductDisplayables();
            if (smallProdctDisplayables != null && smallProdctDisplayables.length > 0) {
                IContainerContext<?> containerContext = null;
                for (int i = 0; i < smallProdctDisplayables.length; i++) {
                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_DELETE_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context
                        .appendContainerContext(Constants.Containers.COMPARE_DESTINATION_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_HOTEL_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_TYPE_CONTAINER);
                    containerContext.write(Constants.Context.COMPARE_PRODUCT_DISPLAYABLE, compareDisplayables[i]);
                    containerContext = context
                        .appendContainerContext(Constants.Containers.COMPARE_MAINPICTURE_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_PRIX_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_REF_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_DATEDEP_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_CITYARR_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_DURATION_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext =
                                    context.appendContainerContext(Constants.Containers.COMPARE_PRESTATION_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_BTNCONTI_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_BTNBOTTOM_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    /*
                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_LUX_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_NOCES_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_RELAX_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_CHARM_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_DIVE_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_WITHOUANI_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_STUDENTS_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_CULTURAL_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_CLUB_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_BRIDGEWK_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);

                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_OFFRE_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);
                    */

                    containerContext = context
                        .appendContainerContext(Constants.Containers.COMPARE_DISPONIBILITY_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);
                    containerContext.write(Constants.Context.COMPARE_PRODUCT_DISPLAYABLE, compareDisplayables[i]);
                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_OPINION_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);
                    containerContext = context
                        .appendContainerContext(Constants.Containers.COMPARE_DESCRIPTION_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);
                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_INCLUDE_CONTAINER);
                    containerContext.write(Constants.Context.COMPARE_PRODUCT_DISPLAYABLE, compareDisplayables[i]);
                    containerContext = context.appendContainerContext(Constants.Containers.COMPARE_CONTINUE_CONTAINER);
                    containerContext.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, smallProdctDisplayables[i]);
                }
            }
            IContainerContext<?> containerContext = context
                .appendContainerContext(Constants.Containers.COMPARE_CRITERE_MAIN_CONTAINER);
            CriterionValueDisplayableList critereValueList = constructCriterionValueDisplayableList();
            if (critereValueList != null) {
                containerContext.write(Constants.Context.CRITERE_VALUE_LIST, critereValueList);
                containerContext.write(Constants.Context.COMPARE_DISPLAYABLE_LIST, compareDisplayableList);
            }
            this.processContainers(context);
        }
    }


    /**
     * Construct criterion displayable value list by criterion service.
     * @return valueList criterion displayable value list
     */
    private CriterionValueDisplayableList constructCriterionValueDisplayableList() {
        CriterionValueDisplayableList valueList = null;

        CriterionValue[] thematicValues = Util
                .getCriterionAllValueList(Constants.CriterionConstants.CRITERION_THEMATIC_CODE);
        if (thematicValues != null) {
            valueList = new CriterionValueDisplayableList();
            List<CriterionValueDisplayable> critereValues = new ArrayList<CriterionValueDisplayable>();

            if (thematicValues != null) {
                addCriterionValueDisplayables(critereValues, thematicValues,
                        Constants.CriterionConstants.CRITERION_THEMATIC_CODE);
            }
            int size = critereValues.size();
            CriterionValueDisplayable[] critereValueTable = new CriterionValueDisplayable[size];
            int count = 0;
            for (CriterionValueDisplayable critereValue : critereValues) {
                critereValueTable[count] = critereValue;
                count++;
            }
            valueList.setCriterionValueDisplayables(critereValueTable);
        }
        return valueList;
    }

    /**
     * Add criterion displayable values in the list by criterionValue table.
     * @param criterionValueDisplayables criterion value displayable list
     * @param criterionValues criterion values table
     * @param criterionNameCode criterion name code
     */
    private void addCriterionValueDisplayables(List<CriterionValueDisplayable> criterionValueDisplayables,
            CriterionValue[] criterionValues,
            String criterionNameCode) {
        for (CriterionValue criterionValue : criterionValues) {
            CriterionValueDisplayable critere = new CriterionValueDisplayable();
            String code = Util.appendString(criterionNameCode, ".", criterionValue
                    .getCode());
            String label = criterionValue.getLabel();
            critere.setCode(code);
            critere.setLabel(label);
            criterionValueDisplayables.add(critere);
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
