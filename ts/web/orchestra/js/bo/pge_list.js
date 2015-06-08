(function($, orx){

  /* Submenu navigations */
  $("#wrapSideBar").on("click", ".item, .searchItem", function(event) {
    var that = $(this),
        $target = $(event.target);


    if (!$target.hasClass("icon-edit")) {

      // If we were in favSearch list - edit mode, after click no need to re-search
      if (!that.hasClass("inEdit")) {
        var pge =  that.data("page"),
        url = pge + ".action",
        // code is used as a code in configurable lists search or as a userSearchId
        code = that.data("sidebar-code"),
        mode = that.data("mode");
        that.parents("#wrapSideBar").find(".active").removeClass("active");
        that.addClass("active");

        $.ajax($.extend({}, orxapi.ajax, {
          url : url,
          data : {code: code, mode : mode},
          success : function(data, textStatus, jqXHR) {
            $("#wrapContent").html(data).removeClass().addClass(pge);
            $.getScript("./shared/ts/web/js/bo/pge_list/" + pge + ".js");

            // update current subMenu position in tab
            orxapi.selectors.tabsBarZone.find("li.active").data("submenu", code);
            orxapi.storeTabCookie();
          }
        }));
      }

    } else {
        // go to edit mode
        var overflowClickOut = $("<div/>", { "id" : "overlayClickOut" }),
            btn_remove = $("<div/>", { "class" : "icon-trash btn_remove" }),
            userSearchId = that.data("sidebar-code");

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

          $("#favoriteUser").find("li.inEdit").removeClass("inEdit");

          // rename this favorite list
          if (oldLabel !== newLabel) {
            // label changed. rename it in db
            $.ajax($.extend({}, orxapi.ajax, {
              url : "processRenameUserSearch.action",
              data : {code:userSearchId, label:newLabel},
              success : function(data, textStatus, jqXHR) {
                var txtSpan =  that.find("span.lbl span.txt");
                data == "" ? txtSpan.text("-") : txtSpan.text(data);
              }
            }));
          }

          overflowClickOut.remove();
          that.addClass("editable")
              .find(".btn_remove").remove().end()
              .find(".txt").removeClass("hidden").end()
              .find("input").blur().end()
              .find("input").addClass("hidden");
          return false;
        });

        btn_remove.on("click", function() {
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
                     $.ajax($.extend({}, orxapi.ajax, {
                       url : "processDeleteUserSearch.action",
                       data : {code: userSearchId},
                       success : function(data, textStatus, jqXHR) {
                         that.remove();
                         overflowClickOut.remove();
                       }
                     }));
                   }
                }
          }));
        });
    }

  });


  /* open previously opened submenu - if not open the preconfigured default list */
  if (orxapi.selectors.tabsBarZone.find(".tab_list.active").length && orxapi.selectors.tabsBarZone.find(".tab_list.active").data("submenu") != null) {
      var submenuToOpen = orxapi.selectors.tabsBarZone.find(".tab_list.active").data("submenu");
      if ($("#wrapSideBar").find("li.item[data-sidebar-code='" + submenuToOpen + "']").length) {
        // list search was opened as submenu
        $("#wrapSideBar").find("li.item[data-sidebar-code='" + submenuToOpen + "']").trigger('click');
      } else {
        // advanced search was last opened. Was it distributor or producer search?
        if ($("h3[data-sidebar-code='" + submenuToOpen + "']").length) {
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
      $("#wrapSideBar").find("li.item").first().trigger('click');
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
