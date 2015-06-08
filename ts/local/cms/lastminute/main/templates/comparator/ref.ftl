<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<td class="col-${productDisplayable.params}">
  <div class="table-middle reference">
    ref.: ${productDisplayable.id}
  </div>
</td>
