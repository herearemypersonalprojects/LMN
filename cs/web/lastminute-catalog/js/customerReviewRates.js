/*!
 * JavaScript customerReviewRates
 *
 * @copyright (c) 2013 Orchestra
 * @project   LASTMINUTE - RESPONSIVE (DESKSTOP/TABLET/MINI-TABLET)
 * @version 1.0
 */

$(function() {

  "use strict";

  //loadReviewRates("");

  loadTripAdvisorReviews();

  loadReviewSummary();

  // Click event: load the reviews when there exist a opinion
  $(document).on("click", ".clientType", function() {
    var that = $(this),
        type = that.attr('id'),
        isOpinion =  $.trim(that.text()) !== "(0 avis)";

    if (isOpinion) {
      loadReviewRates(type);

    } else {
      alert("Aucun commentaire est trouvé pour ce type des clients!");
    }
  });

});

/**
 * loadTripAdvisorReviews
 */
function loadTripAdvisorReviews() {
  if ($('#tripAdvisorReviewsSection').length){
    var id = $('#catalogCode').val(),
    path = "/shared-cs/lastminute-catalog/reviews/" + id + "/",
    url = path + "tripAdvisorReviews.html";
    $.ajaxSetup({
      scriptCharset: "iso-8859-1",
      cache: false
    });
    $.ajax({
      type:"GET",
      url: url,
      contentType: "application/x-www-form-urlencoded;charset=iso-8859-1",
      success: function(data){
        $('#tripAdvisorReviewsSection').html(data);
      }
    });
  }
}

/**
 * loadReviewSummary
 */
function loadReviewSummary() {
  var v1 = parseFloat($('#reviewSummaryBarValue1').text().replace(",", ".")),
      v2 = parseFloat($('#reviewSummaryBarValue2').text());

  $('#reviewSummaryBar').width("" + (v1 / v2) * 114 + "px");
}


/**
 * loadReviewRates
 *
 * @param {String} type
 */
function loadReviewRates(type) {
  var id = $('#catalogRef').val(),
      path = "/shared-cs/lastminute-catalog/reviews/" + id + "/",
      url = path + "globalReviews.html";

  if (type === "nbReviewFamille") {
    url = path + "familyReviews.html";

  } else if (type === "nbReviewFamilleBebe") {
    url = path + "familyChildReviews.html";

  } else if (type === "nbReviewCouple") {
    url = path + "travelPairReviews.html";

  } else if (type === "nbReviewAmis") {
    url = path + "travelFriendReviews.html";

  } else if (type === "nbReviewSeniors") {
    url = path + "seniorReviews.html";

  } else if (type === "nbReviewSeuls") {
    url = path + "travelAloneReviews.html";
  }

  // load review customer
  $.get(url, function(data) {
    $('#customerReviewRates').html(data);

    /** Global */
    var v1 = $('#noteGlobaleBarValue1').text().replace(',','.');

    var v2 = $('#noteGlobaleBarValue2').text().replace(',','.');
    $('#noteGlobaleBar').width(""+(v1/v2)*114+"px");

    /** Service */
    var v = $('#noteServiceBarValue').text().replace(',','.');
    $('#noteServiceBar').width(""+v*114+"px");

    /** Environement */
    var v = $('#noteCadreBarValue').text().replace(',','.');
    $('#noteCadreBar').width(""+v*114+"px");

    /** Geography */
    var v = $('#noteLocationBarValue').text().replace(',','.');
    $('#noteLocationBar').width(""+v*114+"px");

    /** Logement */
    var v = $('#noteChambreBarValue').text().replace(',','.');
    $('#noteChambreBar').width(""+v*114+"px");

    /** Restauration */
    var v = $('#noteRestaurationBarValue').text().replace(',','.');
    $('#noteRestaurationBar').width(""+v*114+"px");

    /** Animation */
    var v = $('#noteAnimationBarValue').text().replace(',','.');
    $('#noteAnimationBar').width(""+v*114+"px");

    /** Prix */
    var v = $('#notePrixBarValue').text().replace(',','.');
    $('#notePrixBar').width(""+v*114+"px");

    /** Equippement */
    var v = $('#noteEquipmentBarValue').text().replace(',','.');
    $('#noteEquipmentBar').width(""+v*114+"px");

    /** Enfants */
    var v = $('#noteActivitesBebeBarValue').text().replace(',','.');
    $('#noteActivitesBebeBar').width(""+v*114+"px");

    /** Excursions */
    var v = $('#noteExcursionsBarValue').text().replace(',','.');
    $('#noteExcursionsBar').width(""+v*114+"px");

    /** Vol */
    var v = $('#noteVolBarValue').text().replace(',','.');
    $('#noteVolBar').width(""+v*114+"px");

    /** noteTransfertBar */
    var v = $('#noteTransfertBarValue').text().replace(',','.');
    $('#noteTransfertBar').width(""+v*114+"px");

    /** noteCorrespondantLocalBar */
    var v = $('#noteCorrespondantLocalBarValue').text().replace(',','.');
    $('#noteCorrespondantLocalBar').width(""+v*114+"px");

    /** noteReFamValue */
    var v = $('#noteReFamValue').text().replace(',','.');
    $('#noteReFam').width(""+v*114+"px");

    /** noteReCoupValue */
    var v = $('#noteReCoupValue').text().replace(',','.');
    $('#noteReCoup').width(""+v*114+"px");

    /** noteReCeliValue */
    var v = $('#noteReCeliValue').text().replace(',','.');
    $('#noteReCeli').width(""+v*114+"px");

    /** noteReAgeValue */
    var v = $('#noteReAgeValue').text().replace(',','.');
    $('#noteReAge').width(""+v*114+"px");

    /** noteReJeunValue */
    var v = $('#noteReJeunValue').text().replace(',','.');
    $('#noteReJeun').width(""+v*114+"px");

    /** noteReAmisValue */
    var v = $('#noteReAmisValue').text().replace(',','.');
    $('#noteReAmis').width(""+v*114+"px");

    /** noteReEtuValue */
    var v = $('#noteReEtuValue').text().replace(',','.');
    $('#noteReEtu').width(""+v*114+"px");

    /** Seprononcepas */
    var v = $('#noteReAbsValue').text().replace(',','.');
    $('#noteReAbs').width(""+v*114+"px");

  });
}
