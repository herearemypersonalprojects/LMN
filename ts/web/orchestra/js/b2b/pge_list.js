(function($, orx){

  orxapi.createOrUpdateLastSearchList(orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch"),
      orxapi.selectors.tabsBarZone.find("li.active").data("lastSearchAction"));

  //Click on Last Search List
  $("#lastSearchList").on("click", function() {
    if ($(this).hasClass("disabled")) {
      return false;
    }

    var that = $(this);
    var pge =  that.data("page"),
    component = that.data("component"),
    code = that.data("sidebar-code"),
    shared = that.data("shared"),
    url = pge + ".action",
    criteria = that.data("criteria");
    formCode = that.data("formCode");

    var param = "code=" + code;
    param = param + "&" + criteria;
    param = param + "&noSearchZone=true";

    //get the min prices state if it exists
    var minPricesState = orxapi.selectors.tabsBarZone.find("li.active").data("minprices");
    var submenu = orxapi.selectors.tabsBarZone.find("li.active").data("submenu");
    if (typeof(minPricesState) != "undefined" && minPricesState != "" && submenu == code) {
      param += "&" + minPricesState;
    }
    //set the result mode (cartouches or table) based on the cookie
    var prefsCookie = orx.getPreferencesCookie();
    if (!$.isEmptyObject(prefsCookie)) {
      if (typeof(prefsCookie.boxSearch) != "undefined") {
        param += "&boxSearch=" + prefsCookie.boxSearch;
      }
      param=orxapi.deleteItemFromSerializedList(param,"selectedSort");
      param=orxapi.deleteItemFromSerializedList(param,"ascSort");
      // Retrieving sort information from cookies
      if (prefsCookie.selectedSort) {
          param += "&selectedSort=" + prefsCookie.selectedSort;
      }
      if (prefsCookie.ascSort) {
            param += "&ascSort=" + prefsCookie.ascSort;
      }
    }

    that.parents("#wrapSideBar").find(".active").removeClass("active");
    that.addClass("active");

    $.ajax($.extend({}, orxapi.ajax, {
      url : url,
      data : param,
      success : function(data, textStatus, jqXHR) {
        $("#wrapContent").html(data).removeClass().addClass(pge);
        orxapi.refreshResultNumberInLastSearch();
        orxapi.loadJs(orx.selectors.body.data("module"), component, shared);
        orxapi.loadJs(orx.selectors.body.data("module"), "pge_list/pge_advSearch", shared);

        // TODO change this... (used to restore the country + city)
        orxapi.restoreForm(criteria, $("#advSearchForm"));

        // update current subMenu position in tab
        orxapi.selectors.tabsBarZone.find("li.active").data("submenu", code);
        orxapi.storeTabCookie();

        //Display the serialized criteria in the url
        location.hash = '';
        location.hash = criteria;

        $("#wrapContent").scrollTop(0);
      }
    }));
    return false;
  });


  /*************************************/
  /** FAVORITE LISTS                  **/
  /*************************************/
  //Click on add new fav list
  $("#wrapSideBar").on("click", ".btn_addList", function() {
    if ($(".li_addlist").length > 1) {
      return false;
    }
    var duplicateMsg = $(this).data("duplicate");

    var itemList = $("#favoriteUser").find(".itemList");
    itemList.prepend($("#addFavListModel").html());
    itemList.find(".li_addlist").addClass("inEdit");

    var input = $("#wrapSideBar").find("li.inEdit:visible .ipt_favlist");
    var overflowClickOut = $("<div/>", { "id" : "overlayClickOut" });
    overflowClickOut.appendTo("body");

    input.focus();

    // if enter click while inEdit mode : same as clickOut
    input.on("keyup", function(e) {
      if (e.which == 13) {
        overflowClickOut.trigger("click");
      }
    });

    overflowClickOut.click(function() {

      var that = $(this);
      var url = "processCreateFavList.action";
      var listName = input.val();

      if (!validateListName(listName)) {
        var alreadyExistsDialog = $("<div/>",{ "id":"contentDialog"});
        alreadyExistsDialog.html(duplicateMsg);
        alreadyExistsDialog.dialog($.extend({}, orx.plugin.dialog, {
          buttons:{
            "Fermer": function() {
              $(this).dialog("destroy").remove();
              return false;
             }
          }
        }));
        return false;
      }

      input.closest(".item").remove();

      if (listName != null && listName != "") {
          $.ajax($.extend({}, orxapi.ajax, {
            url : url,
            data : {listName : listName},
            success : function(data, textStatus, jqXHR) {
              var itemList = $("#favoriteUser").find(".itemList");
              itemList.prepend(data);

              var dropDown = $("#favListDropDown").find(".dropdown-menu");
              $("#favListDropDown").removeClass("hidden");
              dropDown.append("<li data-listname=\"" + listName + "\" class=\"li_favlist add-fav\"><a>" + listName + "</a></li>");
            }
        }));
      }

      overflowClickOut.remove();
    });
  });

  //Delete the input to add a new favorite list
  $("#wrapSideBar").on("click", "li.inEdit .favlist_delete", function() {
    $(this).parents(".item").remove();
    $("#overlayClickOut").remove();
  });
  /*************************************/
  /** END OF FAVORITE LISTS           **/
  /*************************************/


  /* Submenu navigations */
  $("#wrapSideBar").on("click", ".item:not(#lastSearchList), .searchItem", function(event) {
    var that = $(this),
        $target = $(event.target);

    if (!$target.hasClass("icon-edit")) {

      // If we were in favSearch list - edit mode, after click no need to re-search
      if (!that.hasClass("inEdit")) {
        // search this list
        var pge =  that.data("page"),
        component = that.data("component"),
        shared = that.data("shared"),
        url = pge + ".action",
        // code is used as a code in configurable lists search or as a userSearchId
        code = that.data("sidebar-code"),
        mode = that.data("mode"),
        listName = encodeURIComponent(that.data("oldlabel"));

        var param = "code=" + code;
        param += "&mode=" + mode;
        param += "&listName=" + listName;

        //get the min prices state if it exists
        var minPricesState = orxapi.selectors.tabsBarZone.find("li.active").data("minprices");
        var submenu = orxapi.selectors.tabsBarZone.find("li.active").data("submenu");
        if (typeof(minPricesState) != "undefined" && minPricesState != "" && submenu == code) {
          param += "&" + minPricesState;
        }
        //set the result mode (cartouches or table) based on the cookie
        var prefsCookie = orx.getPreferencesCookie();
        if (!$.isEmptyObject(prefsCookie)) {
          if (typeof(prefsCookie.boxSearch) != "undefined") {
            param += "&boxSearch=" + prefsCookie.boxSearch;
          }
          // Retrieving sort information from cookies
          if (prefsCookie.selectedSort) {
              param += "&selectedSort=" + prefsCookie.selectedSort;
          }
          if (prefsCookie.ascSort) {
                param += "&ascSort=" + prefsCookie.ascSort;
          }
        }

        that.parents("#wrapSideBar").find(".active").removeClass("active");
        if (that.hasClass("searchItem")) {
          // click on advanced search
          that.find("span.btn_advSearch").addClass("active");
        }
        that.addClass("active");

        $.ajax($.extend({}, orxapi.ajax, {
          url : url,
          data : param,
          success : function(data, textStatus, jqXHR) {
            $("#wrapContent").html(data).removeClass().addClass(pge);
            orxapi.loadJs(orx.selectors.body.data("module"), component, shared);

            // update current subMenu position in tab
            orxapi.selectors.tabsBarZone.find("li.active").data("submenu", code);
            orxapi.storeTabCookie();

            $("#wrapContent").scrollTop(0);
          }
        }));
      }

    } else {
        // go to edit mode
         var overflowClickOut = $("<div/>", { "id" : "overlayClickOut" }),
             btn_remove = $("<div/>", { "class" : "icon-trash btn_remove" }),
             userSearchId = that.data("sidebar-code"),
             listName = that.data("oldlabel");

         that.addClass("inEdit")
             .removeClass("editable")
             .find(".txt").before(btn_remove).end()
             .find(".txt").addClass("hidden").end()
             .find("input").removeClass("hidden").focus().end()
             .find("input").select();

         overflowClickOut.appendTo("body");

         // if enter click while inEdit mode : same as clickOut
         that.on("keyup", "input", function(e) {
           if (e.which == 13) {
             overflowClickOut.trigger("click");
           }
         });

         overflowClickOut.click(function() {
           var oldLabel = that.data("oldlabel"),
               newLabel = that.find("input").val();

           // rename this favorite list
           if (oldLabel !== newLabel && newLabel != "") {

             if (!validateListName(newLabel)) {
               var duplicateMsg = $(".btn_addList").data("duplicate");
               var alreadyExistsDialog = $("<div/>",{ "id":"contentDialog"});
               alreadyExistsDialog.html(duplicateMsg);
               alreadyExistsDialog.dialog($.extend({}, orx.plugin.dialog, {
                 buttons:{
                   "Fermer": function() {
                     $(this).dialog("destroy").remove();
                     return false;
                    }
                 }
               }));

               return false;
             }

             // label changed. rename it in db
             $.ajax($.extend({}, orxapi.ajax, {
               url : "processRenameFavList.action",
               data : {listName:oldLabel, newName:newLabel},
               success : function(data, textStatus, jqXHR) {
                 var txtSpan =  that.find("span.lbl span.txt");
                 if (data != "") {
                   //if data is not null, it contains the new name
                   txtSpan.text(data);
                   that.removeAttr("data-oldlabel");
                   that.attr("data-oldlabel", data);
                   that.data("oldlabel", data);
                   that.data("sidebar-code", data);
                 } else {
                   txtSpan.text("-");
                 }
               }
             }));
           }

           overflowClickOut.remove();
           that.addClass("editable")
               .find(".btn_remove").remove().end()
               .find(".txt").removeClass("hidden").end()
               .find("input").blur().end()
               .find("input").addClass("hidden").end();
           $("#favoriteUser").find("li.inEdit").removeClass("inEdit");
           return false;
         });

         btn_remove.on("click", function() {
           if ($(this).closest(".item").find(".nbre").text() == 0) {
             // delete this favorite list
             deleteFavList(listName, that, overflowClickOut);
           } else {
             //Display the "do you really want to delete" dialog
             var contentDialog = $("<div/>",{ "id":"contentDialog"});
             contentDialog.html($("#favoriteUser").find("ul.itemList").data("deletemsg"));
             contentDialog.dialog($.extend({}, orx.plugin.dialog, {
               buttons:{
                 "Fermer": function() {
                   $(this).dialog("destroy").remove();
                   return false;
                  },
                  "OK": function() {
                    $(this).dialog("close");
                    // delete this favorite list
                    deleteFavList(listName, that, overflowClickOut);
                  }
               }
             }));
           }
         });

         function deleteFavList(listName, that, overflowClickOut) {
           $.ajax($.extend({}, orxapi.ajax, {
             url : "processDeleteFavList.action",
             data : {listName: listName},
             success : function(data, textStatus, jqXHR) {
               that.remove();
               overflowClickOut.remove();

               if ($("#listNameDiv").text() == listName) {
                 //Where were displaying this list, we have to display a new message
                 $("#innerWrapContent").children(":visible").remove();
                 $("#noResultAlert").removeClass("hidden");
               } else {
                 //Remove the list name from the drop down box in the action bar
                 var dropDown = $("#favListDropDown").find(".dropdown-menu");
                 dropDown.find("li[data-listname=\"" + listName + "\"]").remove();

                 //Hide the button is there's no list anymore
                 if (dropDown.find("li").length == 0) {
                   $("#favListDropDown").find("button").addClass("disabled");
                 }
               }
             }
           }));
         };

    }
    return false;
  });

  //Check whether the list name is free or already used
  function validateListName(listName) {
    var nameExists = false;
    $("#favoriteUser").find(".item").each( function() {
      if ($(this).data("oldlabel") == listName) {
        nameExists = true;
      }
    });
    return !nameExists;
  }


  /* open previously opened submenu - if not open the adv search page */
  if (orxapi.selectors.tabsBarZone.find(".tab_list.active").length && orxapi.selectors.tabsBarZone.find(".tab_list.active").data("submenu") != null) {
      var submenuToOpen = orxapi.selectors.tabsBarZone.find(".tab_list.active").data("submenu");
      if ($("#wrapSideBar").find("li.item[data-sidebar-code='" + submenuToOpen + "']").length > 0) {
        // list search was opened as submenu
        $("#wrapSideBar").find("li.item[data-sidebar-code='" + submenuToOpen + "']").trigger('click');
      } else {
        // advanced search was last opened. Was it distributor or producer search?
        if ($("h3[data-sidebar-code='" + submenuToOpen + "']").length > 0) {
          $("h3[data-sidebar-code='" + submenuToOpen + "']").trigger('click');
        } else {
          $("#wrapSideBar").find(".searchItem").first().trigger('click');
        }
      }
  } else {
    // open the preconfigured default list
    var defaultActiveCode = $("#wrapSideBar").data("defaultlistcode");
    if (defaultActiveCode != "") {
      $("#wrapSideBar").find("[data-sidebar-code='" + defaultActiveCode + "']").trigger('click');
    } else {
      // 1st existing list
      $("#wrapSideBar").find(".btn_advSearch").first().trigger('click');
    }
  }

  /* Resize height content for scroll  */
  $.fn.resizeContentTab = function () {
    var windowHeight = $(window).height(),
        contentTabHeight = $(window).height() - $("#headerBarZone").height() - orxapi.selectors.tabsBarZone.height() - ($("#supHeaderBarZone").length ? $("#supHeaderBarZone").height() : 0) - 3;
    $(this).css("height", contentTabHeight);
  };
  $(window).resize(function(){
    $("#pge_list").find("#wrapContent").resizeContentTab();
    $("#pge_list").find("#wrapSideBar").resizeContentTab();
  });
  $("#pge_list").find("#wrapContent").resizeContentTab();
  $("#pge_list").find("#wrapSideBar").resizeContentTab();

})(jQuery, orxapi);
