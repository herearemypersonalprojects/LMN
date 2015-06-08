<#assign
  backGroundPicture = context.lookup("backgroundPicture")!''
  backGroundUrl = context.lookup("backGroundUrl")!''
  currentPage = context.lookup("currentPage")!''
  usePrintCss = context.lookup("usePrintCss")!'false'
  brandData = context.lookup("brandData")!brandData
/>

<#import "/lib/directives.ftl" as resource>

<head>
  <#if currentPage != 'productEmail' && brandData.getBrandName() = 'lastminute'>
    <@tag_cms_renderContainer code="tagManJsContainer" />
  </#if>

  <@tag_cms_renderContainer code="metaContainer" />

  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
  <meta content="width=device-width" name="viewport" />

  <#--<#if myEnvironment.getAttribute("showdesc") = 'true'>
    <#import "/lib/includeStaticFileDirective.ftl" as engine>
    <style type="text/css">
      <@resource.retrieveResource canonicalAddress="css" fileName="productPdf" fileExtension="css" type="physical";result>
        <@engine.includeStaticFile fileName="${result}";productPdfCss>${productPdfCss}</@engine.includeStaticFile>
      </@resource.retrieveResource>
      <@resource.retrieveResource canonicalAddress="css" fileName="print" fileExtension="css" type="physical";result>
        <@engine.includeStaticFile fileName="${result}";printCss>${printCss}</@engine.includeStaticFile>
      </@resource.retrieveResource>
      <#if myEnvironment.getAttribute("showdesc") = 'true'>
        .breadcrumb {display:none;}
        .economy {display:none;}
        .slide-product-wrap  {display:none;}
        .book-stay  {display:none;}
        .advertising-right   {display:none;}
        .summary-bookStay {display:none;}
        #calendar {display:none;}
        .details-youLike {display:none;}
        .advertising-item-bottom {display:none;}
        .details-customerReviews {display:none;}
      </#if>
    </style>

  <#else> -->
    <link media="all" type="text/css" rel="stylesheet" href=<@resource.retrieveResource canonicalAddress="css" fileName="ui" fileExtension="css"/>>
    <link media="all" type="text/css" rel="stylesheet" href=<@resource.retrieveResource canonicalAddress="css" fileName="fonts" fileExtension="css"/>>
    <link media="all" type="text/css" rel="stylesheet" href=<@resource.retrieveResource canonicalAddress="css" fileName="reset" fileExtension="css"/>>
    <link media="all" type="text/css" rel="stylesheet" href=<@resource.retrieveResource canonicalAddress="css" fileName="form" fileExtension="css"/>>
    <link media="all" type="text/css" rel="stylesheet" href=<@resource.retrieveResource canonicalAddress="css" fileName="common" fileExtension="css"/>>
    <link media="print" type="text/css" rel="stylesheet" href=<@resource.retrieveResource canonicalAddress="css" fileName="print" fileExtension="css"/>>

    <link href="http://cdn.travelpn.com.edgesuite.net/lmn/s/cap.prod.19.6593/sandwich.css" rel="stylesheet" type="text/css" />

    <#if currentPage = 'product'>
      <link media="all" type="text/css" rel="stylesheet" href=<@resource.retrieveResource canonicalAddress="css" fileName="owl.carousel" fileExtension="css"/>>
      <link media="all" type="text/css" rel="stylesheet" href=<@resource.retrieveResource canonicalAddress="css" fileName="owl.theme" fileExtension="css"/>>
    </#if>

    <!--[if IE 8]>
      <link media="all" type="text/css" rel="stylesheet" href=<@resource.retrieveResource canonicalAddress="css" fileName="ie8" fileExtension="css"/>>
    <![endif]-->
  <#--</#if>-->

  <#if currentPage != 'productEmail' && brandData.getBrandName() = 'lastminute'>
    <script type="text/javascript">
      TMAN.doTags(TMAN.position.BOTTOM_HEAD);
    </script>
  </#if>

  <#if brandData.getBrandName() = 'CDISCOUNT'>
    <script type='text/javascript'>
      var googletag = googletag || {};

      googletag.cmd = googletag.cmd || [];

      (function() {
        var gads = document.createElement('script');
        gads.async = true;
        gads.type = 'text/javascript';
        var useSSL = 'https:' == document.location.protocol;
        gads.src = (useSSL ? 'https:' : 'http:') + '//www.googletagservices.com/tag/js/gpt.js';
        var node = document.getElementsByTagName('script')[0];
        node.parentNode.insertBefore(gads, node);
      })();

      googletag.cmd.push(function(){
        googletag.defineSlot('/7190/CdiscountVoyages/autrespages', [160, 600], 'div-gpt-ad-2').addService(googletag.pubads()).setTargeting('pos', 'body');
        googletag.defineOutOfPageSlot('/7190/CdiscountVoyages/autrespages', 'div-gpt-ad-3').addService(googletag.pubads()).setTargeting('type', 'habillage');
        googletag.pubads().setTargeting('univ', 'booking_process_sejour_orchestra');
        googletag.pubads().enableSingleRequest();
        googletag.enableServices();
      });
    </script>
    <div id='div-gpt-ad-3'>
      <script type='text/javascript'>
        googletag.cmd.push(function() { googletag.display('div-gpt-ad-3'); });
      </script>
    </div>
  </#if>
<script src="http://www.google.com/jsapi"></script>
<script type="text/javascript" charset="utf-8">
 google.load('ads.search', '2');
</script>
</head>