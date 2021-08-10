let EdalReport = new function() {
    this.datatable = null;
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
    this.defaultFileType = "*";
    this.rangeSizeValues = [];
    this.units = ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    this.queries = [];


    this.ulDummy = document.createElement("ul");
    this.ulDummy.classList.add("list-group");
    this.ulDummy.style.maxHeight = "250px";
    this.ulDummy.style.overflowY = "auto";
    this.ulDummy.style.overflowX = "hidden";
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
      self.addQuery(obj);
    }

    this.facetedTerms = async function(){
      let self = this;
      self.countFacetedTerms(PERSON, self.creators, document.getElementById("CreatorUl"));
      self.countFacetedTerms(CONTRIBUTOR, self.contributors, document.getElementById("ContributorUl"));
      self.countFacetedTerms(SUBJECT, self.subjects, document.getElementById("SubjectUl"));
      self.countFacetedTerms(TITLE, self.titles, document.getElementById("TitleUl"));
      self.countFacetedTerms(DESCRIPTION, self.descriptions, document.getElementById("DescriptionUl"));
      self.loadFileExtensions();
    }

    this.loadFileExtensions = function(){
      let self = this;
      if(self.currentRequestData["hitType"] == "singleData"){
        var select = document.getElementById("suffixesSelect");
        select.innerHTML = "";
        var option = document.createElement("option");
        option.innerHTML = "*";
        select.appendChild(option);
        var request = {"termType":FILETYPE, "terms":filetypes, "requestData":self.currentRequestData};
        $.post("/rest/extendedSearch/countHits", JSON.stringify(request), function(data){
          data.sortedByNames.forEach((term, i) => {
            var opt = document.createElement("option");
            opt.value = term[0];
            opt.innerHTML = term[0] + " (" + term[1]+")";
            select.appendChild(opt);
            if(term[0] == self.defaultFileType){
              select.selectedIndex = ++i;
            }
          });
        });
      }
    }

    this.addQuery = function(query){
      let self = this;
      $.post(serverURL+"/rest/extendedSearch/parsequery2", JSON.stringify(query), function(data){
        if(data != ""){
          const index = self.queries.length;
          self.queries.push(data);
          self.addTab(data, index);
        }
        self.search();
      });
    }

    this.search = function(){
      let self = this;
      var queryValue = document.getElementById('query').value;
      document.getElementById("loading-indicator").style.display="inline-block";
      ID++;
      let requestId = ID;
      let requestData = { "hitType":document.querySelector('input[name = "hitType"]:checked').value, "existingQuery":queryValue, "filters":this.filters, "bottomResultId":this.bottomResultId, "pageSize":this.pageSize,"pageIndex":0,"pagination":[], "pageArraySize":0,"displayedPage":1, "queries":self.queries };
      self.currentRequestData = requestData;
      $.post("/rest/extendedSearch/search", JSON.stringify(requestData), function(data){
      reportData = data.results;
      if(ID == requestId){
        if(data.parsedQuery != null && data.parsedQuery != ""){
          const queryIndex = self.queries.length-1;
          self.queries.push(data.parsedQuery);
          self.addTab(data.parsedQuery,queryIndex);
        }
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
        }else{
            document.getElementById("result-stats").innerHTML = 'No results found';
        }
      }
      });
      $("#query").val("");
    }

    this.addTab = function(query, index){
      let self = this;
      let parent = document.getElementById("query-ul");
      const i = index;
      const text = query;
      let span = document.createElement("span");
      span.classList.add("btn", "btn-outline-dark", "shadow-none", "query-span","mx-1");
      span.innerHTML = text;
      let innerSpan = document.createElement("span");
      span.onmouseover = hover(innerSpan);
      span.onmouseleave = afterHover(innerSpan);
      innerSpan.classList.add("remove-query-btn", "ml-2");
      innerSpan.innerHTML = '&times';
      innerSpan.onclick = function(){
        console.log("trying to delete tab with index: "+i +" fro marray with_ elemnts "+self.queries.length);
        if(self.queries.length > 1){
          self.queries.splice(i,1);
        }else{
          self.queries = [];
        }
        parent.innerHTML = "";
        self.queries.forEach((item, j) => {
          const indx = j;
          self.addTab(item, indx);
        });
      }
      let li = document.createElement("li");
      span.appendChild(innerSpan);
      li.appendChild(span);
      parent.appendChild(li);
    }

    this.countFacetedTerms = async function(type, terms, ul){
      let self = this;
      var request = {"termType":type, "terms":terms, "requestData":self.currentRequestData};
      let requestId = ID;
      ul.innerHTML = "";
      $.post("/rest/extendedSearch/countHits", JSON.stringify(request), function(data){

        if(requestId == ID){
          var tempList = data.sortedByHits;
          for(i = 0; i < 4; i++){
            if(i < tempList.length){
              var li = document.createElement("li");
              const term = tempList[i][0];
              const count = tempList[i][1];
              li.classList.add("decoration-underline");
              li.innerHTML = term+'('+count+")";
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
                self.addQuery(obj);
              }
              ul.appendChild(li);
            }
          }
          if(tempList.length > 4){
            var li = document.createElement("li");
            li.style.fontWeight = "bold";
            li.innerHTML = "More ("+tempList.length+")";
            li.classList.add("decoration-underline");
            switch(type){
              case PERSON:
                li.onclick = function(){
                  self.listCreatorTerms();
                };
                break;
              case CONTRIBUTOR:
                li.onclick = function(){
                  self.listContributorTerms();
                };
                break;
              case SUBJECT:
                li.onclick = function(){
                  self.listSubjectTerms();
                };
                break;
              case TITLE:
                li.onclick = function(){
                  self.listTitleTerms();
                };
                break;
              case DESCRIPTION:
                li.onclick = function(){
                  self.listDescritionTerms();
                };
                break;
            }
            ul.appendChild(li);
          }
          self.terms[type] = {"sortedByHits":data.sortedByHits, "sortedByNames":data.sortedByNames};

        }
      });
    }

    this.loadTerms = async function(type, unorderedList, terms){
      let self = this;
      var radioButton = document.getElementById("radioBtnNames");
      if(radioButton.checked){
        radioButton.removeAttribute("onclick");
      }else{
        radioButton.onclick = function(){
          unorderedList.innerHTML = "";
          self.loadTerms(type, unorderedList, self.terms[type].sortedByNames);
        }
      }
      radioButton = document.getElementById("radioBtnHits");
      if(radioButton.checked){
        radioButton.removeAttribute("onclick");
      }else{
        radioButton.onclick = function(){
          unorderedList.innerHTML = "";
          self.loadTerms(type, unorderedList, self.terms[type].sortedByHits);
        }
      }
        for (i = 0; i < terms.length; i++) {
          var li = document.createElement("li");
          li.classList.add("list-group-item", "d-flex", "justify-content-between", "align-items-center", "liHover");
          li.style.textOverflow = "ellipsis";
          li.innerHTML = '<p style="max-width:90%;overflow:hidden;padding: 0;margin: 0;">'+terms[i][0]+'</p><span class="badge badge-primary badge-pill">'+terms[i][1]+'</span>';
          const term = terms[i][0];
          const value = terms[i][1];
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
            self.addQuery(obj);
            document.getElementById("myModal").style.display = "none";
          }
          unorderedList.appendChild(li);
        }
    }

    this.listCreatorTerms = function(){
      let self = this;
      if(self.terms[PERSON].sortedByHits.length == 0){
        return;
      }
      self.currentRequestData["hitType"] = document.querySelector('input[name = "hitType"]:checked').value;
      self.currentRequestData["filters"] = self.filters;
      document.getElementById("modal-headline").innerHTML = "Creators";
      var ulCreator = self.ulDummy.cloneNode(false);
      self.loadTerms(PERSON, ulCreator, self.terms[PERSON].sortedByHits);
      document.getElementById("myModal").style.display = "flex";
      var modalList = document.getElementById("modal-list");
      modalList.innerHTML = "";
      document.getElementById("modal-list").appendChild(ulCreator);
    }


    this.listContributorTerms = function(){
      let self = this;
      if(self.terms[CONTRIBUTOR].sortedByHits.length == 0){
        return;
      }
      self.currentRequestData["hitType"] = document.querySelector('input[name = "hitType"]:checked').value;
      self.currentRequestData["filters"] = self.filters;
      document.getElementById("modal-headline").innerHTML = "Contributors";
      var ulContributor = self.ulDummy.cloneNode(false);
      self.loadTerms(CONTRIBUTOR, ulContributor, self.terms[CONTRIBUTOR].sortedByHits);
      document.getElementById("myModal").style.display = "flex";
      var modalList = document.getElementById("modal-list");
      modalList.innerHTML = "";
      document.getElementById("modal-list").appendChild(ulContributor);
    }

    this.listSubjectTerms = function(){
      let self = this;
      if(self.terms[SUBJECT].sortedByHits.length == 0){
        return;
      }
      self.currentRequestData["hitType"] = document.querySelector('input[name = "hitType"]:checked').value;
      self.currentRequestData["filters"] = self.filters;
      document.getElementById("modal-headline").innerHTML = "Subjects";
      var ulSubjects = self.ulDummy.cloneNode(false);
      self.loadTerms(SUBJECT, ulSubjects, self.terms[SUBJECT].sortedByHits);
      document.getElementById("myModal").style.display = "flex";
      var modalList = document.getElementById("modal-list");
      modalList.innerHTML = "";
      document.getElementById("modal-list").appendChild(ulSubjects);
    }

    this.listTitleTerms = function(){
      let self = this;
      if(self.terms[TITLE].sortedByHits.length == 0){
        return;
      }
      self.currentRequestData["hitType"] = document.querySelector('input[name = "hitType"]:checked').value;
      self.currentRequestData["filters"] = self.filters;
      document.getElementById("modal-headline").innerHTML = "Titles";
      var ulTitles = self.ulDummy.cloneNode(false);
      self.loadTerms(TITLE, ulTitles, self.terms[TITLE].sortedByHits);
      document.getElementById("myModal").style.display = "flex";
      var modalList = document.getElementById("modal-list");
      modalList.innerHTML = "";
      document.getElementById("modal-list").appendChild(ulTitles);
    }

    this.listDescritionTerms = function(){
      let self = this;
      if(self.terms[DESCRIPTION].sortedByHits.length == 0){
        return;
      }
      self.currentRequestData["hitType"] = document.querySelector('input[name = "hitType"]:checked').value;
      self.currentRequestData["filters"] = self.filters;
      document.getElementById("modal-headline").innerHTML = "Descriptions";
      var ulDescriptoions = self.ulDummy.cloneNode(false);
      self.loadTerms(DESCRIPTION, ulDescriptoions, self.terms[DESCRIPTION].sortedByHits);
      document.getElementById("myModal").style.display = "flex";
      var modalList = document.getElementById("modal-list");
      modalList.innerHTML = "";
      document.getElementById("modal-list").appendChild(ulDescriptoions);
    }

    this.testCount = function(term){
      $.post("/rest/extendedSearch/countHits", term, function(data){
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
        //   $.post("/rest/extendedSearch/countHits", JSON.stringify(request), function(data){
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
        //   $.post("/rest/extendedSearch/countHits", JSON.stringify(request), function(data){
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
      document.getElementById("loading-indicator").style.display="inline-block";
      let self = this;
      ID++;
      let requestId = ID;
      currentRequestData["bottomResultId"] = history[index].bottomResult;
      currentRequestData["bottomResultScore"] = history[index].bottomResultScore;
      currentRequestData["pageIndex"] = index;
      currentRequestData["pageArraySize"] = history.length;
      currentRequestData["displayedPage"] = page;
      currentRequestData["queries"] = self.queries;
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
      this.currentRequestData["hitType"] = document.querySelector('input[name = "hitType"]:checked').value;
      document.getElementById("suffixesSelect").disabled = this.currentRequestData["hitType"] == "singleData" ? false : true;
      if($('#slider-range').slider("values")[0] > 2010 || $('#slider-range').slider("values")[1] < 2021){
        let lowbound = $('#slider-range').slider("values")[0]+"-01-01";
        let highbound = $('#slider-range').slider("values")[1]+"-12-31";
        if(lowbound != "" && highbound != ""){
          this.filters.push({"type":STARTDATE,"lower":lowbound,"upper":highbound,"fuzzy":false,"Occur":"And"});
        }
      }
      let filesize = document.getElementById("filesize");
      let lower_handle_index = $('#slider-filesize').slider("values")[0];
      let upper_handle_index = $('#slider-filesize').slider("values")[1];
      console.log("lower: "+lower_handle_index+" upper: "+upper_handle_index);
      if(lower_handle_index > $("#slider-filesize").slider("option", "min") || upper_handle_index < $("#slider-filesize").slider("option", "max")){
        console.log("#################### adding filesize filter");
        let lowbound = parseInt(this.rangeSizeValues[lower_handle_index][0]);
        let highbound = parseInt(this.rangeSizeValues[upper_handle_index][0]);
        let lowerSize = this.rangeSizeValues[lower_handle_index][1];
        let higherSize = this.rangeSizeValues[upper_handle_index][1];
        this.filters.push({"type":SIZE,"lower":this.reverseNiceBytes(lowbound,lowerSize),"upper":this.reverseNiceBytes(highbound,higherSize),"fuzzy":false,"Occur":"And"});
        console.log(this.filters);
      }
      let suffix = document.getElementById("suffix");
      let suffixValue = document.getElementById("suffixesSelect").value;
      if(suffixValue != "*"){
        this.filters.push({"type":FILETYPE,"searchterm":suffixValue,"fuzzy":false,"Occur":"And"});
        this.defaultFileType = suffixValue;
      }else{
        this.defaultFileType = "*";
      }
      this.search();
    }

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
            var toggler = document.getElementsByClassName("caret");
            for (i = 0; i < toggler.length; i++) {
              toggler[i].addEventListener("click", function() {
                this.parentElement.querySelector(".nested").classList.toggle("active");
                this.classList.toggle("caret-down");
              });
            }
            //init sliders
            $(function () {
              $("#slider-range").slider({
                range: true,
                min: 2010,
                max: 2021,
                values: [
                  2010, 2021
                ],
                slide: function (event, ui) {
                  $("#date-label").html(ui.values[0] + " - " + ui.values[1]);
                },
                stop: function (event, ui) {
                  EdalReport.filterChange();
                }
              });
              $("#date-label").html($("#slider-range").slider("values", 0) + " - " + $("#slider-range").slider("values", 1));
            });
            //populate Array with values for file size slider
            var units_array = ["B",...self.units];
            for(i = 0; i < 4; i++){
              for(j = 0; j < 10; j++){
                self.rangeSizeValues.push([Math.pow(2,j),units_array[i]]);
              }
            }
            self.rangeSizeValues.push([1,units_array[4]]);
            $("#slider-filesize").slider({
                range: true,
                min: 0,
                max: self.rangeSizeValues.length-1,
                values: [0, self.rangeSizeValues.length-1],
                slide: function(event, ui) {
                  $("#file-size-label").html(self.rangeSizeValues[ui.values[0]][0]+self.rangeSizeValues[ui.values[0]][1]+" - "+self.rangeSizeValues[ui.values[1]][0]+self.rangeSizeValues[ui.values[1]][1]);
                },
                stop: function (event, ui) {
                  EdalReport.filterChange();
                }
            });
            $("#file-size-label").html(self.rangeSizeValues[0][0]+self.rangeSizeValues[0][1]+" - "+self.rangeSizeValues[self.rangeSizeValues.length-1][0]+self.rangeSizeValues[self.rangeSizeValues.length-1][1]);
            $('.selector').slider('disable');
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
      self.currentRequestData.hitType = document.querySelector('input[name = "hitType"]:checked').value;
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
      let self = this;
      const thresh = 1024;
      const dp = 1;
      if (Math.abs(bytes) < thresh) {
        return bytes + ' B';
      }

      let u = -1;
      const r = 10**dp;

      do {
        bytes /= thresh;
        ++u;
      } while (Math.round(Math.abs(bytes) * r) / r >= thresh && u < self.units.length - 1);

      return bytes.toFixed(dp).replace(".", ",") + ' ' + self.units[u];
    };

    this.reverseNiceBytes = function(fileSize, unit){
      let self = this;
      if(unit === 'B')
        return fileSize;
      let multiplier = 1024;
      for(let i = 0; i < self.units.length; i++){
        fileSize = fileSize * 1024;
        if(self.units[i] === unit){
          break;
        }
      }
      console.log(fileSize);
      return fileSize;
    }
}
