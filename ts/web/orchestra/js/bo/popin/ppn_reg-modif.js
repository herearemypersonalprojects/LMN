(function($, orx) {

  $.validator.addMethod("requiredValue", function(value, element) {return value !== '';},"");
  $.validator.addMethod("cityCode", function(value, element) { return value.length == 3; },"");
  $.validator.addMethod("min3", function(value, element) { return value.length >= 3; },"");
  $.validator.addMethod("iptsPresentOrNot", function(value, element) {
      var $ipt1 = $(element).parents(".surchargeZone").find("input[type!='hidden'][name$='totalAmount']"),
          $ipt2 = $(element).parents(".surchargeZone").find("input[type!='hidden'][name$='proGrossAmount']"),
          $ipt3 = $(element).parents(".surchargeZone").find("input[type!='hidden'][name$='proAmount']"),
          valid = validateFuelPrices($ipt1.val(), $ipt2.val(), $ipt3.val());
      if (!valid) {
        $ipt1.addClass("error")
        $ipt2.addClass("error")
        $ipt3.addClass("error")
      } else {
        $ipt1.removeClass("error")
        $ipt2.removeClass("error")
        $ipt3.removeClass("error")
      }
      return valid;
  }, "");
  $.validator.addMethod("tPrice", function(value, element) {
  // allowed format: 10 || 10,25 || 10.25  || 1 025,3 || 1025,3 || -5,35
    var formatedVal = orxapi.removeSpacesFromNumber(value);
    return (value == "" || /^(\+|-)?[0-9]{1,10}([\,.][0-9]{1,2})?$/.test(formatedVal));
  }, "");

  // difference between brutTO and netTO must be positive if selected source was TO
  $.validator.addMethod("tDiffGrossAndNetTO", function(value, element) {
    var $currDiv = $(element).parents("div.surchargeZone"),
        grossTOIptVal = $currDiv.find(".proGrossBuy input").val(),
        netTOIptVal = $currDiv.find(".proBuy input").val();
    grossTOIptVal = grossTOIptVal.replace(",", ".");
    grossTOIptVal = orxapi.removeSpacesFromNumber(grossTOIptVal);
    netTOIptVal = netTOIptVal.replace(",", ".");
    netTOIptVal = orxapi.removeSpacesFromNumber(netTOIptVal);
    return (grossTOIptVal - netTOIptVal >= 0) || (grossTOIptVal == "" && netTOIptVal == "");
  }, "");

  $.validator.addClassRules("notEmpty", {requiredValue:true});
  $.validator.addClassRules("cityCode", {cityCode:true});
  $.validator.addClassRules("minLength", {min3:true});
  $.validator.addClassRules("iptsPresentOrNot", {iptsPresentOrNot:true});
  $.validator.addClassRules("price", {tPrice:true});
  $.validator.addClassRules("diffGrossAndNetTO", {tDiffGrossAndNetTO: true});

  $("#ppn_file-modifProcessForm").validate({
      // ignore in global validation
      ignore: ".ignoreGlobalValidation :input"
  });

  var orx_dp = orx.plugin.datepicker,
      popin = $("#wrapPopin").find(".popin");

  popin.find(".datePicker").datepicker($.extend({}, { }, orx_dp));
  orx.pictoDate();

  /* resize popin */
  orx.resizePopin();
  $(window).resize(function(){
    orxapi.resizePopin();
  });

  /** Search products by quickRef */
  $("#productChange").find(".btn_search").click(function(evt) {
    evt.preventDefault();
    if (!$("#ipt_name").valid()) {
      return false;
    }
    var quickRef = $.trim($("#ipt_name").val()),
        provider = $("#slt_to").val(),
        providerLabel = $("#searchedProviderLabel").val();
    $.ajax($.extend({}, orxapi.ajax, {
      url : "prepareModifProductList.action",
      data: {provider:provider, quickRef:quickRef, providerLabel:providerLabel},
      success : function(data, textStatus, jqXHR) {
        $("#productListContainer").html(data);
      }
    }));
  });

  /** Click on product in the table list */
  $("#productListContainer").on("click", "table tbody tr", function() {
    if ($(this).hasClass("success")) {
      $(this).removeClass("success");
    } else {
      $(this).parent().find(".success").removeClass("success");
      $(this).addClass("success");
    }
    // enable all inputs in this tr and disable in all other trs
    $(this).find("input").prop("disabled", false);
    $(this).parents("table:eq(0) tbody").find("tr").not(".success").find("input").prop("disabled", true);
  });

  /** process (validate) multi regulation (go to mail popin) */
  popin.find("#btn_processRegModif").click(function(event) {

    event.preventDefault();

    // global jquery validation
    if (!$("#ppn_file-modifProcessForm").valid()) {
      return false;
    }
    // check if there's at least one field not empty
    var $form = $("#ppn_file-modifProcessForm");
    // first price elements
    var notEmpty = false;
    $form.find("#fdt_surcharge").find("input[type!='hidden']").each(function() {
      var $currInput = $(this);
      if ($currInput.val() != "" && $currInput.attr("value") != "") {
        notEmpty = true;
        return;
      }
    });
    if (!notEmpty) {
      // second presta
      $form.find("#fdt_product").find("dd:not(.productList)").find("input[type!='hidden']").each(function() {
        var $currInput = $(this);
        if ($currInput.val() != "undefined" && $currInput.val() != "" && $currInput.attr("value") != "") {
          notEmpty = true;
          return;
        }
      });
      if (!notEmpty) {
        // third select box
        var $sltFormula = $("#slt_formula");
        notEmpty = $sltFormula.attr("name") != "" && $sltFormula.find(":selected").html() != "";
        if (!notEmpty) {
          // fifth product
          notEmpty = $form.find("#productListContainer").find("tr.active") != null
              && $form.find("#productListContainer").find("tr.active").length == 1;

          if (!notEmpty) {
            // display message
            var validMsg = $form.data("valid-msg");
            var contentDialog = $("<div/>",{ "id":"contentDialog"});
            contentDialog.html(validMsg);

            contentDialog.dialog($.extend({}, orx.plugin.dialog, {
                  buttons:{
                    "Fermer": function() {
                      $(this).dialog("destroy").remove();
                      return false;
                     }
                  }
            }));
            return false;
          }
        }
      }
    }


    var params = $("#ppn_file-modifProcessForm").serialize();

    // ognl fix: remove empty values from the list of params
    params = params.replace(/[^&]+=\.?(?:&|$)/g, '');

    $.ajax($.extend({}, orxapi.ajax, {
      url : "reg-modifProcess.action",
      data: params,
      success : function(data, textStatus, jqXHR) {
        $("#wrapPopin").find(".popin").replaceWith(data);
        orxapi.resizePopin();
        $.getScript("./shared/ts/web/js/bo/popin/ppn_reg-modif-mail.js");
        $("#wrapPopin").find(".replyZone").removeClass("hidden");
        /* add light wysiwyg on #bodyMail */
        tinyMCE.init($.extend({
          elements : "bodyMail",
          resizable:true,
          theme_advanced_resizing : true,
          theme_advanced_resize_horizontal : false
        }, orxapi.plugin.wysiwygLight));
      }
    }));



  });

  // autocomplete modification departure city
  $("#ipt_depCity").tsautocomplete({
    autocomplete : {
      focus : function(event, ui) {
        $(this).val(ui.item.code);
        return false;
      },
      select : function(event, ui) {
        $(this).val(ui.item.code);
        ;
        return false;
      },
      source : function(request, response) {
        $.post("autocompleteDeparture", $.extend(request, {
          code : this.element.val()
        }), function(data) {
          response(data);
        }, "json");
      }
    },
    type : false
  });

  // insurance radio btn click
  $("#fdt_insurance").on("click", "input:radio", function() {
    // current selection is not disabled. all others are.
    var $clickedRadio = $(this);
    $clickedRadio.parents("dd").find("input:hidden").prop("disabled", true);
    $clickedRadio.parent().find("input:hidden").prop("disabled", false);
  });



  /*--------------------------------------------------------------------------------*/
  /*                 MODIFICATION TABLE BEHAVIOUR                                   */
  /*--------------------------------------------------------------------------------*/


  /* Add new row */
  $("#ppn_file-modifProcessForm").find("button.addOneRow").click(function(evt) {
    evt.preventDefault();
    var $tr = $(this).parents("tr.tr_adder");
    orxapi.addTableRow($tr);
    orxapi.resetValues($tr);
    return false;
  });

  /* edit row from dropdown menu */
  $(".popin").on("click", "li.editRow", function() {
    orxapi.editTableRow($(this).parents("tr:eq(0)"), "MODIFY");
  });

  /* delete row from dropdown menu */
  $(".popin").on("click", "li.deleteRow", function() {
    orxapi.deleteTableRow($(this).parents("tr:eq(0)"));
  });

  /* replace row from dropdown menu */
  $("body").on("click", "li.replaceRow", function() {
    orxapi.editTableRow($(this).parents("tr:eq(0)"), "REPLACE");
  });

  // Cancel existing modification
  $(".popin").on("click", "button.cancelOneModif", function() {
    var $curTr = $(this).parents("tr:eq(0)");
    if ($curTr.hasClass("tr_added")) {
      $curTr.remove();
    } else {
      $curTr.addClass("canceled");
      orxapi.undoEditionOfTableRow($curTr);
    }
    return false;
  });

  // formula select box behaviour
  $("#slt_formula").change(function() {
    var $selected = $(this).find(":selected");
    // show input only in the case of 'Autre' not for empty select
    var selOptVal = $selected.val();
    var selOptHtml = $selected.html();
    if (selOptVal == "" && selOptHtml != "") {
      // other was selected. show input
      $("#slt_formula").attr("name","");
      $("#ipt_formula").attr("name","boFileModif.modifFormula");
      $("#otherFormula").removeClass("hidden");
    } else {
      $("#otherFormula").addClass("hidden");
      $("#slt_formula").attr("name","boFileModif.modifFormula");
      $("#ipt_formula").attr("name","");
    }
  });



  /*--------------------------------------------------------------------------------*/
  /*                       VALIDATION OF MODIFICATIONS                              */
  /*--------------------------------------------------------------------------------*/

  $.fn.validateModification = function () {
    var $form = $(this),
        validMsg = "";

    // 1st validation level: check if atleast 1 tr in tables is in edit mode
    validMsg = orxapi.validateTrInEditMode($form);
    if (validMsg == "") {
      // 2nd validation level: check if input values are different from initial ones
      validMsg = $form.validateModifValues();
    }
    return validMsg;
  };

  $.fn.validateModifValues = function () {
    var validMsg = "";
    var $form = $(this);

    /* Check product zone */
    validMsg = orxapi.validateProductZone($form);
    if (validMsg != "") {
      return validMsg;
    }

    /* Check table zones */
    validMsg = orxapi.validateTableZones($form);
    if (validMsg != "") {
      return validMsg;
    }

    /* Check formula */
    validMsg = orxapi.validateFormulaZone($form);
    if (validMsg != "") {
      return validMsg;
    }

    /* Check insurance */
    validMsg = orxapi.validateInsuranceZone($form);
    if (validMsg != "") {
      return validMsg;
    }

    return validMsg;
  };

  // if we are in view mode disable all inputs
  if ($("#viewMode").text() == "true") {
    $("#ppn_file-modifProcessForm").find("input, select").prop("disabled", true);
  }

  function validateFuelPrices(ipt1Val, ipt2Val, ipt3Val) {
    var valid = false;
    if ((ipt1Val !="" && ipt2Val !="" && ipt3Val !="") || (ipt1Val == "" && ipt2Val == "" && ipt3Val == "")) {
      valid = true;
    }
    return valid;
  }


  // SPECIFIC FOR MULTI REGULATION (DESIGN)

  popin.find(".btn_mailRegulation").click(function() {
    var that = $(this);
    $("#wrapPopin").animate({"padding-top": "+=90px" }, 150, "easeInCubic", function(){
      $("#wrapPopin").remove();
      orxapi.madePopin(that.data("page"), "pge_reg");
      orxapi.resizePopin();
    });
  });

  $(".iCheckBox").switchbutton({
      classes: 'ui-switchbutton-thin',
      duration: 100,
      labels: false
  });

})(jQuery, orxapi);

