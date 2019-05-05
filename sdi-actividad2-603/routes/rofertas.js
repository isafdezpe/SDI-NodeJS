module.exports = function (app, swig, gestorBDofertas, gestorBDusuarios) {

    // Tienda
    app.get("/tienda", function(req, res) {
        var criterio = {};
        if (req.session.usuario)    {
            criterio = { "autor" : {$ne:gestorBDusuarios.mongo.ObjectID(req.session.usuario._id.toString())} };
            if( req.query.busqueda != null ){
                criterio = { "autor" : {$ne:gestorBDusuarios.mongo.ObjectID(req.session.usuario._id.toString())}, "nombre" :  {$regex : ".*"+req.query.busqueda+".*", '$options': 'i'} };
        }} else {
            if (req.query.busqueda != null) {
                criterio = {"nombre": {$regex: ".*" + req.query.busqueda + ".*", '$options': 'i'}};
            }
        }
        var pg = parseInt(req.query.pg); // Es String !!!
        if ( req.query.pg == null){ // Puede no venir el param
            pg = 1;
        }
        gestorBDofertas.obtenerOfertasPg(criterio, pg , function(ofertas, total ) {
            if (ofertas == null) {
                app.get("logger").error("Error al acceder a la tienda");
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
                app.get("logger").info("Acceso a la tienda");
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
        app.get("logger").info("Acceso a agregar oferta");
        res.send(respuesta);
    });

    app.post("/oferta", function(req, res) {
        var oferta = {
            nombre : req.body.nombre,
            descripcion : req.body.descripcion,
            precio : req.body.precio,
            autor : gestorBDusuarios.mongo.ObjectID(req.session.usuario._id.toString()),
            fecha : new Date(),
            vendida : false,
            autorEmail : req.session.usuario.email
        }
        // Conectarse
        gestorBDofertas.insertarOferta(oferta, function(id) {
            if (id == null) {
                app.get("logger").error("Error al agregar oferta");
                res.redirect("/propias?mensaje=Error al insertar oferta")
            } else {
                app.get("logger").info("Oferta agregada");
                res.redirect("/propias");
            }
        });
    });

    app.get('/oferta/comprar/:id', function (req, res) {
        var criterioOferta = { "_id" : gestorBDofertas.mongo.ObjectID(req.params.id) };
        var criterioUsuario = { "_id" : gestorBDusuarios.mongo.ObjectID(req.session.usuario._id.toString()) };
        gestorBDofertas.obtenerOfertas(criterioOferta, function (ofertas) {
            if (!ofertas) {
                app.get("logger").error("Error al comprar oferta");
                res.redirect("/tienda?mensaje=Error al comprar")
            } else {
                if (req.session.usuario.saldo >= ofertas[0].precio){
                    ofertas[0].vendida = true;
                    ofertas[0].comprador = criterioUsuario._id;
                    gestorBDofertas.actualizarOferta(criterioOferta, ofertas[0], function (id) {

                        if (!id) {
                            app.get("logger").error("Error al comprar oferta");
                            res.redirect("/tienda?mensaje=Error al comprar")
                        }else {
                            req.session.usuario.saldo -= ofertas[0].precio;
                            var user = req.session.usuario;
                            var userID = user._id;
                            delete user._id;
                            gestorBDusuarios.actualizarUsuario(criterioUsuario, user , function (id) {
                                user._id = userID;
                                if (id == null) {
                                    app.get("logger").error("Error al actualizar el saldo del usuario");
                                    res.redirect("/tienda?mensaje=Error al actualizar el usuario")
                                } else {
                                    app.get("logger").info("Oferta comprada");
                                    res.redirect("/compras");
                                }

                            })

                        }
                    })
                } else {
                    res.redirect("/tienda?mensaje=Saldo insuficiente");
                }
            }
        });


    });

    app.get('/oferta/:id', function (req, res) {
        var criterio = { "_id" : gestorBDofertas.mongo.ObjectID(req.params.id) };
        gestorBDofertas.obtenerOfertas(criterio,function(ofertas){
            if ( ofertas == null ){
                app.get("logger").error("Error al mostrar la oferta");
                res.send("Error al mostrar oferta");
            } else {
                var respuesta = swig.renderFile('views/boferta.html',
                    {
                        usuario : req.session.usuario,
                        oferta : ofertas[0]
                    });
                app.get("logger").info("Oferta mostrada al usuario");
                res.send(respuesta);
            }
        });
    });

    app.get('/propias/eliminar/:id', function (req, res) {
        var criterio = {"_id" : gestorBDofertas.mongo.ObjectID(req.params.id) };
        gestorBDofertas.eliminarOferta(criterio,function(ofertas){
            if ( ofertas == null ){
                app.get("logger").error("Error al eliminar oferta");
                res.send("Error al eliminar oferta");
            } else {
                app.get("logger").info("Oferta eliminada");
                res.redirect("/propias");
            }
        });
    });

    app.get("/propias", function(req, res) {
        var criterio = { autor : gestorBDusuarios.mongo.ObjectID(req.session.usuario._id.toString()) };
        gestorBDofertas.obtenerOfertas(criterio, function(ofertas) {
            if (ofertas == null) {
                app.get("logger").error("Error al mostrar ofertas propias");
                res.send("Error al listar")
            } else {
                var respuesta = swig.renderFile('views//bpropias.html',
                    {
                        usuario : req.session.usuario,
                        ofertas : ofertas
                    });
                app.get("logger").info("Acceso a las ofertas propias");
                res.send(respuesta);
            }
        });
    });

    // Ofertas compradas
    app.get('/compras', function (req, res) {
        var criterio = { comprador :gestorBDusuarios.mongo.ObjectId(req.session.usuario._id.toString()) };
        gestorBDofertas.obtenerOfertas(criterio ,function(ofertas){
            if (ofertas == null) {
                app.get("logger").error("Error al mostrar las compras");
                res.send("Error al listar")
            } else {
                var respuesta = swig.renderFile('views/bcompras.html',
                    {
                        usuario : req.session.usuario,
                        ofertas : ofertas,
                    });
                app.get("logger").info("Acceso a las comprae del usuario");
                res.send(respuesta);
            }
        });
    });

};