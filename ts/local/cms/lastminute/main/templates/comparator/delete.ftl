<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<td class="col-${productDisplayable.params} align-right">
  <span id="del-col${productDisplayable.params}">
    supprimer le produit
    <span class="ico-bg">
      <i class="icon--delete">x</i>
    </span>
  </span>
</td>