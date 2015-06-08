(function($, orx) {

  var orx_dp = orx.plugin.datepicker;

  $("#searchZone").find(".datePicker").datepicker($.extend({}, {}, orx_dp));
  orxapi.pictoDate();

  var mkgFieldZone = $("#searchZone").find(".cbxCodeZone");

  mkgFieldZone.find("input:checkbox").click(
      function() {
        if (!$(this).is(":checked")) {
          $(this).parents(".cbxCodeZone").find("input:text").prop("disabled", true);
          $(this).parents(".cbxCodeZone").find("input:text").removeClass("lightValue");
        } else {
          $(this).parents(".cbxCodeZone").find("input:text").prop("disabled", false);
          $(this).parents(".cbxCodeZone").find("input:text").addClass("lightValue");
        }
  });

  $.validator.messages.required = "";

  $(".searchForm").validate({
      rules : {
        totalAmount : {
          number : true
        }
      },
      errorPlacement: function(error, element) {}
    });

  /* Simple search */
  $(".searchForm").keydown(function(evt) {
    if (evt.which  == 13) {
      evt.preventDefault();
      $(".searchComptaBtn").trigger("click");
    }
  });

  /* Search click */
  $("#innerWrapContent").on("click", ".searchComptaBtn", function(event) {

    var params = $(".searchForm").serialize();
    var action = $(this).data("page");
    action = action + ".action"
    if ($(".searchForm").valid()) {

      $.ajax($.extend({}, orxapi.ajax, {
        url : action,
        data : params,
        success : function(data, textStatus, jqXHR) {
          $("#resultTable").html(data);
          orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch", params);
          $("#wrapSideBar").find("li.active").data("lastSearch", params);
          $.getScript("./shared/ts/web/js/bo/pge_list/pge_resultList.js");
          orxapi.storeTabCookie();
        }
      }));
    }
    return false;
  });

  var lastSearch = orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch");
  var newSideBarCode = $("#ipt_ptnSideBarCode").attr("value");

  if (lastSearch != null && lastSearch != "") {
    $(".searchForm").deserialize(lastSearch);
    if (newSideBarCode != $("#ipt_ptnSideBarCode").attr("value")) {

      // here we are in a new list search
      // first search if there is an old search stocked into this list
      var lastSideBarSearch = $("#wrapSideBar").find("li.active").data("lastSearch");
      if (lastSideBarSearch != null && lastSideBarSearch != "") {
        // deserialize lastSearch stocked in this submenu
        $("select option:selected").removeAttr("selected");

        $(".searchForm").deserialize(lastSideBarSearch);
      } else {
        $("select option:selected").removeAttr("selected");
        $("input:not(:hidden)").val("");
        $("select, input").removeClass("lightValue");

        orxapi.selectors.tabsBarZone.find("li.active").data("lastSearch", "");
        $("#ipt_ptnSideBarCode").attr("value", newSideBarCode);
      }
    }
    if (newSideBarCode == $("#ipt_ptnSideBarCode").attr("value")) {
      $(".searchComptaBtn").trigger("click");
    }
  }

  $(".searchForm").find("input[value!='']:not(:disabled):not(:checkbox), select[value!='']:not(:disabled)").addClass("lightValue");

  // multiselects
  $(".multiselect").multiselect();
  $(".ui-multiselect").css("width","224");
})(jQuery, orxapi);

