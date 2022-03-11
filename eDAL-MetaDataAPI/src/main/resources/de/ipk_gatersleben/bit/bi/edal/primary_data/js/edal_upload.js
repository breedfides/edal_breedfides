const worker = new Worker("/js/edal_traverse.js");
let files = {};

async function startUpload(){
    //let msg = await traverse(fileSystemEntry, listing);
    await startUpload2();
    //let msg = await traverse(fileSystemEntry);

    $('#submitBtn').prop('disabled', true);
    //console.log(msg);
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
          console.log("publish() response: "+resData);
      }
    });
  }

async function startUpload2(){
    let metadata = getInputMetadata();
    await uploadEntityAndMetadata(metadata.title,metadata);
    await uploadEntity2(fileSystemEntry.name, null);

    for (var key in files) {
        if (files.hasOwnProperty(key)) {
            if(files[key].isDirectory){
                await uploadEntity2(key, null);
            }else{
                await uploadEntity2(key, files[key].file);
            }
        }
    }
}

async function uploadEntity2(path, file){
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
                console.log(key+" "+JSON.stringify(files[key]));
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
  
    for (let i=0; i<items.length; i++) {
      //let item = items[i].webkitGetAsEntry();
      let item = await items[i].getAsFileSystemHandle();
      console.log("dropped item");
      if(item.kind == 'directory'){
        fileSystemEntry = item;
        //const dirHandle = await showDirectoryPicker();
        $('#droplabel').text(item.name);
        worker.postMessage( item );
        break;
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
    }
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
          if(index < 7){
            currentPerson[$th.text()] = $(element).text();
            var colVal = $(element).text();
            console.log("    Value in col " + (index + 1) + " : " + colVal.trim());
          }else if(index == 7){
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
        console.log($(element).children('td:first').text());
        subjects.push($(element).children('td:first').text());
      });
      globalMetadata.subjects = subjects;
      console.log(JSON.stringify(globalMetadata));
      return globalMetadata;
    }
  
  
    function addSubjectRow(){
        markup = "<tr><td contenteditable></td><td class='text-center' contenteditable><button class='btn btn-outline-secondary btn-sm' onclick='deleteRow(this)'>&#x2716</button></td></tr>";
        tableBody = $("#subject_table > tbody");
        tableBody.append(markup);
    }
  
    function addAuthorRow(){
        markup = "<tr><td contenteditable></td><td contenteditable></td><td contenteditable></td><td contenteditable></td><td contenteditable></td><td contenteditable></td><td contenteditable></td><td ><select class='form-control form-control-sm'><option>Creator</option><option>Contributor</option><option value='legalperson'>Legal person</option></select></td><td class='text-center' contenteditable><button class='btn btn-outline-secondary btn-sm' onclick='deleteRow(this)'>&#x2716</button></td></tr>";
        tableBody = $("#author_table > tbody");
        tableBody.append(markup);
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