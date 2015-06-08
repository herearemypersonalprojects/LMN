<#import "/directives.ftl" as util>
<#assign resaChannel = context.lookup('resaChannel')!>


<@tag_cms_lookup name="product"; product>
<form id="resaForm" action="<@util.retrieveConfigValue keyName="LMN_MOBILE_RESA_URL"/>" class="select-form">
  <fieldset>

    <input type="hidden" name="depCityCode" value="">
    <input type="hidden" name="homeUrl" value="<@util.retrieveConfigValue keyName="LMN_MOBILE_SITE_DOMAIN_URL"/>/index.html">
    <input type="hidden" name="provider" value="${product.product.technicalInfo.tourOperator.code}">
    <input type="hidden" name="catalogCode" value="${product.product.code}">
    <input type="hidden" name="toCode" value="${product.product.toProductCode}">
    <input type="hidden" name="productName" value="${product.title}">
    <#if (product.diaporamaItemsUrls.size() > 0)>
      <input type="hidden" name="thumbNailUrl" value="${product.diaporamaItemsUrls.get(0)}">
    </#if>
    <input type="hidden" name="reservationProfileChannelCode" value="${resaChannel}">

    <input type="hidden" name="productUrl" value="<@util.retrieveConfigValue keyName="LMN_MOBILE_SITE_DOMAIN_URL"/>">
    <input type="hidden" name="productDetailsUrl" value="<@util.retrieveConfigValue keyName="LMN_MOBILE_SITE_DOMAIN_URL"/>">

    <#assign toInfos = "">
    <#if product.toInfos??>
      <#if product.toInfos.name??><#assign toInfos = toInfos + "&toName=" + product.toInfos.name></#if>
      <#if product.toInfos.address??><#assign toInfos = toInfos + "&toAddress=" + product.toInfos.address></#if>
      <#if product.toInfos.postalCode??><#assign toInfos = toInfos + "&toPostaleCode=" + product.toInfos.postalCode></#if>
      <#if product.toInfos.city??><#assign toInfos = toInfos + "&toCity=" + product.toInfos.city></#if>
      <#if product.toInfos.country??><#assign toInfos = toInfos + "&toCountry=" + product.toInfos.country></#if>
    </#if>

    <input type="hidden" name="complementaryParameters" value="partnerId=33051${toInfos}">

    <ul class="form-list">
      <li class="clearfix">
        <div class="counter">
          <a href="#" class="btn-dec">dec</a>
          <input type="text" id="ipt-1" placeholder="2" title="min=0;max=9" name="nbAdults" />
          <a href="#" class="btn-inc">inc</a>
        </div>
        <label for="ipt-1">Nombre d'adultes</label>
      </li>
      <li class="clearfix">
        <div class="counter">
          <a href="#" class="btn-dec">dec</a>
          <input type="text" id="ipt-2" placeholder="0" title="min=0;max=5" name="nbChildren" class="childrenAge" />
          <a href="#" class="btn-inc">inc</a>
        </div>
        <label for="ipt-2">Nombre d'enfants (-12 ans)</label>
      </li>
      <li class="persons childrenAge clearfix" id="child-ipt-2">
        <#list 1..5 as i>
          <div class="box">
            <label for="slt_children_age${i}" class="title">Age enfant ${i}</label>
            <select id="slt_children_age${i}" name="ageChildren[${i-1}]">
              <option>2</option><option>3</option><option>4</option><option>5</option><option>6</option>
              <option>7</option><option>8</option><option>9</option><option>10</option><option>11</option>
            </select>
          </div>
        </#list>
      </li>
      <li style="display: none" class="clearfix"></li>
      <li class="clearfix">
        <div class="counter">
          <a href="#" class="btn-dec">dec</a>
          <input type="text" id="ipt-3" placeholder="0" title="min=0;max=3" name="nbBabies" />
          <a href="#" class="btn-inc">inc</a>
        </div>
        <label for="ipt-3">Nombre de b&eacute;b&eacute;s (-2 ans)</label>
      </li>
    </ul>

    <select class="select" name="disponibility" id="slt_avail">
      <#list product.availabilities as avail>
        <option <#if product.price?number == avail.price>data="cheapest"</#if> value="${avail.departureDate?string("dd-MM-yyyy")}-${avail.nbDays}-${avail.nbNights}">${avail.departureDate?string("EEE d MMMM")} - ${avail.nbDays}j/${avail.nbNights}n - ${avail.price} &euro;*</option>
      </#list>
    </select>

    <input type="submit" class="submit" value="R&eacute;server ce s&eacute;jour" />
  </fieldset>
</form>
</@tag_cms_lookup>