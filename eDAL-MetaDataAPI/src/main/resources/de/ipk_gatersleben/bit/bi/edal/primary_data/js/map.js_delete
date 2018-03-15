function initialize() {
	var infoWindow = null;
	var map;

	// Create the map instance with settings
	map = new google.maps.Map(document.getElementById('worldmap'), {
		center : new google.maps.LatLng(38, 15),
		zoom : 1,
		mapTypeId : google.maps.MapTypeId.TERRAIN
	});

	// Create a popup info window
	infoWindow = new google.maps.InfoWindow();

	// Load the xml file and create the markers
	var xmlDoc_1 = loadXml("$serverURL/JS/doi_2014_14_ips.txt.xml");

	createMarkersRed(xmlDoc_1);

	/**
	 * Loads and returns the given xml file
	 */
	function loadXml(xmlUrl) {
		if (window.XMLHttpRequest) {
			// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp = new XMLHttpRequest();
		} else {
			// code for IE6, IE5
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.open("GET", xmlUrl, false);
		xmlhttp.send();
		xmlDoc = xmlhttp.responseXML;
		console.log(xmlDoc);
		return xmlDoc;
	}

	/**
	 * Creates a marker for each entry in the xml document
	 */
	function createMarkersRed(xmlDoc) {
		// Get all marker elements
		var items = xmlDoc.getElementsByTagName('marker');

		var latlng;
		var marker;
		for (var i = 0; i < items.length; i++) {
			// Extract location information
			latlng = new google.maps.LatLng(items[i].getAttribute('lat'),
					items[i].getAttribute('lng'));
			marker = new google.maps.Marker({
				position : latlng,
				title : items[i].getAttribute('title'),
				map : map,
				icon : 'marker_red.png'
			});

			// Listen for click events to show the info window
			google.maps.event.addListener(marker, 'click', function() {
				infoWindow.setContent(this.getTitle());
				infoWindow.open(map, this);
			});
		}
	}

}
google.maps.event.addDomListener(window, 'load', initialize);