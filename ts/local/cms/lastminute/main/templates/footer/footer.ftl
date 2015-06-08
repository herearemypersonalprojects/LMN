<@tag_cms_renderContainer code="middAdsContainer" />
<br>
<!-- ******** BEGIN FOOTER ************************************** -->
<@tag_cms_lookup name="brandData"; brandData>
  <#assign currentPage = context.lookup("currentPage")!''/>
  <#if brandData.brandName == 'lastminute'>
    <#if currentPage = 'serp'>
      <div id="googleAdsBottom" class="google-ads-wrap">
        <div class="google-ads-head"><a href='https://support.google.com/adsense/bin/request.py?hl=fr&contact_type=afs_violation&rd=2' target='_blank'>Annonces Google</a></div>
        <div id="adcontainer1" class="google-ads-content"></div>
      </div>
    </#if>
  </#if>

  <#assign b2bctx = context.lookup("displayb2bBlock")!false />
    <div id="footer">
      <#import "/lib/includeStaticFileDirective.ftl" as engine>
      <#if b2bctx && brandData.brandName = 'lastminute'>
          <@engine.includeStaticFile fileName="/generated/local/${brandData.brandName}/footer_b2b.html";insertedFile>
            ${insertedFile}
          </@engine.includeStaticFile>
        <#else>
          <@engine.includeStaticFile fileName="/generated/local/${brandData.brandName}/footer.html";insertedFile>
            ${insertedFile}
          </@engine.includeStaticFile>
       </#if>
    </div>
</@tag_cms_lookup>
<!-- END FOOTER -->