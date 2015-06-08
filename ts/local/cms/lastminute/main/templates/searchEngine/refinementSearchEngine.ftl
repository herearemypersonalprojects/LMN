<#assign brandData = context.lookup("brandData")!'' />
<@tag_cms_lookup name="displayb2bBlock">
<form class="productIdForm" id="productIdSearch" method="GET" action="/serp.cms">
  <fieldset>
    <h2>B2B</h2>
    <input type="hidden" name="user" value="b2b"/>
    <label for="ipt_productId">Recherche par ID du produit</label>
    <input type="text"id="ipt_productId" name="s_pid" />
    <#if brandData.brandName != 'SELECTOUR'>
      <label for="ipt_toName">Recherche par nom du TO</label>
      <input type="text"id="ipt_toName" name="s_to" />
    </#if>
    <label for="ipt_toName">Recherche par nom produit</label>
    <input type="text"id="ipt_toName" name="s_hname" />
    <span class="btn"><span><input type="submit" value="recherchez" /></span></span>
  </fieldset>
</form>
</@tag_cms_lookup>

<@tag_cms_lookup name="refinementSearchEngine"; refinementSearchEngine>
  <form class="advanced-search" action="#">
    <fieldset>
      <div class="title-block">
      <h2>affiner votre recherche</h2>
      <div class="link">
        <a href="#" class="reinitAll">réinitialiser la recherche</a>
      </div>
      </div>

      <@tag_cms_lookup name="displayDestinationCities">
        <#list refinementSearchEngine.getCriterion() as criterion>
          <#if "de"=criterion.getCode()>
            <div id="regionsPanel" class="block regions closed">
              <strong class="toggle-regions-inner">villes</strong>
              <div class="regions-inner hidden">
              <#list criterion.getValue() as critValue>
                <#if critValue.getCode()?index_of(".") != -1>
                  <div class="row">
                    <input type="checkbox" id="${critValue.getCode()}" class="check" name="aff_c.de" value="${critValue.getCode()}" />
                    <label for="${critValue.getCode()}">
                      <#if critValue.getEntityNb() == 0>
                        <span class="offerlabel nooffer">${critValue.getLabel()}</span></label>
                      <#else>
                        <span class="offerlabel">${critValue.getLabel()}</span></label>
                      </#if>
                      <#if critValue.getEntityNb() == 0><span class="zeroffer"><#else><span></#if>(${critValue.getEntityNb()})</span>
                  </div>
                </#if>
              </#list>
              <div class="link">
               <a href="#" class="uncheckAll">tout décocher</a>
               <a href="#" class="checkAll">tout cocher</a>
              </div>
            </div>
            </div>
          </#if>
        </#list>
      </@tag_cms_lookup>
      <#list refinementSearchEngine.getCriterion() as criterion>
        <#if "cat"=criterion.getCode()>
          <div class="block firstBlock">
            <strong>catégorie</strong>
            <#list criterion.getValue() as critValue>
              <div class="row">
                <input type="checkbox" id="${critValue.getCode()}" class="check" name="aff_c.cat" value="${critValue.getCode()}" />
                <label for="${critValue.getCode()}">
                  <#if critValue.getCode() = "category1">1*
                  <#elseif  critValue.getCode() = "category2">2*
                  <#elseif  critValue.getCode() = "category3">3*
                  <#elseif  critValue.getCode() = "category4">4*
                  <#else>5*
                  </#if>
                </label>
                <#if critValue.getEntityNb() == 0><span class="zeroffer"><#else><span></#if>(${critValue.getEntityNb()})</span>
              </div>
            </#list>
            <div class="link">
              <a href="#" class="uncheckAll">tout décocher</a>
              <a href="#" class="checkAll">tout cocher</a>
            </div>
          </div>
        </#if>
      </#list>

      <div class="block">
        <strong>formule</strong>
        <#list refinementSearchEngine.getMealPlan() as mealPlan>
          <#if (mealPlan.getCode() != "Program" && mealPlan.getCode() != "Others" && mealPlan.getCode() != "American" && mealPlan.getCode() != "Continental" && mealPlan.getCode() != "NoMealIncluded")>
            <div class="row">
              <input type="checkbox" id="${mealPlan.getCode()}" class="check" name="aff_meal" value="${mealPlan.getCode()}" />
              <label for="${mealPlan.getCode()}">${mealPlan.getLabel()}</label>
            </div>
          </#if>
        </#list>
        <div class="link">
          <a href="#" class="uncheckAll">tout décocher</a>
          <a href="#" class="checkAll">tout cocher</a>
        </div>
      </div>

      <#list refinementSearchEngine.getCriterion() as criterion>
        <#if "tvoyages"=criterion.getCode()>
          <div class="block">
            <strong>types de voyages</strong>
            <#list criterion.getValue() as critValue>
              <div class="row">
                <input type="checkbox" id="${critValue.getCode()}" class="check" title="${critValue.getLabel()}" name="aff_c.tvoyages" value="${critValue.getCode()}" />
                <label for="${critValue.getCode()}">
                  <#if critValue.getEntityNb() == 0>
                    <span class="offerlabel nooffer">${critValue.getLabel()}</span></label>
                  <#else>
                    <span class="offerlabel">${critValue.getLabel()}</span></label>
                  </#if>
                    <#if critValue.getEntityNb() == 0><span class="zeroffer"><#else><span></#if>(${critValue.getEntityNb()})</span>
              </div>
            </#list>
            <div class="link">
              <a href="#" class="uncheckAll">tout décocher</a>
              <a href="#" class="checkAll">tout cocher</a>
            </div>
          </div>
        </#if>
      </#list>

      <div class="open-close">
        <a href="#" id="open" class="open-close-link" style="display:none;">plus de critères</a>

        <div class="slide-block">
            <div class="block">
            <strong>prix</strong>
            <div class="row">
              <input type="checkbox" id="chx_price1" class="check" name="aff_mmp" value="50,299" />
              <label for="chx_price1">de 50 &euro; à 299 &euro;</label>
            </div>
            <div class="row">
              <input type="checkbox" id="chx_price2" class="check" name="aff_mmp" value="300,499" />
              <label for="chx_price2">de 300 &euro; à 499 &euro;</label>
            </div>
            <div class="row">
              <input type="checkbox" id="chx_price3" class="check" name="aff_mmp" value="500,799" />
              <label for="chx_price3">de 500 &euro; à 799 &euro;</label>
            </div>
            <div class="row">
              <input type="checkbox" id="chx_price4" class="check" name="aff_mmp" value="800,999" />
              <label for="chx_price4">de 800 &euro; à 999 &euro;</label>
            </div>
            <div class="row">
              <input type="checkbox" id="chx_price5" class="check" name="aff_mmp" value="1000," />
              <label for="chx_price5">plus de 1000 &euro;</label>
            </div>
            <div class="link">
              <a href="#" class="uncheckAll">tout décocher</a>
              <a href="#" class="checkAll">tout cocher</a>
            </div>
          </div>


          <#list refinementSearchEngine.getCriterion() as criterion>
            <#if "th"=criterion.getCode()>
              <div class="block">
                <strong>thématiques</strong>
                <#list criterion.getValue() as critValue>
                  <div class="row lists">
                    <input type="checkbox" id="${critValue.getCode()}" class="check" name="aff_c.th" value="${critValue.getCode()}" />
                    <label for="${critValue.getCode()}">
                      <#if critValue.getEntityNb() == 0>
                        <span class="offerlabel nooffer">${critValue.getLabel()}</span></label>
                      <#else>
                        <span class="offerlabel">${critValue.getLabel()}</span></label>
                      </#if>
                    <#if critValue.getEntityNb() == 0><span class="zeroffer"><#else><span></#if>(${critValue.getEntityNb()})</span>
                  </div>
                </#list>
                <div class="link">
                  <a href="#" class="uncheckAll">tout décocher</a>
                  <a href="#" class="checkAll">tout cocher</a>
               </div>
              </div>
            </#if>
          </#list>
          <a  id="close" class="open-close-link">moins de critères</a>
       </div>
     </div>
    </fieldset>
  </form>
</@tag_cms_lookup>
<#if brandData.brandName == 'CDISCOUNT'>
  <div id='div-gpt-ad-2'>
  <script type='text/javascript'>
  googletag.cmd.push(function() { googletag.display('div-gpt-ad-2'); });
  </script>
  </div>
</#if>