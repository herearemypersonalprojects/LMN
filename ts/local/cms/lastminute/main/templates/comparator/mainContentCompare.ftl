<#assign
  compareDisplayableList = context.lookup("compareDisplayableList")
/>

              <div class="block-contents">
                <a href="#" class="link-blue-back">retour aux résultats</a>
                <h2>comparaison de s&eacute;jours <strong> (${compareDisplayableList.getCompareProductDisplayables()?size}

                <#assign i = compareDisplayableList.getCompareProductDisplayables()?size>
                <#if i < 2>
                  produit s&eacute;lectionn&eacute;)</strong></h2>
                <#else>
                  produits s&eacute;lectionn&eacute;s)</strong></h2>
                </#if>


                <div id="topTableWrap" class="table-wrap">
                  <table id="compareTable" border="0" cellpading="0" cellspacing="0">
                    <tbody>
                      <tr class="fixed-row">
                        <td class="legend"> </td>
                        <@tag_cms_renderContainer code="deleteCompareContainer" />
                      </tr>

                      <tr class="fixed-row">
                        <td class="legend">Destinations</td>
                        <@tag_cms_renderContainer code="destinationCompareContainer" />
                      </tr>
                      <tr class="fixed-row">
                        <td class="legend">Type de voyages/formules</td>
                        <@tag_cms_renderContainer code="typeCompareContainer" />
                      </tr>
                      <tr>
                        <td class="legend">  </td>
                        <@tag_cms_renderContainer code="mainPictureCompareContainer" />
                      </tr>
                      <tr>
                        <td class="legend">  </td>
                        <@tag_cms_renderContainer code="prixCompareContainer" />
                      </tr>
                      <tr>
                        <td class="legend">  </td>
                        <@tag_cms_renderContainer code="refCompareContainer" />
                      </tr>

                      <tr>
                        <td class="legend verticalAlignMiddle">Date de d&eacute;part</td>
                        <@tag_cms_renderContainer code="dateDepCompareContainer" />
                      </tr>

                      <tr>
                        <td class="legend verticalAlignMiddle">Ville d'arriv&eacute;e</td>
                        <@tag_cms_renderContainer code="cityArriveCompareContainer" />
                      </tr>

                      <tr>
                        <td class="legend verticalAlignMiddle">Dur&eacute;e du s&eacute;jour</td>
                        <@tag_cms_renderContainer code="durationCompareContainer" />
                      </tr>
                      <#--
                      <tr>
                        <td class="legend">Prestations</td>
                        <@tag_cms_renderContainer code="prestationsCompareContainer" />
                      </tr>
                      -->
                      <tr>
                        <td class="legend"> Prestations </td>
                        <@tag_cms_renderContainer code="includeCompareContainer" />
                      </tr>


                      <tr>
                        <td class="legend"></td>
                        <@tag_cms_renderContainer code="btnContinueCompareContainer" />
                      </tr>
                      <@tag_cms_renderContainer code="critereMainContainer" />
                    <tr>
                        <td class="legend"></td>
                        <@tag_cms_renderContainer code="btnBottomCompareContainer" />
                      </tr>

                    </tbody>
                  </table>
                </div>

              </div>