<#assign brandData = context.lookup("brandData")!'' />
<#if brandData.brandName == 'CITROEN'>
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(
    // Debut du tracking principal pour le compte Google Analytics de Citroen
    ['_setAccount', 'UA-23524479-1'],  // Compte Google Analytics de Citroen
    ['_setDomainName', 'multicity.citroen.fr'],
    ['_setAllowLinker', true],
    ['_trackPageview'],

    // Fin du tracking principal pour le compte Google Analytics de Citroen
    // Debut du tracking principal pour le compte Google Analytics de LastMinute
    ['b._setAccount', 'UA-28201322-5'],  // Compte Google Analytics de LastMinute
    ['b._setDomainName', 'multicity.citroen.fr'],
    ['b._setAllowLinker', true],
    ['b._trackPageview']
    // Fin du tracking principal pour le compte Google Analytics de LastMinute
  );
  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script>
<#elseif  brandData.brandName == 'CDISCOUNT'>
	<script type="text/javascript">

	  var _gaq = _gaq || [];
	  _gaq.push(['_setAccount', 'UA-28201322-6']);
	  _gaq.push(['_trackPageview']);

	  (function() {
	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();

	</script>

<#elseif  brandData.brandName == 'SELECTOUR'>
	<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-28201322-7']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
<#else>
  <script type="text/javascript">
    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', 'UA-28201322-1']);
    _gaq.push(['_setDomainName', 'cgos.lastminute.com']);
    _gaq.push(['_trackPageview']);
    (function() {
      var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
      ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
      var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
    })();
  </script>
</#if>
