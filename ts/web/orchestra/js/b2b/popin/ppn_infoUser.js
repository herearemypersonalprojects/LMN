(function($, orx) {
  var $form = $("#infoUserPopinForm");

  // Validation rules
  $.validator.addMethod("requiredValue", function(value, element) {return value !== '';},"");
  $.validator.addClassRules("notEmpty", {requiredValue:true});
  $form.validate({
      rules: {
          "userProfile.mail": {
              email: true,
              required: true
          }
      },
      errorPlacement: function(error, element) {}
  });

  $(".multiselect").multiselect();
  $(".ui-multiselect").css("width","224");

  $("#btn_infoUserPopupSave").click(function(event) {
      event.preventDefault();
      var $that = $(this);

      if (!$form.valid()) {
          return false;
      }

      // run request
      var params = $form.serialize();

      $.ajax($.extend({}, orxapi.ajax, {
        url : $that.data("action"),
        data: params,
        success : function(data, textStatus, jqXHR) {
            $("#wrapPopin").remove();
            location.reload();
        }
      }));
  });

})(jQuery, orxapi);