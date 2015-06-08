(function($, orx){
  $("#innerWrapContent").on("click", ".openProfilePopin", function(event) {
    orxapi.madePopin($(this).data("page"), null);
  });

})(jQuery, orxapi);
