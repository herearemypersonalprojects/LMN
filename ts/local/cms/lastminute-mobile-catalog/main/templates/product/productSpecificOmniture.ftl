if(m){
    m.contextData['OmniVars.ViewedPackageType'] = 'Holidays';
    <#--m.contextData['OmniVars.SupplierID'] = 'Lowcostbeds Switzerland::Avro (Schedule)::resorthoppa'; //Set to SupplierID
    m.contextData['OmniVars.Promotion'] = '555555'; //Set to promo code, if no Promotion just send false-->
    m.events = "prodView";
    <#assign productExists = context.lookup('product')??>
    <#if productExists>
      <@tag_cms_lookup name="product"; product>
        <#if product.mealPlanCode??>
          m.contextData['OmniVars.ProductMeal'] = '${product.mealPlanCode}';
        </#if>
        m.products= ";${product.product.code} HOLIDAY;2;${product.price};;evar3=Holidays|evar27=Orchestra";
      </@tag_cms_lookup>
    <#else>
      m.contextData['OmniVars.ErrorMessage'] = 'PRODUCT DOESN'T EXISTS';
    </#if>
}