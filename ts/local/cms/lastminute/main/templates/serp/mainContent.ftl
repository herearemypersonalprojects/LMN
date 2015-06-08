<div id="content" class="content-wrap">
  <#list context.childElements as currentContainer>
    <@tag_cms_renderContainer container=currentContainer />
  </#list>
</div>