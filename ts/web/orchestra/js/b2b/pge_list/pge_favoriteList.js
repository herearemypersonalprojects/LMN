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
        data["listName"] = activeItem.data("oldlabel");
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
      data["listName"] = activeItem.data("oldlabel");

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
      data["listName"] = activeItem.data("oldlabel");
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
      data["listName"] = activeItem.data("oldlabel");

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
          //Check that the product is published
          if (!$(this).hasClass("unpublished")) {
            if( ids == "") {
              ids = $(this).data("id");
            } else {
              ids = ids + "," + $(this).data("id");
            }
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

    //**********************//
    // Remove from fav list //
    //**********************//
    //When using the select boxes
    $("#innerWrapContent").on("click", ".actionsBar .remove-fav", function() {
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

      var activeItem = $("#wrapSideBar .item.active, #wrapSideBar .searchItem.active");
      var listName = activeItem.data("oldlabel");
      removeFromFav(ids, listName);
    });

    //Execute the desired action on the desired products
    function removeFromFav(ids, listName) {
      if (ids == "") {
        return;
      }

      $.ajax($.extend({}, orxapi.ajax, {
          url : "processRemoveFromFavorite.action",
          data : {ids : ids, listName : listName},
          success : function(data, textStatus, jqXHR) {
            if (data != null) {
              //Change the displayed number of product of the list
              var nbOfProducts = parseInt(data);
              var li = $("#favoriteUser").find("li[data-oldLabel='" + listName + "']");
              var oldNbr = li.find(".nbre").text();
              var newNbr = parseInt(oldNbr) - nbOfProducts;
              li.find(".nbre").text(newNbr);

              if ($("#innerWrapContent").find("#ul_resultList").length > 0) {
                  //IN BOX MODE
                  //Remove the boxes of the products
                $("#innerWrapContent").find(".prdtBlock").each(function( index ) {
                if ($(this).find(".cbx_tr").is(':checked')) {
                  $(this).remove();
                }
                });

                //If all products have been removed, display the blue message
                if ($("#innerWrapContent").find(".prdtBlock").length == 0) {
                  $("#innerWrapContent").find(".actionsBar").remove();
                  $("#innerWrapContent").find("#noResultAlert").removeClass("hidden");
                }
              } else if ($("#innerWrapContent").find("#tbe_resultList").length > 0) {
                //IN RESULT TABLE MODE
                //Remove the lines of the products
                var body = $("#tbe_resultList").find("tbody");
                body.find("tr").each(function( index ) {
                if ($(this).find(".cbx_tr").is(':checked')) {
                  $(this).remove();
                }
                });

                //If all products have been removed, display the blue message
                if (body.find("tr").length == 0) {
                  $("#innerWrapContent").find(".actionsBar").remove();
                  $("#innerWrapContent").find("#tbe_resultList").remove();
                  $("#innerWrapContent").find("#noResultAlert").removeClass("hidden");
                }
              }

              $(".cbx-related-btn").addClass("disabled");
            }
          }
      }));
    }
    //*****************************//
    // End of remove from fav list //
    //*****************************//
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
