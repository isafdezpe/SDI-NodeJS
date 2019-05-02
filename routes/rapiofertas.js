module.exports = function (app, gestorBDofertas) {

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
}