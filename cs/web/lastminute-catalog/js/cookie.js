/*!
 * JavaScript cookie.
 *
 * @copyright (c) 2013 Orchestra
 * @project   LASTMINUTE - RESPONSIVE (DESKSTOP/TABLET/MINI-TABLET)
 * @version 1.0
 */

window.separated = '%26%26%26';


/**
 * getCookieByName
 */
function getCookieByName(name) {

  var cookies = document.cookie.replace(' ','').split(';'),
      trimName, i;

  for (i = 0; i < cookies.length; i += 1) {
    trimName = $.trim(cookies[i]);

    if (trimName.indexOf(name) == 0) {
      return cookies[i].substring(cookies[i].indexOf('=') + 1);
    }
  }

  return "";
}


/**
 * addToCookies
 */
function addToCookies(name, ref) {
  var value = getCookieByName(name),
      newValue;

  if (value == "") {
    document.cookie = name + '=' + ref;

  } else {
    newValue = value + separated + ref;
    document.cookie = name + '=' + newValue;
  }
}


/**
 * removeFromCookies
 */
function removeFromCookies(name, ref) {
  var value = getCookieByName(name);

  if (value != '' && value.indexOf(ref) >= 0) {
    var start = getStartPos(value, ref);
    var end = value.indexOf(ref) + ref.length;
    var tobeRemoved = value.substring(start, end);
    var newValue = value.replace(tobeRemoved, '');

    if (newValue.indexOf(separated) == 0) {
      newValue = newValue.substring(separated.length);
    }

    document.cookie = name + '=' + newValue;
  }
}


/**
 * getStartPos
 *
 * @param {Object} value
 * @param {Object} ref
 */
function getStartPos(value, ref) {
  if (value.indexOf(separated) < value.indexOf(ref)) {
    return value.indexOf(ref) - separated.length;

  } else {
    return value.indexOf(ref);
  }
}
