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
    this.ID = 0;

    /*  For REST-ExtendedSearch
        Const fields needed to access the proper indexed fields for the search  */
    const TITLE = "title";
    const DESCRIPTION = "description";
    const COVERAGE = "coverage";
    const IDENTIFIER = "identifier";
    const RELATEDIDENTIFIERTYPE = "relatedIdentifierType";
    const RELATIONTYPE = "relationType";
    const SIZE = "size";
    const TYPE = "type";
    const LANGUAGE = "language";
    const ALGORITHM = "algorithm";
    const CHECKSUM = "checkSum";
    const VERSIONID = "versionID";
    const SUBJECT = "subject";
    const RELATION = "relation";
    const MIMETYPE = "mimeType";
    const STARTDATE = "startDate";
    const ENDDATE = "endDate";
    const PERSON = "person";
    const LEGALPERSON = "legalPerson";

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
            var dataArray = self.datatable.rows({ search: 'applied' }).data().toArray();
            var totalAccesses = 0;
            var totalDownloadVolume = 0;
            dataArray.forEach((item) => {
              var accesses = parseInt(item.accesses);
              if(!isNaN(accesses)){
                totalAccesses += accesses;
              }
              var vol = parseInt(item.downloads);
              if(!isNaN(vol)){
                totalDownloadVolume += parseInt(item.downloads);
              }
            });
            document.getElementById("statisticsSpan").innerHTML = "DOIs: "+dataArray.length+" - distinct client IP addresses: "+totalAccesses+" - download volume: "+self.niceBytes(totalDownloadVolume);
        });

        $(document).on('keyup', '#edal-report-search', function(event) {
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
                      var totalAccesses = 0;
                      var totalDownloadVolume = 0;
                      self.reportData.forEach((item) => {
                        var accesses = parseInt(item.accesses);
                        if(!isNaN(accesses)){
                          totalAccesses += accesses;
                        }
                        var vol = parseInt(item.downloads);
                        if(!isNaN(vol)){
                          totalDownloadVolume += parseInt(item.downloads);
                        }
                      });
                      document.getElementById("statisticsSpan").innerHTML = "DOIs: "+self.reportData.length+" - distinct client IP addresses: "+totalAccesses+" - download volume: "+self.niceBytes(totalDownloadVolume);
                  } else {
                      //self.datatable.search(searchword).columns(5).search(self.yearFilter).draw();
                      //self.reportData = $.get("http://bit-58.ipk-gatersleben.de/rest/keywordsearch/"+self.yearFilter);
                      self.ID++;
                      var requestId = self.ID;
                      $.get( serverURL+"/rest/keywordsearch/"+self.yearFilter, function( data ) {
                          self.reportData = data;
                          console.log("Data:")
                          console.log(self.reportData);
                          if(self.ID == requestId){
                            self.datatable.destroy();
                            self.renderDatatable();
                            var totalAccesses = 0;
                            var totalDownloadVolume = 0;
                            console.log("stringContent: "+data[1].downloads);
                            self.reportData.forEach((item) => {
                              var accesses = parseInt(item.accesses);
                              if(!isNaN(accesses)){
                                totalAccesses += accesses;
                              }
                              var vol = parseInt(item.downloads);
                              if(!isNaN(vol)){
                                totalDownloadVolume += parseInt(item.downloads);
                              }
                            });
                            document.getElementById("statisticsSpan").innerHTML = "DOIs: "+self.reportData.length+" - distinct client IP addresses: "+totalAccesses+" - download volume: "+self.niceBytes(totalDownloadVolume);
                        }
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
                      var totalAccesses = 0;
                      var totalDownloadVolume = 0;
                      self.reportData.forEach((item) => {
                        var accesses = parseInt(item.accesses);
                        if(!isNaN(accesses)){
                          totalAccesses += accesses;
                        }
                        var vol = parseInt(item.downloads);
                        if(!isNaN(vol)){
                          totalDownloadVolume += parseInt(item.downloads);
                        }
                        console.log("totalVolume: "+totalDownloadVolume)
                      });
                      document.getElementById("statisticsSpan").innerHTML = "DOIs: "+self.reportData.length+" - distinct client IP addresses: "+totalAccesses+" - download volume: "+self.niceBytes(totalDownloadVolume);
                  } else {
                      self.ID++;
                      var requestId = self.ID;
                      // $.get( serverURL+"/rest/keywordsearch/"+searchword, function( data ) {
                      //     self.reportData = data;
                      //     console.log("ID: "+requestId+"Data:")
                      //     console.log(self.reportData);
                      //     if(self.ID == requestId){
                      //       self.datatable.destroy();
                      //       self.renderDatatable();
                      //       var totalAccesses = 0;
                      //       var totalDownloadVolume = 0;
                      //       console.log("stringContent: "+data[1].downloads);
                      //       self.reportData.forEach((item) => {
                      //         var accesses = parseInt(item.accesses);
                      //         if(!isNaN(accesses)){
                      //           totalAccesses += accesses;
                      //         }
                      //         var vol = parseInt(item.downloads);
                      //         if(!isNaN(vol)){
                      //           totalDownloadVolume += parseInt(item.downloads);
                      //         }
                      //       });
                      //       document.getElementById("statisticsSpan").innerHTML = "DOIs: "+self.reportData.length+" - distinct client IP addresses: "+totalAccesses+" - download volume: "+self.niceBytes(totalDownloadVolume);
                      //   }
                      // });

                      // var arr = {"hitType":"both","groups":
                      //   [ {"goup":
                      //     [
                      //         {"type":"person1","value":"someTerm1","fuzzy":false,"Occur":"must"},
                      //         {"type":"person2","value":"someTerm2","fuzzy":true,"Occur":"must"},
                      //         {"type":"person3","value":"someTerm3","fuzzy":false,"Occur":"should"}
                      //       ],
                      //       "Occur":""
                      //     },
                      //     {"goup":
                      //       [
                      //         {"type":"person1","value":"someTerm1","fuzzy":false,"Occur":"must"},
                      //         {"type":"person2","value":"someTerm2","fuzzy":true,"Occur":"must"},
                      //         {"type":"person3","value":"someTerm3","fuzzy":false,"Occur":"should"}
                      //       ],
                      //       "Occur":""
                      //     }
                      // ]};

                      var arr = {"hitType":"public","groups":
                        [ {"goup":
                          [
                              {"type":PERSON,"value":"Arend","fuzzy":false,"Occur":"MUST"},
                              {"type":STARTDATE,"lower":"01-09-2014","upper":"01-10-2014","fuzzy":false,"Occur":"MUST"}
                            ],
                            "Occur":"MUST"
                          }
                      ]};
                      $.post(serverURL+"/rest/extendedSearch/search", JSON.stringify(arr), function(data){
                        self.reportData = data;
                        console.log("ID: "+requestId+"Data:")
                        console.log(self.reportData);
                        if(self.ID == requestId){
                          self.datatable.destroy();
                          self.renderDatatable();
                          var totalAccesses = 0;
                          var totalDownloadVolume = 0;
                          console.log("stringContent: "+data[1].downloads);
                          self.reportData.forEach((item) => {
                            var accesses = parseInt(item.accesses);
                            if(!isNaN(accesses)){
                              totalAccesses += accesses;
                            }
                            var vol = parseInt(item.downloads);
                            if(!isNaN(vol)){
                              totalDownloadVolume += parseInt(item.downloads);
                            }
                          });
                          document.getElementById("statisticsSpan").innerHTML = "DOIs: "+self.reportData.length+" - distinct client IP addresses: "+totalAccesses+" - download volume: "+self.niceBytes(totalDownloadVolume);
                      }
                      });
                  }
              }
        });

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

    this.niceBytes = function(bytes) {
      const thresh = 1024;
      const dp = 1;
      if (Math.abs(bytes) < thresh) {
        return bytes + ' B';
      }

      const units = ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
      let u = -1;
      const r = 10**dp;

      do {
        bytes /= thresh;
        ++u;
      } while (Math.round(Math.abs(bytes) * r) / r >= thresh && u < units.length - 1);

      return bytes.toFixed(dp).replace(".", ",") + ' ' + units[u];
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
