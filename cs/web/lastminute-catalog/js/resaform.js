/**
 * resaform.js
 * quoc-anh.le@2013
 */

$(window).unload(function(){
  /** DON'T REMOVE (to force reload when clicking Back button) */
  $.ajax({cache: false});
});

$(document).ready(function(){

  /** initialize information */
  initPrices();

  /** Add live event for submit booking form */
  $("#calendar").on("click ", function(event){
    submitBooking(event);
  });

  /** Change the number of children */
  $('#nbChildren').on("click", function() {
    nbChildrenChangeListener();
  });

  /** Bind the event every time the booking options change */
  $('.resaOption').on("change", function() {
    updateUrl();
    updateBookingStatus();
    updateBookingTable();
  });

  /** Bind the event every time the url hash changes!*/
  $(window).on("hashchange", function(){
    //updateUrl();
  });
});

function getProductParams() {
  var params = '';
  if (window.location.href.indexOf('#') !== -1) {
    params = window.location.href.split('#')[1];
  }
  return params;
}

function initBookingOptions() {
  var params = getProductParams();
    var nbAdults = getParamValueByName(params, 'nbAdults');
    var nbChildren = getParamValueByName(params, 'nbChildren');
    var nbBabies = getParamValueByName(params, 'nbBabies');

    var date = getParamValueByName(params, 'date');
    var city = getParamValueByName(params, 'city');
    var durationNight = getParamValueByName(params, 'duration');
    var durationDay = getParamValueByName(params, 'preferredDurationDay');
    var duration = durationDay + ',' +  durationNight.split(',')[0];

    $('#spinner_room-1_pax-adults').attr('value',nbAdults==''?'2':nbAdults);
    $('#spinner_room-1_pax-childs').attr('value',nbChildren==''?'0':nbChildren);
    $('#spinner_room-1_pax-babys').attr('value',nbBabies==''?'0':nbBabies);

    if(date !=''){ $('#slt_departureMonth_resa').val(date.substring(date.length-7));}
    if(city !=''){$('#slt_city').val(city);}
    if(durationDay !=''){$('#slt_time').val(duration);}
    //$('#slt_departureMonth_resa option[value='+date+']').attr('selected','selected');
    //$('#slt_city option[value='+city+']').attr('selected','selected');
    //$('#slt_time option[value='+duration+']').attr('selected','selected');

    for(i=0; i<nbChildren; i++) {
      var value = getParamValueByName(params, "ageChildren"+i);
      if (value == '' || value == null) {
        value = "2";
      }
      $("#ageChildren"+i).val(value);
    }

    nbChildrenChangeListener();
}

function initPrices() {
  /* By default, the CGOS code is not actived! */
  $('input[id="codeActive"]').val("false");
  $('input[id="promoCode"]').val("");

  var url = '/productResumeAndResaFromAjax.cms?pid=' + $('#catalogCode').val() + '&' + getProductParams();
  $.ajax({
    url: url,
    type: 'GET',
    dataType: 'text',
    success: function(msg) {
      $('#price-block-waitingDom').hide();
      $('#reserve-block-waitingDom').hide();
      if($(msg).find('#price-block').length) {
        $('#price-block').html($(msg).find('#price-block').html());
      }
      if($(msg).find('#slt_departureMonth_resa').length) {
        $('#slt_departureMonth_resa').html($(msg).find('#slt_departureMonth_resa').html());
      }
      if($(msg).find('#calendar').length) {
        $('#calendar').html($(msg).find('#calendar').html());
      } else {
        $('#calendar').html('<script type="text/javascript">writeNoDispoMessage();</script>');
      }


      if($(msg).find('.bx-viewport').length) {
        $('.bx-viewport').html($(msg).find('.bx-viewport').html());
        $('.bx-viewport').show();
      }



      $('#price-block').show();
      $('#contain-book').show();
      $('#table-departure').show();
      initBookingOptions();
      updateBookingStatus();

      initFormComplemParams();
      initFormPartnerIdAjax();

      /**/
      if ($('#calendar').find('.best-price').length) {
        var finalPrice = $('#calendar').find('.best-price').parent().find('.new-price').text().split(' ')[0];
        $('#finalPrice').text(finalPrice);
      }
    }
  });
}

/** Adds partnerCode hidden form field using value retrieved with Ajax. */
function initFormPartnerIdAjax() {
  var partnerId = $.cookie("partnerId");
  if (partnerId) {
      var url = '/findPartnerCode?partnerId=' + partnerId;
      $.ajax({
          url: url,
          type: "GET",
          dataType: "text",
          success: function(msg){
           if (msg.indexOf("KO") == -1) {
            $('#reservationForm').append(
                 $('<input type="hidden" name="reservationProfile.defaultResConfigCode" value="' + msg + '" />'));
           }
          }
      });
  }
}

/** Initializes partnerId and omnitureSource complementary parameters from cookie value, to avoid cache problems. */
function initFormComplemParams() {
 var complemParamsInput = $('#complementaryParameters');

var partnerId = $.cookie("partnerId");
if (partnerId == null) {
 partnerId = "0";
}
if (partnerId != "" &&  complemParamsInput.val() &&
  complemParamsInput.val().indexOf("partnerId=") == -1) {
 complemParamsInput.val(
   complemParamsInput.val() +
   "&partnerId=" + partnerId);
}

var omnitureSource = $.cookie("omniture_source");
if (omnitureSource != null && omnitureSource != "" && complemParamsInput.val() &&
  complemParamsInput.val().indexOf("omnitureSource=") == -1) {
 complemParamsInput.val(
   complemParamsInput.val() +
   "&omnitureSource=" + omnitureSource);
}
}

/**
 * Submit booking
 */
function submitBooking(event) {
  var that = $(event.target);
  if (that.is("td")) {
    that = that.parent();
  } else
  if (that.is("span")) {
    that = that.parent().parent();
  } else {
    that = null;
  }
  if (that != null && that.attr("class").split(" ")[0] == "resaSubmit") {

    /** B2B */
    if($('#b2blogin').length != 0) {
      alert("Veuillez vous connecter afin d'effectuer votre réservation");
      return false;
    }

    /** C.G.O.S */
    var isPromoCodeValid = $('input[id="codeActive"]').val();
    if(isPromoCodeValid === "false") {
        alert("Veuillez renseigner votre numéro d'agent C.G.O.S");
        return false;
    }  else if (isPromoCodeValid == "true" && $('#inputPromoCode').length == 0)   {
      var promoCodes = $("#promoCode").val().replace(/^\s+|\s+$/g, "");
      if(promoCodes == '') {
            alert('Veuillez renseigner votre code promo');
            return false;
      }
     $('#reservationForm').append(
               $('<input type="hidden" id="inputPromoCode" name="client.affiliationNumber" value="' + promoCodes + '" />'));
    }

    /**
     * on set productUrl avec l'URL en cours lors du submit
     * on fait cela car l'URL (hash) change a chaque maj du formulaire
     */
    document.getElementById("productUrl").value = window.location;

    var signature = that.find("span").find("span").find("input").attr("id");
    if ($("#channel").val().indexOf("CGOS") != -1) {
      if (!(signature.indexOf("09-2013") == -1 && signature.indexOf("10-2013") == -1)) {
          $("#channel").val().replace("_NOFEES","");
          $("#channel").val($("#channel").val() + "_NOFEES");
       }
    }

    /** set variables */
    var childrenNumber = parseInt($("#spinner_room-1_pax-childs").val()),
        nbAdults = parseInt($("#spinner_room-1_pax-adults").val()),
        nbBabies = parseInt($("#spinner_room-1_pax-babys").val()),
        departCityCode = that.find("span").find("span").find("input").attr("name"),
        mainTransferPriceValue = 0;

        if($("#pp_transfer").length) {
		    var transferPrice = 0;
        	var passengers = childrenNumber + nbAdults;
        	mainTransferPriceValue = parseInt($("#pp_transfer_price").attr('value'));
        	transferPrice = mainTransferPriceValue * passengers;
        	$("#pp_transfer_price").val(transferPrice);
        }


        var url = $("#reservationForm").attr("action") + "?" + $("#reservationForm").serialize(),
        age;

	    if($("#pp_transfer").length) {
	    	$("#pp_transfer_price").val(mainTransferPriceValue);
	    }

    /** Don't follow */
    event.preventDefault();
    url += "&disponibility=" + signature;
    url += "&depCityCode=" + departCityCode;
    url += "&nbAdults=" + nbAdults;

    if (childrenNumber > 0){
      url += "&nbChildren=" + childrenNumber;
      for (var i = 0; i < childrenNumber; i++){
         age = $("#ageChildren" + i).val();
         url += "&ageChildren[" + i + "]" + "=" + age;
      }
    }

    if (nbBabies > 0) {
      url += "&nbBabies=" + nbBabies;
    }


    /** display waiting icon */
    var booking = $("#booking_" + signature);
    booking.parent("span").removeClass();
    booking.parent("span").attr("style","float:right; padding:0 3px 0 0;");
    booking.hide();
    booking.parent().parent().find(".new-price").hide();
    booking.parent().parent().find(".best-price").hide();
    booking.parent("span").find(".loader").show();


    var adjustDate = that.find(".adjust").html();
    url = adjustReturnDate(url, adjustDate);

    $(".overlay-lm").removeClass("hidden");

    $.ajax({cache: false});

    window.location.href = url;

  } /** end if submit btn click */

} /** end function */

function adjustReturnDate(url, date) {
  var adjustReturnDate = date.split('|')[0],
  adjustNbDays = date.split('|')[1],
  complementaryValue = 'adjustNbDays=' + adjustNbDays + '&adjustReturnDate=' + adjustReturnDate,
  complementaryInfo = extractUrlParams(url, 'complementaryParameters');
  if (complementaryInfo == null)  {
    url += "&complementaryParameters=" + encodeURIComponent(complementaryValue);
  } else {
    url = url.replace("complementaryParameters=" + complementaryInfo, "complementaryParameters=" + complementaryInfo + encodeURIComponent('&' + complementaryValue));
  }
  return url;
}

function extractUrlParams(url, param){
  var t = url.split('&');
  for (var i=0; i<t.length; i++){
    var x = t[ i ].split('=');
    if (x[0] === param) {
      return x[1];
    }
  }
  return null;
}

/**
 * When nbChildren value is changed, ageChildrenX inputs are shown/hidden.
 */
function nbChildrenChangeListener() {
    var childrenNumber = $('#spinner_room-1_pax-childs').val();
    $('.ageChildrenBack').each(function(){
      $(this).attr('style','display: none;');
    });
    for(var i = 0; i < childrenNumber; i++){
      $('#ageChildrenBack' + i).attr('style','display: block;');
    }
}

function updateUrl() {
  var nbAdults = $('#spinner_room-1_pax-adults').attr('value');
  var nbChildren = $('#spinner_room-1_pax-childs').attr('value');
  var nbBabies = $('#spinner_room-1_pax-babys').attr('value');

  var urlDate = $('#slt_departureMonth_resa').val();
  var urlCity = $('#slt_city').val();
  var time = $('#slt_time').val();
  var nbDay = time.split(",")[0];
  var nbNight = time.split(",")[1];

  var url = window.location.href;
  if (url.indexOf('#') > 0) {
    url = url.substring(0, url.indexOf('#'));
  }

  url = url + '#' +
  'nbAdults=' + nbAdults +
  '&nbChildren=' + nbChildren +
  '&nbBabies=' + nbBabies +
  '&date=' + urlDate +
  '&city=' + urlCity +
  '&duration=' + nbNight + ',' + nbNight +
  '&preferredDurationDay=' + nbDay;

  for (i = 0; i < nbChildren; i++) {
    var ageChild = $("#ageChildren"+i).val();
    url += "&ageChildren"+i+"="+ageChild;
  }
  window.location.href = url;
}

function updateBookingStatus() {

  var nbAdults = $('#spinner_room-1_pax-adults').attr('value');
  var nbChildren = $('#spinner_room-1_pax-childs').attr('value');
  var nbBabies = $('#spinner_room-1_pax-babys').attr('value');
  var date = $('#slt_departureMonth_resa option:selected').text();
  var city = $('#slt_city option:selected').text();
  var durationDayNight = $('#slt_time option:selected').text();

  var sumary = '<ul><li>'+nbAdults+' adultes</li>'+
               '<li>|</li><li>'+date+'</li>'+
               '<li>|</li><li>départ de '+city+'</li>'+
               '<li>|</li><li>'+durationDayNight+'</li>'+
               '</ul>';
  $('.summary-bookStay').html(sumary);
}

function updateBookingTable() {
  var pid = $('#catalogCode').val();
  var params = window.location.href;
  params = params.substring(params.indexOf('&date='));
  var url = '?pid=' + pid + params + '&from=calendar';

  $.ajax({
    url:'/calendarResaAjax.cms'+url,
    cache: false,
    type:"GET",
    dataType: "text",
    success: function(msg){
      var $response = $(msg);
      var content = $response.find('#calendar').html();
      $('#calendar').html(content);
    }
  });
}

/**
 * writeNoDispoMessage
 */
function writeNoDispoMessage(){
  var departDate = $("#slt_departureMonth_resa option:selected").text(),
      departCity = $("#slt_city option:selected").text(),
      departDuration = $("#slt_time option:selected").text();

  $("#calendar").html("<div class='block-infos'>" +
      "<p>" +
      "<strong style='color: #EC008C;'>" +
      "Il n'y a pas de disponibilité en " + departDate + ", départ de " + departCity +
      "</strong></p><br /><br /><br /></div>");
  $(".summary-bookStay").hide();
}