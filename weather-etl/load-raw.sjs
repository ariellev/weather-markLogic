declareUpdate();

var loadToCollection = function(files, col_name) {
    var file_names = files.map(function(f) { 
      return { 
        'path' : f.pathname, 
        'uri': 'raw' + f.pathname.substr(f.pathname.lastIndexOf('/')),
        'collection' : col_name
      } 
    });

    file_names.forEach(function(f) { 
      xdmp.documentLoad(f.path, { 'uri': f.uri, 'collections': f.collection}); 
    });

   return file_names;
}

var data_files = xdmp.filesystemDirectory("/Users/ariellev/myWS/sandbox/weather-ml/weather-etl/NOAA").filter(function(f){return f.filename.match('mini-data-geo.*.json');});
var meta_files = xdmp.filesystemDirectory("/Users/ariellev/myWS/sandbox/weather-ml/weather-etl/US").filter(function(f){return f.filename.match('.*.json');});

// outputs resource uris and paths
loadToCollection(data_files, 'raw').concat(loadToCollection(meta_files, 'raw-metadata'));