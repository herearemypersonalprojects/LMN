(function($, orx){

  $("#pge_file").find("#wrapContent").resizeContentTab();

  $.fn.resizeNoteZone = function(){
    var h_wrapContent = $("#wrapContent").height(),
        h_noteList = h_wrapContent - $("#adderNoteZone").outerHeight();
    $(this).css("height", h_noteList);
  };

  $("#noteListZone").resizeNoteZone();
  $("#noteListZone").scrollTop($("#noteListZone dl").height());

  $(window).resize(function(){
    $("#noteListZone").resizeNoteZone();
    $("#noteListZone").scrollTop($("#noteListZone dl").height());
  });

  $("#addNoteBtn").on("click", function(event) {
      var url = "processAddNoteAction.action";
      var pge = "pge_file/pge_file-note";
      $.ajax($.extend({}, orxapi.ajax, {
          url : url,
          data : {note:$("#tta_newNote").val(), id:orxapi.selectors.bodyContent.find("div:eq(0)").data("id")},
          success : function(data, textStatus, jqXHR) {
            $("#innerWrapContent").html(data);
            $.getScript("./shared/ts/web/js/bo/" + pge + ".js");
          }
        }));
      return false;
    });


})(jQuery, orxapi);
