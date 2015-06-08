(function($, orx){

    // Override default error message
    $.validator.messages.required = "";

    // Override generation of error label
    $("#paymentPopinForm").validate({
      errorPlacement: function(error, element) {},
    });

  $.validator.addMethod("requiredValue", function(value, element) {return value !== '';},"");

    $.validator.addMethod("tPrice", function(value, element) {
    // allowed format: 10 || 10,25 || 10.25  || 1 025,3 || 1025,3 || -5,35
      var formatedVal = removeSpacesFromNumber(value);
      return (value == "" || /^(\+|-)?[0-9]{1,10}([\,.][0-9]{1,2})?$/.test(formatedVal));
    }, "");
    $.validator.addMethod("positiveInteger", function(value, element) {
        // allowed format: 10 || 10,25 || 10.25  || 1 025,3 || 1025,3 || -5,35
          var formatedVal = removeSpacesFromNumber(value);
          return (value == "" || /^(\+)?[0-9]{1,10}?$/.test(formatedVal));
        }, "");
    $.validator.addMethod("dateFormat", function(value, element) {
        try {
          $.datepicker.parseDate("dd/mm/yy", value);
          return true;
        } catch (e) {
          return false;
        }
    }, "");

    $.validator.addClassRules("notEmpty", {requiredValue:true});
    $.validator.addClassRules("date-4y", {requiredValue:true, dateFormat:true});
    $.validator.addClassRules("price", {tPrice:true});
    $.validator.addClassRules("positiveInteger", {positiveInteger:true});

  var orx_dp = orx.plugin.datepicker,
      popin = $("#wrapPopin").find(".popin");

  $("#wrapPopin").find(".datePicker").datepicker($.extend({}, { }, orx_dp));
  orx.pictoDate();

  /* resize popin */
  orx.resizePopin();
  $(window).resize(function(){
    orxapi.resizePopin();
  });


  //SAVE --------------------------------------------
  $("#btn_paymentPopupSave").click(function(evt) {
      evt.preventDefault();

      if (!$("#paymentPopinForm").valid()) {
          return false;
        }

      var action = $(this).data("action") + ".action";

      // change all values from format xx,xx to format xx.xx
      orxapi.changeToPointFormat($("#paymentPopinForm").find("input.price"));

      var params = $("#paymentPopinForm").serialize();
      params = params + "&id=" + $("#tabsBarZone").find("li.active").data("id");

      $.ajax($.extend({}, orxapi.ajax, {
          url : action,
          data: params,
          success : function(data, textStatus, jqXHR) {
            //close the popin with animation
            $("#wrapPopin").animate({"padding-top": "+=90px" }, 150, "easeInCubic", function(){ $("#wrapPopin").remove(); });
            $("#innerWrapContent").parent().html(data);
            $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-payment.js");
          }
      }));
  });
  //END SAVE ----------------------------------------



  //Refresh the new balance and balance after auto payments when modifying the amount
  $(".amount").on('change',function() {
    displayBalanceAfterAutoPayments();
  });


  //Refresh the balance after auto payments when modifying an auto payment
  $(".ipt_newAmount").on('change', function() {
    //If empty, reset the old amount
    if($(this).val()=="") {
      var oldAmount = $(this).parent().parent().parent().find(".td_oldAmount").text();
      $(this).val(oldAmount);
    }
    displayBalanceAfterAutoPayments();
  });

  //Display the date of the auto payment based on the offset change
  $("#ipt_offset").on('change', function() {
    if(!$("#ipt_offset").valid()) {
      $("#offsetDate").text("");
      return;
    }
    var offset = $("#ipt_offset").val();
    if(offset == "") {
      offset = 0;
      $("#ipt_offset").val(offset);
    }

    var depDate = new Date($("#ipt_depDate").val());
    var paymentDate = addDays(depDate, -offset);

    //Need to change this for internationalization
    var dateStr = paymentDate.getDate() + "/" + paymentDate.getMonth() + "/" + paymentDate.getFullYear();
    $("#offsetDate").text($.datepicker.formatDate('dd/mm/yy', paymentDate));
  });

  if($("#ipt_offset").val() != "") {
      $("#ipt_offset").trigger("change");
  }

  function addDays(d, j)
  {
   return new Date(d.getTime() + (1000 * 60 * 60 * 24 * j));
  }

  /** Format from 1 234 to 1234 */
  function removeSpacesFromNumber(x) {
    return x.replace(/\s/g, "");
  }

  /** Format from 1234 to 1 234 */
  function getNumberWithSpaces(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
  }

  function displayBalanceAfterAutoPayments() {
    var value = $(".amount").val();
    var balance = $("#balance").text();
    if(value==""){
      value = "0";
    }

    balance = removeSpacesFromNumber(balance);
    balance = balance.replace(",", ".");
    balance = parseFloat(balance, 10);
    balance = balance.toFixed(2);

    value = removeSpacesFromNumber(value);
    value = value.replace(",", ".");
    value = parseFloat(value, 10);
    value = value.toFixed(2);

    //Change the value of the other auto payment if there is only one -----------------
    if (popin.find(".tbe_impactPayment").find(".ipt_newAmount").length == 1) {
      var autoVal = popin.find(".tbe_impactPayment").find(".ipt_newAmount").val();
      var autoVal = autoVal.replace(",", ".");
      var resultNbr = balance - value;
      autoVal = removeSpacesFromNumber(autoVal);

      if(resultNbr - parseFloat(autoVal) < 0) {
        if(resultNbr >= 0) {
          autoVal = resultNbr;
        } else {
          autoVal = 0;
        }
      }

      autoVal = getNumberWithSpaces(autoVal);
      autoVal = autoVal.replace(".", ",");
      popin.find(".tbe_impactPayment").find(".ipt_newAmount").val(autoVal)
    }
    //END --------------

    //Change the balance after auto payments
    var balanceAfterAuto = balance - value;
    if (popin.find(".tbe_impactPayment").find(".ipt_newAmount").length) {
        var $ipts = popin.find(".tbe_impactPayment").find(".ipt_newAmount");
        $ipts.each(function () {
          var valToAdd = $(this).val().replace(",", ".");
          valToAdd = removeSpacesFromNumber(valToAdd);
          balanceAfterAuto = balanceAfterAuto - parseFloat(valToAdd);
          balanceAfterAuto = balanceAfterAuto.toFixed(2);
        });
      }
    balanceAfterAuto = parseFloat(balanceAfterAuto, 10);
    balanceAfterAuto = balanceAfterAuto.toFixed(2);

    var balanceAfterAutoNumber =  balanceAfterAuto;
    balanceAfterAuto = balanceAfterAuto.replace(".", ",");
    $("#balanceAfterAutos").text(getNumberWithSpaces(balanceAfterAuto));

    //TEMP : display color
    if(balanceAfterAutoNumber == 0) {
      $("#balanceAfterAutos").attr("style","color: #128C29;");
    } else if(balanceAfterAutoNumber > 0) {
      $("#balanceAfterAutos").attr("style","color: #000;");
    } else if (balanceAfterAutoNumber < 0) {
      $("#balanceAfterAutos").attr("style","color: #f00;");
    }
  }

})(jQuery, orxapi);

