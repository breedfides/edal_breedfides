let EdalReport = new function() {
    //jquery table container
    this.datatable = null;
    //ID to only process current REST responses
    this.ID = 0;
    //for pagination (used on REST side to continue the search from the bottomResult)
    this.bottomResultId = null;
    this.previousBottomResultId = null;
    //tableRow pixel height
    this.rowHeight = 35;
    //indicates the number of displayed tableRows
    this.pageSize = Math.floor((document.getElementById("table-column").clientHeight - 3*this.rowHeight) / this.rowHeight);
    //contains Objects that are used to filter for dates/ filesizes and filetypes
    this.filters = [];
    //number of totalhits
    this.hitSize = 0;
    //number of available pages
    this.pageNumbers = 0;
    //holds information that is used for searching and calculation of facetedTerms
    this.currentRequestData = {};
    //modal dialog element that shows all facetedTerms for a category
    this.myModal = document.getElementById("myModal");
    //list for the category lists with all facetedTerms
    this.terms = {"Creator":[], "Contributor":[], "Subject":[], "Title":[], "Description":[]};
    //lists that hold all at the start recevied facetedTerms (via REST)
    this.creators;
    this.contributors;
    this.subjects;
    this.titles;
    this.descriptions;
    //symbol to indicate that no filetype is selected
    this.defaultFileType = "*";
    //contains fileSizes that relate to the currently selected slider filsize value
    this.rangeSizeValues = [];
    //for calculation of readable filesizes instead of raw bits and bytes
    this.units = ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    //list for current queries (the information is displayed as tabs above the datatable)
    this.queries = [];
    this.contentQueries = [];
    //dummy element that is used to clone new elements when facetedTerms-uls are calculated and appended to the dom
    this.ulDummy = document.createElement("ul");
    this.ulDummy.classList.add("list-group");
    this.ulDummy.style.maxHeight = "250px";
    this.ulDummy.style.overflowY = "auto";
    this.ulDummy.style.overflowX = "hidden";

    this.reportColumns = [
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
                        title: "Dataset",
                        data: "title",
                        class: "edal-report-title"
                    }
                ];

    this.fileColumns = [
        {
            title: "Filename",
            width: "25%",
            class: "edal-report-doi",
            render: function (data, type, row) {
                return '<a href="'+serverURL+'/DOI/'+row.link+'" target="_blank">'+row.fileName+'</a>';
            }
        },
        {
            title: "File type",
            width: "10%",
            data: "ext",
            class: "edal-report-title"
        },
        {
            title: "File size",
            width: "10%",
            data: "size",
            class: "edal-report-title",
            render: function (data, type, row) {
                if (type === "display") {
                    return EdalReport.niceBytes(parseInt(data));
                } else {
                    return data;
                }
            }
        },
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
            title: "Dataset",
            data: "title",
            class: "edal-report-title"
        },
        {
          title: "DL",
          class: "edal-report-title",
          width: "35px",
          render: function (data, type, row) {
              return '<a href="'+serverURL+'/DOI/'+row.link+'/DOWNLOAD" target="_blank" role="button" class="mt-3 btn btn-outline-primary btn-sm"><span class="oi oi-data-transfer-download" title="icon name" aria-hidden="true"></span>';
          }
        }
    ];

    this.directoriesColumns = [
        {
            title: "Directory",
            width: "25%",
            class: "edal-report-doi",
            render: function (data, type, row) {
                return '<a href="'+serverURL+'/DOI/'+row.doi+'" target="_blank">'+row.fileName+'</a>';
            }
        },
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
            title: "Dataset",
            data: "title",
            class: "edal-report-title"
        }
    ];

    //ID to only process current REST responses (for search()
    let ID = 0;
    //holds data that is needed if the datatables is created/changed
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

    /** used to append a Query from the advanced Search bar to the
     currentRequestData and to the displayed tabs */
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

    /*triggers a REST call to parse a Query and passes the response to a helper
     function to add a new displayed tab*/
    this.addQuery = function(query){
      let self = this;
      $.post(serverURL+"/rest/extendedSearch/parsequery", JSON.stringify(query), function(data){
        if(data != ""){
          const index = self.queries.length;
          self.queries.push(data);
          self.addTab(data, index);
        }
        self.search();
      });
    }

    /* used to search with the currentRequestData and to process the results */
    this.search = function(){
      return new Promise((resolve, reject)=>{
        let self = this;
        self.build();
        var queryValue = document.getElementById('query').value;
        document.getElementById("loading-indicator").style.display="inline-block";
        ID++;
        let requestId = ID;
        let requestData = { "hitType":document.querySelector('input[name = "hitType"]:checked').value, "existingQuery":queryValue, "filters":this.filters,
         "bottomResultId":this.bottomResultId, "pageSize":this.pageSize,"pageIndex":0,"pagination":[], "pageArraySize":0,"displayedPage":1, "queries":self.queries, "whereToSearch":document.getElementById("searchContextSelect").value };
        self.currentRequestData = requestData;
        //check if search is useful because of a potential selected file type
        var fileTypeSelected = false;
        if(self.filters.length > 0){
          for(i = 0; i < self.filters.length; i++){
            fileTypeSelected = self.filters[i].type == FILETYPE;
            if(fileTypeSelected){
              break;
            }
          }
        }
        if(self.queries.length > 0 || queryValue != "" || fileTypeSelected){
          $.post("/rest/extendedSearch/search", JSON.stringify(requestData), function(data){
          reportData = data.results;
          if(ID == requestId){
            self.updateTerms(data.facets);
            if(data.parsedQuery != null && data.parsedQuery != ""){
              const queryIndex = self.queries.length;
              self.queries.push(data.parsedQuery);
              self.addTab(data.parsedQuery,queryIndex);
            }
            self.currentRequestData.existingQuery = data["parsedQuery"];
            var currentPage = 0;
            var history = data.pageArray;
            self.hitSize = data.hitSizeDescription+' '+data.hitSize;
            self.pageNumbers = Math.ceil(data.hitSize/self.pageSize);
            self.reportData = data.results;
            self.datatable.destroy();
            $("#report tbody").empty();
            $("#report thead").empty();
            if(requestData.hitType == "dataset"){
              self.renderDatatableReports(self.reportColumns);
            }else if(requestData.hitType == "singledata"){
              self.renderDatatableReports(self.fileColumns);
            }else if(requestData.hitType == "directory"){
              self.renderDatatableReports(self.directoriesColumns);
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
        }else{
          self.reportData = [];
          self.currentRequestData = {};
          let hitType = document.querySelector('input[name="hitType"]:checked').value;
          self.currentRequestData.hitType = hitType;
            // /* count hits for a list of terms and a specific ul and appends <li> elements to that list Parameter = JSON string with optional filters and a hitType */
          $.post("/rest/extendedSearch/drillDown",JSON.stringify({"filters":self.filters,"hitType":hitType}), function(data){
            self.updateTerms(data);
          });
          $('#report').DataTable().clear().draw();
          $('#ul_pagination').empty();
          $('#result-stats').text('No results found');
          $('#loading-indicator').css('display','none');
        }
        resolve();
      });

    }

    /* clears the queries, filtetypes select, factedTerms */
    this.clearFiltersTabs = function(){
      let self = this;
      self.queries = [];
      $("#query").val("");
      $("#query-ul").html("");
      $("#query-ul").css("display","none");
      $('.jqslider').each(function(){
        var options = $(this).slider( 'option' );
        $(this).slider( 'values', [ options.min, options.max ] );
      });
      $("#suffixesSelect").val("*");
      self.filterChange();
    }

    /* creates and adds a "tab" to the displayed container of queries */
    this.addTab = function(query, index){
      let self = this;
      let parent = document.getElementById("query-ul");
      const i = index;
      const text = query;
      let span = document.createElement("a");
      span.classList.add("btn", "btn-outline-dark","btn-sm", "shadow-none", "query-span","mx-1");
      span.style.cursor = "default";
      var innerHtmlString = "";
      var closeTag = false;
      for(j = 0; j < text.length;j++){
        let tempChar = text.charAt(j);
        innerHtmlString += tempChar;
        if(tempChar == ':'){
          if(j > 0 && text.charAt(j-1) != '\\'){
            innerHtmlString += "<b>";
            closeTag = true;
          }
          //whiteSpace?
        }else if(closeTag && tempChar.trim() === ''){
          innerHtmlString += "</b>&nbsp";
          closeTag = false;
        }
      }
      span.innerHTML = innerHtmlString;
      let innerSpan = document.createElement("button");
      innerSpan.classList.add("remove-query-btn", "ml-2");
      innerSpan.innerHTML = '&times';
      span.appendChild(innerSpan);
      innerSpan.onclick = function(){
        if(self.queries.length > 1){
          self.queries.splice(i,1);
        }else{
          self.queries = [];
          document.getElementById("query-ul").style.display = "none";
        }
        parent.innerHTML = "";
        self.search();
        self.queries.forEach((item, j) => {
          const indx = j;
          self.addTab(item, indx);
        });
      }
      let li = document.createElement("li");
      li.appendChild(span);
      parent.appendChild(li);
      if(self.queries.length == 1){
        document.getElementById("query-ul").style.display = "block";
      }
    }

    this.updateTerms = function(data){
      let self = this;
      document.getElementById(PERSON).innerHTML = "";
      document.getElementById(CONTRIBUTOR).innerHTML = "";
      document.getElementById(SUBJECT).innerHTML = "";
      document.getElementById(TITLE).innerHTML = "";
      document.getElementById(DESCRIPTION).innerHTML = "";
      data.forEach(function(facet){
        let tempList = facet.sortedByHits;
        //facet for filetypes ? -> fill select with values
        if(facet.category == "Filetype"){
          if(self.currentRequestData["hitType"] == "singledata"){
            //reset select values
            var select = document.getElementById("suffixesSelect");
            select.innerHTML = "";
            var option = document.createElement("option");
            option.innerHTML = "*";
            select.appendChild(option);
            var request = {"termType":FILETYPE, "terms":filetypes, "requestData":self.currentRequestData};
            //sort values lexicographicaly for there labels
            tempList.sort((a, b) => {
                if (a.label > b.label)
                    return 1;
                if (a.label < b.label)
                    return -1;
                return 0;
            });
            //fill select with fileTypes and the number of hits
            for (i = 0; i < tempList.length; i++) {
              var opt = document.createElement("option");
              opt.value = tempList[i].label;
              opt.innerHTML = tempList[i].label + " (" + tempList[i].value+")";
              select.appendChild(opt);
              //first fileType = "*", skip this value
              if(tempList[0].label == self.defaultFileType){
                select.selectedIndex = ++i;
              }
            }
          }
        }else{
          //Add faceted terms to DOM
          let ul = document.getElementById(facet.category);
          for(i = 0; i < 4; i++){
            if(i < tempList.length){
              var li = document.createElement("li");
              const term = tempList[i].label;
              const count = tempList[i].value;
              li.classList.add("decoration-underline");
              li.innerHTML = term+' ('+count+")";
              li.onclick = function(){
                var searchInput = document.getElementById("query");
                if(!searchInput.value){
                  searchInput.classList.add("x");
                }
                let obj = {
                  "type":facet.category,
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
            switch(facet.category){
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
            self.terms[facet.category] = {"sortedByHits":facet.sortedByHits, "sortedByNames":facet.sortedByNames};
        }
      });
    }

    /* calculate new faceted Terms and adds them to a specific ul element */
    this.loadTerms = async function(type, unorderedList, terms){
      let self = this;
      var radioButton = document.getElementById("radioBtnNames");
      if(radioButton.checked){
        radioButton.removeAttribute("onclick");
      }else{
        radioButton.onclick = function(){
          unorderedList.innerHTML = "";
          if(!self.terms[type].sortedByNames){
            console.log("need to sort faceted terms..");
            const sorted = [...terms].sort((a, b) => {
                if (a.label > b.label)
                    return 1;
                if (a.label < b.label)
                    return -1;
                return 0;
            });
            self.terms[type].sortedByNames = sorted;
          }
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
        li.innerHTML = '<p style="max-width:90%;overflow:hidden;padding: 0;margin: 0;">'+terms[i].label+'</p><span class="badge badge-primary badge-pill">'+terms[i].value+'</span>';
        const term = terms[i].label;
        const value = terms[i].value;
        li.onclick = function(){
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
      var searchInput = document.getElementById("query");
      if(!searchInput.value){
        searchInput.classList.add("x");
      }
    }

    /* creates ul inside the modal dialoge, fills it with facetedTerms and displays it
      for creators*/
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

    /* creates ul inside the modal dialoge, fills it with facetedTerms and displays it
      for contributors*/
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

    /* creates ul inside the modal dialoge, fills it with facetedTerms and displays it
      for subjects*/
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

    /* creates ul inside the modal dialoge, fills it with facetedTerms and displays it
      for titles*/
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

    /* creates ul inside the modal dialoge, fills it with facetedTerms and displays it
      for descriptions*/
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
      currentRequestData["existingQuery"] = "";
      currentRequestData["queries"] = self.queries;
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
          if(offset > history.length){
            i -= offset-history.length;
            if(history.length > 10){
              i--;
            }
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
        if(currentRequestData.hitType == "dataset"){
          self.renderDatatableReports(self.reportColumns);
        }else if(currentRequestData.hitType == "singledata"){
          self.renderDatatableReports(self.fileColumns);
        }else if(currentRequestData.hitType == "directory"){
          self.renderDatatableReports(self.directoriesColumns);
        }
        document.getElementById("loading-indicator").style.display="none";
        self.manipulateDataTable(pageArray, self.currentRequestData, history);
      }
      });
    }

    /* creates list with filter data and triggers a search() */
    this.filterChange = function(){
      this.filters = [];
      let periodbox = document.getElementById("period");
      let oldHitType = this.currentRequestData.hitType;
      this.currentRequestData["hitType"] = document.querySelector('input[name = "hitType"]:checked').value;
      if($('#slider-range').slider("values")[0] > minYear || $('#slider-range').slider("values")[1] < maxYear){
        let lowbound = $('#slider-range').slider("values")[0]+"-01-01";
        let highbound = $('#slider-range').slider("values")[1]+"-12-31";
        if(lowbound != "" && highbound != ""){
          this.filters.push({"type":STARTDATE,"lower":lowbound,"upper":highbound,"fuzzy":false,"Occur":"And"});
        }
      }
      if(this.currentRequestData.hitType == "singledata"){
        document.getElementById("suffixesSelect").disabled = false;
        $('.opacity-change').css('opacity','1');
        $('#slider-filesize').slider("enable");
        let filesize = document.getElementById("filesize");
        let lower_handle_index = $('#slider-filesize').slider("values")[0];
        let upper_handle_index = $('#slider-filesize').slider("values")[1];
        if(lower_handle_index > $("#slider-filesize").slider("option", "min") || upper_handle_index < $("#slider-filesize").slider("option", "max")){
          let lowbound = parseInt(this.rangeSizeValues[lower_handle_index][0]);
          let highbound = parseInt(this.rangeSizeValues[upper_handle_index][0]);
          let lowerSize = this.rangeSizeValues[lower_handle_index][1];
          let higherSize = this.rangeSizeValues[upper_handle_index][1];
          this.filters.push({"type":SIZE,"lower":this.reverseNiceBytes(lowbound,lowerSize),"upper":this.reverseNiceBytes(highbound,higherSize),"fuzzy":false,"Occur":"And"});
        }
        let suffix = document.getElementById("suffix");
        let suffixValue = document.getElementById("suffixesSelect").value;
        if(suffixValue != "*"){
          this.filters.push({"type":FILETYPE,"searchterm":suffixValue,"fuzzy":false,"Occur":"And"});
          this.defaultFileType = suffixValue;
        }else{
          this.defaultFileType = "*";
        }
      }else{
        document.getElementById("suffixesSelect").disabled = true;
        $('#slider-filesize').slider("disable");
        $('.opacity-change').css('opacity','0.5');
      }
      this.search();
    }

    /* initializes some variables and jquery elements (sliders, datatable) */
    this.init = function(reportData, mapData) {
        this.reportData = reportData;
        this.mapData = mapData;
        this.reportDataKeyed = _.keyBy(reportData, 'doi');
        let self = this;
        if(self.pageSize > 5){
          self.pageSize = self.pageSize - self.pageSize%5;
        }
        $(document).ready(async function() {
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
                min: Number(minYear),
                max: Number(maxYear),
                values: [
                  Number(minYear), Number(maxYear)
                ],
                slide: function (event, ui) {
                  $("#date-label").html(ui.values[0] + " - " + ui.values[1]);
                },
                stop: function (event, ui) {
                  EdalReport.filterChange();
                }
              });
              $("#date-label").html(minYear + " - " + maxYear);
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
            $('#slider-filesize').slider("disable");
            $('.opacity-change').css('opacity','0.5');
            self.renderDatatableReports(self.reportColumns);
            if(initQuery !== ""){
              $('#query').val(initQuery);
              await self.search();
            }else{
              self.manipulateDataTable([],null,[]);
            }
            $('#query').on('keypress',function(e) {
                if(e.which == 13) {
                    self.search();
                }
            });
            $('#searchterm').on('keypress',function(e) {
                if(e.which == 13) {
                    self.search();
                }
            });
            window.history.replaceState(null, null, window.location.pathname);
        });
    };

    // /* resets term lists and calls EdalReport.facetedTerms() to count hits for
    //  every search term which depend on the current set queries*/
    // this.resetTermList = function(){
    //   let self = this;
    //   self.currentRequestData.existingQuery = document.getElementById("query").value;
    //   self.currentRequestData.filters = self.filters;
    //   self.currentRequestData.hitType = document.querySelector('input[name = "hitType"]:checked').value;
    //   $.post("/rest/extendedSearch/getTermLists", function(data){
    //     self.creators = data[PERSON];
    //     self.contributors = data[CONTRIBUTOR];
    //     self.subjects = data[SUBJECT];
    //     self.titles = data[TITLE];
    //     self.descriptions = data[DESCRIPTION];
    //     self.facetedTerms();
    //   });
    // }

    // /* resets term lists and calls EdalReport.facetedTerms() to count hits for
    //  every search term which depend on the current set queries*/
    // this.initTermLists = function(){
    //   let self = this;
    //   self.currentRequestData.existingQuery = document.getElementById("query").value;
    //   self.currentRequestData.filters = self.filters;
    //   self.currentRequestData.hitType = document.querySelector('input[name = "hitType"]:checked').value;
    //   $.post("/rest/extendedSearch/getTermLists", function(data){
    //     self.creators = data[PERSON];
    //     self.contributors = data[CONTRIBUTOR];
    //     self.subjects = data[SUBJECT];
    //     self.titles = data[TITLE];
    //     self.descriptions = data[DESCRIPTION];
    //   });
    // }

    /* renders the datatable with columns for directories */
    this.renderDatatableReports = function(cols) {
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
            columns: cols
        });
    };

    /* creates and appends pagination to the dom */
    this.manipulateDataTable = function(numbers, currentRequestData, history){
      if(typeof numbers == 'undefined' || numbers.length == 0){
        return;
      }
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
          self.changePage(nextIndex, nextPage, currentRequestData, history);
        };
      }
      if(currentIndex-1 > -1){
        var nextBtn = document.getElementById("btn_previous");
        nextBtn.classList.remove("disabled");
        nextBtn.onclick = function(){
          var prevIndex = currentIndex-1;
          var prevPage = currentPage-1;
          self.changePage(prevIndex, prevPage, currentRequestData, history);
        };
      }
    }
    /* converts bytes to the human readable equivalent */
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

    /* converts a the human readable  file to the rare bytes*/
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
      return fileSize;
    }
}
