<#if reviews??>
  <div id='tripAdvisorReviewsSection' class='options'>
    <div class='title'>commentaires de voyageurs</div>
      <div class='contents'>
          <#assign req = reviews.getData().getReview() />
          <#list req as item>
            <div class='item'>
              <div class='comments-title'> ${item.title} </div>
              <p>${item.text}</p>
              <img title='' src='${item.ratingImageUrl}'>
              <div class='date'>${item.published_date} ${item.getUser().getUser_location().getName()}</div>
            </div>
          </#list>
          <div class='link-block'>
            <a class='link-blue' href='${reviews.getWrite_review()}' target='_blank'>&eacute;crire un avis</a>
          </div>
      </div>
  </div>
</#if>