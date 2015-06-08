<#import "/directives.ftl" as util>
<#assign pageCode = context.lookup('pageCode')!'Undefined'>
<!DOCTYPE html>
<html lang="fr">

  <@tag_cms_renderContainer code="headContainer" />

  <body>
    <div id="wrapper">

      <@tag_cms_renderContainer code="shareProductFormContainer" />

      <div id="wrapper-content">
        <@tag_cms_renderContainer code="headerContainer" />

        <ul id="menu" class="menu">
          <li><a href="/voyage/favorites.html" class="opener ico1">Mes favoris</a></li>
          <li><a href="/voyage/facebook.html" class="opener ico2">Aimer sur facebook</a></li>
          <li><a href="/voyage/googleplus.html" class="opener ico3">+1 sur Google+</a></li>
          <li><a href="/voyage/legal-notice.html" class="opener ico4">Informations l&eacute;gales</a></li>
          <li><a href="/voyage/need-help.html" class="opener ico5">Besoin d'aide ?</a></li>
          <li><a href="/voyage/clear-private-data.html" class="opener ico6">Effacer mes informations</a></li>
        </ul>

        <div id="main">
          <@tag_cms_renderContainer code="bodyContainer" />
        </div>

        <@tag_cms_renderContainer code="footerContainer" />

        <script type="text/javascript">
            //<![CDATA[
            var m=new TagContainerLoader();m.tagContainerDC="d1";m.tagContainerNamespace="lastminute";m.tagContainerName="FranceMobile";m.loadTagContainer();function TagContainerLoader(){var b=this,a=b.w=window;b.d=a.document;b._c="s_l";if(!a.s_c_il){a.s_c_il=[];a.s_c_in=0}b._il=a.s_c_il;b._in=a.s_c_in;b._il[b._in]=b;a.s_c_in++;b.timeout=5000;b.to=new Function("var t=s_c_il["+b._in+"];if(t.mt)t.mt(0)");b.loadTagContainer=function(){var q=this,g,e=q.d.body,f,k=q.tagContainerServer?q.tagContainerServer:"www.adobetag.com",j=q.tagContainerServerSecure?q.tagContainerServerSecure:k,i=q.d.cookie,h=q.tagContainerEnv?q.tagContainerEnv:(i?(i.indexOf("s_tagEnv=dev")>=0?"dev":(i.indexOf("s_tagEnv=stage")>=0?"stage":"live")):"live"),o=(q.w.location.protocol.toLowerCase().indexOf("https")>=0?"https://"+j:"http://"+k)+"/"+(q.tagContainerDC?q.tagContainerDC+"/":"")+q.tagContainerNamespace+"/"+h+"/"+q.tagContainerName+".js";if(q.tagContainerURL){o=q.tagContainerURL}if(q.timeout){q.ti=setTimeout(q.to,q.timeout)}if(q.d.getElementsByTagName){g=q.d.getElementsByTagName("HEAD");if(g&&g[0]){e=g[0]}}else{e=0}if(e&&!q.tagContainerSynchronous){f=q.d.createElement("SCRIPT");if(f){f.type="text/javascript";f.setAttribute("async","async");f.src=o;if(e.firstChild){e.insertBefore(f,e.firstChild)}else{e.appendChild(f)}}}else{q.d.write('<script language="JavaScript" type="text/javascript" src="'+o+'"><\/script>')}};b.fs=function(d,h){if(d&&h){var e=d.split(","),c=h.split(","),g,f;for(g=0;g<e.length;g++){for(f=0;f<c.length;f++){if(e[g]==c[f]){return 1}}}}return 0};b.aa=function(d){var c=0,e;if(d){c=[];for(e=0;e<d.length;e++){c[e]=d[e]}}return c};b.wl=[];b.wq=[];b.createAsynchronousCustomTagHandler=function(h,g){var e=this,c,d;if(!g){g=h;h=0;c=e.w}else{if(!e.w[h]){e.w[h]={}}c=e.wl[h]=e.w[h]}if(typeof(g)!="object"){g=[g]}for(d=0;d<g.length;d++){if(!c[g[d]]){c[g[d]]=new Function("var t=s_c_il["+e._in+"];t.wq[t.wq.length]={"+(h?'o:"'+h+'",':"")+'f:"'+g[d]+'",a:t.aa(arguments)}')}}};b.as=function(c){var e=[],d;for(d=1;d<c.length;d++){e[e.length]=c[d]}return e};b.s=0;b.contextData={};b.retrieveLightData={};if(!a.s_giq){a.s_giq=[]}b._gi=a.s_gi;a.s_gi=new Function("u","var t=s_c_il["+b._in+'],w=t.w,l=t._il,i,j,x,s;u=u.toLowerCase();if(l)for(j=0;j<2;j++)for(i=0;i<l.length;i++){s=l[i];x=s._c;if((!x||x=="s_c"||(j>0&&x=="s_l"))&&s.oun&&(s.oun==u||(s.fs&&s.sa&&s.fs(s.oun,u)))){if(s.sa)s.sa(u);return s}}if(!t.oun){t.sa(u);return t}if(t._gi)return t._gi(u);s=new TagContainerLoader();s.tagContainerName="s_tca_"+w.s_giq.length;s.sa(u);w.s_giq[w.s_giq.length]=s;return s');b.sa=function(c){var d=this;if(d.s){d.s.sa(c)}d.un=c;if(!d.oun){d.oun=c}else{if(!d.fs(d.oun,c)){d.oun+=","+c}}};b.tq=[];b.track=b.t=function(d){var e=this,c;if(e.s){return e.s.t(d)}if(!d){d={}}for(c in e){if(c!="un"||e.u!=e.un){d[c]=e[c]}}e.tq[e.tq.length]=d;e.lnk=e.linkName=e.linkType="";return""};b.trackLink=b.tl=function(f,d,g,c){var e=this;if(e.s){return e.s.tl(f,d,v,c)}e.lnk=f;e.linkType=d;e.linkName=g;return e.t(c)};b.trackLight=function(g,f,e,c){var d=this;if(d.s){return d.s.trackLight(g,f,e,c)}d.lightProfileID=g;d.lightStoreForSeconds=f;d.lightIncrementBy=e;return d.t(c)};b.lmq=[];b.loadModule=function(g,c,f){var e=this;if(e.s){return e.s.loadModule(g,c,f)}e.lmq[e.lmq.length]={n:g,u:c,d:f};return 0};b.ml=[];b.mmq=[];b.mo=function(c,g){var e=this,d;e.ml[c]=e[c]={};if(g){for(d=0;d<g.length;d++){e[c][g[d]]=new Function("var t=s_c_il["+e._in+'];t.mmq[t.mmq.length]={m:"'+c+'",f:"'+g[d]+'",a:t.aa(arguments)}')}}};b.mo("Media",["open","play","stop","close","track"]);b.mo("Survey",["launch"]);b.mci=[];b.mn=[];b.mc=function(i,d,h,f){var g=this,c,e=0;if(typeof(mboxFactoryDefault)=="undefined"||g.d.getElementById(d)==null){return}if(!mboxFactoryDefault.isEnabled()){clearInterval(g.mci[f]);g.mt(true);return}if(typeof(mboxFactoryDefault.get(g.mn[0],0))!="undefined"){e=mboxFactoryDefault.get(g.mn[0],0).isShown()}if(f==0||e){clearInterval(g.mci[f]);c=mboxFactoryDefault.create(i,h.split("&"));if(c){c.load()}}};if(!a.mboxCreate&&!a.mboxDefine&&!a.mboxUpdate){a.mboxVersion="mini";if(!b.d.getElementById("mboxScriptContainer")){b.d.write('<div id="mboxScriptContainer" style="display:none;visibility:hidden;"></div><style>.mboxDefault{visibility:hidden;}</style>')}b.mt=function(h){var g=this,e,c,k;if(typeof(mboxFactoryDefault)=="undefined"||h){for(e in g.mci){clearInterval(g.mci[e])}k=(g.d.getElementsByClassName)?g.d.getElementsByClassName("mboxDefault"):g.d.getElementsByTagName("div");for(c in k){if(k[c].className=="mboxDefault"){k[c].style.visibility="visible"}}}};b.mpi={};b.mp=function(d,c){var e=this;e.mpi[d]=setInterval(function(){if(typeof(mboxFactoryDefault)==="undefined"){return}c();clearInterval(e.mpi[d])},13)};a.mboxCreate=function(h){var e=0,f,c,g,d;for(f in b.mn){if(b.mn[f]==h){e++}}b.mn[b.mn.length]=h;c="mboxMarker-default-"+h+"-"+e;b.d.write('<div id="'+c+'" style="visibility:hidden;display:none">&nbsp;</div>');g=b.as(arguments).join("&");d=b.mci.length;b.mci[d]=setInterval(function(){b.mc(h,c,g,d)},5)};a.mboxDefine=function(e,f){var c=b.as(arguments);b.mp("define_"+f,function(){mboxFactoryDefault.create(f,c,e)})};a.mboxUpdate=function(e){var d=b.as(arguments),c;c="update_"+e;b.mpi[c]=setInterval(function(){if(typeof(mboxFactoryDefault)==="undefined"||typeof(mboxFactoryDefault.get(e))==="undefined"){return}mboxFactoryDefault.update(e,d);clearInterval(b.mpi[c])},13)};a.mboxLoadSCPlugin=function(c){(function(){if(typeof(mboxFactoryDefault)==="undefined"||typeof(mboxExternalLoadSCPlugin)==="undefined"||(c._c=="s_l"&&!c.s)){setTimeout(arguments.callee,19);return}if(c._c=="s_l"){c=c.s}mboxExternalLoadSCPlugin(c)})()}}};
            if(m){
                m.contextData['OmniSettings.JSessionID'] = $.cookie("JSESSIONID");
                m.contextData['OmniSettings.pageName'] = document.title.toLowerCase();
                m.contextData['OmniSettings.server'] = window.location.hostname;
                m.contextData['OmniSettings.siteSection'] = "<@tag_cms_writeMessage key="pages.${pageCode}.omniturePageCode" fileName="pages.properties" />";
                m.contextData['OmniSettings.channel'] = "Voyages";
                var s_eVar4 = "Voyages";
                m.contextData['OmniVars.PartnerID'] = '33051';
                m.contextData['OmniVars.PageURL'] = document.location;

                <@tag_cms_renderContainer code="specificOmnitureContainer" />

                m.t();
            }
            //]]>
        </script>
      </div>

    </div>
  </body>
</html>