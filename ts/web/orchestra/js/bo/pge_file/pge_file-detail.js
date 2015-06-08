(function($, orx){

  /* call resizeContentTab function in pge_file */
  $("#pge_file").find("#wrapContent").resizeContentTab();

//  $(".a_export_mcto").click(function() {
//      var id = orxapi.selectors.bodyContent.find("div:eq(0)").data("id");
//
//	  $.ajax($.extend({}, orxapi.ajax, {
//	        url : "processMCTOExportAction.action",
//	        data: {id : id},
//	        success : function(data, textStatus, jqXHR) {}
//	  }));
//  });

})(jQuery, orxapi);



