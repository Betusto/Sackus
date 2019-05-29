package aplicacion.android.app.betusto.sackus;

import java.sql.Timestamp;

public class Gastos {
    //Texto de la nota
    private String noteText;
    //Fecha de la nota
    private String noteDate;
    //Cantidad del gasto
    private  String noteGasto;
    //TimeStamp para referencias
    private  String Timestamp;

    public Gastos() {
    }

    public Gastos(String noteText, String noteDate, String noteGasto, String TimeStamp) {
        this.noteText = noteText;
        this.noteDate = noteDate;
        this.noteGasto = noteGasto;
        this.Timestamp = TimeStamp;
    }

    public String getNoteText() {
        return noteText;
    }


    public String getNoteDate() {
        return noteDate;
    }

    public String getNoteGasto(){
        return noteGasto;
    }

    //Solo para referencias
    public String getTimeStamp(){return Timestamp; }
}

class Invitaciones{

    public  Invitaciones(){

    }

    //Titulo del viaje
    private String noteViaje;
    //Mensaje
    private String noteMensaje;
    //Fecha de la invitacion
    private  String noteFecha;
    //TimeStamp para referencias
    private  String Timestamp;

    private String EnviadoPor;

    public Invitaciones(String noteViaje, String noteMensaje, String noteFecha, String EnviadoPor, String Timestamp) {
        this.noteViaje = noteViaje;
        this.noteMensaje = noteMensaje;
        this.noteFecha = noteFecha;
        this.EnviadoPor = EnviadoPor;
        this.Timestamp = Timestamp;
    }

    public String getNoteViaje() {
        return noteViaje;
    }

    public String getNoteMensaje() {
        return noteMensaje;
    }

    public String getNoteFecha() {
        return noteFecha;
    }

    public String getEnviadoPor() {
        return EnviadoPor;
    }

    //Solo para referencias
    public String getTimeStamp(){return Timestamp; }

}

class Viajes{

    public  Viajes(){

    }

    //Titulo del viaje
    private String noteViaje;
    //Mensaje
    private String noteDinero;
    //Fecha de inicio del viaje
    private  String noteFechaInicio;
    //Fecha de regreso del viaje
    private  String noteFechaRegreso;
    //Si el viaje es compartido
    private String noteCompartido;
    //TimeStamp para referencias
    private  String Timestamp;


    public Viajes(String noteViaje, String noteDinero, String noteFechaInicio, String noteFechaRegreso, String noteCompartido, String Timestamp) {
        this.noteViaje = noteViaje;
        this.noteDinero = noteDinero;
        this.noteFechaInicio = noteFechaInicio;
        this.noteFechaRegreso = noteFechaRegreso;
        this.noteCompartido = noteCompartido;
        this.Timestamp = Timestamp;
    }

    public String getNoteViaje() {
        return noteViaje;
    }

    public String getNoteDinero() {
        return noteDinero;
    }

    public String getNoteFechaInicio() {
        return noteFechaInicio;
    }

    public String getNoteFechaRegreso() {
        return noteFechaRegreso;
    }

    public String getNoteCompartido() {
        return noteCompartido;
    }

    public String getTimestamp() {
        return Timestamp;
    }

}

class Notas{

    public  Notas(){

    }


    //Titulo de la nota
    private String noteNota;
    //Descripcion
    private String noteDescripcion;
    //Fecha en que se escribio la nota
    private  String noteFecha;
    //TimeStamp para referencias
    private  String Timestamp;


    public Notas(String noteNota, String noteDescripcion, String noteFecha, String Timestamp) {
        this.noteNota = noteNota;
        this.noteDescripcion = noteDescripcion;
        this.noteFecha = noteFecha;
        this.Timestamp = Timestamp;
    }

    public String getNoteNota() {
        return noteNota;
    }

    public String getNoteDescripcion() {
        return noteDescripcion;
    }

    public String getNoteFecha() {
        return noteFecha;
    }

    public String getTimestamp() {
        return Timestamp;
    }

}


