<#if item??>
  <#if item?has_content>
        <div class="block-customer-reviews">
          <div class="block-outside">
            <ul>
              <li><strong>${item.firstName+' '+item.lastName}</strong>(${item.age})</li>
              <li>est parti(e) : ${item.context}</li>
              <li>Date du s&eacute;jour : ${item.getDateString()}</li>
            </ul>
            <div class="block-price">
              <div class="overall-rating">Note globale :</div>
              <div class="rating-block"><strong>${item.getStringScore()}</strong>/${item.maxiScore}</div>
              <div class="rating-bar">
                <div class="rating-bar_off">
                  <#assign noteRate = (item.score/item.maxiScore)*114 />
                  <#assign noteStr = "" + noteRate />
                  <span class="rating-bar_on" style="width: ${noteStr?replace(",", ".")}px;"> </span>
                </div>
              </div>
            </div>
          </div>
          <div class="titleBold-pink paddingLeftx10">Commentaires :</div>
          <div class="positives">Les points positifs :</div>
          <p>${item.posPoint}</p>
          <div class="areas-improvement">Les points &agrave; am&eacute;liorer :</div>
          <p class="lastChild">${item.negPoint}</p>
          <div class="details-ratings close">
            <div class="titleBold-pink paddingLeftx10">Notes : <span class="link-more">afficher les d&eacute;tails</span></div>
            <div class="contents-details-ratings">
              <ul>
              <#list item.getNoteList() as itemList>
              <#list itemList.getReviewNotes() as note>
              <#switch note.criterionName>
                <#case "_FLY">
                  <li>Le vol (respect des horaires, service)<span>${note.answer?replace(".", "")}
                    <#if note.answer != "NC">/${note.maxiScore}</#if></span></li>
                <#break>
                <#case "_STRINGER">
                  <li>Le correspondant local<span>${note.answer?replace(".", "")}
                    <#if note.answer != "NC">/${note.maxiScore}</#if></span></li>
                <#break>
                <#case "_ENVIRON">
                  <li>Le cadre de l'h&ocirc;tel<span>${note.answer?replace(".", "")}
                    <#if note.answer != "NC">/${note.maxiScore}</#if></span></li>
                <#break>
                <#case "_HOUSING">
                  <li>Votre chambre / logement<span>${note.answer?replace(".", "")}
                    <#if note.answer != "NC">/${note.maxiScore}</#if></span></li>
                <#break>
                <#case "_ANIMATION">
                  <li>L'animation / les activit&eacute;s<span>${note.answer?replace(".", "")}
                    <#if note.answer != "NC">/${note.maxiScore}</#if></span></li>
                <#break>
                <#case "_EQUIPEMENT">
                  <li>Les &eacute;quipements de l'h&ocirc;tel<span>${note.answer?replace(".", "")}
                    <#if note.answer != "NC">/${note.maxiScore}</#if></span></li>
                <#break>
                <#case "_EXCURSION">
                  <li>Les excursions<span>${note.answer?replace(".", "")}
                    <#if note.answer != "NC">/${note.maxiScore}</#if></span></li>
                <#break>
                <#default>
                <#break>
              </#switch>
              </#list>
              </#list>
              </ul>
              <ul>
              <#list item.getNoteList() as itemList>
              <#list itemList.getReviewNotes() as note>
              <#switch note.criterionName>
                <#case "_TRANSFERT">
                  <li>Le transfert<span>${note.answer?replace(".", "")}
                    <#if note.answer != "NC">/${note.maxiScore}</#if></span></li>
                <#break>

                <#case "_SERVICE">
                  <li>Le service / le personnel de l'h&ocirc;tel<span>${note.answer?replace(".", "")}
                    <#if note.answer != "NC">/${note.maxiScore}</#if></span></li>
                <#break>

                <#case "_GEO">
                  <li>La situation g&eacute;ographique de l'h&ocirc;tel<span>${note.answer?replace(".", "")}
                    <#if note.answer != "NC">/${note.maxiScore}</#if></span></li>
                <#break>

                <#case "_RESTORATION">
                  <li>La restauration<span>${note.answer?replace(".", "")}
                    <#if note.answer != "NC">/${note.maxiScore}</#if></span></li>
                <#break>

                <#case "_QUALITYPRICE">
                  <li>Le rapport qualit&eacute;/prix<span>${note.answer?replace(".", "")}
                    <#if note.answer != "NC">/${note.maxiScore}</#if></span></li>
                <#break>

                <#case "_KIDACTIVITY">
                  <li>Les activit&eacute;s pour enfants<span>${note.answer?replace(".", "")}
                    <#if note.answer != "NC">/${note.maxiScore}</#if></span></li>
                <#break>

                <#default>
                <#break>
              </#switch>
              </#list>
              </#list>
              </ul>

            </div>
          </div>
        </div>
  </#if>
</#if>