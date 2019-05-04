module.exports = function (app, gestorBDofertas, gestorBDmensajes, gestorBDusuarios) {

    // Tienda
    app.get("/api/tienda", function (req, res) {
        var criterio = {"autor": {$ne: res.usuario}};
        gestorBDofertas.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null) {
                res.status(500);
                res.json({error: "fallo al cargar"})
            } else {
                res.status(200);
                res.json(ofertas);
            }
        });
    });

    app.get("/api/oferta/:id", function(req, res) {
        var criterio = { "_id" : gestorBD.mongo.ObjectID(req.params.id)}

        gestorBDofertas.obtenerOfertas(criterio,function(ofertas){
            if ( ofertas == null ){
                res.status(500);
                res.json({
                    error : "se ha producido un error"
                })
            } else {
                res.status(200);
                res.send( JSON.stringify(ofertas[0]) );
            }
        });
    });

    app.post("/api/mensaje", function (req, res) {
        var criterio = {"_id" : gestorBDofertas.mongo.ObjectID(req.body.idOferta)}
        gestorBDofertas.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null) {
                res.status(500);
                res.json({
                    error : "se ha producido un error"
                })
            } else {
                var oferta = ofertas[0];
                var usuario = res.usuario;
                var mensaje = {
                    emisor : usuario.email,
                    receptor : req.body.receptor,
                    idOferta : gestorBDofertas.mongo.ObjectID(oferta._id.toString()),
                    mensaje : req.body.mensaje,
                    fecha: new Date(),
                };
                gestorBDmensajes.insertarMensaje(mensaje, function (mensajes) {
                    if (mensajes == null) {
                        res.status(500);
                        res.json({
                            error: "se ha producido un error"
                        })
                    } else {
                        res.status(200);
                        res.send( JSON.stringify(mensaje) );
                    }
                })
            }
        })
    });

    app.get("/api/conversaciones", function (req, res) {
        var criterio = { $or: [{emisor: res.usuario.email}, {receptor : res.usuario.email}]};
        gestorBDmensajes.obtenerConversaciones(criterio, function (conversaciones) {
            if (conversaciones == null ) {
                res.status(500);
                res.json({
                    error : "se ha producido un error"
                })
            } else {
                let idsOfertas = conversaciones.map(o => gestorBDofertas.mongo.ObjectID(o.toString()));
                var criterioOfertas = { "_id" : { $in : idsOfertas }}
                gestorBDofertas.obtenerOfertas(criterioOfertas, function(ofertas) {
                    if (ofertas == null) {
                        res.status(500);
                        res.json({
                            error : "se ha producido un error"
                        })
                    } else {
                        res.status(200);
                        res.send( JSON.stringify(ofertas) );
                    }
                })
            }
        })
    });

    app.get("/api/conversacion/:id", function (req, res) {
        var criterio = { $or: [{emisor: res.usuario.email},
                {receptor : res.usuario.email}],
                idOferta: gestorBDofertas.mongo.ObjectID(req.params.id.toString())};
        gestorBDmensajes.obtenerMensajes(criterio, function (mensajes) {
            if (mensajes == null ) {
                res.status(500);
                res.json({
                    error : "se ha producido un error"
                })
            } else {
                res.status(200);
                res.send( JSON.stringify(mensajes) );
            }
        })
    })

}