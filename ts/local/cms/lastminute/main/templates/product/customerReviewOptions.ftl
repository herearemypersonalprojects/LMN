<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
  publishedProduct = context.lookup("publishedProduct")!publishedProduct
/>

<#import "/lib/includeStaticFileDirective.ftl" as engine>
<@engine.includeStaticFile fileName="/shared/cs/web/lastminute-catalog/reviews/${productDisplayable.id?lower_case}/crawableReviews.json";existedFile>
<#if existedFile != ''>
  <div id="details-customerReviews" class="details-customerReviews">
    <div class="options">
      <div class="title">voir plus d'avis clients</div>

          <#--<@tag_cms_renderContainer code="customerReviewTrierContainer" />
          <@tag_cms_renderContainer code="customerReviewPaginationContainer" /> -->
          <div id="customerReviews" class="contents">
            <@tag_cms_renderContainer code="customerReviewItemsContainer" />
            <@tag_cms_lookup name="crawableList"; crawableList>
              <#if crawableList?has_content>
                <#import "/lib/includeStaticFileDirective.ftl" as engine>
                <#list crawableList?split(":") as fileName>
                  <@engine.includeStaticFile fileName="/shared/cs/web/lastminute-catalog/reviews/${productDisplayable.id?lower_case}/"+fileName+".html";existedFile>
                    <#if existedFile != ''>
                      ${existedFile}
                    </#if>
                  </@engine.includeStaticFile>
                </#list>
              </#if>
            </@tag_cms_lookup>

          <@tag_cms_renderContainer code="customerReviewPaginationContainer" />
          <#--
          <div class="content-up-page">
              <a href="#header">Haut de page<span></span></a>
          </div>
          -->
      </div>
    </div>
  </div>
</#if>
</@engine.includeStaticFile>