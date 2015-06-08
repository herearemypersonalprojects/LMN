(function($, orx){

  /* call resizeContentTab function in pge_file */
  $("#pge_file").find("#wrapContent").resizeContentTab();

  $("#innerWrapContent").on("click", "#btn_editClient", function(event) {
  //$("#innerWrapContent").find("#btn_editClient").click(function(event) {
      event.stopPropagation();
      var params = "id=" + $("#tabsBarZone").find("li.active").data("id");
      orxapi.madePopin($(this).data("page"), params);
      orxapi.resizePopin();
      // after modify btn click - btn stays focused so if we click enter - ppn reopens. dirty fix:
      $(this).blur();
  });

})(jQuery, orxapi);
