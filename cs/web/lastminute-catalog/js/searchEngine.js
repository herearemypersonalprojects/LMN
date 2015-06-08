/**
 * searchEngine.js
 * quoc-anh.le@2013
 */

$( document ).ready(function() {
  initResultInfo();
  initCriteria();
  setupActions();
  initDatepicker();
});


/** INITIALIZATIONS */
/** begin init Datepicker */
function initDatepicker(){
  var calendarWidget = '/shared-cs/lastminute-catalog/images/calendar-icon.gif',
  brandName = $("#brandName").val();
  if (brandName == 'CDISCOUNT') {
    calendarWidget = 'http://cdn.lastminute.com/site/ico_calendar_search_widget.gif?skin=fr_cdiscount_beta';
  } else if (brandName == 'CITROEN') {
    calendarWidget = 'http://cdn.lastminute.com/site/ico_calendar_search_widget.gif?skin=fr_citroen';
  }

  $('.datepicker').datepicker({
    numberOfMonths: 2,
    showOn: "button",
    buttonImage: calendarWidget,
    buttonImageOnly: true,
    showButtonPanel: true,
    minDate: '0d',
    maxDate: '+1Y',
    closeText: "Fermer"
  });


  $('.datepicker').each(function(){
    var inp = $(this);
    inp.css({
      'width': inp.next().width(),
      'height': inp.next().height() - 3,
      'padding': 0,
      'margin-right': -inp.next().width(),
      'margin-top': -7,
      'border': 0
    });
  });


  $.datepicker.regional['fr'] = {
    closeText: 'Fermer',
    prevText: '&#x3c;Prec',
    nextText: 'Suiv&#x3e;',
    currentText: 'Courant',
    monthNames: ['Janvier', 'Fevrier', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Decembre'],
    monthNamesShort: ['Jan', 'Fev', 'Mar', 'Avr', 'Mai', 'Jun', 'Jul', 'Aou', 'Sep', 'Oct', 'Nov', 'Dec'],
    dayNames: ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'],
    dayNamesShort: ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam'],
    dayNamesMin: ['D', 'L', 'M', 'M', 'J', 'V', 'S'],
    weekHeader: 'Sm',
    dateFormat: 'dd/mm/yy',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: ''
  };

  $.datepicker.setDefaults($.datepicker.regional['fr']);

  $(window).resize(function(){
    $('.datepicker').datepicker('hide');
  });

}
/** end init Datepicker */

function initCriteria() {
  /** restore from the previous search if possible */
  //var params =  decodeURIComponent(window.location.href);
  //params = params.substring(params.indexOf('?'));
  var params = $('#queryString').val();

  /** save current url to be back later */
  document.cookie = 'previousSearch'+'='+ window.location.href;

  var day = '', dmy = '', dest = '', city = '';
  if (params != null && params != "") {
    day = getParamValueByName(params, 'dd');
    dmy = getParamValueByName(params, 'dmy');
    dest = getParamValueByName(params, 's_c.de');
    destCity = getParamValueByName(params, 'aff_c.de');
    if (destCity != null && destCity != "") {
      dest = destCity;
    }

    city = getParamValueByName(params, 's_dpci');
    var flexDays = getParamValueByName(params, 's_aj');
    var during = getParamValueByName(params, 's_minMan');

    var size = getParamValueByName(params, 'size');
    var sortType = getParamValueByName(params, 's_st');

    var sortedForm = $('#sortedForm');
    if (sortType != "") {
      setTitleBoldByValue(sortType, sortedForm.find('span[name=st]'));
    }
    if (size != "") {
      setTitleBoldByText(size, sortedForm.find('span[name=nbDisplay]'));
    }

    initFlexibility(flexDays);
    initDuring(during);

    initAdvancedSearch(params);
  }
  initDestinations(dest);
  initCities(city);
  initMonthYearDay(day, dmy);
}

/** USER ACTIONS */
function setupActions() {
  /** Deal of day: DISPLAY BLOCK SPECIAL OFFER IN ALL PAGE */
    $(".blkSpecialOffer").find(".link").on("click", function() {
        var link = $(this),
        blkSpecialOffer = $(".blkSpecialOffer"),
        inner = blkSpecialOffer.find(".inner-special-offer"),
        condition = blkSpecialOffer.find(".actionsTitle"),
        data = $(this).data("toggleText").split(",");

    if (inner.hasClass("hidden")) {
      inner.removeClass("hidden").stop().animate({ height: "87px" });

      link.text(data[1]);
      condition.addClass("open");
      $.cookie("blkSpecialOffer", "open");

    } else {
      inner.stop().animate({ height: "0" }, function() {
        $(this).addClass("hidden");
      });

      link.text(data[0]);
      condition.removeClass("open");
      $.cookie("blkSpecialOffer", "hidden");
    }
  });

  /** SELECT DESTINATIONS */
  function addDestination(that) {
    var value = that.val();
    if (value == -1 || value == '') return false;
    var label = $("#slt_destination option:selected").text();
    addSelectedDest(value, label);
    $('#destAdd').show();
    that.hide();
  }

  $("#slt_destination").change(function() {
    addDestination($(this));
    $(this).parent().find(".customSelect").addClass("hidden");
  });
  /*
  $('#slt_destination option').on("click", function(){
    var that = $('#slt_destination');
    addDestination(that);
  });
  */

  $('#destAdd').on("click", function(e) {
    var that = $(this);
    e.preventDefault();
    that.hide();
    $(this).parent().find(".customSelect").removeClass("hidden");
    $('#slt_destination').show();
  });

  $('.datepicker').change(function(){
    var dateValue = $('.datepicker').val();
    var dateTable = dateValue.split('/');
    var day = dateTable[0];
    var month = dateTable[1];
    var year = dateTable[2];
    var monthYear = month + '/' + year;
    var maxDay = getMaxDays(month, year);
    buildDayOption(maxDay, year, month, 0);

    $("#dayDeparture option").each(function(){
      if ($(this).val() === day) {
        $(this).attr('selected', 'selected');
        $("#dayDeparture").parent().find(".customSelectInner").text($(this).text());
        return false;
      }
    });

    $("#monthDeparture option").each(function(){
      if ($(this).val() === monthYear) {
        $(this).attr('selected', 'selected');
        $("#monthDeparture").parent().find(".customSelectInner").text($(this).text());
        return false;
      }
    });


    return false;
  });


  /** DELETE SELECTED DESTINATIONS */
  /** this event will trigger a reload of this javascript file */
  $(document).on("click", ".selectedDest", function(event) {
    var that = $(this).find(".icon--delete");
    $("#slt_destination option[value='"+that.attr('id')+"']").removeAttr('disabled');

    that.parent().parent().remove();

    if ($('.icon--delete').length == 0) {
      $('#slt_destination').parent().find(".customSelect").removeClass("hidden");
      $('#slt_destination').val($("#slt_destination option:first").val());
      $('#destAdd').hide();
      $("#slt_destination").show();
    }
  });

  /** DATE SELECTION */
  $('#monthDeparture option').click(function(){
    var monthYear = $(this).val();
    var monthYearArr = monthYear.split('/');
    var month = monthYearArr[0];
    var year = monthYearArr[1];
    var maxDay = getMaxDays(month, year);
    var currentDay = $('#dayDeparture').val();
    buildDayOption(maxDay, year, month, currentDay);
  });

  /** SEARCH */
  $('#searchForm').submit(function(){
    var url = $('#searchForm').attr('action') + "?";
    $('#slt_destination').val('');
    var params = $("#searchForm").find('input, select').filter(getValues).serialize();

    /** SELECTED DESTINATIONS */
    var selectedDests = $('.icon--delete');
    var dests = '';
    selectedDests.each(function(){
      dests += ',' + $(this).attr('id');
    });
    if (dests.length > 0) {
      dests = dests.substring(1);
      params += '&s_c.de=' + dests;
    }

    url += (!endsWith(url, '?'))?'&':'';

    url = url + decodeURIComponent(params);

    window.location.href = "/" + url;

    return false;
  });

  /** SORT */
  $('#sortedForm').find('span[name=st]').click(function(){
    var sortedValue = $(this).attr('value');
    setTitleBoldByValue(sortedValue, $('#sortedForm span[name=st]'));
    var params = getUrlParams(true);
    params = replaceParamValue(params, 's_st=', sortedValue);
    var url = getUrlParams(false) + '?' + params;
    window.location.href = url;
    return false;
  });

  /** DISPLAYED NUMBER */
  $('#sortedForm').find('span[name=nbDisplay]').click(function(){
    var nb = $(this).text();
    setTitleBoldByText(nb, $('#sortedForm span[name=nbDisplay]'));
    window.location.href = getUrlParams(false) + "?" + updatePartitionerUrl(nb);
    return false;
  });

  /** ADVANCED SEARCH */
  $('#advancedSearch').on('click').find('.imp').find('input[type=checkbox]').click(function(){
    var that = $(this);
    if (that.attr('checked')) {
      that.removeAttr("checked", "checked");
    } else {
      that.attr("checked", "checked");
    }

    resetSearch(that.parent().parent().parent());
  });

  $('#advancedSearch').find('.tick-all').click(function(){
    var that = $(this);
    var checkBoxes = that.parent().find('.imp').find('input[type=checkbox]');
    var toutCocher = "tout cocher";
    if (that.text() == toutCocher) {
      checkBoxes.attr('checked', 'checked');
      that.text("tout décocher");
    } else {
      checkBoxes.removeAttr('checked');
      that.text(toutCocher);
    }
    resetSearch(that.parent());
    return false; /** Only use return false when you want both preventDefault() and stopPropagation() */
  });

  $('.reinitAll').click(function(){
    var paramsTable = $('#queryString').val().split('&');
    var params = "";
    for (var i = 0; i < paramsTable.length; i++) {
      if (paramsTable[i].indexOf('aff') == -1) {
        params += paramsTable[i] + '&';
      }
    }
    params = params.substring(0, params.length - 1);
    var url = getUrlParams(false) + '?' + params;
    window.location.href = url;
    return false;
  });
}

/**
 * FUNCTIONS
 */

function resetSearch(that) {
  var value = '';
  that.find('.imp').find('input[type=checkbox]:checked').each(function() {
    value += ',' + $(this).attr('value');
  });
  value = (value.indexOf(',')>=0)? value.substring(1):value;

  var name = that.find('.imp').find('input[type=checkbox]:first').attr('name')+'=';
  var params = $('#queryString').val();//getUrlParams(true);
  params = replaceParamValue(params, name, value);
  params = replaceParamValue(params, "s_fin=", "0");
  var nbDisplay = $('#sortedForm').find('span[class=selected]').eq(1).text();
  params = replaceParamValue(params, "s_lin=", nbDisplay);
  var url = getUrlParams(false) + '?' + params;
  $(".overlay-lm").removeClass("hidden");
  window.location.href = url;
}

function replaceParamValue(params, param, newValue) {
  var idxS = params.indexOf(param);
  if (idxS === -1) {
    params += '&' + param + newValue;
  } else {
    var idxE = params.indexOf('&', idxS+1);
    var sortedState = (idxE === -1)? params.substring(idxS):params.substring(idxS, idxE);
    var newState = param + newValue;
    if (newValue.length==0) {
      newState = '';
      sortedState = '&'+sortedState;
    }

    params = params.replace(sortedState, newState);
  }
  return params;
}

function updatePartitionerUrl(productsMaxSize){
  var queryParameters = getUrlParams(true), newQueryParameters = '', queryParameterArray = queryParameters.split('&');

  if (queryParameters == '') {
    newQueryParameters = "s_aj=2";
  }

  for (var i = 0; i < queryParameterArray.length; i++) {
    if (queryParameterArray[i].indexOf('s_fin') === -1 &&
    queryParameterArray[i].indexOf('s_lin') === -1 &&
    queryParameterArray[i].indexOf('size') === -1) {
      newQueryParameters = newQueryParameters + queryParameterArray[i] + '&';
    }
  }
  return newQueryParameters + "s_fin=0&s_lin=" + productsMaxSize + "&size=" + productsMaxSize;
}

function addSelectedDest(dest, text) {
  var selectedDests = $("#selectedDests");
  if (selectedDests.find(".icon--delete").length > 2) {
    alert("Vous ne pouvez sélectionner que 3 destinations au maximum");

    return "";
  }


  text = text.indexOf('-') >= 0? text.substring(3):text;
  text = text.indexOf('.') >= 0? text.substring(text.indexOf('.')+1):text;


  selectedDests.append('<li style="" class="selectedDest"><span class="ico-bg"><i id='+dest+' class="icon--delete">x</i></span>'+ ' ' + text+'</li>');

  $("#slt_destination option[value='"+dest+"']").attr('disabled', 'disabled');

  return text;
}

/**
*
* @param {Boolean} [isParams] is true return second part.
* @return {String} part of URL string.
*/
function getUrlParams(isParams){
 var url = window.location.protocol + "//" + window.location.host + "/serp.cms?" + $("#queryString").val();
 var questionIndex = url.indexOf("?");
 var firstPart = "";
 var secondPart = "";
 if (questionIndex != -1 && questionIndex < url.length - 1) {
   firstPart = url.substring(0, questionIndex);
   secondPart = url.substring(questionIndex + 1, url.length);
 }
 if (isParams === true) {
   return secondPart;
 }
 else {
   return firstPart;
 }
}

/**
 * Set Trier par : top des ventes | prix croissant | prix décroissant | nombre d'étoiles
 *     Afficher : 20 | 40
 */
function setTitleBoldByValue(value, objs) {
  objs.each(function(){
    var that = $(this);
    if (that.attr('value')==value) {
      that.addClass("selected");
    } else {
      that.removeClass("selected");
    }
  });
}
function setTitleBoldByText(text, objs) {
  objs.each(function(){
    var that = $(this);
    if (that.text()==text) {
      that.addClass("selected");
    } else {
      that.removeClass("selected");
    }
  });
}

/**
 * initialize resuls info
 */
function initResultInfo() {

  $(".colx3").filter(function() {
    return !$(this).find("table tr").length;
  }).addClass('empty');

  if ($(".blkSpecialOffer")[0]) {
    var isOpen = $.cookie("blkSpecialOffer"),
    inner = $(".blkSpecialOffer").find(".inner-special-offer"),
    condition = $(".blkSpecialOffer").find(".actionsTitle"),
    data = $(".link").data("toggleText").split(",");

    if (isOpen !== "hidden") {
      isOpen = "open";
    }

    if (isOpen == "open" && inner.hasClass("hidden")) {
      inner.removeClass("hidden").stop().animate({ height: "87px" });
      $(".link").text(data[1]);
      condition.addClass("open");
    }
  }

  $('#tobehidden').remove();
  if ($('.noResults')[0]) {
    $('.message-error').removeClass("hidden");
    $('#noResultInfo').removeClass("hidden");
    $('#resultInfo').addClass("hidden");
  } else {
    $('#resultInfo').removeClass("hidden");
  }

  $('#totalNumberLabel').text($('#totalNumberInput').val());
}
/**
 * initialize advanced search
 */
function initAdvancedSearch(params) {
  var advParams = '';
  advParams += getParamValueByName(params, 'aff_c.cat');
  advParams += getParamValueByName(params, 'aff_c.de');
  advParams += getParamValueByName(params, 'aff_meal');
  advParams += getParamValueByName(params, 'aff_c.tvoyages');
  advParams += getParamValueByName(params, 'aff_mmp');
  advParams += getParamValueByName(params, 'aff_c.th');

  $("#advancedSearch").find('.imp').find('input[type=checkbox]').each(function() {
    var that = $(this);
    var value = that.attr('value');
    if (advParams.indexOf(value)>=0) {
      that.attr("checked", "checked");
      that.parent().parent().find('.numb').removeClass('hidden');
      that.parent().parent().parent().parent().parent().find('.tick-all').text("tout décocher");
    }
  });

  var toutCocher = "tout cocher";
  $('#advancedSearch').find('.tick-all').each(function() {
    var that = $(this);
    if (that.text() == toutCocher) {
      that.parent().find('.numb').removeClass('hidden');
    }
  });
}

/**
 * destinations
 */
function initDestinations(dests) {
  /** restore from the previous research */
  if (dests != '') {
    var strDests = dests.split(',');
    if (strDests.length == 1) {
      $("#slt_destination").val(dests);
    }
    for (var i = 0; i < strDests.length; i ++ ) {
      var label = $("#slt_destination option[value='"+ decodeURIComponent(strDests[i])+"']:last").text();
      var text = addSelectedDest(strDests[i], label);
      if ($("#dest_voyage")) {
        $("#dest_voyage").text("Voyage " + text);
      }
    }
  }

  /** Disable the options whose value is -1 (the line blanks) */
  $('#slt_destination option').each(function(){
    var value = $(this).val();
    if (value == -1) {
        $(this).attr('disabled', 'disabled');
    }
  });
}

/**
 * cities
 */
function initCities(city) {
  /** restore from the previous research */
  if (city != '') {
    $('#slt_city').val(city);
  } else {
    $("#slt_city").val($("#slt_city option:first").val());
  }

  /** Disable the options whose value is -1 (the line blanks) */
  $('#slt_city option').each(function(){
         var value = $(this).val();
         if (value == -1) {
             $(this).attr('disabled', 'disabled');
         }
     });
}

/**
* flexibility
*/
function initFlexibility(flexDays) {
  if (flexDays != '') {
    $('#slt_aj').val(flexDays);
  } else {
    $("#slt_aj").val(2);
  }
}

/**
 * length of stay
 */
function initDuring(during) {
  if (during != '') {
    $('#slt_time').val(during);
  } else {
    $("#slt_time").val($("#slt_time option:first").val());
  }
}

/**
 * fill values for the departure date options
 */
function initMonthYearDay(day, dmy) {
  var currentDate = new Date();
  var currentMonth = currentDate.getMonth();
  var currentYear = currentDate.getFullYear();

  /** fill month/year into the departure option */
  for (var i = currentMonth; i < currentMonth + 13; i++) {
    var month = i;
    var monthName = '';
    var year = currentYear;
    if (month > 11) {
      month = month - 12;
      year = year + 1;
    }
    year = year + "";
    var yearCode = year.split("20");
    yearCode = yearCode[1];
    monthName = getMonthName(month);
    month += 1;
    if (month < 10) {
      month = '0' + month;
    }
    if (dmy === month + '/' + year) {
      $('#monthDeparture').append('<option selected="selected" value="' + month + '/' + year + '">' + monthName + '  ' + yearCode + '</option>');
      $("#monthDeparture").parent().find(".customSelectInner").text(monthName + '  ' + yearCode);
    }
    else {
      $('#monthDeparture').append('<option value="' + month + '/' + year + '">' + monthName + '  ' + yearCode + '</option>');
    }
  }

  /** fill date into the departure option */
  var monthStr = '';
  currentMonth += 1;
  if (currentMonth < 10) {
    monthStr = '0' + currentMonth;
  }
  else {
    monthStr = currentMonth + 1;
  }

  if (dmy) {
    monthStr = dmy.substr(0,2);
    currentYear = dmy.substr(3);
  }

  var currentDay = currentDate.getDate();
  var maxDay = getMaxDays(monthStr, currentYear);
  buildDayOption(maxDay, currentYear, monthStr, day);
  return false;

}

/**
*
* @param {int, int, string, int} the month and the year.
* @return fill day values into date option.
*/
function buildDayOption(maxDay, year, month, currentDay){
  $("#dayDeparture").html('');

  month -= 1;
  var url = window.location.href;

  if (url.indexOf("searchBox") != -1) {
    $("#dayDeparture").append('<option value="">Peu m\'import</option>');
  }
  else {
    $("#dayDeparture").append('<option value="">tous</option>');
  }

  for (var i = 1; i <= maxDay; i++) {
    var d = new Date(year, month, i);
    var dayName = getDayName(d.getDay());
    if (i < 10) {
      if (i == currentDay) {
        $("#dayDeparture").append('<option selected="selected" value="0' + i + '">' + dayName  + ' 0' + i + '  ' + '</option>');
        $("#dayDeparture").parent().find(".customSelectInner").text(dayName  + ' 0' + i);
      }
      else {
        $("#dayDeparture").append('<option value="0' + i + '">' + dayName  + ' 0' + i + '  ' + '</option>');
      }
    }
    else {
      if (i == currentDay) {
        $("#dayDeparture").append('<option selected="selected" value="' + i + '">' + dayName  + ' ' + i + '  ' + '</option>');
        $("#dayDeparture").parent().find(".customSelectInner").text(dayName  + ' ' + i);
      }
      else {
        $("#dayDeparture").append('<option value="' + i + '">' + dayName + '  ' + i + '</option>');
      }
    }
  }
}
/**
*
* @param {string, int} the month and the year.
* @return {int the number of the days in the given month.
*/
function getMaxDays(month, year){
  var maxDay = '';
  switch (month) {
    case '01': case '03': case '05': case '07': case '8': case '10': case '12':
      maxDay = 31; break;
    case '04': case '06': case '09': case '11':
      maxDay = 30; break;
    case '02':
      maxDay = (isBissextile(year)) ? 29 : 28;
      break;
    default:
      maxDay = 31;
      break;
  }
  return maxDay;
}

/**
*
* @param {int} the year.
* @return {boolean} return true if that is a bissextile year or leap year.
*/
function isBissextile(year){
  if (year % 4 !== 0) {
    return false;
  }
  return true;
}

/**
*
* @param {int} the day in numerical.
* @return {String} the day name in string.
*/
function getDayName(day){
  var dayName = ['dim','lun','mar','mer','jeu','ven','sam'];
  var idx = $.inArray(dayName[day], dayName);
  return (idx < 0)?'':dayName[day];
}
/**
*
* @param {int} the month in numerical.
* @return {String} the name in string.
*/
function getMonthName(month){
  var monthName = ['jan','fév','mar','avr','mai','juin','juil','août','sep','oct','nov','déc'];
  var idx = $.inArray(monthName[month], monthName);
  return (idx < 0)?'jan':monthName[month];
}

/**
 * get search criteria whose value is not empty nor -1
 */
function getValues() {
  return $.trim(this.value) && $.trim(this.value) !== '-1' && $.trim(this.value) != '';
}

/**
 * check whether the str ends with a specific suffix
 */
function endsWith(str, suffix){
  return str.indexOf(suffix, str.length - suffix.length) !== -1;
}