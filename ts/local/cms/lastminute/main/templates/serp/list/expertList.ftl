<#assign
  showExpertBlock = context.lookup("showExpertBlock")!false
/>
<#if showExpertBlock == true>

    <@tag_cms_renderContainer code="cartoucheContainer" />

</#if>