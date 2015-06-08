/*!
 * searchBox.js
 * for searchBox.
 */
$(function(){

    /** Initialize */
    buildMonthYearDay();
    initSelectCity();
    initSelectDest();
    initDatepicker();

    $('.datepicker').change(function(){
        var dateValue = $('.datepicker').val(),
            dateTable = dateValue.split('/'),
            day = dateTable[0],
            month = dateTable[1],
            year = dateTable[2],
            monthYear = month + '/' + year;
            maxDay = getMaxDays(month, year);
        buildDayOption(maxDay, year, month, 0);

        $("#slt_departureDay option").each(function(){
            if ($(this).val() === day) {
                $(this).attr('selected', 'selected');
            }
        });

        $("#slt_departureMonth option").each(function(){
            if ($(this).val() === monthYear) {
                $(this).attr('selected', 'selected');
            }
        });

        return false;
    });

    $('#slt_departureMonth').change(function(){
        var monthYear = $(this).val();
        var monthYearTable = monthYear.split('/');
        var month = monthYearTable[0];
        var year = monthYearTable[1];
        var maxDay = getMaxDays(month, year);
        var day = $('#slt_departureDay').val();
        buildDayOption(maxDay, year, month, day);
    });

    $('#search').submit(function(){
        var dpci = $('#slt_city').val();
        var dest =  $('#slt_destination').val();
        var dmy = $('#slt_departureMonth').val();
        var dd = $('#slt_departureDay').val();
        var aj = $('#slt_aj').val();
        var minMan = $('#slt_time').val();
        var pid= $('#ipt_productId').val();
        var toName= $('#ipt_toName').val();
        var hotelName= $('#ipt_hname').val();

        var url = $('#search').attr('action') + "?";
        if (dpci !== '' && dpci !== '-1') {
            if (endsWith(url, '?')) {
                url += 's_dpci=' + dpci;
            }
            else {
                url += '&s_dpci=' + dpci;
            }
        }
        if (dest !== '' && dest !== '-1') {
            if (endsWith(url, '?')) {
                url += 's_c.de=' + dest;
            }
            else {
                url += '&s_c.de=' + dest;
            }
        }
        if (dmy !== '') {
            if (endsWith(url, '?')) {
                url += 's_dmy=' + dmy;
            }
            else {
                url += '&s_dmy=' + dmy;
            }
        }
        if (dd !== '') {
            if (endsWith(url, '?')) {
                url += 's_dd=' + dd;
            }
            else {
                url += '&s_dd=' + dd;
            }
        }

        if (aj !== '') {
            if (endsWith(url, '?')) {
                url += 's_aj=' + aj;
            }
            else {
                url += '&s_aj=' + aj;
            }
        }
        if (minMan !== '') {
            if (endsWith(url, '?')) {
                url += 's_minMan=' + minMan;
            }
            else {
                url += '&s_minMan=' + minMan;
            }
        }

        if (pid) {
            if (endsWith(url, '?')) {
                url += 's_pid=' + pid;
            }
            else {
                url += '&s_pid=' + pid;
            }
        }

        if (toName) {
            if (endsWith(url, '?')) {
                url += 's_to=' + toName;
            }
            else {
                url += '&s_to=' + toName;
            }
        }


        if (hotelName) {
            if (endsWith(url, '?')) {
                url += 's_hname=' + hotelName;
            }
            else {
                url += '&s_hname=' + hotelName;
            }
        }


        $("#search").find("input:hidden").each(function() {
          if ($(this).val() !== '0') {
            url += '&' + $(this).attr("name") + '=' + $(this).val();
          }
        });

        window.location.href = url;
        return false;
    });
});


function getParamValueByName(params, name){
    var paramValue = '';
    var paramsTable = params.split('&');
    var tableLength = paramsTable.length;
    for (var i = 0; i < tableLength; i++) {
        if (paramsTable[i].indexOf(name) != -1) {
            var paramValueTable = paramsTable[i].split('=');
            if (paramValueTable.length > 1) {
                paramValue = paramValueTable[1];
                return paramValue;
            }
        }
    }
    return paramValue;
}

/** Initialises departure city selector. */
function initSelectCity(){
	// Desactive les options dont la valeur est -1 (les titres de section)
	$('#slt_city option').each(function(){
        var value = $(this).val();
        if (value == -1) {
            $(this).attr('disabled', 'disabled');
        }
    });
	return false;
}

/** Initialises destination city/country selector. */
function initSelectDest(){

	// Desactive les options dont la valeur est -1 (les titres de section)
	$('#slt_destination option').each(function(){
        var value = $(this).val();
        if (value == -1) {
            $(this).attr('disabled', 'disabled');
        }
    });

    var params = getUrlParams(true);
    if (params.indexOf('s_c.de') !== -1) {
        var paramsTable = params.split('&');
        for (var i = 0; i < paramsTable.length; i++) {
            if (paramsTable[i].indexOf('s_c.de') !== -1) {
                var destParam = paramsTable[i].split('=');
                if (destParam.length > 1) {
                    var destValue = destParam[1];
                    var destValueTable = destValue.split(',');
                    $('#selectedDest').attr('style', '');
                    for (var j = 0; j < destValueTable.length; j++) {
                        $('#slt_destination option').each(function(){
                            var value = $(this).val();
                            if (value === destValueTable[j]) {
                                var label = $(this).text();
                                $(this).attr('disabled', 'disabled');
                                $('#selectedDest span').append('<div class="row"><div class="checkboxAreaChecked"></div><input class="check outtaHere" id="' + value + '" type="checkbox" value="' + value + '"/><label for="slt_destination">' + label + '</label></div>');

                            }
                        });
                    }
                }
            }
        }
    }
    return false;
}

/**
 *
 * @param {Boolean} [isParams] is true return second part.
 * @return {String} part of URL string.
 */
function getUrlParams(isParams){
    var url = window.location.href;
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


function buildDayOption(maxDay, year, month, currentDay){
    $("#slt_departureDay").html('');
    month -= 1;
    var url = window.location.href;
    $("#slt_departureDay").append('<option value="">peu m\'importe</option>');


    for (var i = 1; i <= maxDay; i++) {
        var d = new Date(year, month, i);
        var dayName = getDayName(d.getDay());
        if (i < 10) {
            if (i == currentDay) {
               $("#slt_departureDay").append('<option selected="selected" value="0' + i + '">' + dayName  + ' 0' + i + '  ' + '</option>');
            }
            else {
                $("#slt_departureDay").append('<option value="0' + i + '">' + dayName  + ' 0' + i + '  ' + '</option>');
            }
        }
        else {
            if (i == currentDay) {
               $("#slt_departureDay").append('<option selected="selected" value="' + i + '">' + dayName  + ' ' + i + '  ' + '</option>');
            }
            else {
              $("#slt_departureDay").append('<option value="' + i + '">' + dayName + '  ' + i + '</option>');
            }
        }
    }
}


function isBissextile(year){
    if (year % 4 !== 0) {
        return false;
    }
    return true;
}

function buildMonthYearDay(){
    var params = getUrlParams(true);
    var day = getParamValueByName(params, 'dd');
    var dmy = getParamValueByName(params, 'dmy');
    var currentDate = new Date();
    var currentMonth = currentDate.getMonth();
    var currentYear = currentDate.getFullYear();

    var url = window.location.href;
    $("#slt_departureMonth").append('<option value="">peu m\'importe</option>');

    for (var i = currentMonth; i < currentMonth + 12; i++) {
        var month = i;
        var monthName = '';
        var year = currentYear;
        if (month > 11) {
            month = month - 12;
            year = year + 1;
        }
        monthName = getMonthName(month);
        month += 1;
        if (month < 10) {
            month = '0' + month;
        }
        if (dmy === month + '/' + year) {
            $('#slt_departureMonth').append('<option selected="selected" value="' + month + '/' + year + '">' + monthName + '  ' + year + '</option>');
        }
        else {
            $('#slt_departureMonth').append('<option value="' + month + '/' + year + '">' + monthName + '  ' + year + '</option>');
        }
    }
    var monthStr = '';
    currentMonth += 1;
    if (currentMonth < 10) {
        monthStr = '0' + currentMonth;
    }
    else {
        monthStr = currentMonth + 1;
    }

    var currentDay = currentDate.getDate();
    var maxDay = getMaxDays(monthStr, currentYear);
    buildDayOption(maxDay, currentYear, monthStr, day);
    return false;
}

function getMaxDays(month, year){
    var maxDay = '';
    switch (month) {
        case '01':
        case '03':
        case '05':
        case '07':
        case '8':
        case '10':
        case '12':
            maxDay = 31;
            break;
        case '04':
        case '06':
        case '09':
        case '11':
            maxDay = 30;
            break;
        case '02':
            maxDay = (isBissextile(year)) ? 29 : 28;
            break;
        default:
            maxDay = 31;
            break;
    }
    return maxDay;
}

function getMonthName(month){
    var monthName = '';
    switch (month) {
        case 0:
            monthName = 'Janvier';
            break;
        case 1:
            monthName = 'Fevrier';
            break;
        case 2:
            monthName = 'Mars';
            break;
        case 3:
            monthName = 'Avril';
            break;
        case 4:
            monthName = 'Mai';
            break;
        case 5:
            monthName = 'Juin';
            break;
        case 6:
            monthName = 'Juillet';
            break;
        case 7:
            monthName = 'Août';
            break;
        case 8:
            monthName = 'Septembre';
            break;
        case 9:
            monthName = 'Octobre';
            break;
        case 10:
            monthName = 'Novembre';
            break;
        case 11:
            monthName = 'Decembre';
            break;
        default:
            monthName = 'Janvier';
            break;
    }
    return monthName;
}

function getDayName(day){
    var dayName = '';
    switch (day) {
        case 0:
            dayName = 'dim';
            break;
        case 1:
            dayName = 'lun';
            break;
        case 2:
            dayName = 'mar';
            break;
        case 3:
            dayName = 'mer';
            break;
        case 4:
            dayName = 'jeu';
            break;
        case 5:
            dayName = 'ven';
            break;
        case 6:
            dayName = 'sam';
            break;
        default:
            dayName = 'sam';
            break;
    }
    return dayName;
}

function endsWith(str, suffix){
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}


function countSelectDestNumber(){
    var selectDestNumber = 0;
    $('#selectedDest div').each(function(){
        selectDestNumber++;
    });
    return selectDestNumber;
}


function getQueryParameter(parameterName){
    var queryString = window.top.location.search.substring(1);
    var parameterName = parameterName + "=";
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



function initDatepicker(){
    $('.datepicker').datepicker({
        showOn: "button",
        buttonImage: "http://cdn.lastminute.com/site/ico_calendar.gif?skin=lastminute",
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

