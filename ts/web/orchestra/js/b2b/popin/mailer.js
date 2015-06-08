(function($, orx){

  var mailer = $(".mailerZone");

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

  /* Resize input send mail */
  $.fn.resizeIptMail = function () {
    var w_list = $(this).find(".headingMailValueList li").width() - 20,
        iptList = $(this).find(".headingMailValueList li input");
        w_subjectTag = $(this).find(".headingMailValueList li.subjectIptZone #subjectTag").innerWidth(),
        iptSubject = $(this).find(".headingMailValueList li.subjectIptZone #ipt_suject");
    iptList.css("width", w_list);
    iptSubject.css("width", w_list - w_subjectTag);
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
  mailer.resizeIptMail();
  mailer.find(".replyZone .iCheckBox").madeIcheckbox();


  /*------------- On click and on change ----------------------------------------------*/

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

  /* send mail when clicking the "SEND" button*/
  mailer.on("click", ".btn_send", function(evt) {
      evt.preventDefault();
      if (!$("#sendMailForm").valid()) {
        return false;
      }
      var action = "sendProductByMail.action";
      var params = $("#sendMailForm").serialize();
      params = params + "&from=" + $(".fromAddress").text();
      params = params + "&content=" + escape(tinyMCE.getInstanceById('bodyMail').getContent());
      params = params + "&checked=" + $("#btn_checked").hasClass("active");
      //Id used when printing a product on the b2b
      params = params + "&id=" + orxapi.selectors.tabsBarZone.find("li.active").data("id");

      $.ajax($.extend({}, orxapi.ajax, {
          url : action,
          data: params,
          success : function(data, textStatus, jqXHR) {
            $("#wrapPopin").animate({"padding-top": "+=90px" }, 150, "easeInCubic", function(){ $("#wrapPopin").remove(); });
          }
      }));
  });

  // TODO make the mailer.js generic (b2b + bo) and don't validate here...
  /*----------------------*/
  /*      VALIDATION      */
  /*----------------------*/

  var basicEmailsRules = {
      multiEmails : true
  };

  var requiredEmailsRules = {};
  $.extend(requiredEmailsRules, basicEmailsRules, {required: true});

  var defaultMessage = "";

  $("#sendMailForm").validate({
    rules : {
      to: requiredEmailsRules,
      cc: basicEmailsRules,
      bcc: basicEmailsRules
    },
    messages : {
      to: defaultMessage,
      cc: defaultMessage,
      bcc: defaultMessage
    }
  });

  $.validator.addMethod(
      "multiEmails",
       function(value, element) {
           if (this.optional(element)) {
             // return true on optional element
             return true;
           }
           // split element by ","
           var emails = value.split(/[,]+/);
           valid = true;
           for (var i in emails) {
               value = emails[i];
               valid = valid &&
                       jQuery.validator.methods.email.call(this, $.trim(value), element);
           }
           return valid;
       },
      ""
  );

})(jQuery, orxapi);
