<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<td class="col-${productDisplayable.params}">
  <div class="table-middle">
    ${productDisplayable.baseDepartureDate}
  </div>
</td>
