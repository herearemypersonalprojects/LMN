(function($, orx) {


  /*--------------------------------------------------------------------------------*/
  /*                  Base page functions                                           */
  /*--------------------------------------------------------------------------------*/

  var orx_dp = orx.plugin.datepicker;
  $("#searchZone").find(".datePicker").datepicker($.extend({}, {}, orx_dp));
  orxapi.pictoDate();
  $(".table-sortable").tablesorter();

  /* Resize height content for scroll  */
  $.fn.resizeContentTab = function () {
    var windowHeight = $(window).height(),
        contentTabHeight = $(window).height() - $("#headerBarZone").height() - $("#tabsBarZone").height() - ($("#supHeaderBarZone").length ? $("#supHeaderBarZone").height() : 0) - 3;
    $(this).css("height", contentTabHeight);
  };
  /* equalize height  */
  $.fn.equalizeHeight = function(){
    $(this).css("height","auto");
    $(this).css("min-height", $("#pge_reg").find("#wrapContent").height()+"px");
    return this.height( Math.max.apply(this, $.map( this , function(e){ return $(e).height(); }) ) );
  };


  /* Call function */
  $(window).resize(function(){
    $("#pge_reg").find("#wrapContent").resizeContentTab();
    $("#pge_reg").find(".wrapLeft, .wrapRight").equalizeHeight();
  });


  $("#pge_reg").find("#wrapContent").resizeContentTab();
  $("#pge_reg").find(".wrapLeft, .wrapRight").equalizeHeight();

  /* SELECTABLE TABLE */
  $("#destDropTable").selectable({
    filter:"tbody tr"
  });

  $("#droppedListZone").find(".btn_removeSelectedDrop").click(function() {

    // enable deleted trs in resultTable
    $("#destDropTable").find("tr.ui-selected").each(function() {
      $("#tbe_resultList tbody").find("tr[data-id='" + $(this).data("id") + "']").removeClass("added");
    });

    $("#destDropTable").find("tr.ui-selected").remove();
    if (!$("#destDropTable").find("tbody tr").length){
      $("#pge_reg .wrapRight").find(".alert-info").removeClass("hidden");
      $("#droppedListZone").addClass("hidden");
    }
  });

  $("#wrapContent").find(".iCheckBox").switchbutton({
      classes: 'ui-switchbutton-thin',
      duration: 100,
      labels: false
  });




  /*--------------------------------------------------------------------------------*/
  /*                  Search  functions                                           */
  /*--------------------------------------------------------------------------------*/

  /* Search by reference click */
 $("#pge_reg").find(".searchRegByRef").click(function(evt) {
    evt.preventDefault();
    var $this = $(this);
    var refVal = $this.parent().find("#ipt_ibkStatus").val();
    refVal = $.trim(refVal)

    $.ajax($.extend({}, orxapi.ajax, {
      url : "reg-searchByRef.action",
      data: {reference : refVal},
      success : function(data, textStatus, jqXHR) {
        if (data == "") {
          // no result found
          return false;
        }
        $("#bodyContent").find("#droppedListZone").removeClass("hidden");
        var tableDropList = $("#bodyContent").find("#destDropTable tbody");
        $(data).each(function(i) {
            var trWithSameFileId = tableDropList.find("tr[data-id='" + $(this).data("id") + "']");
            if (trWithSameFileId == null || trWithSameFileId.length == 0) {
              $(this).appendTo(tableDropList);
              $("#tbe_resultList tbody").find("tr[data-id='" + $(this).data("id") + "']").removeClass("ui-selected").addClass("added");
            }
        });
        return false;
      }
    }));
 });

  /** Reference input : enter clicked : execute search */
  $("#ipt_ibkStatus").keypress(function(e) {
    if (e.which == 13) {
      $("#pge_reg").find(".searchRegByRef").trigger("click");
    }
  });

  /** On input, select keybord enter : execute search */
  $(".regSearchForm").find("input, select").keypress(function(e) {
    if (e.which == 13) {
     $(".regSearchForm").find(".searchBtn").trigger("click");
     return false;
    }
  });

  /* Global search click */
  $("#regSearchBtn").click(function(evt) {
    evt.preventDefault();
    var params = $("#regSearchForm").serialize();
    var url = "processRegSearch.action";
    var pge = "pge_reg/pge_resultList";

    if ($("#regSearchForm").valid()) {

      $.ajax($.extend({}, orxapi.ajax, {
        url : url,
        data : params,
        success : function(data, textStatus, jqXHR) {
          $(".regResultContainer").html(data);
          // remove double click on file
          $("#tbe_resultList").removeClass("table-result-files");
          // add the added class to added file
          $("#tbe_resultList tbody tr").each(function (i) {
           var $newTr = $(this);
           $("#destDropTable tbody tr").each(function (j) {
             var $destTr = $(this);
             if ($newTr.data("id") == $destTr.data("id")) {
               $newTr.addClass("added");
               return false;
             }
           });
          });
          orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch", params);
          $.getScript("./shared/ts/web/js/bo/" + pge + ".js");
          $("#pge_reg").find(".wrapLeft, .wrapRight").equalizeHeight();
          orxapi.storeTabCookie();
        }
      }));
    }
    return false;
  });

  // retrieve last search
  var lastSearch = orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch");
  if (lastSearch != null && lastSearch != "") {
    $("#regSearchForm").deserialize(lastSearch);

    if ($(".canalIpt").length > 0 && $(".canalIpt").val() != "") {
      var selectOrgValue = orxapi.getUrlParameter("criteria.bookingChannel.organization", lastSearch);
      var selectUserValue = orxapi.getUrlParameter("criteria.bookingChannel.agent", lastSearch);
      orxapi.completeOrg(selectOrgValue, selectUserValue);
      if(selectOrgValue!="" && selectUserValue!=""){
          orxapi.completeUser(selectOrgValue, selectUserValue);
       }
    }
    $("#regSearchForm").find("input[value!='']:not(:disabled):not(:checkbox), select[value!='']:not(:disabled)").addClass("lightValue");
    $("#regSearchBtn").trigger("click");
  }

  $("#ipt_ptnCityStart").tsautocomplete({
    autocomplete : {
      focus : function(event, ui) {
        $(this).val(ui.item.code);
        return false;
      },
      select : function(event, ui) {
        $(this).val(ui.item.code);
        return false;
      },
      source : function(request, response) {
        $.post("autocompleteDeparture", $.extend(request, {
          code : this.element.val()
        }), function(data) {
          response(data);
        }, "json");
      }
    },
    type : false
  });

  $("#ipt_ptnDesti").tsautocomplete({
    autocomplete : {

      focus : function(event, ui) {
        $(this).val(ui.item.code);
        return false;
      },
      select : function(event, ui) {
        $(this).val(ui.item.code);
        return false;
      },
      source : function(request, response) {
        $.post("autocompleteDestination", $.extend(request, {
          code : this.element.val()
        }), function(data) {
          response(data);
        }, "json");
      }
    },
    type : false
  });




  // Validate reg selection (open modif ppn)
  $(".btn_addRegulation").not(".disabled").on("click", function(evt) {
    evt.preventDefault();
    var params = "id=" + getAllAddedFileIds();
    orxapi.madePopin($(this).data("page"), params);
    // unblur this btn : if not, after click btn stays focused and every time we hit enter it will reopen ppn
    $(this).blur();
  });


  /** Get all added boFile ids */
  function getAllAddedFileIds() {
    var ids = "";
    var filesNmb = $("#droppedListZone tbody tr").length;
    $("#droppedListZone tbody tr").each(function(i) {
      var $this = $(this);
      ids = ids += $this.data("id");
      if (i < filesNmb-1) {
        ids += ",";
      }
    });
    return ids;
  }

  // multiselects
  $(".multiselect").multiselect();
  $(".ui-multiselect").css("width","224");

})(jQuery, orxapi);

