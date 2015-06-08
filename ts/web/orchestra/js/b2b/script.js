
(function($, orx){
  var module = "b2b";

  //*********************//
  // Add to fav list     //
  //*********************//
  //When using the select boxes
  $(document).on("click", ".li_favlist", function() {
    // Get the ids of checked products
    var ids = "";
    var names = "";
    $("#innerWrapContent tr").each(function( index ) {
    if ($(this).find(".cbx_tr").is(':checked')) {
      if( ids == "") {
        ids = $(this).data("id");
      } else {
        ids = ids + "," + $(this).data("id");
      }

      if( names == "") {
        names = $(this).data("label");
      } else {
        names = names + "," + $(this).data("label");
      }
    }
    });

    var listName = $(this).data("listname");
    orxapi.addToFav(ids, names, listName);
    $("#favoriteUser").find("li[data-oldLabel='" + listName + "']").evtAnim();
    orxapi.unselectProducts();
  });

  //Highlight the list when products are added to it
  $.fn.evtAnim = function () {
    $(this).addClass("animeEvt").removeClass("animeEvt", 500, "easeInBack");
  };

  //When applying an action on a single result
  $(document).on("click", ".prdtBlock .dropdown-action", function() {
    // Get the id of current product
    var ids = $(this).parents("tr").data("id");
    var names = $(this).parents("tr").data("label");
    var listName = "";
    orxapi.addToFav(ids, names, listName);
  });

  //Ajax call to add products in a favorite list
  orxapi.addToFav = function(ids, names, listName) {
    if (ids == "") {
      return;
    }
    $.ajax($.extend({}, orxapi.ajax, {
        url : "processAddToFavorite.action",
        data : {ids : ids, names : names, listName : listName},
        success : function(data, textStatus, jqXHR) {
          if (data != null && $("#favoriteUser").length > 0) {
            //Change the displayed number of product of the list
            var nbOfProducts = parseInt(data);
            var li = $("#favoriteUser").find("li[data-oldLabel='" + listName + "']");
            var oldNbr = li.find(".nbre").text();
            var newNbr = parseInt(oldNbr) + nbOfProducts;
              li.find(".nbre").text(newNbr);
          }
        }
    }));
  }
  //****************************//
  // End of add to fav list     //
  //****************************//

  //Unselect all checkboxes
  orxapi.unselectProducts = function() {
    $(".cbx_tr").attr('checked', false);
  }

  orxapi.retrieveLastAdvSearch = function() {
    var lastSearch = orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch");
    var lastSearchResultsNumber = orxapi.selectors.tabsBarZone.find("li.active").data("lastSearchResultsNumber");
    if (lastSearch) {
      var lastSearchList = $("#lastSearchList");
      if (lastSearchResultsNumber) {
        lastSearchList.find(".nbre").text(lastSearchResultsNumber);
      }
      lastSearchList.attr("data-criteria", lastSearch);
      lastSearchList.removeClass("hidden");
    }

//    if (lastSearch != null && lastSearch != "") {
//      $("#advSearchForm").deserialize(lastSearch);
//      orxapi.completeSearchForm(lastSearch);
//      $("#advSearchForm").find("input[value!='']:not(:disabled):not(:checkbox), select[value!='']:not(:disabled)").addClass("lightValue");
//      $("#bodyContent").find(".searchBtn").trigger("click");
//    }
  }

  orxapi.completeSearchForm = function(lastSearch) {
    //Nothing to do so far
  }

  //Add or update the "Last Search" side bar list
  orxapi.createOrUpdateLastSearchList = function(lastSearch, lastSearchAction) {
    var lastSearchList = $("#lastSearchList");
    if (typeof(lastSearch) == "undefined") {
      lastSearchList.addClass("disabled");
      return false;
    }
    var formCode = orxapi.parser.getParameter(lastSearch, "formCode");
    lastSearchList.data("criteria", lastSearch);
    lastSearchList.data("formCode", formCode);
    lastSearchList.find(".nbre").text(orxapi.selectors.tabsBarZone.find("li.active").data("lastSearchResultsNumber"));
    lastSearchList.removeClass("disabled");
    if (lastSearchAction ==  "processSearchOnlyResults") {
      // we have to contruct the whole page, not only the results page in this case
      lastSearchList.data("page", "processSimpleSearch");
    } else {
      lastSearchList.data("page", lastSearchAction);
    }
  }

  // TODO : change this function and avoid the ajax calls? (we already come from the server)
  orxapi.restoreForm = function (params, form) {
    var formCode = orxapi.parser.getParameter(params, "formCode");

    // Restore the country cascade dropdown
    var countryCode = orxapi.parser.getParameter(params, "criteria.countryCode");
    var cityCode = orxapi.parser.getParameter(params, "criteria.cityCode");
    if (countryCode && countryCode != "") {
      form.find("#slt_city").loadDestinations(countryCode, formCode, cityCode);
    }

    // Restore the cameleo fields cascade dropdowns
    $("select.cameleoMaster").each(function() {
      var critCode = $(this).data("field-value");
      var critValues = orxapi.parser.getParameters(params, "doubleCameleoCriterion%5B'" + critCode + "'%5D");
      var slave = $("#slt_cameleoDouble_slave_" + critCode);
      if (critValues != null && critValues.length > 0) {
        var selectedMasterValue = critValues[0];
        if (selectedMasterValue && selectedMasterValue != "") {
          // Restore the master value.
          $(this).val(selectedMasterValue);

          // Load the slave values, preselect the right one if needed.
          var mastValues = selectedMasterValue.split(".");
          var selectedMasterDepth = mastValues.length;
          var selectedSlaveValue = "";
          if (critValues.length > 1) {
            selectedSlaveValue = critValues[1];
          }
          slave.loadSlaves(critCode, selectedMasterValue, formCode, selectedMasterDepth, selectedSlaveValue);
        }
      }
    })

    // Restore the form
    form.deserialize(params);

    // Enable the date margin if we have a date
    var date = $("#ipt_ptnStartDate").val();
    if (date && date != "") {
      $("#ipt_daysMargin").removeAttr("disabled");
    }

    //handle the read only fields
    $(".addedCriteria").find(".read_only_field").each(function() {
      var div = $(this);
      var field = $(this).find("input");
      if (field.length > 0 ) {
        if (typeof(field.val()) != "undefined" && field.val() != "") {
          field.removeAttr("disabled");
        } else {
          field.attr("disabled", "true");
        }
      }


      var select = $(this).find("select");
      if (select.length > 0 ) {
        if (select.find(":selected").length > 0) {
          field.removeAttr("disabled");
        } else {
          field.attr("disabled", "true");
        }
      }
    });

  }

  /**
   * Retrieves the preferences cookie
   */
  orxapi.getPreferencesCookie = function() {
    var cookieStr = $.cookie("preferences");
    return JSON.parse(cookieStr);
  }

  /**
   * Stores or updates the preferences cookie with the input data
   */
  orxapi.updatePreferencesCookie = function(data) {
    var cookie = orxapi.getPreferencesCookie();
    if (cookie == null) {
      cookie = {};
    }
    $.extend(cookie, data);
    cookieStr = JSON.stringify(cookie);
    $.cookie("preferences", cookieStr);
  }
  /**
   * Stores or updates the sort preferences cookie with the input selectedSort & ascSort
   */
  orxapi.updateSortCookie = function(selectedSort, ascSort) {
      sort = {
              selectedSort :selectedSort,
              ascSort : ascSort,
          };
      orxapi.updatePreferencesCookie(sort);

  }
  /**
   * remove an item form a serialized list
   */
  orxapi.deleteItemFromSerializedList = function(strSerialize, strParamName) {
    var arrSerialize = strSerialize.split("&");
    var i = arrSerialize.length;

    while (i--) {
      if (arrSerialize[i].indexOf(strParamName+"=") == 0) {
        arrSerialize.splice(i,1);
      }
    }

    return arrSerialize.join("&");
  }

  /**
   * Remove a value in a comma separated string and returns the new comma separated string.
   */
  orxapi.removeValueFromCommaSeparatedString = function(commaSeparatedString, valueToRemove) {
    var values = commaSeparatedString.split(",");
    for(var i = 0 ; i < values.length ; i++) {
      if(values[i] == valueToRemove) {
        values.splice(i, 1);
        return values.join(",");
      }
    }
    return commaSeparatedString;
  }

  /**
   * Fill the data of the product tab and open it
   */
  orxapi.updateProductTabData = function (id, prefs, toProductCode) {
    // if tab was already opened, click on it.
    if ($("#tabsBarZone").find("li[data-id='" + id + "']").length) {
      // tab for this file was already opened. Navigate to it.
      if (prefs != null) {
        var productTab = orxapi.selectors.tabsBarZone.find("li[data-id='" + id + "']");
        productTab.data("prefs", prefs);
        productTab.data("sidebar", "formBook");
      }
      $("#tabsBarZone").find("li[data-id='" + id + "']").trigger("click");
    } else {
      var id = id;
      var module = orx.selectors.body.data("module");
      var shared = "false";

      if(module == "b2b") {
        var pge = "pge_product";
      } else {
        var pge = "pge_file";
      }

      $.ajax($.extend({}, orxapi.ajax, {
        url : pge + ".action",
        data: {id : id},
        success : function(data, textStatus, jqXHR) {
          orxapi.selectors.bodyContent.removeClass().addClass(pge).html(data);
          orxapi.addTab(pge);
          orxapi.loadJs(module, pge);
          if (prefs != null) {
            var productTab = orxapi.selectors.tabsBarZone.find("li[data-id='" + id + "']");
            productTab.data("prefs", prefs);
            productTab.data("sidebar", "formBook");
          }
        }

      }));
    }
  }

  /**********************
   * COMPARATOR         *
   **********************/
  /**
   * Add products to the comparator.
   */
  orxapi.addProductToComparator = function(productIds) {
    if (productIds == "") {
      return;
    }
    var comparator = $("#headerBarZone").find("li[data-nav='pge_comparator']");
    var maxNumber = comparator.data("maxnumber");

    //merge both comma separated list of ids
    var existingIds = comparator.data("param");
    var idsToSet = existingIds;
    if (idsToSet && idsToSet != "") {
      idsToSet += "," + productIds;
    } else {
      idsToSet = productIds;
    }
    idsToSet = idsToSet + "";

    //remove duplicates and trim ids if needed
    var splitted = idsToSet.split(',');
    var collector = {};
    for (i = 0; i < splitted.length; i++) {
       key = splitted[i].replace(/^\s*/, "").replace(/\s*$/, "");
       collector[key] = true;
    }
    var out = [];
    for (var key in collector) {
       out.push(key);
    }
    var numberOfProducts = out.length;
    var output = out.join(',');

    //Test if we can add it
    if (maxNumber != "") {
      maxNumber = parseInt(maxNumber);
      if (maxNumber > 0 && numberOfProducts > maxNumber) {
        //Too many products
        var contentDialog = $("<div/>",{ "id":"contentDialog"});
        contentDialog.html(comparator.data("limitreachedmessage"));
        contentDialog.dialog($.extend({}, orx.plugin.dialog, {
          buttons:{
             "OK": function() {
               $(this).dialog("close");
               return;
             }
          }
        }));
        return;
      }
    }

    //Set the ids and the number of product in the header icon
    comparator.data("param", output);
    comparator.find(".notificationBubble").text(numberOfProducts);
    if (numberOfProducts == 0) {
      comparator.find(".notificationBubble").addClass("hidden");
      comparator.addClass("disabled");
    } else {
      comparator.find(".notificationBubble").removeClass("hidden");
      comparator.removeClass("disabled");
    }

    //Set the ids in the comparator tab if it exists
    var comparatorTab = orxapi.selectors.tabsBarZone.find("li.tab_comparator");
    if (comparatorTab.length > 0) {
      comparatorTab.data("id", output);
    }

    //Set the ids and the number in the cookie
    var data = {comparatorIds : output,
        comparatorNumber : numberOfProducts};
    orxapi.updatePreferencesCookie(data);
  }

  function restoreComparator() {
    //Basing on the preference cookie, fill the comparator when loading the b2b
    var prefCookie = orxapi.getPreferencesCookie();
    if (!$.isEmptyObject(prefCookie)) {
      if (typeof(prefCookie.comparatorIds) != "undefined") {
        var prefCookieComparatorIds = prefCookie.comparatorIds;
      }
      if (typeof(prefCookie.comparatorNumber) != "undefined") {
        var prefCookieComparatorNumber = prefCookie.comparatorNumber;
      }
    }
    if (typeof(prefCookieComparatorIds) != "undefined") {
      //Set in the header icon
      $("#headerBarZone").find("li[data-nav='pge_comparator']").data("param", prefCookieComparatorIds);
      if (typeof(prefCookieComparatorNumber) != "undefined") {
        $("#headerBarZone").find("li[data-nav='pge_comparator']").find(".notificationBubble").text(prefCookieComparatorNumber);
        if (parseInt($("#headerBarZone").find("li[data-nav='pge_comparator']").find(".notificationBubble").text()) == 0) {
          $("#headerBarZone").find("li[data-nav='pge_comparator']").find(".notificationBubble").addClass("hidden");
          $("#headerBarZone").find("li[data-nav='pge_comparator']").addClass("disabled");
        } else {
          $("#headerBarZone").find("li[data-nav='pge_comparator']").find(".notificationBubble").removeClass("hidden");
          $("#headerBarZone").find("li[data-nav='pge_comparator']").removeClass("disabled");
        }
      }
      //Set in the tab
      if (orxapi.selectors.tabsBarZone.find("li.tab_comparator").length > 0) {
        orxapi.selectors.tabsBarZone.find("li.tab_comparator").data("id", prefCookieComparatorIds);
      }
    }
  }
  /**********************
   * END OF COMPARATOR  *
   **********************/

  /**********************
   *                    *
   *   PRODUCT SEARCH   *
   *                    *
   **********************/
  //Switch the search form
  function switchSearchForm(formCode){
    var url;

    if ($("#searchZone").hasClass("additionalSearch")) {
      url = "prepareAdditionalAdvSearch";
    } else {
      url = "prepareAdvSearch";
    }

    $.ajax($.extend({}, orxapi.ajax, {
      url : url,
      data : {formCode: formCode},
      success : function(data, textStatus, jqXHR) {
        $("#searchZone").html(data);
        orxapi.loadJs(module, "pge_list/pge_advSearch", false);
      }
    }));
    return false;
  }

  //Switch the search form by click on the tabs (default)
  $(document).on("click", "#searchZone #tab_formType li", function() {
    var formCode = $(this).find("#formCode").data("value");
    switchSearchForm(formCode);
  });

  //Switch the search form by click on the right droplist
  $(document).on("change", "#searchZone #lst_formType", function() {
    var formCode = $(this).val();
    switchSearchForm(formCode);
  });

  /*--------------------------
    -   PROCESS THE SEARCH   -
    --------------------------*/

  $(document).on("click", "#searchZone .searchBtn", function(evt) {

    if(evt.which == undefined) {
        return;
    }

    if ($(".advSearchForm").valid()) {
      var dataPage = $(this).data("page");
      var url = dataPage + ".action";
      var formCode = $("#ipt_formType").val();

      // ognl fix: remove empty values from the list of params. if not, inputs with integers will be considered as strings
      var params = $('.advSearchForm').find('input, select').filter(function () {return $.trim(this.value);}).serialize();
      //set the result mode (cartouches or table) based on the cookie
      var prefsCookie = orx.getPreferencesCookie();
      if (!$.isEmptyObject(prefsCookie)) {
        if (typeof(prefsCookie.boxSearch) != "undefined") {
          params += "&boxSearch=" + prefsCookie.boxSearch;
        }
        // Retrieving sort information from cookies
        if (prefsCookie.selectedSort) {
            params += "&selectedSort=" + prefsCookie.selectedSort;
        }
        if (prefsCookie.ascSort) {
              params += "&ascSort=" + prefsCookie.ascSort;
        }
      }

      orxapi.storeTabCookie();

      // Fill the lastSearch fields
      $("#wrapSideBar").find(".active").removeClass("active");
      $("#lastSearchList").addClass("active");
      orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch", params);
      orxapi.selectors.tabsBarZone.find("li.active").data("submenu", "lastSearchList");
      orxapi.selectors.tabsBarZone.find("li.active").data("lastSearchAction", dataPage);

      // Fill the preferences cookie if needed
      var data = {};
      var depCities = $("#slt_departureCity").val();
      var depDate = $("#ipt_ptnStartDate").val();
      var dateMargin = $("#ipt_daysMargin").val();
      if (dateMargin && dateMargin != "") {
        $.extend(data, {margin : dateMargin});
      }
      var nightsInterval = $("#ipt_nightsDuration").val();
      var daysInterval = $("#ipt_daysDuration").val();

      $.extend(data, {depCities : depCities});
      $.extend(data, {depDate : depDate});
      $.extend(data, {nightsInterval : nightsInterval});
      $.extend(data, {daysInterval : daysInterval});

      if (!$.isEmptyObject(data)) {
        orxapi.updatePreferencesCookie(data);
      }

      if (dataPage == "processSearchWithAdditional") {
        // Process the search and calculate the new form
        $.ajax($.extend({}, orxapi.ajax, {
          url : url,
          data : params,
          success : function(data, textStatus, jqXHR) {
            $("#wrapContent").html(data);
            orxapi.restoreForm(params, $("#advSearchForm"));

            orxapi.createOrUpdateLastSearchList(params, dataPage);
            //set the resultNumber
            orxapi.refreshResultNumberInLastSearch();

            //Display the serialized criteria in the url
            location.hash = '';
            location.hash = params;

            orxapi.loadJs(module, "pge_list/pge_advSearch", false);
            $.getScript("./shared/ts/web/js/b2b/pge_list/pge_resultList.js");
          }
        }));
      } else {
        // Process a simple search without changing the form, just update the results list.
        $.ajax($.extend({}, orxapi.ajax, {
          url : url,
          data : params,
          success : function(data, textStatus, jqXHR) {
            $("#resultTable").html(data);

            orxapi.createOrUpdateLastSearchList(params, dataPage);
            //set the resultNumber
            orxapi.refreshResultNumberInLastSearch();

            //Display the serialized criteria in the url
            location.hash = '';
            location.hash = params;

            $.getScript("./shared/ts/web/js/b2b/pge_list/pge_resultList.js");
          }
        }));
      }

    }
    return false;
  });

  orxapi.refreshResultNumberInLastSearch = function() {
      var resultNumber = $("#resultNumber").data("resultnumber");
      $("#lastSearchList").find(".nbre").text(resultNumber);
      orxapi.selectors.tabsBarZone.find("li.active").data("lastSearchResultsNumber", resultNumber);
      orxapi.storeTabCookie();
  }

  //Set the min prices state in the list TAB
  orxapi.refreshMinPricesState = function(minPricesState) {
    orxapi.selectors.tabsBarZone.find("li.active").data("minprices", minPricesState);
    orxapi.storeTabCookie();
  }

  /*-------------------------
    -   RESET SEARCH FORM   -
    -------------------------*/
  $(document).on("click", ".resetBtn", function() {
    if ( $(this).parents("form:eq(0)").find("#tab_formType li.active").length) {
      // click on tab that corresponds to current search
      $(this).parents("form:eq(0)").find("#tab_formType li.active").trigger("click");
    } else {
      orxapi.resetForm($(this).parents("form"), true);
    }
    return false;
  });

  orxapi.resetForm = function(form, emptyResultTable) {

      var $form = form;
      orxapi.resetFormGeneric($form);

      //Remove the relevant criteria
      $(".addedCriteria").remove();

      //set the "new search" button as active
      if (!$("#newAdvSearch").hasClass("active")) {
        $("#lastSearchList").removeClass("active");
        $("#newAdvSearch").addClass("active");
      }

      // Slave fields (cascade dropdowns)
      $form.find("select.slave").each(function() {
        $(this).reset();
        $(this).attr("disabled", "disabled");
      });

      if (emptyResultTable) {
        $("#resultTable").empty();
      }

      //Disable the daysMargin select
      $("#ipt_daysMargin").attr("disabled", "disabled");

      //restore the form in the center
      $("#wrapContent").removeClass("searchZone-side");
      $("#searchZone").removeAttr("data-responsive");

      //relaunch the count results
      orxapi.countResults(form);
      return false;
  }

  /*-------------------------
  -   ADVANCED SEARCH FORM   -
  -------------------------*/

  /* Click on the link to go to the advanced search form */
  $(document).on("click", ".advSearchLnk", function() {
    orxapi.selectors.tabsBarZone.find(".tab_list").data("submenu", null);
    $("#wrapSideBar").find(".btn_advSearch").first().trigger('click');
  });
  /*****************************
   *                           *
   *   END OF PRODUCT SEARCH   *
   *                           *
   *****************************/

  /* Search resultBoxes : on double click, open details */
  $("#bodyContent").on("dblclick", ".prdtBlock", function() {

    if ($(this).find("tr:first").hasClass("unpublished")) {
      // this product was unpublished after it was added to list. No dblclick.
      return false;
    }

    var id = $(this).data("id");
    if ($("#tabsBarZone").find("li[data-id='" + id + "']").length) {
      // tab for this file was already opened. Navigate to it.
      $("#tabsBarZone").find("li[data-id='" + id + "']").trigger("click");
    } else {
      // load the page and create the tab
      orxapi.openPage(id);
    }
 });


  /* Handles click on the td containing the checkbox */
  $('#bodyContent').on('click', '.td_left', function (evt) {
      var evt = evt || window.event;
      var current = evt.target || evt.srcElement;

      if(current.children[0]) {
          if($(this).context.children[0].checked) {
              $(this).context.children[0].checked = false;
          } else {
              $(this).context.children[0].checked = true;
          }
          $(this).children().trigger("change");
      }
  });

  /* Click on a CHECKBOX in the resultTable or resultBoxes -> enable the action buttons */
  $("#bodyContent").on("change", ".cbx_tr", function (evt) {

    var c = 0;
    $(".cbx_tr").each( function() {
      if ($(this).is(':checked')) {
        c += 1;
      }
    });
    //For each checkbox related btn
    $(".cbx-related-btn").each( function() {
      //If it is a drop down button, check that the drop down list isn't empty
      if ($(this).siblings(".dropdown-menu").length == 1) {
        var dropDown = $(this).siblings(".dropdown-menu");
        if (dropDown.find("li").length == 0) {
          return;
        }
      }

      if (c > 0) {
        $(this).removeClass("disabled");
      } else {
        $(this).addClass("disabled");
      }
    });
  });

  /* Hide or show min prices */
  $("#bodyContent").on("click", ".resultShortListBlock .titleZone .btn", function() {
    var resultShort = $(this).parents(".resultShortListBlock");
    var span = resultShort.parents("span");
    if (resultShort.hasClass("closed")) {
      resultShort.find(".tableList").removeClass("hidden");
      resultShort.removeClass("closed");
      $(this).addClass("active");
      $(this).removeClass("dropright");
      resultShort.find(".tableList").slideDown();
      //active
      span.find(".min_price_hidden").val("false");
    } else {
      resultShort.addClass("closed");
      $(this).removeClass("active");
      $(this).addClass("dropright");
      resultShort.find(".tableList").slideUp();
      //closed
      span.find(".min_price_hidden").val("true");
    }

    orxapi.refreshMinPricesState($("#minPriceForm").serialize());

  });

  /**********************************************************************
    FAST PRODUCT SEARCH (autocomplete)
  ***********************************************************************/
  function launchAutoComplete(that) {
    var searchedString = $.trim(that.val());
      if (searchedString.length >= 3) {
      $.ajax($.extend({}, orxapi.ajax, {
        url : "processFastProductSearch.action",
        data: {searchedString : searchedString},
        success : function(data, textStatus, jqXHR) {
          $(".autoCompleteZone").html(data);
          $("#autoCompSearch").find("li.resultSearch:eq(0)").addClass("active")
          $("body").addClass("autoCompleteOn");
        }
      }));
    }
  }

  function closeAutoComplete(withBlur) {
    $("#quickSearchZone").find("#autoCompSearch").find(".active").removeClass("active");
    eqActive = 0;
    $("#quickSearchZone").find("#autoCompSearch").remove();
    $(".ipt_searchPrincipal").off("keydown");
    if (withBlur === true) {
      $(".ipt_searchPrincipal").blur();
    }
    $("body").removeClass("autoCompleteOn");
  }

  // open fast search result in a file tab or if too many results - in a lists search
  $.fn.openSearchResult = function () {
      if ($(this).hasClass("resultSearch")) {
        var id = $(this).data("id");
        if (typeof(id) != "undefined" && id != "") {
          orxapi.openPage(id);
        }
      } else if ($(this).hasClass("advSearch")) {
        // launch advSearch (listTab) with current fastSearch string
        // For now only possible in backOffice module (not in compta)
        var formCode = $(this).data("formcode"),
            page = $(this).data("page"),
            searchedText = $(".ipt_searchPrincipal").val(),
            $listTab;

        var newCriteria = "formCode=" + formCode + "&criteria.fastSearchText=" + searchedText;
        newCriteria += "&criteria.sortBy=SORT_EXTERN_CODE_ASC";
        newCriteria += "&bannerTitle=" + $(this).data("listdesc") + " \"" + searchedText + "\"";
        newCriteria += "&forceForm=true";

        // create list tab and re-use fastSearchText input
        $listTab = $("#tabsBarZone").find("li[data-page='pge_list']");
        if (!$listTab.length) {
          // create list tab
          orxapi.addTab("pge_list")
          $listTab = $("#tabsBarZone").find("li[data-page='pge_list']");
        }
        $listTab.data("submenu", page);
        $listTab.data("lastSearch", newCriteria)
        $listTab.data("lastSearchAction", "processSearchWithAdditional");
        //Reinit the min prices
        $listTab.data("minprices", "");

        $listTab.trigger("click");
      }

      closeAutoComplete(true);
      $(".ipt_searchPrincipal").val("");
  }

  /** Delay function: launch function after X ms delay. */
  var delay = (function() {
    var timer = 0;
    return function(callback, ms) {
      clearTimeout (timer);
      timer = setTimeout(callback, ms);
    };
  })();

  $.fn.autoComp = function(options) {

    // This is the easiest way to have default options.
    var settings = $.extend({
      "posBlock": "right"
    }, options);

    var that = $(this),
        iptAutoComp = $(this).find(".ipt_searchPrincipal");

    $("#autoCompSearch").css(settings.posBlock, 0);

    // index of active(selected) result
    var eqActive = 0;

    that.on("focus", ".ipt_searchPrincipal", function() {
      launchAutoComplete(iptAutoComp);
    });

    that.on("keyup", ".ipt_searchPrincipal", function(e) {

      if (e.which == 40) {
        // event key "down"
        e.preventDefault();
        if(!$("#autoCompSearch").find('li').hasClass("active")) {
          eqActive = 0;
        } else {
          if (eqActive >= $("#autoCompSearch").find("li").length - 1) {eqActive = 0;} else {eqActive = eqActive +1;}
        }
        $("#autoCompSearch").find(".active").removeClass("active");
        $("#autoCompSearch").find("li").eq(eqActive).addClass("active");
      } else if (e.which == 38) {
        // event key "up"
        e.preventDefault();
        $("#autoCompSearch").find(".active").removeClass("active");
        if (eqActive <= 0) {eqActive = $("#autoCompSearch").find("li").length -1;} else {eqActive = eqActive -1;}
        $("#autoCompSearch").find("li").eq(eqActive).addClass("active");
      } else if (e.which == 13) {
        // event key enter
        e.preventDefault();
        if (!$("#autoCompSearch").find(".active").length) {
          // preselect 1st result
          $("#autoCompSearch").find("li:eq(0)").addClass("active");
        }
        if ($("#autoCompSearch").find("li").length) {
          $("#autoCompSearch").find(".active").openSearchResult();
        } else {
          // no results found
          $(".defSearchBar").effect("shake", 20);
        }
      } else if (e.which == 27) {
        // escape key clicked
        closeAutoComplete(true);
      } else {
        // any other key pressed: after some delay launch the search
        delay(function() {
          if (iptAutoComp.val().length < 3 && $("#autoCompSearch").length) {
            closeAutoComplete(false);
          } else {
            launchAutoComplete(iptAutoComp);
          }
        }, 500);
      }
    });

    // active only if autocomplete is shown
    $(document).on("click", "body.autoCompleteOn", function(e) {
      var that = $(this);
      var $target = $(e.target);
      if ($target.parents("#quickSearchZone").length < 1) {
        // not clicked inside the multiresultats
         closeAutoComplete(true);
      }
    });

    that.on("click", "li", function(e) {
      e.stopPropagation();
      $(this).openSearchResult();
    });
    that.on("mouseenter", "li.resultSearch", function() {
      $(this).addClass("hover");
    });
    that.on("mouseleave", "li.resultSearch", function() {
      $(this).removeClass("hover");
    });
  };

  $("#quickSearchZone").autoComp();

  /**********************************************************************
  END OF FAST PRODUCT SEARCH (autocomplete)
   ***********************************************************************/

  // handle the error messages
  $(document).ready(function() {

      // the change password error
      var changePasswordErrorKey = orx.selectors.body.data("change-password-error-key");
      if (typeof changePasswordErrorKey != "undefined" && changePasswordErrorKey != "") {
          orxapi.madePopin("ppn_changeUserPassword", { "changePasswordErrorKey": changePasswordErrorKey });
      } else {
          // handle the first connection
          var firstConnection = orx.selectors.body.data("first-connection");
          if (typeof firstConnection != "undefined" && firstConnection) {
              orxapi.madePopin("ppn_changeUserPassword", { firstConnection: true });
          }
      }
  });

  // Restore the comparator from preference cookie
  $(document).ready(function() {
    restoreComparator();
    //resizeDiaporama();
  });

  /*$('img').bind('error', function() {
      this.parentNode.removeChild(this);
  });*/

  /*function resizeDiaporama() {
    var totalDiaporamaWidth = 0;
    $('.slideshowImg').each(function() {
        totalDiaporamaWidth += $(this).outerWidth(true);
    });
    $('.insideSlide').width(totalDiaporamaWidth + 20);
  };*/

})(jQuery, orxapi);
