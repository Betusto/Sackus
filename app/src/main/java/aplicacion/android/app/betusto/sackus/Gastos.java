package aplicacion.android.app.betusto.sackus;

public class Gastos {
    //Texto de la nota
    private String noteText;
    //Fecha de la nota
    private String noteDate;
    //Cantidad del gasto
    private  String noteGasto;

    public Gastos() {
    }

    public Gastos(String noteText, String noteDate, String noteGasto) {
        this.noteText = noteText;
        this.noteDate = noteDate;
        this.noteGasto = noteGasto;
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

}
