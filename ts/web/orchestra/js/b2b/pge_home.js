(function($, orx){

  if (typeof($(".cpt_searchBlock")) != "undefined") {
    $.getScript("./shared/ts/web/js/b2b/pge_list/pge_advSearch.js");
  }

  /* Resize height content for scroll  */
  $.fn.resizeContentTab = function () {
    var windowHeight = $(window).height(),
        contentTabHeight = $(window).height() - $("#headerBarZone").height() - $("#tabsBarZone").height() - ($("#supHeaderBarZone").length ? $("#supHeaderBarZone").height() : 0)  - 3;
    $(this).css("height", contentTabHeight);
  };
  $(window).resize(function(){
    $("#pge_home").find("#wrapContent").resizeContentTab();
  });
  $("#pge_home").find("#wrapContent").resizeContentTab();


  $(".cpt_listBlock.accordionType .cpt_inner").each(function(){
    var dataHeightStyle = $(this).parents(".accordionType").data("heightstyle");
    $(this).accordion({
      heightStyle: dataHeightStyle
    }).parents("[class*='spread']").css("overflow", "visible");
  });


  /** Click on a link with a PRODUCT ID. **/
  $("#pge_home").on("click", ".id_link", function() {
    var id = $(this).data("id");
    var prefs = $(this).data("prefs");

    if (typeof(prefs) == "undefined" || prefs == "") {
      // load the page and create the tab
      orxapi.openPage(id);
    } else {
      //If prefs exists, create the tab and fill the preferred avail
      var toProductCode = $(this).data("toproductcode");
      //Create the product tab and fills it's data + simulate click
      orxapi.updateProductTabData(id, prefs, toProductCode);
    }
  });

  /** Click on a link with a SEARCH CRITERIA. **/
  $("#pge_home").on("click", ".search_link", function() {
    var criteria = $(this).data("search");
    criteria = encodeURI(criteria);
    var bannerTitle = $(this).data("tcname");
    var tcCode = $(this).data("listdesctc");

    //prevent to being able to switch form
    criteria = criteria + "&forceForm=true";

    //set the banner title
    if (typeof(bannerTitle) != "undefined" && bannerTitle.length > 0) {
      criteria = criteria + "&bannerTitle=" + bannerTitle;
    }
    //set the tc code
    if (typeof(tcCode) != "undefined" && tcCode.length > 0) {
      criteria = criteria + "&listDescTCCode=" + tcCode;
    }

    //Open list tab and do the search
    updateListTabData(criteria, "processSearchWithAdditional");
  });


  /*************************************************/
  /** handle the click on a link with a POST: tag. */
  /*************************************************/
  $("a[href*='POST:']").click(function() {
     var currentAnchor = $(this),
     ssoUrl = decodeURI(currentAnchor.attr("href").trim().substring(5)),
     formId =  "sso_" + currentAnchor.attr("title");
     if (currentAnchor.parent().attr("id") == formId) {
        $("#" + formId).submit();
        return false;
     }
     var result = parseUrl(ssoUrl);
     if (result[0]) {
       var action = result[0].mid, nameValuePair = "",
       form = $("<form />", {
           id: formId,
           action: action,
           method: "POST",
           target:"_blank"
      });
      currentAnchor.wrap(form);
      for (var index = 0; index < result[0].args.length; index++) {
         nameValuePair = nameValuePair + "<input type ='hidden' name='" + result[0].args[index].key + "' value ='" + result[0].args[index].value + "'>"
      }
      $(nameValuePair).insertBefore(currentAnchor);
      $("#" + formId).submit();
      return false;
     }
   });

  /*
   * Javascript URL Parser
   * @description Parses any URL like string and returns an array or URL "Components"
   */
  function parseUrl(str) {
    var arr = str.split('#'),
    result = new Array(),
    ctr=0;
    for (index in arr) {
      var qindex = arr[index].indexOf('?');
      result[ctr] = {};
      if( qindex==-1 ) {
        result[ctr].mid=part;
        result[ctr].args = [];
        ctr++;
        continue;
      }

      result[ctr].mid = arr[index].substring(0, qindex);
      var args = arr[index].substring(qindex+1);
      args = args.split('&');
      var localctr = 0;
      result[ctr].args = new Array();
      for (val in args ) {
        if(args[val] !== "" && args[val].indexOf("=") !== -1){
          var keyval = args[val].split('=');
          result[ctr].args[localctr] = new Object();
          result[ctr].args[localctr].key = keyval[0];
          result[ctr].args[localctr].value = decodeURIComponent(keyval[1].replace(/\+/g, " "));
          localctr++;
        }
      }
      ctr++;
    }
    return result;
  }
  /*************************************************/
  /** End of click on a link with a POST: tag.     */
  /*************************************************/

  /******************************/
  /** SEARCH FORM             ***/
  /******************************/
  var lastSearch = orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch");
  var searchFormId = orxapi.selectors.tabsBarZone.find("li.active").data("searchFormId");
  if (typeof(lastSearch) != "undefined") {
    orxapi.restoreForm(lastSearch, $("#" + searchFormId + ""));
  }

  //Switch the search form at the home page
  function switchSearchFormAtHomePage(parentRow, formCode){
    var url = "prepareHomeSearch.action";
    var code = $("#pge_home").data("code");

    $.ajax($.extend({}, orxapi.ajax, {
      url : url,
      data : {formCode: formCode, code: code},
      success : function(data, textStatus, jqXHR) {
        parentRow.empty();
        parentRow.html(data);
        $.getScript("./shared/ts/web/js/b2b/pge_list/pge_advSearch.js");
      }
    }));
    return false;
  }


  //Switch the search form by click on the tabs (default)
  $("#pge_home").on("click", "#tab_formType li", function() {

    var formCode = $(this).find("#formCode").data("value");


    var parentRow = $(this).parents(".cpt_searchBlock").parent();
    switchSearchFormAtHomePage(parentRow, formCode);
  });

  //Switch the search form by click dropdown list
  $("#pge_home").on("change", "#lst_formType", function() {
    var formCode = $(this).val();
    var parentRow = $(this).parents(".cpt_searchBlock").parent();
    switchSearchFormAtHomePage(parentRow, formCode);
  });

  // click on the link to go to the advanced search form
  $("#pge_home").on("click", ".advSearchLnk", function() {
    $("#headerBarZone").find(".pto_list").first().trigger('click');
  });

  // Process the search
  $("#pge_home").on("click", ".searchBtn", function() {
    var form = $(this).parents(".advSearchForm");
    if ($(this).parents(".advSearchForm").valid()) {
      var dataPage = $(this).data("page");

      // ognl fix: remove empty values from the list of params. if not, inputs with integers will be considered as strings
      var params = form.find('input, select').filter(function () {return $.trim(this.value);}).serialize();
      //set the result mode (cartouches or table) based on the cookie
      var prefsCookie = orx.getPreferencesCookie();
      if (!$.isEmptyObject(prefsCookie)) {
        if (typeof(prefsCookie.boxSearch) != "undefined") {
          params += "&boxSearch=" + prefsCookie.boxSearch;
        }
      }

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

      //Fill data behind HOME tab
      orxapi.selectors.tabsBarZone.find("li.active").data("searchFormId", form.attr('id'));
      orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch", params);

      //Fill the data behind the LIST tab and simulate a click on it to launch the search
      updateListTabData(params, dataPage);
    }
    return false;
  });

  //Fill the data of the list tab and open it
  function updateListTabData(params, dataPage) {
      //First, check that the list tab is opened
      if (orxapi.selectors.tabsBarZone.find("li[data-page='pge_list']").length == 0) {
        //Create the list tab
        var tabData = {
            pageName: "pge_list",
            tabLabel: orxapi.selectors.bodyContent.find("div:eq(0)").data("tab-list-label"),
            tabActive: true
        };
        orxapi.addTabInBar(tabData);
      }

      orxapi.selectors.tabsBarZone.find("li[data-page='pge_list']").data("lastSearch", params);
      orxapi.selectors.tabsBarZone.find("li[data-page='pge_list']").data("submenu", "lastSearchList");
      orxapi.selectors.tabsBarZone.find("li[data-page='pge_list']").data("lastSearchAction", dataPage);
      //Reinit the min prices
      orxapi.selectors.tabsBarZone.find("li[data-page='pge_list']").data("minprices", "");

      orxapi.selectors.tabsBarZone.find("li[data-page='pge_list']").trigger("click");
      orxapi.storeTabCookie();
  }
  /******************************/
  /** END OF SEARCH FORM      ***/
  /******************************/

  //--------------------------------------------------- Document ready functions

  //Handle missing images
  $(document).ready(function() {
    $("img").each(function(index) {
      if ($(this).parents(".thumbnailZone").length == 1) {
        // result product picto
        $(this).error(function() {
          this.src = "/b2b/shared/ts/web/images/missing_img.gif";
        });
        if ($(this).attr("src") == "") {
          $(this).attr("src", "/b2b/shared/ts/web/images/missing_img.gif");
        }
        $(this).attr("src", $(this).attr("src"));
      }
    });
  });
})(jQuery, orxapi);
