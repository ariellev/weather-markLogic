declareUpdate();

var files = xdmp.filesystemDirectory("/Users/ariellev/myWS/sandbox/weather-ml/weather-node/");
var data_files = files.filter(function(f){return f.filename.match('mini-data-geo.*.json');});
var file_names = data_files.map(function(f) { 
  return { 
    'path' : f.pathname, 
    'uri': 'raw' + f.pathname.substr(f.pathname.lastIndexOf('/')) 
  } 
});

file_names.forEach(function(f) { 
  xdmp.documentLoad(f.path, { 'uri': f.uri, 'collections': 'raw'}); 
});

file_names;