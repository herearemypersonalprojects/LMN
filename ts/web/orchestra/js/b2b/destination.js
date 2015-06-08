$(function() {

  $(document).on("change", "select.country", function() {
    // Get the country code
    var countryCode = getSelectValue($(this));

    var form = $(this).parents(".advSearchForm");

    var formCode;
    if (form.find("#typeFormZone").find("#lst_formType").length) {
      // we are in the list search. use the value.
      formCode = form.find("#typeFormZone").find("#lst_formType").find("option:selected").val();
    } else {
      // we are in the home page and it is tab inteface
      formCode = form.find("#typeFormZone").find("#tab_formType li.active").find("#formCode").data("value");
    }

    var citySelect = form.find("select.city");

    // Load cities
    if (countryCode != "") {
      citySelect.loadDestinations(countryCode, formCode);
    } else {
      citySelect.reset();
      citySelect.attr('disabled', 'disabled');
    }
  });

});

/**
 * Returns the value of the given "select" element, "" otherwise
 */
function getSelectValue(elt) {
  var value = "";
  if (elt.find("option:selected").length > 0) {
    value = elt.find("option:selected").val();
  }
  return value;
}

/**
 * Loads the destinations on the given elements.
 * Concatenation (+=) is very long in IE7. Thats why we re using array's push method for
 * constructing the dString with for loop.
 */
$.fn.loadDestinations = function(countryCode, formCode, selectedCityCode) {
  return $(this).each(function(i, slt) {

    // Reset the select
    var $slt = $(slt).reset();

    $.ajax($.extend({}, orxapi.ajax, {
      url : "getCities.action",
      data : {code: countryCode, formCode : formCode},
      success : function(data) {
        var dString = [];
        var destinations = $.parseJSON(data);
        var destLength = destinations.length;
        for (var a = 0; a <destLength; a++) {
          dString.push("<option value=\"" + destinations[a].code + "\">" + destinations[a].label + "</option>");
        };
        if (destLength > 0) {
            $slt.prop("disabled", false);
            for (var b = 0; b <destLength; b++) {
              $(dString[b]).appendTo($slt);
            }
        } else {
          $slt.prop("disabled", true);
        }
        if ($slt.data("selectedValue")) {
          $slt.val($slt.data("selectedValue")).change();
          $slt.children("option[value^='" + $slt.data("selectedValue") + "']").prop("selected", true).end().removeData("selectedValue");
        }
        if (selectedCityCode) {
          $slt.val(selectedCityCode);
        }
      }
    }));

  });
};

/**
 * Resets the given elements.
 */
$.fn.reset = function() {
 return $(this).each(function() {
   if ($(this).children().length > 0) {

     // Find the first child
     var $firstChild = $(this).children(":first");

     // If it has no value, keep it, otherwise empty everyting
     if ($firstChild.val() == "") {
       $(this).html($firstChild);
     } else {
       $(this).empty();
     }

     $(this).removeClass("selectedsearch");
   }
 });
};