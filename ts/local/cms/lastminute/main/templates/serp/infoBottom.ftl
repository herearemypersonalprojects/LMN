<@tag_cms_lookup name="seoLinks">
  <@tag_cms_lookup name="brandData"; brandData>
    <#if brandData.brandName == 'lastminute'>
      <div class="info-area">
        <strong>vous aimerez aussi</strong>
        <ul>
          <#list seoLinks.getCodeLabel() as seoLink>
            <li><a href="${seoLink.getCode()}">${seoLink.getLabel()}</a></li>
          </#list>
        </ul>
      </div>
    </#if>
   </@tag_cms_lookup>
</@tag_cms_lookup>
