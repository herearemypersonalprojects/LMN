<div class="content-box">
  <div class="heading">
    <h2 class="ico1">Mes favoris</h2>
  </div>

  <div id="no-favorites" style="display: none;">
    Votre liste des favoris est vide.
  </div>
  <div id="storage-disabled" style="display: none;">
    Votre navigateur ne permet pas de g&eacute;rer une liste de favoris.
  </div>

  <ul id="fav-list" class="fav-list">

    <#-- BEGIN hidden model favorite block -->
    <li style="display: none;">
      <div class="col">
        <a href="#" class="btn-delete">delete</a>
      </div>
      <div class="col2">
        <div class="text-box">
          <h3><a href=""><#-- title --></a></h3>
          <p class="dur"></p>
          <span class="price"><#-- price <sup>&euro;</sup> --></span>
          <span class="alt-text"> TTC / pers.</span>
        </div>
      </div>
      <div class="col3">
        <a href="" class="btn-next">next</a>
      </div>
    </li>
    <#-- END hidden model favorite block -->

  </ul>
</div>
