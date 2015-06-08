/**
 * productContent.js
 * quoc-anh.le@2013
 */

$(document).ready(function() {
  $('#backPage').attr('href', getCookieByName('previousSearch'));

  $('.colLeft').removeClass('hidden');

  if ($(".currentPage").length != 0) {
    $(".currentPage").val(window.location.href);
  }

  /**
   * 1. iPAD Mini
   * screen.availWidth = 748
   * screen.availHeight = 1024
   *
   * 2. iPad 2
   * screen.availWidth = 768
   * screen.availHeight = 928
   */

  if ($(window).width() <= 768 && $(window).height() <= 928) {
    $(".options").addClass("close");
  }

  /**
   * Go top

  $(window).scroll(function() {
    if($(this).scrollTop() > 100){
        $('#goTop').stop().animate({
            top: '20px'
            }, 500);
    }
    else{
        $('#goTop').stop().animate({
           top: '-100px'
        }, 500);
    }
});
$('#goTop').click(function() {
    $('html, body').stop().animate({
       scrollTop: 0
    }, 500, function() {
       $('#goTop').stop().animate({
           top: '-100px'
       }, 500);
    });
});
  */

  /**
   * Check C.G.O.S codes
   */
  $('#cgosValidator').submit(function() {
    var promoCodes = $("#promoCode").val().replace(/^\s+|\s+$/g, "");;
    if(promoCodes == '') {
          alert('Veuillez renseigner votre code promo');
          return false;
    }

    var url = '/cgosCodePromoValidator?promoCode=' + promoCodes;
    $.ajax({
        url: url,
        type: "GET",
        dataType: "text",
        success: function(msg){
          var response = handleMessage(msg);
          if (response != "") {
            alert(response);
          }
        }
    });
    return false;
  });


  /**
   * B2B LOGIN
   */
  $('#b2blogin').submit(function(){
    var login = $("#ipt_login").val(),
        psw = $("#psd_login").val();
    if(login == '') {
          alert('Veuillez renseigner votre identifiant');
          return false;
      }
    if (psw == '') {
       alert('Veuillez renseigner votre mot de passe');
       return false;
    }
  });

  /** --------------------------------------------- ******************************************** *
   * SEND PRODUCT DETAILS MAIL
   */
  $(document).on("click", "#sendProductEmail", function(){
    /** validate required fields */
    $("#sendEmailForm:input").not(":radio").removeClass("errorForm");
    if(validate()){
       $(".loader").show();
       $("#sendProductEmail").hide();

       $("#productId").val(getUrlParameter('pid'));
       $("#productName").val(getUrlParameter('pName'));

       $.post("sendProductEmail", $("#sendEmailForm").serialize(), function(data) {
         $(".loader").hide();
         $("#sendProductEmail").show();
         if (data !== null && data == "success") {
           $("#statusMsg").removeClass("msgError").addClass("msgValid").text("Message bien envoyé.");
         } else {
           $("#statusMsg").removeClass("msgValid").addClass("msgError").text("Une erreur est survenue lors de l'envoi.");
         }
       });

    }
  });

  /** open send product details email popup */
  $(".link-send").click(function() {
    var href = $(this).attr("href");
    window.open(href, "sendEmail", "resizable=yes,width=755,height=430,left=100,top=100,dependent=no,scrollbars=yes");
    return false;
  });

  /** --------------------------------------------- ******************************************** *
   * BEGIN DETAIS STAY COLLAPSE OPTIONS
   */

  $(document).on("click", ".title", function(){
    var title = $(this),
        options = title.parents(".options");

    if (options.hasClass("close")) {
      options.removeClass("close");
    } else {
      options.addClass("close");
    }
  });

  $(document).on("click", ".link-more", function(){
    var title = $(this),
        options = title.parent().parent();

    if (options.hasClass("close")) {
      options.removeClass("close");
    } else {
      options.addClass("close");
    }
  });

  $(document).on("click", ".details-ratings .link-more", function(){
    var title = $(this),
        options = title.parents(".details-ratings");

    if (options.hasClass("close")) {
      options.removeClass("close");
    } else {
      options.addClass("close");
    }
  });
});

/*------------------------------------------------*/
/*            Send product email functions        */
/*------------------------------------------------*/

function validate() {
  var valid = true;
  $("#sendEmailForm input[needValidate='true']").each(function(i){
     if(!requiredField(this)) {
       valid = false;
     }
  });
  $("input[needValidate='false']").each(function(i){
     $('#'+this.id+'_error').html("");
  })
  return valid;
}

function requiredField(o) {
  $('#'+o.id+'_error').html("");
  var ipt_email_friend = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
  var ipt_email = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
  var ipt_name = /^(\w){2,20}|[^u4e00-u9fa5]{2,20}$/;
  var ipt_email_friend_error = "- L'email du client doit \xEAtre renseign\xE9 et valide";
  var ipt_email_error = "- Votre email doit \xEAtre renseign\xE9 et valide";
  var ipt_name_error = "- Votre nom doit \xEAtre renseign\xE9\n";

  if ((trimStrig(o.value)).match(eval(o.id))) {
     return true;
  } else {
     $('#'+o.id+'_error').html(eval(o.id+'_error')).attr("style","color:red;");
     $('#'+o.id).addClass("errorForm");
  }
  return false;
}

function trimStrig (myString) {
  return myString.replace(/^s+/g,'').replace(/s+$/g,'')
}

/**
 * Returns an URL parameter.
 */
function getUrlParameter(name) {
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regex = new RegExp("[\\?&]"+name+"=([^&#]*)");
  var results = regex.exec(window.location.href);
  if (results === null) {
    return "";
  } else {
    return results[1];
  }
}

/** handle C.G.O.S message */
function handleMessage(msg) {
  var validString = " valide";
  var promoCode = msg.split("#")[0];
  var message = msg.split("#")[1];

  $("#promoCode").val(promoCode);
  if (message.indexOf(validString) != -1) {
    $('input[id="codeActive"]').val("true");
    if($('input[id="inputPromoCode"]').val()) {
      $('input[id="inputPromoCode"]').val(promoCode);
    } else {
       $('#reservationForm').append(
         $('<input type="hidden" id="inputPromoCode" name="client.affiliationNumber" value="' + promoCode + '" />'));
    }
  } else {
    $('input[id="codeActive"]').val("false");
  }
  return message;
}

