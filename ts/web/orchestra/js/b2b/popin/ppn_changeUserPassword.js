(function($, orx) {
  var $form = $("#changeUserPasswordForm");
  var firstConnection = orx.selectors.body.data("first-connection");
  if (typeof firstConnection != "undefined" && firstConnection) {
      firstConnection = true;
  } else {
      firstConnection = false;
  }
  // if first connection cancel button becomes logoff button
  if (firstConnection) {
      $("#btn_changeUserPasswordPopupLogoff").show();
      $("#btn_changeUserPasswordPopupCancel").hide();

      $("#btn_changeUserPasswordPopupLogoff").click(function(event) {
          location.href = "/b2b/logoff.action";
      });

      // display msg
      $("#fdt_changeUserPassword").children("div.alert-info").show();
  }

  // Validation rules
  $.validator.addMethod("requiredValue", function(value, element) {return value !== '';},"");
  $.validator.addMethod("same", function(value, element) {
      var $ipt1 = $(element).parents(".bodyZone").find("input[type!='hidden'][name$='newPassword']"),
      $ipt2 = $(element).parents(".bodyZone").find("input[type!='hidden'][name$='confirmPassword']"),
      valid = $ipt1.val() === $ipt2.val();
      if (!valid) {
        $ipt1.addClass("error");
        $ipt2.addClass("error");
      } else {
        $ipt1.removeClass("error");
        $ipt2.removeClass("error");
      }
      return valid;
  }, "");
  $.validator.addClassRules("notEmpty", {requiredValue:true});
  $.validator.addClassRules("same", {same:true});
  $form.validate({
      errorPlacement: function(error, element) {}
  });

  $("#btn_changeUserPasswordPopupSave").click(function(event) {
      event.preventDefault();
      var $that = $(this);

      if (!$form.valid()) {
          return false;
      }

      var params = $form.serialize();

      var url = "https://" + location.host + "/b2b/" + $that.data("action");
      $form.attr("method", "post");
      $form.attr("action", url);
      $form.submit();
  });

})(jQuery, orxapi);