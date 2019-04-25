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

    public String getTimestamp() {
        return Timestamp;
    }
}
