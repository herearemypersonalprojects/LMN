<@tag_cms_lookup name="brandData"; brandData>
<#assign b2bctx = context.lookup("displayb2bBlock")!false />
<div id="header">
      <#import "/lib/includeStaticFileDirective.ftl" as engine>
      <#if b2bctx && brandData.brandName = 'lastminute'>
        <@engine.includeStaticFile fileName="/generated/local/${brandData.brandName}/header_b2b.html";insertedFile>
          ${insertedFile}
        </@engine.includeStaticFile>
      <#else>
        <@engine.includeStaticFile fileName="/generated/local/${brandData.brandName}/header.html";insertedFile>
          ${insertedFile}
        </@engine.includeStaticFile>
     </#if>
</div>
</@tag_cms_lookup>
