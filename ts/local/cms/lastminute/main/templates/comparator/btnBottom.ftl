<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<td class="col-${productDisplayable.params}">
  <div class="btn table-bottom even">
    <a href="/${productDisplayable.seoUrl}" class="btn-pink-next">continuer</a>
  </div>
</td>
