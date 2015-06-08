(function($, orx){

  var mailer = $("#innerWrapContent").find(".mailerZone");

  /*--------------------------------------------------------------------------------*/
  /*                 MAILER                                                         */
  /*--------------------------------------------------------------------------------*/


  /* Resize width all mailer  */
  $.fn.resizemailerZone = function () {
    var w_innerRight = $("#pge_file").find(".wrapRight .inner").outerWidth(),
        h_window = windowHeight = $(window).height(),
        h_mailer = $(window).height() - $("#headerBarZone").height() - $("#tabsBarZone").height() - $("#headerPage").height() - ($("#supHeaderBarZone").length ? $("#supHeaderBarZone").height() : 0) - 20;
    $(this).css({ "width": w_innerRight, "height" : h_mailer });
    $(this).find(".mailListZone").css({ "width": w_innerRight, "height" : h_mailer });
  };

  /* make icheckbox  */
  $.fn.madeIcheckbox = function () {
    $(this).switchbutton({
      classes: 'ui-switchbutton-thin',
      duration: 100,
      labels: false
    });
  };

  $(window).resize(function() {
    mailer.resizemailerZone();
    mailer.find(".mailZone").resizemailerZone();
    mailer.find(".replyZone").resizemailerZone();
    $("#innerWrapContent").find(".wrapLeft").resizeLeftZone();
    mailer.resizeIptMail();
  });

  mailer.resizemailerZone();


  /*------------- On click and on change ----------------------------------------------*/

  /* open VIEW mail when double clicking on the header of a mail */
  mailer.on("dblclick", "tr", function() {
    var action = "prepareDisplayMail.action";
    var params = "uid=" + $(this).data("uid");
    params = params + "&direction=" + $(this).data("direction");
    params = params + "&type=" + $(this).data("type");
    params = params + "&modifId=" + $(this).data("modifid");
    params = params + "&recipient=" + $(this).data("recipient");
    params = params + "&checked=" + $(this).data("checked");
    params = params + "&fileMailId=" + $(this).data("id");
    params = params + "&id=" + orxapi.selectors.bodyContent.find("div:eq(0)").data("id");

    $.ajax($.extend({}, orxapi.ajax, {
        url : action,
        data: params,
        success : function(data, textStatus, jqXHR) {
          mailer.find(".mailZone").remove();
          mailer.append(data);
          mailer.find(".mailZone").css({ "bottom": "-20%" });
          mailer.find(".mailZone").removeClass("hidden").animate({bottom: "+=20%" }, 250, "easeOutCubic").addClass("open");
          mailer.find(".mailZone .iCheckBox").madeIcheckbox();
          mailer.find(".mailZone").resizemailerZone();
          $("#bodyMail_ifr_read").squirt(emailContent);
        }
    }));
  });


  /* animate for close VIEW mail */
  mailer.on("click", ".mailZone .close", function(){
    if (! mailer.find(".replyZone").hasClass("open")){
      mailer.find(".mailZone").animate({bottom: "-=20%" }, 250, "easeInCubic", function(){ $(this).addClass("hidden"); }).removeClass("open");
    }
  });


  /* animate for close SEND mail when clicking on x */
  mailer.on("click", ".replyZone .close", function(){
    mailer.find(".replyZone").animate({bottom: "-=20%" }, 250, "easeInCubic", function(){ $(this).addClass("hidden").removeClass("open"); });
    mailer.find(".replyZone .iCheckBox").madeIcheckbox();
  });
  /* animate for close SEND mail when clicking on CANCEL*/
  mailer.on("click", ".replyZone .btn_cancel", function(evt) {
    evt.preventDefault();
    mailer.find(".replyZone").animate({bottom: "-=20%" }, 250, "easeInCubic", function(){ $(this).addClass("hidden").removeClass("open"); });
    mailer.find(".replyZone .iCheckBox").madeIcheckbox();
  });

  /* open the send mail page using the "REPLY" button */
  mailer.on("click", ".btn_replyMail", function() {
      var action = "prepareDisplayReplyOnly.action";
      orxapi.sendMailPopin($(this), action, true);
  });

  /* Switch checked button */
  mailer.on("change", ".mailZone .cbx_processed", function(evt) {
    evt.preventDefault();
    var action = "processCheckMailAction.action";
    var params = "fileMailId=" + $("#ipt_fileMailId").val();
    params = params + "&type=" + $("#ipt_type").val();
    params = params + "&reply=true";
    params = params + "&modifId=" + $(this).data("modifid");
    params = params + "&id=" + orxapi.selectors.bodyContent.find("div:eq(0)").data("id");

    var type = $("#ipt_type").val();
    var modifId = $(this).data("modifid");
    var mailId = $("#ipt_fileMailId").val();

    $.ajax($.extend({}, orxapi.ajax, {
        url : action,
        data: params,
        success : function(data, textStatus, jqXHR) {
          if (type == "MODIF" || type == "REGUL" || type == "CANCEL") {
            //Refresh modif block
            var modifBlock = $("#modifBlock_" + modifId);
            var modifZone = modifBlock.find(".modificationZone");
            modifBlock.find(".modificationZone").empty();
            modifBlock.find(".modificationZone").html(data);

            //Refresh mail header's checkBox
            var checkBox = $(".headerLine_" + mailId).find(".checkBoxIcon");
            if(checkBox.hasClass("icon-ok")) {
              checkBox.removeClass("icon-ok");
            } else {
              checkBox.addClass("icon-ok");
            }

            // if there is atleast 1 client or To mail to treat, set the warning
            orxapi.modifToTreatInfo($("#wrapSideBar").find("li.modificationSideTab"));
          } else if (type == "ON_REQUEST") {
            // on request dd block returned
            $("#modificationListZone .request").empty();
            $("#modificationListZone .request").html(data);
            $("#requestBlock").addClass("active");

            //Refresh mail header's checkBox
            var checkBox = $(".headerLine_" + mailId).find(".checkBoxIcon");
            if(checkBox.hasClass("icon-ok")) {
              checkBox.removeClass("icon-ok");
            } else {
              checkBox.addClass("icon-ok");
            }
            // if there is atleast 1 client or To mail to treat, set the warning
            orxapi.modifToTreatInfo($("#wrapSideBar").find("li.modificationSideTab"));
          } else {
            $("#mailBodyZone").empty();
            $("#mailBodyZone").html(data);
          }
        }
    }));
  });


  //Change template
  mailer.on("change", "#slt_template", function(evt) {
    evt.preventDefault();
      var action = "processChangeTemplateAction.action";
      var params = $("#sendMailForm").serialize();
      params = params + "&id=" + orxapi.selectors.bodyContent.find("div:eq(0)").data("id");
      params = params + "&templateType=" + $(this).data("templatetype");
      params = params + "&selectedTemplate=" + $("#slt_template").val();

      $.ajax($.extend({}, orxapi.ajax, {
          url : action,
          data: params,
          success : function(data, textStatus, jqXHR) {
              $("#replyBodyZone").empty();
              $("#replyBodyZone").html(data);
              /* add light wysiwyg on #bodyMail */
              tinyMCE.init($.extend({ elements : "bodyMail", oninit : orxapi.resizeHeigthSendMail }, orxapi.plugin.wysiwygLight));

              mailer.find(".replyZone .iCheckBox").madeIcheckbox();
              mailer.resizeIptMail();
          }
      }));
  });

 /* send mail when clicking the "SEND" button*/
  mailer.on("click", ".replyZone .btn_send", function(evt) {
    evt.preventDefault();
      if (!$("#sendMailForm").valid()) {
        return false;
      }
      var action = "processSendMailAction.action";
      var params = $("#sendMailForm").serialize();
      params = params + "&from=" + $(".fromAddress").text();
      params = params + "&id=" + orxapi.selectors.bodyContent.find("div:eq(0)").data("id");
      params = params + "&content=" + escape(tinyMCE.getInstanceById('bodyMail').getContent());
      params = params + "&subjectTag=" + $("#subjectTag").text()
      params = params + "&checked=" + $("#btn_checked").hasClass("active");
      params = params + "&fileMailId=" + $("#ipt_fileMailId").val();
      params = params + "&confirm=" + $(this).data("confirm");

      //The type of mail
      var type = $("#ipt_type").val();
      var modifId = $("#ipt_modifId").val();

      $.ajax($.extend({}, orxapi.ajax, {
          url : action,
          data: params,
          success : function(data, textStatus, jqXHR) {
            if (type == "DOCUMENT") {
              $("#wrapContent").empty();
              $("#wrapContent").html(data);
              $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-doc.js");
            } else if (type == "INVOICE") {
              $("#wrapContent").empty();
              $("#wrapContent").html(data);
              $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-invoice.js");
            } else if (type == "MODIF" || type == "REGUL" || type == "CANCEL") {
              var modifBlock = $("#modifBlock_" + modifId);
              modifBlock.find(".modificationZone").empty();
              modifBlock.find(".modificationZone").html(data);
              modifBlock.removeClass("active");
              modifBlock.trigger("click");
            } else if (type == "ON_REQUEST") {
              var requestBlock = $(".request");
              requestBlock.empty();
              requestBlock.html(data);
              requestBlock.find(".singleBlock").removeClass("active");
              requestBlock.find(".singleBlock").trigger("click");
            }
          }
      }));
  });


  /* ----------------------- Util functions -------------------------------------------*/



})(jQuery, orxapi);
