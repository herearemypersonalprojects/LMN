<@tag_cms_lookup name="lmnSearchImgLauncher"; lmnSearchImgLauncher>
  <#if lmnSearchImgLauncher != "">
    <div id="${context.associatedLayout.id}-toLmnSearch" class="image-box lastminuteSearchLauncher">
      <div class="holder">
        <img src="${lmnSearchImgLauncher}" alt="voir les offres derni&egrave;re minute" />
      </div>
    </div>
  </#if>
</@tag_cms_lookup>