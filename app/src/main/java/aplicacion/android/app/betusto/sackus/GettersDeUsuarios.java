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



class InvitacionesGetters{
    public String NombreViaje, UsuarioMando, UsuarioDestino, Fecha, Texto, TimeStamp;
    public InvitacionesGetters(){

    }
    public InvitacionesGetters(String NombreViaje,String  UsuarioMando, String UsuarioDestino, String Fecha, String Texto, String TimeStamp){
        this.NombreViaje = NombreViaje;
        this.UsuarioMando = "Enviado por "+UsuarioMando;
        this.UsuarioDestino = UsuarioDestino;
        this.Fecha = "Enviado el "+Fecha;
        this.Texto = Texto;
        this.TimeStamp = TimeStamp;
    }
}

class ViajesGetters{
    public String Viaje, DineroDedicado, FechaInicio, FechaRegreso, Compartir, TimeStamp, InvertirEnHotel, InvertirEnTransporte, InvertirEnComida,
            InvertirEnBaratijas;
    public ViajesGetters(){

    }
    public ViajesGetters(String Viaje,String DineroDedicado, String FechaInicio, String FechaRegreso, String Compartir, String TimeStamp,
                         String InvertirEnHotel, String InvertirEnTransporte, String InvertirEnComida, String InvertirEnBaratijas){
        this.Viaje = Viaje;
        this.DineroDedicado = DineroDedicado;
        this.FechaInicio = FechaInicio;
        this.FechaRegreso = FechaRegreso;
        this.Compartir = Compartir;
        this.TimeStamp = TimeStamp;
        this.InvertirEnHotel = InvertirEnHotel;
        this.InvertirEnTransporte = InvertirEnTransporte;
        this.InvertirEnComida = InvertirEnComida;
        this.InvertirEnBaratijas = InvertirEnBaratijas;
    }
}

class InvitadosGetters{
    public String Invitado1, Invitado2, Invitado3, Invitado4, Invitado5;
    public InvitadosGetters(){

    }
    public InvitadosGetters(String Invitado1, String Invitado2, String Invitado3, String Invitado4, String Invitado5){
        this.Invitado1 = Invitado1;
        this.Invitado2 = Invitado2;
        this.Invitado3 = Invitado3;
        this.Invitado4 = Invitado4;
        this.Invitado5 = Invitado5;
    }
}

class ElementosInvitadosGetters{
    public String Usuario, SolicitudAceptada;
    public ElementosInvitadosGetters(){

    }
    public ElementosInvitadosGetters(String Usuario, String SolicitudAceptada){
        this.Usuario = Usuario;
        this.SolicitudAceptada = SolicitudAceptada;
    }
}

class NotasGetters{
    public String Titulo, Descripcion, Fecha, TimeStamp;
    public NotasGetters(){

    }

    public NotasGetters(String Titulo, String Descripcion, String Fecha, String TimeStamp){
        this.Titulo = Titulo;
        this.Descripcion = Descripcion;
        this.Fecha = Fecha;
        this.TimeStamp = TimeStamp;
    }
}