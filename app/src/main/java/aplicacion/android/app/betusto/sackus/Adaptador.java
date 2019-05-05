package aplicacion.android.app.betusto.sackus;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.ArrayList;


public class Adaptador extends RecyclerView.Adapter<Adaptador.NoteHolder> {

    private static ArrayList<Gastos> notas;
    private Context context;
    public static int isPressed = 0;
    private ListListener listener;

    public Adaptador(ArrayList<Gastos> notas, Context context) {
        this.notas = notas;
        this.context = context;
        Log.e("TEST", "Notas tamaño: "+notas+" Context: "+context);
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_gastos, viewGroup, false);
        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteHolder noteHolder, int position) {
        final Gastos nota = getNotas(position);
        if (nota != null) {
            if(isPressed == 0) {
                noteHolder.noteText.setText(nota.getNoteText());
                noteHolder.noteDate.setText(nota.getNoteDate());
                noteHolder.noteGasto.setText(nota.getNoteGasto() + ""); //Hacerlo string
                //Inicializamos el listener de click
                noteHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onGastoClick(nota);
                    }
                });
                //Inicializamos el listener de click largo
                noteHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        listener.onGastoLongClick(nota);
                        return false;
                    }
                });
            }else{
                //Si se presiona el boton de agregar conseguirá la fecha actual
                noteHolder.noteText.setText(nota.getNoteText());
                noteHolder.noteDate.setText(MetodosUtiles.fechaHora(Long.parseLong(nota.getNoteDate())));
                noteHolder.noteGasto.setText(nota.getNoteGasto() + ""); //Hacerlo string
            }
        }
    }

    @Override
    public int getItemCount() {
        Log.e("TEST", "COUNT: "+notas.size() );
        return notas.size();
    }

    private Gastos getNotas(int position) {
        return notas.get(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        TextView noteText, noteDate, noteGasto;
        CheckBox checkBox;

        public NoteHolder(View itemView) {
            super(itemView);
            //Referenciamos los elementos que se repiten en el listview
            noteDate = itemView.findViewById(R.id.note_date);
            noteText = itemView.findViewById(R.id.note_text);
            checkBox = itemView.findViewById(R.id.checkBox);
            noteGasto = itemView.findViewById(R.id.gasto_text);
        }
    }

    public static ArrayList<Gastos> ReiniciarNotas(){
        if(notas != null) {
            notas.clear();
            Log.e("test", "notas no es nulo");
        }
        return  notas;
    }

    //Marcamos el listener
    public void setListener(ListListener listener){
        this.listener = listener;
    }

}