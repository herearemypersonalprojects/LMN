(function($, orx) {

  $("#pge_file").find("#wrapContent").resizeContentTab();

  // load the mailer on modification block click
  $("#innerWrapContent").on("click", ".singleBlock", function(event) {
    if(!$(this).hasClass("active")) {
      $(this).parents("#modificationListZone").find(".active").removeClass("active");
      $(this).addClass("active");
      $(this).loadMailer();
    }
  });

  // if there is atleast 1 client or To mail to treat, set the warning
  orxapi.modifToTreatInfo($("#wrapSideBar").find("li.active"));

  /*--------------------------------------------------------------------------------*/
  /*                                      ACTIONS                                   */
  /*--------------------------------------------------------------------------------*/


  // ppn confirm email cbx change: enable/disable dialog ok btn
  $("body").on("click", ".confirmEmails", function() {
    var $btnOK = $(".ui-dialog-buttonpane button:contains('OK')");
    $(this).is(":checked") ? $btnOK.attr("disabled", false).removeClass("ui-state-disabled") : $btnOK.attr("disabled", true).addClass("ui-state-disabled");
  });


  /* Show edit modif ppn */
  $("#innerWrapContent").find(".btn_editModification").not(".disabled").click(function(event) {
    event.stopPropagation();
    var params = "id=" + $("#tabsBarZone").find("li.active").data("id");
    params = params + "&modifId=" + $(this).parents("div.modificationBlock").data("modif-id");

    // if we are in view mode, no saving of modifs is possible
    if ($(this).data("view-mode") != "") {
      params = params + "&viewMode=" + $(this).data("view-mode");
    }

    // processType : is it modification or regulation
    if ($(this).data("process-type") != "") {
      params = params + "&processType=" + $(this).data("process-type");
    }

    orxapi.madePopin($(this).data("page"), params);
    $(this).parents("#modificationListZone").find(".active").removeClass("active");
    $(this).parents(".modificationBlock").addClass("active");
  });

  /* Show change reference ppn */
  $("#innerWrapContent").find(".btn_changeRef").click(function(event) {
    event.stopPropagation();
    var params = "id=" + $("#tabsBarZone").find("li.active").data("id");
    params = params + "&modifId=" + $(this).parents("div.modificationBlock").data("modif-id");

    orxapi.madePopin($(this).data("page"), params);
    $(this).parents("#modificationListZone").find(".active").removeClass("active");
    $(this).parents(".modificationBlock").addClass("active");
  });


  /* sub menu for adjust quotation */
  $("#innerWrapContent").find(".btn_editQuotations").click(function(event) {
      event.stopPropagation();
      var params = "id=" + $("#tabsBarZone").find("li.active").data("id");
      params = params + "&modifId=" + $(this).parents("div.modificationBlock").data("modif-id");

      // if we are in view mode, no saving of modifs is possible
      if ($(this).data("view-mode") != "") {
        params = params + "&viewMode=" + $(this).data("view-mode");
      }

      // processType : is it modification or regulation
      if ($(this).data("process-type") != "") {
        params = params + "&processType=" + $(this).data("process-type");
      }

      params = params + "&edit=true";
      orxapi.madePopin($(this).data("page"), params);
  });

  /* sub menu for doing a CB payment */
  $("#innerWrapContent").find(".btn_payModif").not(".disabled").click(function(event) {
      event.stopPropagation();
      var params = "id=" + $("#tabsBarZone").find("li.active").data("id");
      params = params + "&modifId=" + $(this).parents("div.modificationBlock").data("modif-id");
      params = params + "&fromModif=true";
      params = params + "&amount=" + $(this).data("amount");
      params = params + "&type=" + $(this).data("type");
      orxapi.madePopin($(this).data("page"), params);
  });

  /* Cancel one modification block */
  $("#innerWrapContent").find(".btn_cancelModifBlock").click(function(event) {

    event.stopPropagation();
    $(this).parents("#modificationListZone").find(".active").removeClass("active");
    $(this).parents(".modificationBlock").addClass("active");

    var contentDialog = $("<div/>",{ "id":"contentDialog"});
    contentDialog.html($(this).data("popin-text"));

    var modifId = $(this).parents("div.modificationBlock").data("modif-id")
    var id = orxapi.selectors.bodyContent.find("div:eq(0)").data("id");

    contentDialog.dialog($.extend({}, orx.plugin.dialog, {
        title : $(this).data("popin-title"),
        buttons:{
          "Fermer": function() {
            $(this).dialog("destroy").remove();
            return false;
           },
          "OK": function() {
            $(this).dialog("close");
            $.ajax($.extend({}, orxapi.ajax, {
              url : "changeModificationStatus.action",
              data: {id : id, modifId : modifId, modifStatus: "cancelled"},
              success : function(data, textStatus, jqXHR) {
                $("#innerWrapContent .wrapLeft").find("div.inner").html(data);
                $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-modifsList.js");
                $("#innerWrapContent").find(".modificationBlock:eq(0)").trigger("click");
              }
            }));
          }
        }
    }));

  });

   /* Validate one modification block */
  $("#innerWrapContent").find(".btn_validateModifBlock").click(function(event) {
    event.stopPropagation();
    $(this).parents("#modificationListZone").find(".active").removeClass("active");
    var $currModifBlock = $(this).parents(".modificationBlock");
    $currModifBlock.addClass("active");

    var modifId = $currModifBlock.data("modif-id");

    var contentDialog = $("<div/>",{ "id":"contentDialog"});

    if ($currModifBlock.find("span.dateLimitWarn").length) {
      // red color
      var $alertSpan = $("<span/>",{ "style":"color:red"});
      ppnText = $currModifBlock.find("span.dateLimitWarn").data("ppn-msg");
      $alertSpan.append(ppnText);
      var $label = $("<label/>",{ "class":"label_msg"});
      $label.append($alertSpan);
      contentDialog.append($label);
    }

    var newStatus = $(this).data("new-status");

    // show right msg depending if its valid of cancellation or not
    $(".validModifDialog").find("p.msg").addClass("hidden");
    (newStatus == "cancelFile") ? $(".validModifDialog").find(".cancellationMsg").removeClass("hidden") : $(".validModifDialog").find(".validMsg").removeClass("hidden");
    contentDialog.append($(".validModifDialog").html());

    // unique ids id dialog inputs and labels
    contentDialog.find("input").each(function() {
      $(this).attr("id", $(this).attr("id") + modifId);
    });
    contentDialog.find("label").each(function() {
      $(this).attr("for", $(this).attr("for") + modifId);
    });

    var id = orxapi.selectors.bodyContent.find("div:eq(0)").data("id");

    contentDialog.dialog($.extend({}, orx.plugin.dialog, {
          title : $(this).data("popin-title"),
          dialogClass: "modalFormStyle",
          buttons:{
            "Fermer": function() {
              $(this).dialog("destroy").remove();
              return false;
             },
            "OK": function() {
              $(this).dialog("destroy").remove();
              var sendEmailToClient = $(this).find(".sendClientFacture").is(":checked");
              $.ajax($.extend({}, orxapi.ajax, {
                url : "changeModificationStatus.action",
                data: {id : id, modifId : modifId, modifStatus: newStatus, sendEmailToClient:sendEmailToClient},
                success : function(data, textStatus, jqXHR) {
                  $("#innerWrapContent .wrapLeft").find("div.inner").html(data);
                  if ($currModifBlock.find(".modificationSubFile") != null
                      && $currModifBlock.find(".modificationSubFile").length > 0) {
                    orxapi.selectors.tabsBarZone.find("li.active").trigger("click");
                  } else {
                    $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-modifsList.js");
                    $("#innerWrapContent").find(".singleBlock:eq(0)").trigger("click");
                  }
                }
              }));
            }
          }
    }));
    $(".ui-dialog-buttonpane button:contains('OK')").attr("disabled", true).addClass("ui-state-disabled");
  });

  /* Change file status modification block */
  $("#innerWrapContent").on("click", ".btn_changeFileStatus", function(event) {

    event.stopPropagation();

    if ($(this).hasClass("disabled")) {
      return;
    }
    $(this).parents("#modificationListZone").find(".active").removeClass("active");
    var $currModifBlock = $(this).parents(".singleBlock");
    $currModifBlock.addClass("active");

    var contentDialog = $("<div/>",{ "id":"contentDialog"}),
        newStatus = $(this).data("new-status"),
        id = orxapi.selectors.bodyContent.find("div:eq(0)").data("id");

    // show right msg depending if its confirmation or invalidation
    $(".changeFileStatusDialog").find(".msg").addClass("hidden");
    (newStatus == "7") ? $(".changeFileStatusDialog").find(".newStatusFullMsg").removeClass("hidden") : $(".changeFileStatusDialog").find(".newStatusConfirmMsg").removeClass("hidden");
    contentDialog.append($(".changeFileStatusDialog").html());

    // unique ids id dialog inputs and labels
    contentDialog.find("input").each(function() {
      $(this).attr("id", $(this).attr("id") + newStatus);
    });
    contentDialog.find("label").each(function() {
      $(this).attr("for", $(this).attr("for") + newStatus);
    });

    contentDialog.dialog($.extend({}, orx.plugin.dialog, {
          title : $(this).data("popin-title"),
          dialogClass: "modalFormStyle",
          buttons:{
            "Fermer": function() {
              $(this).dialog("destroy").remove();
              return false;
             },
            "OK": function() {
              $(this).dialog("destroy").remove();
              $.ajax($.extend({}, orxapi.ajax, {
                url : "changeFileStatus.action",
                data: {id : id, fileStatus: newStatus},
                success : function(data, textStatus, jqXHR) {
                  $("#innerWrapContent .wrapLeft").find("div.inner").html(data);
                  $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-modifsList.js");

                  if ($("#innerWrapContent").find(".singleBlock").length) {
                    $("#innerWrapContent").find(".singleBlock:eq(0)").trigger("click");
                  }
                }
              }));
            }
          }
    }));
  });

  /* resize left wrap */
  $("#innerWrapContent").find(".wrapLeft").resizeLeftZone();


  /*--------------------------------------------------------------------------------*/
  /*                 NEW MODIF CHECKBOX                                             */
  /*--------------------------------------------------------------------------------*/

  var $checkBoxes = $("#innerWrapContent .switchZoneForm").find("input.iCheckBox"),
      overflowClickOut = $("<div/>", { "id" : "overlayClickOut" });


  $("#innerWrapContent .tooltipSwitch").find(".iCheckBox").switchbutton({
      classes: 'ui-switchbutton-thin',
      duration: 100,
      labels: false
  });

  $("#wrapContent").find(".btn_dropdownSwitcher").click(function() {
    if ($(this).hasClass("disabled")) {
      return;
    }
    var that= $(this),
        $tooltip = $(this).next();
    $tooltip.css("margin-left", that.position().left);
    $tooltip.removeClass("closed");
    overflowClickOut.appendTo("body");
    overflowClickOut.click(function() {
      $tooltip.addClass("closed");
      overflowClickOut.remove();
    });
  });
  $checkBoxes.filter("#cbx_modifTraveller").change(function() {
    var $room = $checkBoxes.filter("#cbx_modifRoom");
    if (!$(this).is(':checked') && $room.is(':checked')) {
      // un check
        $room.removeAttr("disabled");
      $room.trigger("click");
    } else if ($(this).is(':checked')) {
      // check
      if (!$room.is(':checked')) {
        $room.trigger("click");
      }
      $room.attr("disabled", "disabled");
    }
  });


  /* sub menu for adjust quotation */
  $("#btn_adjustQuotation").not(".disabled").click(function() {
      var params = "id=" + $("#tabsBarZone").find("li.active").data("id");
      params = params + "&processType=modification";
      orxapi.madePopin($(this).data("page"), params);
  });

  /* sub menu for cancellation */
  $("#btn_cancel").not(".disabled").click(function() {
      var params = "id=" + $("#tabsBarZone").find("li.active").data("id");
      params = params + "&processType=annulation";
      orxapi.madePopin($(this).data("page"), params);
  });

  $checkBoxes.change(function() {
    if ($checkBoxes.length) {
      $(this).parents(".tooltipSwitch").find(".alert-info").addClass("hidden");
      //$("#checkboxModifZone").find(".alert-info").addClass("hidden");
    }
  });

  // Validate new modif (by checkboxes)
  $(".addModificationZone").on("click", ".btn_popinModif", function(event) {
    var $room = $checkBoxes.filter("#cbx_modifRoom");
    // remove disabled on room if disabled
    var roomDisabled = $room.attr("disabled") == "disabled";
    if (roomDisabled) {
      $room.removeAttr("disabled");
    }

    // check if atleast one is selected.
    if ($(this).parents(".tooltipSwitch").find("input.iCheckBox:checked").length < 1) {
      $(this).parents(".tooltipSwitch").find(".alert-info").removeClass("hidden");
      event.stopPropagation();
      return false;
    }

    $(this).parents(".tooltipSwitch").find(".alert-info").addClass("hidden");
    $(this).parents(".tooltipSwitch").addClass("closed");
    overflowClickOut.remove();

    var params = $(this).parents(".tooltipSwitch").find(".switchZoneForm").serialize();
    params = params + "&id=" + $("#tabsBarZone").find("li.active").data("id");
    orxapi.madePopin($(this).data("page"), params);
    // reset disabled if existed
    if (roomDisabled) {
      $room.attr("disabled", "disabled");
    }

    $(this).blur();
  });


  /* open the send mail page using the "SEND A MAIL TO CLIENT/TO" button */
  $("#modificationListZone").on("click", ".btn_sendMail", function(evt) {
    if($(this).parents(".singleBlock").hasClass("active")) {
      evt.preventDefault();
      var action = "prepareDisplayReplyOnly.action";
      orxapi.sendMailPopin($(this), action, false);
      return false;
    }
  });

})(jQuery, orxapi);
