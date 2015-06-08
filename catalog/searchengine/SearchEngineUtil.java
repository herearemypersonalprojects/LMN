/*
 * Created on 27 oct. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.searchengine;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.travelsoft.cameleo.catalog.data.CodeLabel;
import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.LayoutComponent;
import com.travelsoft.cameleo.catalog.data.SearchEngine;
import com.travelsoft.cameleo.cms.processor.messages.MessageRetriever;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.PageIdentifier;
import com.travelsoft.lastminute.data.SelectOption;
import com.travelsoft.lastminute.data.SelectOptionList;

/**
 * <p>Titre : SearchEngineUtil.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class SearchEngineUtil {

    /**
     * Method retrieves the top destination and departures cities.
     * @param context the context
     * @param searchEngine The search engine
     */
    public void retrieveTopDestinationAndDepartureCities(IComponentContext<? extends LayoutComponent> context,
            SearchEngine searchEngine) {
        MessageRetriever retriever = new MessageRetriever(getPageIdentifierInstance(), "searchEngine.properties");
        if (retriever != null) {
            this.retrieveTopDestination(context, searchEngine, retriever);
            this.retrieveTopDepartureCities(context, searchEngine, retriever);
        }
    }

    /**
     * @param context The context
     * @param searchEngine The search engine object
     * @param messageRetriever The <code>MessageRetriever</code> instance object
     */
    private void retrieveTopDepartureCities(IComponentContext<? extends LayoutComponent> context,
            SearchEngine searchEngine, MessageRetriever messageRetriever) {
        String topDepartureCityValue = messageRetriever.createMessage("searchEngine.topCity", null);
        if (topDepartureCityValue != null) {
            Map<String, String> topCityMap = parseValue(topDepartureCityValue, context, searchEngine);
            this.mergeDepartureCities(context, searchEngine, topCityMap, messageRetriever);
        }

    }

    /**
     * Method retrieves the top destination and departures cities.
     * @param context the context
     * @param searchEngine The {@link SearchEngine}
     * @param messageRetriever The message retriever
     */
    private void retrieveTopDestination(IComponentContext<? extends LayoutComponent> context,
            SearchEngine searchEngine, MessageRetriever messageRetriever) {
        String topDestinationValue = messageRetriever.createMessage("searchEngine.topDestination", null);
        if (topDestinationValue != null) {
            Map<String, String> topDestinationMap = parseValue(topDestinationValue, context, searchEngine);
            this.mergeDestination(context, searchEngine, topDestinationMap, messageRetriever);
        }
    }

    /**
     * @param context The context
     * @param searchEngine The search engine object
     * @param topDestinationMap The top destination as map
     * @param messageRetriever The <code>MessageRetriever</code> instance object
     */
    private void mergeDestination(IComponentContext<? extends LayoutComponent> context, SearchEngine searchEngine,
            Map<String, String> topDestinationMap, MessageRetriever messageRetriever) {
        SelectOptionList selectOptionList = new SelectOptionList();
        String topDestinationLabel = messageRetriever.createMessage("searchEngine.topDestination.label", null);
        if (topDestinationLabel != null) {
            String[] parts = topDestinationLabel.split("%");
            if (parts.length > 1) {
                SelectOption selectionOption = new SelectOption();
                selectionOption.setCode(parts[0]);
                selectionOption.setLabel(parts[1]);
                selectOptionList.addDestination(selectionOption);
            }
        }

        Criterion[] criterionArray = searchEngine.getCriterion();
        if (criterionArray != null) {
            for (Criterion criterion : criterionArray) {
                if (criterion != null && "de".equals(criterion.getCode())) {
                    CriterionValue[] criterionValues = criterion.getValue();
                    Iterator<Entry<String, String>> iterator = topDestinationMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Entry<String, String> entry = iterator.next();
                        String codeValue = entry.getKey();
                        if (codeValue != null && isCodeExistsInSearchEngineResult(codeValue, criterionValues)) {
                            SelectOption selectionOption = new SelectOption();
                            selectionOption.setCode(codeValue);
                            selectionOption.setLabel(entry.getValue());
                            selectOptionList.addDestination(selectionOption);
                        }
                    }
                }
            }
            context.write("topDestination", selectOptionList);
        }
    }

    /**
     * Merges the departure cities.
     * @param context The context
     * @param searchEngine The search engine
     * @param topCityMap The top destination as map
     * @param messageRetriever The <code>MessageRetriever</code> instance object
     */
    private void mergeDepartureCities(IComponentContext<? extends LayoutComponent> context, SearchEngine searchEngine,
            Map<String, String> topCityMap, MessageRetriever messageRetriever) {
        SelectOptionList selectOptionList = new SelectOptionList();
        String topCitiesLabel = messageRetriever.createMessage("searchEngine.topCity.label", null);

        if (topCitiesLabel != null) {
            String[] splitValues = topCitiesLabel.split("%");
            if (splitValues.length > 1) {
                SelectOption selectionOption = new SelectOption();
                selectionOption.setCode(splitValues[0]);
                selectionOption.setLabel(splitValues[1]);
                selectOptionList.addDepartureCities(selectionOption);
            }
        }

        Iterator<Entry<String, String>> iterator = topCityMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            String codeValue = entry.getKey();
            if (codeValue != null && isCodeExistsInSearchEngineResult(codeValue, searchEngine.getDepartureCity())) {
                SelectOption selectionOption = new SelectOption();
                selectionOption.setCode(codeValue);
                selectionOption.setLabel(entry.getValue());
                selectOptionList.addDepartureCities(selectionOption);
            }
        }
        context.write("topDepartureCities", selectOptionList);
    }

    /**
     * Checks if the code exists in map key.
     *
     * @param code The code
     * @param codeLabelArray The properties value as map
     * @return true if code exists in the given code label array
     *
     * FIXME this is a O(n) search called from within loops => avoid it unless the codeLabelArray is extremely short
     */
    private boolean isCodeExistsInSearchEngineResult(String code, CodeLabel[] codeLabelArray) {
        if (codeLabelArray != null) {
            for (CodeLabel codeLabel : codeLabelArray) {
                if (codeLabel != null && code.equals(codeLabel.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Parses the value in the properties file.
     * @param value the value in the properties file
     * @param context The context
     * @param searchEngine The search engine
     * @return the properties value as map
     */
    private Map<String, String> parseValue(String value, IComponentContext<? extends LayoutComponent> context,
            SearchEngine searchEngine) {
        Map<String, String> codeLabelMap = new LinkedHashMap<String, String>();
        String[] codeLabelDestionationArray = value.split(";");
        if (codeLabelDestionationArray != null) {
            for (String codeLabelDestionation : codeLabelDestionationArray) {
                if (codeLabelDestionation != null) {
                    String[] codeLabel = codeLabelDestionation.split("%");
                    if (codeLabel != null && codeLabel.length > 1) {
                        codeLabelMap.put(codeLabel[0], codeLabel[1]);
                    }
                }
            }
        }
        return codeLabelMap;
    }

    /**
     * Method builds PageIdentifier object.
     * @return PageIdentifier object
     */
    private PageIdentifier getPageIdentifierInstance() {
        String staticRoot = "/var/data/static/lastminute";
        String applicationCode = "lastminute";
        String siteCode = "lastminute";
        Locale locale = Locale.getDefault();
        return new PageIdentifier(staticRoot, applicationCode, null, siteCode, locale);
    }
}
