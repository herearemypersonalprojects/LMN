/**
 * compare.js
 * quoc-anh.le@travelsoft.fr
 */
window.name = 'comparator';

$(document).on("click", ".btn-compare", function() {
  var cookie = getCookieByName('comparator');
  if (cookie === '') {
    alert("Veuillez ajouter des produits dans le comparateur");
    return false;
  }
});

$( document ).ready(function() {
  // init from the previous session
  var ids = getCookieByName(name);
  var curCparedProdsNmb = 0;
  if(ids.length != 0) {
    curCparedProdsNmb = ids.split(separated).length;
    $('.results-form').find('input[type=checkbox]').each(function() {
      var that = $(this);
      if (ids.indexOf(that.val())>=0) {
        //curCparedProdsNmb ++;
        that.attr("checked", "checked");
      }
    });
  }

  var currNum = $('.cparedProdsNmb:first');
  var text = currNum.text().substring(1);
  var remain = text.substring(text.indexOf(' '));
  $('.cparedProdsNmb').text('('+curCparedProdsNmb+remain);

   // ACTIONS
  $('.deleteComparator').click(function() {
    $('.add-compare').find('input').removeAttr("checked", "checked");
    $('.cparedProdsNmb').text('(0 produits séléctionnés)');
    document.cookie = name+'=';
  });

  $('.results-form').on('click').find('input[type=checkbox]').click(function(){
    var currNum = $('.cparedProdsNmb:first');
    var text = currNum.text().substring(1);
    var curCparedProdsNmb = text.substring(0,text.indexOf(' '));
    var remain = text.substring(text.indexOf(' '));

    var that = $(this);
    if (that.attr('checked')) {
      curCparedProdsNmb = curCparedProdsNmb>0?curCparedProdsNmb-1:0;
      that.removeAttr("checked", "checked");
      removeFromCookies(name, that.attr('value'));
    } else {
      if ( parseInt(curCparedProdsNmb)+1>3) {
        alert("Vous ne pouvez comparer que 3 produits au maximum");
        that.removeAttr("checked", "checked");
      } else {
        curCparedProdsNmb++;
        that.attr("checked", "checked");
        addToCookies(window.name, that.attr('value'));
      }
    }
    $('.cparedProdsNmb').text('('+curCparedProdsNmb+remain);

  });
});
