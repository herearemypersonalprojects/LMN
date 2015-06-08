<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<div class="sep"> </div>
<#if productDisplayable.ratingImageUrl?has_content && productDisplayable.rating?has_content && productDisplayable.num_reviews?has_content>
<@tag_cms_lookup name="reviews"; reviews>
  <div id="details-customerReviews" class="details-customerReviews">
    <h2>avis des voyageurs</h2>
  <div class="options">
        <div class="title">note moyenne</div>
        <div class="contents">
          <#if reviews.ratingImageUrl?has_content>
            <div class="average-rating">
              <div class="title-average-rating">Note moyenne de l'h&ocirc;tel</div>
              <div class="img-block">
                <img alt="" src="${reviews.ratingImageUrl}">
              </div>
              <p>bas&eacute;e sur <strong>${reviews.num_reviews}</strong> avis clients</p>
            </div>
          </#if>
          <#if reviews.awards?has_content &&  (reviews.awards.getAwardCount()>0) >
            <div class="travellers-choice">
              <img alt="" src="${reviews.awards.getAward(0).images.large}">
            </div>
          </#if>
          <#if reviews.reviewRatingCount?has_content>
            <div class="score">
              <div class="title-score">Note attribu&eacute;e :</div>
              <#assign
                total = reviews.reviewRatingCount.excellent +
                        reviews.reviewRatingCount.very_good +
                        reviews.reviewRatingCount.average +
                        reviews.reviewRatingCount.poor +
                        reviews.reviewRatingCount.terrible
                excellent = reviews.reviewRatingCount.excellent / total
                very_good = reviews.reviewRatingCount.very_good / total
                average = reviews.reviewRatingCount.average / total
                poor = reviews.reviewRatingCount.poor / total
                terrible = reviews.reviewRatingCount.terrible / total
              />
              <ul>
                <li><strong>Excellent</strong>
                  <img title="" src="http://back-lastminute.orchestra-platform.com/admin/TS/fckUserFiles/Image/tripAdvisor-3.png">
                  <span>${reviews.reviewRatingCount.excellent}</span>
                </li>
                <li><strong>Tr&egrave;s bon</strong>
                  <img title="" src="http://back-lastminute.orchestra-platform.com/admin/TS/fckUserFiles/Image/tripAdvisor-3.png">
                  <span>${reviews.reviewRatingCount.very_good}</span>
                </li>
                <li><strong>Moyen</strong>
                  <img title="" src="http://back-lastminute.orchestra-platform.com/admin/TS/fckUserFiles/Image/tripAdvisor-4.png">
                  <span>${reviews.reviewRatingCount.average}</span>
                </li>
                <li><strong>M&eacute;diocre</strong>
                  <img title="" src="http://back-lastminute.orchestra-platform.com/admin/TS/fckUserFiles/Image/tripAdvisor-5.png">
                  <span>${reviews.reviewRatingCount.poor}</span>
                </li>
                <li><strong>Horrible</strong>
                  <img title="" src="http://back-lastminute.orchestra-platform.com/admin/TS/fckUserFiles/Image/tripAdvisor-5.png">
                  <span>${reviews.reviewRatingCount.terrible}</span>
                </li>
              </ul>
            </div>
          </#if>
          <#if reviews.subratings?has_content>
            <div class="details">
              <div class="title-details">D&eacute;tail :</div>
              <#assign
                emplacement = "http://c1.tacdn.com/img2/ratings/traveler/s" + reviews.subratings.location + ".gif"
                service = "http://c1.tacdn.com/img2/ratings/traveler/s" + reviews.subratings.service + ".gif"
                cleanliness = "http://c1.tacdn.com/img2/ratings/traveler/s" + reviews.subratings.cleanliness + ".gif"
                sleep_quality = "http://c1.tacdn.com/img2/ratings/traveler/s" + reviews.subratings.sleep_quality + ".gif"
                rooms = "http://c1.tacdn.com/img2/ratings/traveler/s" + reviews.subratings.rooms + ".gif"
                value = "http://c1.tacdn.com/img2/ratings/traveler/s" + reviews.subratings.value + ".gif"
              />

              <ul>
                <#if reviews.subratings.location?has_content>
                  <li>
                    <img title="" src="${emplacement}">
                    <span>Emplacement</span>
                  </li>
                </#if>
                <#if reviews.subratings.service?has_content>
                  <li>
                    <img title="" src="${service}">
                    <span>Service</span>
                  </li>
                </#if>
                <#if reviews.subratings.cleanliness?has_content>
                  <li>
                    <img title="" src="${cleanliness}">
                    <span>Propret&eacute;</span>
                  </li>
                </#if>
                <#if reviews.subratings.sleep_quality?has_content>
                  <li>
                    <img title="" src="${sleep_quality}">
                    <span>Literie</span>
                  </li>
                </#if>
                <#if reviews.subratings.rooms?has_content>
                  <li>
                    <img title="" src="${rooms}">
                    <span>Chambres</span>
                  </li>
                </#if>
                <#if reviews.subratings.value?has_content>
                  <li>
                    <img title="" src="${value}">
                    <span>Rapport qualit&eacute; / prix</span>
                  </li>
                </#if>
              </ul>
            </div>
          </#if>
        </div>
      </div>
  </div>

  <!-- REVIEWS DETAIL  --->
  <#if reviews.data?exists>
  <div id="tripAdvisorReviewsSection" class="options noindexable">
  </div>
  </#if>
</@tag_cms_lookup>

<@tag_cms_renderContainer code="customerReviewOptionsContainer" />

</#if>