(function($, orx) {

  var orx_dp = orx.plugin.datepicker,
      popin = $("#wrapPopin").find(".popin");


  $.validator.addMethod("requiredValue", function(value, element) {return value !== '';},"");
  $.validator.addMethod("tPrice", function(value, element) {
  // allowed format: 10 || 10,25 || 10.25  || 1 025,3 || 1025,3 || -5,35
    var formatedVal = orxapi.removeSpacesFromNumber(value);
    return (value == "" || /^(\+|-)?[0-9]{1,10}([\,.][0-9]{1,2})?$/.test(formatedVal));
  }, "");
  $.validator.addMethod("dateFormat", function(value, element) {
      try {
        $.datepicker.parseDate("dd/mm/yy", value);
        return true;
      } catch (e) {
        return false;
      }
  }, "");

  // if sale > 0 : diff between sale and net must >= 0 if selected source was TO
  // if sale < 0 : diff between sale and net must be <=0 if selected source was TO
  $.validator.addMethod("tDiffSaleAndNetTO", function(value, element) {
    var $tr = $(element).parents("tr:first");
    if ($(element).is(":disabled") || $tr.find(".td_supplier select").val() != "") {
      // selected source was TO
      var saleIptVal = $tr.find(".td_sale input").val();
      var netTOIptVal = $tr.find(".td_buy input").val();
      saleIptVal = saleIptVal.replace(",", ".");
      saleIptVal = orxapi.removeSpacesFromNumber(saleIptVal);
      netTOIptVal = netTOIptVal.replace(",", ".");
      netTOIptVal = orxapi.removeSpacesFromNumber(netTOIptVal);
      if (saleIptVal > 0) {
        return (saleIptVal - netTOIptVal >= 0);
      } else {
        return (saleIptVal - netTOIptVal <= 0);
      }
    } else {
      return true;
    }
  }, "");

  // if sale > 0 : diff between brutTO and netTO must be >=0 if selected source was TO
  // if sale < 0 : diff between rutTO and netTO must be <=0 if selected source was TO
  $.validator.addMethod("tDiffGrossAndNetTO", function(value, element) {
    var $tr = $(element).parents("tr:first");
    if ($(element).is(":disabled") || $tr.find(".td_supplier select").val() != "") {
      // selected source was TO
      var saleIptVal = $tr.find(".td_sale input").val();
      var grossTOIptVal = $tr.find(".td_grossBuy input").val();
      var netTOIptVal = $tr.find(".td_buy input").val();
      saleIptVal = saleIptVal.replace(",", ".");
      saleIptVal = orxapi.removeSpacesFromNumber(saleIptVal);
      grossTOIptVal = grossTOIptVal.replace(",", ".");
      grossTOIptVal = orxapi.removeSpacesFromNumber(grossTOIptVal);
      netTOIptVal = netTOIptVal.replace(",", ".");
      netTOIptVal = orxapi.removeSpacesFromNumber(netTOIptVal);

      if (saleIptVal > 0) {
        return (grossTOIptVal - netTOIptVal >= 0);
      } else {
        return (grossTOIptVal - netTOIptVal <= 0);
      }
    } else {
      return true;
    }
  }, "");

  // ipts that are required if source is TO
  $.validator.addMethod("tReqIfTO", function(value, element) {
    var $tr = $(element).parents("tr:first");
    if (!$(element).is(':disabled') && $tr.find(".td_supplier select").val() != "") {
      // selected source was TO
      return value != "";
    } else {
      return true;
    }
  }, "");

  $.validator.addClassRules("notEmpty", {requiredValue:true});
  $.validator.addClassRules("date-4y", {requiredValue:true, dateFormat:true});
  $.validator.addClassRules("price", {tPrice:true});
  $.validator.addClassRules("diffSaleAndNetTO", {tDiffSaleAndNetTO: true});
  $.validator.addClassRules("diffGrossAndNetTO", {tDiffGrossAndNetTO: true});
  $.validator.addClassRules("reqIfTO", {tReqIfTO: true});

  $("#ppn_file-quoteForm").validate({
      // ignore in global validation
      ignore: ".tr_adder :input"
  });

  // calculate total sums par default
  calculateAllTotalSums();


  popin.find(".datePicker").datepicker($.extend({}, orx_dp, { minDate: 0 }));
  orx.pictoDate();

  // process create/edit quote ppn
  popin.find("#btn_editModif").click(function(event) {

    var currentModifId = $("#modifId").val();

    if (!$("#ppn_file-quoteForm").valid()) {
        return false;
    }

    // change all values from format xx,xx to format xx.xx
    orxapi.changeToPointFormat($("#ppn_file-quoteForm").find("tr.tr_modified input.price"));

    // seralize with ognl fix: if value is empty dont include it.
    var params = $("#ppn_file-quoteForm").serialize();

    $("#ppn_file-quoteForm").find("tr.tr_modified input[disabled]").each( function() {
        params = params + '&' + $(this).attr('name') + '=' + $(this).val();
    });

    // ognl fix: remove empty values from the list of params
    params = params.replace(/[^&]+=\.?(?:&|$)/g, '');

    $("#wrapPopin").remove();
    $.ajax($.extend({}, orxapi.ajax, {
      url : "file-quoteProcess.action",
      data: params,
      success : function(data, textStatus, jqXHR) {
        // reload modification list
        $("#innerWrapContent .wrapLeft").find("div.inner").empty();
        $("#innerWrapContent .wrapLeft").find("div.inner").html(data);
        $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-modifsList.js");

        // if it was edition of modification, click the block that was edited.
        if (currentModifId != null && currentModifId != "") {
          $("#modificationListZone").find(".active").removeClass("active");
          $("#innerWrapContent").find(".modificationBlock[data-modif-id='" + currentModifId + "']").addClass("active")
        } else {
          // creation of modification: click the 1st block
          $("#innerWrapContent").find(".modificationBlock:eq(0)").trigger("click");
        }

      }
    }));

  });


  /** duplicate tarif */
  $(".duplicateQuote").click(function(evt) {
    evt.preventDefault();
    var $adderTr = $("#ppn_file-quoteForm .tr_adder");
    var $curTr = $(this).parents("tr");
    $curTr.find("td").each(function () {
      if ($(this).attr("data-value")) {
        // set values in adder tr and simulate adding of new quote
        var curTdClass = $(this).attr("class");
        if ($adderTr.find("." + curTdClass).length) {
          // in added we have currentTd. add its value to id.
          var origVal = $(this).data("value");
          var $targetTd = $adderTr.find("." + curTdClass);
          if ($targetTd.find("select").length) {
            $targetTd.find("select option[value='" + origVal + "']").prop("selected", true);
            $targetTd.find("select").trigger("change");
          } else if ($targetTd.find("input").length) {
           $targetTd.find("input").val(origVal);
          }
        }
      }
    });
    $adderTr.find("button.addOneRow").click();
  });



  /*--------------------------------------------------------------------------------*/
  /*                 PRICE CALCULATIONS                                             */
  /*--------------------------------------------------------------------------------*/


  // provider change event : if td selected, no buy, comission and markup (they are disabled and null in db)
  $("#ppn_file-quoteForm").on("change", ".td_supplier select", function() {
    var $currTr = $(this).parents("tr"),
         selectedVal = $currTr.find("td.td_supplier :selected").val(),
         selectedType = $currTr.find(".td_type :selected").val();

    if (selectedVal == '') {
      // TD selected : no qte, unit amount, brut to, net to, commission
      if (selectedType != "TAX") {
        $currTr.find(".td_quantity select").val("").trigger("change");
        $currTr.find(".td_grossBuy input").val("").prop("disabled", true).removeClass("error");
        $currTr.find(".td_buy input").val("").prop("disabled", true).removeClass("error");
        $currTr.find(".td_com input").val("");
        $currTr.find(".td_markup input").val("");
      }
      calculateAllTotalSums();
    } else {
      // TO selected : if TAX, LIT or GCO disable brut and net (values inside are forced to be the same as vente), else not
      $currTr.find(".td_quantity select").prop("disabled", false);
      if (selectedType != "TAX" && selectedType != "LIT" && selectedType != "GCO") {
        $currTr.find(".td_grossBuy input").prop("disabled", false);
        $currTr.find(".td_buy input").prop("disabled", false);
      }
    }
  });
  // trigger source select change by default
  $("#ppn_file-quoteForm .td_supplier select").each(function() {
    $(this).trigger("change");
  });

  // type change event
  $("#ppn_file-quoteForm").on("change", ".td_type select", function() {
    var $currTr = $(this).parents("tr");

    // change insurance label
    var $currSource = $currTr.find(".td_supplier select option[value='']");
    $(this).val() == "INS" ? $currSource.text($(".tr_adder").data("inslabel")) : $currSource.text($(".tr_adder").data("tdlabel"));

    // if type is TAX hide null quantity
    var $quantityNullOpt = $currTr.find(".td_quantity select option[value='']");
    if ($(this).val() == "TAX") {
      var selectedQtte = $currTr.find(".td_quantity :selected").val();
      if (selectedQtte == "") {
        // select qtte 1 and unit amount same as sale
        $currTr.find(".td_quantity option[value='1']").prop("selected", true);
        $currTr.find(".td_sale input").prop("disabled", true);
        $currTr.find(".td_unit input").prop("disabled", false);
        $currTr.find(".td_unit input").val($currTr.find(".td_sale input").val());
      }
      $quantityNullOpt.addClass("hidden");
    } else {
      $quantityNullOpt.removeClass("hidden");
    }

    // if type is TAX or LIT or GCO: disable brut and net
    if ($(this).val() == "TAX" || $(this).val() == "LIT" || $(this).val() == "GCO") {
        $currTr.find(".td_grossBuy input").prop("disabled", true);
        $currTr.find(".td_buy input").prop("disabled", true);

        if ($(this).val() == "TAX") {
          // force the same value as vente
          var saleValue = $currTr.find(".td_sale input").val();
          $currTr.find(".td_grossBuy input").val(saleValue);
          $currTr.find(".td_buy input").val(saleValue);
        }
        orxapi.calculTotalSum("td_totalGrossBuy", $currTr.find(".td_grossBuy input"));
        orxapi.calculTotalSum("td_totalBuy", $currTr.find(".td_buy input"));

    } else if ($currTr.find("td.td_supplier :selected").val() != '') {
      // TO source was selected
      $currTr.find(".td_grossBuy input").prop("disabled", false);
      $currTr.find(".td_buy input").prop("disabled", false);
    }

  });

  // trigger type select change by default
  $("#ppn_file-quoteForm .td_type select").each(function() {
    $(this).trigger("change");
  });

  // quantite change event
  $("#ppn_file-quoteForm").on("change", ".td_quantity", function() {
    var $currTr = $(this).parent();
    var selectedVal = $currTr.find("td.td_quantity :selected").val();
    selectedVal == "" ? $currTr.find(".td_sale input").prop("disabled", false) : $currTr.find(".td_sale input").prop("disabled", true);
    orxapi.calculSaleAmount($currTr);
  });

  // unit price change
  $("#ppn_file-quoteForm").on("change", "td.td_unit input", function() {
    var $currTr = $(this).parents("tr");
    if ($(this).valid()) {
      orxapi.calculSaleAmount($currTr);
      if ($currTr.find(".td_sale input").valid() && $currTr.find("td.td_supplier :selected").val() != '') {
        orxapi.calculMarkup($currTr);
      }
    }
  });

  // sale price change
  $("#ppn_file-quoteForm").on("change", "td.td_sale input", function() {
    if ($(this).valid()) {
      orxapi.calculTotalSum("td_totalSale", $("#ppn_file-quoteForm tbody tr.tr_added").find(".td_sale input"));
      var $currTr = $(this).parents("tr"),
          currSelectedType = $currTr.find(".td_type :selected").val(),
          currValue = $(this).val();
      if (currSelectedType == "TAX") {
        // if selected type was TAX then brut and net price are forced to be same as sale price
        $currTr.find(".td_grossBuy input").val(currValue);
        $currTr.find(".td_buy input").val(currValue);
        orxapi.calculTotalSum("td_totalGrossBuy", $currTr.find(".td_grossBuy input"));
        orxapi.calculTotalSum("td_totalBuy", $currTr.find(".td_buy input"));
      }
      if ($currTr.find(".td_grossBuy input").valid() && $currTr.find("td.td_supplier :selected").val() != '') {
        // recalcul those values if provider is TO
        orxapi.calculMarkup($currTr);
      }
    }
  });

  // buy (net TO) price change
  $("#ppn_file-quoteForm").on("change", "td.td_buy input", function() {
    orxapi.calculTotalSum("td_totalBuy", $("#ppn_file-quoteForm tbody tr.tr_added").find(".td_buy input"));
    orxapi.calculCommision($(this).parents("tr"));
  });

  // buy (brut TO) price change
  $("#ppn_file-quoteForm").on("change", "td.td_grossBuy input", function() {
    orxapi.calculTotalSum("td_totalGrossBuy", $("#ppn_file-quoteForm tbody tr.tr_added").find(".td_grossBuy input"));
    orxapi.calculMarkup($(this).parents("tr"));
    orxapi.calculCommision($(this).parents("tr"));
  });

  /** Calculate all total sums in last tr */
  function calculateAllTotalSums() {
    orxapi.calculTotalSum("td_totalSale", $("#ppn_file-quoteForm tbody tr.tr_added").find(".td_sale input"));
    orxapi.calculTotalSum("td_totalGrossBuy", $("#ppn_file-quoteForm tbody tr.tr_added").find(".td_grossBuy input"));
    orxapi.calculTotalSum("td_totalBuy", $("#ppn_file-quoteForm tbody tr.tr_added").find(".td_buy input"));
    orxapi.calculTotalSum("td_totalMarkup", $("#ppn_file-quoteForm tbody tr.tr_added").find(".td_markup input"));
    orxapi.calculTotalSum("td_totalCom", $("#ppn_file-quoteForm tbody tr.tr_added").find(".td_com input"));
  }

  /*--------------------------------------------------------------------------------*/
  /*                 MODIFICATION TABLE BEHAVIOUR                                   */
  /*--------------------------------------------------------------------------------*/

  /* Add new row */
  $("#quoteZone").find("button.addOneRow").click(function(evt) {
    evt.preventDefault();
    var $tr = $(this).parents("tr.tr_adder");
    if ($tr.allIptsValid()) {
      $tr.addTableRow();
      orxapi.resetValues($tr);
      $("#slt_type").trigger("change");
      $("#slt_source").trigger("change");
      calculateAllTotalSums();
    }

    return false;
  });

  // Cancel existing modification
  $("#ppn_file-quoteForm").on("click", "button.cancelOneModif", function() {
    var $curTr = $(this).parents("tr:eq(0)");
    $curTr.remove();
    calculateAllTotalSums();
    return false;
  });

  // Cancel existing modification
  $("#ppn_file-quoteForm").on("click", "button.duplicateQuote", function() {
    return false;
  });


  $.fn.addTableRow = function () {
    var $tr = $(this),
        $table = $(this).parents("table:eq(0)"),
        trIndex = $table.find("tbody tr.tr_modified").length,
        modifSource = $table.data("modif") + "[" + trIndex + "]",
        modifSourceIdName = $table.data("target-id-name");

    var $clonedTr = $tr.clone();
    $clonedTr.removeClass().addClass("tr_added tr_modified");
    orxapi.addSpecificCancelBtn($clonedTr, "icon-plus");

    var $tdsToChange = $clonedTr.find("td").not(".td_action, .td_date");
    $tdsToChange.each(function(i) {

      var $this = $(this),
          sourceTd = $(this).attr("class"),
          $changedField = $this.find("input,select"),
          $targetField = $table.find("tr.tr_adder").find("." + sourceTd).find("input,select");

      // fix: cloned select fields are not keeping original value
      $changedField.val($targetField.val());
      $changedField.attr("name", $changedField.data("name").replace("$$", trIndex));
      $changedField.attr("id", $changedField.attr("id")+"[" + trIndex + "]");
      $changedField.removeData();
    });

    $tr.before($clonedTr);
  }

  /** Check if all inputs are valid */
  $.fn.allIptsValid = function () {
    var valid = true;
    $(this).find("input").each(function () {
      var currIpt = $(this);
      if (!currIpt.valid()) {
        valid = false;
        return false;
      }
    })
    return valid;
  }

  // if we are in view mode disable all inputs
  if ($("#viewMode").text() == "true") {
    $("#ppn_file-quoteForm").find("input, select").prop("disabled", true);
  }

})(jQuery, orxapi);

