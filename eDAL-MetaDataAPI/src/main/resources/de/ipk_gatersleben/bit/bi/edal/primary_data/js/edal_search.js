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
    this.bottomResultId = null;
    this.previousBottomResultId = null;
    this.rowHeight = 35;
    this.pageSize = Math.floor((document.getElementById("table-column").clientHeight - 3*this.rowHeight) / this.rowHeight);
    this.filters = [];
    this.hitSize = 0;
    this.pageNumbers = 0;
    this.currentRequestData = {};
    this.myModal = document.getElementById("myModal");
    this.terms = {"Creator":[], "Contributor":[], "Subject":[], "Title":[], "Description":[]};
    this.creators;
    this.contributors;
    this.subjects;
    this.titles;
    this.descriptions;


    this.ulDummy = document.createElement("ul");
    this.ulDummy.classList.add("list-group");
    this.ulDummy.style.maxHeight = "250px";
    this.ulDummy.style.overflowY = "auto";
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
    const CONTRIBUTOR = "Contributor";

    this.build = function(){
      let self = this;
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
        let requestData = { "hitType":document.getElementById("hitType").value, "filters":self.filters, "existingQuery":document.getElementById('query').value };
        self.resetTermList();
      });
    }

    this.facetedTerms = async function(){
      let self = this;
      self.countFacetedTerms(PERSON, self.creators, "creatorButton");
      self.countFacetedTerms(CONTRIBUTOR, self.contributors, "contributoButton");
      self.countFacetedTerms(SUBJECT, self.subjects, "subjectButton");
      self.countFacetedTerms(TITLE, self.titles, "titleButton");
      self.countFacetedTerms(DESCRIPTION, self.descriptions, "descriptionButton");
    }

    this.search = function(){
      let self = this;
      var queryValue = document.getElementById('query').value;
      if(queryValue == ""){
        self.facetedTerms();
        return;
      }
      document.getElementById("loading-indicator").style.display="block";
      ID++;
      let requestId = ID;
      let requestData = { "hitType":document.getElementById("hitType").value, "existingQuery":queryValue, "filters":this.filters, "bottomResultId":this.bottomResultId, "pageSize":this.pageSize,"pageIndex":0,"pagination":[], "pageArraySize":0,"displayedPage":1 };
      self.currentRequestData = requestData;
      $.post("/rest/extendedSearch/search", JSON.stringify(requestData), function(data){
      reportData = data.results;
      if(ID == requestId){
        document.getElementById("query").value = data["parsedQuery"];
        self.currentRequestData.existingQuery = data["parsedQuery"];
        self.facetedTerms();
        var currentPage = 0;
        var history = data.pageArray;
        self.hitSize = data.hitSizeDescription+' '+data.hitSize;
        self.pageNumbers = Math.ceil(data.hitSize/self.pageSize);
        self.reportData = data.results;
        self.datatable.destroy();
        var tableid = "#report";
        $(tableid + " tbody").empty();
        $(tableid + " thead").empty();
        console.log("pagearray");
        console.log(data.pageArray);
        if(requestData.hitType == "rootDirectory"){
          self.renderDatatableReports();
        }else if(requestData.hitType == "singleData"){
          self.renderDatatableFiles();
        }else if(requestData.hitType == "directory"){
          self.renderDatatableDirectories();
        }
        self.manipulateDataTable(data.pageArray, self.currentRequestData, history);
        document.getElementById("loading-indicator").style.display="none";
        if (typeof data.results !== 'undefined'){
            document.getElementById("result-stats").innerHTML = 'Showing 1 to '+data.results.length+' of ' +self.hitSize+' results';
        }
      }
      });
    }

    this.countFacetedTerms = async function(type, terms, btnName){
      let self = this;
      var request = {"termType":type, "terms":terms, "requestData":self.currentRequestData};
      let requestId = ID;
      document.getElementById(btnName).innerHTML = "0 elements";
      $.post("/rest/extendedSearch/countHits2", JSON.stringify(request), function(data){
        if(requestId == ID){
          var tempList = [];
          for(i = 0; i < data.length; i++){
            if(data[i] > 0){
              tempList.push([terms[i],data[i]]);
            }
          }
          self.terms[type] = tempList;
          //visual animation for the number of available faceted terms
          const el = document.getElementById(btnName);
          animateCountUp(el, tempList.length);
        }
      });
    }

    this.loadTerms = async function(currentPageNo, type, unorderedList, list){
      let self = this;
      const startOption = ((currentPageNo - 1) * 5); //for example if pageNo is 2 then startOption = (2-1)*10 + 1 = 11
      const upperBound = startOption + 5;
      const endOption = upperBound < list.length ? upperBound : list.length;//for example if pageNo is 2 then endOption = 11 + 10 = 21
      const slice = list.slice(startOption, endOption);
      var request = {"termType":type, "terms":slice, "requestData":self.currentRequestData};
      console.log("loadingSubjectTerms with page: "+currentPageNo+" start "+startOption+" end_ "+endOption+" length of array: "+list.length);
      console.log(slice);
      var terms = self.terms[type];
      if(terms.length > 0){
        for (i = 0; i < terms.length; i++) {
          var li = document.createElement("li");
          li.classList.add("list-group-item", "d-flex", "justify-content-between", "align-items-center", "liHover");
          li.innerHTML = terms[i][0]+'<span class="badge badge-primary badge-pill">'+terms[i][1]+'</span>';
          const term = terms[i][0];
          const value = terms[i];
          li.onclick = function(){
            var searchInput = document.getElementById("query");
            if(!searchInput.value){
              searchInput.classList.add("x");
            }
            let obj = {
              "type":type,
              "searchterm":term,
              "occur":"MUST",
              "fuzzy":false
            }
            let requestData = { "existingQuery":document.getElementById('query').value, "newQuery":obj };
            $.post(serverURL+"/rest/extendedSearch/parsequery", JSON.stringify(requestData), function(data){
              document.getElementById("query").value = "";
              document.getElementById("query").value = data;
              let requestData = { "hitType":document.getElementById("hitType").value, "filters":self.filters, "existingQuery":document.getElementById('query').value };
              self.search();
              document.getElementById("myModal").style.display = "none";
            });
          }
          unorderedList.appendChild(li);
        }
      }else{
        $.post("/rest/extendedSearch/countHits2", JSON.stringify(request), function(data){
          console.log("returned data: ");
          console.log(data);
          for (i = 0; i < data.length; i++) {
            if(data[i] == 0){
              continue;
            }
            var li = document.createElement("li");
            li.classList.add("list-group-item", "d-flex", "justify-content-between", "align-items-center", "liHover");
            li.innerHTML = slice[i]+'<span class="badge badge-primary badge-pill">'+data[i]+'</span>';
            const term = slice[i];
            li.onclick = function(){
              var searchInput = document.getElementById("query");
              if(!searchInput.value){
                searchInput.classList.add("x");
              }
              let obj = {
                "type":type,
                "searchterm":term,
                "occur":"MUST",
                "fuzzy":false
              }
              let requestData = { "existingQuery":document.getElementById('query').value, "newQuery":obj };
              $.post(serverURL+"/rest/extendedSearch/parsequery", JSON.stringify(requestData), function(data){
                document.getElementById("query").value = "";
                document.getElementById("query").value = data;
                let requestData = { "hitType":document.getElementById("hitType").value, "filters":self.filters, "existingQuery":document.getElementById('query').value };
                self.search();
                document.getElementById("myModal").style.display = "none";
              });
            }
            unorderedList.appendChild(li);
          }
          const incrementedPageNo = ++currentPageNo;
          if(document.getElementById("myModal").style.display == "flex" && ((currentPageNo - 1) * 5) < list.length){
            self.loadTerms(incrementedPageNo, type, unorderedList, list);
          }
        });
      }
    }

    this.listCreatorTerms = function(){
      let self = this;
      self.currentRequestData["hitType"] = document.getElementById("hitType").value;
      self.currentRequestData["filters"] = self.filters;
      document.getElementById("modal-headline").innerHTML = "Creators";
      var ulCreator = self.ulDummy.cloneNode(false);
      self.loadTerms(1,PERSON, ulCreator, self.creators);
      document.getElementById("myModal").style.display = "flex";
      var modalList = document.getElementById("modal-list");
      modalList.innerHTML = "";
      document.getElementById("modal-list").appendChild(ulCreator);
    }

    this.listContributorTerms = function(){
      let self = this;
      self.currentRequestData["hitType"] = document.getElementById("hitType").value;
      self.currentRequestData["filters"] = self.filters;
      document.getElementById("modal-headline").innerHTML = "Contributors";
      var ulContributor = self.ulDummy.cloneNode(false);
      self.loadTerms(1,CONTRIBUTOR, ulContributor, self.contributors);
      document.getElementById("myModal").style.display = "flex";
      var modalList = document.getElementById("modal-list");
      modalList.innerHTML = "";
      document.getElementById("modal-list").appendChild(ulContributor);
    }

    this.listSubjectTerms = function(){
      let self = this;
      self.currentRequestData["hitType"] = document.getElementById("hitType").value;
      self.currentRequestData["filters"] = self.filters;
      document.getElementById("modal-headline").innerHTML = "Subjects";
      var ulSubjects = self.ulDummy.cloneNode(false);
      self.loadTerms(1,SUBJECT, ulSubjects, self.subjects);
      document.getElementById("myModal").style.display = "flex";
      var modalList = document.getElementById("modal-list");
      modalList.innerHTML = "";
      document.getElementById("modal-list").appendChild(ulSubjects);
    }

    this.listTitleTerms = function(){
      let self = this;
      self.currentRequestData["hitType"] = document.getElementById("hitType").value;
      self.currentRequestData["filters"] = self.filters;
      document.getElementById("modal-headline").innerHTML = "Titles";
      var ulTitles = self.ulDummy.cloneNode(false);
      self.loadTerms(1,TITLE, ulTitles, self.titles);
      document.getElementById("myModal").style.display = "flex";
      var modalList = document.getElementById("modal-list");
      modalList.innerHTML = "";
      document.getElementById("modal-list").appendChild(ulTitles);
    }

    this.listDescritionTerms = function(){
      let self = this;
      self.currentRequestData["hitType"] = document.getElementById("hitType").value;
      self.currentRequestData["filters"] = self.filters;
      document.getElementById("modal-headline").innerHTML = "Descriptions";
      var ulDescriptoions = self.ulDummy.cloneNode(false);
      self.loadTerms(1,DESCRIPTION, ulDescriptoions, self.descriptions);
      document.getElementById("myModal").style.display = "flex";
      var modalList = document.getElementById("modal-list");
      modalList.innerHTML = "";
      document.getElementById("modal-list").appendChild(ulDescriptoions);
    }

    this.testCount = function(term){
      $.post("/rest/extendedSearch/countHits2", term, function(data){
        console.log(data);
      });
    }


        // this.loadContributorTerms = async function(currentPageNo, type){
        //   let self = this;
        //   const startOption = ((currentPageNo - 1) * 5); //for example if pageNo is 2 then startOption = (2-1)*10 + 1 = 11
        //   const upperBound = startOption + 5;
        //   const endOption = upperBound < subjects.length ? upperBound : subjects.length;//for example if pageNo is 2 then endOption = 11 + 10 = 21
        //   const slice = subjects.slice(startOption, endOption);
        //   var request = {"termType":type, "terms":slice};
        //   console.log("loadingSubjectTerms with page: "+currentPageNo+" start "+startOption+" end_ "+endOption);
        //   console.log(slice);
        //   $.post("/rest/extendedSearch/countHits2", JSON.stringify(request), function(data){
        //     console.log("returned data: ");
        //     console.log(data);
        //     for (i = 0; i < data.length; i++) {
        //         var li = document.createElement("li");
        //         li.classList.add("list-group-item", "d-flex", "justify-content-between", "align-items-center", "liHover");
        //         li.innerHTML = slice[i]+'<span class="badge badge-primary badge-pill">'+data[i]+'</span>';
        //         self.ulContributor.appendChild(li);
        //         console.log(slice[i]+" "+data[i]);
        //     }
        //     const incrementedPageNo = ++currentPageNo;
        //     if(((currentPageNo - 1) * 5) < subjects.length){
        //       self.loadContributorTerms(incrementedPageNo, type);
        //     }
        //   });
        // }

        // this.loadSubjectTerms = async function(currentPageNo, type){
        //   let self = this;
        //   const startOption = ((currentPageNo - 1) * 5); //for example if pageNo is 2 then startOption = (2-1)*10 + 1 = 11
        //   const upperBound = startOption + 5;
        //   const endOption = upperBound < subjects.length ? upperBound : subjects.length;//for example if pageNo is 2 then endOption = 11 + 10 = 21
        //   const slice = subjects.slice(startOption, endOption);
        //   var request = {"termType":type, "terms":slice};
        //   console.log("loadingSubjectTerms with page: "+currentPageNo+" start "+startOption+" end_ "+endOption);
        //   console.log(slice);
        //   $.post("/rest/extendedSearch/countHits2", JSON.stringify(request), function(data){
        //     console.log("returned data: ");
        //     console.log(data);
        //     for (i = 0; i < data.length; i++) {
        //         var li = document.createElement("li");
        //         li.classList.add("list-group-item", "d-flex", "justify-content-between", "align-items-center", "liHover");
        //         li.innerHTML = slice[i]+'<span class="badge badge-primary badge-pill">'+data[i]+'</span>';
        //         self.ulSubjects.appendChild(li);
        //         console.log(slice[i]+" "+data[i]);
        //     }
        //     const incrementedPageNo = ++currentPageNo;
        //     if(((currentPageNo - 1) * 5) < subjects.length){
        //       self.loadSubjectTerms(incrementedPageNo, type);
        //     }
        //   });
        // }

    this.changePage = function(index, page, currentRequestData, history){
      document.getElementById("loading-indicator").style.display="block";
      let self = this;
      ID++;
      let requestId = ID;
      currentRequestData["bottomResultId"] = history[index].bottomResult;
      currentRequestData["bottomResultScore"] = history[index].bottomResultScore;
      currentRequestData["pageIndex"] = index;
      currentRequestData["pageArraySize"] = history.length;
      currentRequestData["displayedPage"] = page;
      console.log(currentRequestData);
      $.post("/rest/extendedSearch/search", JSON.stringify(currentRequestData), function(data){
      reportData = data.results;
      if(ID == requestId){
        self.reportData = data.results;
        var currentPage = index+1;
        self.pageNumbers = Math.ceil(data.hitSize/self.pageSize);
        if (typeof data.results !== 'undefined'){
          document.getElementById("result-stats").innerHTML = 'Showing '+(index*self.pageSize+1)+' to '+(index*self.pageSize+data.results.length)+' of ' +self.hitSize+' results';
        }
        var pageArray = [];
        var offset;
        if(history[index].page < 7){
          i = 0;
        }else if(index > 5){
          i = index-5;
          if(data.pageArray.length > 0){
            for(j = 0; j < data.pageArray.length; j++){
              history.push(data.pageArray[j]);
            }
          offset = index+4;
          console.log("index= "+index+"offset= "+offset+" history.length_"+history.length);
          if(offset > history.length){
            i -= offset-history.length;
            if(history.length > 10){
              i--;
            }
            console.log("index adjusted to: "+i);
          }
          }
        }else{
          alert("bummer: index < 5 und page >= 7");
        }
        var j = 0;
        var sum = i+j;
        while(j < 10 && sum < history.length){
          pageArray.push(history[sum]);
          j++;
          sum = i+j;
        }
        self.datatable.destroy();
        var tableid = "#report";
        $(tableid + " tbody").empty();
        $(tableid + " thead").empty();
        if(currentRequestData.hitType == "rootDirectory"){
          self.renderDatatableReports();
        }else if(currentRequestData.hitType == "singleData"){
          self.renderDatatableFiles();
        }else if(currentRequestData.hitType == "directory"){
          self.renderDatatableDirectories();
        }
        document.getElementById("loading-indicator").style.display="none";
        self.manipulateDataTable(pageArray, self.currentRequestData, history);
      }
      });
    }

    this.filterChange = function(){
      this.filters = [];
      let periodbox = document.getElementById("period");
      this.currentRequestData["hitType"] = document.getElementById("hitType").value;
      if(periodbox.checked){
        let lowbound = document.getElementById("datepickerlow").value;
        let highbound = document.getElementById("datepickerhigh").value;
        if(lowbound != "" && highbound != ""){
          this.filters.push({"type":STARTDATE,"lower":lowbound.replaceAll('/', '-'),"upper":highbound.replaceAll('/', '-'),"fuzzy":false,"Occur":"And"});
        }
      }
      let filesize = document.getElementById("filesize");
      if(filesize.checked){
        let lowbound = parseInt(document.getElementById("filesizelow").value);
        let highbound = parseInt(document.getElementById("filesizehigh").value);
        let lowerSize = document.getElementById("filesizelowbyte").value;
        let higherSize = document.getElementById("filesizehighbyte").value;
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
      this.search();
    }

    // this.queryChange = function(){
    //   alert("queryChange");
    //   let self = this;
    //   ID++;
    //   let requestId = ID;
    //   let requestData = { "hitType":document.getElementById("hitType").value, "existingQuery":document.getElementById('query').value, "filters":self.filters };
    //   $.post("/rest/extendedSearch/countHits", JSON.stringify(requestData), function(data){
    //     if(ID == requestId){
    //       this.query = data;
    //     }
    //   });
    // };

    this.init = function(reportData, mapData) {
        this.initReportData = this.reportData = reportData;
        this.mapData = mapData;
        this.reportDataKeyed = _.keyBy(reportData, 'doi');
        this.allYears = _.uniq(_.map(reportData, 'year')).sort().reverse();
        let self = this;
        if(self.pageSize > 5){
          console.log("height > 5 :"+self.pageSize+" getting subtracted by "+self.pageSize%5);
          self.pageSize = self.pageSize - self.pageSize%5;
          console.log("new height :"+self.pageSize);
        }
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
            self.resetTermList();
            self.renderYearSelectOptions();
            self.renderDatatableReports();
            self.manipulateDataTable([],null,[]);
            self.addObservers();
        });
    };

    this.resetTermList = function(){
      let self = this;
      self.currentRequestData.existingQuery = document.getElementById("query").value;
      self.currentRequestData.filters = self.filters;
      self.currentRequestData.hitType = document.getElementById("hitType").value;
      $.post("/rest/extendedSearch/getTermLists", function(data){
        self.creators = data[PERSON];
        self.contributors = data[CONTRIBUTOR];
        self.subjects = data[SUBJECT];
        self.titles = data[TITLE];
        self.descriptions = data[DESCRIPTION];
        self.facetedTerms();
      });
    }

    this.renderYearSelectOptions = async function() {
        let selectElem = $('#edal-report-year-filter');
        _.forEach(this.allYears, function(year) {
            selectElem.append('<option value="'+year+'">'+year+'</a>&nbsp;');
        });
    }

    this.renderDatatableFiles = function() {
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
          "pageLength": self.pageSize,
          info: false,
          "order": [],
          "language": {
            "emptyTable": ""
          },
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
    };

    this.renderDatatableDirectories = function() {
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
          "pageLength": self.pageSize,
          info: false,
          "order": [],
          "language": {
            "emptyTable": ""
          },
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
    };

    this.manipulateDataTable = function(numbers, currentRequestData, history){
      if(typeof numbers == 'undefined' || numbers.length == 0){
        return;
      }
      console.log("number array when creating pagination ");
      console.log(numbers);
      let self = this;
      self.currentRequestData = currentRequestData;
      var currentPage = currentRequestData.displayedPage;
      var currentIndex = currentRequestData.pageIndex;
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
      var currentSelectedLi = document.getElementById("page"+history[currentIndex].page).classList.add("active");
      for(i = 0; i < numbers.length; i++){
        if(history[currentIndex].page != numbers[i].page){
          const index = i;
          console.log("numbers["+index+"].page == "+numbers[index].page);
          document.getElementById("page"+numbers[index].page).onclick = function(){
            self.changePage(numbers[index].index, numbers[index].page, currentRequestData, history);
          }
        }
      }
      if(currentIndex+1 < history.length){
        var nextBtn = document.getElementById("btn_next");
        nextBtn.classList.remove("disabled");
        nextBtn.onclick = function(){
          var nextIndex = currentIndex+1;
          var nextPage = currentPage+1;
          console.log("nextIndex = "+nextIndex+" nextpage="+nextPage)
          self.changePage(nextIndex, nextPage, currentRequestData, history);
        };
      }
      if(currentIndex-1 > -1){
        var nextBtn = document.getElementById("btn_previous");
        nextBtn.classList.remove("disabled");
        nextBtn.onclick = function(){
          var prevIndex = currentIndex-1;
          var prevPage = currentPage-1;
          console.log("prevIndex = "+prevIndex+" prevpage="+prevPage)
          self.changePage(prevIndex, prevPage, currentRequestData, history);
        };
      }
    }

    this.renderDatatableReports = function() {
      $('tr').css('height','10px');
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
            "pageLength": self.pageSize,
            info: false,
            "order": [],
            "language": {
              "emptyTable": ""
            },
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
                        return '<a href="http://dx.doi.org/'+data+'" target="_blank">'+data+'</a>';
                    }
                },
                {
                    title: "Record",
                    data: "title",
                    class: "edal-report-title"
                }
            ]
        });
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
