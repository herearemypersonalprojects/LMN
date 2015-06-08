<#if review??>
<#assign rating = review.getRating() />
<#if review.getRating()?length = 1>
  <#assign rating = review.getRating()+".0" />
</#if>
<#assign crawable = " nocrawable" />
<#if 0<=review.getCrawable()?index_of("true")>
  <#assign crawable = " crawable" />
</#if>
  <div class='block-customer-reviews'>
    <div class='item ${crawable}'>
      <div class='comments-title'>${review.getTitle()}</div>
      <p>${review.getText()}</p>
      <img src='http://c1.tacdn.com/img2/ratings/traveler/s${rating}.gif' title=''>
      <div class='date'>${review.getPublishedDate()} ${review.getLocation()}</div>
    </div>
  </div>
</#if>