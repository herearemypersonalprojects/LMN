(function($, orx){

  var id = orxapi.selectors.bodyContent.find("div:eq(0)").data("id");

  /* Load mailer part for the single modification block  */
  $.fn.loadMailer = function () {

    var $firstModifBlock = $(this);

    var idCode = $firstModifBlock.find(".IDCode").text();
    var modifType = $firstModifBlock.find(".modifType").text();
    var modifId = $firstModifBlock.data("modif-id");

    $.ajax($.extend({}, orxapi.ajax, {
        url : "prepareModifsMail.action",
        data: {modifId : modifId, id : id, idCode : idCode, modifType : modifType},
        success : function(data, textStatus, jqXHR) {
          $(".tbe_mailer").remove();
          $("#innerWrapContent .wrapRight").find("div.inner").html(data);
          $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-modifsEmail.js");
        }
      }));
  };


  /* Get the list of modifications */
  $.ajax($.extend({}, orxapi.ajax, {
    url : "prepareModifsList.action",
    data: {id:id},
    success : function(data, textStatus, jqXHR) {
      // set modif blocks
      $("#innerWrapContent .wrapLeft").find("div.inner").html(data);
      $.getScript("./shared/ts/web/js/bo/pge_file/pge_file-modifsList.js");

      // load the mailer for the 1st modification block
      if ($("#innerWrapContent").find(".singleBlock").length) {
        $("#innerWrapContent").find(".singleBlock:eq(0)").addClass("active").loadMailer();
      } else {
        // load mailer without mails
        $().loadMailer();
      }
    }
  }));


})(jQuery, orxapi);

