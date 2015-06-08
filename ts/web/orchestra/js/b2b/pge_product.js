(function($, orx) {

  /* ------------------------------------------------- *
   *                                                   *
   *                   CALENDAR PART                   *
   *                                                   *
   * ------------------------------------------------- */

  /*---------------------------
    Calendar global functions
   ---------------------------*/
  /**
  * Ajax call to update the calendar
  */
  updateCalendar = function(data) {
   $.extend(data, {id: orxapi.selectors.tabsBarZone.find("li.active").data("id")});
   $.ajax($.extend({}, orxapi.ajax, {
     url : "updateCalendar.action",
     data : data,
     success : function(data, textStatus, jqXHR) {
       orxapi.updatePageFromJSONResponse(data);
       resizeDiaporama();
     }
   }));
  }

  /**
  * Updates the calendar, setting the first month to the input month
  */
  updateCalendarByMonth = function(month) {
   var cityCode = $("#slt_startCity").val(),
       duration = $("#slt_duration").val(),
       currSelectedDate = $("#selectedDate").val(),
       data = {
         cityCode : cityCode,
         duration : duration,
         departureDate : currSelectedDate,
         firstMonthToDisplay : month
       };

   updateCalendar(data);
  }

  /**
  * Initializes the calendar
  */
  initCalendar = function() {
    var tabPrefs = orxapi.selectors.tabsBarZone.find("li.active").data("prefs");
    var prefsFoundInTab = false;
    var duration = "";
    var cityCode = "";
    if (tabPrefs) {
      var prefs = tabPrefs.split("-");
      if (prefs && prefs.length == 3) {
        cityCode = prefs[0];
        $("#slt_startCity").val(cityCode);
        duration = prefs[1];
        $("#slt_duration").val(duration);
        $("#selectedDate").val(prefs[2]);
        prefsFoundInTab = true;
      }
    }

    var prefsCookie = orx.getPreferencesCookie();
    if (!$.isEmptyObject(prefsCookie)) {
      // Always retrieve this part from the cookie
      if (prefsCookie.nbAdults) {
        $("#slt_nbrAdult").val(prefsCookie.nbAdults);
      }
      if (prefsCookie.nbChildren) {
        var nbChildren = prefsCookie.nbChildren
        $("#slt_nbrChildren").val(nbChildren);
        if (prefsCookie.childrenAges) {
          var childrenAges = prefsCookie.childrenAges;
          for (var i = 0; i < nbChildren && i < childrenAges.length; i++) {
            var currentAge = childrenAges[i];
            var currAgeTab = currentAge.split("/");
            if (currAgeTab && currAgeTab.length == 3) {
              var currChildNumber = i + 1;
              $("#slt_dayChild" + currChildNumber).val(currAgeTab[0]);
              $("#slt_monthChild" + currChildNumber).val(currAgeTab[1]);
              $("#slt_yearChild" + currChildNumber).val(currAgeTab[2]);
            }
          }
        }
      }

      // Take this part only if we did not find the preferences in the tab
      if (!prefsFoundInTab) {
        // At the moment, select only the departure date
        if (prefsCookie.depDate) {
          $("#selectedDate").val(prefsCookie.depDate);
        }
      }
    }


    // Retrieve the date. The city and duration can only be set by the "data" part of the tab
    var currSelectedDate = $("#selectedDate").val(),
        prefsCookie = orx.getPreferencesCookie();

    // Call the server in order to update the calendar
    var data = {
        cityCode : cityCode,
        duration : duration,
        departureDate : currSelectedDate,
    };

    if (!$.isEmptyObject(prefsCookie)) {
      if (prefsCookie.nightsInterval) {
        $.extend(data, {nightsInputInterval : prefsCookie.nightsInterval});
      }
      if (prefsCookie.daysInterval) {
        $.extend(data, {daysInputInterval : prefsCookie.daysInterval});
      }
      if (prefsCookie.depCities) {
        depCities = prefsCookie.depCities;
        var stringDepCities = depCities.join(",");
        $.extend(data, {preferredDepCities: stringDepCities});
      }
    }

    updateCalendar(data);
  }

  /*------------------
     Calendar actions
    ------------------*/
  // Change city / duration
  $("#slt_startCity, #slt_duration").on("change", function() {
    var cityCode = $("#slt_startCity").val();
    var duration = $("#slt_duration").val();
    var currSelectedDate = $("#selectedDate").val();
    var data = {
      cityCode : cityCode,
      duration : duration,
      departureDate : currSelectedDate,
    }

    orxapi.selectors.tabsBarZone.find("li.active").data("prefs", cityCode + "-" + duration + "-" + currSelectedDate);

    updateCalendar(data);
  });

  // Click on an arrow of the calendar (previous/next month)
  $("#wrapContent").on("click", ".btn-small.left, .btn-small.right", function() {
    var month = $(this).data("first-month");
    updateCalendarByMonth(month);
  });

  //Change the month via the select
  $("#wrapContent").on("change", ".monthChoice", function() {
    var month = $(this).val();
    updateCalendarByMonth(month);
  });

  // Click on a day of the calendar
  $("#wrapContent").on("click", ".innerDay.active", function() {
    // CSS changes
    $(".tbe_calendarDateBook .innerDay.active").removeClass("selected");
    $(this).addClass("selected");

    // Update the selected date
    var thisDate = $(this).data("date");
    if (thisDate) {
      $("#selectedDate").val(thisDate);
    }

    // Store the selected dispo in the tab
    var cityCode = $("#slt_startCity").val();
    var duration = $("#slt_duration").val();
    var currSelectedDate = $("#selectedDate").val();
    orxapi.selectors.tabsBarZone.find("li.active").data("prefs", cityCode + "-" + duration + "-" + currSelectedDate);
  });


  /*-------------------------
     Calendar initialization
    -------------------------*/
  initCalendar();

  /* ---------------------------------------------- *
   *                                                *
   *    BIRTHDATE + TRAVELLERS CONSTRUCTION PART    *
   *                                                *
   * ---------------------------------------------- */

  constructChildrenAges = function() {
    var childrenAges = new Array();
    $(".birthdate:visible").each(function(index) {
      var day = $(this).find(".ipt_day").val();
      var month = $(this).find(".ipt_month").val();
      var year = $(this).find(".ipt_year").val();
      childrenAges[index]= day + "/" + month + "/" + year;
    });

    return childrenAges;
  }

  constructTravellerFormInputs = function(nbAdults, nbChildren, childrenAges) {
    var globalTravellerCounter = 0;
    var inputs = "";
    for (; globalTravellerCounter < nbAdults; globalTravellerCounter++) {
      inputs += "<input type=\"hidden\" name=\"traveller[" + globalTravellerCounter + "].type\" value=\"A\">\n";
    }
    for (var i = 0; i < nbChildren; i++ + globalTravellerCounter++) {
      inputs += "<input type=\"hidden\" name=\"traveller[" + globalTravellerCounter + "].birthDate\" value=\"" + childrenAges[i] + "\">\n";
    }

    $("#travellerInputs").html(inputs);
  }

  // Give the focus to the next input (when date are displayed as inputs)
  $("#wrapContent").on("keyup", ".ipt_day, .ipt_month", function() {
    if ($(this).val().length == 2) {
      $(this).next().focus().select();
    }
  });

  // Placeholder (when date are displayed as inputs)
  $("#wrapContent").on("focus", ".ipt_day, .ipt_month, .ipt_year", function() {
    if( $(this).val() == $(this).attr("placeholder")) {
      $(this).val("");
    }
  }).on("blur", ".ipt_day, .ipt_month, .ipt_year", function() {
    if( !$(this).val() ) {
      $(this).val($(this).attr("placeholder"));
      $(this).trigger("change");
    }
  });

  /* ---------------------------------------------- *
   *                                                *
   *                 FORM SUBMISSION                *
   *                                                *
   * ---------------------------------------------- */

  fillCityAndDuration = function() {
    $("#depCityCode").val($("#slt_startCity").val());
    var duration = $("#slt_duration").val();
    if (duration) {
      var durationTab = duration.split(",");
      if (durationTab && durationTab.length == 2) {
        $("#nbDays").val(durationTab[0]);
        $("#nbNights").val(durationTab[1]);
      }
    }
  }

  validateForm = function() {
    var formValid = true;

    $(".birthdate:visible").each(function() {
      var isValid = false;
      var date = "";
      date += $(this).find(".ipt_day").val() + "/";
      date += $(this).find(".ipt_month").val() + "/";
      date += $(this).find(".ipt_year").val();
      try {
        $.datepicker.parseDate("dd/mm/yy", date);
        isValid = true;
        $(this).find("input").removeClass("error");
      } catch (e) {
        // The date is not valid
        $(this).find("input").addClass("error");
      }
      formValid = formValid && isValid;
    });

    return formValid;
  }

  $(".reservationForm").on("submit", function() {
    var formValid = validateForm();
    var resaForm = $(this);
    if (!formValid) {
      return false;
    }

    var childrenAges = constructChildrenAges(),
        duration = $("#slt_duration").val(),
        dateSelected = $("#calendar").find("div.selected").data("date");

    // chech via ajax the children age
    $.ajaxSettings.traditional = true;
    $.ajax($.extend({}, orxapi.ajax, {
        url : "validateTravellerAge.action",
        async : false,
        data : {
            childrenAges : childrenAges,
            duration : duration,
            dateSelected : dateSelected
        },
        success : function(data) {
          if (data != null && data != "") {
              //Display the "do you really want to delete" dialog
              var contentDialog = $("<div/>",{ "id":"contentDialog"});
              contentDialog.html(data);
              contentDialog.dialog($.extend({}, orx.plugin.dialog, {
                buttons:{
                  "Fermer": function() {
                    $(this).dialog("destroy").remove();
                    return false;
                   }
                },
                width:"420"
              }));
          } else {
                // Update the number of adults/children and their birth dates in the preferences cookie
                var nbAdults = $("#slt_nbrAdult").val();
                var nbChildren = $("#slt_nbrChildren").val();
                var data = {
                    nbAdults: nbAdults,
                    nbChildren: nbChildren,
                    childrenAges: childrenAges
                };
                orxapi.updatePreferencesCookie(data);

                // Construct the traveller inputs of the reservation form
                constructTravellerFormInputs(nbAdults, nbChildren, childrenAges);

                // Set the current city and duration
                fillCityAndDuration();

                // Fill the generic part of this form
                resaForm.find(".genericParameters").html($("#resaGenericParameters").html());

                // Switch in reservation mode
                $(".nav .itemBook").removeClass("hidden");
                updateToResaMode();
                orxapi.selectors.tabsBarZone.find("li.active").data("resaKey", $("#resaKey").val());
                orxapi.selectors.tabsBarZone.find("li.active").data("sidebar", $(".nav .itemBook").data("ref"));


          }
        }
      }));

  });

  /* ------------------------------------------------- *
   *                                                   *
   *                   RESERVATION                     *
   *                                                   *
   * ------------------------------------------------- */

  /*----------------------------------------------------
    Switch between the reservation and the product page
    -----------------------------------------------------*/
  updateToResaMode = function() {
    // Switch the contents
    $("#wrapContent").addClass("hidden");
    $("#wrapContent").attr("id", "hiddenWrapContent");
    $("#reservationFrame").removeClass("hidden");
    $("#reservationFrame").attr("id", "wrapContent");

    // Update the side bar
    $("#wrapSideBar").addClass("bookMode");
    $("#wrapSideBar").find(".nav .item").removeClass("active");
    $("#wrapSideBar").find(".nav .item.itemBook").addClass("active");

    $("#wrapContent").resizeContentTab();
    $("iframe#wrapContent").css({ "width": $("#pge_product").width() - $("#wrapSideBar").width() });
  }

  updateToProductMode = function() {
    // Switch the contents
    $("#wrapContent").addClass("hidden");
    $("#wrapContent").attr("id", "reservationFrame");
    $("#hiddenWrapContent").removeClass("hidden");
    $("#hiddenWrapContent").attr("id", "wrapContent");

    // Update the side bar
    $("#wrapSideBar").removeClass("bookMode");
    $("#wrapSideBar").find(".nav .item.itemBook").removeClass("active");

    $("#wrapContent").resizeContentTab();
  }

  /*----------------------------------------------
     Restore the previous reservation if possible
    ----------------------------------------------*/
  var resaKey = orxapi.selectors.tabsBarZone.find("li.active").data("resaKey");
  if (resaKey && resaKey != "") {
    $.ajax($.extend({}, orxapi.ajax, {
      url : "retrieveResaSession.action",
      data : {
        key : resaKey
      },
      success : function(data) {
        if (data && data != "") {
          var jsonData = $.parseJSON(data);
          if (jsonData && jsonData.hasOwnProperty("url")) {
            var resaUrl = jsonData["url"];
            if (resaUrl && resaUrl != "") {
              $("#reservationFrame").attr("src", jsonData["url"]);
              $(".nav .itemBook").removeClass("hidden");

              // restore the reservation tab if needed (do it in this callBack or it won't work)
              var lastTab = orxapi.selectors.tabsBarZone.find("li.active").data("sidebar");
              if (lastTab && lastTab == "reservation") {
                $(".nav .itemBook").trigger("click");
              }
            }
          }
        }
      }
    }));
  }

  /* ------------------------------------------------- *
   *                                                   *
   *                  MISCELLANEOUS                    *
   *                                                   *
   * ------------------------------------------------- */

  /*------------------------
     Print the product page
    ------------------------*/
  /*$(".print_btn").on("click", function() {
    window.open("printProduct.action", "_self");
  });*/

  /*-------------------------------
     Send the product page by mail
    -------------------------------*/
  /*$(".sendMail_btn").on("click", function() {
    window.open("sendProductByMail.action", "_self");
  });*/
  $(".sendMail_btn").on("click", function(evt) {
    evt.preventDefault();
    orxapi.constructMailPopin("prepareSendProduct", null);
  });

  /*-----------------------------------
    Add the product to a favorite list
   ------------------------------------*/
  //Add to the list
  $("#headerPage").on("click", ".li_product_page_favlist", function() {
    // Get the ids of checked products
    var id = orxapi.selectors.tabsBarZone.find("li.active").data("id");
    var name = $("#headerPage").find(".titleZone .title").text();
    var listName = $(this).data("listname");
    orxapi.addToFav(id, name, listName);
  });

  /*--------------------------------
     Add to the comparator
   ---------------------------------*/
  $("#headerPage").on("click", ".add-to-comparator", function() {
    var id = orxapi.selectors.tabsBarZone.find("li.active").data("id");
    orxapi.addProductToComparator(id);
  });

  /* ------------------------------------------------- *
   *                                                   *
   *                   DESIGN PART                     *
   *                                                   *
   * ------------------------------------------------- */

  /* Resize height content for scroll  */
  $.fn.resizeContentTab = function () {
    var windowHeight = $(window).height(),
        contentTabHeight = $(window).height() - $("#headerBarZone").height() - $("#tabsBarZone").height() - $("#headerPage").height() - ($("#supHeaderBarZone").length ? $("#supHeaderBarZone").height() : 0);
    $(this).css("height", contentTabHeight);
  };
  $("#pge_product").find("#wrapContent").resizeContentTab();

  $(window).resize(function(){
    $("#pge_product").find("#wrapContent").resizeContentTab();
    $("iframe#wrapContent").css({ "width": $("#pge_product").width() - $("#wrapSideBar").width() });
  });


  $(".nav").on("click",".item:not(.disabled)", function(e){
    e.preventDefault();

    if ($(this).hasClass("itemBook")) {
      orxapi.selectors.tabsBarZone.find("li.active").data("sidebar", $(this).data("ref"));
      // We clicked on the resa button
      if (!$("#wrapSideBar").hasClass("bookMode")) {
        // We are in the product mode but have already a reservation, show it.
        updateToResaMode();
      }
    } else {
      // We clicked on another button
      if ($("#wrapSideBar").hasClass("bookMode")) {
        // We are in the resa mode, we need to update the page
        updateToProductMode();
      }
      // Scroll
      $("#wrapContent").stop().animate({ scrollTop: $($(this).find("a").attr("href")).offset().top + $("#innerWrapContent").offset().top*-1 }, 400);
    }
  });


  /**
   * Add change event for children pax select.
   * and first initizalised
   */
  $("#formBook").find("#slt_nbrChildren").change(function(evt){
    var val = $(this).val();
    // Close when children is not selected
    if (val === "0") {
      $(".ageChildrenSelection").hide();
      // Open and show number of children selected
    } else {
    $(".ageChildrenSelection")
      .find("li")
      .hide().end()
      .find("li:lt(" + val + ")")
      .show().end()
      .show();
    }

  }).change();


  $(".slt_monthList").on("change", function(){
    $(".scrollableZone").stop().animate({ scrollTop: $("#"+this.value).offset().top + $(".listTravelView").offset().top*-1 }, 400);
  });

  $('#wrapContent').scrollspy('refresh');

  // TODO Slideshow. load() does not fire when the image is cached (image loaded before jquery binds the event)
  // + count the number of images, so we resize only when ALL images are loaded ? (but then care if 1 image is never loaded...)
  /*
  var imagesLength = 0;
  $("#galleryScroll img").each(function(){
    $(this).load(function(){
      imagesLength += $(this).outerWidth(true);
      $(".insideSlide").width(imagesLength);
    });
  });
  */

  /*-----------------------------------
     Restore the sidebar tab if needed
    -----------------------------------*/
   var lastTab = orxapi.selectors.tabsBarZone.find("li.active").data("sidebar");
   if (lastTab && lastTab != "") {
    $("#wrapSideBar .item").each(function() {
      var currentRef = $(this).data("ref");
      // NB we can't handle the restoration of the reservation tab here or it will restore an empty iframe,
      // and the trigger("click") won't work. We handle this reservation case in the callback of the ajax restoring
      // the reservation iframe
      if (currentRef && currentRef != "reservation" && currentRef == lastTab) {
        $(this).trigger("click");
      }
    });
   }

   // Store the current tab of the sidebar
   $("#wrapSideBar").on("activate", ".nav li", function() {
     orxapi.selectors.tabsBarZone.find("li.active").data("sidebar", $(this).data("ref"));
   });

  /* ------------------------------------------------- *
   *                                                   *
   *                  INITIALIZATION                   *
   *                                                   *
   * ------------------------------------------------- */

  $(document).ready(function() {
    //Disabled the "add to list" button if there is no list
    var btnGroup = $(".fileOptionZone").find(".btn-group");
    btnGroup.each(function() {
      if ($(this).find("li").length == 0) {
        $(this).find("button").addClass("disabled");
      }
    });

    //Disable the add to comparator if the product is already in it
    var id = orxapi.selectors.tabsBarZone.find("li.active").data("id");
    var comparator = $("#headerBarZone").find("li[data-nav='pge_comparator']");
    var compProducts = comparator.data("param");
    if (compProducts != "") {
      var compProductsArray = compProducts.split(",");
      for (var i=0 ; i<compProductsArray.length ; i++) {
        if (compProductsArray[i] == id) {
          $(".add-to-comparator").addClass("disabled");
          break;
        }
      }
    }

    //count the number of images
    var imageCount = $(".slideshowImg").length;
    // init a load counter
    var loadCounter = 0;

    $(".slideshowImg").load(function() {

        $('.insideSlide').width(imageCount*700);
        // an image has loaded, increment load counter
        loadCounter++;
        // remove overlay once all images have loaded
        if(loadCounter == imageCount) {
            resizeDiaporama();
        }
    }).each(function() {
        // make sure the `.load` event triggers
        // even when the image(s) have been cached
        // to ensure that the diaporama sized is updated
        if(this.complete) {
            $(this).trigger("load");
        }
    });

    resizeDiaporama();
  });

  $(window).load(function(){
      resizeDiaporama();
  });

  function resizeDiaporama() {
    var totalDiaporamaWidth = 0;
    $('.slideshowImg').each(function() {
        totalDiaporamaWidth += $(this).outerWidth(true);
    });
    $('.insideSlide').width(totalDiaporamaWidth + 20);
  };

})(jQuery, orxapi);
