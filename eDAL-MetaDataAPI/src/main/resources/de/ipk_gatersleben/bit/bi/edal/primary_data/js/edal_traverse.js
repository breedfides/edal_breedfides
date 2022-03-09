onmessage = async function(e) {
  const empyDirectories = traverseDirectory(e.data);
  postMessage(empyDirectories);
};

function traverseDirectory(entry) {
  console.log(entry);
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
              $('#boxtitle').text(fileSystemEntry.fullPath+" empty entries: "+emptyDirectories);
            }
            resolveDirectory(Promise.all(iterationAttempts));
          } else {
            // Add a list of promises for each directory entry.  If the entry is itself
            // a directory, then that promise won't resolve until it is fully traversed.
            iterationAttempts.push(Promise.all(batchEntries.map((batchEntry) => {
              if (batchEntry.isDirectory) {
                console.log("dir: "+batchEntry.name);
                return traverseDirectory(batchEntry);
              }else{
              batchEntry.file((file) => {
                if(file.size == 0){
                  emptyDirectories++;
                  $('#boxtitle').text(fileSystemEntry.fullPath+" empty entries: "+emptyDirectories);
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
    }, 50);

  });
}
