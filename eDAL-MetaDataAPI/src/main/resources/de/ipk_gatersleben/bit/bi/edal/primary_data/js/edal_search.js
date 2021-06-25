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
    this.searchID = 0;
    this.buildID = 0;
    this.query = "";
    this.bottomResultId = null;
    this.previousBottomResultId = null;
    this.pageSize = 15;
    this.filters = [];
    this.hitSize = 0;
    this.pageNumbers = 0;
    this.currentRequestData = {};
    let searchTerms = [];
    let ID = 0;
    let reportData = null;

    /*  For REST-ExtendedSearch
        Const fields needed to access the proper indexed fields for the search  */
    const TITLE = "Title";
  	const DESCRIPTION = "Description";
  	const COVERAGE = "Coverage";
  	const IDENTIFIER = "Identifier";
  	const RELATEDIDENTIFIERTYPE = "RelatedIdentifierType";
  	const RELATIONTYPE = "RelationType";
  	const SIZE = "Size";
  	const TYPE = "Type";
  	const LANGUAGE = "Language";
  	const ALGORITHM = "Algorithm";
  	const CHECKSUM = "Checksum";
  	const VERSIONID = "versionID";
  	const SUBJECT = "Subject";
  	const RELATION = "Relation";
  	const MIMETYPE = "Mimetype";
  	const STARTDATE = "Startdate";
  	const ENDDATE = "Enddate";
  	const PERSON = "Creator";
  	const LEGALPERSON = "Legalperson";
    const FILETYPE = "Filetype";

    this.build = function(){
      if(document.getElementById("searchterm").value == ""){
        console.log("no val in searchterm");
        return;
      }
      var searchInput = document.getElementById("query");
      if(!searchInput.value){
        searchInput.classList.add("x");
      }
      let obj = {
        "type":document.getElementById("element").value,
        "searchterm":document.getElementById("searchterm").value,
        "occur":document.getElementById("occur").value,
        "fuzzy":document.getElementById("fuzzy").checked
      }
      document.getElementById('searchterm').value = '';

      let requestData = { "existingQuery":document.getElementById('query').value, "newQuery":obj };
      $.post(serverURL+"/rest/extendedSearch/parsequery", JSON.stringify(requestData), function(data){
        console.log("data:");
        console.log(data);
        document.getElementById("query").value = "";
        document.getElementById("query").value = data;
      });
    }

    this.search = function(){
      document.getElementById("loading-indicator").style.display="block";
      let self = this;
      ID++;
      let requestId = ID;
      let requestData = { "hitType":document.getElementById("hitType").value, "existingQuery":document.getElementById('query').value, "filters":this.filters, "bottomResultId":this.bottomResultId, "pageSize":this.pageSize,"pageIndex":0,"pagination":[] };
      console.log(requestData);
      self.currentRequestData = requestData;

      $.post("/rest/extendedSearch/search", JSON.stringify(requestData), function(data){
      reportData = data.results;
      console.log("ID: "+requestId+"Data:")
      console.log(self.datatable);
      if(ID == requestId){
        var currentPage = 0;
        var history = data.pageArray;
        self.hitSize = data.hitSize;
        self.pageNumbers = Math.ceil(data.hitSize/self.pageSize);
        self.hitSize = data.hitSize;
        document.getElementById("result-stats").innerHTML = 'Page 1 of '+data.hitSizeDescription+' '+data.hitSize+' results';
        self.reportData = data.results;
        self.datatable.destroy();
        var tableid = "#report";
        $(tableid + " tbody").empty();
        $(tableid + " thead").empty();
        console.log("pagearray");
        console.log(data.pageArray);
        if(requestData.hitType == "rootDirectory"){
          self.renderDatatableReports(history, currentPage);
        }else if(requestData.hitType == "singleData"){
          self.renderDatatableFiles(history, currentPage);
        }else if(requestData.hitType == "Directory"){
          self.renderDatatableDirectories(history, currentPage);
        }else{
          self.renderDatatableMixed(history, currentPage);
        }
        document.getElementById("loading-indicator").style.display="none";
        console.log("pageNumbers: "+self.pageNumbers);
        if(self.pageNumbers > 1){
          var nextBtn = document.getElementById("btn_next");
          nextBtn.classList.remove("disabled");
          console.log(history[0]);
          console.log(history[1]);
          console.log(currentPage);
          nextBtn.onclick = function(){
            self.next(self.currentRequestData, history, currentPage);
          };
        }
      }
      });
    }

    this.previous = function(requestData, history, currentPage){
      document.getElementById("loading-indicator").style.display="block";
      currentPage--;
      let self = this;
      ID++;
      let requestId = ID;
      requestData["bottomResultId"] = history[currentPage].bottomResult;
      requestData["bottomResultScore"] = history[currentPage].bottomResultScore;
      $.post("/rest/extendedSearch/search", JSON.stringify(this.currentRequestData), function(data){
      reportData = data.results;
      console.log("ID: "+requestId+"Data:")
      console.log(self.datatable);
      if(ID == requestId){
        self.pageNumbers = Math.ceil(data.hitSize/self.pageSize);
        document.getElementById("result-stats").innerHTML = 'Page '+(currentPage+1)+' of '+data.hitSizeDescription+' '+self.hitSize+' results';
        console.log(data);
        self.reportData = data.results;
        self.datatable.destroy();
        var tableid = "#report";
        $(tableid + " tbody").empty();
        $(tableid + " thead").empty();
        if(requestData.hitType == "rootDirectory"){
          self.renderDatatableReports();
        }else if(requestData.hitType == "singleData"){
          self.renderDatatableFiles();
        }else if(requestData.hitType == "Directory"){
          self.renderDatatableDirectories();
        }else{
          self.renderDatatableMixed();
        }
        document.getElementById("loading-indicator").style.display="none";
        var nextBtn = document.getElementById("btn_next");
        if(nextBtn.classList.contains("disabled")){
          nextBtn.classList.remove("disabled");
        }
        nextBtn.onclick = function(){
          self.next(self.currentRequestData, history, currentPage);
        };
        var prevBtn = document.getElementById("btn_previous");
        if(currentPage > 0){
          prevBtn.classList.remove("disabled");
          prevBtn.onclick = function(){
            self.previous(self.currentRequestData, history, currentPage);
          };
        }
      }
      });
    }

    this.next = function(requestData, history, currentPage){
      document.getElementById("loading-indicator").style.display="block";
      currentPage++;
      let self = this;
      ID++;
      let requestId = ID;
      requestData["bottomResultId"] = history[currentPage].bottomResult;
      requestData["bottomResultScore"] = history[currentPage].bottomResultScore;
      $.post("/rest/extendedSearch/search", JSON.stringify(this.currentRequestData), function(data){
      reportData = data.results;
      console.log("ID: "+requestId+"Data:")
      console.log(self.datatable);
      if(ID == requestId){
        console.log("currentPage:"+currentPage);
        console.log("selfhitSize:"+self.hitSize);
        self.pageNumbers = Math.ceil(data.hitSize/self.pageSize);
        document.getElementById("result-stats").innerHTML = 'Page '+(currentPage+1)+' of '+data.hitSizeDescription+' '+self.hitSize+' results';
        console.log(data);
        self.reportData = data.results;
        self.datatable.destroy();
        var tableid = "#report";
        $(tableid + " tbody").empty();
        $(tableid + " thead").empty();
        if(requestData.hitType == "rootDirectory"){
          self.renderDatatableReports();
        }else if(requestData.hitType == "singleData"){
          self.renderDatatableFiles();
        }else if(requestData.hitType == "Directory"){
          self.renderDatatableDirectories();
        }else{
          self.renderDatatableMixed();
        }
        document.getElementById("loading-indicator").style.display="none";
        var nextBtn = document.getElementById("btn_next");
        if(self.pageNumbers > 1){
          nextBtn.classList.remove("disabled");
          history.push({"bottomResult":data.bottomResult, "bottomResultScore":data.bottomResultScore});
          nextBtn.onclick = function(){
            self.next(self.currentRequestData, history, currentPage);
          };
        }
        var prevBtn = document.getElementById("btn_previous");
        prevBtn.classList.remove("disabled");
        prevBtn.onclick = function(){
          self.previous(self.currentRequestData, history, currentPage);
        };
      }
      });
    }

    this.filterChange = function(){
      this.filters = [];
      let periodbox = document.getElementById("period");
      if(periodbox.checked){
        let lowbound = document.getElementById("datepickerlow").value;
        let highbound = document.getElementById("datepickerhigh").value;
        if(lowbound != "" && highbound != ""){
          console.log("adding dates");
          this.filters.push({"type":STARTDATE,"lower":lowbound.replaceAll('/', '-'),"upper":highbound.replaceAll('/', '-'),"fuzzy":false,"Occur":"And"});
        }
      }
      let filesize = document.getElementById("filesize");
      if(filesize.checked){
        let lowbound = parseInt(document.getElementById("filesizelow").value);
        let highbound = parseInt(document.getElementById("filesizehigh").value);
        let lowerSize = document.getElementById("filesizelowbyte").value;
        let higherSize = document.getElementById("filesizehighbyte").value;
        console.log(reverseNiceBytes(lowbound,lowerSize));
        console.log(reverseNiceBytes(highbound,higherSize));
        if(lowbound != "" && highbound != ""){
          this.filters.push({"type":SIZE,"lower":reverseNiceBytes(lowbound,lowerSize),"upper":reverseNiceBytes(highbound,higherSize),"fuzzy":false,"Occur":"And"});
        }
      }
      let suffix = document.getElementById("suffix");
      if(suffix.checked){
        let suffixValue = document.getElementById("suffixesSelect").value;
        if(suffixValue != "")
          this.filters.push({"type":FILETYPE,"searchterm":suffixValue,"fuzzy":false,"Occur":"And"});
      }
    }

    this.queryChange = function(){
      let filters = [];
      ID++;
      let requestId = ID;
      let periodbox = document.getElementById("period");
      if(periodbox.checked){
        let lowbound = document.getElementById("datepickerlow").value;
        let highbound = document.getElementById("datepickerhigh").value;
        if(lowbound != "" && highbound != ""){
          console.log("adding dates");
          filters.push({"type":STARTDATE,"lower":lowbound.replaceAll('/', '-'),"upper":highbound.replaceAll('/', '-'),"fuzzy":false,"Occur":"And"});
        }
      }
      let filesize = document.getElementById("filesize");
      if(filesize.checked){
        let lowbound = parseInt(document.getElementById("filesizelow").value);
        let highbound = parseInt(document.getElementById("filesizehigh").value);
        let lowerSize = document.getElementById("filesizelowbyte").value;
        let higherSize = document.getElementById("filesizehighbyte").value;
        console.log(reverseNiceBytes(lowbound,lowerSize));
        console.log(reverseNiceBytes(highbound,higherSize));
        if(lowbound != "" && highbound != ""){
          filters.push({"type":SIZE,"lower":reverseNiceBytes(lowbound,lowerSize),"upper":reverseNiceBytes(highbound,higherSize),"fuzzy":false,"Occur":"And"});
        }
      }
      let suffix = document.getElementById("suffix");
      if(suffix.checked){
        let suffixValue = document.getElementById("suffixesSelect").value;
        if(suffixValue != "")
          filters.push({"type":FILETYPE,"searchterm":suffixValue,"fuzzy":false,"Occur":"And"});
      }
      let requestData = { "hitType":document.getElementById("hitType").value, "existingQuery":document.getElementById('query').value, "filters":filters };
      $.post("/rest/extendedSearch/countHits", JSON.stringify(requestData), function(data){
        if(ID == requestId){
          this.query = data;
          console.log(this.query);
        }
      });
    };

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
            self.renderDatatableReports([],-1);
            self.addObservers();
        });
    };

    this.renderYearSelectOptions = function() {
        let selectElem = $('#edal-report-year-filter');
        _.forEach(this.allYears, function(year) {
            selectElem.append('<option value="'+year+'">'+year+'</a>&nbsp;');
        });
    }

    this.renderDatatableFiles = function(numbers = [], currentPage) {
        let self = this;

        this.datatable = $('#report').DataTable({
          data: self.reportData,
          dom: 't',
          //dom: 'Bfrtip',
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
          "pagingType": "simple",
          "pageLength": 15,
          info: false,
          "order": [],
            columns: [
                {
                    title: "Filename",
                    width: "25%",
                    class: "edal-report-doi",
                    render: function (data, type, row) {
                        return '<a href="'+serverURL+'/DOI/'+row.doi+'" target="_blank">'+row.fileName+'</a>';
                    }
                },
                {
                    title: "Extension",
                    data: "ext",
                    class: "edal-report-title"
                },
                {
                    title: "Record",
                    data: "title",
                    class: "edal-report-title"
                }
            ]
        });
        self.manipulateDataTable(numbers, currentPage);
    };

    this.renderDatatableDirectories = function(numbers = [], currentPage) {
        let self = this;

        this.datatable = $('#report').DataTable({
          data: self.reportData,
          dom: 't',
          //dom: 'Bfrtip',
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
          "pagingType": "simple",
          "pageLength": 15,
          info: false,
          "order": [],
            columns: [
                {
                    title: "Directory",
                    width: "25%",
                    class: "edal-report-doi",
                    render: function (data, type, row) {
                        return '<a href="'+serverURL+'/DOI/'+row.doi+'" target="_blank">'+row.fileName+'</a>';
                    }
                },
                {
                    title: "Record",
                    data: "title",
                    class: "edal-report-title"
                }
            ]
        });
        self.manipulateDataTable(numbers, currentPage);
    };

    this.manipulateDataTable = function(numbers, currentPage){
      let self = this;
      function insertNumbers(numbers){
        var liNumbers = '';
        for(i = 0; i < numbers.length; i++){
          liNumbers += '<li class="page-item" id="page'+numbers[i].page+'"><a class="page-link" href="#">'+numbers[i].page+'</a></li>';
        }
        return liNumbers;
      }
      var div = document.createElement("div");
      div.classList.add("dataTables_paginate");
      div.classList.add("paging_simple");
      document.getElementById("report_wrapper").appendChild(div);
      div.innerHTML = '<ul class="pagination justify-content-center" id="ul_pagination"><li class="page-item disabled"  id="btn_previous"><a class="page-link" href="#" tabindex="-1">Previous</a></li>'+insertNumbers(numbers)+'<li class="page-item disabled" id="btn_next"><a class="page-link" href="#">Next</a></li></ul>';
      if(currentPage < 0){
        return;
      }
      console.log(numbers);
      console.log(typeof numbers);
      console.log("numbers.length: "+numbers.length);
      var currentSelectedLi = document.getElementById("page"+numbers[currentPage].page).classList.add("active");
      for(i = 1; i < numbers.length; i++){
        var index = i;
        document.getElementById("page"+numbers[index].page).onclick = function(){
          console.log("clicked index:_ ");
          console.log(index);
        }
      }
    }

    this.renderDatatableMixed = function(numbers = []) {
        let self = this;

        this.datatable = $('#report').DataTable({
          data: self.reportData,
          dom: 't',
          //dom: 'Bfrtip',
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
          "pagingType": "simple",
          "pageLength": 15,
          info: false,
          "order": [],
            columns: [
                {
                    title: "Title",
                    width: "25%",
                    class: "edal-report-doi",
                    render: function (data, type, row) {
                      if(row.type == "record"){
                        return '<a href="'+serverURL+'/DOI/'+data+'" target="_blank">'+data+'</a>';
                      }else{
                        return '<a href="'+serverURL+'/DOI/'+row.doi+'" target="_blank">'+row.fileName+'</a>';
                      }
                    }
                },
                {
                    title: "Type",
                    data: "type",
                    class: "edal-report-title"
                },
                {
                    title: "Extension",
                    data: "ext",
                    class: "edal-report-title"
                },
                {
                    title: "Record",
                    data: "title",
                    class: "edal-report-title"
                }
            ]
        });
        self.manipulateDataTable(numbers, currentPage);
    };

    this.renderDatatableReports = function(numbers = [], currentPage) {
        let self = this;

        this.datatable = $('#report').DataTable({
            data: self.reportData,
            dom: 't',
            //dom: 'Bfrtip',
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
            "pagingType": "simple",
            "pageLength": 15,
            info: false,
            "order": [],
            fixedColumns:   {
                heightMatch: 'none'
            },
            columns: [
                {
                    title: "DOI",
                    data: "doi",
                    width: "10%",
                    class: "edal-report-doi",
                    render: function (data, type, row) {
                        return '<a href="'+serverURL+'/DOI/'+data+'" target="_blank">'+data+'</a>';
                    }
                },
                {
                    title: "Record",
                    data: "title",
                    class: "edal-report-title"
                }
            ]
        });
        self.manipulateDataTable(numbers, currentPage);
    };

    this.typeChange = function() {
          //clear old recommendations
          var searchTerm = document.getElementById("searchterm");
          var dl = document.getElementById("auto-complete");
          dl.parentNode.removeChild(dl);
          //add new recommendations
          dl = document.createElement('datalist');
          dl.id="auto-complete";
          var type = document.getElementById("element").value;
          switch(type){
            case FILETYPE:
              for (i = 0; i < 8; i++) {
                console.log(filetypes[i]);
                var el = document.createElement("option");
                el.textContent = filetypes[i];
                dl.appendChild(el);
              }
              break;
            default:
          }
          searchTerm.appendChild(dl);
    }

    this.addObservers = function() {
        let self = this;

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
                      $.get( serverURL+"/rest/keywordsearch/"+searchword, function( data ) {
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

    function reverseNiceBytes(fileSize, unit){
      if(unit === 'B')
        return fileSize;
      let multiplier = 1024;
      const units = ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
      for(let i = 0; i < units.length; i++){
        fileSize = fileSize * 1024;
        if(units[i] === unit){
          break;
        }
      }
      console.log(fileSize);
      return fileSize;
    }
}
