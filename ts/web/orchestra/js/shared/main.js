var quickSearchErrors = 0,
    lastSearchVal = "",
    nextNumProdSeq = 1;

$(document).ready(function() {

  orxapi.restoreFromTabCookie();

  var tab_tabsGroup = $("#tabsBarZone").find(".tabsNav");

  /* drag tab */
  tab_tabsGroup.sortable({
     axis: "x",
     placeholder: "tab-highlight",
     delay: 100,
     start: function(event, ui) {
       $(this).find(".tab-highlight").width(ui.item.width());
       ui.helper.width() + 5;
       var wTabGrab = ui.helper.width();
       ui.helper.width(wTabGrab+1);
       ui.helper.height(30);
     },
     change: function(event, ui) {
         orxapi.storeTabCookie();
     }
  });

  /* add class hover on hover */
  $("#tabsBarZone").find(".tab").hoverMod();

  /** click on middle button mouse - CLOSE */
  $("#tabsBarZone").on("mousedown", ".tab", function(b) {
    if(b.which==2) {
      $(this).closeTab();
    }
  });

  // FULLSCREEN beta
  function toggleFullScreen(elemFS) {
    if ((elemFS.fullScreenElement && elemFS.fullScreenElement !== null) ||    // alternative standard method
        (!elemFS.mozFullScreen && !elemFS.webkitIsFullScreen)) {               // current working methods
      if (elemFS.documentElement.requestFullScreen) {
        elemFS.documentElement.requestFullScreen();
      } else if (elemFS.documentElement.mozRequestFullScreen) {
        elemFS.documentElement.mozRequestFullScreen();
      } else if (elemFS.documentElement.webkitRequestFullScreen) {
        elemFS.documentElement.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
      }
    } else {
      if (elemFS.cancelFullScreen) {
        elemFS.cancelFullScreen();
      } else if (elemFS.mozCancelFullScreen) {
        elemFS.mozCancelFullScreen();
      } else if (elemFS.webkitCancelFullScreen) {
        elemFS.webkitCancelFullScreen();
      }
    }
  }
  $("#btn_fullscreen").click(function(){
    toggleFullScreen(document);
    $(this).toggleClass("activeFS");
  });

});


/** Orchestra API object */
var orxapi = (function(window) {
  // load scripts from cache
  $.ajaxSetup({
    cache: true,
    data: {
      // always having this paran in request, we know that request was made from ajax call
      // this data is accumulative to other data that will be added. Possible values: backOffice, accounting
      module: $("body").data("module")
    },
  });


  // Use local or create orxapi object
  var orx = window.orxapi || {};

  // commonly used selectors
  orx.selectors = {
    body: $("body"),
    starterZone: $("#starterZone"),
    bodyContent: $("#bodyContent"),
    tabsBarZone: $("#tabsBarZone")
  };

  orx.parser = {
    /**
     * @param url the url to parse
     * @param key the key of the parameter we want to find
     * @returns the value of the parameter associated to the input key
     */
    getParameter : function(url, key) {
      key = key.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
      var regex = new RegExp("[\\?&]" + key + "=([^&#]*)"),
          results = regex.exec(url);
      return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    },

    getParameters : function(url, key) {
      key = key.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
      var regex = new RegExp("[\\?&]" + key + "=([^&#]*)", "g");
      var result;
      var globalResults = new Array();
      while ((result = regex.exec(url)) !== null) {
        if (result != null) {
          globalResults.push(decodeURIComponent(result[1].replace(/\+/g, " ")));
        }
      }
      return globalResults;
    }

  }

  /**
   * Updates the input blocks from an ajax response
   *
   * @param updates the JSON object containing the updates
   */
  orx.updatePageFromJSONResponse = function(data) {
    var updates = $.parseJSON(data);
    if (updates) {
      // Update the HTML blocks
      var blocksToUpdate = updates.blockUpdates
      if (blocksToUpdate) {
        for (var currentBlockUpdate in blocksToUpdate) {
          if(blocksToUpdate.hasOwnProperty(currentBlockUpdate)) {
            var id = currentBlockUpdate;
            var content = blocksToUpdate[currentBlockUpdate];
            $("#" + id).html(content);
          }
        }
      }

      // Update the values
      var valuesToUpdate = updates.valueUpdates
      if (valuesToUpdate) {
        for (var currentValUpdate in valuesToUpdate) {
          if(valuesToUpdate.hasOwnProperty(currentValUpdate)) {
            var id = currentValUpdate;
            var value = valuesToUpdate[currentValUpdate];
            $("#" + id).val(value);
          }
        }
      }
    }
  }

  // ajax default options
  orx.ajax = {
    type: "POST",
    dataType : "html",
    error : function(jqXHR, textStatus, errorThrown) {
       if (jqXHR.status == "509") {
         // Session expired. Set targetAction param in login form
         var targetAction = jqXHR.getResponseHeader('targetAction');
         window.location = "login.action?targetAction=" + targetAction;
       } else {
         // all other errors
         var contentDialog = $("<div/>",{ "id":"contentDialog"});
         if (jqXHR.status == "500") {
           // FreemarkerExceptionHandler or B2BExceptionHandlerInterceptor were executed
           contentDialog.html(jqXHR.statusText);
         } else {
             if(jqXHR.responseText != null && jqXHR.responseText != "") {
                 contentDialog.html(jqXHR.responseText);
             } else {
                 contentDialog.html($("#genericErrorMsg").text());
             }
         }
         contentDialog.dialog({
               title : 'Erreur',
               modal : true,
               zIndex: 8888,
               resizable: false,
               close: function(event, ui) {
                 $(this).dialog("destroy").remove();
               },
               buttons:{"OK": function() {
               $(this).dialog("close");}}
         });
         $("#overlayLoading").remove();
         orx.selectors.starterZone.removeClass("loading");
       }
    },
    beforeSend : function(jqXHR, settings) {
      orx.selectors.starterZone.addClass("loading");
      orx.selectors.body.append($("<div/>",{ "id":"overlayLoading"}));
      orx.selectors.body.css("cursor", "wait");

      if ($("#wrapper").find(".niceScroll").length) {
         $("#wrapper").find(".niceScroll").getNiceScroll().remove();
      }
    },
    complete: function(jqXHR, textStatus) {
        $("#overlayLoading").remove();
        orx.selectors.starterZone.removeClass("loading");
        orx.selectors.body.css("cursor", "default");
    }
  };

  // Plugin
  orx.plugin = {
    // datepicker
    datepicker : {
      dateFormat: "dd/mm/yy",
      changeMonth: true,
      changeYear: true,
      onSelect: function(dateText, inst) {
        if(!inst.input.attr("value") == "") {
          inst.input.addClass("lightValue");
        } else {
          inst.input.removeClass("lightValue");
        }

        if ($(this).hasClass("startDate")) {
          var $endDateIpt = $(this).parents(".margeDateZone").find(".endDate");
          var startDate = $.datepicker.parseDate("dd/mm/yy", dateText);
          if ($endDateIpt.attr("value") != "") {
            var endDate = $.datepicker.parseDate("dd/mm/yy", $endDateIpt.attr("value"));
            if (startDate > endDate) {
              $endDateIpt.datepicker( "setDate", startDate );
            }
          }
        }
        $(this).valid();
      },
      beforeShow: function(input, inst) {
        if ($(this).hasClass("endDate")) {
          // set defaultDate and minDate into the endDate depending on the startDate
          var startDateStr = $(input).parents(".margeDateZone").find(".startDate").attr("value");
          if(startDateStr != ""){
             var startDate = $.datepicker.parseDate("dd/mm/yy", startDateStr);
             inst.input.datepicker("option", "defaultDate", new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()));
             inst.input.datepicker("option", "minDate", new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()));
          } else {
             inst.input.datepicker("option", "minDate", null);
          }
        }
      }
    },
    // dialog
    dialog : {
      modal : true,
      zIndex: 8888,
      resizable: false,
      title: "Avertissement",
      close: function(event, ui) {
        $(this).dialog("destroy").remove();
      },
      create: function(event, ui){
        $(this).parents(".ui-dialog").find("button").addClass("btn");
      }
    },
    wysiwygLight : {
      language: "fr",
      mode: "exact",
      theme: "advanced",
      skin: "cirkuit",
      theme_advanced_buttons1: "bold,italic,underline,strikethrough,|,bullist,numlist,undo,redo,link,unlink,|,fullscreen,",
      plugins : "fullscreen",
      fullscreen_new_window : true,
      theme_advanced_toolbar_align: "left",
      theme_advanced_toolbar_location: "top",
      theme_advanced_statusbar_location: "bottom",
      content_css : "./shared/ts/web/css/wysiwygLight.css",
      theme_advanced_statusbar_location : "bottom",
      theme_advanced_path : false,
      /*resizable:true,
      theme_advanced_resizing : true,
      theme_advanced_resize_horizontal : false,*/
      convert_urls : false
    },
    cfgWysiwygNormal : {
        convert_urls: false,
        language: "fr",
        mode: "exact",
        plugin_insertdate_dateFormat: "%d-%m-%Y",
        plugin_insertdate_timeFormat: "%H:%M:%S",
        plugins: "style,table,advhr,advimage,advlink,emotions,insertdatetime,media,searchreplace,contextmenu,paste,fullscreen,visualchars,nonbreaking",
        relative_urls: false,
        theme: "advanced",
        theme_advanced_buttons1: "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect",
        theme_advanced_buttons2: "cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,|,insertdate,inserttime,|,forecolor,backcolor",
        theme_advanced_buttons3: "tablecontrols,|,hr,removeformat,visualaid,|,sub,sup,|,charmap,emotions,media,advhr,|,ltr,rtl,|,fullscreen",
        theme_advanced_buttons4: "styleprops,|,visualchars,nonbreaking,blockquote",
        theme_advanced_toolbar_align: "left",
        theme_advanced_toolbar_location: "top",
        theme_advanced_statusbar_location: "bottom",
        convert_urls : false
    }
  }
  return orx;

})(window);



(function($, orx){

  // Cache jQuery Object
  var bodyContent = orx.selectors.bodyContent;


  /**********************************************************************
    COMMON PAGE
  ***********************************************************************/

  /*---------------------------------------------------------------------
    Hover mod
    Initialize hover
  ----------------------------------------------------------------------*/
  $.fn.hoverMod = function() {
    $(this).on("hover",function() {
      $(this).toggleClass("hover");
    });
  };

  /*---------------------------------------------------------------------
    load content tabs and active
  ----------------------------------------------------------------------*/

  /* Prevent search form submit on enter */
  bodyContent.on("submit", ".searchForm", function(evt) {
    evt.preventDefault();
    $(this).find(".searchBtn").trigger("click");
  });

  /* Prevent modif checkbox form submit on enter */
  bodyContent.on("submit", "#selectModifForm", function(evt) {
    evt.preventDefault();
    $("#checkboxModifZone .btn_popinModif").trigger("click");
  });

  /* generic close popin btn */
  orx.selectors.body.on("click", ".btn_cancelPopin", function(evt) {
    evt.preventDefault();
    $("#wrapPopin").animate({"padding-top": "+=90px" }, 150, "easeInCubic", function(){ $("#wrapPopin").remove(); });
  });

  /*
   * Esc btn : close popin if it exists and there is no dialog active
   *         : close mail
   * Enter btn : validate ppn if it exists and there is no dialog active
   *           : send mail
   */
  $("body").on("keyup", "#wrapPopin input", function(e) {
    if (e.keyCode == 13) {
        $('.validatePpn').click();
    } else if (e.keyCode == 27) {
        $('.btn_cancelPopin').click();
    }
  });

  /** SSO links: add loader */
  $("#starterZone").on("click", "a", function() {
    var attr = $(this).attr("target");
    if (typeof attr === "undefined" || attr === false) {
      // only for a hrefs without target attr
      orx.selectors.starterZone.addClass("loading");
      orx.selectors.body.append($("<div/>",{ "id":"overlayLoading"}));
      orx.selectors.body.css("cursor", "wait");
    }
  });

  /* Click on the reset button (Reinitialiser for a new user) */
  $("#btn_reinit").click(function() {
    // remove cookie of tabs except three default tabs (Produits, Hotels and Vols)
    var cookieStr = "tabs_" + userId + "_" + orx.selectors.body.data("module");
    $.cookie(cookieStr, null);

    // remove cookie of preferences so that it resets the formule to be default and empte the comparator
    $.cookie("preferences", null);

    // refresh
    location.reload();
  });

  /* Click on header menu icons (except 'starter' one) */
  $("#headerBarZone").on("click", " ul.nav li, #btn_userProfile", function() {

    var $that = $(this),
    pge = $that.data("nav"),
    shared = $that.data("shared"),
    param = $that.data("param"),
    component = $that.data("component"),
    module = orx.selectors.body.data("module");

    if (!$that.hasClass("disabled")) {
      if (orxapi.selectors.tabsBarZone.find("li[data-page='" + pge + "']").length) {
        // tab was already opened. Navigate into it.
        orxapi.selectors.tabsBarZone.find("li[data-page='" + pge + "']").trigger("click");
      } else {

        // load the page and create the tab
        var url = pge + ".action";
        $.ajax($.extend({}, orxapi.ajax, {
          url : url,
          data: {module : module, param : param},
          success : function(data, textStatus, jqXHR) {
            orxapi.selectors.bodyContent.removeClass().addClass(pge).html(data);
            orxapi.addTab(pge, component);

            //Load the js from the data component if it exists, the data page otherwise
            if (typeof(component) != "undefined" && component != "") {
              orxapi.loadJs(module, component, shared);
            } else {
              orxapi.loadJs(module, pge, shared);
            }
          },
          error: function() {
              var contentDialog = $("<div/>",{ "id":"contentDialog"});
              contentDialog.html($("#genericErrorMsg").text());
              contentDialog.dialog({
                    title : 'Erreur',
                    modal : true,
                    zIndex: 8888,
                    resizable: false,
                    close: function(event, ui) {
                      $(this).dialog("destroy").remove();
                    },
                    buttons:{"OK": function() {
                    $(this).dialog("close");}}
              });
          }

        }));

      }
        }
  });

  /* tabs clicks */
  orxapi.selectors.tabsBarZone.on("click", "li.tab", function(event) {

    var that = $(this);
    var module = orx.selectors.body.data("module");
    if ($(event.target).hasClass("pto_cross")) {
       if (that.hasClass("notCloseable") == false) {
         that.closeTab();
       }
    } else {
      var pge = that.data("page"),
          url = pge + ".action",
          shared = that.data("shared"),
          component = that.data("component");

      // if tab was file tab, use its id
      var id = that.data("id");
      $.ajax($.extend({}, orxapi.ajax, {
        url : url,
        data: {id : id, module : module},
        success : function(data, textStatus, jqXHR) {
          orxapi.selectors.bodyContent.removeClass().addClass(pge).html(data);
          //Load the js from the data component if it exists, the data page otherwise
          if (typeof(component) != "undefined" && component != "") {
            orxapi.loadJs(module, component, shared);
          } else {
            orxapi.loadJs(module, pge, shared);
          }

          // extract the tab label
          var $data = $(data);
          var tab_label = $data.data("tab-label");

          //Display the prefix if needed
          if(pge == "pge_product"){
            if(typeof($data.data("tab-prefix")) != "undefined" && $data.data("tab-prefix") != "") {
              tab_label = "<span class=\"prefix\">" + $data.data("tab-prefix") + " </span> " + $data.data("data-tab-label");

            } else {
              tab_label = $data.data("data-tab-label");
            }
          }else{
            if(typeof($data.data("tab-prefix")) != "undefined" && $data.data("tab-prefix") != "") {
              tab_label = "<span class=\"prefix\">" + $data.data("tab-prefix") + " </span> " + $data.data("tab-label");

            } else {
              tab_label = $data.data("tab-label");
            }
          }

          that.find(".lbl").html(tab_label);

          if(that.parent().find(".active").length) {
              that.parent().find(".active").removeClass("active");
          }
          that.addClass("active");
          location.hash = '';
          orxapi.storeTabCookie();
        },
        error: function() {
            var contentDialog = $("<div/>",{ "id":"contentDialog"});
            contentDialog.html("<p>La connexion a été interrompue.<br />Veuillez réessayer dans quelques instants.</p>");
            contentDialog.dialog({
                  title : 'Erreur',
                  modal : true,
                  zIndex: 8888,
                  resizable: false,
                  close: function(event, ui) {
                    $(this).dialog("destroy").remove();
                  },
                  buttons:{"OK": function() {
                  $(this).dialog("close");}}
            });
        }
      }));
    }

  });

  /* Load the correct javascript file */
  orxapi.loadJs = function (module, pge, shared) {
    //if the page in flaged as shared, get it in the shared folder
    if(shared) {
      $.getScript("./shared/ts/web/js/shared/" + pge + ".js");
      return;
    }

    //else, check in the related module
      if (module == "b2b") {
          $.getScript("./shared/ts/web/js/b2b/" + pge + ".js");
      } else {
          $.getScript("./shared/ts/web/js/bo/" + pge + ".js");
      }
  }

  /* resetFormGeneric */
  orxapi.resetFormGeneric = function ($form) {
    $form.find('select:not(".no-reset")').each(function() {
        $(this).prop("selectedIndex", 0);
    });

    $form.find(".multiselect").each(function() {
      var $currMltSlt = $(this);
      if ($currMltSlt.data("noreset") != undefined && $currMltSlt.data("noreset") == true) {
        // get the default values
        $currMltSlt.find("option.defaultSelected").each(function() {
          $(this).attr("selected", 1);
        });
        $currMltSlt.multiselect("refresh");
      } else {
        // simple multiselect
        $currMltSlt.multiselect("uncheckAll");
      }
    });

    $form.find("input[type!='hidden']").val("");
    $form.find("select, input").removeClass("lightValue");
    $form.find("input:checkbox:checked").prop("checked", false);

  }

  /* Dropdown starter menu */
  orxapi.selectors.starterZone.find(".pto_starter").dropdown()

  /* adding lightValue class  */
  $("body").on("blur", ".valueFocus", function(){
    if(!$(this).attr("value") == "") {
      $(this).addClass("lightValue");
    } else {
      $(this).removeClass("lightValue");
    }
  });



  /*--------------------------------------------------------------------------------*/
  /*                             Orxapi functions                                   */
  /*--------------------------------------------------------------------------------*/

  /** add icon-calendar for the datepicker  */
  orxapi.pictoDate = function() {
      orx.selectors.body.find(".datePicker").each(function(i) {
      var pictoCal = $("<i />",{ "class" : "icon-calendar iconForInput ui-datepicker-trigger"});
      if(!$(this).hasClass("withPicto")){
        $(this).addClass("withPicto");
        $(this).after(pictoCal);
        $(this).next(".iconForInput").click(function(){
          $(this).prev().focus();
        });
      }
    });
  };

  /** Resize height content for scroll  */
  orxapi.resizePopin = function () {
    var popin = $("#wrapPopin").find(".popin"),
        minHeigt;
    if(popin.height() <= $(window).height()) {
      minHeigt = $(window).height() - 50;
      popin.css("min-height", minHeigt);
    }
  };

  /** Look at existing displayed products to find out the new number sequence  */
  orxapi.findNextNumProdSeq = function(){
    orxapi.selectors.tabsBarZone.find("li.tab_product").each(function(index) {
      var label = $(this).find("span.lbl").html();

      var num = parseInt(label.substring(label.indexOf(":")),10);
      if(num >= nextNumProdSeq){
        nextNumProdSeq = num+1;
      }
   });
  }


  /**
   * Function that will create tab for the specific page.
   * The tab prefix, label and id will be retrieved from the body if it exists, otherwise the prefix and id will be empty strings and
   * the label will be retrieve from the header buttons.
   */
  orxapi.addTab = function(pageName, component) {
    orxapi.selectors.tabsBarZone.find("li.active").removeClass("active");
    var bodyFirstDiv = orxapi.selectors.bodyContent.find("div:eq(0)");

    orxapi.findNextNumProdSeq();

    var tabData;
    if(pageName == "pge_product"){
      var tabData = {
                     pageName: pageName,
                     tabPrefix: bodyFirstDiv.attr("data-tab-prefix") ? bodyFirstDiv.attr("data-tab-prefix") : "",
                     tabLabel: bodyFirstDiv.attr("data-tab-label") ?  nextNumProdSeq + ". " + bodyFirstDiv.attr("data-tab-label") : $("#headerBarZone").find("li[data-nav='" + pageName + "']").find("span").text(),
                     tabActive: true,
                     id: bodyFirstDiv.data("id") ? bodyFirstDiv.data("id") : "",
                     component: component
      };
      nextNumProdSeq = 1;
    }else{
      var tabData = {
        pageName: pageName,
        tabPrefix: bodyFirstDiv.attr("data-tab-prefix") ? bodyFirstDiv.attr("data-tab-prefix") : "",
        tabLabel: bodyFirstDiv.attr("data-tab-label") ?  bodyFirstDiv.attr("data-tab-label") : $("#headerBarZone").find("li[data-nav='" + pageName + "']").find("span").text(),
        tabActive: true,
        id: bodyFirstDiv.data("id") ? bodyFirstDiv.data("id") : "",
        component: component
      };
    }

    orxapi.addTabInBar(tabData);

    orxapi.storeTabCookie();
  };


  orxapi.addTabInBar = function(tabData) {

    var pageName = tabData["pageName"];
    var component = tabData["component"];

    var liClass= "tab ";
    if (typeof(tabData["tabActive"]) != "undefined" && tabData["tabActive"]) {
      liClass += "active ";
    }

    var className = pageName.replace("pge","tab");
    liClass += className;

    var tabConfIpt = $("span.tabConfigs").find("input[name='" + className + "']");
    if (tabConfIpt.length && tabConfIpt.val() == "false") {
      // this tab is not closeable
      liClass += " notCloseable";
    }

    var icoClass = pageName.replace("pge","ico");
    var dataObj = tabData["dataObj"];

    //Display the prefix if needed
    var tabLabel = "";
    if(typeof(tabData["tabPrefix"]) != "undefined" && tabData["tabPrefix"] != "") {
      tabLabel = "<span class=\"prefix\">" + tabData["tabPrefix"] + " </span> " + tabData["tabLabel"];
    } else {
      tabLabel = tabData["tabLabel"];
    }

    var liTab = "";
    if (pageName == "pge_file" || pageName == "pge_product") {
      // we are creating file tab -> stock id that will be used in tab navigation
      var id = tabData["id"];
      liTab = $("<li/>",{ "class":liClass, "data-page":pageName, "data-component": component, "data-id":id});
    } else {
       liTab = $("<li/>",{ "class":liClass, "data-page":pageName, "data-component": component});
    }
    if (pageName != "pge_product") {
      var spanIco = $("<span/>",{ "class":"ico_tab "+icoClass, "html":"&nbsp;" }).appendTo(liTab);
    }
    var spanLabel = $("<span/>",{ "class" : "lbl", "html":tabLabel }).appendTo(liTab);

    if (tabConfIpt.length == 0 || tabConfIpt.val() == "true") {
        spanCross = $("<span/>",{ "class" : "pto_cross", "html":"&nbsp;" }).appendTo(liTab);
    }

    if (typeof(dataObj) != "undefined") {
      liTab.data(dataObj);
    }

    orxapi.selectors.tabsBarZone.find("ul.tabsNav").append(liTab);
  };

  orxapi.storeTabCookie = function () {

      var cookieStr = "";
      orxapi.selectors.tabsBarZone.find("li.tab").each(function(index) {
         var params = $(this).data();
         delete params.sortableItem;

         $.extend(params, {tabLabel: $(this).find("span.lbl").html(), tabActive: $(this).hasClass('active')});

         cookieStr += JSON.stringify(params) + "\n";
      });

  // change the expiration of cookie to 1 day
      $.cookie("tabs_" + userId + "_" + orx.selectors.body.data("module"), cookieStr, { expires: 1 });
  }

  orxapi.restoreFromTabCookie = function() {

      var cookieStr = $.cookie("tabs_" + userId + "_" + orx.selectors.body.data("module"));
      if (cookieStr != null && cookieStr.length > 1) {
          var cookieStrArray = cookieStr.split("\n");
          if (cookieStrArray.length > 0) {
              orxapi.selectors.tabsBarZone.find("ul.tabsNav").empty();
              $(cookieStrArray).each(function(idx) {
              if (this.length > 1) {
                var dataObj = JSON.parse(this);

                var tabData = {pageName: dataObj["page"],
                               tabPrefix: orxapi.selectors.bodyContent.find("div:eq(0)").attr("data-tab-prefix"),
                               tabLabel: dataObj["tabLabel"],
                               id: dataObj["id"],
                               tabActive: dataObj["tabActive"],
                               dataObj: dataObj};
                orxapi.addTabInBar(tabData);
               }
              });
          }

          /* open default (1st) tab */
          if(orxapi.selectors.tabsBarZone.find(".active").length == 0) {
              orxapi.selectors.tabsBarZone.find("li.tab:eq(0) span:eq(0)").trigger('click');
          } else {
              orxapi.selectors.tabsBarZone.find("li.tab.active:eq(0)").trigger('click');
          }
      } else {
        // no tabs in cookie. open default tab (liste for bo module)
        var module = orx.selectors.body.data("module");
        if (module == "backOffice") {
          $("#headerBarZone").find("li[data-nav='pge_list']").trigger("click");
        } else if (module == "b2b"){
          //Open the tabs that have the openedstartup tag if any, the first home otherwise
          if ($("#headerBarZone").find("li[data-openedstartup='true']").length > 0) {
            //For each item, build it's tab without loading it's content
            $("#headerBarZone").find("li[data-openedstartup='true']").each(function(){
              orxapi.addTab($(this).data("nav"), $(this).data("component"));
            });
            // select as active the tab that we will open
            var $navTabToOpen = $("#headerBarZone").find("li[data-openedstartup='true']").first(),
              navData = $navTabToOpen.data("nav");
            $("#tabsBarZone").find("li.active").removeClass("active");
            $("#tabsBarZone").find("li[data-page='" + navData + "']").addClass("active");

            //Then click on the first tab created
            $navTabToOpen.trigger("click");
          } else {
            $("#headerBarZone").find("li[data-nav^='pge_home']").first().trigger("click");
          }
        } else {
          $("#headerBarZone").find("li[data-nav='pge_list']").trigger("click");
        }
      }

  }

  orxapi.getUrlParameter = function(name, target) {
    name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)");
    var result = regex.exec(target);
    return result !== null ? result[1] : "";
  }

  /* Search resultTable : on double click, open details */
  $("#bodyContent").on("dblclick", ".table-result-files tbody tr", function(event) {

    if ($(event.target).parents(".td_actionUnit").length) {
      // 2x clicked on actions.
      return false;
    }


    var id = $(this).data("id");
    if ($("#tabsBarZone").find("li[data-id='" + id + "']").length) {
      // tab for this file was already opened. Navigate to it.
      $("#tabsBarZone").find("li[data-id='" + id + "']").trigger("click");
    } else {
      // load the page and create the tab
      orxapi.openPage(id);
    }
 });

  /* Search resultTable : on title single click, open details */
  $("#bodyContent").on("click", ".openProductZone", function(event) {

    var id = $(this).parents("table.tbe_prdtBlock").find("tbody tr:eq(0)").data("id");
    if ($("#tabsBarZone").find("li[data-id='" + id + "']").length) {
      // tab for this file was already opened. Navigate to it.
      $("#tabsBarZone").find("li[data-id='" + id + "']").trigger("click");
    } else {
      // load the page and create the tab
      orxapi.openPage(id);
    }
 });

  /* Open product tab with specific id  */
  orxapi.openPage = function(id) {

    // if tab was already opened, click on it.
    if ($("#tabsBarZone").find("li[data-id='" + id + "']").length) {
      // tab for this file was already opened. Navigate to it.
      $("#tabsBarZone").find("li[data-id='" + id + "']").trigger("click");
    } else {
      var id = id;
      var module = orx.selectors.body.data("module");
      var shared = "false";

      if(module == "b2b") {
        var pge = "pge_product";
      } else {
        var pge = "pge_file";
      }

      $.ajax($.extend({}, orxapi.ajax, {
        url : pge + ".action",
        data: {id : id},
        success : function(data, textStatus, jqXHR) {
          orxapi.selectors.bodyContent.removeClass().addClass(pge).html(data);
          orxapi.addTab(pge);
          orxapi.loadJs(module, pge);
        },
        error: function() {
            var contentDialog = $("<div/>",{ "id":"contentDialog"});
            contentDialog.html($("#genericErrorMsg").text());
            contentDialog.dialog({
                  title : 'Erreur',
                  modal : true,
                  zIndex: 8888,
                  resizable: false,
                  close: function(event, ui) {
                    $(this).dialog("destroy").remove();
                  },
                  buttons:{"OK": function() {
                  $(this).dialog("close");}}
            });
        }
      }));
    }
  };

  /** ----------------------- */
  /**                         */
  /** modification ppn page   */
  /**                         */
  /** ----------------------- */

  /** Reinit adder tr values */
  orxapi.resetValues = function (target) {
    $(target).find("input").val("").removeClass("lightValue");
    $(target).find("select option:selected").removeAttr("selected");
  }

  orxapi.addHiddenIpt = function(target, name, value) {
    if (value != null && value != "") {
      $(target).append($("<input >", { name:name, value:value, type:"hidden" }));
    }
  };

  /** Add btn with specific class to target*/
  orxapi.addSpecificCancelBtn = function(target, className) {
    var btnToAdd = $("#actionBtns").find("button.cancelOneModif").clone();
    btnToAdd.find("i").addClass(className);
    $(target).find("td.td_action").empty().html(btnToAdd);
  }

  orxapi.showFormFields = function(target, hideFormFields) {
    // show inputs and selects
    var $tr = $(target),
        $table = $tr.parents("table:eq(0)"),
        trIndex = $table.hasClass("hotelRoomModif") ? $("#ppn_file-modifProcessForm table.hotelRoomModif").find("tbody tr").not(".tr_adder").index($tr) :  $table.find("tbody tr").not(".tr_adder").index($tr),
        $tdsToChange = $tr.find("td").not(".td_action");

    $tdsToChange.each(function(i) {

      var $this = $(this),
          sourceTd = $(this).attr("class"),
          targetTd = $table.find("tr.tr_adder").find("." + sourceTd);

      var $clonedField;
      var selValue = $this.find("span.viewModif").data("value");
      if (targetTd.find("input").length) {
        $clonedField = targetTd.find("input").clone(false);
        $clonedField.val(selValue);
      } else if (targetTd.find("select").length) {
        $clonedField = targetTd.find("select").clone();
        $clonedField.find("option[value='" + selValue + "']").prop("selected", true);
      }

      if ($clonedField.hasClass("datePicker")) {
        $clonedField.removeClass("hasDatepicker");
      }

      $clonedField.attr("name", $clonedField.data("name").replace("$$", trIndex));
      $clonedField.attr("class", $clonedField.data("class"));
      $clonedField.attr("id", $clonedField.attr("id")+"[" + trIndex + "]");

      if (hideFormFields) {
        $clonedField.addClass("hidden");
      }

      $this.append($clonedField)

      if ($this.find("input.datePicker").length) {
        $this.find("input.datePicker").datepicker($.extend({}, orx.plugin.datepicker, {
          'yearRange' : '-80:+0'
        }));
        if (!hideFormFields) {
          var calImg = $("<i/>",{ "class":"icon-calendar iconForInput ui-datepicker-trigger"});
          $this.append(calImg);
        }
      }
    });
  }


  /** Format from 1 234 to 1234 */
  orxapi.removeSpacesFromNumber = function(x) {
    return x.replace(/\s/g, "");
  }

  /** Format from 1234 to 1 234 */
  orxapi.getNumberWithSpaces = function(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
  }


  /*--------------------------------------------------------------------------------*/
  /*                  Other functions (move them to orxapi)                         */
  /*--------------------------------------------------------------------------------*/

  /** Resize width all mailer  */
  $.fn.resizeLeftZone = function() {
    var w_innerRight = $("#pge_file").find(".wrapLeft .inner").outerWidth(),
        h_window = windowHeight = $(window).height(),
        h_mailer = $(window).height() - $("#headerBarZone").height() - $("#tabsBarZone").height() - $("#headerPage").height();

    $(this).css({ "min-height" : h_mailer });
  };


  /** Resize height content for scroll  */
  $.fn.resizeBodyContent = function() {
    var windowHeight = $(window).height(),
        contentTabHeight = $(window).height() - $("#headerBarZone").height() - orxapi.selectors.tabsBarZone.height();
    $(this).css("height", contentTabHeight);
  };
  $(window).resize(function(){
    orxapi.selectors.bodyContent.resizeBodyContent();
    orxapi.resizeHeigthSendMail();
  });
  orxapi.selectors.bodyContent.resizeBodyContent();

  orxapi.resizeHeigthSendMail = function () {
    $("#bodyMail_ifr").css({"height": $(".replyZone").height() - 340 });
  };

  /* Close tab : ff behaviour */
  $.fn.closeTab = function() {
    var $this = $(this);

    if ($this.hasClass("notCloseable")) {
      // this tab is not closeable
      return false;
    }

    $this.removeClass("active");

    var $nextTab = "";
    var $newTab = true;
    if ($(".tabsNav").find("li.tab.active").length) {
      // closed tab was not active. Active one stays active.
      $nextTab = $(".tabsNav").find("li.tab.active");
      $newTab = false;
    } else if ($this.next("li.tab").length) {
      // Active tab was closed. next one that exists is new active.
      $nextTab = $this.next("li.tab")
    } else if ($this.prev("li.tab").length) {
      // Active tab was closed and there is no next one. Prev one is new active.
      $nextTab = $this.prev("li.tab");
    }

    if ($nextTab == "") {
        orxapi.selectors.bodyContent.removeClass().addClass("empty");
    } else {
        // If we remain in the same tab, we do not need to reload the tab
        if ($newTab) {
          var pge = $this.data("page");
          orxapi.selectors.bodyContent.find("#" + pge).remove();
          $nextTab.find("span:eq(0)").trigger('click');
        }
    }

    $(this).remove();

    orxapi.storeTabCookie();
  };



  /** Made popin  */
  orxapi.madePopin = function (pge, params) {

    var $that = $(this),
        page = pge +".action",
        wrapPopin = $("<div >", { id:"wrapPopin" }),
        formId = pge + "Form",
        module = orx.selectors.body.data("module");
        shared = $that.data("shared"),
        script = "popin/" + pge;

    $("#wrapPopin").remove();
    $.ajax($.extend({}, orxapi.ajax, {
      url : page,
      data: params,
      success: function(data, textStatus, jqXHR) {
        wrapPopin.appendTo("body");
        $(data).appendTo(wrapPopin);
        orxapi.loadJs(module, script, shared)

        /* data width and height */
        var popin = wrapPopin.find(".popin"),
            widthPopin = popin.data("popin").width,
            heightPopin = popin.data("popin").height;

        wrapPopin.css({"padding-top": "90px" });
        wrapPopin.animate({"padding-top": "-=90px" }, 150, "easeOutCubic");
        orxapi.resizePopin();

        if (widthPopin) { popin.css("width", widthPopin); }
        if (heightPopin) { popin.css("height", heightPopin); }
      },
      error: function() {
          var contentDialog = $("<div/>",{ "id":"contentDialog"});
          contentDialog.html($("#genericErrorMsg").text());
          contentDialog.dialog({
                title : 'Erreur',
                modal : true,
                zIndex: 8888,
                resizable: false,
                close: function(event, ui) {
                  $(this).dialog("destroy").remove();
                },
                buttons:{"OK": function() {
                $(this).dialog("close");}}
          });
      }
    }));
  };

  /**
   * Constructs a popin to send a mail.
   */
  orxapi.constructMailPopin = function (pge, params) {

    var $that = $(this),
        page = pge +".action",
        wrapPopin = $("<div >", { id:"wrapPopin" }),
        formId = pge + "Form",
        module = orx.selectors.body.data("module");
        shared = $that.data("shared"),
        script = "popin/mailer";

    $("#wrapPopin").remove();
    $.ajax($.extend({}, orxapi.ajax, {
      url : page,
      data: params,
      success: function(data, textStatus, jqXHR) {
        wrapPopin.appendTo("body");
        $(data).appendTo(wrapPopin);
        orxapi.loadJs(module, script, shared)

        /* data width and height */
        var popin = wrapPopin.find(".popin"),
            widthPopin = popin.data("popin").width,
            heightPopin = popin.data("popin").height;

        wrapPopin.css({"padding-top": "90px" });
        wrapPopin.animate({"padding-top": "-=90px" }, 150, "easeOutCubic");
        orxapi.resizePopin();

        if (widthPopin) { popin.css("width", widthPopin); }
        if (heightPopin) { popin.css("height", heightPopin); }

        var mailer = $("#innerWrapContent").find(".mailerZone");
        mailer.find(".replyZone").remove();
        mailer.append(data);
        mailer.find(".replyZone").css({ "bottom": "-20%" });
        mailer.find(".replyZone").removeClass("hidden").animate({bottom: "+=20%" }, 250, "easeOutCubic").addClass("open");
        /* add light wysiwyg on #bodyMail */
        tinyMCE.init($.extend({
          elements : "bodyMail", resizable:true,
          theme_advanced_resizing : true,
          theme_advanced_resize_horizontal : false
        }, orxapi.plugin.wysiwygLight));
      },
      error: function() {
          var contentDialog = $("<div/>",{ "id":"contentDialog"});
          contentDialog.html($("#genericErrorMsg").text());
          contentDialog.dialog({
                title : 'Erreur',
                modal : true,
                zIndex: 8888,
                resizable: false,
                close: function(event, ui) {
                  $(this).dialog("destroy").remove();
                },
                buttons:{"OK": function() {
                $(this).dialog("close");}}
          });
      }
    }));
  };

})(jQuery, orxapi);
