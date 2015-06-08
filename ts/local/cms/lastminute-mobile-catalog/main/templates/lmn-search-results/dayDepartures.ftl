<#import "/directives.ftl" as util>
<@tag_cms_lookup name="dayDepartures"; dayDepartures>
  <span class="lastminute-date">D&eacute;part ${dayDepartures.departureDate?string("EEE dd MMMM yyyy")}</span>
  <ul class="news-list">
    <@tag_cms_renderContainer code="departureContainer" />
  </ul>
</@tag_cms_lookup>