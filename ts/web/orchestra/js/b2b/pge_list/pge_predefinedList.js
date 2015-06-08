(function($, orx) {

    //*******//
    // Sorts //
    //*******//
    //Sort when using the result table
    $("#tbe_resultList").on("click", "th.th_sortable", function() {
        var that = $(this),
            activeItem = $("#wrapSideBar .item.active, #wrapSideBar .searchItem.active"),
            pge =  activeItem.data("page"),
            url = pge + ".action",
            data = {},
            // code is used as a code in configurable lists search or as a userSearchId
            code = activeItem.data("sidebar-code"),
            columnCode = that.data("header-code"),
            ascOrder = !that.hasClass("th_headerSortUp");

        data["code"] = code;
        data["boxSearch"] = false;

        if ($("#wrapContent form.searchForm").length == 0) {
          // no-form case (list is displayed directly when a config list or favorite list is clicked)
          data["columnsToSort[1].code"] = columnCode;
          data["columnsToSort[1].ascOrder"] = ascOrder;
        }

        $.ajax($.extend({}, orxapi.ajax, {
          url : url,
          data : data,
          success : function(data, textStatus, jqXHR) {
            $("#wrapContent").html(data).removeClass().addClass(pge);

            var searchForm = $("#wrapContent form.searchForm");
            if (searchForm.length > 0) {
              var item = orxapi.selectors.tabsBarZone.find("li.active");
              searchForm.deserialize(item.data("lastSearch"));
              //orxapi.completeSearchForm(item.data("lastSearch"));

              searchForm.find("input[type='hidden'].ipt_columnsToSort").remove();
              $("<input>").attr({"type": "hidden",
                  "class": "ipt_columnsToSort",
                  "name": "columnsToSort[1].code",
                  "value": columnCode
              }).appendTo(searchForm);

              $("<input>").attr({"type": "hidden",
                  "class": "ipt_columnsToSort",
                  "name": "columnsToSort[1].ascOrder",
                  "value": ascOrder
              }).appendTo(searchForm);

              item.data("lastSearch", searchForm.serialize());
            }

            $.getScript("./shared/ts/web/js/b2b/pge_list/" + pge + ".js");
          }
        }));
      });

    //Sort when using the result boxes
    $("#innerWrapContent").on("change", "#slt_sortBy", function() {
      var activeItem = $("#wrapSideBar .item.active, #wrapSideBar .searchItem.active"),
        pge =  activeItem.data("page"),
        url = pge + ".action",
        data = {};

      data["code"] = activeItem.data("sidebar-code");
      data["selectedSort"] = $(this).val();
      data["ascSort"] = ($(".btn_sortBy").find("i").data("asc") == true) + "";
      orxapi.updateSortCookie (data["selectedSort"],data["ascSort"]);

      $.ajax($.extend({}, orxapi.ajax, {
          url : url,
          data : data,
          success : function(data, textStatus, jqXHR) {
            $("#wrapContent").html(data).removeClass().addClass(pge);

           $.getScript("./shared/ts/web/js/b2b/pge_list/" + pge + ".js");
          }
        }));
    });


    $("#innerWrapContent").on("click", ".btn_sortBy", function() {
      if ($(this).hasClass("disabled")) {
        return;
      }

      var activeItem = $("#wrapSideBar .item.active, #wrapSideBar .searchItem.active"),
      pge =  activeItem.data("page"),
      url = pge + ".action",
      data = {};

      data["code"] = activeItem.data("sidebar-code");
      data["selectedSort"] = $("#slt_sortBy").val();
      data["ascSort"] = !($(this).find("i").data("asc") == true) + "";
      orxapi.updateSortCookie (data["selectedSort"],data["ascSort"]);

      $.ajax($.extend({}, orxapi.ajax, {
          url : url,
          data : data,
          success : function(data, textStatus, jqXHR) {
            $("#wrapContent").html(data).removeClass().addClass(pge);

           $.getScript("./shared/ts/web/js/b2b/pge_list/" + pge + ".js");
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
        pge =  activeItem.data("page"),
        url = pge + ".action",
        data = {};

      data["code"] = activeItem.data("sidebar-code");
      data["boxSearch"] = boxSearch;

      $.ajax($.extend({}, orxapi.ajax, {
          url : url,
          data : data,
          success : function(data, textStatus, jqXHR) {
            $("#wrapContent").html(data).removeClass().addClass(pge);
            orxapi.updatePreferencesCookie({boxSearch : boxSearch});

           $.getScript("./shared/ts/web/js/b2b/pge_list/" + pge + ".js");
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
//	  //When using the select boxes
//	  $("#innerWrapContent").on("click", ".actionsBar .dropdown-action", function() {
//		  // Get the ids of checked products
//		  var ids = "";
//		  $("#innerWrapContent tr").each(function( index ) {
//			if ($(this).find(".cbx_tr").is(':checked')) {
//				if( ids == "") {
//					ids = $(this).data("id");
//				} else {
//					ids = ids + "," + $(this).data("id");
//				}
//			}
//		  });
//
//		  var url = $(this).data("page") + ".action";
//		  executeAction(url, ids);
//	  });
//
//	  //When applying an action on a single result
//	  $("#innerWrapContent").on("click", ".prdtBlock .dropdown-action", function() {
//		  // Get the id of current product
//		  var ids = $(this).parents("tr").data("id");
//		  var url = $(this).data("page") + ".action";
//		  executeAction(url, ids);
//	  });
//
//	  //Execute the desired action on the desired products
//	  function executeAction(url, ids) {
//		  var params = "ids=" + ids;
//
//		  $.ajax($.extend({}, orxapi.ajax, {
//		      url : url,
//		      data : params,
//		      success : function(data, textStatus, jqXHR) {}
//		  }));
//	  }
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
      pge =  activeItem.data("page"),
      url = pge + ".action";

      var span = that.parents("span");

      if (incrementLevel) {
        span.find(".min_price_level").val( parseInt(that.data("level")) + 1 );
      } else {
        span.find(".min_price_level").val( parseInt(that.data("level")) );
      }
      span.find(".min_price_value").val( that.data("value") );
      span.find(".min_price_hierarchy").val( that.data("value") );
      //setOpenStatuses();

      var minPricesState = $("#minPriceForm").serialize();
      var data = minPricesState + "&code="  + activeItem.data("sidebar-code");
      //set the result mode (cartouches or table) based on the cookie
      var prefsCookie = orx.getPreferencesCookie();
      if (!$.isEmptyObject(prefsCookie)) {
        if (typeof(prefsCookie.boxSearch) != "undefined") {
          data += "&boxSearch=" + prefsCookie.boxSearch;
        }
      }
      orxapi.refreshMinPricesState(minPricesState);

      $.ajax($.extend({}, orxapi.ajax, {
          url : url,
          data : data,
          success : function(data, textStatus, jqXHR) {
            $("#wrapContent").html(data).removeClass().addClass(pge);

           $.getScript("./shared/ts/web/js/b2b/pge_list/" + pge + ".js");
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

