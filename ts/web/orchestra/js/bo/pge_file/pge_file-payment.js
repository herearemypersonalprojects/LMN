(function($, orx) {

  $("#pge_file").find("#wrapContent").resizeContentTab();

  var h = 0,
      h_buttonPaymentList = $("#wrapContent").find("#buttonPaymentList").outerHeight(),
      buttonsList = $("#buttonPaymentList");

  /* resizing payment block */
  $(window).resize(function(){
    $("#wrapContent").find(".boxTitleBlock.resizable").css("min-height", $("#pge_file").find("#wrapContent").height() - h_buttonPaymentList - 1);
    $("#wrapperDefault").find(".boxTitleBlock.resizable").each(function(){
      h = Math.max(h, $(this).innerHeight());
    }).css({"min-height": h+"px"});
  });
  $("#wrapContent").find(".boxTitleBlock.resizable").css("min-height", $("#pge_file").find("#wrapContent").height() - h_buttonPaymentList - 1);
  $("#wrapperDefault").find(".boxTitleBlock.resizable").each(function(){
    h = Math.max(h, $(this).innerHeight());
  }).css({"min-height": h+"px"});

  $("#btn_historyPayment").click(function() {
    $("#wrapperDefault").toggleClass("hidden");
    $("#wrapperHistory").toggleClass("hidden");
  });


  buttonsList.find(".btn-group a, #btn_autoPayment").not(".disabled").click(function() {



    var params = "id=" + $("#tabsBarZone").find("li.active").data("id");
    params = params + "&type=" + $(this).data("type");
    orxapi.madePopin($(this).data("page"), params);
  });

  $(".a_attemptResp").click(function() {
     var req = $(this).parent().find("#span_req").text();
     var reqTitle = $(this).parent().find("#span_req").data("title");

     var resp = $(this).parent().find("#span_resp").text();
     var respTitle = $(this).parent().find("#span_resp").data("title");

     var contentDialog = $("<div/>",{ "id":"contentDialog"});
     contentDialog.html(reqTitle + " :<br/><pre>" + req + "</pre><br/>" + respTitle + " :<br/><pre>" + resp + "</pre>");

     contentDialog.dialog($.extend({}, orx.plugin.dialog, {
        title : $(this).data("title"),
        width : 700,
        height : 500,
        buttons:{
          "Fermer": function() {
            $(this).dialog("destroy").remove();
            return false;
           }
        }
     }));
  });

  //Edit auto payment
  $(".a_editAutoPayment").click(function() {
      var autoId = $(this).parents("div").data("autoid");

      var params = "id=" + $("#tabsBarZone").find("li.active").data("id");
      params = params + "&paymentAuto.id=" + autoId;

      orxapi.madePopin($(this).data("page"), params);
  });

  //Delete auto payment
  $(".a_deleteAutoPayment").click(function() {
      var autoId = $(this).parents("div").data("autoid");
      var action = $(this).data("action") + ".action";
      var params = "id=" + $("#tabsBarZone").find("li.active").data("id");
      params = params + "&paymentAuto.id=" + autoId;

      var contentDialog = $("<div/>",{ "id":"contentDialog"});
      contentDialog.html($(this).data("dialogcontent"));

      contentDialog.dialog($.extend({}, orx.plugin.dialog, {
          title : $(this).data("dialogtitle"),
          buttons:{
            "Annuler": function() {
              $(this).dialog("destroy").remove();
              return false;
             },
            "OK": function() {
              $(this).dialog("close");
              $.ajax($.extend({}, orxapi.ajax, {
                  url : action,
                  data: params,
                  success : function(data, textStatus, jqXHR) {
                    $("#innerWrapContent").parent().html(data);
                    $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-payment.js");
                  }
              }));
            }
          }
      }));
  });

  //SEND RECEIPT
  $(".a_receipt").click(function() {
      var paymentId = $(this).parents("td").data("paymentid");
      var action = $(this).data("action") + ".action";

      var params = "paymentId=" + paymentId;
      params = params + "&id=" + $("#tabsBarZone").find("li.active").data("id");


      var contentDialog = $("<div/>",{ "id":"contentDialog"});
      contentDialog.html($(this).data("dialogcontent"));

      contentDialog.dialog($.extend({}, orx.plugin.dialog, {
          title : $(this).data("dialogtitle"),
          buttons:{
            "Annuler": function() {
              $(this).dialog("destroy").remove();
              return false;
             },
            "OK": function() {
              $(this).dialog("close");
              $.ajax($.extend({}, orxapi.ajax, {
                  url : action,
                  data: params,
                  success : function(data, textStatus, jqXHR) {
                    $("#innerWrapContent").parent().html(data);
                    $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-payment.js");
                  }
              }));
            }
          }
      }));
  });


})(jQuery, orxapi);


























