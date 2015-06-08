<#assign currentPage = context.lookup("currentPage")!''/>
<#assign brandData = context.lookup("brandData")!''/>
<#assign mediaTag = context.lookup("mediaTagStats") />
<div class="advertising-right">

    <script language="JavaScript">
      document.write('<scr'+ 'ipt language="JavaScript" src="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&pagepos=1&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?"></scr'+'ipt>');
    </script>

    <a href="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&pagepos=1&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?"
    target="_blank">
    <img src="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&pagepos=1&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?"
    alt=""></a>
    <div class="advertising-right-bottom">
        <a target='_blank' href='https://support.google.com/adsense/bin/request.py?hl=fr&contact_type=afs_violation&rd=2'>Annonces Google</a>
        <div id="adcontainer2"></div>
    </div>
</div>
<@tag_cms_renderContainer code="productWeatherContainer" />