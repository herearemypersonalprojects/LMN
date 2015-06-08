<#assign brandData = context.lookup("brandData")!''/>
<#if brandData.getBrandName() = 'lastminute'>
  <#assign mediaTag = context.lookup("mediaTagStats") />
  <#assign currentPage = context.lookup("currentPage")!'serp' />

  <#if currentPage = 'serp'>
    <span class="banner">
      <script language="JavaScript">
      <!--
      document.write('<scr'+ 'ipt language="JavaScript" src="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&pagepos=2&area=holidays&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?"></scr'+'ipt>');
      -->
      </script>
      <noscript>
         <a href="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&pagepos=2&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?"><img src="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&pagepos=2&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?" border="0" width=120 height=600 /></a>
       </noscript>
     </span>
  <#elseif currentPage = 'product'>
    <div class="ad2">
      <script language="JavaScript">
      <!--
      document.write('<scr'+ 'ipt language="JavaScript" src="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?"></scr'+'ipt>');
      -->
      </script>
      <noscript>
      <a
      href="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?"><img src="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?" border="0" width=120 height=600 /></a>
      </noscript>
  </div>
  </#if>
<#elseif brandData.getBrandName() = 'CDISCOUNT'>
  <div id='div-gpt-ad-2'>
    <script type='text/javascript'>
      googletag.cmd.push(function() { googletag.display('div-gpt-ad-2'); });
    </script>
  </div>
</#if>

