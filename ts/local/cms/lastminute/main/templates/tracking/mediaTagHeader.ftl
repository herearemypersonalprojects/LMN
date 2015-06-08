<!-- START: Leadboard -->
<div id="leaderBoard">
  <div class="leaderBoardContent">
    <#assign currentPage = context.lookup("currentPage")!''/>
    <#assign brandData = context.lookup("brandData")!''/>
    <#if currentPage = 'serp' || currentPage = 'product' || currentPage = 'compare'>
      <#assign mediaTag = context.lookup("mediaTagStats") />
      <script language="JavaScript">
      <!--
      document.write('<scr'+ 'ipt language="JavaScript" src="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=728x90&cat=travel&area=holidays&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?"></scr'+'ipt>');
      -->
      </script>
      <noscript>
      <a
      href="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=728x90&cat=travel&area=holidays&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?"><img src="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=728x90&cat=travel&area=holidays&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?" border="0" width=728 height=90 /></a>
      </noscript>
    </#if>
  </div>
</div>
<!-- END: Leadboard -->

  <#if currentPage = 'serp'>
    <span class="bannerRight">
    <script language="JavaScript">
    <!--
    document.write('<scr'+ 'ipt language="JavaScript" src="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&pagepos=1&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?"></scr'+'ipt>');
    -->
    </script>
    <noscript>
    <a
    href="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&pagepos=1&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?"><img src="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&pagepos=1&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?" border="0" width=120 height=600 /></a>
    </noscript>
  </span>
  <#if brandData.brandName == 'lastminute'>
    <div id="googleAds">
      <div class="googleAdsHead"><a target='_blank' href='https://support.google.com/adsense/bin/request.py?hl=fr&contact_type=afs_violation&rd=2'>Annonces Google</a></div>
      <div id="adcontainer2"></div>
      </div>
    </#if>
  </#if>