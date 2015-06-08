<#import "/directives.ftl" as util>
<#assign pageCode = context.lookup('pageCode')!'Undefined'>
<#assign devicePlatform = context.lookup('devicePlatform')!'unknown'>

<#assign productExists = context.lookup('product')??>
<#if productExists>
  <#assign product = context.lookup('product')>
</#if>

<head>
  <meta charset="utf-8">
  <meta name="HandheldFriendly" content="true">

  <#-- any edit in the platform specific content shoud be
       added in the cachemire post process script. -->
  <#if devicePlatform == "cachemire">
    <!-- INCLUDE specific mobile device tags -->

  <#else>
    <!-- app detected mobile device platform: '${devicePlatform}' -->
    <#if devicePlatform == "android">
      <meta name="viewport" content="width=640">
    <#elseif devicePlatform == "winphone">
      <meta name="MobileOptimized" content="width">
      <meta http-equiv="cleartype" content="on">
      <meta name="viewport" content="width=480">
    <#else>
      <#-- ios, webos, default/unknown settings -->
      <meta name="viewport" content="width=640, initial-scale=0.5, maximum-scale=0.5">
    </#if>
  </#if>

  <title><@tag_cms_writeMessage key="pages.${pageCode}.title" fileName="pages.properties" /></title>

  <script type="text/javascript" src=/voyage/<@util.retrieveResource canonicalAddress="js/lib" fileName="jquery.min" fileExtension="js"/>></script>
  <script type="text/javascript" src=/voyage/<@util.retrieveResource canonicalAddress="js/lib" fileName="plugins" fileExtension="js"/>></script>
  <script type="text/javascript" src=/voyage/<@util.retrieveResource canonicalAddress="js" fileName="common" fileExtension="js"/>></script>

  <@tag_cms_renderContainer code="specificJSContainer" />

  <link media="all" type="text/css" rel="stylesheet" href=/voyage/<@util.retrieveResource canonicalAddress="css" fileName="common" fileExtension="css"/> />
  <@tag_cms_renderContainer code="specificCSSContainer" />

  <#if context.lookup('pageCode')! == 'index'>
    <link rel="canonical" href="http://www.fr.lastminute.com/site/voyages/agence-de-voyage/" />
  <#elseif context.lookup('pageCode')! == 'lmnSearchResults'>
    <link rel="canonical" href="http://www.fr.lastminute.com/site/bon-plan/a-la-derniere-minute/" />
  <#elseif context.lookup('pageCode')! == 'product'>
    <#if productExists>
      <link rel="canonical" href="http://voyage.lastminute.com/${product.seoTitle}/${product.product.code}" />
    </#if>
  </#if>
  <!--[if IE]>
    <style type="text/css">
      #header .section .btn { font-size: .9rem; margin: .3rem .5rem 0 0; padding:.2rem .5rem; }
      #header .section .btn.back { margin-top: .3rem; }
      #header .section .btn.back span { background-image: none; padding:.2rem .5rem; }
      #header .logo { background-position: 100px 5px; background-size: 800px 300px; margin-top: .2rem; }
      ul#menu { display: none; position: absolute; top: 58px; left: 17px; z-index: 20; }

    </style>
  <![endif]-->
</head>
