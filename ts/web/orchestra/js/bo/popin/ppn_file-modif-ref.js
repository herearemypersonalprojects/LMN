(function($, orx) {

  $.validator.addMethod("requiredValue", function(value, element) {return value !== '';},"");
  $.validator.addClassRules("notEmpty", {requiredValue:true});


  $("#ppn_file-modifProcessForm").validate({
      // ignore in global validation
      ignore: ".ignoreGlobalValidation :input"
  });

  var popin = $("#wrapPopin").find(".popin");

  /* resize popin */
  orx.resizePopin();
  $(window).resize(function() {
    orxapi.resizePopin();
  });

  // process create/edit modif ppn
  popin.find("#btn_processRefModif").click(function(event) {

    event.preventDefault();

    // global jquery validation
    if (!$("#ppn_file-edit-reference").valid()) {
      return false;
    }

    var currentModifId = $("#modifId").val();

    // validate if in each block we modified at least 1 element
    var validMsg = $("#ppn_file-edit-reference").validateModification();

    if (validMsg != "") {
      // there is modification element that is not valid
      var contentDialog = $("<div/>",{ "id":"contentDialog"});
      contentDialog.html(validMsg);
      contentDialog.dialog($.extend({}, orx.plugin.dialog, {
            buttons:{
              "Fermer": function() {
                $(this).dialog("destroy").remove();
                return false;
               }
            }
      }));
      return false;
    }

    var params = $("#ppn_file-edit-reference").serialize();

    // ognl fix: remove empty values from the list of params
    params = params.replace(/[^&]+=\.?(?:&|$)/g, '');

    $("#wrapPopin").remove();
    $.ajax($.extend({}, orxapi.ajax, {
      url : "file-modifRefProcess.action",
      data: params,
      success : function(data, textStatus, jqXHR) {
        // reload this
        $("#headerPage").find(".numfileZone").html(data);

        if ($("#innerWrapContent").find(".modificationBlock[data-modif-id='" + currentModifId + "']").find(".modificationSubFile").length) {
          // replace existing info with the latest ones
          $("#innerWrapContent").find(".modificationBlock[data-modif-id='" + currentModifId + "']").find(".modificationSubFile").html(data);
        } else {
          // add reference change infos into the block
          var liTab = $("<li/>",{ "class":"modificationSubFile" });
          $(data).appendTo(liTab)
          $("#innerWrapContent").find(".modificationBlock[data-modif-id='" + currentModifId + "']").find(".modificationList").append(liTab);
        }
      }
    }));

  });

  /*--------------------------------------------------------------------------------*/
  /*                       VALIDATION OF MODIFICATIONS                              */
  /*--------------------------------------------------------------------------------*/

  $.fn.validateModification = function () {
    var $form = $(this),
        validMsg = "";

    // check if input values are different from initial ones
    validMsg = orxapi.validateReferenceZone($form);
    return validMsg;
  };

})(jQuery, orxapi);

