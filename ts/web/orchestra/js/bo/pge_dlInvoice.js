(function($, orx){


  $("#searchZone").find(".datePicker").datepicker($.extend({}, { }, orx.plugin.datepicker));
  orxapi.pictoDate();

  reinitTables();

  retrieveProInvoiceLastSearch();

  //Launch search after uploading a file
  var filename = $("#wrapper").find("#pge_dlInvoice").data("filename");
  var equalizeHtableZone = function() {
    var hBodyContent = $("#bodyContent").height(),
        hSearchZone = $("#searchZone").height(),
        hTableZone = (hBodyContent - hSearchZone)/2,
        hTableScroll = hTableZone - 130;

        $(".tableScrollZone").css("height", hTableScroll);
  };
  equalizeHtableZone();

  $(window).resize(function(){
    equalizeHtableZone();
  });

  /*tabeZone.find(".niceScroll").getNiceScroll().hide();*/

  $("#searchZone").find(".btn_uploadInvoice").click(function(){
    orxapi.madePopin($(this).data("page"), "pge_dlInvoice");
    //Rebuild the scroll to prevent a crash of the table behind the popin
    $("#wrapper").find(".niceScroll").niceScroll({cursorborder:"1px solid #fff",cursorcolor:"#414141",boxzoom:false,zindex:9});
  });


  //Click on the "FILTER" button
  $(".accountingZone").on("click", "#btn_proInvoiceSearch", function(event) {
    var action = $(this).data("action") + ".action";
    var params = $("#proInvoiceForm").serialize();
    $.ajax($.extend({}, orxapi.ajax, {
        url : action,
        data: params,
        success : function(data, textStatus, jqXHR) {
          $("#pge_dlInvoice .boFileLists").remove();
          $("#pge_dlInvoice").append(data);
          equalizeHtableZone();
          reinitTables();
          orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch", params);
          orxapi.storeTabCookie();
        }
    }));
  });


  /* Reinit each table headers */
  function reinitTables() {
    $("#wrapper").find(".invoiceTableZone").find(".tbe_thead").each(function() {
        var tbeHead = $("<table/>", { "class":"table table-striped table-bordered table-condensed thead tFixed" });
        $(this).parents(".tableScrollZone").before(tbeHead);
        $(this).clone().appendTo(tbeHead);
        $(this).hide();
    });
    $("#wrapper").find(".invoiceTableZone").find(".tbe_tfoot").each(function(){
      var tbefoot = $("<table/>", { "class":"table table-striped table-bordered table-condensed table-sortable tfoot tFixed" });
      $(this).parents(".tableScrollZone").after(tbefoot);
      $(this).clone().appendTo(tbefoot);
      $(this).hide();
    });

    $("#wrapper").find(".niceScroll").niceScroll({cursorborder:"1px solid #fff",cursorcolor:"#414141",boxzoom:false,zindex:9});
  };

  function retrieveProInvoiceLastSearch() {
      var lastSearch = orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch");

      if (lastSearch != null && lastSearch != "") {
        $("#proInvoiceForm").deserialize(lastSearch);
        $("#proInvoiceForm").find("input[value!='']:not(:disabled):not(:checkbox), select[value!='']:not(:disabled)").addClass("lightValue");
        $("#btn_proInvoiceSearch").trigger("click");
      }
    }

})(jQuery, orxapi);

