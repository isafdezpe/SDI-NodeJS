module.exports = function (app, gestorBDofertas, gestorBDmensajes) {

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

    app.post("/api/mensaje/:id", function (req, res) {
        var criterio = {"_id" : gestorBDofertas.mongo.ObjectID(req.params.id)}
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
                    emisor : usuario,
                    receptor : req.body.receptor,
                    oferta : oferta,
                    mensaje : req.body.mensaje,
                    fecha: moment().format('dddd, MMMM Do YYYY, h:mm:ss a'),
                };
                gestorBDmensajes.insertarMensaje(mensaje, function (mensajes) {
                    if (mensajes == null) {
                        res.status(500);
                        res.json({
                            error: "se ha producido un error"
                        })
                    } else {
                        res.status(200);
                        res.send( JSON.stringify(mensajes) );
                    }
                })
            }
        })
    });
}