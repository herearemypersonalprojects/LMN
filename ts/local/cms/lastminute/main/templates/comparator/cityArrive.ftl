<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<td class="col-${productDisplayable.params}">
  <div class="table-middle equalized-row">
    <strong>${productDisplayable.baseDepartureCity}</strong>
  </div>
</td>
