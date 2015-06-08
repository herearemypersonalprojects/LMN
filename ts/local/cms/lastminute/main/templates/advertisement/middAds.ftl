<#assign currentPage = context.lookup("currentPage")!'' />

<#if currentPage = 'product'>
  <script language="JavaScript">
  var randNum = new Date().getTime();
  var randTile= Math.ceil(1000000*Math.random());
  </script>
  <#assign myEnvironment = context.lookup("myEnvironment") />
  <#if myEnvironment.getAttribute("showdesc") != 'true'>
    <div id="bannerProduct" class="advertising-item-bottom">
      <script language="JavaScript">
      //script pour bottom 728x90
      var my_adlbdparams = 'site=lastminfr&adsize=728x90&cat=defaultcat&pagepos=2&area=holidays&section=defaultsection&city=&country=&tm=&locale=fr&random=';
      <!--
      document.write('<scr'+'ipt language="JavaScript" type="text/javascript" src="http://dm.travelocity.com/js.ng/'+my_adlbdparams+randNum+'&tile='+randTile+'?"></scr'+'ipt>');
      //-->
      </script>
    </div>
  </#if>
<#else>
  <script language="JavaScript">
  var randNum = new Date().getTime();
  var randTile= Math.ceil(1000000*Math.random());
  </script>

  <div id="banner" class="advertising-item-midd">

    <script language="JavaScript">
    //script pour bottom 728x90
    var my_adlbdparams = 'site=lastminfr&adsize=728x90&cat=defaultcat&pagepos=2&area=holidays&section=defaultsection&city=&country=&tm=&locale=fr&random=';
    <!--
    document.write('<scr'+'ipt language="JavaScript" type="text/javascript" src="http://dm.travelocity.com/js.ng/'+my_adlbdparams+randNum+'&tile='+randTile+'?"></scr'+'ipt>');
    //-->
    </script>
  </div>


  <#--
  <div id="banner" class="advertising-item-midd">
  <div class="title">Publicit&eacute;</div>
      <div id="closeAds" class="btn adsButton">
            <span></span>
            fermer
      </div>
    <script language="JavaScript">
    //script pour bottom 728x90
    var my_adlbdparams = 'site=lastminfr&adsize=728x90&cat=defaultcat&pagepos=2&area=holidays&section=defaultsection&city=&country=&tm=&locale=fr&random=';
    -->
    <!--
    document.write('<scr'+'ipt language="JavaScript" type="text/javascript" src="http://dm.travelocity.com/js.ng/'+my_adlbdparams+randNum+'&tile='+randTile+'?"></scr'+'ipt>');
    //-->
    <#--
    </script>
  </div>


  <div id="replayAds" class="advertising-item-midd hidden" style="height: 24px;">
    <div class="title">Publicit&eacute;</div>
    <div id="" class="btn adsButton">
        <span></span>
        rejouer
    </div>
  </div>
  -->
</#if>