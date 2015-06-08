<#assign show = context.lookup("showDestinationInfo")!false
         title = context.lookup("title")!''
         stayType = context.lookup("seoData.stayType") !''
         mainPicture = context.lookup("mainPicture")!''
         resume = context.lookup("resume")!'' />
<#if show == true>

  <div class="discover">
    <!--
      <h3>
          <@tag_cms_lookup name="seoData";seoData>
            <#assign stayType = seoData.stayType!'' />
            <#if stayType != ''>${stayType}<#else>Voyage</#if>
          </@tag_cms_lookup>
          ${title}
      </h3>
      -->
      <p>${resume}</p>
  </div>
</#if>
