(function($, orx){

  var orx_dp = orx.plugin.datepicker;
  var dateSelected = "";

  initDatePicker();
  $(".table-sortable").tablesorter();

  function initDatePicker() {
    $("#ipt_compareDate").datepicker($.extend({}, orx_dp, {
      onClose: function(dateText) {
        //Don't launch a search if the value hasn't changed
        if (dateText != dateSelected) {
          dateSelected = dateText;
          if (dateText == "") {
            var defaultMargin = $("#defaultMargin").data('value');;
            $("#slt_compareMoreLess").val(defaultMargin);
            $("#slt_compareMoreLess").attr("disabled","disabled");
          } else {
            $("#slt_compareMoreLess").removeAttr("disabled","disabled");
          }
          submitComparatorForm();
        }
      }
    }));
    orxapi.pictoDate();
  }

  /* Resize height content for scroll  */
  $.fn.resizeContentTab = function () {
    var windowHeight = $(window).height(),
        contentTabHeight = $(window).height() - $("#headerBarZone").height() - $("#tabsBarZone").height() - $("#searchZone").outerHeight() - ($("#supHeaderBarZone").length ? $("#supHeaderBarZone").height() : 0);
    $(this).css("height", contentTabHeight);
  };
  $("#pge_comparator").find("#wrapContent .comparatorZone").resizeContentTab();
  $(window).resize(function(){
    $("#pge_comparator").find("#wrapContent .comparatorZone").resizeContentTab();
  });

  /**
   * Remove a product from the comparator.
   */
  $("#tbe_comparator").find(".btn_deletePdt").click(function(){
    var colProduct = $(this).data("delprdt"),
        colProductClass = $("#tbe_comparator").find("."+colProduct),
        emptyCompare = $("<td />", { "class":"td_emptyCompare", "rowspan":"99", "text":"Le comparateur est vide" });

    if(colProduct != "allproduct") {
        //Remove a single product

        var action = $(this).data("page") + ".action";
        var productid = $(this).parents("td").data("id");

        //Treatment part
        var oldNumber = parseInt($("#headerBarZone").find("li[data-nav='pge_comparator']").find(".notificationBubble").text());
        var newString = orxapi.removeValueFromCommaSeparatedString($("#headerBarZone").find("li[data-nav='pge_comparator']").data("param"),productid);
        $("#headerBarZone").find("li[data-nav='pge_comparator']").data("param",newString);
        var newNumber = oldNumber - 1;
        if (newNumber == 0) {
          $("#headerBarZone").find("li[data-nav='pge_comparator']").find(".notificationBubble").addClass("hidden");
          $("#headerBarZone").find("li[data-nav='pge_comparator']").addClass("disabled");
        }
        $("#headerBarZone").find("li[data-nav='pge_comparator']").find(".notificationBubble").text(oldNumber - 1);
        orxapi.selectors.tabsBarZone.find("li.active").data("id", newString);

        //Change the href for the button printMultipleProducts
        $("#printMultipleProducts").attr("href", "printMultipleProducts.action?ids=" + $("#headerBarZone").find("li[data-nav='pge_comparator']").data("param"));

        //Set the ids and the number in the cookie
        var data = {comparatorIds : $("#headerBarZone").find("li[data-nav='pge_comparator']").data("param"),
            comparatorNumber : $("#headerBarZone").find("li[data-nav='pge_comparator']").find(".notificationBubble").text()};
        orxapi.updatePreferencesCookie(data);
        orxapi.storeTabCookie();

        //Design part
        if ($("#tbe_comparator").find(".td_delete").length <= 1) {
          orxapi.selectors.tabsBarZone.find("li.active").closeTab();
        } else {

          colProductClass.contents().remove();
          var numberOfElements = colProductClass.length;
          deactivateHover();
          colProductClass.each(function() {
            $(this).delay(100).animate({width:"0px", minWidth:"0px"}, function(){ $(this).remove(); if (!--numberOfElements) {activateHover();} });
          });

        }

    } else {
      //Remove all products

      //Treatment part
      $("#headerBarZone").find("li[data-nav='pge_comparator']").data("param","");
      $("#headerBarZone").find("li[data-nav='pge_comparator']").find(".notificationBubble").text("0");
      $("#headerBarZone").find("li[data-nav='pge_comparator']").find(".notificationBubble").addClass("hidden");
      $("#headerBarZone").find("li[data-nav='pge_comparator']").addClass("disabled");
      orxapi.selectors.tabsBarZone.find("li.active").data("id", "");

      //Set the ids and the number in the cookie
      var data = {comparatorIds : $("#headerBarZone").find("li[data-nav='pge_comparator']").data("param"),
          comparatorNumber : $("#headerBarZone").find("li[data-nav='pge_comparator']").find(".notificationBubble").text()};
      orxapi.updatePreferencesCookie(data);
      orxapi.storeTabCookie();

      orxapi.selectors.tabsBarZone.find("li.active").closeTab();

//      //Design part
//      $("#tbe_comparator").find("th").each(function(){
//        $(this).css({ "height": $(this).height() })
//      });
//      $("#tbe_comparator").find("[class*='comparePdct']").remove();
//      $("#tbe_comparator").find("tr:first").append(emptyCompare);
//      $("#tbe_comparator").find(".btn_actions").addClass("disabled");
//

    }
  });

  //-------------------------//
  // Availability search     //
  //-------------------------//
  $("#innerWrapContent").on("change", "select", function() {
    if ($(this).attr("id") == "slt_compareMoreLess" && $("#ipt_compareDate").val() == "") {
      return;
    }

    submitComparatorForm();
  });

  function submitComparatorForm() {
    //Save the values in the tab
    var cityCode = $("#slt_compareCityStart").val();
    var duration = $("#slt_compareDuration").val();
    var currSelectedDate = $("#ipt_compareDate").val();
    var margin = $("#slt_compareMoreLess").val();
    orxapi.selectors.tabsBarZone.find("li.active").data("prefs", cityCode + "-" + duration + "-" + currSelectedDate + "-" + margin);

    var param = $("#comparatorForm").serialize();
    param += "&ids=" + $("#headerBarZone").find("li[data-nav='pge_comparator']").data("param");

    $.ajax($.extend({}, orxapi.ajax, {
      url : $("#comparatorForm").data("action") + ".action",
      data : param,
      async : false,
      success : function(data, textStatus, jqXHR) {

        var currentMinPriceTr = $(".comp-min_price");
        var currentDatesTr = $(".comp-dates");

        var newMinPriceTr = $(data).filter(".comp-min_price");
        var newDatesTr = $(data).filter(".comp-dates");

        //replacing min prices
        var previousSibling = currentMinPriceTr.prev();
        currentMinPriceTr.remove();
        previousSibling.after(newMinPriceTr);

        //replacing dates
        var previousSibling = currentDatesTr.prev();
        currentDatesTr.remove();
        previousSibling.after(newDatesTr);

        activateHover();
      }
    }));
  }
  //-----------------------------//
  // End of Availability search  //
  //-----------------------------//

  //-----------------------------//
  // Button actions              //
  //-----------------------------//
  //Disabled the button if there is no list
  var btnGroup = $("#tbe_comparator").find(".btn-group");
  btnGroup.each(function() {
    if ($(this).find("li").length == 0) {
      $(this).find("button").addClass("disabled");
    }
  });

  //Add to the list
  $("#tbe_comparator").on("click", ".li_product_page_favlist", function() {
    var id = $(this).parents("td").data("id");
    var index = $(this).parents("td").data("index");
    var name = $("#tbe_comparator").find(".comp-title").find("." + index).text();
    var listName = $(this).data("listname");
    orxapi.addToFav(id, name, listName);
  });

  //-----------------------------//
  // End of button actions       //
  //-----------------------------//

  //Display the product page when clicking the title or the image of a product
//  $("#tbe_comparator").on("click", ".product-link", function() {
//    var id = $(this).data("id");
//    // load the page and create the tab
//    orxapi.openPage(id);
//  });

  //Dbl click on a column opens the product (except in the Dates section)
  $("#tbe_comparator").on("dblclick", 'td.dbl-clickable', function() {
    var id = $(this).data("id");
    // load the page and create the tab
    orxapi.openPage(id);
  });

  //Display the product page when clicking on an avail
  $("#tbe_comparator").on("click", ".product-date-select", function() {
    var tr = $(this).parents("tr.comp-dates-one-date");
    var id = tr.data("id");

    var depCity = tr.find(".td_city").data("value");
    var duration = tr.find(".td_duration").data("value");
    var depDate = tr.find(".td_date").data("value");
    var toProductCode = tr.parents(".td_dateAvailable").data("toproductcode");

    var prefs = depCity + "-" + duration + "-" + depDate;

    //Create the product tab and fills it's data + simulate click
    orxapi.updateProductTabData(id, prefs, toProductCode);
  });

  //-------------------------//
  // Init search form        //
  //-------------------------//
   /**
    * Init the search form.
    */
  function initSearchForm() {
    var tabPrefs = orxapi.selectors.tabsBarZone.find("li.active").data("prefs");
    var prefsFoundInTab = false;
    if (tabPrefs) {
      var prefs = tabPrefs.split("-");
      if (prefs && prefs.length == 4) {

        $('#slt_compareCityStart').append('<option value="' + prefs[0] + '" selected="selected"></option>');
        $("#slt_compareCityStart").val(prefs[0]);
        $('#slt_compareDuration').append('<option value="' + prefs[1] + '" selected="selected"></option>');
        $("#slt_compareDuration").val(prefs[1]);
        $("#ipt_compareDate").val(prefs[2]);
        $("#slt_compareMoreLess").val(prefs[3]);

        prefsFoundInTab = true;
      }
    }

    var prefsCookie = orx.getPreferencesCookie();
    if (prefsCookie != null) {
      // Take this part only if we did not find the preferences in the tab
      if (!prefsFoundInTab) {
        // If possible, select the first preferred city that exists in the possible departure cities.
        if (prefsCookie.depCities != null) {
          var depCitiesArray = prefsCookie.depCities;
          for (var i = 0; i < depCitiesArray.length; i++) {
            var cityCode = depCitiesArray[i];
            $('#slt_compareCityStart').append('<option value="' + cityCode + '" selected="selected"></option>');
            $("#slt_compareCityStart").val(cityCode);
            // If the city actually exists in the dep cities, we're fine. Else we have to continue the loop and try with the next one.
            if ($("#slt_compareCityStart").val() == cityCode) {
              break;
            }
          }
        }

        if (prefsCookie.depDate != null) {
          $("#ipt_compareDate").val(prefsCookie.depDate);
        }
        if (prefsCookie.margin != null) {
          $("#slt_compareMoreLess").val(prefsCookie.margin);
        }
      }
    }

    // Retrieve the city and date. The duration can only be set by the "data" part
    var cityCode = $("#slt_compareCityStart").val(),
        currSelectedDate = $("#ipt_compareDate").val(),
        margin = $("#slt_compareMoreLess").val(),
        duration = $("#slt_compareDuration").val(),
        prefsCookie = orx.getPreferencesCookie();

    // Call the server in order to update the calendar
    var data = {
        cityCode : cityCode,
        duration : duration,
        departureDate : currSelectedDate,
        margin : margin,
    };



    if (prefsCookie != null) {
      if (prefsCookie.depCities != null) {
        depCities = prefsCookie.depCities;
        var stringDepCities = depCities.join(",");
            $.extend(data, {preferredDepCities: stringDepCities});
      }
    }

    updateSearchForm(data);
  }

  /**
   * Ajax call to update the calendar.
   */
  function updateSearchForm(data) {
    $.extend(data, {ids: $("#headerBarZone").find("li[data-nav='pge_comparator']").data("param")});
    $.ajax($.extend({}, orxapi.ajax, {
      url : "updateComparatorSearchForm.action",
      data : data,
      success : function(data, textStatus, jqXHR) {
        $("#comparatorForm").html(data);
        initDatePicker();
        dateSelected = $("#ipt_compareDate").val();
        if (dateSelected == "") {
          var defaultMargin = $("#defaultMargin").data('value');
          $("#slt_compareMoreLess").val(defaultMargin);
          $("#slt_compareMoreLess").attr("disabled","disabled");
        }
        submitComparatorForm();
      }
    }));
  }
  //--------------------------//
  // End of Init search form  //
  //--------------------------//

  //--------------------------------------------------- Document ready functions
  //Handle missing images
  $(document).ready(function() {
    $("img").each(function(index) {
      if ($(this).parents(".td_image").length == 1) {
        // result product picto
        $(this).error(function() {
          this.src = "/b2b/shared/ts/web/images/missing_img.gif";
        });
        $(this).attr("src", $(this).attr("src"));
      }
    });
  });

  //If there is at least one product, init the form
  $(document).ready(function() {
    var comparator = $("#headerBarZone").find("li[data-nav='pge_comparator']");

    //Check and update the number of products if needed (if a product has been unpublished for example)
    checkPresentProducts(comparator);
    comparator.find(".notificationBubble").text($(".comp-header").find("td").length);
    if (parseInt(comparator.find(".notificationBubble").text()) == 0) {
      comparator.find(".notificationBubble").addClass("hidden");
      comparator.addClass("disabled");
    } else {
      comparator.find(".notificationBubble").removeClass("hidden");
    }
    //Set the ids of the header icon in the tab
    orxapi.selectors.tabsBarZone.find("li.active").data("id", comparator.data("param"));
    //Set the ids and the number in the cookie
    var data = {comparatorIds : comparator.data("param"),
        comparatorNumber : comparator.find(".notificationBubble").text()};
    orxapi.updatePreferencesCookie(data);
    orxapi.storeTabCookie();

    if ($("#noProducts").length == 0) {
      //Init the form
      initSearchForm();
    }
  });

  /**
   * Compare the products displayed in the comparator with the ids stored in the header icon
   * and remove some ids from the icon if needed.
   */
  function checkPresentProducts(comparator) {
    var presentIds = new Array();
    var i=0;
    $(".comp-header").find("td").each(function(){
      var currId = $(this).data("id");
      presentIds[i] = currId;
      i++;
    });
    var params = comparator.data("param");
    var paramsArray = params.split(",");

    for(i = 0 ; i < paramsArray.length ; i++) {
      var found = false;
      for(j = 0 ; j < presentIds.length ; j++) {
        if (presentIds[j] == paramsArray[i]) {
          found = true;
          break;
        }
      }

      if (!found) {
        paramsArray.splice(i,1);
      }
    }

    comparator.data("param", paramsArray.join(","));
  }

  function activateHover() {
    /* Hover table comparator */
    $("[class*='comparePdct']").hover(function() {
      $(".comparePdct" + $(this).data("target")).addClass("hover");
    }, function(){
      $(".comparePdct" + $(this).data("target")).removeClass("hover");
    });
  }

  function deactivateHover() {
    $("[class*='comparePdct']").unbind('mouseenter mouseleave');
  }

})(jQuery, orxapi);
