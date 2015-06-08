/*!
 * JavaScript product.
 *
 * @copyright (c) 2013 Orchestra
 * @project   LASTMINUTE - RESPONSIVE (DESKSTOP/TABLET/MINI-TABLET)
 * @version 1.0
 * @author : Alexandre PLOUZENNEC
 */

(function($) {


  "use strict";


  /** ---------------------------------------------
   * SET LOCAL VARIABLES
   */

      // Cache jQuery object
  var slideProduct = $("#slideProduct"),
      slideshow = $("#slideshow"),
      slideProductPage = $("#slideProductPage"),
      slideshowBottom = $("#slideshowBottom"),

      // App
      spinnerApp = {},
      pageApp = {};


  /** ---------------------------------------------
   * SLIDER
   */

  // Initialized the slideshow product
  slideshow.owlCarousel({
    rewindSpeed: 0,
    navigation: true,
    pagination: true,
    mouseDrag: true,
    singleItem: true,
    theme: "owl-product-theme",
    afterInit: function() {
      slideshow.removeClass("visibleHidden");
    }
  });


  // Click event: jump to the big image
  slideProductPage.on("click", "a", function(evt) {
    var index = +$(this).data("slideIndex");

    // don't follow
    evt.preventDefault();
    slideshow.data("owlCarousel").jumpTo(index);
  });


  // Initialized the multi-product carousel
  slideshowBottom.owlCarousel({
    items: 2,
    itemsDesktop: [1000,2],
    itemsDesktopSmall: [940,1],
    itemsTablet: [600,1],
    itemsMobile: false,
    lazyLoad: true,
    autoPlay: 4000,
    stopOnHover: true,
    pagination: false,
    navigation: true,
    navigationText: ["", ""]
  });


  // Click event: show the previous items
  slideshowBottom.on("click", "prev", function(){
    slideshowBottom.trigger('owl.prev');
  });


  // Click event: show the next items
  slideshowBottom.on("click", "next", function(){
    slideshowBottom.trigger('owl.next');
  });



  /** ---------------------------------------------
   * SLIDE PAGE
   */

  // Page application
  pageApp = {

    // Initialize
    init: function() {
      this.slideProductLink = slideProductPage.find("> a");
      this.numberItem = this.slideProductLink.length;
      this.widthItem = this.slideProductLink.outerWidth(true);
      this.productPageWidth = this.widthItem * this.numberItem;
      this.itemFirst = this.slideProductLink.first();
      this.arrowLeft = slideProduct.find(".arrow-left");
      this.arrowRight = slideProduct.find(".arrow-right");
      this.isPagerAnimate = false;

      this.getMask();

      slideProductPage.width(this.widthItem * this.numberItem);
      this.arrowLeft.addClass("hidden");

      if (slideProductPage.width() <= this.maskWidth) {
        this.arrowLeft.addClass("hidden");
        this.arrowRight.addClass("hidden");

      } else {
        this.showPrev();
        this.showNext();
      }
    },

    // Click event: slide to the previous thumbnail
    showPrev: function() {
      pageApp.arrowLeft.on("click", function() {
        var pagerPos = slideProductPage.position().left;

        if (pagerPos === 0 && !pageApp.isPagerAnimate) {
          pageApp.arrowLeft.addClass("hidden");
          return;

        } else if (!pageApp.isPagerAnimate) {
          pageApp.isPagerAnimate = true;
          pageApp.arrowRight.removeClass("hidden");

          slideProductPage.stop().animate({
            left: "+=" + pageApp.widthItem
          }, 200, "linear", function() {
            pageApp.isPagerAnimate = false;
          });
        }
      });
    },

    // Click event: slide to the next thumbnail
    showNext: function() {
      pageApp.arrowRight.on("click", function() {
        var test = pageApp.getMask() - pageApp.productPageWidth,
            pagerPos = slideProductPage.position().left;

        if (pagerPos <= test && !pageApp.isPagerAnimate) {
          pageApp.arrowRight.addClass("hidden");
          return;

        } else if (!pageApp.isPagerAnimate) {
          pageApp.isPagerAnimate = true;
          pageApp.arrowLeft.removeClass("hidden");

          slideProductPage.stop().animate({
            left: "-=" + pageApp.widthItem
            }, 200, "linear", function() {
              pageApp.isPagerAnimate = false;
          });
        }
      });
    },

    // Get the mask width
    getMask: function() {
      this.maskWidth = $(".slide-mask").width();
      return this.maskWidth;
    }
  };

  // Initialized the page application
  pageApp.init();


  // Add breakpoint for large desktop
  $.breakpoint({
    condition: function () {
      return window.matchMedia('only screen and (min-width: 959px)').matches;
    },
    enter: function () {
      pageApp.getMask();
    }
  });


  // Add breakpoint for tablet and landscape mode
  $.breakpoint({
    condition: function () {
      return window.matchMedia('only screen and (min-width: 759px) and (max-width: 959px)').matches;
    },
    enter: function () {
      pageApp.getMask();
    }
  });


  // Add breakpoint for mini tablet and portrait mode
  $.breakpoint({
    condition: function () {
      return window.matchMedia('only screen and (min-width: 1px) and (max-width: 759px)').matches;
    },
    enter: function () {
      pageApp.getMask();
    }
  });



  /** ---------------------------------------------
   * BEGIN DETAIS STAY COLLAPSE OPTIONS
   */

  //
  $("#descriptionProduct").on("click", ".title", function(){
    var title = $(this),
        options = title.parents(".options");

    if (options.hasClass("close")) {
      options.removeClass("close");
    } else {
      options.addClass("close");
    }

  });


  //
  $(".block-benefits").on("click", ".link-more", function(){
    var title = $(this),
        options = title.parents(".block-benefits");

    if (options.hasClass("close")) {
      options.removeClass("close");
    } else {
      options.addClass("close");
    }

  });


  //
  $(".block-guests-recommend").on("click", ".link-more", function(){
    var title = $(this),
        options = title.parents(".block-guests-recommend");

    if (options.hasClass("close")) {
      options.removeClass("close");
    } else {
      options.addClass("close");
    }

  });


  //
  $(document).on("click", ".details-ratings .link-more", function(){
    var title = $(this),
        options = title.parents(".details-ratings");

    if (options.hasClass("close")) {
      options.removeClass("close");
    } else {
      options.addClass("close");
    }

  });



  /** ---------------------------------------------
   * BEGIN SPINNER APP
   */

  // Spinner application
  spinnerApp = {

    // Default options
    options: {
      classElement: "spinner"
    },

    // Initialize
    init: function(options) {
      this.opts = $.extend({}, this.options, options);
      this.elem = $("." + this.opts.classElement);

      // Add click event
      this.elem.on("click", this.onClick);
    },

    // Click event
    onClick: function(evt) {
      var elem = $(this),
          target = $(evt.target),
          input = elem.find(".spinner-value"),
          value = +input.val(),
          spinAdd = target.hasClass("spinner-add"),
          spinRemove = target.hasClass("spinner-remove");

      // When the value is equal to maximum or minimum, exit the method
      if (spinAdd && value === elem.data("spinnerMax") ||
          spinRemove && value === elem.data("spinnerMin")) {
        return;

      // Change the value and off spinner when necessary
      } else if (spinAdd || spinRemove) {
        input.val( spinAdd ? value += 1 : value -= 1 );
        elem.find(".disabled").removeClass("disabled");

        if (value === elem.data("spinnerMax") || value === elem.data("spinnerMin")) {
          target.addClass("disabled");
        }

        // update spinner's value
        input.attr('value', value);
        input.val(value);
        updateBookingStatus();
        updateUrl();
      }
    }
  };

  // Initialize spinnerApp
  spinnerApp.init();

})(jQuery);

