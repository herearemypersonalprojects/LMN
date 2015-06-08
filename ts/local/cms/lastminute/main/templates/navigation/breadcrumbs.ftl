<@tag_cms_lookup name="seoData"; seoData>
<#assign destinationCountry = seoData.destinationCountry!'' />
<#assign destinationCity = seoData.destinationCity!'' />
<#assign stayType = seoData.stayType!'' />
<#assign productName = seoData.productName!'' />
<#assign currentPage = context.lookup("currentPage")!'' />


      <!-- ******** BEGIN BREADCRUMB ************************************* -->
              <div class="breadcrumb">
                <div>
                    <span class="select" itemprop="title">Voyage</span>
                    <#if destinationCountry != '' || destinationCity != '' || stayType != ''>
                      <span class="arrow">></span>
                    </#if>
                </div>

                <#if destinationCountry != ''>
                  <#if destinationCity != '' || stayType != '' || productName != ''>
                    <div  itemtype="http://data-vocabulary.org/Breadcrumb" itemscope="itemscope">
                        <a href="/voyage-${destinationCountry}"  itemprop="url">
                          <span itemprop="title">
                            voyage ${destinationCountry}
                          </span>
                        </a>
                        <span class="arrow">></span>
                    </div>
                  <#else>
                    <div  itemtype="http://data-vocabulary.org/Breadcrumb" itemscope="itemscope">
                          <span class="select"  itemprop="title">
                            voyage ${destinationCountry}
                          </span>
                    </div>
                  </#if>

                  <#if destinationCity !=''>
                    <#if productName != '' || stayType != ''>
                      <div itemtype="http://data-vocabulary.org/Breadcrumb" itemscope="itemscope">
                          <a href="/voyage-${destinationCountry}/${destinationCity}" itemprop="url">
                            <span itemprop="title">
                              voyage ${destinationCity}
                            </span>
                          </a>
                          <#if productName != ''><span class="arrow">></span></#if>
                      </div>
                    <#else>
                      <div itemtype="http://data-vocabulary.org/Breadcrumb" itemscope="itemscope">
                            <span class="select"  itemprop="title">
                              voyage ${destinationCity}
                            </span>
                      </div>
                    </#if>
                  <#elseif stayType != ''>
                    <#if currentPage = 'product'>
                      <#if productName != ''>
                        <div itemtype="http://data-vocabulary.org/Breadcrumb" itemscope="itemscope">
                            <a href="/voyage-${destinationCountry}/${stayType}" itemprop="url">
                              <span itemprop="title">
                                ${stayType} ${destinationCountry}
                              </span>
                            </a>
                            <#if productName != ''><span class="arrow">></span></#if>
                        </div>
                      <#else>
                        <div itemtype="http://data-vocabulary.org/Breadcrumb" itemscope="itemscope">
                              <span class="select" itemprop="title">
                                ${stayType} ${destinationCountry}
                              </span>
                        </div>
                      </#if>
                    <#else>
                      <div itemtype="http://data-vocabulary.org/Breadcrumb" itemscope="itemscope">
                          <a  itemprop="url"><span>${stayType} ${destinationCountry}</span></a>
                          <#if productName != ''><span class="arrow">></span></#if>
                      </div>
                    </#if>
                  </#if>

                  <#if productName != ''>
                    <div itemtype="http://data-vocabulary.org/Breadcrumb" itemscope="itemscope">
                        <span class="select" itemprop="title">${productName}</span>
                    </div>
                  </#if>
                <#elseif destinationCountry == '' && destinationCity == '' && stayType != ''>
                  <div>
                    <#if currentPage = 'product'>
                      <a href="/${stayType}"  itemprop="url"><span itemprop="title">${stayType}</span></a>
                    <#else>
                      <span class="select" itemprop="title">${stayType}</span>
                    </#if>
                  </div>
                </#if>
              </div>
              <!-- END BREADCRUMB -->

</@tag_cms_lookup>