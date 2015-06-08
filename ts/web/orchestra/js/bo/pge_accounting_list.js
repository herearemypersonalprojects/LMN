(function($){

  /* Submenu navigations */
  $("#wrapSideBar").on("click", ".item, .searchItem", function() {
    var that = $(this),
        pge =  that.data("page"),
        url = pge + ".action";
        code = that.data("sidebar-code"),
        entity = that.data("sidebar-entity");

    that.parents("#wrapSideBar").find(".active").removeClass("active");
    that.addClass("active");

    $.ajax($.extend({}, orxapi.ajax, {
      url : url,
      data : {code: code, entity:entity},
      success : function(data, textStatus, jqXHR) {
        $("#wrapContent").html(data).removeClass().addClass(pge);
        $.getScript("./shared/ts/web/js/bo/pge_list/" + pge + ".js");

        // update current subMenu position in tab
        orxapi.selectors.tabsBarZone.find("li.active").data("submenu", code);
        orxapi.selectors.tabsBarZone.find("li.active").data("submenuentity", entity);
        orxapi.storeTabCookie();
      }
    }));
  });


  /* open previously opened submenu - that is submenu with the same code and entity. if not open the adv search page */
  if (orxapi.selectors.tabsBarZone.find(".tab_list.active").length
      && orxapi.selectors.tabsBarZone.find(".tab_list.active").data("submenu") != null
      && orxapi.selectors.tabsBarZone.find(".tab_list.active").data("submenuentity") != null) {
      var submenuCodeToOpen = orxapi.selectors.tabsBarZone.find(".tab_list.active").data("submenu");
      var submenuEntityToOpen = orxapi.selectors.tabsBarZone.find(".tab_list.active").data("submenuentity");
      if ($("#wrapSideBar").find("li.item[data-sidebar-code='" + submenuCodeToOpen + "']").length
        && $("#wrapSideBar").find("li.item[data-sidebar-entity='" + submenuEntityToOpen + "']").length) {
        // list search was opened as submenu
        $("#wrapSideBar").find("li.item[data-sidebar-code='" + submenuCodeToOpen + "'][data-sidebar-entity='" + submenuEntityToOpen + "']").trigger('click');
      } else {
        $("#wrapSideBar").find(".searchItem").first().trigger('click');
      }
  } else {
      // open the 1st submenu
      $("#wrapSideBar").find("li.item").first().trigger('click');
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


})(jQuery);
