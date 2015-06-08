/*!
 * jquery.orx.utils.size
 *
 * @project   Orchestra libraries
 * @version   1.0.0
 * @author    emmanuel.sammut
 */

(function($){

  "use strict";

  /**
   * Get maximum size of all elements
   *
   * @param {String} [value] all set dimension method, height by default.
   * @param {Boolean} [outerOption] true parameter for outer method.
   * @return {Number} maximum size of all elements.
   *
   * use: $(".row").getMaxSize();
   *      $(".col").getMaxSize("width");
   *      $(".col").getMaxSize("innerHeight");
   *      $(".col").getMaxSize("outerWidth", true);
   */
  $.fn.getMaxSize = function(value, outerOption) {

    var aMax = [];

    // Iterate into the elements, get size and set array
    this.each(function(index) {
      aMax[index] = outerOption ? $(this)[value](outerOption) : $(this)[value || "height"]();
    });

    // Sort array numerically and descending
    aMax.sort(function(a, b){
      return b - a;
    });

    return aMax[0];

  };


  /**
   * Set equal height or width size for all elements
   *
   * @require getMaxSize method
   * @param {String} [value] all set dimension method, height by default.
   * @param {Boolean} [outerOption] true parameter for outer method.
   * @return {Object} jQuery
   *
   * use: $("#container").
   *        find(".row1").equalSize().end().
   *        find(".row2").equalSize("innerHeight").end().
   *        find(".row3").equalSize("outerWidth", true);
   */
  $.fn.equalSize = function(value, outerOption) {

    value = value || "height";

    // Get the max size of the value elements
    var max = $(this).getMaxSize(value, outerOption);

    // Iterate into the elements and set height or width.
    return this.each(function() {
      $(this)[value](max);
    });

  };

})(jQuery);
