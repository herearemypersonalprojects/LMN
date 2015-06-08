(function($){

  /* Submenu navigation */
  $("#wrapSideBar").on("click", ".item", function() {
    var that = $(this),
        pge =  that.data("page"),
        url = pge + ".action";

    var id = orxapi.selectors.bodyContent.find("div:eq(0)").data("id");

    that.parents("#wrapSideBar").find(".active").removeClass("active");
    that.addClass("active");

    $.ajax($.extend({}, orxapi.ajax, {
      url : url,
      data: {id : id},
      success : function(data, textStatus, jqXHR) {
        $("#wrapContent").html(data).removeClass().addClass(pge);
        $.getScript("shared/ts/web/js/bo/pge_file/" + pge + ".js");
        orxapi.selectors.tabsBarZone.find("li.active").data("submenu", pge);
        orxapi.storeTabCookie();
      }
    }));


  });

  /** Product indisponible: devis */
  $(".prodIndisponible").click(function(evt) {
    return false;
  });

  /* open previously opened submenu - if not open the default (first) one */
  if (orxapi.selectors.tabsBarZone.find(".tab_file.active").length && orxapi.selectors.tabsBarZone.find(".tab_file.active").data("submenu") != null) {
      var submenuToOpen = orxapi.selectors.tabsBarZone.find(".tab_file.active").data("submenu");
      $("#wrapSideBar").find("li.item[data-page='" + submenuToOpen + "']").trigger('click');
  } else {
      // open the 1st submenu
      $("#wrapSideBar").find("li.item").first().trigger('click');
  }

  /* Resize height content for scroll  */
  $.fn.resizeContentTab = function () {
    var windowHeight = $(window).height(),
        contentTabHeight = $(window).height() - $("#headerBarZone").height() - orxapi.selectors.tabsBarZone.height() - $("#headerPage").height() - ($("#supHeaderBarZone").length ? $("#supHeaderBarZone").height() : 0);
    $(this).css("height", contentTabHeight);
  };
  $(window).resize(function(){
    $("#pge_file").find("#wrapContent").resizeContentTab();
  });

})(jQuery);
