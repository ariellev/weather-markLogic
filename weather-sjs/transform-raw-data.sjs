/*
  Extracts weather events from raw documents separate event documents
*/
declareUpdate();

var jsearch     = require('/MarkLogic/jsearch.sjs'),
    collection  = jsearch.collections,
    qbe         = jsearch.byExample;

// extract uris of all documents in raw collection
var uris = collection('raw').documents().result().results.map(function(result) {return result.uri;});

// collection map. will be used as metadata
var event_types = [];
var tmp_type = {};

uris = [uris[0]]
// split raw data into separate event documents
uris.forEach( function(uri) {
  var events = cts.doc(uri) .root.toObject();
  events.forEach(function(e){
   var date = new Date(e.date);
   e.year = date.getYear() + 1900;
   e.epoch_time = date.getTime();    
   xdmp.documentInsert('/events/' + e.id, e, xdmp.defaultPermissions(), ['all-events', e.event_type, e.state, e.year.toString()]);
   tmp_type[e.event_type] = '';
  });
});

// inserting metadata
var event_types_doc = cts.doc('/events_types');
var types = event_types_doc? event_types_doc.root.toObject() : [];
  
for (p in tmp_type) { if (typeof p !== 'undefined' && types.indexOf(p) == -1) types.push(p)};
xdmp.documentInsert('/events_types', types, xdmp.defaultPermissions(), 'metadata');

uris;