/*!
 * JavaScript common.
 *
 * @copyright (c) 2013 Orchestra
 * @project   LASTMINUTE - RESPONSIVE (DESKSTOP/TABLET/MINI-TABLET)
 * @version 1.0
 * @author : Alexandre PLOUZENNEC
 */



(function($) {


  $('#lightbox').click(function(e) {
    $('#lightbox').addClass( "hidden" );
  });

  $('#hieuroi').click(function(e) {
    e.preventDefault();
    $('#lightbox').removeClass( "hidden" );
  });

  var refineSearch = $(".contain-block"),
      btnDisplay = $(".btn-block > span"),
      blkSpecialOffer = $("#blkSpecialOffer"),
      advancedSearch = $("#advancedSearch");

  //  RETURN TO PREVIOUS SEARCH
  $('.link-blue-back').attr('href', getCookieByName("previousSearch"));


  // DISPLAY REFINE SEARCH BLOCK
  btnDisplay.on("click", function() {

    if (refineSearch.hasClass("close")) {
      refineSearch.removeClass("hidden");
      refineSearch.removeClass("close");
      btnDisplay.html("masquer");

    } else {
      refineSearch.addClass("hidden");
      refineSearch.addClass("close");
      btnDisplay.html("afficher");
    }

  });


  // DISPLAY FORMULE LINE SEARCH BLOCK
  advancedSearch.on("click", ".title", function() {
    var title = $(this),
        line = title.hasClass("title-up") ? "up": "bottom",
        icon = $(".filters-plus").css("float") === "left" ? title.find("> span") : advancedSearch.find(".title-" + line).find("> span"),
        elem = $(".filters-plus").css("float") === "left" ? title.next(".line-" + line) : advancedSearch.find(".line-" + line);

    if (elem.hasClass("close")) {
      elem.removeClass("hidden");
      elem.removeClass("close");
      icon.html("-");

    } else {
      elem.addClass("hidden");
      elem.addClass("close");
      icon.html("+");
    }
  });


  // CUSTOM SELECT BOX
  $('.custom-select-box').customSelect();

  // SCROLLTO TOP
  function juizScrollTo(element) {
    $(element).on("click", function() {
        var goscroll = false,
            the_hash = $(this).attr("href"),
            regex = new RegExp("\#(.*)","gi"),
            the_element = '';

        if(the_hash.match("\#(.+)")) {
            the_hash = the_hash.replace(regex,"$1");

            if($("#"+the_hash).length>0) {
                the_element = "#" + the_hash;
                goscroll = true;
            }
            else if($("a[name=" + the_hash + "]").length>0) {
                the_element = "a[name=" + the_hash + "]";
                goscroll = true;
            }

            if(goscroll) {
                $('html, body').animate({
                    scrollTop:$(the_element).offset().top
                }, 'slow');
                return false;
            }
        }
    });
  }

  juizScrollTo('a[href^="#"]');


  setTimeout(hidePub, 1);

  function hidePub() {
    $("#banner").addClass("hidden");
    $("#replayAds").removeClass("hidden");
  }

  $("#replayAds").on("click", function() {
    $("#banner").removeClass("hidden");
    $("#replayAds").addClass("hidden");
  });

  $("#closeAds").on("click", function() {
    $("#banner").addClass("hidden");
    $("#replayAds").removeClass("hidden");
  });


  // Click event: call the popup with data settings
  $(".link-video").on("click", function() {
   var elem = $(this),
       contentID = elem.data("contentId"),
       playerID = elem.data("playerId");

     newPopup("/video.cms?contentID=" + contentID + "&playerID=" + playerID);
  });


  // Add breakpoint for large desktop
  $.breakpoint({
    condition: function () {
      return window.matchMedia('only screen and (min-width: 959px)').matches;
    },
    enter: function () {
      if (advancedSearch.hasClass("close")) {
        btnDisplay.trigger("click");
      }
    }
  });


  // Add breakpoint for mini tablet and portrait mode
  $.breakpoint({
    condition: function () {
      return window.matchMedia('only screen and (min-width: 1px) and (max-width: 759px)').matches;
    },
    enter: function () {
      if (!advancedSearch.hasClass("close")) {
        btnDisplay.trigger("click");
      }
    }
  });


})(jQuery);


/**
 * newPopup
 *
 * @param {String} url The URL for the opening pop-up
 */
function newPopup(url) {
   popupWindow = window.open(url,'','height=400,width=700,left=10,top=10,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no,status=yes');
}


/**
 * getParamValueByName
 *
 * @param {String} (eg. queryString=s_aj%3D2&s_c.de=indifferent&s_dpci=&s_dd=&s_dmy=&s_aj=6)
 * @param {String} the name of one parameter
 * @return {String} the value of the given parameter name.
 */
function getParamValueByName(params, name){
  var paramValue = '';
      paramsTable = params.split('&'),
      tableLength = paramsTable.length;

  for (var i = 0; i < tableLength; i++) {
    if (paramsTable[i].indexOf(name) != -1) {
      var paramValueTable = paramsTable[i].split('=');
      if (paramValueTable.length > 1) {
        paramValue = paramValueTable[1];
        return paramValue;
      }
    }
  }

  return paramValue;
}
