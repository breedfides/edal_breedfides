let EdalReport = new function() {
    this.datatable = null;
    this.markerColors = ['#ff0000', '#cccc00', '#0000ff', '#ffff00', '#66ffff', '#000000'];
    this.doiMarkers = {};
    this.doiColor = {};
    this.allMarkers = [];
    this.yearFilter = null;
    this.searchFilter = null;
    this.stateEntriesShown = 0;
    this.stateShowAllMarkers = false;
    this.allYears = [];
    this.initReportData = null;


    this.init = function(reportData, mapData) {
        this.initReportData = this.reportData = reportData;
        this.mapData = mapData;
        this.reportDataKeyed = _.keyBy(reportData, 'doi');
        this.allYears = _.uniq(_.map(reportData, 'year')).sort().reverse();

        let self = this;
        $(document).ready(function() {
            $.fn.isInViewport = function() {
                var elem = $(this);
                var elementTop = elem.position().top;
                var elementBottom = elementTop + elem.outerHeight();
                var scrollBody = $('div.dataTables_scrollBody');
                var viewportTop = scrollBody.scrollTop();
                var viewportBottom = viewportTop + scrollBody.height();
                return elementBottom > viewportTop && elementTop < viewportBottom;
            };
            self.renderYearSelectOptions();
            self.renderDatatable();
            self.addObservers();
        });
    };

    this.initGoogleMap = function() {
        this.map = new google.maps.Map(document.getElementById('map'), {
            center: {lat: 30, lng: 0},
            zoom: 2,
            mapTypeId: 'terrain',
            disableDefaultUI: true
        });
    };

    this.renderYearSelectOptions = function() {
        let selectElem = $('#edal-report-year-filter');
        _.forEach(this.allYears, function(year) {
            selectElem.append('<option value="'+year+'">'+year+'</a>&nbsp;');
        });
    }

    this.renderDatatable = function() {
        let self = this;

        this.datatable = $('#report').DataTable({
            data: self.reportData,
            dom: 't',
            //dom: 'Bfrtip',
            order: [[2, 'desc']],
            buttons: [
                {
                    extend: 'copyHtml5',
                    exportOptions: {
                        columns: [0, 1, 2, 3]
                    }
                },
                {
                    extend: 'csvHtml5',
                    exportOptions: {
                        columns: [0, 1, 2, 3]
                    }
                },
            ],
            searching: true,
            paging: false,
            info: false,
            scrollY: true,
            scrollCollapse: true,
            columns: [
                {
                    title: "DOI",
                    data: "doi",
                    class: "edal-report-doi",
                    render: function (data, type, row) {
                        return '<a href="http://dx.doi.org/'+data+'" target="_blank">'+data+'</a>';
                    }
                },
                {
                    title: "Title",
                    data: "title",
                    class: "edal-report-title"
                },
                {
                    title: "Distinct IPs",
                    data: "accesses",
                    width: "120px",
                    className: "text-center",
                },
                {
                    title: "Download volume",
                    data: "downloads",
                    width: "170px",
                    className: "text-center",
                    render: function (data, type, row) {
                        if (type === "display") {
                            return self.niceBytes(parseInt(data));
                        } else {
                            return data;
                        }
                    }
                },
                {
                    title: "Map",
                    orderable: false,
                    width: "70px",
                    className: "text-center",
                    render: function (data, type, row) {
                        return '<a class="worldmap-link" data-doi="'+row['doi']+'" href="#" target="_blank">Show</a>';
                    }
                },
                {
                    title: "Year",
                    data: "year",
                    visible: false
                },
            ]
        });
    };

    this.addObservers = function() {
        let self = this;

        $(document).on('click', 'a.worldmap-link', function(event) {
            event.preventDefault();
            let elem = $(this);

            let state = elem.text();
            let doi = elem.data('doi');
            if (state === 'Show') {
                elem.text('Hide');
                let color = self.showMarkersForDOI(doi, elem);
                elem.css({
                    'border-right': '20px solid '+color,
                    'padding-right': '5px'
                });
            } else {
                elem.text('Show');
                self.hideMarkersForDOI(doi);
                elem.css({
                    'border-right': '0',
                    'padding-right': '0'
                });
            }
            self.toogleGoogleMapVisibility(elem);
        });

        $(document).on('change', '#edal-report-year-filter', function(event) {
            event.preventDefault();
            let elem = $(this);
            let year = parseInt(elem.val());
            if (self.searchFilter === null) {
                if (isNaN(year)) {
                    self.datatable.search('').columns().search('').draw();
                    self.yearFilter = null;
                } else {
                    self.datatable.columns(5).search(year).draw();
                    self.yearFilter = year;
                }
            } else {
                if (isNaN(year)) {
                    self.datatable.search(self.searchFilter).columns().search('').draw();
                    self.yearFilter = null;
                } else {
                    self.datatable.search(self.searchFilter).columns(5).search(year).draw();
                    self.yearFilter = year;
                }
            }
        });

        function delay(fn, ms) {
          let timer = 0
          return function(...args) {
            clearTimeout(timer)
            timer = setTimeout(fn.bind(this, ...args), ms || 0)
          }
        }

        $(document).on('keyup', '#edal-report-search', delay(function(event) {
            event.preventDefault();
            let searchword = $(this).val();
            if (searchword === '') {
                searchword = null;
            }
            self.searchFilter = searchword;
            if (self.yearFilter !== null) {
                if (searchword === null) {
                    //self.datatable.search('').columns(5).search(self.yearFilter).draw();
                    self.reportData = self.initReportData;
                    console.log("Data:")
                    console.log(self.reportData);
                    self.datatable.destroy();
                    self.renderDatatable();
                } else {
                    //self.datatable.search(searchword).columns(5).search(self.yearFilter).draw();
                    //self.reportData = $.get("http://bit-58.ipk-gatersleben.de/rest/keywordsearch/"+self.yearFilter);
                    $.get( "http://bit-58.ipk-gatersleben.de/rest/keywordsearch/"+self.yearFilter, function( data ) {
                        self.reportData = data;
                        console.log("Data:")
                        console.log(self.reportData);
                        self.datatable.destroy();
                        self.renderDatatable();
                    });
                }
            } else {
                if (searchword === null) {
                    //self.datatable.search('').columns().search('').draw();
                    self.reportData = self.initReportData;
                    console.log("Data:")
                    console.log(self.reportData);
                    self.datatable.destroy();
                    self.renderDatatable();
                } else {
                    //self.datatable.search(searchword).draw();
                    //self.reportData = $.get("http://bit-58.ipk-gatersleben.de/rest/keywordsearch/"+searchword);
                    $.get( "http://bit-58.ipk-gatersleben.de/rest/keywordsearch/"+searchword, function( data ) {
                        self.reportData = data;
                        console.log("Data:")
                        console.log(self.reportData);
                        self.datatable.destroy();
                        self.renderDatatable();
                    });
                }
            }
        }, 300));

        $(document).on('click', '#edal-report-export-csv', function(event) {
            event.preventDefault();
            self.datatable.button('.buttons-csv').trigger();
        });

        $(document).on('click', '#edal-report-export-clipboard', function(event) {
            event.preventDefault();
            self.datatable.button('.buttons-copy').trigger();
        });

        $(document).on('click', '#edal-report-show-map-all-locations', function(event) {
            event.preventDefault();
            let state = $(this).text();
            if (state === 'Show') {
                self.showGoogleMap();
                self.showAllMarkers();
                $(this).text('Hide');
            } else {
                self.hideGoogleMap();
                self.hideAllMarkers();
                $(this).text('Show');
            }
        });

    };

    this.showAllMarkers = function() {
        this.stateShowAllMarkers = true;
        let self = this;
        const markerImg = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAcAAAAHCAYAAADEUlfTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAaUlEQVQImX3OsQ3CUBRD0SPCCNmALqLJBjBP5kFeBaosAl3qPwBCehR8JCosWdduLKuqMYnW2oBja21IoqpGSWDCFffOKYm9jy449Xzo/QwzNjzx6twwf2dX1I/XJHZ9asENj84F/Hv7BtAVNyeCakVFAAAAAElFTkSuQmCC';
        const icon = {
            url: markerImg,
        };
        _.forEach(this.mapData, function(loc) {
            let marker = new google.maps.Marker({
                position: {lat: loc.lat, lng: loc.long},
                icon: icon,
            });
            marker.setMap(self.map);
            self.allMarkers.push(marker);
        });
    };

    this.hideAllMarkers = function() {
        this.stateShowAllMarkers = false;
        _.forEach(this.allMarkers, function(marker) {
            marker.setMap(null);
        });
    };

    this.showMarkersForDOI = function(doi, link) {
        this.stateEntriesShown++;
        let self = this;
        let locations = this.reportDataKeyed[doi].locations;
        let icon;

        let color = this.markerColors.shift();
        this.doiColor[doi] = color;

        _.forEach(locations, function(loc) {
            icon = self.getIcon(color);
            let marker = new google.maps.Marker({
                position: {lat: loc.lat, lng: loc.long},
                icon: icon
            });
            marker.setMap(self.map);
            if (self.doiMarkers[doi] === undefined) {
                self.doiMarkers[doi] = [];
            }
            self.doiMarkers[doi].push(marker);
        });
        return icon.fillColor;
    };

    this.hideMarkersForDOI = function(doi) {
        this.stateEntriesShown--;
        this.markerColors.unshift(this.doiColor[doi]);
        _.forEach(this.doiMarkers[doi], function(marker) {
            marker.setMap(null);
        });
    };

    this.toogleGoogleMapVisibility = function(elemShowMapLink) {
        let numberShown = $("a.worldmap-link:contains('Hide')").length;
        if (numberShown > 0) {
            this.showGoogleMap();
        } else {
            if (this.stateShowAllMarkers === false) {
                this.hideGoogleMap();
            }
        }

        let pos = elemShowMapLink.parent().position();

        setTimeout(function(){
            let isInViewport = $(elemShowMapLink.parent()).isInViewport();
            var currScrollPos = $('div.dataTables_scrollBody').scrollTop();
            if (!isInViewport) {
                $('div.dataTables_scrollBody').scrollTop(currScrollPos+300);
            }
        }, 100);
    };

    this.showGoogleMap = function() {
        $('#map-container').show();
        $('#grid-container-table-and-map').css('grid-template-rows', '1fr 300px');
    };

    this.hideGoogleMap = function() {
        if (this.stateEntriesShown === 0) {
            $('#map-container').hide();
            $('#grid-container-table-and-map').css('grid-template-rows', '1fr');
        }
    };

    this.niceBytes = function(x) {
        const units = ['bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
        let l = 0, n = parseInt(x, 10) || 0;
        while(n >= 1024 && ++l)
            n = n/1024;
        return(n.toFixed(n >= 10 || l < 1 ? 0 : 1) + ' ' + units[l]);
    };

    this.getIcon = function(color) {
        if (color === undefined || color === null || color === '') {
            color = '#000000';
        }
        return {
            path: "M-20,0a20,20 0 1,0 40,0a20,20 0 1,0 -40,0",
            fillColor: color,
            fillOpacity: 1,
            anchor: new google.maps.Point(0,0),
            strokeWeight: 0,
            scale: 0.17
        }
    }
}
