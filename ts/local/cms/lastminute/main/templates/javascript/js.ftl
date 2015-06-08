<#assign currentPage = context.lookup("currentPage")!'' />

<#import "/lib/directives.ftl" as resource>

<script src="/shared-cs/lastminute-catalog/js/lib/jquery.min.js"></script>
<script src="/shared-cs/lastminute-catalog/js/lib/jquery.ui.min.js"></script>
<script src="/shared-cs/lastminute-catalog/js/lib/jquery.cookie.js"></script>
<script src="/shared-cs/lastminute-catalog/js/lib/jquery.custom.select.min.js"></script>
<script src="/shared-cs/lastminute-catalog/js/lib/matchmedia.polyfill.js"></script>
<script src="/shared-cs/lastminute-catalog/js/lib/jquery.breakpoint.min.js"></script>
<script src="/shared-cs/lastminute-catalog/js/cookie.js"></script>
<script src="/shared-cs/lastminute-catalog/js/common.js"></script>
<script src="/shared-cs/lastminute-catalog/js/csa-FR-sejour.js"></script>

<#if currentPage = 'product' || currentPage = 'productEmail'>
  <script src="/shared-cs/lastminute-catalog/js/lib/owl.carousel.min.js"></script>

  <script src="/shared-cs/lastminute-catalog/js/productContent.js"></script>
  <script src="/shared-cs/lastminute-catalog/js/resaform.js"></script>
  <script src="/shared-cs/lastminute-catalog/js/product.js"></script>

  <script src="/shared-cs/lastminute-catalog/js/customerReviewRates.js"></script>
  <script src="/shared-cs/lastminute-catalog/js/customerReviewFilter.js"></script>

<#elseif currentPage = 'serp'>
  <script src="/shared-cs/lastminute-catalog/js/searchEngine.js"></script>
  <script src="/shared-cs/lastminute-catalog/js/compare.js"></script>

<#elseif currentPage = 'compare'>
  <script src="/shared-cs/lastminute-catalog/js/lib/jquery.orx.utils.size.js"></script>
  <script src="/shared-cs/lastminute-catalog/js/comparator.js"></script>
</#if>

<script src="http://www.google.com/adsense/search/ads.js" type="text/javascript"></script>
