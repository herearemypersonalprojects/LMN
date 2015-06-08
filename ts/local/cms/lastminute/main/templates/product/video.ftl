<!DOCTYPE html>
<html lang="fr">
  <head>
    <title>Vid&eacute;o produit</title>
    <style>
      #bieplayer { width: 640px; height: 360px; margin: 0 auto; }
    </style>
  </head>
  <body>
    <div id='bieplayer'></div>
    <script type="text/javascript">
      (function(document) {
        function getURLParameters(paramName) {
          var sURL = window.document.URL.toString();
          if (sURL.indexOf("?") > 0) {
             var arrParams = sURL.split("?");
             var arrURLParams = arrParams[1].split("&");
             var arrParamNames = new Array(arrURLParams.length);
             var arrParamValues = new Array(arrURLParams.length);
             var i = 0;
             for (i=0;i<arrURLParams.length;i++)
             {
            var sParam =  arrURLParams[i].split("=");
            arrParamNames[i] = sParam[0];
            if (sParam[1] != "")
              arrParamValues[i] = unescape(sParam[1]);
            else
              arrParamValues[i] = "No Value";
             }

             for (i=0;i<arrURLParams.length;i++)
             {
                if(arrParamNames[i] == paramName){
              //alert("Param:"+arrParamValues[i]);
                return arrParamValues[i];
               }
             }
             return "No Parameters Found";
          }
        }
        var s = document.createElement("script");
        s.type = "text/javascript";
        s.src = "http://player.ooyala.com/v3/" + getURLParameters('playerID');
        var h = document.getElementsByTagName("head")[0];
        h.appendChild(s);

        function player() {
          try {
            var contentID = getURLParameters('contentID');
            var videoPlayer = OO.Player.create('bieplayer', contentID);
            videoPlayer.play();
            clearInterval(timer);
          } catch (e) {
            // don't removed
          }
        }
        var timer = setInterval(player, 200);
      })(document);
    </script>
  </body>
</html>