
(function($, orx) {

  $(".resultShortListBlock.closed .tableList").hide();
  $(".resultShortListBlock:not(.closed) .titleZone .btn").addClass("active");

  //********************//
  // Sorts 				//
  //********************//
  //Sort when using the result table
  $("#innerWrapContent").on("click", "th.th_sortable", function() {
    var that = $(this),
        activeItem = $("#wrapSideBar .item.active, #wrapSideBar .searchItem.active"),
        pge =  "processSearchOnlyResults",
        url = pge + ".action",
        data = {},
        // code is used as a code in configurable lists search or as a userSearchId
        code = activeItem.data("sidebar-code"),
        columnCode = that.data("header-code"),
        ascOrder = !that.hasClass("th_headerSortUp");

    var params = orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch");
    params += "&code=" + activeItem.data("sidebar-code");
    params += "&boxSearch=false";

    params += "&columnsToSort[1].code=" + columnCode;
    params += "&columnsToSort[1].ascOrder=" + ascOrder;

    $.ajax($.extend({}, orxapi.ajax, {
      url : url,
      data : params,
      success : function(data, textStatus, jqXHR) {
        $("#resultTable").html(data);

        //$.getScript("./shared/ts/web/js/b2b/pge_list/pge_resultList.js");
      }
    }));
  });

  //Sort when using the result boxes
  $("#innerWrapContent").on("change", "#slt_sortBy", function() {
    var activeItem = $("#wrapSideBar .item.active, #wrapSideBar .searchItem.active"),
      pge =  "processSearchOnlyResults",
      url = pge + ".action";

    var params = orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch");
    params=orxapi.deleteItemFromSerializedList(params,"selectedSort");
    params=orxapi.deleteItemFromSerializedList(params,"ascSort");

    params += "&code=" + activeItem.data("sidebar-code");
    params += "&selectedSort=" + $(this).val();
    params += "&ascSort=" + ($(".btn_sortBy").find("i").data("asc") == true);
    params += "&listName=" + activeItem.data("oldlabel");
    orxapi.updateSortCookie ($(this).val(),($(".btn_sortBy").find("i").data("asc") == true)+"");

    $.ajax($.extend({}, orxapi.ajax, {
        url : url,
        data : params,
        success : function(data, textStatus, jqXHR) {          $("#resultTable").html(data);
        }
      }));
  });

  $("#innerWrapContent").on("click", ".btn_sortBy", function() {
    if ($(this).hasClass("disabled")) {
      return;
    }

    var activeItem = $("#wrapSideBar .item.active, #wrapSideBar .searchItem.active"),
      pge =  "processSearchOnlyResults",
      url = pge + ".action";

    var params = orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch");
    params=orxapi.deleteItemFromSerializedList(params,"selectedSort");
    params=orxapi.deleteItemFromSerializedList(params,"ascSort");
    params += "&code=" + activeItem.data("sidebar-code");
    params += "&selectedSort=" + $("#slt_sortBy").val();
    params += "&ascSort=" + !($(this).find("i").data("asc") == true);
    params += "&listName=" + activeItem.data("oldlabel");
    orxapi.updateSortCookie ($("#slt_sortBy").val(),!($(this).find("i").data("asc") == true)+"");

    $.ajax($.extend({}, orxapi.ajax, {
        url : url,
        data : params,
        success : function(data, textStatus, jqXHR) {
          $("#resultTable").html(data);
        }
    }));
  });
  //********************//
  // End of Sorts       //
  //********************//


  //*************************************************//
  // Switch the result list display (table or boxes) //
  //*************************************************//
  $("#innerWrapContent").on("click", ".btn_viewModeList", function() {
    if (!$(this).hasClass("active")) {
      changeResultTableMode($(this), false);
    }
  });

  $("#innerWrapContent").on("click", ".btn_viewModeLarge ", function() {
    if (!$(this).hasClass("active")) {
      changeResultTableMode($(this), true);
    }
  });

  //Used when changing the style of display of the result table (Boxes or table)
  function changeResultTableMode(that, boxSearch) {
    var activeItem = $("#wrapSideBar .item.active, #wrapSideBar .searchItem.active"),
      pge =  "processSearchOnlyResults",
      url = pge + ".action";

    var serializedSearch = orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch");
    var params = orxapi.deleteItemFromSerializedList(serializedSearch, "boxSearch");
    params += "&boxSearch=" + boxSearch;

    $.ajax($.extend({}, orxapi.ajax, {
        url : url,
        data : params,
        success : function(data, textStatus, jqXHR) {          $("#resultTable").html(data);

          orxapi.updatePreferencesCookie({boxSearch : boxSearch});

          //$.getScript("./shared/ts/web/js/b2b/pge_list/pge_resultList.js");
        }
      }));
  }
  //********************************************************//
  // End of Switch the result list display (table or boxes) //
  //********************************************************//

  //*********************//
  // Result list actions //
  //*********************//
  $("#innerWrapContent").on("click", ".add-to-comparator", function() {
    // Get the ids of checked products
    var ids = "";
    $("#innerWrapContent tr").each(function( index ) {
      if ($(this).find(".cbx_tr").is(':checked')) {
        if( ids == "") {
          ids = $(this).data("id");
        } else {
          ids = ids + "," + $(this).data("id");
        }
      }
    });

    orxapi.addProductToComparator(ids);
    orxapi.unselectProducts();
  });

//  //When using the select boxes
//  $("#innerWrapContent").on("click", ".actionsBar .dropdown-action", function() {
//	  // Get the ids of checked products
//	  var ids = "";
//	  $("#innerWrapContent tr").each(function( index ) {
//		if ($(this).find(".cbx_tr").is(':checked')) {
//			if( ids == "") {
//				ids = $(this).data("id");
//			} else {
//				ids = ids + "," + $(this).data("id");
//			}
//		}
//	  });
//
//	  var url = $(this).data("page") + ".action";
//	  executeAction(url, ids);
//  });
//
//  //When applying an action on a single result
//  $("#innerWrapContent").on("click", ".prdtBlock .dropdown-action", function() {
//	  // Get the id of current product
//	  var ids = $(this).parents("tr").data("id");
//	  var url = $(this).data("page") + ".action";
//	  executeAction(url, ids);
//  });
//
//  //Execute the desired action on the desired products
//  function executeAction(url, ids) {
//	  $.ajax($.extend({}, orxapi.ajax, {
//	      url : {ids : ids},
//	      data : params,
//	      success : function(data, textStatus, jqXHR) {}
//	  }));
//  }
  //****************************//
  // End of result list actions //
  //****************************//

//*********************//
  // Click on min price  //
  //*********************//
  $(".resultShortListBlock.closed .tableList").hide();
  $(".resultShortListBlock:not(.closed) .titleZone .btn").addClass("active");

  //Click on a min price element
  $("#innerWrapContent").on("click", ".tr_minprice", function() {
    searchWithMinPrice($(this), true);
  });

  // Click on an element of the path
  $("#innerWrapContent").on("click", ".min_price_path", function() {
    searchWithMinPrice($(this), false);
  });

  function searchWithMinPrice(that, incrementLevel) {
    var activeItem = $("#wrapSideBar .item.active, #wrapSideBar .searchItem.active"),
    url = activeItem.data("page") + ".action";

    var span = that.parents("span");
    var newValue = that.data("value");

    if (incrementLevel) {
      span.find(".min_price_level").val( parseInt(that.data("level")) + 1 );
    } else {
      span.find(".min_price_level").val( parseInt(that.data("level")) );
    }
    span.find(".min_price_value").val( newValue );
    span.find(".min_price_hierarchy").val( newValue );
    setOpenStatuses();

    var minPricesState = $("#minPriceForm").serialize();
    var oldSearch = orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch");
    var newSearch = "";


    var criterionToAdd = null;
    if (that.data("criterion") == "TO_EXT_CODE") {
      criterionToAdd = "TOExtCode";
    } else {
      criterionToAdd = "cameleoCriterion%5B'" + that.data("criterion") + "'%5D";
    }

    //Remove the criterion from the serialized criteria
    newSearch = orxapi.deleteItemFromSerializedList(oldSearch, criterionToAdd);
    //If the new value isn't null, re add it with the new value
    if (newValue != "") {
      if (newSearch != "") {
        newSearch += "&" + criterionToAdd + "=" + newValue;
      } else {
        newSearch = newValue;
      }
    }

    orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch", newSearch);
    $("#lastSearchList").data("criteria", newSearch);


    var data = minPricesState + "&" + newSearch;
    data += "&code="  + activeItem.data("sidebar-code");
    //set the result mode (cartouches or table) based on the cookie
    var prefsCookie = orx.getPreferencesCookie();
    if (!$.isEmptyObject(prefsCookie)) {
      if (typeof(prefsCookie.boxSearch) != "undefined") {
        data += "&boxSearch=" + prefsCookie.boxSearch;
      }
      // Retrieving sort information from cookies
     if (prefsCookie.selectedSort) {
          param += "&selectedSort=" + prefsCookie.selectedSort;
      }
      if (prefsCookie.ascSort) {
            param += "&ascSort=" + prefsCookie.ascSort;
      }

    }

    orxapi.refreshMinPricesState(minPricesState);

    $.ajax($.extend({}, orxapi.ajax, {
        url : url,
        data : data,
        success : function(data, textStatus, jqXHR) {
         $("#wrapContent").html(data);

         //orxapi.resetForm($("#wrapContent form.searchForm"), false);
         orxapi.restoreForm(newSearch, $("#advSearchForm"));
         //set the resultNumber
         orxapi.refreshResultNumberInLastSearch();

     $.getScript("./shared/ts/web/js/b2b/pge_list/pge_advSearch.js");
         $.getScript("./shared/ts/web/js/b2b/pge_list/pge_resultList.js");
        }
    }));
  }

  function setOpenStatuses() {
    $(".resultShortListBlock").each( function() {
      var span = $(this).parents("span");
      if ($(this).hasClass("closed")) {
        span.find(".min_price_hidden").val("true");
      } else {
        span.find(".min_price_hidden").val("false");
      }
    });
  }
  //****************************//
  // End of Click on min price  //
  //****************************//

})(jQuery, orxapi);


$(document).ready(function() {
  $("img").each(function(index) {
    if ($(this).parents(".td_imgZone").length == 1) {
    // result product picto
      $(this).error(function() {
        this.src = "/b2b/shared/ts/web/images/missing_img.gif";
      });
      $(this).attr("src", $(this).attr("src"));
    }
  });
});
