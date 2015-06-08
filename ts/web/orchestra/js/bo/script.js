(function($, orx){

  /* Click on a subFile reference to open the related producer file */
  $("#bodyContent").on("click", ".subFileReference", function() {
    var fileReference = $(this).data("file-reference");
    // load the page and create the tab
    openFilePageByRef(fileReference, true);
 });

  /* Click on a distributor file reference to open the related distrib. file */
  $("#bodyContent").on("click", ".distribFileReference", function() {
    var fileReference = $(this).data("file-reference");
    // load the page and create the tab
    openFilePageByRef(fileReference, false);
 });

  /** Save favorite search */
  $("#bodyContent").on("click", ".btn_saveResult", function() {

    var searchRequest = $(this).data("search-request");
    var type = $(this).data("type");

    //var pge = "pge_list/pge_resultList";
    $.ajax($.extend({}, orxapi.ajax, {
      url : "processSaveUserSearch.action",
      data : {searchRequest:searchRequest, type:type},
      success : function(data, textStatus, jqXHR) {
        // result is <li> of saved user search
        if ($("#favoriteUser ul.itemList").find("div.alert-info").length) {
          $("#favoriteUser ul.itemList").find("div.alert-info").remove();
        }
        $("#favoriteUser ul.itemList").append(data);
      }
    }));
    return false;

  })

  /* Search form: canal,organization, user select behaviour */
  $("#bodyContent").on("change", ".canalIpt", function() {
    $(".orgIpt").attr("disabled","disabled");
    $(".userIpt").attr("disabled","disabled");
    if ($(".canalIpt").val()!="") {
      orxapi.completeOrg('','');
    } else{
      orxapi.emptyOrganizationSelectBox($(this).data("contrologra"));
      orxapi.emptyUserSelectBox();
    }
  });

  $("#bodyContent").on("change", ".orgIpt", function() {
    $(".userIpt").attr("disabled","disabled");
    if ( $(".orgIpt").val()!=""){
      orxapi.completeUser('','');
    } else{
      orxapi.emptyUserSelectBox();
    }
  });

  /* Reset click */
  $("#bodyContent").on("click", ".resetBtn", function() {

      var $this = $(this),
          $form = $this.parents("form");

      orxapi.resetFormGeneric($form);

      var orgIpt =  $form.find(".orgIpt");
      if (orgIpt.length) {
        if(!orgIpt.data("controlchannel")) {
          orgIpt.prop("disabled", true);
        }
        if(!orgIpt.data("controlorga")) {
          orgIpt.prop("disabled", true);
        }
      }

      // marketing checkboxes
      var checkBoxes = $form.find("input:checkbox");
      checkBoxes.parents(".cbxCodeZone").find("input:text").attr("disabled","disabled");
      checkBoxes.parents(".cbxCodeZone").find("input:text").removeClass("lightValue");
      checkBoxes.parents(".depCityZone").find("input:text").prop("disabled", false);

      orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch", "");
      $("#resultTable").empty();

      return false;
  });

  /* Advanced distributor and producer search click */
//  $("#bodyContent").on("click", ".searchBtn", function() {
//    var params = $(".advSearchForm").serialize();
//    var url = "processAdvSearch.action";
//    var pge = "pge_list/pge_resultList";
//
//    if ($(".advSearchForm").valid()) {
//
//      $.ajax($.extend({}, orxapi.ajax, {
//        url : url,
//        data : params,
//        success : function(data, textStatus, jqXHR) {
//          $("#resultTable").html(data);
//
//          //Depending on the search (DISTRIBUTOR or PRODUCER), set the correct data in the list tab
//          var fileType = $("#ipt_fileType").val();
//          if(fileType == "DISTRIBUTOR") {
//              orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch", params);
//          } else if (fileType == "PRODUCER") {
//              orxapi.selectors.tabsBarZone.find("li.active").data("lastProducerSearch", params);
//          }
//
//          $.getScript("./shared/ts/web/js/bo/" + pge + ".js");
//          orxapi.storeTabCookie();
//        }
//      }));
//    }
//    return false;
//  });


  //Print the file
  $("body").on("click", ".print_btn", function() {
    var params = "id=" + $(this).data("id");
    var action = $(this).data("page") + ".action?" + params;
    var popup = window.open(action, "", "width=1020,height=700,scrollbars=1");
  });


  /** Rejouer devis */
  $("body").on("click", ".submitRecreation", function(e) {
    e.preventDefault();
    var $this = $(this);

    if ($this.hasClass("pusblishedProductExists") == false) {
      // we came here from result table. There we didnt check if published prod exitst in MO (search performance).
      // Check it now. if not : dialog
      var id = $this.parents("tr").data("id");
      $.ajax($.extend({}, orxapi.ajax, {
        url : "receivePublishedProduct.action",
        async : false,
        data : {id:id},
        success : function(data, textStatus, jqXHR) {
          if (data == null || data != "success") {
            $this.parents(".btn-group").removeClass("open");
            // dialog
            var contentDialog = $("<div/>",{ "id":"contentDialog"});
            contentDialog.html(data);
            contentDialog.dialog($.extend({}, orx.plugin.dialog, {
                buttons:{
                  "Fermer": function() {
                    $(this).dialog("destroy").remove();
                    return false;
                   }
                }
            }));
          } else {
            // There is published/disponible product
            resendQuotation($this.parent());
            return false;
          }
        }
      }));
    } else {
      // resend from boFile header : already checked if there is published/disponible product
      resendQuotation($this.parent());
      return false;
    }
  });

  function resendQuotation($btnGroupDiv) {
    $btnGroupDiv.find(".submitRecreation").attr("href", "");
    var $form = $btnGroupDiv.find("form.bookingprocess-form");
    var action = $form.attr("action");
    $form.attr('action', action + "?" + Math.random()*1000);
    $form.submit();
    $btnGroupDiv.removeClass("open");
  }



  orxapi.completeSearchForm = function(lastSearch) {
    var $cbxZone = $("#searchZone").find(".cbxCodeZone,.depCityZone");

    // checkbox
        $cbxZone.find("input:checkbox").each(function() {
          if ($(this).is(":checked")) {
            $(this).parents(".cbxCodeZone").find("input:text").prop("disabled", false);
            $(this).parents(".cbxCodeZone").find("input:text").addClass("lightValue");

            $(this).parents(".depCityZone").find("input:text").prop("disabled", true);
            $(this).parents(".depCityZone").find("input:text").val("");
            $(this).parents(".depCityZone").find("input:text").removeClass("lightValue");
          }
        });

        if ($(".canalIpt").length > 0 && $(".canalIpt").val() != "") {
          var selectOrgValue = orxapi.getUrlParameter("criteria.bookingChannel.organization", lastSearch);
          var selectUserValue = orxapi.getUrlParameter("criteria.bookingChannel.agent", lastSearch);
          orxapi.completeOrg(selectOrgValue, selectUserValue);
          if (selectOrgValue != "") {
              orxapi.completeUser(selectOrgValue, selectUserValue);
           }
        }
    }


  orxapi.retrieveLastAdvSearch = function() {
    var lastSearch = orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch");

    if (lastSearch != null && lastSearch != "") {
      $("#advSearchForm").deserialize(lastSearch);
      orxapi.completeSearchForm(lastSearch);
      $("#advSearchForm").find("input[value!='']:not(:disabled):not(:checkbox), select[value!='']:not(:disabled)").addClass("lightValue");
      $("#bodyContent").find(".searchBtn").trigger("click");
    }
  }

  orxapi.retrieveLastProducerAdvSearch = function() {
    var lastProducerSearch = orxapi.selectors.tabsBarZone.find("li.active").data("lastProducerSearch");


    if (lastProducerSearch != null && lastProducerSearch != "") {
      $("#advProducerSearchForm").deserialize(lastProducerSearch);
      orxapi.completeSearchForm(lastProducerSearch);
      $("#advProducerSearchForm").find("input[value!='']:not(:disabled):not(:checkbox), select[value!='']:not(:disabled)").addClass("lightValue");
      $("#bodyContent").find(".searchBtn").trigger("click");
    }
  }

  orxapi.completeOrg = function(lastSearchOrg, selectUserValue) {
  var controlOrga = $(".orgIpt").data("controlorga");
  var controlAgent = $(".userIpt").data("controlagent");
    if(!controlOrga) {
        $.ajax($.extend({}, orxapi.ajax, {
          url : "completeOrganization",
          dataType : "json",
          async : false,
          data : {
            selectedChannel : $(".canalIpt").val()
          },
          success : function(data, textStatus, jqXHR) {
            orxapi.emptyOrganizationSelectBox(controlOrga);
            $.each(data, function(i) {

              if ( $(".orgIpt option[value=" + data[i].organization + "]").length == 0) {
                if (lastSearchOrg == data[i].organization){
                  $(".orgIpt").append($("<option>",{ "selected": "selected","value" : data[i].organization, "text":data[i].organization }));
                } else{
                  $(".orgIpt").append($("<option>",{ "value" : data[i].organization, "text":data[i].organization }));
                }
              }
            });
          }
        }));
    }
    $(".orgIpt").removeAttr("disabled");
    if(controlAgent) {
          orxapi.completeUser($(".orgIpt").val(), selectUserValue);
    } else if(controlOrga) {
        orxapi.completeUser('', '');
    }
  }

  orxapi.completeUser = function(org, lastSearchUser) {
  var controlAgent = $(".userIpt").data("controlagent");
    var selectedOrg = $(".orgIpt").val();
    if (selectedOrg == ""){
      selectedOrg = org;
    }
    if(!controlAgent) {
       $.ajax($.extend({}, orxapi.ajax, {
          url : "completeUser",
          dataType : "json",
          async : false,
          data : {
            selectedChannel : $("#ipt_ibkCanalDistrib").val(),
            selectedOrg : selectedOrg
          },
          success : function(data, textStatus, jqXHR) {
            orxapi.emptyUserSelectBox(controlAgent);
            $.each(data, function(i) {
              if ($(".userIpt option[value="+data[i].agent+"]").length == 0) {
                if(lastSearchUser==data[i].agent){
                  $(".userIpt").append($("<option>",{ "selected": "selected","value" : data[i].agent, "text":data[i].agent }));
                }else{
                  $(".userIpt").append($("<option>",{ "value" : data[i].agent, "text":data[i].agent }));
                }
              }
            });

          }
      }));
    }
    $(".userIpt").removeAttr("disabled");
  }

  orxapi.emptyOrganizationSelectBox = function(controlOrga) {
    $(".orgIpt").empty().removeClass("lightValue");
    if(!controlOrga) {
      $(".orgIpt").append("<option value=\"\"></option>");
    }
  }

  orxapi.emptyUserSelectBox = function(controlAgent) {
    $(".userIpt").empty().removeClass("lightValue");
    if(!controlAgent) {
      $(".userIpt").append("<option value=\"\"></option>");
    }
  }


  orxapi.addTableRow = function(target) {
    var $tr = $(target),
        $table = $tr.parents("table:eq(0)"),
        trIndex = $table.hasClass("hotelRoomModif") ? $("#ppn_file-modifProcessForm table.hotelRoomModif").find("tbody tr").not(".tr_adder").length :  $table.find("tbody tr").not(".tr_adder").length,
        modifSource = $table.data("modif") + "[" + trIndex + "]",
        modifSourceIdName = $table.data("target-id-name");

    var $clonedTr = $tr.clone();
    $clonedTr.removeClass().addClass("tr_added");
    orxapi.addHiddenIpt($clonedTr, modifSource + ".modifType", "ADD");
    orxapi.addSpecificCancelBtn($clonedTr, "icon-plus");

    // specific for rooms connected to hotelId:
    if ($tr.parents("table:eq(0)").data("hotel-id") != "") {
      var currHotelId = $tr.parents("table:eq(0)").data("hotel-id");
      orxapi.addHiddenIpt($clonedTr, modifSource + ".hotelId", currHotelId);
    }

    var $tdsToChange = $clonedTr.find("td").not(".td_action");
    $tdsToChange.each(function(i) {

      var $this = $(this),
          sourceTd = $(this).attr("class"),
          $changedField = $this.find("input,select"),
          $targetField = $table.find("tr.tr_adder").find("." + sourceTd).find("input,select");

      // fix: cloned select fields are not keeping original value
      $changedField.val($targetField.val());
      $changedField.attr("name", $changedField.data("name").replace("$$", trIndex));
      $changedField.attr("class", $changedField.data("class"));
      $changedField.attr("id", $changedField.attr("id")+"[" + trIndex + "]");
      $changedField.removeData();

      if ($changedField.hasClass("datePicker")) {
        $changedField.removeClass("hasDatepicker");
      }
    });

    $tr.before($clonedTr);
    if ($table.find("tr:last").prev().find("input.datePicker").length) {
        $clonedTr.find("input.datePicker").datepicker();
    }
  }

  orxapi.deleteTableRow = function (target) {

    var $tr = $(target),
        $table = $tr.parents("table:eq(0)"),
        curModifId = $tr.data("id"),
        trIndex = $table.hasClass("hotelRoomModif") ? $("#ppn_file-modifProcessForm table.hotelRoomModif").find("tbody tr").not(".tr_adder").index($tr) :  $table.find("tbody tr").not(".tr_adder").index($tr),
        modifSource = $table.data("modif") + "[" + trIndex + "]",
        modifSourceIdName = $table.data("target-id-name");

    $tr.removeClass();
    $tr.addClass("tr_removed");
    orxapi.addSpecificCancelBtn($tr, "icon-trash");

    // information about the initial fileRoomId, travellerId,...
    orxapi.addHiddenIpt($tr, modifSource + "." + modifSourceIdName, curModifId);
    orxapi.addHiddenIpt($tr, modifSource + ".modifType", "SUPPRESS");

    // specific for rooms connected to hotelId:
    if ($tr.parents("table:eq(0)").data("hotel-id") != "") {
      var currHotelId = $tr.parents("table:eq(0)").data("hotel-id");
      orxapi.addHiddenIpt($tr, modifSource + ".hotelId", currHotelId);
    }

    // show inputs and selects
    orxapi.showFormFields($tr, true);
  }

  /** executed on modification or replacement tr action */
  orxapi.editTableRow = function(target, modifType) {

    var $tr = $(target),
        $table = $tr.parents("table:eq(0)"),
        curModifId = $tr.data("id"),
        trIndex = $table.hasClass("hotelRoomModif") ? $("#ppn_file-modifProcessForm table.hotelRoomModif").find("tbody tr").not(".tr_adder").index($tr) :  $table.find("tbody tr").not(".tr_adder").index($tr),
        modifSource = $table.data("modif") + "[" + trIndex + "]",
        modifSourceIdName = $table.data("target-id-name");

    $tr.addClass("tr_edited");
    // information about the initial fileRoomId, travellerId,...
    orxapi.addHiddenIpt($tr, modifSource + "." + modifSourceIdName, curModifId);
    orxapi.addHiddenIpt($tr, modifSource + ".modifType", modifType);

    // specific for rooms connected to hotelId:
    if ($tr.parents("table:eq(0)").data("hotel-id") != "") {
      var currHotelId = $tr.parents("table:eq(0)").data("hotel-id");
      orxapi.addHiddenIpt($tr, modifSource + ".hotelId", currHotelId);
    }

    if (modifType == "MODIFY") {
      orxapi.addSpecificCancelBtn($tr, "icon-pencil");
    } else if (modifType == "REPLACE") {
      orxapi.addSpecificCancelBtn($tr, "icon-resize-full");
    }
    $tr.find("span.viewModif").addClass("hidden");

    // show inputs and selects
    orxapi.showFormFields($tr, false);
  }

  orxapi.undoEditionOfTableRow = function(target) {
    var $tr = $(target),
        $table = $tr.parents("table:eq(0)");

    $tr.removeClass();
    $tr.find("td.td_action").empty().html($table.find("tr.tr_adder").find("td.td_action div.btn-group").clone().removeClass("hidden"));
    $tr.find("input, select").remove();
    // datepicker leftover
    $tr.find("i.icon-calendar").remove();
    $tr.find("span.viewModif").removeClass("hidden");
  }

  /**
   * Validate if in each modif table we have atleast one row with modified class. If table that is validated is multihotel one then
   * atleast 1 hotel should have a tr in edit mode.
   */
  orxapi.validateTrInEditMode = function(target) {
    var validMsg = "",
        $target = $(target);
    if ($target.find("table.table-validation").length) {
      $target.find("table.table-validation").each(function () {
          var $currTable = $(this),
              $tr = $currTable.find("tbody tr").not(".tr_adder");
          if ($tr.length
                && $tr.filter(".tr_edited").length < 1
                && $tr.filter(".tr_removed").length < 1
                && $tr.filter(".tr_transfered").length < 1
                && $tr.filter(".tr_added").length < 1) {
            validMsg = $currTable.data("valid-msg");
            return false;
          }
      });
    }

    if (validMsg != "") {
      return validMsg;
    }

    // multihotel : check if atleast hotel has edited tr
    if ($target.find("table.multiTable-validation").length) {
      var $hotelsContainer = $target.find("#fileRoomsWithHotel");
      var $allTrs = $hotelsContainer.find("tbody tr").not(".tr_adder");
      if ($allTrs.length
            && $allTrs.filter(".tr_edited").length < 1
            && $allTrs.filter(".tr_removed").length < 1
            && $allTrs.filter(".tr_transfered").length < 1
            && $allTrs.filter(".tr_added").length < 1) {
        validMsg = $hotelsContainer.find("table.multiTable-validation:eq(0)").data("valid-msg");
      }
    }
    return validMsg;
  };

  /** Check if current input value is different from the initial one */
  /** false returned : values are the same */
  orxapi.validateIfValIsDifferent = function (ipts) {
    var $iptsToCheck = $(ipts),
        different = false;
    $iptsToCheck.each(function () {
      var $currIpt = $(this);
      var initVal = $currIpt.parent().find(".viewModif").data("value");
      if ($currIpt.val() !== initVal) {
        // diff value as init value. Valid.
        different = true;
        return false;
      }
    });
    return different;
  }

  /** for each table, check if edited or replaced trs have atleast 1 field that is different than initial one */
  orxapi.validateTableZones = function(target) {
    var validMsg = "",
        $form = $(target);
    if ($form.find("table.table-validation").length < 1) {
      return validMsg;
    }

    $form.find("table.table-validation").each(function () {
      var $currTable = $(this);
      $currTable.find("tbody").find("tr.tr_edited, tr.tr_replaced, tr.tr_added").each(function () {
        // for each tr, atleast one field should be different
        var $iptsToCheck = $(this).find("input.checkInitVal, select.checkInitVal");
        if ($iptsToCheck.length && !orxapi.validateIfValIsDifferent($iptsToCheck)) {
          validMsg = $currTable.data("valid-msg");
          return false;
        }
      });
      if (validMsg != "") {
        return false;
      }
    });

    return validMsg;
  }

  /** validate formula zone */
  orxapi.validateFormulaZone = function(target) {
    var validMsg = "",
        $form = $(target);
    if ($form.find("#formulaModif").length) {
      var $modifZone = $form.find("#formulaModif");
      var initVal = $modifZone.find(".viewModif").data("value");
      var currVal;
      if ($modifZone.find("#slt_formula").attr("name") != "") {
        currVal = $modifZone.find("#slt_formula").val();
      } else if ($modifZone.find("#ipt_formula").attr("name") != "") {
        currVal = $modifZone.find("#ipt_formula").val();
      }

      if (currVal == "" || currVal == initVal) {
        // not valid!
        validMsg = $("#formulaModif").data("valid-msg");
      }
    }
    return validMsg;
  }

  /** validateReferenceZone: atleast one subreference should be diff then initial one  */
  orxapi.validateReferenceZone = function(target) {
    var validMsg = "",
        $form = $(target);

    if ($form.find("#fdt_clientFileRef").length) {
      var $zoneToCheck = $form.find("#fdt_clientFileRef");
      var $subZonesToCheck = $zoneToCheck.find("dd");
      $subZonesToCheck.each(function () {
        var $iptsToCheck = $(this).find("input.checkInitVal");
        if ($iptsToCheck.length && !orxapi.validateIfValIsDifferent($iptsToCheck)) {
          // same value founded.
          validMsg = $(this).data("valid-msg");
        } else {
          // atleast one reference is diff.
          validMsg = "";
          return false;
        }
      });
    }
    return validMsg;
  }

  /** Validate product zone. Subzones (depCity, depDate) should hold values different from initial ones */
  orxapi.validateProductZone = function(target) {
    var validMsg = "",
        $form = $(target);

    if ($form.find("#fdt_product").length) {
      // we have product form. Validate if prod, depDate and depCity are different
      var $zoneToCheck = $form.find("#fdt_product");
      var $subZonesToCheck = $zoneToCheck.find("dd");
      $subZonesToCheck.each(function () {
        var $iptsToCheck = $(this).find("input.checkInitVal, select.checkInitVal");
        if ($iptsToCheck.length && !orxapi.validateIfValIsDifferent($iptsToCheck)) {
          validMsg = $(this).data("valid-msg");
          return false;
        }

        // product table list validation: at least one product selected
        if ($(this).hasClass("productList")) {
           if ($("#productListContainer").find("table tbody tr.success").length < 1) {
             validMsg = $(this).data("valid-msg");
             return false;
           }
        }

        // depDate modif: check also if date is > reservationDate
        if ($(this).hasClass("depDateList")) {
          var newDepDate = $.datepicker.parseDate("dd/mm/yy", $(this).find("#ipt_depDate").val())
          var reservationDate = $.datepicker.parseDate("dd/mm/yy", $(this).find(".reservationDate").data("value"));
          if (newDepDate <= reservationDate) {
             validMsg = $(this).find(".reservationDate").text();
             return false;
          }
        }
      })
    }
    return validMsg;
  }

  /** Validate product zone. Subzones (depCity, depDate) should hold values different from initial ones */
  orxapi.validateInsuranceZone = function(target) {
    var validMsg = "",
        $form = $(target);

    if ($form.find("#fdt_insurance").length) {
      // we have insurance. Validate if there is selected value and if its diff from init val.
      var $zoneToCheck = $form.find("#fdt_insurance dd");

      // one radio must be selected
      if ($zoneToCheck.find("input[name='insuranceRadio']:checked").length < 1) {
        return $zoneToCheck.data("valid-select-msg");
      }

      // selected value must be different from init value
      var newCode = $zoneToCheck.find("input[name='insuranceRadio']:checked").nextAll("input[name$='.code']").val();
      var initCode = $zoneToCheck.find(".viewModif").text();
      if (newCode == initCode) {
        return $zoneToCheck.data("valid-msg");
      }
    }
    return validMsg;
  }

  /** Recalculate vente */
  orxapi.calculSaleAmount = function(tr) {

    var selectedQty = $(tr).find("td.td_quantity :selected").val(),
        $iptUnit = $(tr).find("td.td_unit input"),
        $iptSale = $(tr).find("td.td_sale input");

    if (selectedQty == "") {
      // empty quantity. unit is empty and disabled
      $iptUnit.val("").prop("disabled", true);
    } else {
      // not empty quantity: unitAmount is not disabled and recalculated
      $iptUnit.prop("disabled", false);
      var iptVal = $iptUnit.val();
      iptVal = orxapi.removeSpacesFromNumber(iptVal);
      var totalAmount = (selectedQty * iptVal.replace(",", ".")).toFixed(2);
      totalAmount = totalAmount.replace(".", ",");
      $iptSale.val(orxapi.getNumberWithSpaces(totalAmount));

      // if type was TAX or LIT brut and net are forced to be the same as sale. recalcul also totals
      var selectedType = $(tr).find(".td_type :selected").val();
      if (selectedType == "TAX") {
        $(tr).find(".td_grossBuy input").val(orxapi.getNumberWithSpaces(totalAmount));
        $(tr).find(".td_buy input").val(orxapi.getNumberWithSpaces(totalAmount));
        orxapi.calculTotalSum("td_totalGrossBuy", $("#ppn_file-quoteForm tbody tr.tr_added").find(".td_grossBuy input"));
        orxapi.calculTotalSum("td_totalBuy", $("#ppn_file-quoteForm tbody tr.tr_added").find(".td_buy input"));
      }

      orxapi.calculTotalSum("td_totalSale", $("#ppn_file-quoteForm tbody tr.tr_added").find(".td_sale input"));
    }

  }

  /** Recalculate commission input */
  orxapi.calculCommision = function(tr) {
    var iptBuyVal = $(tr).find("td.td_buy input").val(),
        iptGrossBuyVal = $(tr).find("td.td_grossBuy input").val();

    if (iptGrossBuyVal != "" && iptBuyVal != "") {
      iptGrossBuyVal = iptGrossBuyVal.replace(",", ".");
      iptGrossBuyVal = orxapi.removeSpacesFromNumber(iptGrossBuyVal);

      iptBuyVal = iptBuyVal.replace(",", ".");
      iptBuyVal = orxapi.removeSpacesFromNumber(iptBuyVal);

      var commission = iptGrossBuyVal - iptBuyVal;
      commission = commission.toFixed(2).replace(".", ",")
      $(tr).find("td.td_com input").val(orxapi.getNumberWithSpaces(commission));
    } else {
      $(tr).find("td.td_com input").val("0,00");
    }
    orxapi.calculTotalSum("td_totalCom", $("#ppn_file-quoteForm tbody tr.tr_added").find(".td_com input"));
  }

  /** Recalculate markup input */
  orxapi.calculMarkup = function(tr) {
    var iptSaleVal = $(tr).find("td.td_sale input").val(),
        iptGrossBuyVal = $(tr).find("td.td_grossBuy input").val();

    if (iptGrossBuyVal != "" && iptSaleVal != "") {
      iptGrossBuyVal = iptGrossBuyVal.replace(",", ".");
      iptGrossBuyVal = orxapi.removeSpacesFromNumber(iptGrossBuyVal);

      iptSaleVal = iptSaleVal.replace(",", ".");
      iptSaleVal = orxapi.removeSpacesFromNumber(iptSaleVal);

      var markup = iptSaleVal - iptGrossBuyVal;
      markup = markup.toFixed(2).replace(".", ",")
      $(tr).find("td.td_markup input").val(orxapi.getNumberWithSpaces(markup));
    } else {
      $(tr).find("td.td_markup input").val("0,00");
    }
    orxapi.calculTotalSum("td_totalMarkup", $("#ppn_file-quoteForm tbody tr.tr_added").find(".td_markup input"));
  }

  /** Recalculate total sum of quotes */
  orxapi.calculTotalSum = function(totalClass, ipts) {
    var $ipts = $(ipts);
    var totalInit = ($("#cancelFileProcess").length < 1) ? $(".tr_initialTotal").find("." + totalClass).text() : $(".tr_cancelInitialTotal").find("." + totalClass).text();
    if (totalInit == "-") {
      totalInit = "0,00";
    }

    var totalInit = orxapi.removeSpacesFromNumber(totalInit);
    totalInit = totalInit.replace(",", ".");
    totalInit = parseFloat(totalInit, 10)
    if ($("#ppn_file-quoteForm tbody").find("tr.tr_added").length) {
      $ipts.each(function () {
        var valToAdd = $(this).val();
        if (valToAdd != "") {
          valToAdd = valToAdd.replace(",", ".");
          valToAdd = orxapi.removeSpacesFromNumber(valToAdd);
          totalInit = totalInit + parseFloat(valToAdd, 10);
       }
      });
    }
    totalInit = totalInit.toFixed(2);
    totalInit = totalInit.replace(".", ",");
    $(".tr_total").find("." + totalClass).text(orxapi.getNumberWithSpaces(totalInit));
  }

  /* Change prices from point to comma format */
  orxapi.changeToPointFormat = function(ipts) {
    var $ipts = $(ipts);
    $ipts.each(function() {
      var $ipt = $(this),
          valBefore = $ipt.val(),
          valAfter = valBefore.replace(",", ".");
          // no empty spaces
          valAfter = orxapi.removeSpacesFromNumber(valAfter);
      $ipt.val(valAfter);
    });
  }

  /** If there is atleast 1 mail from client or TO to treat, add alert. if not remove it. */
  orxapi.modifToTreatInfo = function(subMenu) {
    var $modifSubMenu = $(subMenu);
    if ($("#modificationListZone").find("div.pendingMails").length < 1) {
      $modifSubMenu.removeClass("itm_warning");
      $modifSubMenu.find("i.icon-exclamation-sign").remove();
    } else {
      $modifSubMenu.addClass("itm_warning");
      if ($modifSubMenu.find("i.icon-exclamation-sign").length < 1) {
        var modifAlert = $("<i/>",{ "class":"icon-exclamation-sign"});
        $modifSubMenu.prepend(modifAlert);
      }
    }
  }


  /* Open file tab with specific id  */
  function openFilePage(id) {

    // if tab was already opened, click on it.
    if ($("#tabsBarZone").find("li[data-id='" + id + "']").length) {
      // tab for this file was already opened. Navigate to it.
      $("#tabsBarZone").find("li[data-id='" + id + "']").trigger("click");
    } else {
      var id = id,
          pge = "pge_file";
      $.ajax($.extend({}, orxapi.ajax, {
        url : pge + ".action",
        data: {id : id},
        success : function(data, textStatus, jqXHR) {
          orxapi.selectors.bodyContent.removeClass().addClass(pge).html(data);
          orxapi.addTab(pge);
          $.getScript("./shared/ts/web/js/bo/" + pge + ".js");
        }
      }));
    }
  };

  /* Open file tab with specific reference  */
  function openFilePageByRef(fileReference, onlyProducer) {
      var reference = fileReference,
          pge = "pge_file";
      $.ajax($.extend({}, orxapi.ajax, {
        url : pge + ".action",
        data: {fileReference : reference, onlyProducer : onlyProducer},
        success : function(data, textStatus, jqXHR) {
          var id =  $(data).data("id");
          // if tab was already opened, click on it.
          if ($("#tabsBarZone").find("li[data-id='" + id + "']").length) {
            // tab for this file was already opened. Navigate to it.
            $("#tabsBarZone").find("li[data-id='" + id + "']").trigger("click");
          } else {
              orxapi.selectors.bodyContent.removeClass().addClass(pge).html(data);
              orxapi.addTab(pge);
              $.getScript("./shared/ts/web/js/bo/" + pge + ".js");
          }
        }
      }));
  };

  // Export of the search results
  $("#wrapper").on("click", ".btn_exportResult", function(event) {
      if(!$(this).hasClass("disabled")) {
        var searchRequest = $(this).data("search-request"),
            type = $(this).data("type"),
            app = $("#starterZone").find(".pto_starter").data("code"),
            headers = $(this).data("headers")
        var url = "exportResultsCSV.action?searchRequest=" + encodeURIComponent(searchRequest) + "&app=" + app + "&type=" + type;
        if (typeof(headers) != "undefined") {
          url = url + "&headers=" + headers
        }
        window.location.href = url;
      }
  });

  // Full Export of the search results
  $("#wrapper").on("click", ".btn_fullExportResult", function(event) {
      if(!$(this).hasClass("disabled")) {
        var searchRequest = $(this).data("search-request");
        var url = "processFullExportResults.action?searchRequest=" + encodeURIComponent(searchRequest);
        window.location.href = url;
      }
  });

  //Print the result table
  $("#wrapper").on("click", ".btn_printResult", function(event) {
    if(!$(this).hasClass("disabled")) {
      var searchRequest = $(this).data("search-request");
      var type = $(this).data("type");

      var params = "searchRequest=" + encodeURIComponent(searchRequest) + "&type=" + type;
      var action = $(this).data("page") + ".action?" + params;

      window.open(action, "", "width=1060,height=700,scrollbars=1");
    }
  });

  /* SEND MAIL Popin : Resize input send mail */
  $.fn.resizeIptMail = function () {
    var w_list = $(this).find(".headingMailValueList li").width() - 20,
        iptList = $(this).find(".headingMailValueList li input");
        w_subjectTag = $(this).find(".headingMailValueList li.subjectIptZone #subjectTag").innerWidth(),
        iptSubject = $(this).find(".headingMailValueList li.subjectIptZone #ipt_suject");
    iptList.css("width", w_list);
    iptSubject.css("width", w_list - w_subjectTag);
  };



  //OPEN THE SEND MAIL POPIN
  orxapi.sendMailPopin = function (pointer, action, isReply) {
      var mailer = $("#innerWrapContent").find(".mailerZone");
      var params = "id=" + orxapi.selectors.bodyContent.find("div:eq(0)").data("id");
      params = params + "&type=" + pointer.data("type");
      params = params + "&recipient=" + pointer.data("recipient");
      if(typeof(pointer.data("modifid")) != "undefined") {
        params = params + "&modifId=" + pointer.data("modifid");
      }
      if(typeof(pointer.data("template")) != "undefined") {
          params = params + "&templateType=" + pointer.data("template");
      }
      if(typeof(pointer.data("invoiceid")) != "undefined") {
          params = params + "&invoiceId=" + pointer.data("invoiceid");
      }
      if(typeof(mailer.find(".subjectLabel").text()) != "") {
          params = params + "&subjectLabel=" + mailer.find(".subjectLabel").text();
      }

      if(typeof(pointer.data("confirm")) != "undefined") {
          params = params + "&confirm=" + pointer.data("confirm");
      }
      if(isReply == true) {
        params = params + "&replying=true";
      }

      $.ajax($.extend({}, orxapi.ajax, {
          url : action,
          data: params,
          success : function(data, textStatus, jqXHR) {
              mailer.find(".replyZone").remove();
              mailer.append(data);
              mailer.find(".replyZone").css({ "bottom": "-20%" });
              mailer.find(".replyZone").removeClass("hidden").animate({bottom: "+=20%" }, 250, "easeOutCubic").addClass("open");
              mailer.find(".replyZone").resizemailerZone();
              /* add light wysiwyg on #bodyMail */
              tinyMCE.init($.extend({ elements : "bodyMail", oninit : orxapi.resizeHeigthSendMail }, orxapi.plugin.wysiwygLight));
              mailer.find(".replyZone .iCheckBox").madeIcheckbox();
              mailer.resizeIptMail();
          }
      }));
  };



  /** ----------------------- */
  /**                         */
  /**    adv search page      */
  /**                         */
  /** ----------------------- */

  /* Advanced distributor and producer search click */
  $("#bodyContent").on("click", ".searchBtn", function() {
  // ognl fix: remove empty values from the list of params. if not, inputs with integers will be considered as strings
  var params = $('.advSearchForm').find('input, select').filter(function () {return $.trim(this.value);}).serialize();
    var url = "processAdvSearch.action";
    var pge = "pge_list/pge_resultList";
    var module = orx.selectors.body.data("module");
    var shared = $(this).data("shared");

    if ($(".advSearchForm").valid()) {

      $.ajax($.extend({}, orxapi.ajax, {
        url : url,
        data : params,
        success : function(data, textStatus, jqXHR) {
          $("#resultTable").html(data);

          //Depending on the search (DISTRIBUTOR or PRODUCER), set the correct data in the list tab
          var fileType = $("#ipt_fileType").val();
          if(fileType == "DISTRIBUTOR") {
            orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch", params);
          } else if (fileType == "PRODUCER") {
            orxapi.selectors.tabsBarZone.find("li.active").data("lastProducerSearch", params);
          }

          orxapi.loadJs(module, pge, shared);
          //$.getScript("./shared/ts/web/js/bo/" + pge + ".js");
          orxapi.storeTabCookie();
        }
      }));
    }
    return false;
  });

  /**********************************************************************
    FAST FILE SEARCH (autocomplete)
  ***********************************************************************/

  function launchAutoComplete(that) {
    var searchString = $.trim(that.val());
      if (searchString.length >= 3) {
      $.ajax($.extend({}, orxapi.ajax, {
        url : "fastFileSearch.action",
        data: {searchString : searchString},
        success : function(data, textStatus, jqXHR) {
          $(".autoCompleteZone").html(data);
          $("#autoCompSearch").find("li.resultSearch:eq(0)").addClass("active")
          $("body").addClass("autoCompleteOn");
        }
      }));
    }
  }

  function closeAutoComplete(withBlur) {
    $("#quickSearchZone").find("#autoCompSearch").find(".active").removeClass("active");
    eqActive = 0;
    $("#quickSearchZone").find("#autoCompSearch").remove();
    $(".ipt_searchPrincipal").off("keydown");
    if (withBlur === true) {
      $(".ipt_searchPrincipal").blur();
    }
    $("body").removeClass("autoCompleteOn");
  }

  // open fast search result in a file tab or if too many results - in a lists search
  $.fn.openSearchResult = function () {

      if ($(this).hasClass("resultSearch")) {
        var id = $(this).data("id");
        if (typeof(id) != "undefined" && id != "") {
          openFilePage(id);
        }
      } else if ($(this).hasClass("advSearch")) {
        // launch advSearch (listTab) with current fastSearch string
        // For now only possible in backOffice module (not in compta)
        var $this = $(this),
            destPage = $this.data("page"),
            fileType = $this.data("type"),
            searchedText = $(".ipt_searchPrincipal").val(),
            $listTab,
            module = $("body").data("module") == "backOffice" ? "backOffice" : "accounting";

        var newCriteria = "criteria.type=" + fileType + "&criteria.fastSearchText=" + searchedText;

        if (module == "backOffice") {
          // create list tab and re-use fastSearchText input
          $listTab = $("#tabsBarZone").find("li[data-page='pge_list']");
          if (!$listTab.length) {
            // create list tab
            orxapi.addTab("pge_list")
            $listTab = $("#tabsBarZone").find("li[data-page='pge_list']");
          }
          $listTab.data("submenu", destPage);
          fileType == "DISTRIBUTOR" ? $listTab.data("lastSearch", newCriteria) : $listTab.data("lastProducerSearch", newCriteria);
          $listTab.trigger("click");
        }
      }
      closeAutoComplete(true);
      $(".ipt_searchPrincipal").val("");
  }

  /** Delay function: launch function after X ms delay. */
  var delay = (function() {
    var timer = 0;
    return function(callback, ms) {
      clearTimeout (timer);
      timer = setTimeout(callback, ms);
    };
  })();

  $.fn.autoComp = function(options) {

    // This is the easiest way to have default options.
    var settings = $.extend({
      "posBlock": "right"
    }, options);

    var that = $(this),
        iptAutoComp = $(this).find(".ipt_searchPrincipal");

    $("#autoCompSearch").css(settings.posBlock, 0);

    // index of active(selected) result
    var eqActive = 0;

    that.on("focus", ".ipt_searchPrincipal", function() {
      launchAutoComplete(iptAutoComp);
    });

    that.on("keyup", ".ipt_searchPrincipal", function(e) {

      if (e.which == 40) {
        // event key ↓
        e.preventDefault();
        if(!$("#autoCompSearch").find('li').hasClass("active")) {
          eqActive = 0;
        } else {
          if (eqActive >= $("#autoCompSearch").find("li").length - 1) {eqActive = 0;} else {eqActive = eqActive +1;}
        }
        $("#autoCompSearch").find(".active").removeClass("active");
        $("#autoCompSearch").find("li").eq(eqActive).addClass("active");
      } else if (e.which == 38) {
        // event key ↑
        e.preventDefault();
        $("#autoCompSearch").find(".active").removeClass("active");
        if (eqActive <= 0) {eqActive = $("#autoCompSearch").find("li").length -1;} else {eqActive = eqActive -1;}
        $("#autoCompSearch").find("li").eq(eqActive).addClass("active");
      } else if (e.which == 13) {
        // event key enter
        e.preventDefault();
        if (!$("#autoCompSearch").find(".active").length) {
          // preselect 1st result
          $("#autoCompSearch").find("li:eq(0)").addClass("active");
        }
        if ($("#autoCompSearch").find("li").length) {
          $("#autoCompSearch").find(".active").openSearchResult();
        } else {
          // no results found
          $(".defSearchBar").effect("shake", 20);
        }
      } else if (e.which == 27) {
        // escape key clicked
        closeAutoComplete(true);
      } else {
        // any other key pressed: after some delay launch the search
        delay(function() {
          if (iptAutoComp.val().length < 3 && $("#autoCompSearch").length) {
            closeAutoComplete(false);
          } else {
            launchAutoComplete(iptAutoComp);
          }
        }, 500);
      }
    });

    // active only if autocomplete is shown
    $(document).on("click", "body.autoCompleteOn", function(e) {
      var that = $(this);
      var $target = $(e.target);
      if ($target.parents("#quickSearchZone").length < 1) {
        // not clicked inside the multiresultats
         closeAutoComplete(true);
      }
    });

    that.on("click", "li", function(e) {
      e.stopPropagation();
      $(this).openSearchResult();
    });
    that.on("mouseenter", "li.resultSearch", function() {
      $(this).addClass("hover");
    });
    that.on("mouseleave", "li.resultSearch", function() {
      $(this).removeClass("hover");
    });
  };

  $("#quickSearchZone").autoComp();

})(jQuery, orxapi);
