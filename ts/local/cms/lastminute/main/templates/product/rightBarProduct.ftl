<#assign publishedProduct = context.lookup("publishedProduct") />
<#assign brandData = context.lookup("brandData")!'' />
<#assign mediaTag = context.lookup("mediaTagStats") />
<#assign myEnvironment = context.lookup("myEnvironment") />
<#import "/lib/directives.ftl" as resource>

<#if myEnvironment.getAttribute("showdesc") != 'true'>
  <!-- banner -->
  <div class="advertising-right" style="right: -170px">

  <#--
    <#if brandData.brandName != 'SELECTOUR'>
      <@resource.retrieveResource canonicalAddress="images" fileName="sidebar-image1" fileExtension="gif">
          <img width="159" height="188" alt="" src="<@resource.retrieveResource canonicalAddress="images" fileName="sidebar-image1" fileExtension="gif"/>" />
      </@resource.retrieveResource>
    </#if>
  -->

     <script language="JavaScript">
        <!--
          document.write('<scr'+ 'ipt language="JavaScript" src="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&section=defaultsection<#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>&city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>&country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>&tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>&stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}&locale=fr&random=${mediaTag.random13}?"></scr'+'ipt>');
        -->
     </script>
     <noscript>
      <a href="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&section=defaultsection
        <#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>
        &city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>
        &country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>
        &tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>
        &stars=${mediaTag.starsNmb}</#if>
        &tile=${mediaTag.random15}
        &locale=fr&random=${mediaTag.random13}?">
        <img alt="" src="http://dm.travelocity.com/js.ng/site=lastminfr&adsize=120x600&cat=travel&area=holidays&section=defaultsection
        <#if (mediaTag.getTripTypeLabel())?has_content>&holidaytype=${mediaTag.tripTypeLabel}</#if>
        &city=<#if (mediaTag.getDestCityLabel())?has_content>${mediaTag.destCityLabel}</#if>
        &country=<#if (mediaTag.getDestCountryCode())?has_content>${mediaTag.destCountryCode}</#if>
        &tm=<#if (mediaTag.getDepMonth())?has_content>${mediaTag.depMonth}</#if><#if (mediaTag.getStarsNmb())?has_content>
        &stars=${mediaTag.starsNmb}</#if>&tile=${mediaTag.random15}
        &locale=fr&random=${mediaTag.random13}?" />
      </a>
    </noscript>

    <div class="advertising-right-bottom">
      <@tag_cms_lookup name="wheatherImage"; wheatherImage>
        <img  src="${wheatherImage}" />
      </@tag_cms_lookup>
    </div>
  </div>
</#if>
