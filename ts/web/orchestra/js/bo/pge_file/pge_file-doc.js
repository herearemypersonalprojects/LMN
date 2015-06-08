(function($, orx){

  $.getScript("./shared/ts/web/js/bo/pge_file/mailer.js");

  $("#pge_file").find("#wrapContent").resizeContentTab();

  $.fn.loadDocBloc = function () {
      var pge = "pge_file-docBloc";
      var url = pge + ".action";
      var id = orxapi.selectors.bodyContent.find("div:eq(0)").data("id");

      $.ajax($.extend({}, orxapi.ajax, {
        url : url,
        data: {id : id},
        success : function(data, textStatus, jqXHR) {
          $(".wrapLeft .inner").html(data);
          $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-docBloc.js");
        }
      }));
  };

  $(this).loadDocBloc();


})(jQuery, orxapi);















