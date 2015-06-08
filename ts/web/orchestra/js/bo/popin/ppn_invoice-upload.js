(function($, orx){

  var orx_dp = orx.plugin.datepicker,
      popin = $("#wrapPopin").find(".popin");

  popin.find(".datePicker").datepicker($.extend({}, orx_dp, { minDate: 0 }));
  orx.pictoDate();


  $("#btn_uploadValidate").click(function() {
    var that = $(this),
        csvFormat = /\.csv$/i,
        $fileIpt = $("#fle_ppnFile"),
        fileVal = $fileIpt.val(),
        formData,
        pge = that.data("page"),
        page = pge +".action";

    // check if the file is a csv
    if (fileVal.search(csvFormat) == -1) {
        var fileTypeErrMsg = $(this).data("file-type-error-msg");
        alert(fileTypeErrMsg);
        $(this).val("");
        return;
    }

    if (window.FormData !== undefined) {
      // we can use formData for ajax file upload. this functionality is not available in IE7,8,9
      formData = new FormData($("#ppn_upLoadInvoice").find("form")[0]);
      $("#wrapPopin").animate({"padding-top": "+=90px" }, 150, "easeInCubic", function() {
        var wrapPopin = $("<div >", { id:"wrapPopin" }),
            formId = pge + "Form";

        $("#wrapPopin").remove();
        $.ajax($.extend({}, orxapi.ajax, {
          url : page,
          data: formData,
          contentType:false,
          processData:false,
          cache:false,
          success: function(data, textStatus, jqXHR) {
            processResults($(data), pge);
          }
        }));

        orxapi.resizePopin();
      });
    } else {
      // IE7,8,9 : use iFrame ajax call for IE.
      // add hidden inputs to the form
      var $form = $("#ppn_upLoadInvoice").find("form")[0];
      ieFileUpload($form, page, pge);
    }

  });

  function processResults(resultContent, pge) {
      var wrapPopin = $("<div >", { id:"wrapPopin" });
      wrapPopin.appendTo("body");
      var $result = $(resultContent);
      $result.appendTo(wrapPopin);
      $.getScript("./shared/ts/web/js/bo/popin/" + pge + ".js");

      // data width and height
      var popin = wrapPopin.find(".popin"),
          widthPopin = popin.data("popin").width,
          heightPopin = popin.data("popin").height;

      wrapPopin.css({"padding-top": "90px" });
      wrapPopin.animate({"padding-top": "-=90px" }, 150, "easeOutCubic");
      orxapi.resizePopin();

      if (widthPopin) { popin.css("width", widthPopin); }
      if (heightPopin) { popin.css("height", heightPopin); }
      //Rebuild the scroll to prevent a crash of the table behind the popin
      $("#wrapper").find(".niceScroll").niceScroll({cursorborder:"1px solid #fff",cursorcolor:"#414141",boxzoom:false,zindex:9});

  }


  /**
   * Use iFrame to simulate ajax calls with IE during the file upload.
   * http://viralpatel.net/blogs/ajax-style-file-uploading-using-hidden-iframe/
   * @param {Object} form
   * @param {Object} actionUrl the url to call
   */
  function ieFileUpload(form, actionUrl, pge) {
    // Create the iframe...
    var iframe = document.createElement("iframe");
    iframe.setAttribute("id", "upload_iframe");
    iframe.setAttribute("name", "upload_iframe");
    iframe.setAttribute("width", "0");
    iframe.setAttribute("height", "0");
    iframe.setAttribute("border", "0");
    iframe.setAttribute("style", "width: 0; height: 0; border: none;");

    // Add to document...
    $(".bodyZone")[0].appendChild(iframe);
    window.frames['upload_iframe'].name = "upload_iframe";

    iframeId = document.getElementById("upload_iframe");

    // Add event...
    var eventHandler = function () {

      if (iframeId.detachEvent) {
        iframeId.detachEvent("onload", eventHandler);
      } else {
        iframeId.removeEventListener("load", eventHandler, false);
      }

      // Message from server...
      if (iframeId.contentDocument) {
          content = iframeId.contentDocument.body.innerHTML;
      } else if (iframeId.contentWindow) {
          content = iframeId.contentWindow.document.body.innerHTML;
      } else if (iframeId.document) {
          content = iframeId.document.body.innerHTML;
      }
      $("#wrapPopin").animate({"padding-top": "+=90px" }, 150, "easeInCubic", function() {
        $("#wrapPopin").remove();
        processResults(content, pge);
      });

      // Del the iframe...
      setTimeout('iframeId.parentNode.removeChild(iframeId)', 250);
    }

    if (iframeId.addEventListener) iframeId.addEventListener("load", eventHandler, true);
    if (iframeId.attachEvent) iframeId.attachEvent("onload", eventHandler);

    form.setAttribute("encoding", "multipart/form-data");
    form.setAttribute("target", "upload_iframe");
    form.setAttribute("action", actionUrl);

    // Submit the form...
    form.submit();
  }


})(jQuery, orxapi);
