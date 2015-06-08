(function($, orx) {
// TODO add mail functions
    var popin = $("#wrapPopin").find(".popin");
    var mailer = $("#ppn_regMail").find(".mailerZone");


    mailer.resizeIptMail();

    // close popin
    popin.on("click", "#btn_cancelRegModifMail", function(evt) {
      evt.preventDefault();
      $("#wrapPopin").animate({"padding-top": "+=90px" }, 150, "easeInCubic", function(){ $("#wrapPopin").remove(); });
    });

    /** process (validate) multi regulation and send mail */
    popin.on("click", ".validatePpn", function(event) {

      event.preventDefault();

      // global jquery validation
//      if (!$("#sendMailForm").valid()) {
//        return false;
//      }
      var params = $("#sendMailForm").serialize();
      params = params + "&from=" + $(".fromAddress").text();
      params = params + "&content=" + escape(tinyMCE.getInstanceById('bodyMail').getContent());
      params = params + "&confirm=" + $(this).data("confirm");
      // ognl fix: remove empty values from the list of params
      params = params.replace(/[^&]+=\.?(?:&|$)/g, '');

      $("#wrapPopin").remove();
      $.ajax($.extend({}, orxapi.ajax, {
        url : "reg-modif-mailProcess.action",
        data: params,
        success : function(data, textStatus, jqXHR) {
          // TODO add a message for user

        }
      }));
    });


    //Change template
    popin.on("change", "#slt_template", function(evt) {
      evt.preventDefault();
        var action = "ppn_reg-mail_template.action";
        var params = $("#sendMailForm").serialize();
        params = params + "&id=" + orxapi.selectors.bodyContent.find("div:eq(0)").data("id");
        params = params + "&templateType=" + $(this).data("templatetype");
        params = params + "&selectedTemplate=" + $("#slt_template").val();
        params = params + "&massiveReg=true";

        $.ajax($.extend({}, orxapi.ajax, {
            url : action,
            data: params,
            success : function(data, textStatus, jqXHR) {
                $("#replyBodyZone").empty();
                $("#replyBodyZone").html(data);
                /* add light wysiwyg on #bodyMail */
                tinyMCE.init($.extend({
                  elements : "bodyMail", resizable:true,
                  theme_advanced_resizing : true,
                  theme_advanced_resize_horizontal : false
                }, orxapi.plugin.wysiwygLight));
                mailer.resizeIptMail();
            }
        }));
    });

})(jQuery, orxapi);

