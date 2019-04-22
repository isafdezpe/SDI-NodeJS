module.exports = function (app, swig, gestorBDofertas) {

    // Tienda
    app.get("/tienda", function(req, res) {
        var criterio = {};
        if( req.query.busqueda != null ){
            criterio = { "nombre" :  {$regex : ".*"+req.query.busqueda+".*", '$options': 'i'} };
        }
        var pg = parseInt(req.query.pg); // Es String !!!
        if ( req.query.pg == null){ // Puede no venir el param
            pg = 1;
        }
        gestorBDofertas.obtenerOfertasPg(criterio, pg , function(ofertas, total ) {
            if (ofertas == null) {
                res.redirect("/tienda?mensaje=Error al listar")
            } else {
                var ultimaPg = total/4;
                if (total % 4 > 0 ){ // Sobran decimales
                    ultimaPg = ultimaPg+1;
                }
                var paginas = []; // paginas mostrar
                for(var i = pg-2 ; i <= pg+2 ; i++){
                    if ( i > 0 && i <= ultimaPg){
                        paginas.push(i);
                    }
                }
                var respuesta = swig.renderFile('views/btienda.html',
                    {
                        ofertas : ofertas,
                        paginas : paginas,
                        actual : pg
                    });
                res.send(respuesta);
            }
        });
    });

    // Ofertas propias
    app.get('/propias/agregar', function (req, res) {
        var respuesta = swig.renderFile('views/bagregar.html',
            {
            });
        res.send(respuesta);
    });

    app.post("/oferta", function(req, res) {
        var oferta = {
            nombre : req.body.nombre,
            descripcion : req.body.descripcion,
            precio : req.body.precio,
            autor : req.session.usuario
        }
        // Conectarse
        gestorBDofertas.insertarOferta(oferta, function(id) {
            if (id == null) {
                res.redirect("/propias?mensaje=Error al insertar oferta")
            } else {
                res.redirect("/propias");
            }
        });
    });

    app.get('/oferta/comprar/:id', function (req, res) {
        var ofertaId = gestorBDofertas.mongo.ObjectID(req.params.id);
        var compra = {
            usuario : req.session.usuario,
            ofertaId : ofertaId
        }
        //TODO: comprobar que el saldo del usuario es >= que el precio de la oferta
        gestorBDofertas.insertarCompra(compra ,function(idCompra){
            if ( idCompra == null ){
                res.send(respuesta);
            } else {
                res.redirect("/compras");
            }
        });
    });

    app.get('/oferta/:id', function (req, res) {
        var criterio = { "_id" : gestorBDofertas.mongo.ObjectID(req.params.id) };
        gestorBDofertas.obtenerOfertas(criterio,function(ofertas){
            if ( ofertas == null ){
                res.send(respuesta);
            } else {
                var respuesta = swig.renderFile('views/boferta.html',
                    {
                        oferta : ofertas[0]
                    });
                res.send(respuesta);
            }
        });
    });

    app.get('/propias/eliminar/:id', function (req, res) {
        var criterio = {"_id" : gestorBD.mongo.ObjectID(req.params.id) };
        gestorBDofertas.eliminarOferta(criterio,function(ofertas){
            if ( ofertas == null ){
                res.send(respuesta);
            } else {
                res.redirect("/propias");
            }
        });
    });

    app.get("/propias", function(req, res) {
        var criterio = { autor : req.session.usuario };
        gestorBDofertas.obtenerOfertas(criterio, function(ofertas) {
            if (ofertas == null) {
                res.redirect("/propias?mensaje=Error al listar")
            } else {
                var respuesta = swig.renderFile('views//bpropias.html',
                    {
                        ofertas : ofertas
                    });
                res.send(respuesta);
            }
        });
    });

    // Ofertas compradas
    app.get('/compras', function (req, res) {
        var criterio = { "usuario" : req.session.usuario };
        gestorBDofertas.obtenerCompras(criterio ,function(compras){
            if (compras == null) {
                res.redirect("/compras?mensaje=Error al listar")
            } else {
                var ofertasCompradasIds = [];
                for(i=0; i < compras.length; i++){
                    ofertasCompradasIds.push( compras[i].ofertaId );
                }
                var criterio = { "_id" : { $in: ofertasCompradasIds } }
                gestorBDofertas.obtenerOfertas(criterio ,function(ofertas){
                    var respuesta = swig.renderFile('views/bcompras.html',
                        {
                            ofertas : ofertas
                        });
                    res.send(respuesta);
                });
            }
        });
    });

};