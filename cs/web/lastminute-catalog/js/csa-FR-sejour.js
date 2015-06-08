/*!
 * JavaScript csa-FR-sejour.
 *
 * @copyright (c) 2013 Orchestra
 * @project   LASTMINUTE - RESPONSIVE (DESKSTOP/TABLET/MINI-TABLET)
 * @version 1.0
 */

(function () {

  "use strict";

  // Set local variables
  var referrer = document.referrer.split('/')[2],
      addSuffix = "",
      indexOf,
      pageNumber,
      destination,
      pageOptions,
      adblock1,
      adblock2,
      whitelist;

  // update the whitelist if there's any domain that i've missed out
  whitelist = [
    "www.lastminute.com","www.es.lastminute.com","www.fr.lastminute.com","d01v0003.elab.lastminute.com:4001","qa.www.igougo.com",
    "www.de.lastminute.com", "www.ie.lastminute.com", "www.it.lastminute.com",
    "www.igougo.com", "www.travelocity.co.uk", "www.travelocity.com",
    "www.resfeber.dk", "www.resfeber.se", "www.resfeber.no", "blog.lastminute.com","front.lastminute.recette.orchestra-platform.com",
    "www.zuji.com","voyage.lastminute.com"
  ];

  // Add indexOf function for all browser
  indexOf = function(key, array) {
    var i;
    for (i = 0; i < array.length; i += 1) {
      if (array[i] === key) {
        return true;
      }
    }

    return false;
  };

  //external or internal client ID
  addSuffix = ( indexOf(referrer, whitelist) ) ? '' : '-ext';

  pageNumber = getQueryParameter('s_lin');

  if (pageNumber == 'null') {
    pageNumber = 1;

  } else {
    pageNumber = Math.ceil(pageNumber / 20);
  }

  destination = getDestinationForGoogleAds();

  pageOptions = {
    'pubId' : 'lastminute-fr-holidays' + addSuffix,
    'query' : 'sejour, vacances' + destination, //add the search keyword here (destination) if necessary (all in local language)
    'queryLink' : 'sejour, vacances', //in local language
    'hl' : 'fr', //change if the language needed to be change
    'adtest' : 'on', //turn off if you're ready to deploy
    'adPage' : pageNumber // this value should change depends on the pagination
  };

  adblock1 = {
    'container' : 'adcontainer1',
    'number' : '4',
    'width' : '100%',
    'fontSizeTitle' : '12px',
    'fontSizeDescription' : '12x',
    'fontSizeDomainLink' : '11px',
    'colorTitleLink' : '#1A1A1A',
    'colorDomainLink' : '#EC008C',
    'colorBorder' : '#E5E5E5',
    'colorAttribution' : '#1A1A1A',
    'titleBold' : true,
    'linkTarget' : '_blank',
    'noTitleUnderline' : false,
    'sellerRatings' : false,
    'siteLinks' : false
  };

  adblock2 = {
    'container' : 'adcontainer2',
    'number' : '5',
    'width' : '120px',
    'fontSizeTitle' : '12px',
    'fontSizeDescription' : '12px',
    'fontSizeDomainLink' : '11px',
    'colorTitleLink' : '#1A1A1A',
    'colorDomainLink' : '#EC008C',
    'colorBorder' : '#E5E5E5',
    'colorAttribution' : '#1A1A1A',
    'titleBold' : true,
    'linkTarget' : '_blank',
    'noTitleUnderline' : false,
    'sellerRatings' : false,
    'siteLinks' : false
  };

  new google.ads.search.Ads(pageOptions, adblock1, adblock2);

}());



/**
 * getQueryParameter
 *
 * @param {String} parameterName
 * @return {String} null
 */
function getQueryParameter(parameterName) {
  var queryString = window.top.location.search.substring(1),
      parameterName = parameterName + "=";

  if (queryString.length > 0) {
    begin = queryString.indexOf(parameterName);

    if (begin != -1) {
      begin += parameterName.length;
      end = queryString.indexOf("&", begin);

      if (end == -1) {
        end = queryString.length;
      }

      return unescape(queryString.substring(begin, end));
    }
  }

  return "null";
}


/**
 * getDestinationForGoogleAds
 *
 * @return {String} cities and countries
 */
function getDestinationForGoogleAds() {
  return getCities() + getCountries();
}


/**
 * getCities
 *
 * @return {String} result
 */
function getCities() {
  var result = "",
      cities = getQueryParameter("aff_c.de");

  if (cities != "null") {
    result = getRequestedDestinations(cities, 1);

  } else {
    cities = getQueryParameter("s_c.de");

    if (cities != "null") {
      result = getRequestedDestinations(cities, 1);
    }
  }

  return result;
}


/**
 * getCountries
 *
 * @return {String} result
 */
function getCountries() {
  var result = "",
      countries = getQueryParameter("s_c.de");

  if (countries != "null") {
    result = getRequestedDestinations(countries, 0);
  }

  return result;
}


/**
 * getRequestedDestinations
 *
 * @param {String} destinations
 * @param {Number} level
 * @return {String} result
 */
function getRequestedDestinations(destinations, level) {
  var result = "",
      resultArray = [],
      destinationsArray = destinations.split(','),
      i, j;

  for (i = 0; i < destinationsArray.length; i += 1) {
    if (destinationsArray[i].split('.').length === (level + 1)) {
      if (destinationsArray[i].split('.')[level]) {
        resultArray[i] = destinationsArray[i].split('.')[level];
      }
    }
  }

  if (resultArray.length !== 0) {
    for (j = 0; j < resultArray.length; j += 1) {
      if (resultArray[j] !== undefined) {
        result = result + ", " + resultArray[j];
      }
    }
  }
  return result;
}
