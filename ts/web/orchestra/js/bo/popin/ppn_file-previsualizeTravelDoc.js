(function($, orx) {

  var orx_dp = orx.plugin.datepicker,
      popin = $("#wrapPopin").find(".popin");
      travelDocContent = popin.find(".bodyTravelDoc"),
      travelDocTemplateCss = $("#travelDocPrevisualization").data("csspath");

  /* add wysiwyg on #bodyTravelDoc */
  tinyMCE.init($.extend({}, orx.plugin.wysiwygLight, {
    elements : "bodyTravelDoc",
    resizable:false,
    width: "950",
    height: "700",
    content_css : travelDocTemplateCss
  }));


  popin.find(".validatePpn").click(function(event) {
    event.preventDefault();

    var content = escape(tinyMCE.getInstanceById("bodyTravelDoc").getContent()),
    id = $("#tabsBarZone").find("li.active").data("id"),
        latestTravelDocCode = $("#latestTravelDocCode").val(),
        selectedTemplateCode = $("#selectedTemplateCode").val();

    var params = "id=" + id + "&travelDocTemplateCode=" + selectedTemplateCode + "&content=" + content
    if (latestTravelDocCode != "") {
      params = params + "&code=" + latestTravelDocCode
    }

    $("#wrapPopin").remove();
    $.ajax($.extend({}, orxapi.ajax, {
      url : "generateTravelDocument.action",
      data: params,
      success : function(data, textStatus, jqXHR) {
        $(this).loadDocBloc();
      }
    }));
  });

})(jQuery, orxapi);

