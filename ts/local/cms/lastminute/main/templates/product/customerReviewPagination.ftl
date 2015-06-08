<#if productAvis??>
 <#if productAvis?has_content>
   <#assign maxPagings = 6>
   <#assign nbAvis = 133>
   <#assign nbDisplays = 3>
   <#assign nbPagings = maxPagings>
   <#assign totalPages = nbAvis/nbDisplays+1>
   <#assign currentPage = 1>
   <#assign startPage = 1>

   <#if totalPages < maxPagings>
       <#assign nbPagings = totalPages>
   </#if>

   <#if nbPagings/2 < currentPage>

   </#if>

  <div class="sort-selector">
    <table class="paging">
        <tbody>
          <tr>
          <#if nbDisplays < nbAvis>
            <#if currentPage != startPage>
              <td><span>&laquo;</span></td>
            </#if>
            <#list startPage..(nbPagings+startPage-1) as page>
              <#if currentPage == page>
                <td class="selected"><span>${page}</span></td>
              <#else>
                <td><span>${page}</span></td>
              </#if>
            </#list>
            <#if (currentPage < nbPagings)>
              <td><span>&raquo;</span></td>
            </#if>
          </#if>
          </tr>
      </tbody>
    </table>
  </div>
 </#if>
</#if>