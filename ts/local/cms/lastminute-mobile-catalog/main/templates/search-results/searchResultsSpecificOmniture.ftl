if(m) {
    m.contextData['OmniVars.SearchedDepartureCity'] = doc.getUrlParameter("s_dpci", true);

    var omniS_cde = doc.getUrlParameter("s_c.de", true);
    if (omniS_cde === "") {
        m.contextData['OmniVars.SearchedDestinationCountry'] = 'All';
    } else {
        var omniS_cdeParts = omniS_cde.split(".");
        m.contextData['OmniVars.SearchedDestinationCountry'] = omniS_cdeParts[0];
        if (omniS_cdeParts.length > 1) {
            m.contextData['OmniVars.SearchedDestinationCity'] = omniS_cdeParts[1];
        }
    }

    var omniS_dmy = doc.getUrlParameter("s_dmy", true);
    var omniS_dd = doc.getUrlParameter("s_dd", true);
    if (omniS_dmy !== "" && omniS_dd !== "") {
         m.contextData['OmniVars.SearchedDepartureDate'] = omniS_dd + '-' + omniS_dmy.replace('/', '-');
    }
    <#--m.contextData['OmniVars.SearchedReturndate'] = '30-11-2012'; //Set to Searched Return date-->
    var omniS_aj = doc.getUrlParameter("s_aj", true);
    if (omniS_aj !== "") {
        m.contextData['OmniVars.SearchedFlexibility'] = omniS_aj + ' jours';
    }
    var omniS_minMan = doc.getUrlParameter("s_minMan", true);
    if (omniS_minMan === "6,9") {
        m.contextData['OmniVars.ViewedTravelDuration'] = omniS_minMan.replace(',', ' - ') + ' nuits';
    }
    m.contextData['OmniVars.SearchedPackageType'] = 'Holidays';
    <#--m.contextData['OmniVars.NoOfSearchesReturned'] = '300'; //Set to true if is true or false-->
    m.events = "event22";
}