(function($, orx){


  //Close popin
  $(".btn_cancelUploadPopin").click(function() {
    var fileName = $("#ppn_detailUpload").data("filename");
    var provider = $("#ppn_detailUpload").data("provider");

    $("#wrapPopin").animate({"padding-top": "+=90px" }, 150, "easeInCubic", function(){ $("#wrapPopin").remove(); });

    //Add the option containing the recently added file
    var option = "<option value='" + fileName + "'></option>";
    $("#slt_oldLoaded").prepend(option);

    //Select the good criterias and re call the prepare action of the search page
    $("#slt_provider").val(provider);
    $("#slt_oldLoaded").val(fileName);

    var fields = $("#proInvoiceForm").serialize();
    orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch", fields);

    $.ajax($.extend({}, orxapi.ajax, {
      url : "pge_dlInvoice.action",
      data : {},
      success : function(data, textStatus, jqXHR) {
        $("#pge_dlInvoice").remove();
        $("#bodyContent").append(data);
        $.getScript("./shared/ts/web/js/bo/pge_dlInvoice.js");
      }
    }));

  });


})(jQuery, orxapi);
