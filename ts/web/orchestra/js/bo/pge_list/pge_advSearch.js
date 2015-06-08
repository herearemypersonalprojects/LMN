(function($, orx) {
  var orx_dp = orx.plugin.datepicker;

  $("#searchZone").find(".datePicker").datepicker($.extend({}, {}, orx_dp));
  orxapi.pictoDate();

  var mkgFieldZone = $("#searchZone").find(".cbxCodeZone,.depCityZone");

  mkgFieldZone.find("input:checkbox").click(
      function() {
        if (!$(this).is(":checked")) {
          $(this).parents(".cbxCodeZone").find("input:text").prop("disabled", true);
          $(this).parents(".cbxCodeZone").find("input:text").removeClass("lightValue");

          $(this).parents(".depCityZone").find("input:text").prop("disabled", false);
        } else {
          $(this).parents(".cbxCodeZone").find("input:text").prop("disabled", false);
          $(this).parents(".cbxCodeZone").find("input:text").addClass("lightValue");

          $(this).parents(".depCityZone").find("input:text").prop("disabled", true);
          $(this).parents(".depCityZone").find("input:text").val("");
          $(this).parents(".depCityZone").find("input:text").removeClass("lightValue");
        }
      });

  /* Trigger the channel input. */
  if($(".canalIpt").val() != '') {
    $(".canalIpt").trigger("change");
  }

  /* Simple search */
  $(".advSearchForm").keydown(function(evt) {
    if (evt.which  == 13) {
      evt.preventDefault();
      $(".searchBtn").trigger("click");
    }
  });

  $.validator.addMethod("positiveNumber",
    function (value) {
        return value == "" || Number(value) >= 0;
    }, "");
  $.validator.addClassRules("positiveNumber", {positiveNumber:true});

  // Override default error message
  $.validator.messages.required = "";
  $(".advSearchForm").validate({
    rules : {
      totalAmount : {
        number : true
      }
      },
    errorPlacement: function(error, element) {}
  });

  if ($("input.number").length) {
    $("input.number").rules("add", {
      number:true
    });
  }

   var currentSearch = $("#ipt_fileType").val();

   if (currentSearch == "DISTRIBUTOR") {
     orxapi.retrieveLastAdvSearch();
   } else {
     orxapi.retrieveLastProducerAdvSearch();
   }

  $("#ipt_ptnCityStart").tsautocomplete({
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
        $.post("autocompleteDeparture", $.extend(request, {
          code : this.element.val()
        }), function(data) {
          response(data);
        }, "json");
      }
    },
    type : false
  });

  $("#ipt_ptnDesti").tsautocomplete({
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
        $.post("autocompleteDestination", $.extend(request, {
          code : this.element.val()
        }), function(data) {
          response(data);
        }, "json");
      }
    },
    type : false
  });


  // multiselects
  $(".multiselect").multiselect();
  $(".ui-multiselect").css("width","224");

})(jQuery, orxapi);

