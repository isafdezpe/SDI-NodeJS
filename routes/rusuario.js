module.exports = function (app, swig, gestorBDusuarios) {

    // Registrarse como usuario
    app.get("/registrarse", function(req, res) {
        var respuesta = swig.renderFile('views/bregistro.html', {});
        res.send(respuesta);
    });

    app.post('/usuario', function(req, res) {
        if (req.body.password != req.body.repeatPassword) {
            res.redirect("/registrarse?mensaje=Las contraseñas no coinciden");
            return;
        }

        var seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        var usuario = {
            email : req.body.email,
            nombre : req.body.nombre,
            apellido : req.body.apellido,
            password : seguro,
            saldo : 100,
            rol : 'standard',
        }

        gestorBDusuarios.insertarUsuario(usuario, function(id) {
            if (id == null){
                res.redirect("/registrarse?mensaje=Error al registrar usuario")
            } else {
                var criterio = {
                    email : req.body.email,
                    password : seguro
                }
                gestorBDusuarios.obtenerUsuarios(criterio, function(usuarios) {
                    if (usuarios == null || usuarios.length == 0) {
                        req.session.usuario = null;
                        res.redirect("/identificarse" +
                            "?mensaje=Email o password incorrecto"+
                            "&tipoMensaje=alert-danger ");
                    } else {
                        req.session.usuario = usuarios[0];
                        if (usuarios[0].rol === 'admin')
                            res.redirect('/usuarios');
                        else
                            res.redirect('/tienda');
                    }
                });
            }
        });
    });

    // Inicio de sesión
    app.get("/identificarse", function(req, res) {
        var respuesta = swig.renderFile('views/bidentificacion.html', {});
        res.send(respuesta);
    });

    app.post("/identificarse", function(req, res) {
        var seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        var criterio = {
            email : req.body.email,
            password : seguro
        }
        gestorBDusuarios.obtenerUsuarios(criterio, function(usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                req.session.usuario = null;
                res.redirect("/identificarse" +
                    "?mensaje=Email o password incorrecto"+
                    "&tipoMensaje=alert-danger ");
            } else {
                req.session.usuario = usuarios[0];
                if (usuarios[0].rol === 'admin')
                    res.redirect('/usuarios');
                else
                    res.redirect('/tienda');
            }
        });
    });

    // Fin de sesión
    app.get('/desconectarse', function (req, res) {
        req.session.usuario = null;
        res.redirect('/identificarse');
    });

    // Listado de usuarios
    app.get('/usuarios',  function (req, res) {
        var criterio = {};
        gestorBDusuarios.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null) {
                res.send("Error al listar")
            } else {
                var respuesta = swig.renderFile('views/busuarios.html',
                    {
                        usuario : req.session.usuario,
                        usuarios : usuarios
                    });
                res.send(respuesta);
            }
        })
    })

    app.post("/usuarios/eliminar", function (req, res) {
        var emails = req.body.cbemail;
        if (!emails)
            res.redirect("/usuarios");

        if (!Array.isArray(emails)) {
            var aux = emails;
            emails = [];
            emails.push(aux);
        }

        let criterio = {
            email: {
                $in: emails
            }
        }

         gestorBDusuarios.eliminarUsuarios(criterio, function (usuarios) {
                if (usuarios === null || usuarios.length === 0) {
                    res.redirect("/usuarios?mensaje=Error al eliminar usuarios");
                } else {
                    res.redirect("/usuarios");
                }
            });

    });
};