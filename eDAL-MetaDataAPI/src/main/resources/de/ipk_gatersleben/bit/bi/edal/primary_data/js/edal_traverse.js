/** 
 * 
 * This Script only traverses a given directory. It is based on the File System Access API. 
 * The script constructs a large JS Object which will contain every File and every Directory 
 * in a key value style. The key will be the path to the file/directory seperated with '/' 
 * and the value will be a object eitherwith {file + type} or only a {type}
 * 
 * **/
const notRecommendedTypes = [
  "zip", "zipx", "tar", "tar.gz", ".7z", ".rar", ".tgz", "arj",
  "bzip", "bzip2", "bz", "bz2",
];
let processed = 0;
let emptyDirs = 0;
let emptyFiles = 0;
let notAllowedFiles = {};
let foundNotRecommendedFiles = {};
let tempFileCounter = 0;
let globalFileCounter = 0;
let root = {};
onmessage = async (evt) => {
  processed = 0; 
  emptyDirs = 0; 
  emptyFiles = 0;
  tempFileCounter = 0;
  globalFileCounter = 0;
  root = evt.data;
  foundNotRecommendedFiles = {};
  const out = {};
  const dirHandle = evt.data;  
  if(dirHandle.kind == "directory"){
    await handleDirectoryEntry( dirHandle, out);
  }
  postMessage( {
    traversed:out,
    processed:processed,
    emptyDirs:emptyDirs,
    emptyFiles:emptyFiles,
    numberOfFiles:globalFileCounter,
    notRecommendedFiles:foundNotRecommendedFiles,
    tempFiles:tempFileCounter,
  } );
};

//create "array" of files
async function handleDirectoryEntry( dirHandle, out ) {
  let filecounter = 0;
  for await (const entry of dirHandle.values()) {
    //Update the user interface
    postMessage( `${ processed++ } files` );

    const path = await root.resolve(entry);
    //add the root name (user entered title) as a pseudo directory to the start of the file path
    path.unshift(root.name);
    if (entry.kind === "file"){
      let extension = entry.name.slice((entry.name.lastIndexOf(".") - 1 >>> 0) + 2);
        //check if it's a compressed file
      if(notRecommendedTypes.includes(extension)){
        foundNotRecommendedFiles[extension] = extension in foundNotRecommendedFiles ? foundNotRecommendedFiles[extension]++ : 1;
        //check if it's a temporary file
      }else if(extension == 'tmp' || extension == 'foo' || entry.name.slice(0,2) == '~$'){
        tempFileCounter++;
      }
      const file = await entry.getFile();
      if(file.size > 0){
        console.log(processed+"   "+path.join("/"));
        out[path.join("/")] = {file:file,isDirectory:false};
        filecounter++;
        globalFileCounter++;
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