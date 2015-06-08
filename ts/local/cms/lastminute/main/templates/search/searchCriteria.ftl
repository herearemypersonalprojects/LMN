<#assign brandData = context.lookup("brandData")!'' />
<@tag_cms_lookup name="displayb2bBlock">
<form class="productIdForm" id="productIdSearch" method="GET" action="/serp.cms">
  <fieldset>
    <div class="blk-search-b2b">
        <div class="inner-block">
            <h2>B2B</h2>
            <input type="hidden" name="user" value="b2b"/>
            <div class="selectBlock product-id">
                <label for="product-id">ID du produit</label>
                <input type="text" id="ipt_productId" name="s_pid">
            </div>
            <#if brandData.brandName != 'SELECTOUR'>
              <div class="selectBlock to-name">
                  <label for="to-name">Nom du TO</label>
                  <input type="text" id="ipt_toName" name="s_to" >
              </div>
            </#if>
            <div class="selectBlock product-name">
                <label for="product-name">Nom du produit</label>
                <input type="text" id="ipt_hName" name="s_hname">
            </div>
            <div class="buttonBlock">
                <button type="submit" value="recherchez"  id="" class="btn-search">Rechercher</button>
            </div>
        </div>
    </div>
  </fieldset>
</form>
</@tag_cms_lookup>

<div class="message-error hidden">Aucune offre ne correspond &agrave; vos crit&egrave;res de recherche. Nous vous invitons &agrave; renouveler votre recherche en modifiant vos filtres. Nous avons &eacute;tendu vos crit&egrave;res de s&eacute;lection afin de vous pr&eacute;senter d'autres offres</div>

<@tag_cms_lookup name="refinementSearchEngine"; refinementSearchEngine>
  <div class="filters-plus">
      <h2>affiner votre recherche</h2>
      <p class="legend"><a class="reinitAll" href="#">r&eacute;initialiser la recherche</a> </p>
      <div class="btn-block">
          <span>masquer</span>
      </div>

      <div id="advancedSearch" class="contain-block">
        <#-- Begin Cities -->
        <div class="colx3">
            <div class="title title-up"><span>-</span>villes</div>
            <div class="inner-table line-up">
                <table>
                <#assign tickAll = false>
                <@tag_cms_lookup name="displayDestinationCities">
                 <#list refinementSearchEngine.getCriterion() as criterion>
                 <#if "de"=criterion.getCode()>
                  <#assign tickAll = true>
                  <#list criterion.getValue() as critValue>
                    <#if critValue.getCode()?index_of(".") != -1>
                      <tr>
                          <td class="imp"><input id="${critValue.getCode()}" name="aff_c.de" value="${critValue.getCode()}" type="checkbox" alt=""></td>
                          <td class="lab"><label <#if critValue.getEntityNb() == 0>class="numb empty"</#if> for="${critValue.getCode()}">${critValue.getLabel()}</label></td>
                          <td class="numb hidden <#if critValue.getEntityNb() == 0>empty</#if>">(${critValue.getEntityNb()})</td>
                      </tr>
                    </#if>
                   </#list>
                  </#if>
                 </#list>
                 </@tag_cms_lookup>
                </table>
                <#if tickAll><span class="tick-all">tout cocher</span></#if>
            </div>
        </div>

        <#-- Begin Catégories -->
        <div class="colx3">
            <div class="title title-up"><span>-</span>cat&eacute;gorie</div>
            <div class="inner-table line-up">
                <table>
                <#assign tickAll = false>
                <#list refinementSearchEngine.getCriterion() as criterion>
                  <#if "cat"=criterion.getCode()>
                  <#assign tickAll = true>
                  <#list criterion.getValue() as critValue>
                      <tr>
                          <td class="imp"><input id="${critValue.getCode()}" name="aff_c.cat" value="${critValue.getCode()}" type="checkbox" alt=""></td>
                          <td class="lab">
                            <label <#if critValue.getEntityNb() == 0>class="numb empty"</#if> for="${critValue.getCode()}">
                              <#if critValue.getCode() = "category1">
                                1*
                              <#elseif  critValue.getCode() = "category2">
                                2*
                              <#elseif  critValue.getCode() = "category3">
                                3*
                              <#elseif  critValue.getCode() = "category4">
                                4*
                              <#else>
                                5*
                              </#if>
                            </label>
                          </td>
                          <td class="numb hidden <#if critValue.getEntityNb() == 0>empty</#if>">(${critValue.getEntityNb()})</td>
                      </tr>
                  </#list>
                  </#if>
                </#list>
                </table>
                <#if tickAll><span class="tick-all">tout cocher</span></#if>
            </div>
        </div>

        <#-- Begin formule -->
        <div class="colx3">
            <div class="title title-up"><span>-</span>formule</div>
            <div class="inner-table line-up">
                <table>
                  <#assign tickAll = false>
                  <#list refinementSearchEngine.getMealPlan() as mealPlan>
                    <#if (mealPlan.getCode() != "Program" && mealPlan.getCode() != "Others" && mealPlan.getCode() != "American" && mealPlan.getCode() != "Continental" && mealPlan.getCode() != "NoMealIncluded")>
                      <#assign tickAll = true>
                      <#assign label = mealPlan.getLabel()?lower_case?cap_first>
                      <tr>
                          <td class="imp"><input id="${mealPlan.getCode()}" name="aff_meal" value="${mealPlan.getCode()}" type="checkbox" alt=""></td>
                          <td class="lab"><label for="${mealPlan.getCode()}">${label}</label></td>
                          <#-- <td class="numb hidden">(${mealPlan.getEntityNb()})</td> -->
                      </tr>
                   </#if>
                  </#list>
                </table>
                <#if tickAll><span class="tick-all">tout cocher</span></#if>
            </div>
        </div>

        <#-- Begin type de voyages -->
        <div class="clearBlocks"></div>
        <div class="colx3">
            <div class="title title-bottom"><span>-</span>type de voyages</div>
            <div class="inner-table line-bottom">
                <table>
                 <#list refinementSearchEngine.getCriterion() as criterion>
                  <#if "tvoyages"=criterion.getCode()>
                  <#list criterion.getValue() as critValue>
                    <tr>
                        <td class="imp"><input id="${critValue.getCode()}" title="${critValue.getLabel()}" name="aff_c.tvoyages" value="${critValue.getCode()}" type="checkbox" alt=""></td>
                        <td class="lab"><label  <#if critValue.getEntityNb() == 0>class="numb empty"</#if> for="${critValue.getCode()}">${critValue.getLabel()}</label></td>
                        <td class="numb hidden <#if critValue.getEntityNb() == 0>empty</#if>">(${critValue.getEntityNb()})</td>
                    </tr>
                  </#list>
                  </#if>
                 </#list>
                </table>
                <span class="tick-all">tout cocher</span>
            </div>
        </div>

        <#-- Begin prix -->
        <#assign prixLabel = ["De 50 &euro; &agrave; 299 &euro;",
                  "De 300 &euro; &agrave; 499 &euro;",
                  "De 500 &euro; &agrave; 799 &euro;",
                  "De 800 &euro; &agrave; 999 &euro;",
                  "Plus de 1000 &euro;"]>
        <#assign prixValue = ["50,299", "300,499", "500,799", "800,999", "1000,"]>
        <div class="colx3">
            <div class="title title-bottom"><span>-</span>prix</div>
            <div class="inner-table line-bottom">
                <table>
                  <#list prixLabel as p>
                    <tr>
                        <td class="imp"><input id="chx_price${p_index + 1}" name="aff_mmp" value="${prixValue[p_index]}" type="checkbox" alt=""></td>
                        <td class="lab"><label for="">${p}</label></td>
                    </tr>
                  </#list>
                </table>
                <span class="tick-all">tout cocher</span>
            </div>
        </div>

        <#-- Begin thématiques -->
        <div class="colx3">
            <div class="title title-bottom"><span>-</span>th&eacute;matiques</div>
            <div class="inner-table line-bottom">
                <table>
                <#list refinementSearchEngine.getCriterion() as criterion>
                  <#if "th"=criterion.getCode()>
                  <#list criterion.getValue() as critValue>
                    <tr>
                        <td class="imp"><input id="${critValue.getCode()}" name="aff_c.th" value="${critValue.getCode()}" type="checkbox" alt=""></td>
                        <td class="lab"><label <#if critValue.getEntityNb() == 0>class="numb empty"</#if>  for="${critValue.getCode()}">${critValue.getLabel()}</label></td>
                        <td class="numb hidden <#if critValue.getEntityNb() == 0>empty</#if>">(${critValue.getEntityNb()})</td>
                    </tr>
                  </#list>
                  </#if>
                </#list>
                </table>
                <span class="tick-all">tout cocher</span>
            </div>
        </div>
    </div>
  </div>
</@tag_cms_lookup>

<#if brandData.brandName == 'CDISCOUNT'>
  <div id='div-gpt-ad-2'>
  <script type='text/javascript'>
  googletag.cmd.push(function() { googletag.display('div-gpt-ad-2'); });
  </script>
  </div>
</#if>