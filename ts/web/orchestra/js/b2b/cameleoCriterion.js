$(function() {

  $(document).on("change", "select.cameleoMaster", function() {
    // Get the criteria code
    var criteriaCode = $(this).data("field-value"),
        masterLevelsNumber = $(this).data("master-levels"),
        critValue = getSelectValue($(this)),
        formCode,
        slave = $("#slt_cameleoDouble_slave_" + criteriaCode),
        form = $(this).parents(".advSearchForm");

    if (form.find("#typeFormZone").find("#lst_formType").length) {
      // we are in the list search. use the value.
      formCode = form.find("#typeFormZone").find("#lst_formType").find("option:selected").val();
    } else {
      // we are in the home page and it is tab inteface
      formCode = form.find("#typeFormZone").find("#tab_formType li.active").find("#formCode").data("value");
    }
    // Do not load / reset the slave if we did not click on the last level
    var critTab = critValue.split(".");
    var selectedMasterLevel = critTab.length;

    // Load cities
    if (criteriaCode != "" && critValue != "") {
      slave.loadSlaves(criteriaCode, critValue, formCode, selectedMasterLevel);
    } else {
      slave.reset();
      slave.attr('disabled', 'disabled');
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
$.fn.loadSlaves = function(criteriaCode, critValue, formCode, selectedMasterLevel, selectedSlaveCode) {
  return $(this).each(function(i, slt) {
    // Reset the select
    var $slt = $(slt).reset();

    $.ajax($.extend({}, orxapi.ajax, {
      url : "getCameleoCriterionSlaves.action",
      data : {code: criteriaCode, value : critValue, formCode : formCode},
      success : function(data) {
        var slavesString = [];
        var slaves = $.parseJSON(data);
        var slavesLength = slaves.length;
        for (var a = 0; a < slavesLength; a++) {
          // do not indent the first level of children
          var indentationCount = slaves[a].level - selectedMasterLevel - 1;
          var indentation = "";
          if (indentationCount && indentationCount > 0) {
            for (var i = 0; i < indentationCount; i++) {
              indentation += "&nbsp;&nbsp;&nbsp;&nbsp;";
            }
          }
          slavesString.push("<option value=\"" + slaves[a].code + "\">" + indentation + slaves[a].label + "</option>");
        };
        if (slavesLength > 0) {
          $slt.removeProp("disabled");
            for (var b = 0; b < slavesLength; b++) {
              $(slavesString[b]).appendTo($slt);
            }
        }
        else {
            $slt.prop("disabled", true);
        }
        if ($slt.data("selectedValue")) {
          $slt.val($slt.data("selectedValue")).change();
          $slt.children("option[value^='" + $slt.data("selectedValue") + "']").prop("selected", true).end().removeData("selectedValue");
        }
        if (selectedSlaveCode) {
          $slt.val(selectedSlaveCode);
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