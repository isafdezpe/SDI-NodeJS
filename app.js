// Módulos
var express = require('express');
var app = express();

var jwt = require('jsonwebtoken');
app.set('jwt', jwt);

app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Credentials", "true");
    res.header("Access-Control-Allow-Methods", "POST, GET, DELETE, UPDATE, PUT");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
    // Debemos especificar todas las headers que se aceptan. Content-Type , token
    next();
});

var fs = require('fs');

var expressSession = require('express-session');
app.use(expressSession({
    secret: 'abcdefg',
    resave: true,
    saveUninitialized: true
}));

var crypto = require('crypto');

var mongo = require('mongodb');
var swig = require('swig');

var bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

var gestorBDusuarios = require("./modules/gestorBDusuarios.js");
gestorBDusuarios.init(app,mongo);

var gestorBDofertas = require("./modules/gestorBDofertas.js");
gestorBDofertas.init(app,mongo);

var gestorBDmensajes = require("./modules/gestorBDmensajes");
gestorBDmensajes.init(app,mongo);

app.use(express.static('public'));

// routerUsuarioToken
var routerUsuarioToken = express.Router();
routerUsuarioToken.use(function(req, res, next) {
    // obtener el token, vía headers (opcionalmente GET y/o POST).
    var token = req.headers['token'] || req.body.token || req.query.token;
    if (token != null) {
        // verificar el token
        jwt.verify(token, 'secreto', function(err, infoToken) {
            if (err || (Date.now()/1000 - infoToken.tiempo) > 240 ){
                res.status(403); // Forbidden
                res.json({
                    acceso : false,
                    error: 'Token invalido o caducado'
                });
                // También podríamos comprobar que intoToken.usuario existe
                return;

            } else {
                // dejamos correr la petición
                res.usuario = infoToken.usuario;
                next();
            }
        });

    } else {
        res.status(403); // Forbidden
        res.json({
            acceso : false,
            mensaje: 'No hay Token'
        });
    }
});

// Aplicar routerUsuarioToken
app.use('/api/tienda', routerUsuarioToken);
app.use('/api/oferta', routerUsuarioToken);
app.use('/api/mensaje', routerUsuarioToken);
app.use('/api/conversacion/*', routerUsuarioToken);
app.use('/api/conversaciones', routerUsuarioToken);

// RouterUsuarioSession
var routerUsuarioSession = express.Router();
routerUsuarioSession.use(function(req, res, next) {
    console.log("routerUsuarioSession");
    if ( req.session.usuario ) {
        // dejamos correr la petición
        next();
    } else {
        console.log("va a : "+req.session.destino)
        res.redirect("/identificarse");
    }
});
//Aplicar routerUsuarioSession
app.use("/propias",routerUsuarioSession);
app.use("/compras",routerUsuarioSession)
app.use("/oferta/comprar",routerUsuarioSession);
app.use("/usuarios", routerUsuarioSession);

//routerUsuarioAutor
var routerUsuarioAutor = express.Router();
routerUsuarioAutor.use(function(req, res, next) {
    console.log("routerUsuarioAutor");
    var path = require('path');
    var id = path.basename(req.originalUrl);
// Cuidado porque req.params no funciona
// en el router si los params van en la URL.
    gestorBDofertas.obtenerOfertas(
        {_id: mongo.ObjectID(id) }, function (ofertas) {
            console.log(ofertas[0]);
            if(ofertas[0].autor == req.session.usuario ){
                next();
            } else {
                res.redirect("/tienda");
            }
        })
});
//Aplicar routerUsuarioAutor
app.use("/cancion/eliminar",routerUsuarioAutor);


// Variables
app.set('port', 8081);
app.set('db','mongodb://admin:sdi@mywallapop-shard-00-00-dprkm.mongodb.net:27017,mywallapop-shard-00-01-dprkm.mongodb.net:27017,mywallapop-shard-00-02-dprkm.mongodb.net:27017/test?ssl=true&replicaSet=mywallapop-shard-0&authSource=admin&retryWrites=true')
app.set('clave','abcdefg');
app.set('crypto',crypto);


// Rutas/controladores por lógica
require("./routes/rusuario.js")(app, swig, gestorBDusuarios);
require("./routes/rofertas.js")(app, swig, gestorBDofertas, gestorBDusuarios);
require("./routes/rapiusuarios.js")(app, gestorBDusuarios);
require("./routes/rapiofertas.js")(app, gestorBDofertas, gestorBDmensajes, gestorBDusuarios);


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
app.listen(app.get('port'), function() {
    console.log("Servidor activo");
});