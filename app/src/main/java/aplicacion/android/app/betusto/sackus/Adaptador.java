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

    private ArrayList<Gastos> notas;
    private Context context;
    public static int isPressed = 0;

    public Adaptador(ArrayList<Gastos> notas, Context context) {
        this.notas = notas;
        this.context = context;
        Log.e("TEST", "Notas tamaño: "+notas+" Context: "+context);
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.gasto_tab2_historial, viewGroup, false);
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
            }else{
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
            noteDate = itemView.findViewById(R.id.note_date);
            noteText = itemView.findViewById(R.id.note_text);
            checkBox = itemView.findViewById(R.id.checkBox);
            noteGasto = itemView.findViewById(R.id.gasto_text);
        }
    }

}