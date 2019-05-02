module.exports = function (app, swig, gestorBDofertas, gestorBDusuarios) {

    // Tienda
    app.get("/tienda", function(req, res) {
        var criterio = { "autor" : {$ne:req.session.usuario} };
        if( req.query.busqueda != null ){
            criterio = { "autor" : {$ne:req.session.usuario}, "nombre" :  {$regex : ".*"+req.query.busqueda+".*", '$options': 'i'} };
        }
        var pg = parseInt(req.query.pg); // Es String !!!
        if ( req.query.pg == null){ // Puede no venir el param
            pg = 1;
        }
        gestorBDofertas.obtenerOfertasPg(criterio, pg , function(ofertas, total ) {
            if (ofertas == null) {
                res.send("Error al listar")
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
                        usuario : req.session.usuario,
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
                usuario : req.session.usuario,
            });
        res.send(respuesta);
    });

    app.post("/oferta", function(req, res) {
        var oferta = {
            nombre : req.body.nombre,
            descripcion : req.body.descripcion,
            precio : req.body.precio,
            autor : req.session.usuario,
            vendida : false,
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
        var criterioOferta = { "_id" : gestorBDofertas.mongo.ObjectID(req.params.id) };
        var criterioUsuario = { "_id" : gestorBDusuarios.mongo.ObjectID(req.session.usuario._id.toString()) };
        gestorBDofertas.obtenerOfertas(criterioOferta, function (ofertas) {
            if (!ofertas)
                res.redirect("/tienda?mensaje=Error al comprar")
            else {
                if (req.session.usuario.saldo >= ofertas[0].precio){
                    ofertas[0].vendida = true;
                    ofertas[0].comprador = criterioUsuario._id;
                    gestorBDofertas.actualizarOferta(criterioOferta, ofertas[0], function (id) {

                        if (!id)
                            res.redirect("/tienda?mensaje=Error al comprar")
                        else {
                            console.log(id.nModified);
                            req.session.usuario.saldo -= ofertas[0].precio;
                            let user = req.session.usuario;
                            delete user._id;
                            gestorBDusuarios.actualizarUsuario(criterioUsuario, user , function (id) {
                                if (id == null)
                                    res.redirect("/tienda?mensaje=Error al actualizar el usuario")
                                else {
                                    res.redirect("/compras");
                                }

                            })

                        }
                    })
                }
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
                        usuario : req.session.usuario,
                        oferta : ofertas[0]
                    });
                res.send(respuesta);
            }
        });
    });

    app.get('/propias/eliminar/:id', function (req, res) {
        var criterio = {"_id" : gestorBDofertas.mongo.ObjectID(req.params.id) };
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
                res.send("Error al listar")
            } else {
                var respuesta = swig.renderFile('views//bpropias.html',
                    {
                        usuario : req.session.usuario,
                        ofertas : ofertas
                    });
                res.send(respuesta);
            }
        });
    });

    // Ofertas compradas
    app.get('/compras', function (req, res) {
        var criterio = { comprador :gestorBDusuarios.mongo.ObjectId(req.session.usuario._id.toString()) };
        gestorBDofertas.obtenerOfertas(criterio ,function(ofertas){
            if (ofertas == null) {
                res.send("Error al listar")
            } else {
                var respuesta = swig.renderFile('views/bcompras.html',
                    {
                        usuario : req.session.usuario,
                        ofertas : ofertas,
                    });
                res.send(respuesta);
            }
        });
    });

};