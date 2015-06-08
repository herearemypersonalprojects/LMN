<div id="lmn-search-lightbox" class="lightbox">
  <div class="heading">
    <strong class="title">Offres derni&egrave;re minute</strong>
  </div>
  <div class="entry">
    <form id="lmn-search-form" class="lmn-search-form validate" method="post" action="#">
      <fieldset>
        <div class="row">
          <ul><li>
          <label for="slt-dep-city">Ville de d&eacute;part</label>
          <select id="slt-dep-city" name="slt-dep-city" class="required">
            <option disabled="disabled" value="" selected>S&eacute;lectionnez</option>

            <@tag_cms_lookup name="lmnDepCities"; lmnDepCities>
              <#list 0..lmnDepCities.getCitiesCount()-1 as i>
                <option value="${lmnDepCities.getCities(i)}"><@tag_cms_writeMessage key="${lmnDepCities.getCities(i)}" fileName="cityLabels.properties" /></option>
              </#list>
            </@tag_cms_lookup>

          </select>
        </div>
        </li></ul>
        <#--div id="note-container">
          <div class="note">Veuillez choisir une ville de d&eacute;part.</div>
        </div-->
        <div id="email-popin-buttons" class="row">
          <input type="submit" class="submit" value="ok" />
          <input type="reset" class="reset" value="annuler" />
        </div>
      </fieldset>
    </form>
  </div>
</div>