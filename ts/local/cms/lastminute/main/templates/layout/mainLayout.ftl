<!DOCTYPE html>
<html lang="fr">
  <#assign currentPage = context.lookup("currentPage")!'' />
  <#assign brandData = context.lookup("brandData")!''/>
  <@tag_cms_renderContainer code="headContainer" />

  <#if brandData.getBrandName() != 'lastminute'>
    <@tag_cms_renderContainer code="googleAnalyticsContainer" />
  </#if>

  <#if currentPage = 'compare'>
    <#assign cP = 'comparison' />
  <#else>
    <#assign cP = currentPage />
  </#if>
  <body class="${cP}">
    <div class="overlay-lm hidden"></div>
    <#if brandData.getBrandName() = 'lastminute'>
      <@tag_cms_renderContainer code="tagManTopBody" />
    </#if>

    <#if cP = 'product'>
      <#assign myEnvironment = context.lookup("myEnvironment") />
      <#if myEnvironment.getAttribute("showdesc") != 'true'>
        <div id="master" class="wrap-master main_active">
          <div class="wrapper">
            <@tag_cms_renderContainer code="headerContainer" />
            <@tag_cms_renderContainer code="navigationContainer" />
          </div>
        </div>
      </#if>
      <@tag_cms_renderContainer code="mainContainer" />
      <@tag_cms_renderContainer code="dealDayContainer" />
      <#if myEnvironment.getAttribute("showdesc") != 'true'>
        <@tag_cms_renderContainer code="footerContainer" />
      </#if>
    <#elseif cP = 'serp'>
      <div id="master" class="wrap-master main_active">
        <@tag_cms_renderContainer code="headerContainer" />
        <@tag_cms_renderContainer code="mainContainer" />
        <@tag_cms_renderContainer code="dealDayContainer" />
        <@tag_cms_renderContainer code="footerContainer" />
      </div>
    <#elseif cP = 'comparison'>
      <@tag_cms_renderContainer code="headerContainer" />

      <@tag_cms_renderContainer code="mainContainer" />

      <@tag_cms_renderContainer code="dealDayContainer" />
      <@tag_cms_renderContainer code="footerContainer" />
    <#elseif cP = 'errorPage'>
      <@tag_cms_renderContainer code="headerContainer" />
      <@tag_cms_renderContainer code="navigationContainer" />
      <@tag_cms_renderContainer code="mainContainer" />
      <@tag_cms_renderContainer code="footerContainer" />
    </#if>

    <@tag_cms_renderContainer code="javascriptContainer" />
    <@tag_cms_renderContainer code="trackingContainer" />

    <#--
    <#if brandData.getBrandName() != 'lastminute'>
      <@tag_cms_renderContainer code="googleAnalyticsContainer" />
    </#if>
    <#if brandData.getBrandName() = 'CDISCOUNT'>
      <@tag_cms_renderContainer code="cdiscountTagsContainer" />
    </#if>
    <#if brandData.getBrandName() = 'CITROEN'>
      <@tag_cms_renderContainer code="citroenTagsContainer" />
    </#if>
    -->

    <#--
    <div class="asideButtons" role="complementary">
      <div class="socialNetworks grad1" role="banner">
        <ul>
          <li>
            <a href="https://www.facebook.com/lastminute.comFrance" target="_blank" rel="nofollow"> <span id="test-icon-socialMedia-facebook" class="icon icon-socialMedia30 icon-socialMedia30-facebook"></span> </a>
          </li>
          <li>
            <a href="https://twitter.com/lastminute_fr" target="_blank" rel="nofollow"> <span id="test-icon-socialMedia-twitter" class="icon icon-socialMedia30 icon-socialMedia30-twitter"></span> </a>
          </li>
          <li>
            <a href="https://plus.google.com/+lastminutecomFrance/posts" target="_blank" rel="nofollow"> <span id="test-icon-socialMedia-googlePlus" class="icon icon-socialMedia30 icon-socialMedia30-googleplus"></span> </a>
          </li>
          <li>
            <a href="http://pinterest.com/lastminutecomfr/" target="_blank" rel="nofollow"> <span id="test-icon-socialMedia-pinterest" class="icon icon-socialMedia30 icon-socialMedia30-pinterest"></span> </a>
          </li>
          <li>
            <a href="http://le-blog.lastminute.com" target="_blank" rel="nofollow"> <span id="test-icon-socialMedia-lmnBlog" class="icon icon-socialMedia30 icon-socialMedia30-lmnblog"></span> </a>
          </li>
        </ul>
      </div>
    </div>
    -->
  </body>
</html>
