<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
  publishedProduct = context.lookup("publishedProduct")!publishedProduct
  brandData = context.lookup("brandData")!''
/>

<div class="contain-product clearfix">
  <@tag_cms_renderContainer code="productResumeContainer" />
  <@tag_cms_renderContainer code="productDiapoContainer" />
  <div class="clearBlocks"> </div>
  <div class="help-phone tablet-mini-visible">
    <span> </span>
    <p>
      besoin d&rsquo;aide pour r&eacute;server ?
      <br>appelez le
      <strong>
        <#if brandData.brandName == 'CGOS'> 0892 707 200 <#else> 0 892 68 61 00 </#if>
      </strong>
      <em>(0.34 &euro; TTC / min)</em>
      <br>r&eacute;f&eacute;rence produit : <strong>${productDisplayable.id}</strong>
    </p>
  </div>

  <#if productDisplayable.ratingImageUrl?has_content && productDisplayable.rating?has_content && productDisplayable.num_reviews?has_content>
    <div class="customer-reviews-minitab">
    <div class="title">Avis clients :</div>
    <div class="advisor-block">
      <div class="img-block">
        <a href="#details-customerReviews"><img alt="" src="${productDisplayable.ratingImageUrl}"></a>
      </div>
      <div class="mention-rating">
        <#assign ratingNum = productDisplayable.rating?number>
        <#if (ratingNum > 4)>
          Excellent,
        <#elseif (ratingNum > 3)>
          Tr&egrave;s bien,
        <#elseif (ratingNum > 2)>
          Moyen,
        <#elseif (ratingNum > 1)>
          M&eacute;diocre,
        <#elseif (ratingNum >= 0)>
          Horrible,
        </#if>
        ${productDisplayable.rating}
      </div>
      <p>Bas&eacute; sur <strong>${productDisplayable.num_reviews}</strong> &eacute;valuations des clients</p>
    </div>
    <a class="link-blue" href="#details-customerReviews">afficher les &eacute;valuations</a>
  </div>
  </#if>

  <div class="colBottom">
    <ul>
      <li><strong>Voir :</strong></li>
      <li><a href="#book-stay" class="link-blue">r&eacute;servations</a></li>
      <li>|</li>
      <li><a href="#details-stay" class="link-blue">d&eacute;tails</a></li>
      <li>|</li>
      <li><a href="#conditions-tarifaires" class="link-blue">conditions tarifaires</a></li>
      <li>|</li>
      <li><a href="#informations-voyageurs" class="link-blue">informations voyageurs</a></li>
      <#--
      <#import "/lib/includeStaticFileDirective.ftl" as engine>
      <@engine.includeStaticFile fileName="/shared/cs/web/lastminute-catalog/reviews/${productDisplayable.reference?lower_case}/reviewSummary.html";insertedFile>
        <#if insertedFile != ''>
          <li>|</li>
          <li><a href="#details-customerReviews" class="link-blue">avis clients</a></li>
        </#if>
      </@engine.includeStaticFile>
      -->

      <#if productDisplayable.ratingImageUrl?has_content && productDisplayable.rating?has_content && productDisplayable.num_reviews?has_content>
        <li>|</li>
        <li><a href="#details-customerReviews" class="link-blue">avis clients</a></li>
      </#if>
    </ul>
  </div>
  <@tag_cms_lookup name="displayb2bBlock"; displayb2bBlock>
    <div class="blk-btn-product">
      <a href="/sendProductEmail.cms?pid=${publishedProduct.code}&to=${productDisplayable.toLabel}&pName=${productDisplayable.title}" id="openEmailPopup" class="link-send">envoyer</a>
    </div>
  </@tag_cms_lookup>
</div>