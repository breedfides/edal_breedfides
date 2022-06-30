/* Web Worker for traversing a directory */
const worker = new Worker("/js/edal_traverse.js");

/* Container for selected/dropped file/directory */
let files = {};

/* For timing/throttling the requests when entering keys on the title field, see checkIfExists()*/
let keytimer;

/* Flag for input validation */
let titleAlreadyExists = false;

/* For avoiding submit/summary dialog, if the Title validation hasnt finished yet */
let checkingTitle = false;

/* For readability */
let storage = localStorage;

/* To Store the row in which the user clicked on the searchOrcid Button */
let currentAuthorRow = null;

/* To detect if the dataset was fully uploaded */
let fileCounter = 0;

/* Also to detect finished upload and to calculate the progression */
let numberOfFiles = 0;

/* Store the current parallel request count */
let requests = 0;

/* set the limit of possible parallel file uploads for throttling */
const numberOfMaxAllowedParallelUploads = 6;
const displayAlertTime = 5000;
const tooltip_text = "Year when the data is made publicly available";
uploadCanceled = false;
//for calculation of readable filesizes instead of raw bits and bytes
let units = ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

const updateProgressTimeout = 1000;


/* Input validation function that makes a REST call to test if
 the entered title already exists for the current user */
function checkIfExists(input){
    if($(input).val() != ''){
        clearTimeout(keytimer);
        keytimer = setTimeout(() => {
            let form = new FormData();
            form.append("email",email);
            form.append("name",$(input).val());
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

function browserBlocked(){
  var browserAllowed = (function (agent) {        switch (true) {
    case agent.indexOf("edge") > -1: return true;
    case agent.indexOf("edg/") > -1: return true;
    case agent.indexOf("opr") > -1 && !!window.opr: return false;
    case agent.indexOf("chrome") > -1 && !!window.chrome: return false;
    case agent.indexOf("trident") > -1: return true;
    case agent.indexOf("firefox") > -1: return true;
    case agent.indexOf("safari") > -1: return true;
    default: return true;
    }
  })(window.navigator.userAgent.toLowerCase());
  return browserAllowed;
}

function checkNames(cell){
  console.log(new Boolean(cell.textContent));
  if(cell.parentNode.children.item(1).textContent || cell.parentNode.children.item(2).textContent){
    $(cell.parentNode.children.item(4)).attr('contenteditable','false');
    $(cell.parentNode.children.item(4)).css("background-color", "dimgrey");
    //showAlertMessage("Only allowed to set Legalname if no Firstname and no Lastname is set", displayAlertTime);
  }else{
    $(cell.parentNode.children.item(4)).attr('contenteditable','true');
    $(cell.parentNode.children.item(4)).css("background-color", "white");
  }
}



function checkLegalName(cell){
  if(cell.textContent){
    console.log("LEGAL NAME "+cell.textContent);
    $(cell.parentNode.children.item(1)).attr('contenteditable','false');
    $(cell.parentNode.children.item(1)).css("background-color", "dimgrey");
    $(cell.parentNode.children.item(2)).attr('contenteditable','false');
    $(cell.parentNode.children.item(2)).css("background-color", "dimgrey");
    let orcIdBtn = "<button type='button' class='btn btn-sm btn-outline-secondary waves-effect' onclick='searchORCIDsOfRow(cell.parentNode.children.item(3))' data-toggle='modal' data-target='#myModal'><i class='fa-solid fa-magnifying-glass'></i></button>";
    let button = document.createElement('button');
    button.classList.add("btn", "btn-sm","btn-outline-secondary", "waves-effect");
    button.onclick = () => {searchORCIDsOfRow(cell.parentNode)   };
    $(button).attr('type','button');
    $(button).attr('data-target','#myModal');
    $(button).attr('data-toggle','modal');
    $(cell.parentNode.children.item(3)).empty();
    $(cell.parentNode.children.item(3)).append(button);
    $(button).html("<i class='fa-solid fa-magnifying-glass'></i>");
    $(cell.parentNode.children.item(3).firstChild).prop('disabled',true);
    $(cell.parentNode.children.item(3)).css("background-color", "dimgrey");
  }else{
    $(cell.parentNode.children.item(1)).attr('contenteditable','true');
    $(cell.parentNode.children.item(1)).css("background-color", "white");
    $(cell.parentNode.children.item(2)).attr('contenteditable','true');
    $(cell.parentNode.children.item(2)).css("background-color", "white");
    $(cell.parentNode.children.item(3).firstChild).prop('disabled',false);
    $(cell.parentNode.children.item(3)).css("background-color", "white");
  }
}

function showAlertMessage(text, displayTimeInMillis){
  $('#alert-information').empty();
  $('#alert-information').append(text);
  showAlert(displayTimeInMillis);
}

function showAlert(displayTimeInMillis){
  $('.alert').show();
  setTimeout(function (){$('.alert').fadeOut()}, displayTimeInMillis); 
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
    $('#alert-information').empty();
    //validate authors
    let authorsIncomplete = false;
    let atLeastOneAuthor = false;
    var tb = $('#author_table:eq(0) tbody');
    let notValidAuthorRows = [];
    tb.find("tr").each(function(rowIndex, element) {
      let valid = true;
      $(element).find('td').each(function(index, element) {
        if(index > 0 && index < 8 && element.isContentEditable && index != 3 && index != 4){
          var colVal = $(element).text();
          if(!colVal){
            valid = false;
            authorsIncomplete = true;
          }
        }
        if(index == 8 && $(element).find('select').val() == "Creator"){
          atLeastOneAuthor = true;
        }
      });
      if(!valid){
        let msg = `${element.cells[1].textContent} ${element.cells[2].textContent} ${rowIndex+1}.row`;
        notValidAuthorRows.push(msg);
      }
    });
    if(authorsIncomplete){
        $('#alert-information').append('<strong>Missing Author information: </strong>Please complete every author row:\n');
        notValidAuthorRows.forEach((msg) => {
          $('#alert-information').append(`${msg}\n`);
        });
        if(!failedValidations.size){
            showAlert(displayAlertTime);
            return false;
        }
        failedValidations.set(tb,{label:'Missing Author information: ',text:'Please complete every author row or delete it.'});
    }
    if(!atLeastOneAuthor){
      showAlertMessage('Please add at least one creator.',displayAlertTime);
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
            showAlert(displayAlertTime);
            return false;
        }
    }
    //validate if a dataset was selected

    if(fileSystemEntry == null){
        $('#alert-information').append('<strong>Missing Dataset: </strong>Please drop or select a dataset to be uploaded.');
        if(!failedValidations.size){
            showAlert(displayAlertTime);
            return false;
        }
    }
    // if map only contains
    if(failedValidations.size > 1){
        for (const [key, value] of failedValidations.entries()) {
            $('#alert-information').append(`<strong>${value.label}</strong>${value.text}`);
            $('#alert-information').append('<br>');
        }
        showAlert(displayAlertTime);
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

/* Fill summary section with a table of entered metadata */
function setSummary(){
  getInputMetadata();
  let citaionPreview = "";
  if(globalMetadata.creators.length < 4){
    globalMetadata.creators.forEach((creator) => {
      if('Legalname' in creator){
        citaionPreview += `${creator.Legalname}; `;
      }else{
        citaionPreview += `${creator.Lastname}, ${creator.Firstname}; `;
      }
    });
  }else{
    if('Legalname' in globalMetadata.creators[0]){
      citaionPreview += `${crglobalMetadata.creators[0].Legalname} et al.; `;
    }else{
      citaionPreview += `${globalMetadata.creators[0].Lastname} et al.;`;
    }

  }
  citaionPreview += `(${new Date().getFullYear()}): `
  citaionPreview += `${globalMetadata.title}; `
  citaionPreview += `${globalMetadata.creators[0].Address}; `
  citaionPreview += `${globalMetadata.creators[0].Zip}; `
  citaionPreview += `${globalMetadata.creators[0].Country};`

  $('#summary').empty();
  $('#summary').append(`<table class="preview">`);
  $('#summary').append(`<tr><td><h6 style="text-decoration:underline;">Citation preview: </h6></td><td class="pl-2">${citaionPreview}</td></tr>`);
  $('#summary').append(`<tr><td></td><td class="pl-2"></td></tr>`);
  $('#summary').append(`<tr><td><h5>Metadata overview </h5></td></td></tr>`);
  $('#summary').append(`<tr><td><b>Title: </b></td><td class="pl-2">${globalMetadata.title}</td></tr>`);
  $('#summary').append(`<tr><td><b>Language: </b></td><td class="pl-2">${globalMetadata.language_full}</td></tr>`);
  $('#summary').append(`<tr><td><b>Description: </b></td><td class="pl-2">${globalMetadata.description}</td></tr>`);
  $('#summary').append(`<tr><td><b>License: </b></td><td class="pl-2" >${globalMetadata.licenseText}</td></tr>`);
  $('#summary').append(`<tr><td><b>Subjects: </b></td><td class="pl-2">${globalMetadata.subjects.join(', ')}</td></tr>`);
  for (let i = 0; i < globalMetadata.creators.length; i++) {
    if(i == 0){
      $('#summary').append(`<tr><td><b>Creator: </b></td><td class="pl-2">${Object.keys(globalMetadata.creators[i]).map(function(k){return globalMetadata.creators[i][k]}).join(", ")}</td></tr>`);
    }else{
      $('#summary').append(`<tr><td></td><td class="pl-2">${Object.keys(globalMetadata.creators[i]).map(function(k){return globalMetadata.creators[i][k]}).join(", ")}</td></tr>`);
    }
  }
  $('#summary').append(`</tr>`);
  for (let i = 0; i < globalMetadata.contributors.length; i++) {
    if(i == 0){
      $('#summary').append(`<tr><td><b>Contributor: </b></td><td class="pl-2">${Object.keys(globalMetadata.contributors[i]).map(function(k){return globalMetadata.contributors[i][k]}).join(", ")}</td></tr>`);
    }else{
      $('#summary').append(`<tr><td></td><td class="pl-2">${Object.keys(globalMetadata.contributors[i]).map(function(k){return globalMetadata.contributors[i][k]}).join(", ")}</td></tr>`);
    }
  }
  $('#summary').append(`</tr>`);
  $('#summary').append(`</table>`);
}

async function showUploadDialog(){
    //let msg = await traverse(fileSystemEntry, listing);
    if(validateInputs()){
      console.log("in showUpload()-----------");
      setSummary();
      $('#toggleStartButton').prop("checked", false);
      $('#upload-start-button').show();
      $('#upload-start-button').prop('disabled',true);
      $('#upload-title').text('Upload Progress');


      $('.progress-bar').css('transition','width .6s linear');
      //$('#file-counter-info').text("Uploaded 0/"+Object.keys(files).length);
      $('.progress').css('height','17px');
      $('.progress-bar').text('0%'); 
      $('#summary_agreement').addClass("d-flex").removeClass("d-none");
      $('.parallel-uploads').empty();
      $('.parallel-uploads').removeClass("d-flex").addClass("d-none");
      $('#myModal2').modal('show');
      $('#upload-close-button').text('back');
      $('#cancelUploadDialogCross').attr('data-target','');
      $('#upload-close-button').attr('data-target','');
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

  function logout(){
    window.open(serverURL+'/preview', 'https://doi.ipk-gatersleben.de/search').focus();
  }

  function toggleStartUpload(){
    $('#upload-start-button').prop('disabled', !$('#upload-start-button').is(":disabled"));
  }

  async function startUpload(){
    $('#summary_agreement').removeClass("d-flex").addClass("d-none");
    $('.parallel-uploads').addClass("d-flex").removeClass("d-none");
    fileCounter = 0;
    if(fileSystemEntry.kind == "directory"){
      startDirectoryUpload();
    }else if(fileSystemEntry.kind == "file"){
      startSingleFileUpload();
    }
    $('#upload-start-button').hide();
    $('#upload-close-button').text('cancel');
    $('#cancelUploadDialogCross').attr('data-target','#cancelUploadDialog');
    $('#upload-close-button').attr('data-target','#cancelUploadDialog');
    $('#submitBtn').contents().filter(function(){
      return this.nodeType === 3;
    }).remove();
    $('#submitBtn').append('Show Progress');
    document.getElementById("submitBtn").onclick = () => {
    $('#myModal2').modal('show');
    }
  }

async function startSingleFileUpload(){
  await uploadEntityAndMetadata(globalMetadata.title,globalMetadata);
  const file = await fileSystemEntry.getFile();
  files["file"] = file;
  uploadSingleEntityDataset(fileSystemEntry.name, file);
}


async function startDirectoryUpload(){
  console.log("started directory upload");
    await uploadEntityAndMetadata(globalMetadata.title,globalMetadata);
    await uploadEntity2(fileSystemEntry.name, null);
    $('#upload-title').text(`Upload Progress: ${fileCounter} of ${numberOfFiles} files`);
    async function iterateFiles(files){
        requests = 0;
        uniqueProgressId = 0;
        $('.parallel-uploads').empty();
        return new Promise(async (resolve) => {
            for (var key in files) {
                if(!uploadCanceled){
                  const progress_id = "file-no-"+uniqueProgressId++;
                  console.log("progrss-id vor nutzung: "+progress_id)
                  if (files.hasOwnProperty(key)) {
                      if(files[key].isDirectory){
                          await uploadEntity2(key, null);
                      }else{
                          if(requests < numberOfMaxAllowedParallelUploads){
                              requests++;
                              uploadEntity2(key, files[key].file, progress_id).then(function(result) {
                                requests--;
                              });;
                          }else{
                              await uploadEntity2(key, files[key].file,progress_id);
                          }
                          //resumable.addFile(files[key].file);
                      }
                  }
                }
            }
        });
        
    }
    iterateFiles(files);

    //resumable.upload();
}

function publishDataset(){
  $('#upload-title').text('Publication progress');
  $('.progress-bar').css('transition','width .1s linear');
  $('.progress-bar').css('width','0%');
  $('#droplabel').empty();
  $('#droplabel').append(`Drop or Select a single&nbsp;<div class="chooseFileDirectory" onclick="chooseFile();">file&nbsp;</div>or&nbsp;<div class="chooseFileDirectory" onclick="chooseDirectory();">directory </div>`);
  let form = new FormData();
  form.append("email",email);
  form.append("name", globalMetadata.title);
  if($('#input_embargo').val().length > 0){
    form.append("embargo",$('#input_embargo').val());
  }
  if(!uploadCanceled){
    jQuery.ajax({
      url: serverURL+"/restfull/api/publishDataset",
      data: form,
      cache: false,
      contentType: false,
      processData: false,
      method: 'POST',
      type: 'POST',
      success: function(resData){
        setTimeout(() => {
          $('.progress-bar').css('transition','width 1s ease');
          $('.progress-bar').css('width','100%');
          $('.parallel-uploads').removeClass("d-flex").addClass("d-none");
          $('#upload-close-button').text('close');
          $('#cancelUploadDialogCross').attr('data-target','');
          $('#upload-close-button').attr('data-target','');
        }, 2000);
        resetUI();
      }
    });
    fileSystemEntry = null;
  }
  
}

function resetUI(){
  document.getElementById('submitBtn').onclick = () => {showUploadDialog();};
  $('#file-counter-info').text("");
  $('#submitBtn').contents().filter(function(){
    return this.nodeType === 3;
  }).remove();
  $('#submitBtn').append('Start Upload');  
}

/** Update the "Parent" Progress bar of the uplaod dialog, that is placed at the top of the modal **/
async function updateUploadedFiles(counter){
  setTimeout(function (){
    //$('#file-counter-info').text("Uploaded +"+counter+"/"+Object.keys(files).length);
    $('.progress-bar').css('width',`${(Math.round(((((counter)/numberOfFiles)) + Number.EPSILON) * 100)/100)*100}%`);
    $('.progress-bar').text(`${Math.round((Math.round(((((counter)/numberOfFiles)) + Number.EPSILON) * 100)/100)*100)}%`);                 
  }, updateProgressTimeout);

}

async function uploadEntity2(path, file, progressIdentifier){
    return new Promise(async (resolve) => {
        let payload = new FormData();
        payload.append("path",path);
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
            },
            statusCode: {
              404: function() {
                cancelUpload('Server not reachable. Please try again later.')
              },
              0:function() {
                cancelUpload('Server not reachable. Please try again later.')
              },
            }
        });
        }else{

            payload.set("path",path);
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
                },
                statusCode: {
                  404: function() {
                    cancelUpload('Server not reachable. Please try again later.')
                  },
                  0:function() {
                    cancelUpload('Server not reachable. Please try again later.')
                  },
                }
              });

               /* Add progress ui for this file with the key as ID to upload progresses dialog  */      
               console.log("GIVEN progress label:_ "+progressIdentifier);   
               markup = `<div id='${progressIdentifier}-container' class='d-flex flex-row mt-2 mb-2' style='text-align:center;align-items:center;'><div class='file-progress-name mr-2'>: ${file.name} (${niceBytes(file.size)})</div><div class='progress w-100 submitbtn' style='height:17px;'><div class="single-file-progressbar" id=${progressIdentifier} >0%</div></div></div>`;
               $(".parallel-uploads").append(markup);      
               updateFileProgress(email+path, progressIdentifier);

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
      payload.set("path",path);
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
          },
          statusCode: {
            404: function() {
              cancelUpload('Server not reachable. Please try again later.')
            },
            0:function() {
              cancelUpload('Server not reachable. Please try again later.')
            },
          }
          });   
         updateFileProgress(email+path, "parent-progress-bar");
    });
  }

  /** To change the progress bar of an individual file upload and to detect when the upload is finished **/
  async function updateFileProgress(path, progressId){
    console.log("file progress label:_ "+progressId);
    let payload = new FormData();
    payload.append('key',path);
    jQuery.ajax({
      url: serverURL+"/restfull/api/getProgress",
      data: payload,
      cache: false,
      contentType: false,
      processData: false,
      method: 'POST',
      type: 'POST', // For jQuery < 1.9
      success: function(progress){
        if(progress >= 100){
          document.getElementById(progressId).style.width ="100%";
          animateValue(progressId, Number(document.getElementById(progressId).textContent.slice(0, -1)), 100, updateProgressTimeout);
          setTimeout(function (){
            $(`#${progressId}-container`).addClass("d-none").removeClass("d-flex"); 
            fileCounter++;
            $('#upload-title').text(`Upload Progress: ${fileCounter} of ${numberOfFiles} files`);
            updateUploadedFiles(fileCounter);
            if(fileCounter == numberOfFiles){
              publishDataset();                
            }               
          }, updateProgressTimeout);
        }else if(progress > 0 && progress < 100){
          console.log("file upload progress display continues");
          document.getElementById(progressId).style.width = progress+"%";
          animateValue(progressId, Number(document.getElementById(progressId).textContent.slice(0, -1)), progress, updateProgressTimeout);
          //$(`#${progressId}`).text(`${progress}%`);
          setTimeout(function (){
            $(`#${progressId}-container`).addClass("d-flex").removeClass("d-none");
            updateFileProgress(path, progressId)                    
          }, updateProgressTimeout);
        }else{
          setTimeout(function (){
            updateFileProgress(path, progressId)                    
          }, updateProgressTimeout);
        }
      }
      });
  }

  /* Handle message of the web worker. The Web worker is started if a file/direcotry was dropped or selected */
  worker.onmessage = (evt) => {
    //update the user inerface to show the progress of the Traversal
    if (typeof evt.data === 'string') {
        document.querySelector("pre").textContent = JSON.stringify( evt.data, (key, value) => {
            if( value instanceof Blob ) {
              return { name: value.name, size: value.size, type: value.type };
            }
            return value;
          }, 4 );

    //Finished traversing the directory
    }else{
        files = evt.data.traversed;
        numberOfFiles = evt.data.numberOfFiles;
        document.querySelector("pre").textContent = `Directory: ${fileSystemEntry.name}, ${numberOfFiles}`;
        if(evt.data.tempFiles > 0){
          fileSystemEntry = null;
          if(evt.data.tempFiles > 1){
            showAlertMessage(`The choosen directory <b>contains ${evt.data.tempFiles} temporary files</b>. \n .tmp files, .foo files and files with leading ~$ are not allowed. \n Please delete them or choose a valid directory.`, 5*displayAlertTime);
          }else{
            showAlertMessage(`The choosen directory <b>contains a temporary file</b>. \n .tmp files, .foo files and files with leading ~$ are not allowed. \n Please delete the file or choose a valid directory.`, 5*displayAlertTime);
          }
        }else if(numberOfFiles == 0){
          fileSystemEntry = null;
          showAlertMessage("Please select or drop a directory with at least 1 valid file.", 5*displayAlertTime);
        }else{
          createAndShowBadFiles(evt);
        }

        for (var key in files) {
            if (files.hasOwnProperty(key)) {
                $('#submitBtn').prop('disabled', false);
            }
        }
    }

  }

  /* Fills a modal dialog with the information about how many empty/compressed files/directories were found and the file paths */
  function createAndShowBadFiles(evt){
    if(evt.data.emptyFilePaths.length > 0 || evt.data.compressedFilePaths.length > 0){
      let footer = "";
      let foundFiles = "";
      if(evt.data.emptyFilePaths.length > 0 && evt.data.compressedFilePaths.length > 0){
        foundFiles = `Found ${evt.data.emptyFilePaths.length} empty file(s)/directorie(s) and ${evt.data.compressedFilePaths.length} compressed file(s):`;
        footer += `</table></div><p class="mb-2">1. Please note that empty files/directories will be ignored during upload.\n2. Also We recommend to use directories without compressed files.</p><button class='btn btn-outline-primary' style='width:fit-content;margin: auto;' onclick='dismissPrimaryAlert();'>Continue</button></div>`;
      }else if(evt.data.emptyFilePaths.length > 0){
        foundFiles = evt.data.emptyFilePaths.length > 1 ? `Found ${evt.data.emptyFilePaths.length} empty files/directories:` : `Found ${evt.data.emptyFilePaths.length} empty file/directorie:`;
        footer += `</table></div><p class="mb-2">Please note that empty files/directories will be ignored during upload.</p><button class='btn btn-outline-primary' style='width:fit-content;margin: auto;' onclick='dismissPrimaryAlert();'>Continue</button></div>`;
      }else{
        foundFiles = evt.data.compressedFilePaths.length > 1 ? `Found ${evt.data.compressedFilePaths.length} compressed files:` : `Found ${evt.data.compressedFilePaths.length} compressed file:` ;
        footer += `</table></div><p class="mb-2">We recommend to use directories without compressed files.</p><button class='btn btn-outline-primary' style='width:fit-content;margin: auto;' onclick='dismissPrimaryAlert();'>Continue</button></div>`;
      }    
      let alertMessage = `<div class='alert-table-container'><h5 class='mb-2'>${foundFiles}</h5><div class='alert-table-wrapper mb-2 px-2 py-1'><table class='alert-table mb-2'>`;       
      let rowNumber = 1;
      evt.data.compressedFilePaths.forEach((obj) => {
        alertMessage += `<tr><td class="pr-2 py-1 pl-1">${rowNumber++}.</td><td class="py-1" style="min-width:130px;">${obj.type}:</td><td class="pl-2 py-1 pr-1">${obj.path}</td>`;
      });
      evt.data.emptyFilePaths.forEach((obj) => {
        alertMessage += `<tr><td class="pr-2 py-1 pl-1">${rowNumber++}.</td> <td class="py-1"  style="min-width:130px;">${obj.type}:</td><td class="pl-2 py-1 pr-1">${obj.path}</td>`;
      });
      alertMessage += footer;
      //remove last ','
      $('.alert').removeClass( "alert-danger" ).addClass('alert-primary');
      $('#alert-information').empty();
      $('#alert-information').append(alertMessage);
      $('.alert').show();
    }
  }
  
  /* For drag and drop handling we use methos of the HTML Drag and Drop API
   that are experimental and only work in chrome, edge and opera.  */
  dropzone.addEventListener("dragover", function(event) {
      event.preventDefault();
  }, false);
  dropzone.addEventListener("drop", async function(event) {
    event.preventDefault();
    let items = event.dataTransfer.items;
    if(items.length > 1){
      showAlertMessage('You can only drop a single file or directory',displayAlertTime);
      return;
    }
  
    //let item = items[i].webkitGetAsEntry();
    let item = await items[0].getAsFileSystemHandle();
    console.log("dropped item");
    if(item.kind == 'directory' || item.kind == 'file'){
      uploadCanceled = false;
      fileSystemEntry = item;
      $('.progress').css('height','0');
      $('.progress-bar').css('width','0%');
      $('#submitBtn').contents().filter(function(){
          return this.nodeType === 3;
      }).remove();
      $('#submitBtn').append('Start upload');
      //const dirHandle = await showDirectoryPicker();
      if(item.kind == 'directory'){
        worker.postMessage( item );
      }else{
        document.querySelector("pre").textContent = `File: ${fileSystemEntry.name}`;
        numberOfFiles = 1;
      }
      checkIfExists(document.getElementById('input_title'));

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

  function dismissPrimaryAlert(){
    $(".alert").hide();
    $(".alert").removeClass( "alert-primary" ).addClass('alert-danger');
  }

  /* If a user wants to select a directory, this function is called to Show a dialog and handle the result */
  async function chooseDirectory(){
    const dirHandle = await showDirectoryPicker();
    fileSystemEntry = dirHandle;
    worker.postMessage( dirHandle );  
    checkIfExists(document.getElementById('input_title'));
    $('.progress').css('height','0');
    $('.progress-bar').css('width','0%');
    $('#submitBtn').contents().filter(function(){
        return this.nodeType === 3;
    }).remove();
    $('#submitBtn').append('Start upload');
    uploadCanceled = false;
  }

  /* If a user wants to select a file, this function is called to Show a dialog and handle the result */
  async function chooseFile(){
    let [fileHandle] = await window.showOpenFilePicker({multiple:false});
    fileSystemEntry = fileHandle;
    $('.progress').css('height','0');
    $('.progress-bar').css('width','0%');
    $('#submitBtn').contents().filter(function(){
        return this.nodeType === 3;
    }).remove();
    $('#submitBtn').append('Start upload');
    document.querySelector("pre").textContent = `File: ${fileSystemEntry.name}`;
    numberOfFiles = 1;
    checkIfExists(document.getElementById('input_title'));
    //const dirHandle = await showDirectoryPicker();
    uploadCanceled = false;

  }

    /* Harvest the input data and store it in the global variable globalMetaData*/
    function getInputMetadata(){
      globalMetadata.title = $('#input_title').val();
      globalMetadata.description = $('#text_description').val();
      globalMetadata.language = $('#select_language').val();
      globalMetadata.language_full = $('#select_language option:selected').text();
      globalMetadata.license = $('#select_license').val();
      globalMetadata.licenseText = $("#select_license option:selected").text();
      let authors = [];
      let contributors = [];
      var tb = $('#author_table:eq(0) tbody');
      tb.find("tr").each(function(index, element) {
        let currentPerson = {};
        $(element).find('td').each(function(index, element) {
        var $th = $(element).closest('table').find('th').eq(index);
          if(index > 0 && index < 8 && $(element).text()){
            currentPerson[$th.text()] = $(element).text();
            var colVal = $(element).text();
            console.log("    Value in col " + (index + 1) + " : " + colVal.trim());
          }else if(index == 8){
            currentPerson[$th.text()] = $(element).find('select').val();
          }
        });
        if(currentPerson.Type === "Creator"){
          authors.push(currentPerson);
        }else if(currentPerson.Type === "Contributor"){
          contributors.push(currentPerson);
        }
        delete currentPerson.Type;
      });
      globalMetadata.creators = authors;
      globalMetadata.contributors = contributors;
      let subjects = [];
      var tb = $('#subject_table:eq(0) tbody');
      tb.find("tr").each(function(index, element) {
          if($(element).children('td:nth-child(2)').text()){
            subjects.push($(element).children('td:nth-child(2)').text());
          }
      });
      globalMetadata.subjects = subjects;
      return globalMetadata;
    }
  
    /* Adds a row to the subject table */
    function addSubjectRow(){
        markup = "<tr><td class='text-center'><button class='btn btn-outline-secondary btn-sm' onclick='deleteRow(this)'>-</button></td><td contenteditable></td></tr>";
        tableBody = $("#subject_table > tbody");
        tableBody.append(markup);
    }

    /* Adds a row to the author table */
    function addAuthorRow(){
        markup = "<tr><td class='text-center'><button class='btn btn-outline-secondary btn-sm' onclick='deleteRow(this)'>-</button></td><td onkeyup='checkNames(this)'contenteditable></td><td onkeyup='checkNames(this)' contenteditable></td><td class='text-center'><button type='button' class='btn btn-sm btn-outline-secondary waves-effect' onclick='searchORCIDsOfRow(this.parentNode.parentNode)' data-toggle='modal' data-target='#myModal'><i class='fa-solid fa-magnifying-glass'></i></button></td><td onkeyup='checkLegalName(this)' contenteditable></td><td contenteditable></td><td contenteditable></td><td contenteditable></td><td ><select class='form-control form-control-sm''><option>Creator</option><option>Contributor</option></select></td></tr>";
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
          },
          statusCode: {
            404: function() {
              cancelUpload('Server not reachable. Please try again later.')
            },
            0:function() {
              cancelUpload('Server not reachable. Please try again later.')
            },
          }
        });
      });
    }

    function cancelUpload(text){
      if(!uploadCanceled){
        uploadCanceled = true;
        $('myModal2').hide();
        fileSystemEntry = null;
        resetUI();
        if(text){
          alert(text)
        }else{
          alert('The upload was canceled!')
        }

      }
    }
  
  
    async function uploadEntity(entry){
      return new Promise(async (resolve) => {
          let payload = new FormData();
          payload.append("path",entry.fullPath);
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
              },
              statusCode: {
                404: function() {
                  cancelUpload('Server not reachable. Please try again later.')
                },
                0:function() {
                  cancelUpload('Server not reachable. Please try again later.')
                },
              }
          });
          }else{
            payload.set("path",entry.fullPath);
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
                },
                statusCode: {
                  404: function() {
                    cancelUpload('Server not reachable. Please try again later.')
                  },
                  0:function() {
                    cancelUpload('Server not reachable. Please try again later.')
                  },
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