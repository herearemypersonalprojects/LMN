(function($, orx){

  var sltC_selectCard = $("#slt_selectCard"),
      sltC_selected = sltC_selectCard.find(".slt_optionSelected"),
      sltC_listOption = sltC_selectCard.find(".slt_optionsList"),
      sltC_options = sltC_listOption.find("li");


  // select custom for choose old credit card
  // add default value in the select
  sltC_selected.append(sltC_options.first().html());
  sltC_options.first().addClass("selected");

  $(".popin").on("click", ".slt_optionSelected", function() {
    var that = $(this);
        overflowClickOut = $("<div/>", { "id" : "overlayClickOut" });

    if(sltC_options.length) {

      sltC_listOption.css("min-width", sltC_selectCard.innerWidth() );

      if (that.hasClass("active")) {
        $("#overlayClickOut").remove();
        sltC_listOption.hide();
        that.removeClass("active");
      } else {
        that.addClass("active");
        sltC_selectCard.after(overflowClickOut);
        sltC_listOption.toggle();
      }

      overflowClickOut.click(function() {
        $(this).remove();
        sltC_listOption.hide();
        that.removeClass("active");
      });

    }
  });

  sltC_options.click( function() {
    sltC_selected.empty();
    sltC_selected.append($(this).html());
    $(this).parent().find(".selected").removeClass("selected");
    $(this).addClass("selected");
    overflowClickOut.remove();
    $("#overlayClickOut").remove();
    sltC_listOption.hide();
    sltC_selected.removeClass("active");

    fillCBInfos()
  });

  fillCBInfos();


    // Override default error message
    $.validator.messages.required = "";

    // Override generation of error label
    $("#paymentPopinForm").validate({
      errorPlacement: function(error, element) {},
    });

  $.validator.addMethod("requiredValue", function(value, element) {return value !== '';},"");

  $.validator.addMethod("tPrice", function(value, element) {
  // allowed format: 10 || 10,25 || 10.25  || 1 025,3 || 1025,3 || -5,35
    var formatedVal = orxapi.removeSpacesFromNumber(value);
    return (value == "" || /^(\+|-)?[0-9]{1,10}([\,.][0-9]{1,2})?$/.test(formatedVal));
  }, "");
  $.validator.addMethod("positivePrice", function(value, element) {
      // allowed format: 10 || 10,25 || 10.25  || 1 025,3 || 1025,3 || -5,35
      var formatedVal = orxapi.removeSpacesFromNumber(value);
      return (value == "" || /^(\+)?[0-9]{1,10}([\,.][0-9]{1,2})?$/.test(formatedVal));
    }, "");
  $.validator.addMethod("positiveInteger", function(value, element) {
      //allow 10, +10, refuse -10, 10.5
      var formatedVal = orxapi.removeSpacesFromNumber(value);
      return (value == "" || /^(\+)?[0-9]{1,10}?$/.test(formatedVal));
    }, "");
  $.validator.addMethod("cardNumber", function(value, element) {
      // Check if it's a card number based on the Luhn algorithm
      var formatedVal = orxapi.removeSpacesFromNumber(value);
      return isCreditCard(value);
    }, "");
  $.validator.addMethod("dateFormat", function(value, element) {
      try {
        $.datepicker.parseDate("dd/mm/yy", value);
        return true;
      } catch (e) {
        return false;
      }
  }, "");
  $.validator.addMethod("tFeesLessThanAmount", function(value, element) {
    var $divField = $(element).parents("#fdt_chequeCE");
    var feesIptVal = $divField.find("input.fees").val();
    var amountIptVal = $divField.find("input.amount").val();
    feesIptVal = feesIptVal.replace(",", ".");
    feesIptVal = orxapi.removeSpacesFromNumber(feesIptVal);
    amountIptVal = amountIptVal.replace(",", ".");
    amountIptVal = orxapi.removeSpacesFromNumber(amountIptVal);
    return (amountIptVal - feesIptVal >= 0);
  }, "");

  // chosen amount must be <= than transaction amount
  $.validator.addMethod("tMaxRefound", function(value, element) {
    var iptAmount = value.replace(",", ".");
    iptAmount = orxapi.removeSpacesFromNumber(iptAmount);
    iptAmount = parseInt(iptAmount)
    var transAmount = $("#slt_selectCard").find("li.selected").data("transamount");
    transAmount = transAmount.replace(",", ".");
    transAmount = orxapi.removeSpacesFromNumber(transAmount);
    transAmount = parseInt(transAmount);
    return (iptAmount <= transAmount);
  }, "");


  $.validator.addClassRules("notEmpty", {requiredValue:true});
  $.validator.addClassRules("date-4y", {requiredValue:true, dateFormat:true});
  $.validator.addClassRules("price", {tPrice:true});
  $.validator.addClassRules("positivePrice", {positivePrice:true});
  $.validator.addClassRules("positiveInteger", {positiveInteger:true});
  $.validator.addClassRules("cardNumber", {cardNumber:true});
  $.validator.addClassRules("feesLessThanAmount", {tFeesLessThanAmount:true});
  $.validator.addClassRules("maxRefound", {tMaxRefound:true});


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

      if ($(this).hasClass("disabled")) {
        return false;
      }

      if (!$("#paymentPopinForm").valid()) {
          return false;
        }

      var action = $(this).data("action") + ".action";

      // change values from format xx,xx to format xx.xx
      orxapi.changeToPointFormat($("#paymentPopinForm").find("input.positiveInteger, input.price, input.amount, input.positivePrice"));

      var params = $("#paymentPopinForm").serialize();
      params = params + "&id=" + $("#tabsBarZone").find("li.active").data("id");

      // remove empty values from params (bankCode for example: if not we will have ognl exc.)
      params = params.replace(/[^&]+=\.?(?:&|$)/g, '');

      $.ajax($.extend({}, orxapi.ajax, {
          url : action,
          data: params,
          success : function(data, textStatus, jqXHR) {
            if(data == "") {
               // action returned NONE (for exemple ppn was opened from modif block)
               // close popin
                $("#wrapPopin").animate({"padding-top": "+=90px" }, 150, "easeInCubic", function(){ $("#wrapPopin").remove(); });

            } else if(data.lastIndexOf("error$$", 0) === 0) {
                data = data.replace("error$$","");
                if($(".detailErrorZone").hasClass("hidden")) {
                  $(".detailErrorZone").removeClass("hidden");
                }
                $("#errorMessage").text(data);
                $(".paymentSideTab").trigger("click");
            } else {
                //close the popin with animation
                $("#wrapPopin").animate({"padding-top": "+=90px" }, 150, "easeInCubic", function(){ $("#wrapPopin").remove(); });
                if($(".modificationSideTab").hasClass("active")) {
                  //Payed from the button in the modification tab
                  $(".modificationSideTab").removeClass("active");
                  $(".paymentSideTab").trigger("click");
                }
                $("#innerWrapContent").parent().html(data);
                $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-payment.js");
            }
          }
      }));
  });
  //END SAVE ----------------------------------------



  //Selectbox in fraud popin
  $("#slt_oldPayment").on('change',function() {
      if($("#slt_oldPayment").val()!="Autre"){
        //If selected an old payment, fill the label and amount
        var selected = $(this).find('option:selected');
        var amount = selected.data('amount').toString();
        var label = selected.data('label');
        $("#ipt_label").val(label);
        if(amount.substring(0,1) != "-") {
          $("#ipt_amount").val("-" + amount);
        } else {
          $("#ipt_amount").val(amount.substring(1,amount.length));
        }
      }else{
        //Else empty it
        $("#ipt_label").val("");
        $("#ipt_amount").val("");
      }
    });

  //Refresh the new balance and balance after auto payments when modifying the amount
  $(".amount").on('change',function() {
    var value = $(".amount").val();
    var balance = $("#balance").text();
    var negate = $(".amount").data("negate");
    var result = "";
    var resultAfterAuto = "";

    if(value==""){
      value = "0";
    }

    balance = balance.replace(",", ".");
    balance = orxapi.removeSpacesFromNumber(balance);
    balance = parseFloat(balance, 10);
    balance = balance.toFixed(2);

    value = value.replace(",", ".");
    value = orxapi.removeSpacesFromNumber(value);
    value = parseFloat(value, 10);
    value = value.toFixed(2);

    //If in CHEQUE CE, substract the fees
    var fee = $("#paymentPopinForm").find(".fees").val();
    if(typeof(fee) != 'undefined') {
      var fee = $("#paymentPopinForm").find(".fees").val();
      if(fee == "") {
        fee = 0;
      } else {
        fee = fee.replace(",", ".");
        fee = orxapi.removeSpacesFromNumber(fee);
      }
      value = value - fee;
    }

    if(negate=="negate") {
        value = - value;
    }

    //Change the new balance
    result = balance - value;

    var resultNbr = result;
    result = result.toFixed(2);
    result = result.replace(".", ",");
    $("#newBalance").text(getNumberWithSpaces(result));

    //TEMP : display color
    if(resultNbr == 0) {
      $("#newBalance").attr("style","color: #128C29;");
    } else if(resultNbr > 0) {
      $("#newBalance").attr("style","color: #000;");
    } else if (resultNbr < 0) {
      $("#newBalance").attr("style","color: #f00;");
    }



    //Change the value of the auto payment if there is only one -----------------
    if (popin.find(".tbe_impactPayment").find(".ipt_newAmount").length == 1) {
      var autoVal = popin.find(".tbe_impactPayment").find(".ipt_newAmount").val();
      var autoVal = autoVal.replace(",", ".");
      autoVal = orxapi.removeSpacesFromNumber(autoVal);

      if(resultNbr - autoVal < 0) {
        if(resultNbr >= 0) {
          autoVal = resultNbr;
        } else {
          autoVal = 0;
        }
      }
      if (autoVal != "") {
        autoVal = parseFloat(autoVal, 10)
        autoVal = autoVal.toFixed(2);
        autoVal = autoVal.replace(".", ",");
      }
      autoVal = orxapi.getNumberWithSpaces(autoVal)
      popin.find(".tbe_impactPayment").find(".ipt_newAmount").val(autoVal)
    }
    //END --------------


    //Change the balance after auto payments
    var balanceAfterAuto = balance - value;
    if (popin.find(".tbe_impactPayment").find(".ipt_newAmount").length) {
        var $ipts = popin.find(".tbe_impactPayment").find(".ipt_newAmount");
        $ipts.each(function () {
          var valToAdd = $(this).val().replace(",", ".");
          valToAdd = orxapi.removeSpacesFromNumber(valToAdd);
          balanceAfterAuto = balanceAfterAuto - parseFloat(valToAdd);
          balanceAfterAuto = balanceAfterAuto.toFixed(2);
        });
      }

    balanceAfterAuto = parseFloat(balanceAfterAuto, 10);
    balanceAfterAuto = balanceAfterAuto.toFixed(2);
    var balanceAfterAutoNumber =  balanceAfterAuto;
    balanceAfterAuto = getNumberWithSpaces(balanceAfterAuto);
    balanceAfterAuto = balanceAfterAuto.replace(".", ",");

    $("#balanceAfterAutos").text(balanceAfterAuto);
    //TEMP : display color
    if(balanceAfterAutoNumber == 0) {
      $("#balanceAfterAutos").attr("style","color: #128C29;");
    } else if(balanceAfterAutoNumber > 0) {
      $("#balanceAfterAutos").attr("style","color: #000;");
    } else if (balanceAfterAutoNumber < 0) {
      $("#balanceAfterAutos").attr("style","color: #f00;");
    }
  });
  $(".amount").trigger("change");

  //Refresh the balance after auto payments when modifying an auto payment
  $(".ipt_newAmount").on('change', function() {
    //If empty, reset the old amount
    if($(this).val()=="") {
      var oldAmount = $(this).parent().parent().parent().find(".td_oldAmount").text();
      $(this).val(oldAmount);
    }

    var newBalance = $("#newBalance").text();
    newBalance = newBalance.replace(",", ".");
    newBalance = orxapi.removeSpacesFromNumber(newBalance);
    newBalance = parseFloat(newBalance);
    newBalance = newBalance.toFixed(2);

    var balanceAfterAuto = newBalance;

    if (popin.find(".tbe_impactPayment").find(".ipt_newAmount").length) {
        var $ipts = popin.find(".tbe_impactPayment").find(".ipt_newAmount");
        $ipts.each(function () {
          var valToAdd = $(this).val().replace(",", ".");
          valToAdd = orxapi.removeSpacesFromNumber(valToAdd);
          balanceAfterAuto = balanceAfterAuto - parseFloat(valToAdd);
          balanceAfterAuto = balanceAfterAuto.toFixed(2);
        });
      }

    var balanceAfterAutoNumber =  balanceAfterAuto;
    balanceAfterAuto = getNumberWithSpaces(balanceAfterAuto);
    balanceAfterAuto = balanceAfterAuto.replace(".", ",");

    $("#balanceAfterAutos").text(balanceAfterAuto);
    //TEMP : display color
    if(balanceAfterAutoNumber == 0) {
      $("#balanceAfterAutos").attr("style","color: #128C29;");
    } else if(balanceAfterAutoNumber > 0) {
      $("#balanceAfterAutos").attr("style","color: #000;");
    } else if (balanceAfterAutoNumber < 0) {
      $("#balanceAfterAutos").attr("style","color: #f00;");
    }

  });

//Refresh the new balance and balance after auto payments when modifying the amount
  $(".fees").on('change',function() {
    if($(".amount").val() != "") {
      $(".amount").trigger("change");
    }
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

  function addDays(d, j) {
   return new Date(d.getTime() + (1000 * 60 * 60 * 24 * j));
  }

  /** Format from 1234 to 1 234 TODO use the orx function from script.js */
  function getNumberWithSpaces(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
  }

  //Check if CC is a card number
  function isCreditCard(x){
     if (x.length > 19) {
        return (false);
     }

     var sum = 0;
     var mul = 1;
     var l = x.length;
     for (i = 0; i < l; i++) {
      var digit = x.substring(l-i-1,l-i);
      var tproduct = parseInt(digit ,10)*mul;
      if (tproduct >= 10) {
         sum += (tproduct % 10) + 1;
      } else {
         sum += tproduct;
      }
      if (mul == 1) {
         mul = mul + 1;
      } else {
         mul = mul - 1;
      }
     }
     if ((sum % 10) == 0) {
        return (true);
     } else {
        return (false);
     }
  }

  /* Fill the cb details (inputs) fromt he selected card(transaction) */
  function fillCBInfos() {
    if (!$("#slt_selectCard").find("li.selected").hasClass("otn_other")) {

        //If selected an old payment, fill the label and amount
        var selected = $("#slt_selectCard").find("li.selected");

        $(".cbDetail").prop("disabled", true);

        //Select and disable fields
        $('#slt_typeCard option[value='+selected.data('type')+']').attr("selected", "selected");
        $("#ipt_cardNumber").val(selected.data('number'));
        $('#ipt_cardExpireM option[value='+selected.attr("data-expmonth")+']').attr("selected", "selected");
        $('#ipt_cardExpireY option[value='+selected.data('expyear')+']').attr("selected", "selected");
        $("#ipt_firstNameOwner").val(selected.data('firstname'));
        $("#ipt_nameOwner").val(selected.data('lastname'));
        $("#ipt_cardCVV").val("");

        //Set hidden fields
        $("#ipt_transid").val(selected.attr("data-transid"));
      } else {
        //Else empty it
        $(".cbDetail").val("");
        $(".cbDetail").prop("disabled", false);
        $("#ipt_transid").val("");
      }
  }

})(jQuery, orxapi);
