/**
 * customerReviewFilter.js
 * quoc-anh.le@2013
 */
globalPath = "/shared-cs/lastminute-catalog/reviews/h037/";
lastestReviewsFile = globalPath + "lastestReviews.json";
oldestReviewsFile = globalPath + "oldestReviews.json";
bestReviewsFile = globalPath + "bestReviews.json";
lowestReviewsFile = globalPath + "lowestReviews.json";
displayedNumber = 5;
maxPagings = 6;
htmlReviews = [];
finishedNum = 0;
resultNum = 1;
NC = "0";
nbAvis = 0;
ids = [];

$(document).ready(function() {

  var id = $('#catalogCode').val();
  globalPath = "/shared-cs/lastminute-catalog/reviews/" + id + "/";
  lastestReviewsFile = globalPath + "lastestReviews.json";
  oldestReviewsFile = globalPath + "oldestReviews.json";
  bestReviewsFile = globalPath + "bestReviews.json";
  lowestReviewsFile = globalPath + "lowestReviews.json";

  crawableReviews = globalPath + "crawableReviews.json";

  /** init data */
  if ($('#details-customerReviews').length) {
    loadJSonData(crawableReviews, NC, NC, 1);
  }


  /** user actions */
  initActions();
});

function initActions() {
  $("#btnTrier").click(function() {
    filterClick();
  });

  $("#filtedByCriteria").change(function() {
    filterClick();
  });

  $("#filtedByMonth").change(function() {
    filterClick();
  });

  $("#filtedByYear").change(function() {
    filterClick();
  });

  $(document).on("click", "td", function(event) {
    var that = $(this);
    var val = that.text();
    var currentPage = parseInt(val);
    if (isNaN(currentPage) == true) {
      val = that.parent().find("td.selected").text();
      if (that.attr("id") == "previous") {
        currentPage = parseInt(val) - 1;
      } else {
        currentPage = parseInt(val) + 1;
      }
    }


    computeNbDisplayedReviews(currentPage);
  });
}

/** display instantially once a criteria being selected */
function filterClick() {
  var criteria = $("#filtedByCriteria").val();
  var month = $("#filtedByMonth").val();
  var year = $("#filtedByYear").val();

  if (criteria == "LASTESTNOTES") {
    loadJSonData(lastestReviewsFile, month, year, 1);
  } else
  if (criteria == "OLDESTNOTES") {
    loadJSonData(oldestReviewsFile, month, year, 1);
  } else
  if (criteria == "HIGHESTNOTES") {
    loadJSonData(bestReviewsFile, month, year, 1);
  } else
  if (criteria == "LOWESTNOTES") {
    loadJSonData(lowestReviewsFile, month, year, 1);
  } else if (year != NC || month != NC) {
    loadJSonData(lastestReviewsFile, month, year, 1);
  }
}

/** load json file containing (id, date, note) of all reviews */
function loadJSonData(fileName, month, year, currentPage) {
  nbAvis = 0;
  ids = [];

  $.getJSON(fileName,function(result){
    $.each(result, function(i, field){
       var date = field.date;
       var reviewYear = date.substring(date.lastIndexOf("-")+1);
       var reviewMonth = date.substring(date.indexOf("-")+1, date.lastIndexOf("-"));

       if ((year != NC && reviewYear == year) || (year == NC)) {
         if ((month != NC && reviewMonth == month) || (month == NC)) {
           nbAvis = nbAvis + 1;
           ids[nbAvis] = field.id;
         }
       }
    });

    if (nbAvis == 0) {
      var customerReviews = $('#customerReviews');
      customerReviews.empty();
      customerReviews.append("<br><strong>Aucun commentaire ne correspond aux critères de votre recherche</strong>");
    } else {
      computeNbDisplayedReviews(currentPage);
    }
  });
}

/** compute how to display the navigation */
function computeNbDisplayedReviews(currentPage) {
  var beginReviewNumber = (currentPage-1) * displayedNumber + 1;
  var endReviewNumber = beginReviewNumber+displayedNumber;
  if (endReviewNumber > nbAvis + 1) {
    endReviewNumber = nbAvis + 1;
  }

  resultNum = endReviewNumber;
  finishedNum = beginReviewNumber;
  htmlReviews = [];
  for (k = beginReviewNumber; k < endReviewNumber; k++) {
    displayReview(ids[k], k, currentPage);
  }
}

/** load html file corresponding to a particular review */
function displayReview(fileName, num, currentPage) {

  $.get(globalPath + fileName + ".html", function( data ) {
    htmlReviews[num] = data;
    finishedNum = finishedNum + 1;

    /** because the ajax return data in an asynchronous way,
     * we have to ensure that all review files are loaded (finishedNum == resultNum)
     * and then they are display in the right order (htmlReviews[num] = data).
     */
    if (finishedNum == resultNum) {
      var customerReviews = $('#customerReviews');
      customerReviews.empty();
      /**displayNavigation(currentPage);*/
      for (k = 1; k <= resultNum; k++) {
        customerReviews.append(htmlReviews[k]);
      }
      displayNavigation(currentPage);
    }
  });
}

/** create html dynamically of the navigation */
function displayNavigation(currentPage) {
  if (displayedNumber >= nbAvis) {
    return;
  }

  var totalPages = 0;
  if (nbAvis % displayedNumber === 0) {
    totalPages =  parseInt(nbAvis / displayedNumber);
  } else {
    totalPages =  parseInt(nbAvis / displayedNumber) + 1;
  }

  var nbPagings = maxPagings;
  if (nbPagings > totalPages) {
    nbPagings = totalPages;
  }

  /** set the current page at the middle of the navigation */
  var startPage  = currentPage;
  var midd = parseInt(nbPagings/2);
  if (startPage - midd > 0) {
    startPage = startPage - midd;
  } else {
    startPage = 1;
  }

  var navigator = "" +
      "<div class='sort-selector'>" +
      "<table class='paging'>" +
      "<tbody>" +
      "<tr id = 'navigator'>";
      if (currentPage != 1) {
        navigator = navigator + "<td id='previous'><span><a>&laquo;</a></span></td>";
      }
      var endPage = startPage + nbPagings;
      if (endPage > totalPages + 1 ) {
        startPage = startPage - (endPage - totalPages - 1);
        endPage = totalPages + 1;
      }

      for (k = startPage; k < endPage; k++) {
        if (currentPage == k) {
          navigator = navigator + "<td class='selected'><span><a>"+k+"</a></span></td>";
        } else {
          navigator = navigator + "<td><span><a>"+k+"</a></span></td>";
        }
      }
      if (currentPage < totalPages) {
        navigator = navigator + "<td id='next'><span><a>&raquo;</a></span></td>";
      }

      navigator = navigator +
      "</tr>" +
      "</tbody>" +
      "</table>" +
      "</div>";

  $('#customerReviews').append(navigator);
}