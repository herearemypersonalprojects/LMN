(function($, orx){

  $.getScript("./shared/ts/web/js/bo/pge_file/mailer.js");

  $("#pge_file").find("#wrapContent").resizeContentTab();

  $("#innerWrapContent").find(".btn_seeDetail").click(function(){
    $(this).parents(".invoiceClientBlock").find(".detailZone").toggleClass("hidden");
  });

  $("#innerWrapContent").find(".wrapLeft").resizeLeftZone();

  /* open the send mail page using the "SEND A MAIL TO CLIENT/TO" button */
  $(".invoiceClientBlock").on("click", ".btn_sendMail", function(evt) {
      var action = "prepareDisplayReplyOnly.action";
      orxapi.sendMailPopin($(this), action, false);
      return false;
  });

})(jQuery, orxapi);
