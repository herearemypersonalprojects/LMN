
(function($){
  $("#tbe_resultList").on("click", "th.th_sortable", function() {
    var that = $(this),
        activeItem = $("#wrapSideBar .item.active, #wrapSideBar .searchItem.active"),
        pge =  activeItem.data("page"),
        url = pge + ".action",
        data = {},
        // code is used as a code in configurable lists search or as a userSearchId
        code = activeItem.data("sidebar-code"),
        columnCode = that.data("header-code"),
        ascOrder = !that.hasClass("th_headerSortUp");

    data["code"] = code;

    if ($("#wrapContent form.searchForm").length == 0) {
      // no-form case (list is displayed directly when a config list or favorite list is clicked)
      data["columnsToSort[1].code"] = columnCode;
      data["columnsToSort[1].ascOrder"] = ascOrder;
    }

    $.ajax($.extend({}, orxapi.ajax, {
      url : url,
      data : data,
      success : function(data, textStatus, jqXHR) {
        $("#wrapContent").html(data).removeClass().addClass(pge);

        var searchForm = $("#wrapContent form.searchForm");
        if (searchForm.length > 0) {
          var item = orxapi.selectors.tabsBarZone.find("li.active");

          if (pge == "pge_advSearch" || pge == "pge_prepareCompleteSearch") {
            searchForm.deserialize(item.data("lastSearch"));
            orxapi.completeSearchForm(item.data("lastSearch"));
          } else if (pge == "pge_advProducerSearch") {
            searchForm.deserialize(item.data("lastProducerSearch"));
            orxapi.completeSearchForm(item.data("lastProducerSearch"));
          }

          searchForm.find("input[type='hidden'].ipt_columnsToSort").remove();
          $("<input>").attr({"type": "hidden",
              "class": "ipt_columnsToSort",
              "name": "columnsToSort[1].code",
              "value": columnCode
          }).appendTo(searchForm);

          $("<input>").attr({"type": "hidden",
              "class": "ipt_columnsToSort",
              "name": "columnsToSort[1].ascOrder",
              "value": ascOrder
          }).appendTo(searchForm);

          if (pge == "pge_advProducerSearch") {
            item.data("lastProducerSearch", searchForm.serialize());
          } else {
            item.data("lastSearch", searchForm.serialize());
          }
        }

        $.getScript("./shared/ts/web/js/bo/pge_list/" + pge + ".js");
      }
    }));
  });

  /** Product indisponible: devis : do nothing on click */
  $("#tbe_resultList").on("click", ".dropdown-menu .disabled", function(e) {
    return false;
  });

})(jQuery);
