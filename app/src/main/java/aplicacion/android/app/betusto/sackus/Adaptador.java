package aplicacion.android.app.betusto.sackus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public class Adaptador extends RecyclerView.Adapter<Adaptador.NoteHolder> {

    private ArrayList<Notas> notas;
    private Context context;

    public Adaptador(ArrayList<Notas> notas, Context context) {
        this.notas = notas;
        this.context = context;
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.gasto_tab2_historial, viewGroup, false);
        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteHolder noteHolder, int position) {
        final Notas nota = getNotas(position);
        if (nota != null) {
            noteHolder.noteText.setText(nota.getNoteText());
            noteHolder.noteDate.setText(MetodosUtiles.fechaHora(nota.getNoteDate()));
        }
    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    private Notas getNotas(int position) {
        return notas.get(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        TextView noteText, noteDate;
        CheckBox checkBox;

        public NoteHolder(View itemView) {
            super(itemView);
            noteDate = itemView.findViewById(R.id.note_date);
            noteText = itemView.findViewById(R.id.note_text);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

}