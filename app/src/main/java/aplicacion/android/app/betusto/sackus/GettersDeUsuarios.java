package aplicacion.android.app.betusto.sackus;

public class GettersDeUsuarios {
    public String Usuario;
    public String Correo;
    public String Contraseña;
    public String CuentaAbierta;

    public GettersDeUsuarios() {

    }

    //Esto mas de ser un getter, es un setter en realidad
    public GettersDeUsuarios(String Usuario, String Correo, String Contraseña, String CuentaAbierta) {
        this.Usuario = Usuario;
        this.Correo = Correo;
        this.Contraseña = Contraseña;
        this.CuentaAbierta = CuentaAbierta;
    }
}



