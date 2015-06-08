<#assign
  brandData = context.lookup("brandData")!''
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<td class="cell-content">
  <#if brandData.brandName == 'lastminute' || brandData.brandName == 'CGOS'  || brandData.brandName == 'SELECTOUR'>
    <@tag_cms_writeMessage key="${productDisplayable.reference}" fileName="customer_reviews.properties";prodid >
      <script type="text/javascript">
        <!--
          document.write('<scr' + 'ipt language="JavaScript" type="text/javascript" src="http://avis.lastminute.com/?ctrl=rating&act=jsproductopinion&prodid=' + ${prodid} + '"></scr' + 'ipt>');
        //-->
      </script>
    </@tag_cms_writeMessage>
  </#if>
</td>
