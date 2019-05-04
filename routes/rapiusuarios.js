module.exports = function (app, gestorBDusuarios) {

    app.post("/api/autenticar", function(req, res) {
        var seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        var criterio = {
            email : req.body.email,
            password : seguro
        }

        gestorBDusuarios.obtenerUsuarios(criterio, function(usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                app.get("logger").error("Error al autenticar usuario");
                res.status(401); // Unauthorized
                res.json({
                    autenticado : false
                })
            } else {
                var token = app.get('jwt').sign(
                    {usuario: usuarios[0] , tiempo: Date.now()/1000},
                    "secreto");
                app.get("logger").info("Usuario autenticado");
                res.status(200);
                res.json({
                    autenticado : true,
                    token : token
                })
            }

        });
    });

}