(function($, orx){


  $("#pge_file").find("#wrapContent").resizeContentTab();
  $("#innerWrapContent").find(".wrapLeft").resizeLeftZone();

  // $(document).bind('dragover', function (e) {
      // var dropZoneDef = $("#innerWrapContent").find(".dropzone"),
          // dropZoneReplace = $("#innerWrapContent").find(".fileDetailZone"),
          // timeout = window.dropZoneTimeout;
//
      // if (!timeout) {
          // dropZoneDef.addClass("in");
          // dropZoneReplace.addClass("in");
          // alert("in");
          // // TODO: call ajax to add the document
      // } else {
          // clearTimeout(timeout);
      // }
      // window.dropZoneTimeout = setTimeout(function () {
          // window.dropZoneTimeout = null;
          // dropZoneDef.removeClass("in");
          // dropZoneReplace.removeClass("in");
      // }, 100);
  // });
  // $(document).bind("drop dragover", function (e) {
      // e.preventDefault();
  // });
//
  var fileDetailList = $("#wrapContent").find(".fileDetailList");

  fileDetailList.find(".btn_history").click(function(){
    $(this).parents(".fileDetailZone").find(".historyDetail").toggleClass("hidden");
  });


  $("#icbx_bookComplet").switchbutton({
      classes: 'ui-switchbutton-thin',
      duration: 200,
      checkedLabel: 'OK',
      uncheckedLabel: 'KO'
  });

  /* Change the doc status. (partiel <-> complete) */
  $("#icbx_bookComplet").change(function() {
        var boFileId = orxapi.selectors.tabsBarZone.find("li.active").data("id");
        if ($(this).is(':checked')) {
            $(this).attr('checked','checked');
            var docStatus = 3;
        } else {
            $(this).removeAttr('checked');
            var docStatus = 2;
        }
        $.ajax($.extend({}, orxapi.ajax, {
            url : "changeDocStatus.action",
            data : {docStatus: docStatus, boFileId: boFileId},
            success : function(data, textStatus, jqXHR) {
              if(docStatus == 3) {
                $(".documentSideTab").trigger("click");
              } else {
                $("#btn_sendMail").addClass("disabled");
              }
            }
        }));

    });

  /* Edit the document name */
  $(".span_fileName").dblclick(function() {
    $(this).addClass("hidden");
    $(this).next(".span_editFileName").removeClass("hidden");
    var ipt = $(this).next(".span_editFileName").children(".ipt_editFileName");
    ipt.focus();

    // overflow
    var overflowClickOut = $("<div/>", { "id" : "overlayClickOut" });
    overflowClickOut.appendTo("body");
    overflowClickOut.click(function(){

    var url = "renameDocument.action",
          boFileId = ipt.data("file-id"),
          code = ipt.data("code"),
          newFileName = ipt.val();

      $.ajax($.extend({}, orxapi.ajax, {
        url : url,
        data: {newFileName:newFileName, boFileId:boFileId, code:code},
        success : function(data, textStatus, jqXHR) {
            $(this).loadDocBloc();
        }
      }));

      overflowClickOut.remove();

    });
  });


  /* modify file name */
  $(".ipt_editFileName").keypress(function(e) {
    if(e.which == 13) {
      var that = $(this),
          newFileName = that.val(),
          url = "renameDocument.action";

     var boFileId = that.data("file-id");
     var code = that.data("code");

      $.ajax($.extend({}, orxapi.ajax, {
        url : url,
        data: {newFileName:newFileName, boFileId:boFileId, code:code},
        success : function(data, textStatus, jqXHR) {
          $(this).loadDocBloc();
        }
      }));

      $("#overlayClickOut").remove();
    }
  });

  $(".manualTravelDocGeneration").click(function() {
      var selectedTempCode = $(this).data("traveldoctemplatecode"),
          latestDocCode = $(this).data("code"),
          id = orxapi.selectors.tabsBarZone.find("li.active").data("id");

      var params = "id=" + id + "&travelDocTemplateCode=" + selectedTempCode;

      if (typeof(latestDocCode) != "undefined") {
        params = params + "&code=" + latestDocCode
      }
      orxapi.madePopin("ppn_file-previsualizeTravelDoc", params);
  });

  $(".refreshTravelDocs").click(function() {
      var id = orxapi.selectors.tabsBarZone.find("li.active").data("id");
      $.ajax($.extend({}, orxapi.ajax, {
        url : "refreshDocument.action",
        data: {id:id},
        success : function(data, textStatus, jqXHR) {
          $(this).loadDocBloc();
        }
      }));
  });


  /** Delete document */
  $(".btn_trashPdf").click(function() {
      var latestDocCode = $(this).data("code"),
      id = orxapi.selectors.tabsBarZone.find("li.active").data("id");
      $.ajax($.extend({}, orxapi.ajax, {
        url : "deleteTravelDocument.action",
        data: {id:id, code:latestDocCode},
        success : function(data, textStatus, jqXHR) {
          $(this).loadDocBloc();
        }
      }));
  });

  /* open the send mail page using the "SEND A MAIL TO CLIENT/TO" button */
  $(".docBlockZone").on("click", ".btn_sendMail", function(evt) {
    if(!$(this).hasClass("disabled")) {
        var action = "prepareDisplayReplyOnly.action";
        orxapi.sendMailPopin($(this), action, false);
        return false;
    }
  });

  /*--------------------------------------------------------------------------------*/
  /*                             Upload documents started                           */
  /*--------------------------------------------------------------------------------*/


  $("form input[type='file']").change(function() {
    var pdfFormat = /\.pdf$/i,
        boFileId = orxapi.selectors.tabsBarZone.find("li.active").data("id"),
        code = $(this).data("code"),
        formData,
        file = $(this).val();

    // check if the file is a pdf
    if (file.search(pdfFormat) == -1) {
        var fileTypeErrMsg = $(this).data("file-type-error-msg");
        alert(fileTypeErrMsg);
        $(this).val("");
        return;
    }

    if (window.FormData !== undefined) {
      // we can use formData for ajax file upload. this functionality is not available in IE7,8,9
      formData = new FormData($(this).parent('form')[0]);
      formData.append("boFileId", boFileId);
      if (typeof(code) != "undefined") {
          formData.append("code", code);
      }
      $.ajax($.extend({}, orxapi.ajax, {
        url : "uploadTravelDocument.action",
        data: formData,
        contentType:false,
        processData:false,
        cache:false,
        success : function(data, textStatus, jqXHR) {
          if ($(data).find("#maxUploadSize").length) {
            // max file size for upload reached.
            showMaxFilesizePopin(data)
          } else {
            $(this).loadDocBloc();
          }
        }
      }));
    } else {
      // IE7,8,9 : use iFrame ajax call for IE.
      var $form = $(this).parent('form')[0];
      // add hidden inputs to the form
      $(this).after($("<input/>",{ "name":"boFileId", "value": boFileId}));
      if (typeof code != "undefined") {
        $(this).after($("<input/>",{ "name":"code", "value": code}));
      }
      ieFileUpload($form, "uploadTravelDocument.action");
    }
  });


  /**
   * Use iFrame to simulate ajax calls with IE during the file upload.
   * http://viralpatel.net/blogs/ajax-style-file-uploading-using-hidden-iframe/
   * @param {Object} form
   * @param {Object} actionUrl the url to call
   */
  function ieFileUpload (form, actionUrl) {
    // Create the iframe...
    var iframe = document.createElement("iframe");
    iframe.setAttribute("id", "upload_iframe");
    iframe.setAttribute("name", "upload_iframe");
    iframe.setAttribute("width", "0");
    iframe.setAttribute("height", "0");
    iframe.setAttribute("border", "0");
    iframe.setAttribute("style", "width: 0; height: 0; border: none;");

    // Add to document...
    form.parentNode.appendChild(iframe);
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
      if ($(content).find("#maxUploadSize").length) {
        // max file size for upload reached.
        showMaxFilesizePopin(content)
      } else {
        $(this).loadDocBloc();
      }

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


  function showMaxFilesizePopin(content) {
    var contentDialog = $("<div/>",{ "id":"contentDialog"});
    contentDialog.html(content);
    contentDialog.dialog($.extend({}, orx.plugin.dialog, {
        buttons:{
          "Fermer": function() {
            $(this).dialog("destroy").remove();
            return false;
           }
        }
    }));
  }

  /*--------------------------------------------------------------------------------*/
  /*                             Upload documents ended                             */
  /*--------------------------------------------------------------------------------*/

  $("#addFileZone .btnAddFileZone.tmpMode").find(".btn-large.fileinput-button").css("width", $("#addFileZone").find(".btnAddFileZone").outerWidth() - $("#addFileZone").find(".dropdown-toggle").innerWidth() - 20 - 12);
  $(window).resize(function() {
    $("#addFileZone .btnAddFileZone.tmpMode").find(".btn-large.fileinput-button").css({"width" : $("#addFileZone").find(".btnAddFileZone").outerWidth() - $("#addFileZone").find(".dropdown-toggle").innerWidth() - 20 - 12 });
  });


  $("#addFileZone .btnAddFileZone.sgleMode").find(".btn-large.fileinput-button").css("width", $("#addFileZone").find(".btnAddFileZone").width() - 30);
  $(window).resize(function() {
    $("#addFileZone .btnAddFileZone.sgleMode").find(".btn-large.fileinput-button").css("width", $("#addFileZone").find(".btnAddFileZone").width() - 30);
  });

})(jQuery, orxapi);
