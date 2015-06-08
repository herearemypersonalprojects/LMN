
$(document).ready(function() {


  $("#starterZone").find(".pto_starter").dropdown();

  $("#starterZone").on("click", "a", function() {
    var attr = $(this).attr("target");
    if (typeof attr === "undefined" || attr === false) {
      $("#starterZone").addClass("loading");
      $("body").append($("<div/>",{ "id":"overlayLoading"}));
      $("body").css("cursor", "wait");
    }
  });

});
