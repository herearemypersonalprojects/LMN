(function($){


  /* SELECTABLE TABLE */
  $("#tbe_resultList").selectable({
    filter:"tbody tr",
    stop: function(event, ui){
      var that = $(this);
      that.find(".added").removeClass("ui-selected");
      that.find("tr.ui-draggable").draggable("destroy");
      that.find("tr.ui-selected").draggable({
        helper: function() {
          var droppableTable = $("<table/>", { id:"droppableBlock","class":"table table-bordered table-condensed" });
          return droppableTable.append($(this).closest("tbody").find("tr.ui-selected").clone());
        },
        appendTo: "body",
        start: function(event, ui) {
          ui.helper.css({"width": "auto","opacity":"0.5"});
          ui.helper.find(".td_dep_city").css("width", that.find(".td_dep_city").width());
          ui.helper.find(".td_dest_country").css("width", that.find(".td_dest_country").width());
        }
      });

      that.find("tr.ui-selected").click(function() { $(this).removeClass("ui-selected").draggable("destroy"); });

      $("#pge_reg").find(".wrapRight").droppable({
        activeClass: "dropActive",
        hoverClass: "dropHover",
        accept: ":not(.ui-sortable-helper)",
        drop: function(event, ui) {
          var tableDropList = $(event.target).find("#destDropTable tbody");

          /* addClass added when is droped */
          that.find(".ui-draggable").removeClass("ui-selected").addClass("added");

          $("#pge_reg .wrapRight").find(".alert-info").addClass("hidden");
          $("#droppedListZone").removeClass("hidden");

          $("#droppableBlock tr").appendTo(tableDropList);
          tableDropList.find("tr").removeClass();
          /* equalize wrap height */
          $("#pge_reg").find(".wrapLeft, .wrapRight").equalizeHeight();
        }
      });
    }
  });

})(jQuery);
