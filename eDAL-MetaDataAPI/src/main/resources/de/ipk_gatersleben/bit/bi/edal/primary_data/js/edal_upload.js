const worker = new Worker("/js/edal_traverse.js");
let files = {};
let numberOfFiles = 0;
let keytimer;
let titleAlreadyExists = false;
let checkingTitle = false;
let storage = localStorage;
let currentAuthorRow = null;
//for calculation of readable filesizes instead of raw bits and bytes
let units = ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

/* set the limit of possible parallel file uploads */
const numberOfParallelUploads = 6;
const updateProgressTimeout = 1000;

var resumable;


/* Input validation function that makes a REST call to test if
 the entered title already exists for the current user */
function checkIfExists(input){
    if($(input).val() != ''){
        clearTimeout(keytimer);
        keytimer = setTimeout(() => {
            let form = new FormData();
            form.append("subject",email);
            form.append("name",$(input).val());
            checkingTitle = true;
            jQuery.ajax({
                url: serverURL+"/restfull/api/checkIfExists",
                data: form,
                cache: false,
                contentType: false,
                processData: false,
                method: 'POST',
                type: 'POST',
                success: function(titleExists){
                    if(titleExists === 'true'){
                        titleAlreadyExists = true;
                        $('#input_title').removeClass( "is-valid" ).addClass('is-invalid');
                        input.setCustomValidity("Already exists! Please choose another name.");
                        input.reportValidity();
                    }else{
                        titleAlreadyExists = false;
                        $('#input_title').removeClass( "is-invalid" ).addClass('is-valid');
                    }   
                    checkingTitle = false;                                                                
                }
              });
        }, 700);
    }
}

function validateInputs(){
  if(checkingTitle){
    //stop and let the user click again, wait for server side title validation
    return;
  }
    let failedValidations = new Map();
    //validate title and description
    let element = document.getElementById('input_title');
    if(element.value == ""){
      element.setCustomValidity('Please set a title.');
      element.reportValidity();
      return false;
    }
    if(titleAlreadyExists){
        element.setCustomValidity('Please set a new title.');
        element.reportValidity();
        return false;
    }
    element = document.getElementById('text_description');
    if(element.value == ""){
      element.setCustomValidity('Please set a description.');
      element.reportValidity();
      return false;
    }

    //validate authors
    let authorsIncomplete = false;
    var tb = $('#author_table:eq(0) tbody');
    tb.find("tr").each(function(index, element) {
      var colSize = $(element).find('td').length;
      console.log("  Number of cols in row " + (index + 1) + " : " + colSize);
      $(element).find('td').each(function(index, element) {
        //index == 2 = orcid, allowed to be empty
        if(index > 0 && index < 8 && element.isContentEditable && index != 3){
          var colVal = $(element).text();
          if(!colVal){
            authorsIncomplete = true;
          }
        }
      });
    });
    $('#alert-information').empty();
    if(authorsIncomplete){
        $('#alert-information').append('<strong>Missing Author: </strong>Please set at least one author for this publication.');
        if(!failedValidations.size){
            $('.alert').show();
            return false;
        }
        failedValidations.set(tb,{label:'Missing Authors: ',text:'Please set at least one author for this publication.'});
    }
    //validate if at least one subject is set
    let noSubject = true;
    tb = $('#subject_table:eq(0) tbody');
    tb.find("tr").each(function(index, element) {
      if($(element).children('td:nth-child(2)').text()){
        noSubject = false;
      }
    });
    if(noSubject){
        $('#alert-information').append('<strong>Missing Subject: </strong>Please fill in at least one subject for this publication.');
        if(!failedValidations.size){
            $('.alert').show();
            return false;
        }
    }
    //validate if a dataset was selected

    if(fileSystemEntry == null){
        $('#alert-information').append('<strong>Missing Dataset: </strong>Please drop or select a dataset to be uploaded.');
        if(!failedValidations.size){
            $('.alert').show();
            return false;
        }
    }
    // if map only contains
    console.log(failedValidations.get(0));
    if(failedValidations.size > 1){
        for (const [key, value] of failedValidations.entries()) {
            $('#alert-information').append(`<strong>${value.label}</strong>${value.text}`);
            $('#alert-information').append('<br>');
          }
        $('.alert').show();
        return false;
    }else{
        if(failedValidations.size == 1){
            for (const [key, value] of failedValidations.entries()) {
                key.setCustomValidity(value.text);
                key.reportValidity();
            }
            $('.alert').hide();
            return false;
        }
        return true;;
    }
}

/* Persist entered input to the localstorage */
function storeInputs(){
  var tb = $('#author_table:eq(0) tbody');
  let personArray = [];
  tb.find("tr").each(function(index, element) {
    var colSize = $(element).find('td').length;
    let temp_person = {};
    $(element).find('td').each(function(index, element) {
    var $th = $(element).closest('table').find('th').eq(index);
      //index == 2 = orcid, allowed to be empty
      if(element.isContentEditable){
        temp_person[$th.text()] = $(element).text();
      }else if(index == 8){
        temp_person[$th.text()] = $(element).find('select').val();
      }else if(index == 3 && !element.querySelector('button')){
        temp_person[$th.text()] = `<div class='link' onclick='searchORCIDsOfRow(this.parentNode.parentNode)' data-toggle='modal' data-target='#myModal'>${$(element).text()}</div>`;
      }
    });
    personArray.push(temp_person);
  });
  storage.setItem("authors",JSON.stringify(personArray));
  tb = $('#subject_table:eq(0) tbody');
  let subjectArray = [];
  tb.find("tr").each(function(index, element) {
    $(element).find('td').each(function(index, element) {
      if(index > 0){
        subjectArray.push($(element).text());
      }
    });
  });
  storage.setItem("subjects", JSON.stringify(subjectArray));
  storage.setItem("language", $("#select_language").prop('selectedIndex'));
  storage.setItem("description", $("#text_description").val());
  storage.setItem("license", $("#select_license").prop('selectedIndex'));
}

async function showUploadDialog(){
    //let msg = await traverse(fileSystemEntry, listing);
    if(validateInputs()){
        $('#myModal2').modal('show');
        storeInputs();
    }
    //let msg = await traverse(fileSystemEntry);

    //console.log(msg);
    // let form = new FormData();
    // form.append("subject",email);
    // form.append("name", globalMetadata.title);
    // jQuery.ajax({
    //   url: serverURL+"/restfull/api/publishDataset",
    //   data: form,
    //   cache: false,
    //   contentType: false,
    //   processData: false,
    //   method: 'POST',
    //   type: 'POST',
    //   success: function(resData){
    //       console.log("publish() response: "+resData);
    //   }
    // });
  }

  function toggleStartUpload(){
    console.log("test3432");
  }

  async function startUpload(){
    if(fileSystemEntry.kind == "directory"){
      startDirectoryUpload();
    }else if(fileSystemEntry.kind == "file"){
      startSingleFileUpload();
    }
    $('#submitBtn').prop('disabled', true);
  }

async function startSingleFileUpload(){
  var height = document.getElementById('submitBtn').clientHeight;
  $('.progress-bar').css('transition','width .6s linear');
  $('.progress-bar').css('background-color','#0275d8');
  $('#submitBtn').css('width','50%');
  $('.progress').css('height','17px');
  //remove submit btn text
  $('#submitBtn').contents().filter(function(){
      return this.nodeType === 3;
  }).remove();
  $('#submitBtn').css('height',height);
  let metadata = getInputMetadata();
  await uploadEntityAndMetadata(metadata.title,metadata);
  const file = await fileSystemEntry.getFile();
  files["file"] = file;
  uploadSingleEntityDataset(fileSystemEntry.name, file).then(() => {
    $('.progress-bar').css('transition','width .1s linear');
    $('.progress-bar').css('width','0%');
    setTimeout(() => {
        $('.progress-bar').css('background-color','#5cb85c');
        $('.progress-bar').css('transition','width .6s ease');
        $('.progress-bar').css('width','40%');
    },600); 
    let form = new FormData();
    form.append("subject",email);
    form.append("name", globalMetadata.title);
    jQuery.ajax({
      url: serverURL+"/restfull/api/publishDataset",
      data: form,
      cache: false,
      contentType: false,
      processData: false,
      method: 'POST',
      type: 'POST',
      success: function(resData){
        $('.progress-bar').css('width','100%');
        $('.progress-bar').css('transition','width 1.8s ease');
        titleAlreadyExists = true;
        setTimeout(() => {$('#file-counter-info').text("Finished!");},600);                                                                            
      }
    });
  });
}


async function startDirectoryUpload(){
  console.log("started directory upload");
    var height = document.getElementById('submitBtn').clientHeight;
    $('.progress-bar').css('transition','width .6s linear');
    $('#file-counter-info').text("Uploaded 0/"+Object.keys(files).length);
    $('.progress-bar').css('background-color','#0275d8');
    $('#submitBtn').css('width','50%');
    $('.progress').css('height','17px');
    //remove submit btn text
    $('#submitBtn').contents().filter(function(){
        return this.nodeType === 3;
    }).remove();
    $('#submitBtn').css('height',height);
    numberOfFiles = Object.keys(files).length+2;
    let metadata = getInputMetadata();
    await uploadEntityAndMetadata(metadata.title,metadata);
    await uploadEntity2(fileSystemEntry.name, null);
    $('.progress-bar').css('width',`${((2/numberOfFiles).toFixed(1))*100}%`);
    async function iterateFiles(files){
        let requests = 0;
        let counter = 0;
        $('.parallel-uploads').empty();
        let length = Object.keys(files).length
        return new Promise(async (resolve) => {
            for (var key in files) {
                counter++;
                const progress_id = "file-no-"+counter;
                console.log("progrss-id vor nutzung: "+progress_id)
                if (files.hasOwnProperty(key)) {
                    if(files[key].isDirectory){
                        await uploadEntity2(key, null);
                        updateUploadedFiles(counter);
                    }else{
                        if(requests < 10){
                            requests++;
                            uploadEntity2(key, files[key].file, progress_id).then(() => {
                                requests--
                                updateUploadedFiles(counter);
                                if(counter == length && requests == 0){
                                    $('.progress-bar').css('transition','width .1s linear');
                                    $('.progress-bar').css('width','0%');
                                    setTimeout(() => {
                                        $('.progress-bar').css('background-color','#5cb85c');
                                        $('.progress-bar').css('transition','width .6s ease');
                                        $('.progress-bar').css('width','40%');
                                    },600); 
                                    $('#file-counter-info').text("Finished uploading! Publishing initiated..");
                                    let form = new FormData();
                                    form.append("subject",email);
                                    form.append("name", globalMetadata.title);
                                    jQuery.ajax({
                                      url: serverURL+"/restfull/api/publishDataset",
                                      data: form,
                                      cache: false,
                                      contentType: false,
                                      processData: false,
                                      method: 'POST',
                                      type: 'POST',
                                      success: function(resData){
                                        $('.progress-bar').css('width','100%');
                                        $('.progress-bar').css('transition','width 1.8s ease');
                                        titleAlreadyExists = true;
                                        setTimeout(() => {$('#file-counter-info').text("Finished!");},600);                                                                            
                                      }
                                    });
                                }
                            });
                        }else{
                            await uploadEntity2(key, files[key].file,progress_id);
                            updateUploadedFiles(counter);
                        }
                        //resumable.addFile(files[key].file);
                    }
                }
            }
        });
        
    }
    iterateFiles(files);

    //resumable.upload();
}

async function updateUploadedFiles(counter){
  setTimeout(function (){
    $('#file-counter-info').text("Uploaded +"+counter+"/"+Object.keys(files).length);
    $('.progress-bar').css('width',`${(Math.round(((((counter+2)/numberOfFiles)) + Number.EPSILON) * 100)/100)*100}%`);
    $('.progress-bar').text(`${Math.round((Math.round(((((counter+2)/numberOfFiles)) + Number.EPSILON) * 100)/100)*100)}%`);                 
  }, updateProgressTimeout);

}

async function uploadEntity2(path, file, progressIdentifier){
    return new Promise(async (resolve) => {
        let payload = new FormData();
        payload.append("name",path);
        payload.append("email",email);
        payload.append("datasetRoot",globalMetadata.title);
        if(file == null){
          payload.append("type","Directory");
      
          payload.append('file',null);
          jQuery.ajax({
            url: serverURL+"/restfull/api/uploadEntity",
            data: payload,
            cache: false,
            contentType: false,
            processData: false,
            method: 'POST',
            type: 'POST', // For jQuery < 1.9
            success: function(resData){
                resolve("finished dir upload "+resData);
            }
        });
        }else{

            payload.set("name",path);
            payload.append('file',file);
            payload.append('size',file.size);
            payload.append("type","File");
            jQuery.ajax({
                url: serverURL+"/restfull/api/uploadEntity",
                data: payload,
                cache: false,
                contentType: false,
                processData: false,
                method: 'POST',
                type: 'POST', // For jQuery < 1.9
                success: function(resData){
                resolve("finished file upload "+resData);
                }
                });

               /* Add progress ui for this file with the key as ID to upload progresses dialog  */         
               markup = `<div class='d-flex flex-row mt-2 mb-2' style='text-align:center;align-items:center;'><div class='file-progress-name mr-2'>: ${file.name} (${niceBytes(file.size)})</div><div class='progress w-100 submitbtn' style='height:17px;'><div class="single-file-progressbar" id=${progressIdentifier} >0%</div></div></div>`;
               $(".parallel-uploads").append(markup);      
               updateFileProgress(path, progressIdentifier, 0);

                //$.post( serverURL+"/restfull/api/uploadEntity", JSON.stringify(requestData), function(data){
                //resolve("finished!!");
                //});
        }
    });
  }

  async function uploadSingleEntityDataset(path, file){
    return new Promise(async (resolve) => {
      let payload = new FormData();
      payload.append("email",email);
      payload.append("datasetRoot",globalMetadata.title);
      payload.set("name",path);
      payload.append('file',file);
      payload.append('size',file.size);
      payload.append("type","File");
      jQuery.ajax({
          url: serverURL+"/restfull/api/uploadEntity",
          data: payload,
          cache: false,
          contentType: false,
          processData: false,
          method: 'POST',
          type: 'POST', // For jQuery < 1.9
          success: function(resData){
          resolve("finished file upload "+resData);
          }
          });   
         updateFileProgress(path, "parent-progress-bar", 0);
    });
  }

  async function updateFileProgress(path, progressId, updates){
    let payload = new FormData();
    payload.append('email',email);
    payload.append('name',path);
    jQuery.ajax({
      url: serverURL+"/restfull/api/getProgress",
      data: payload,
      cache: false,
      contentType: false,
      processData: false,
      method: 'POST',
      type: 'POST', // For jQuery < 1.9
      success: function(progress){
        console.log(progressId);
        if(progress >= 100){
            document.getElementById(progressId).style.width ="100%";
            animateValue(progressId, Number(document.getElementById(progressId).textContent.slice(0, -1)), 100, updateProgressTimeout);
            setTimeout(function (){
              document.getElementById(progressId).style.backgroundColor ="rgb(92, 184, 92)";                  
            }, updateProgressTimeout);
        }else if(progress > 0 && progress < 100){
          updates++;
          console.log("file upload progress display continues");
          document.getElementById(progressId).style.width = progress+"%";
          animateValue(progressId, Number(document.getElementById(progressId).textContent.slice(0, -1)), progress, updateProgressTimeout);
          //$(`#${progressId}`).text(`${progress}%`);
          setTimeout(function (){
            updateFileProgress(path, progressId, updates)                    
          }, updateProgressTimeout);
        }else{
          setTimeout(function (){
            updateFileProgress(path, progressId, updates)                    
          }, updateProgressTimeout);
        }
      }
      });
  }
  
//   emptyFileCounter.onmessage = (event) => {
//       const { data } = event;
//       alert("empty files: "+data);
//   };
worker.onmessage = (evt) => {
    if (typeof evt.data === 'string') {
        document.querySelector("pre").textContent = JSON.stringify( evt.data, (key, value) => {
            if( value instanceof Blob ) {
              return { name: value.name, size: value.size, type: value.type };
            }
            return value;
          }, 4 );
    }else{
        files = evt.data.traversed;
        document.querySelector("pre").textContent = `${ evt.data.processed } files, ${ evt.data.emptyDirs } empty directories, ${ evt.data.emptyFiles } empty files`;
        console.log(JSON.stringify(evt.data.traversed));
        for (var key in files) {
            if (files.hasOwnProperty(key)) {
                //console.log(key + " -> " + files[key].path.toString());
                $('#submitBtn').prop('disabled', false);
            }
        }
    }

  }
  
  dropzone.addEventListener("dragover", function(event) {
      event.preventDefault();
  }, false);
  dropzone.addEventListener("drop", async function(event) {
    let items = event.dataTransfer.items;
  
    event.preventDefault();
  
    //let item = items[i].webkitGetAsEntry();
    let item = await items[0].getAsFileSystemHandle();
    console.log("dropped item");
    if(item.kind == 'directory'){
      fileSystemEntry = item;
      $('.progress').css('height','0');
      $('.progress-bar').css('width','0%');
      $('#submitBtn').css('width','25%');
      $('#submitBtn').contents().filter(function(){
          return this.nodeType === 3;
      }).remove();
      $('#submitBtn').append('Start upload');
      //const dirHandle = await showDirectoryPicker();
      $('#droplabel').text("Directory: "+item.name);
      
      worker.postMessage( item );
    }else if(item.kind == 'file'){
      $('.progress').css('height','0');
      $('.progress-bar').css('width','0%');
      $('#submitBtn').css('width','25%');
      $('#submitBtn').contents().filter(function(){
          return this.nodeType === 3;
      }).remove();
      $('#submitBtn').append('Start upload');
      //const dirHandle = await showDirectoryPicker();
      $('#droplabel').text("File: "+item.name);
      console.log("dropped file!");
      fileSystemEntry = item;
    }

    
      //   if (item) {
      //       console.log("dropped directory_ "+item.name);
      //       document.getElementById('loading-indicator').style.display = "block";
      //       document.getElementById('droplabel').style.display = "none";
      //       emptyDirectories = 0;
      //       fileSystemEntry = item;
      //       $('#droplabel').text(item.fullPath);
      //       //let resolveMsg = scanFiles(item);
      //       console.log("item:");
      //       console.log(item);
      //       //emptyFileCounter.postMessage(item);
      //       traverseDirectory2(item).then(() => {
      //         document.getElementById('loading-indicator').style.display = "none";
      //         document.getElementById('droplabel').style.display = "block";
      //       });
      //       $('#submitBtn').prop('disabled', false);
      //   }
  }, false);

  async function chooseDirectory(){
    const dirHandle = await showDirectoryPicker();
    $('#droplabel').text(dirHandle.name);
    fileSystemEntry = dirHandle;
    worker.postMessage( dirHandle );  
  }
  
  function uploadPost(){
    console.log("triggered uploadPost()");
    let myfile = $("fileInput").val();
    //$.post(serverURL+"/restfull/api/upload",{file:myfile}, function(data){
    //  console.log("uploaded single file");
    //})
    let data = new FormData();
    data.append('file',myfile);
    jQuery.ajax({
      url: serverURL+"/restfull/api/upload",
      data: data,
      cache: false,
      contentType: false,
      processData: false,
      method: 'POST',
      type: 'POST', // For jQuery < 1.9
      success: function(resData){
          alert(resData);
      }
  });
  }
  
    function getInputMetadata(){
      globalMetadata.title = $('#input_title').val();
      globalMetadata.description = $('#text_description').val();
      globalMetadata.language = $('#select_language').val();
      globalMetadata.license = $('#select_license').val();
      let persons = [];
      var tb = $('#author_table:eq(0) tbody');
      tb.find("tr").each(function(index, element) {
        var colSize = $(element).find('td').length;
        console.log("  Number of cols in row " + (index + 1) + " : " + colSize);
        let currentPerson = {};
        $(element).find('td').each(function(index, element) {
        var $th = $(element).closest('table').find('th').eq(index);
          if(index > 0 && index < 8){
            currentPerson[$th.text()] = $(element).text();
            var colVal = $(element).text();
            console.log("    Value in col " + (index + 1) + " : " + colVal.trim());
          }else if(index == 8){
            currentPerson[$th.text()] = $(element).find('select').val();
          }
        });
        console.log("finished person_ "+JSON.stringify(currentPerson));
        persons.push(currentPerson);
      });
      globalMetadata.persons = persons;
  
      let subjects = [];
      var tb = $('#subject_table:eq(0) tbody');
      tb.find("tr").each(function(index, element) {
          if($(element).children('td:nth-child(2)').text()){
            subjects.push($(element).children('td:nth-child(2)').text());
          }
      });
      globalMetadata.subjects = subjects;
      console.log(JSON.stringify(globalMetadata));
      return globalMetadata;
    }
  
  
    function addSubjectRow(){
        markup = "<tr><td class='text-center'><button class='btn btn-outline-secondary btn-sm' onclick='deleteRow(this)'>-</button></td><td contenteditable></td></tr>";
        tableBody = $("#subject_table > tbody");
        tableBody.append(markup);
    }
  
    function addAuthorRow(){
        markup = "<tr><td class='text-center'><button class='btn btn-outline-secondary btn-sm' onclick='deleteRow(this)'>-</button></td><td contenteditable></td><td contenteditable></td><td class='text-center'><button type='button' class='btn btn-sm btn-outline-secondary waves-effect' onclick='searchORCIDsOfRow(this.parentNode.parentNode)' data-toggle='modal' data-target='#myModal'><i class='fa-solid fa-magnifying-glass'></i></button></td><td contenteditable></td><td contenteditable></td><td contenteditable></td><td contenteditable></td><td ><select class='form-control form-control-sm''><option>Creator</option><option>Contributor</option></select></td></tr>";
        tableBody = $("#author_table > tbody");
        tableBody.append(markup);
        document.getElementById("addAuthorButton").scrollIntoView();
    }

    function searchORCIDsOfRow(row){
      $('#myModal').modal('show');
      currentAuthorRow = row;
      let form = new FormData();
      let firstName = $(row.childNodes[1]).text();
      let lastName = $(row.childNodes[2]).text();

      $('#orcids').empty();
      if(!firstName || firstName.length === 0 || !lastName || lastName.length === 0){
        $("#modal-orcid-title").text(`Please set a first name and a last name'`);
        $('#orcids').text("0 orcids match this name");
        $('#myModal').modal('show');
        return;
      }else{
        $("#modal-orcid-title").text(`Please choose the correct ORCID for '${firstName} ${lastName}'`);
      }
      console.log(`${serverURL}/restfull/api/searchORCID/${firstName}/${lastName}`);
      $.post(`${serverURL}/restfull/api/searchORCID/${firstName}/${lastName}`,function(ids){
        if(ids.length > 0){
          ids.forEach( (id, index) => {
            let orcidUrl = "https://orcid.org/"+id;
            let wrapped = '<div class="d-flex my-2"><button class="btn" >Choose</button><a class="mx-3 vertical-centerd-text" href='+orcidUrl+' target="_blank">'+orcidUrl+'</a><div class="vertical-centerd-text">'+firstName+' '+lastName+'</div></div>';
            $('#orcids').append(wrapped);
            document.getElementById('orcids').childNodes[index].childNodes[0].onclick = () => { 
              $('#myModal').modal('hide');
              let div = document.createElement("div");
              $(row.childNodes[3]).empty();
              $(row.childNodes[3]).append(div);
              $(div).text(id);
              div.classList.add("link");
              div.onclick = () => { searchORCIDsOfRow(row); $('#myModal').modal('show');};
            };
          })
        }else{
          $('#orcids').text("0 orcids match this name");
        }
      } );
    }

    function setOrcid(){
      if(currentAuthorRow != null){
        let div = document.createElement("div");
        $(currentAuthorRow.childNodes[3]).empty();
        if(document.getElementById("modal-input").value.length === 0){
          let btn = "<button type='button' class='btn btn-sm btn-outline-secondary waves-effect' onclick='searchORCIDsOfRow(this.parentNode.parentNode)' data-toggle='modal' data-target='#myModal'><i class='fa-solid fa-magnifying-glass'></i></button>";
          $(currentAuthorRow.childNodes[3]).append(btn);
        }else{
          $(currentAuthorRow.childNodes[3]).append(div);
          $(div).text(document.getElementById("modal-input").value);
          div.classList.add("link");
          div.onclick = () => { searchORCIDsOfRow(currentAuthorRow); $('#myModal').modal('show');};
        }
      }
    }

    function setORCID(cell, id){
      console.log(cell+" "+ id);
    }
  
    function deleteRow(row){
        $(row).parents("tr").remove();
    }
  
    async function traverse(item){
      return new Promise(async (resolve) => {
        let requests = 0;
        console.log("started traverse with stack");
        stack.push(item);
        let counter = 0;
        while(stack.length > 0){
          let currentItem = stack.pop();    
            if(requests > 20){
                if(counter++ == 0){
                    let result = await uploadEntityAndMetadata(currentItem.fullPath ,getInputMetadata());
                  }else{
                    let result = await uploadEntity(currentItem);
                  }
            }else{
                if(counter++ == 0){
                    requests++;
                    let result = await uploadEntityAndMetadata(currentItem.fullPath ,getInputMetadata()).then((result) => {
                        requests--;
                    });
                  }else{
                    if(currentItem.isDirectory){
                        let result = await uploadEntity(currentItem);
                    }else{
                        requests++;
                        uploadEntity(currentItem).then((result) => {
                            requests--;
                        });;
                    }

                  }
            }
          if(currentItem.isDirectory){
            let directoryReader = currentItem.createReader();
            let test = await readChilds(directoryReader);
          }
        }
        resolve("Finished!");
      });
    }
  
    function readChilds(reader){
      return new Promise((resolve) => {
        function readEntries() {
            // According to the FileSystem API spec, readEntries() must be called until
            // it calls the callback with an empty array.
            reader.readEntries((batchEntries) => {
              if (!batchEntries.length) {
                resolve("finished with reading directory");
              } else {
                batchEntries.forEach(function(entry) {
                    stack.push(entry);
                });
                // Try calling readEntries() again for the same dir, according to spec
                readEntries();
              }
            });
          }
          // initial call to recursive entry reader function
          readEntries();


        // reader.readEntries(function(entries) {
        //     entries.forEach(function(entry) {
        //       stack.push(entry);
        //     });
        //     resolve("finished with reading directory");
        // });
      });
    }
  
    async function uploadEntityAndMetadata(fullPath,metadata){
      return new Promise(async (resolve) => {
      let payload = new FormData();
      payload.append("name",fullPath);
      payload.append("email",email);
      payload.append("datasetRoot",globalMetadata.title);
      payload.append("subject",email);
      payload.append("metadata",JSON.stringify(metadata));
        jQuery.ajax({
          url: serverURL+"/restfull/api/uploadEntityAndMetadata",
          data: payload,
          cache: false,
          contentType: false,
          processData: false,
          method: 'POST',
          type: 'POST', // For jQuery < 1.9
          success: function(resData){
              resolve("finished dir upload "+resData);
          }
      });
      });
    }
  
  
    async function uploadEntity(entry){
      return new Promise(async (resolve) => {
          let payload = new FormData();
          payload.append("name",entry.fullPath);
          payload.append("email",email);
          payload.append("datasetRoot",globalMetadata.title);
          if(entry.isDirectory){
            payload.append("type","Directory");
            payload.append('file',null);
            jQuery.ajax({
              url: serverURL+"/restfull/api/uploadEntity",
              data: payload,
              cache: false,
              contentType: false,
              processData: false,
              method: 'POST',
              type: 'POST', // For jQuery < 1.9
              success: function(resData){
                  resolve("finished dir upload "+resData);
              }
          });
          }else{
            payload.set("name",entry.fullPath);
            entry.file(async function (file){
              payload.append('file',file);
              payload.append("type","File");
              jQuery.ajax({
                url: serverURL+"/restfull/api/uploadEntity",
                data: payload,
                cache: false,
                contentType: false,
                processData: false,
                method: 'POST',
                type: 'POST', // For jQuery < 1.9
                success: function(resData){
                  resolve("finished file upload "+resData);
                }
                });
                //$.post( serverURL+"/restfull/api/uploadEntity", JSON.stringify(requestData), function(data){
                  //resolve("finished!!");
                //});
            });
          }
      });
    }
  
    function scanFiles(item) {
      console.log("started scanFiles Function");
      let elem = document.createElement("li");
      if (item.isDirectory) {
        let directoryReader = item.createReader();
        directoryReader.readEntries(async function(entries) {
            if(entries.length == 0){
              emptyDirectories++;
              $('#boxtitle').text(fileSystemEntry.fullPath+" empty entries: "+emptyDirectories);
            }else{
              entries.forEach(function(entry) {
                scanFiles(entry);
              });
            }
        });
      }else{
        item.file((file) => {
          if(file.size == 0){
            emptyDirectories++;
            $('#boxtitle').text(fileSystemEntry.fullPath+" empty entries: "+emptyDirectories);
          }
        });
      }
    }
  
  function traverseDirectory2(entry) {
    const reader = entry.createReader();
    let countReads = 0;
    // Resolved when the entire directory is traversed
    return new Promise((resolveDirectory) => {
      setTimeout(() => {
        const iterationAttempts = [];
        const errorHandler = () => {};
        function readEntries() {
          countReads++;
          // According to the FileSystem API spec, readEntries() must be called until
          // it calls the callback with an empty array.
          reader.readEntries((batchEntries) => {
            if (!batchEntries.length) {
              // Done iterating this particular directory
              if(countReads == 1){
                //if the results at first read was empty -> inc counter
                emptyDirectories++;
                $('#droplabel').text(fileSystemEntry.fullPath+" empty entries: "+emptyDirectories);
              }
              resolveDirectory(Promise.all(iterationAttempts));
            } else {
              // Add a list of promises for each directory entry.  If the entry is itself
              // a directory, then that promise won't resolve until it is fully traversed.
              iterationAttempts.push(Promise.all(batchEntries.map((batchEntry) => {
                if (batchEntry.isDirectory) {
                  console.log("dir: "+batchEntry.name);
                  return traverseDirectory2(batchEntry);
                }else{
                batchEntry.file((file) => {
                  if(file.size == 0){
                    emptyDirectories++;
                    $('#droplabel').text(fileSystemEntry.fullPath+" empty entries: "+emptyDirectories);
                  }
                });
                }
                return Promise.resolve(batchEntry);
              })));
              // Try calling readEntries() again for the same dir, according to spec
              readEntries();
            }
          }, errorHandler);
        }
        // initial call to recursive entry reader function
        readEntries();
      }, 20);
  
    });
  }
  
  async function traverseDirectory3(entry,metadata) {
    if(metadata == null){
      let result = await uploadEntity(entry);
    }else{
      let result = await uploadEntityAndMetadata(entry.fullPath, metadata);
    }
  
    const reader = entry.createReader();
    // Resolved when the entire directory is traversed
    return new Promise((resolveDirectory) => {
        const iterationAttempts = [];
        const errorHandler = () => {};
        function readEntries() {
          // According to the FileSystem API spec, readEntries() must be called until
          // it calls the callback with an empty array.
          reader.readEntries((batchEntries) => {
            if (!batchEntries.length) {
              // Done iterating this particular directory
              resolveDirectory(Promise.all(iterationAttempts));
            } else {
              // Add a list of promises for each directory entry.  If the entry is itself
              // a directory, then that promise won't resolve until it is fully traversed.
              iterationAttempts.push(Promise.all(batchEntries.map(async (batchEntry) => {
                if (batchEntry.isDirectory) {
                  return await traverseDirectory3(batchEntry, null);
                }else{
                    let result = await uploadEntity(batchEntry);
                }
                return Promise.resolve(batchEntry);
              })));
              // Try calling readEntries() again for the same dir, according to spec
              readEntries();
            }
          }, errorHandler);
        }
        // initial call to recursive entry reader function
        readEntries();
    });
  }
  
  async function traverseDirectory4(entry) {
  
    let payload = new FormData();
    payload.append("name",entry.fullPath);
    payload.append("type","Directory");
    payload.append("email",email);
    payload.append("metadata",JSON.stringify(getInputMetadata()));
    let result = await uploadEntityAndMetadata(payload);
    const reader = entry.createReader();
    // Resolved when the entire directory is traversed
    return new Promise((resolveDirectory) => {
      setTimeout(() => {
  
        const iterationAttempts = [];
        const errorHandler = () => {};
        function readEntries() {
          // According to the FileSystem API spec, readEntries() must be called until
          // it calls the callback with an empty array.
          reader.readEntries((batchEntries) => {
            if (!batchEntries.length) {
              // Done iterating this particular directory
              resolveDirectory(Promise.all(iterationAttempts));
            } else {
              // Add a list of promises for each directory entry.  If the entry is itself
              // a directory, then that promise won't resolve until it is fully traversed.
              iterationAttempts.push(Promise.all(batchEntries.map((batchEntry) => {
                if (batchEntry.isDirectory) {
                  return traverseDirectory3(batchEntry);
                }else{
                  uploadEntity(payload, batchEntry);
                }
                return Promise.resolve(batchEntry);
              })));
              // Try calling readEntries() again for the same dir, according to spec
              readEntries();
            }
          }, errorHandler);
        }
        // initial call to recursive entry reader function
        readEntries();
      }, 20);
    });
  }
  
  function auth(){
    console.log("test auth");
    let data = new FormData();
    data.append("email",email);
  
    jQuery.ajax({
      url: serverURL+"/restfull/api/authenticate",
      data: data,
      cache: false,
      contentType: false,
      processData: false,
      method: 'POST',
      type: 'POST',
      success: function(serialized){
      let payload = new FormData();
      payload.append("subject",serialized);
      jQuery.ajax({
        url: serverURL+"/restfull/api/reuseSubject",
        data: payload,
        cache: false,
        contentType: false,
        processData: false,
        method: 'POST',
        type: 'POST',
        success: function(resData){
            console.log("auth() response: "+resData);
        }
      });
      }
    });
  }

      /* converts bytes to the human readable equivalent */
    function niceBytes(bytes)  {
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
        } while (Math.round(Math.abs(bytes) * r) / r >= thresh && u < units.length - 1);
  
        return bytes.toFixed(dp).replace(".", ",") + ' ' + units[u];
      };

      /* Helper function to animate the increasing progression % text on progress-bars*/
      function animateValue(id, start, end, duration) {
        // assumes integer values for start and end
        
        var obj = document.getElementById(id);
        var range = end - start;
        // no timer shorter than 50ms (not really visible any way)
        var minTimer = 50;
        // calc step time to show all interediate values
        var stepTime = Math.abs(Math.floor(duration / range));
        
        // never go below minTimer
        stepTime = Math.max(stepTime, minTimer);
        
        // get current time and calculate desired end time
        var startTime = new Date().getTime();
        var endTime = startTime + duration;
        var timer;
      
        function run() {
            var now = new Date().getTime();
            var remaining = Math.max((endTime - now) / duration, 0);
            var value = Math.round(end - (remaining * range));
            obj.innerHTML = value+"%";
            if (value == end) {
                clearInterval(timer);
            }
        }
        
        timer = setInterval(run, stepTime);
        run();
    }