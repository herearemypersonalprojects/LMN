<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
  productIndex = context.lookup("publishedProductIndex")!productIndex
  flashSaleInitialPrice = context.lookup("flashSaleInitialPrice")!flashSaleInitialPrice
  flashSaleDispo = context.lookup("flashSaleDispo")!flashSaleDispo
/>

<#if productIndex?number % 2 == 0 >
    <#assign classText="None" />
<#else>
    <#assign classText="odd" />
</#if>
<div class="none">
  <div id="LinkAccordion${productIndex}" class="Accordion_links ${classText}">
    <div class="cell_1 padding_5">
      <div class="gold_star_rating_${productDisplayable.getStar()} star_rating"></div>
      <strong class="desti_title">${productDisplayable.getDestinationTitle()!""}</strong>
      <br />
      <span>${productDisplayable.getDestinationCity()!""}<@tag_cms_writeMessage key="${productDisplayable.getPensions()}" fileName="pension.properties";pension ><#if pension != "">, ${pension}</#if></@tag_cms_writeMessage></span>
    </div>
    <div class="cell_2 padding_5">
      <strong>${flashSaleInitialPrice.toString()}€*</strong>
    </div>
    <div class="cell_3 padding_5">
      <strong>${flashSaleInitialPrice.subtract(flashSaleDispo.getTtcPrice()).toString()}€ d'économie</strong>
      <br />
      <span>par personne</span>
    </div>
    <div class="cell_4 padding_5">
      <strong>${flashSaleDispo.getTtcPrice().toString()}€</strong>
      <br />
      <span>par personne</span>
    </div>
  </div>
</div>
<div id="Accordion${productIndex}" class="AccordionContent clear ${classText}" style="display: none;">
    <#include "/flashSale/flashSaleDispo.ftl">
</div>
