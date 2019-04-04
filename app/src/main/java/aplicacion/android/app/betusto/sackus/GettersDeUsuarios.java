package aplicacion.android.app.betusto.sackus;

public class GettersDeUsuarios {
    public String Usuario;
    public String Correo;
    public String Contraseña;
    public String CuentaAbierta;
    public String EfectivoTotal;

    public GettersDeUsuarios() {

    }

    //Esto mas de ser un getter, es un setter en realidad
    public GettersDeUsuarios(String Usuario, String Correo, String Contraseña, String CuentaAbierta, String EfectivoTotal) {
        this.Usuario = Usuario;
        this.Correo = Correo;
        this.Contraseña = Contraseña;
        this.CuentaAbierta = CuentaAbierta;
        this.EfectivoTotal = EfectivoTotal;
    }
}

class GastosGetters{
    public String NombreDelGasto, Fecha, Gasto, TimeStamp;
    public GastosGetters(){

    }

    public GastosGetters(String NombreDelGasto, String Fecha, String Gasto, String TimeStamp){
        this.NombreDelGasto = NombreDelGasto;
        this.Fecha = Fecha;
        this.Gasto = Gasto;
        this.TimeStamp = TimeStamp;
    }
}



