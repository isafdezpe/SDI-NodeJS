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
            isAdmin : false
        }

        gestorBDusuarios.insertarUsuario(usuario, function(id) {
            if (id == null){
                res.redirect("/registrarse?mensaje=Error al registrar usuario")
            } else {
                res.redirect("/identificarse?mensaje=Nuevo usuario registrado");
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
                req.session.usuario = usuarios[0].email;
                if (usuarios[0].isAdmin)
                    res.redirect("/usuarios");
                else
                    res.redirect("/tienda");
            }
        });
    });

    // Fin de sesión
    app.get('/desconectarse', function (req, res) {
        req.session.usuario = null;
        res.redirect('/identificarse');
    });

    // Listado de usuarios
    app.get('/usuarios', function (req, res) {
        var respuesta = swig.renderFile('views/busuarios.html', {});
        res.send(respuesta);
    })
};