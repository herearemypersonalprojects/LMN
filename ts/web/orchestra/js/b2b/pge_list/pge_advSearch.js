(function($, orx) {

  var WAITING_TIME_BEFORE_COUNT = 2000;
  var orx_dp = orx.plugin.datepicker;
  var inTimeout = false;

  $(".datePicker").datepicker($.extend({}, orx_dp,
    {
      minDate : 0,
      onSelect: function(dateText, inst) {
        if(!inst.input.attr("value") == "") {
          inst.input.addClass("lightValue");
        } else {
          inst.input.removeClass("lightValue");
        }
        $(this).valid();
        $(this).trigger("change");
      }
    }));
  orxapi.pictoDate();

  $(".multiselect").multiselect();
  $(".ui-multiselect").css("width","224");

  $("#ipt_ptnStartDate").on("change", function() {
    var value = $(this).val();
    if (value && value != "") {
      var marginSlt = $("#ipt_daysMargin");
      marginSlt.removeAttr("disabled");
      if (!marginSlt.val()) {
        marginSlt.val(marginSlt.data("default"));
      }
    } else {
      $("#ipt_daysMargin").attr("disabled", "disabled");
      $("#ipt_daysMargin").val("");
    }
  });

  var mkgFieldZone = $("#searchZone").find(".cbxCodeZone");

  mkgFieldZone.find("input:checkbox").click(
      function() {
        if (!$(this).is(":checked")) {
          $(this).parents(".cbxCodeZone").find("input:text").prop("disabled", true);
          $(this).parents(".cbxCodeZone").find("input:text").removeClass("lightValue");
        } else {
          $(this).parents(".cbxCodeZone").find("input:text").prop("disabled", false);
          $(this).parents(".cbxCodeZone").find("input:text").addClass("lightValue");
        }
      });

  /* Simple search */
  $(".advSearchForm").keydown(function(evt) {
    if (evt.which == 13) {
      evt.preventDefault();
      $(".searchBtn").trigger("click");
    }
  });

  // Override default error message
  $.validator.messages.required = "";
  $(".advSearchForm").validate({
    rules : {
      totalAmount : {
        number : true
      }
    },
    errorPlacement : function(error, element) {}

  });

  //orxapi.retrieveLastAdvSearch();

  /* Advance search responsive  */
  //advSearchLargeScreenActive

  $.fn.responsiveSide = function () {
    $(this).each(function () {
        var that = $(this),
            hisId = that.attr("id"),
            classResponsive = hisId + "-side",
            wrapContent =  $("#wrapContent");

        wrapContent.addClass(classResponsive);

        if(that.children().height() >= wrapContent.height() || wrapContent.width() < 655) {
          wrapContent.removeClass(classResponsive);
        };

    });
  };

  $('[data-responsive="side"]').responsiveSide();

  $(window).resize(function(){
    $('[data-responsive="side"]').responsiveSide();
  });


  //------------------------------//
  // Count products               //
  //------------------------------//
  //Display the count when we arrive on the form
  $(".advSearchForm").each(function(){

    var value = $(this).find(".countResult").find(".label").html();
    // because the String.trim() does not work for IE
    var trimValue = value.replace(/^\s+|\s+$/g, '')

    if (trimValue.length == 0) {
      countResultsAjax($(this));
    }
  });


  $(".formSelected").on("change", "input", function(){
    var form = $(this).parents(".advSearchForm");
    orxapi.countResultsDelayed(form);
  });

  $(".formSelected").on("change", "select", function(){
    var form = $(this).parents(".advSearchForm");
    orxapi.countResultsDelayed(form);
  });

  //Display a loading and perform the ajax call after a timeout
  orxapi.countResultsDelayed = function(form) {
  form.find(".countResult").find(".label").html("...");
    if (!inTimeout) {
      inTimeout = true;

      //Call the ajax request after 3 seconds
      setTimeout(function(){
        countResultsAjax(form);
      }, WAITING_TIME_BEFORE_COUNT);
    }
  }

  //Do the count instantely
  orxapi.countResults = function(form) {
    countResultsAjax(form);
  }

  //Performs the ajax call to compute the number of results
  function countResultsAjax(form) {
  form.find(".countResult").find(".label").html("...");

    if (form.valid()) {
        var formCode = form.find("#ipt_formType").val();

        // ognl fix: remove empty values from the list of params. if not, inputs with integers will be considered as strings
        var params = form.find('input, select').filter(function () {return $.trim(this.value);}).serialize();

        $.ajax({
          url : "processAdvSearchCount.action",
          data : params,
          error : function(jqXHR, status, error) {
            //TODO : dialog error
          },
          success : function(data, textStatus, jqXHR) {
            form.find(".countResult").find(".label").html(data);
            inTimeout = false;
          }
        });
      }
  }
  //------------------------------//
  // End of count products        //
  //------------------------------//

})(jQuery, orxapi);
