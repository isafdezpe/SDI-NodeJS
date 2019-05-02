module.exports = function (app, swig, gestorBDofertas, gestorBDusuarios) {

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
}