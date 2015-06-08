(function($, orx){

    $.validator.addMethod("onePhoneNotEmpty", function(value, element) {
      var $ipt1 = $('#ipt_mobilePhone');
      var $ipt2 = $('#ipt_phone');
      if ($ipt1.val() !== '' || $ipt2.val() !== '') {
          $ipt1.removeClass("error");
          $ipt2.removeClass("error");
        return true;
      } else {
          $ipt1.addClass("error");
          $ipt2.addClass("error");
          return false;
      }
    },"");
    $.validator.addClassRules("phoneValidation", {onePhoneNotEmpty:true});
  // init var
  var orx_dp = orx.plugin.datepicker,
    popin = $("#wrapPopin").find(".popin");

  $("#ipt_invoiceCountry").tsautocomplete({
    autocomplete : {
      focus : function(event, ui) {
        $(this).val(ui.item.code);
        return false;
      },
      select : function(event, ui) {
        $(this).val(ui.item.code);
        return false;
      },
      source : function(request, response) {
        $.post("autocompleteCountries", $.extend(request, {
          code : this.element.val()
        }), function(data) {
          response(data);
        }, "json");
      }
    },
    type : false
  });

  $("#ipt_deliveryCountry").tsautocomplete({
    autocomplete : {
      focus : function(event, ui) {
        $(this).val(ui.item.code);
        return false;
      },
      select : function(event, ui) {
        $(this).val(ui.item.code);
        return false;
      },
      source : function(request, response) {
        $.post("autocompleteCountries", $.extend(request, {
          code : this.element.val()
        }), function(data) {
          response(data);
        }, "json");
      }
    },
    type : false
  });


  //SAVE --------------------------------------------
  $("#btn_editClientPopupSave").click(function(evt) {
      evt.preventDefault();

      if (!$("#editClientPopinForm").valid()) {
          return false;
        }
      var params = $("#editClientPopinForm").serialize();
      params = params + "&id=" + $("#tabsBarZone").find("li.active").data("id");


      $.ajax($.extend({}, orxapi.ajax, {
        url : "file-editClientProcess",
        data: params,
        success : function(data, textStatus, jqXHR) {

            //close the popin with animation
            $("#wrapPopin").animate({"padding-top": "+=90px" }, 150, "easeInCubic", function(){ $("#wrapPopin").remove(); });
            $("#innerWrapContent").parent().html(data);
            $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-client.js");
        }
      }));
  });

})(jQuery, orxapi);
