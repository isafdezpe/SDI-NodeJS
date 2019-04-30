module.exports = {
    swig: null,
    init: function (swig) {
        this.swig = swig;
    },
    mostrarVista: function (fichero, variables, sesion){
        variables["email"] = sesion.usuario.email;
        variables["rol"] = sesion.usuario.rol;
        variables["dinero"] = sesion.usuario.saldo;
        return swig.renderFile(fichero, variables);
    }
};