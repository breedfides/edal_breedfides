let processed = 0;
let emptyDirs = 0;
let emptyFiles = 0;
let root = {};
onmessage = async (evt) => {
  processed = 0; 
  emptyDirs = 0; 
  emptyFiles = 0;
  root = evt.data;
  const out = {};
  const dirHandle = evt.data;  
  await handleDirectoryEntry( dirHandle, out);
  console.log("finish traversing via WORKER");
  let result = {
    traversed:out,
    processed:processed,
    emptyDirs:emptyDirs,
    emptyFiles:emptyFiles,
  };
  postMessage( result );
};

//create array of files
async function handleDirectoryEntry( dirHandle, out ) {
  let filecounter = 0;
  for await (const entry of dirHandle.values()) {
    postMessage( `${ processed++ } files` );
    const path = await root.resolve(entry);
    path.unshift(root.name);
    if (entry.kind === "file"){
      const file = await entry.getFile();
      if(file.size > 0){
        console.log(processed+"   "+path.join("/"));
        out[path.join("/")] = {file:file,isDirectory:false};
        filecounter++;
      }else{
        emptyFiles++;
      }
    }else if(entry.kind === "directory") {
      const newHandle = await dirHandle.getDirectoryHandle( entry.name, { create: false } );
      out[path.join("/")] = {isDirectory:true};
      let hasFiles = await handleDirectoryEntry( newHandle, out );
      if(hasFiles){
        filecounter++;
      }else{
        delete out[path.join("/")];
      }
    }
  }
  if(filecounter == 0){
    emptyDirs++;
    return false;
  }
  return true;
}


//created json tree of traveres Dir
// async function handleDirectoryEntry( dirHandle, out ) {
//   let filecounter = 0;
//   for await (const entry of dirHandle.values()) {
//     postMessage( `${ processed++ } files` );
//     if (entry.kind === "file"){
//       const path = await root.resolve(entry);
//       console.log(path.toString());
//       const file = await entry.getFile();
//       if(file.size > 0){
//         out[ path.toString() ] = file;
//         filecounter++;
//       }else{
//         emptyFiles++;
//       }
//     }
//     if (entry.kind === "directory") {
//       const newHandle = await dirHandle.getDirectoryHandle( entry.name, { create: false } );
//       const newOut = out[ entry.name ] = {};
//       let hasFiles = await handleDirectoryEntry( newHandle, newOut );
//       if(hasFiles){
//         filecounter++;
//       }else{
//         delete out[entry.name];
//       }
//     }
//   }
//   if(filecounter == 0){
//     emptyDirs++;
//     return false;
//   }
//   return true;
// }