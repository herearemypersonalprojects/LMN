<#assign publishedProduct = context.lookup("publishedProduct") />
<#assign brandData = context.lookup("brandData")!''/>
<#assign mediaTag = context.lookup("mediaTagStats") />

<#import "/lib/directives.ftl" as resource>

<#assign brandData = context.lookup("brandData")!'' />
<#if brandData.brandName != 'SELECTOUR'>
    <img width="159" height="188" alt="" src="<@resource.retrieveResource canonicalAddress="images" fileName="sidebar-image1" fileExtension="gif"/>" />
</#if>


