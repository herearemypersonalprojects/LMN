<@tag_cms_lookup name="displayDPBlock">
  <#assign displayDPBlock = context.lookup("displayDPBlock")!'true'/>
  <#if displayDPBlock = 'true'>
    <@tag_cms_lookup name="dpContent"; dpContent>
    <#assign dpLink = context.lookup("dpLink")!'#'
      brandData = context.lookup("brandData")!brandData />
      <#if brandData.getBrandName() = 'lastminute'>
        <div class="info-box">
          <a class="btn-green" href="${dpLink}" target="_blank"><strong><span class="arrow">vol + h&ocirc;tel</span></strong></a>
          <img src="/shared-cs/lastminute-catalog/images/train_flight_box.png" />${dpContent}
        </div>
      </#if>
    </@tag_cms_lookup>
   </#if>
</@tag_cms_lookup>
