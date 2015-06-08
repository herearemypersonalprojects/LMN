/*!
 * JavaScript commonFunctions.js.
 *
 * @copyright (c) 2013 Orchestra
 * @project   LASTMINUTE - RESPONSIVE (DESKSTOP/TABLET/MINI-TABLET)
 * @version 1.0
 * @author : Quoc-Anh LE
 */


// Click event: call the popup with data settings
$(".link-video").on("click", function() {
 var elem = $(this),
     contentID = elem.data("contentId"),
     playerID = elem.data("playerId");

   newPopup("/video.cms?contentID=" + contentID + "&playerID=" + playerID);
});


/**
 * newPopup
 * @param {String} url The URL for the opening pop-up
 */
function newPopup(url) {
  popupWindow = window.open(url,'','height=400,width=700,left=10,top=10,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no,status=yes');
}


/**
 * getParamValueByName
 * @param {String} (eg. queryString=s_aj%3D2&s_c.de=indifferent&s_dpci=&s_dd=&s_dmy=&s_aj=6)
 * @param {String} the name of one parameter
 * @return {String} the value of the given parameter name.
 */
function getParamValueByName(params, name){
  var paramValue = '',
      paramsTable = params.split('&'),
      tableLength = paramsTable.length,
      paramValueTable, i;

  for (i = 0; i < tableLength; i += 1) {
    if (paramsTable[i].indexOf(name) !== -1) {
      paramValueTable = paramsTable[i].split('=');

      if (paramValueTable.length > 1) {
        paramValue = paramValueTable[1];
        return paramValue;
      }
    }
  }
  return paramValue;
}
