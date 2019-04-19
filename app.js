// Módulos
var express = require('express');
var app = express();

var fs = require('fs');
var https = require('https');

var expressSession = require('express-session');
app.use(expressSession({
    secret: 'abcdefg',
    resave: true,
    saveUninitialized: true
}));
var crypto = require('crypto');

var mongo = require('mongodb');
var swig = require('swig')

var gestorBD = require("./modules/gestorBD.js");
gestorBD.init(app,mongo);

app.use(express.static('public'));

// Variables
app.set('port', 8081);
app.set('db','mongodb://admin:sdi@mywallapop-shard-00-00-dprkm.mongodb.net:27017,mywallapop-shard-00-01-dprkm.mongodb.net:27017,mywallapop-shard-00-02-dprkm.mongodb.net:27017/test?ssl=true&replicaSet=mywallapop-shard-0&authSource=admin&retryWrites=true')
app.set('clave','abcdefg');
app.set('crypto',crypto);


// Rutas/controladores por lógica
require("./routes/rusuario.js")(app, swig, gestorBD);
require("./routes/rofertas.js")(app, swig, gestorBD);


app.use( function (err, req, res, next ) {
    console.log("Error producido: " + err); //we log the error in our db
    if (! res.headersSent) {
        res.status(400);
        res.send("Recurso no disponible");
    }
});

app.get('/', function (req, res) {
    res.redirect('/tienda');
})


// Lanzar el servidor
https.createServer({
    key: fs.readFileSync('certificates/alice.key'),
    cert: fs.readFileSync('certificates/alice.crt')
}, app).listen(app.get('port'), function() {
    console.log("Servidor activo");
});