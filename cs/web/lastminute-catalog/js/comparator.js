/*!
 * JavaScript comparator.
 *
 * @copyright (c) 2013 Orchestra
 * @project   LASTMINUTE - RESPONSIVE (DESKSTOP/TABLET/MINI-TABLET)
 * @version 1.0
 */

(function($) {


  "use strict";


  /** ---------------------------------------------
   * SET LOCAL VARIABLES
   */

      // Cache jQuery object
  var wrapper = $("#wrapper"),
      table = $("#compareTable"),

      // App
      fixedRowApp = {},
      equalizedApp = {};



  /** ---------------------------------------------
   * DELETE COLUMN
   */

  //
  $("#del-col1").on("click", function() {
    removeCmpPro(".col-1");
  });


  $("#del-col2").on("click", function() {
    removeCmpPro(".col-2");
  });


  $("#del-col3").on("click", function() {
    removeCmpPro(".col-3");
  });



  /** ---------------------------------------------
   * FIXED ROW
   */

  // initialize fixed row
  fixedRowApp = {

    // Default options
    options: {
      cloneClass: "fixed-row",
      wrapperElement: wrapper
    },

    // Initialize
    init: function(options) {
      this.opts = $.extend({}, this.options, options);

      this.wrap = $("<div />", { id: "topTableWrap", "class": "clone-wrap" });
      this.tableTop = $("<table />", { "class": "table-wrap" });
      this.top = table.offset().top;

      // Clone table
      this.clone(this.opts.cloneClass, this.opts.wrapperElement);

      // Scroll event: Display the top table clone.
      $(window).scroll(this.scroll).trigger("scroll");
    },

    // Create table clone
    clone: function(cloneClass, wrapperElement) {
      $("." + cloneClass).clone(true).appendTo(fixedRowApp.tableTop);
      fixedRowApp.tableTop.appendTo(fixedRowApp.wrap);
      fixedRowApp.wrap.appendTo(wrapperElement);
    },

    // Scroll event: toggle active class
    scroll: function () {
      if (fixedRowApp.top < $(this).scrollTop()) {
        fixedRowApp.wrap.addClass("active");

      } else {
        fixedRowApp.wrap.removeClass("active");
      }
    }
  };


  fixedRowApp.init();



  /** ---------------------------------------------
   * EQUALIZED ROW
   */

  equalizedApp = {

    // Initialize
    init: function() {
      // Default height
      table.find(".equalized-row").css({ "height": "auto" });

      // Iterate for equalized row
      table.find("tr").each(function() {
        $(this).find(".equalized-row").equalSize();
      });
    }
  };



  /** ---------------------------------------------
   * BREAKPOINT
   */

  // Add breakpoint for large desktop
  $.breakpoint({
    condition: function () {
      return window.matchMedia('only screen and (min-width: 959px)').matches;
    },
    enter: function () {
      equalizedApp.init();
    }
  });

  // Add breakpoint for tablet and landscape mode
  $.breakpoint({
    condition: function () {
      return window.matchMedia('only screen and (min-width: 759px) and (max-width: 959px)').matches;
    },
    enter: function () {
      equalizedApp.init();
    }
  });

  // Add breakpoint for mini tablet and portrait mode
  $.breakpoint({
    condition: function () {
      return window.matchMedia('only screen and (min-width: 1px) and (max-width: 759px)').matches;
    },
    enter: function () {
      equalizedApp.init();
    }
  });


})(jQuery);



/**
 * removeCmpPro
 *
 * @param {String} className
 */
function removeCmpPro(className) {
  var text = $(className).find(".reference").text(),
      ref = "ref.: ",
      id = $.trim(text.substring(text.indexOf(ref) + ref.length));

  removeFromCookies("comparator", id);

  if (getCookieByName("comparator") === "") {
    window.location.href = getCookieByName("previousSearch");

  } else {
    window.location.href = window.location.href;
  }
}

