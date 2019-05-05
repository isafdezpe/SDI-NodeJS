module.exports = function (app, gestorBDofertas, gestorBDmensajes, gestorBDusuarios) {

    // Tienda
    app.get("/api/tienda", function (req, res) {
        var criterio = {"autor": {$ne: gestorBDusuarios.mongo.ObjectID(res.usuario._id.toString())}};
        gestorBDofertas.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null) {
                app.get("logger").error("Error al acceder a /api/tienda");
                res.status(500);
                res.json({error: "fallo al cargar"})
            } else {
                app.get("logger").info("Acceso a /api/tienda");
                res.status(200);
                res.json(ofertas);
            }
        });
    });

    app.get("/api/oferta/:id", function(req, res) {
        var criterio = { "_id" : gestorBD.mongo.ObjectID(req.params.id)}

        gestorBDofertas.obtenerOfertas(criterio,function(ofertas){
            if ( ofertas == null ){
                app.get("logger").error("Error al acceder a la oferta");
                res.status(500);
                res.json({
                    error : "se ha producido un error"
                })
            } else {
                app.get("logger").info("Acceso a oferta");
                res.status(200);
                res.send( JSON.stringify(ofertas[0]) );
            }
        });
    });

    app.post("/api/mensaje", function (req, res) {
        var criterio = {"_id" : gestorBDofertas.mongo.ObjectID(req.body.idOferta)}
        gestorBDofertas.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null) {
                app.get("logger").error("Error al obtener oferta");
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
                        app.get("logger").error("Error al enviar mensaje");
                        res.status(500);
                        res.json({
                            error: "se ha producido un error"
                        })
                    } else {
                        app.get("logger").info("Mensaje enviado");
                        res.status(200);
                        res.send( JSON.stringify(mensaje) );
                    }
                })
            }
        })
    });

    app.get("/api/conversaciones", function (req, res) {
        var criterio = { $or:  [{emisor: res.usuario.email},
                {receptor : res.usuario.email}]};
        gestorBDmensajes.obtenerConversaciones(criterio, function (conversaciones) {
            if (conversaciones == null ) {
                app.get("logger").error("Error al obtener conversaciones");
                res.status(500);
                res.json({
                    error : "se ha producido un error"
                })
            } else {
                let idsOfertas = conversaciones.map(o => gestorBDofertas.mongo.ObjectID(o.toString()));
                var criterioOfertas = { "_id" : { $in : idsOfertas }}
                gestorBDofertas.obtenerOfertas(criterioOfertas, function(ofertas) {
                    if (ofertas == null) {
                        app.get("logger").error("Error al obtener ofertas");
                        res.status(500);
                        res.json({
                            error : "se ha producido un error"
                        })
                    } else {

                        app.get("logger").info("Acceso a la lista de conversaciones");
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
                app.get("logger").error("Error al obtener mensajes de la conversación");
                res.status(500);
                res.json({
                    error : "se ha producido un error"
                })
            } else {
                var receptorActual;
                for (var i = 0; i < mensajes.length; i++) {
                    if(mensajes[i].emisor !== res.usuario.email) {
                        receptorActual = mensajes[i].emisor;
                        break;
                    }
                    if (mensajes[i].receptor !== res.usuario.email) {
                        receptorActual = mensajes[i].receptor;
                        break;
                    }
                }
                app.get("logger").info("Acceso a la conversación");
                res.status(200);
                res.send( JSON.stringify( {
                    mensajes : mensajes,
                    receptorActual : receptorActual
                }) );
            }
        })
    })

}