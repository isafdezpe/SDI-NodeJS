// Módulos
var express = require('express');
var app = express();

app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Credentials", "true");
    res.header("Access-Control-Allow-Methods", "POST, GET, DELETE, UPDATE, PUT");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
    // Debemos especificar todas las headers que se aceptan. Content-Type , token
    next();
});

var fs = require('fs');
var https = require('https');

var expressSession = require('express-session');
app.use(expressSession({
    secret: 'abcdefg',
    resave: true,
    saveUninitialized: true
}));

var crypto = require('crypto');

var fileUpload = require('express-fileupload');
app.use(fileUpload());

var mongo = require('mongodb');
var swig = require('swig')

var bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

var gestorBDusuarios = require("./modules/gestorBDusuarios.js");
gestorBDusuarios.init(app,mongo);

var gestorBDofertas = require("./modules/gestorBDofertas.js");
gestorBDofertas.init(app,mongo);

app.use(express.static('public'));


// Variables
app.set('port', 8081);
app.set('db','mongodb://admin:sdi@mywallapop-shard-00-00-dprkm.mongodb.net:27017,mywallapop-shard-00-01-dprkm.mongodb.net:27017,mywallapop-shard-00-02-dprkm.mongodb.net:27017/test?ssl=true&replicaSet=mywallapop-shard-0&authSource=admin&retryWrites=true')
app.set('clave','abcdefg');
app.set('crypto',crypto);


// Rutas/controladores por lógica
require("./routes/rusuario.js")(app, swig, gestorBDusuarios);
require("./routes/rofertas.js")(app, swig, gestorBDofertas);


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