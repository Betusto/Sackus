package aplicacion.android.app.betusto.sackus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdaptadorNotas extends RecyclerView.Adapter<AdaptadorNotas.NoteHolder> {
    private static ArrayList<Notas> notas;
    private Context context;
    public static int isPressed = 0;
    private NotasListListener listener;

    public AdaptadorNotas(ArrayList<Notas> notas, Context context) {
        this.notas = notas;
        this.context = context;
        Log.e("TEST", "Notas tamaño: "+notas+" Context: "+context);
    }

    @Override
    public AdaptadorNotas.NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_notas, viewGroup, false);
        return new AdaptadorNotas.NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorNotas.NoteHolder noteHolder, int i) {
        final Notas notaApartado = getNotas(i);
        if (notaApartado != null) {
            if(isPressed == 0) {
                noteHolder.tituloText.setText(notaApartado.getNoteNota());
                noteHolder.descripcionText.setText(notaApartado.getNoteDescripcion());
                noteHolder.fechaText.setText(notaApartado.getNoteFecha());
                //Inicializamos el listener de click
                noteHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Generamos un alert dialog custom
                        final Activity contexto = (Activity) context;
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(contexto, R.style.BottomOptionsDialogTheme);
                        final MetodosUtiles MU = new MetodosUtiles();

                        View mView = contexto.getLayoutInflater().inflate(R.layout.editar_nota, null); //Referenciamos el custom layout
                        final EditText tituloEdit = mView.findViewById(R.id.activity_editar_nota_titulo_edittext);
                        final EditText cuerpoEdit =  mView.findViewById(R.id.activity_editar_nota_cuerpo_edittext);
                        Button listoButton =  mView.findViewById(R.id.activity_editar_nota_listobutton);
                        //Limitar a 2 las lineas que el usuario puede escribir
                        MU.LimitarLineasEditText(tituloEdit, 2);
                        MU.LimitarLineasEditText(cuerpoEdit, 10);
                        //MostrarNota(notas.getNoteNota(), notas.getNoteDescripcion(), notas.getTimestamp(), tituloEdit,cuerpoEdit);
                        tituloEdit.setText(notaApartado.getNoteNota());
                        cuerpoEdit.setText(notaApartado.getNoteDescripcion());

                        //Eliminar los emojis
                        MetodosUtiles EliminaEmoijis = new MetodosUtiles();
                        tituloEdit.setFilters(new InputFilter[]{EliminaEmoijis.filters()});
                        cuerpoEdit.setFilters(new InputFilter[]{EliminaEmoijis.filters()});

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();

                        dialog.setCancelable(true);
                        dialog.setCanceledOnTouchOutside(false);

                        dialog.show();
                        listoButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int detectorErrores = 0;
                                String tituloText = tituloEdit.getText().toString().trim();
                                String descripcionText = cuerpoEdit.getText().toString().trim();
                                if(tituloText.isEmpty()){
                                    tituloEdit.setError("La nota necesita un título");
                                    tituloEdit.requestFocus();
                                    detectorErrores = 1;
                                }
                                if(descripcionText.isEmpty()){
                                    cuerpoEdit.setError("La nota necesita contenido");
                                    cuerpoEdit.requestFocus();
                                    detectorErrores = 1;
                                }
                                if(detectorErrores == 0){
                                    ActualizarNoteNota("Titulo", tituloText, notaApartado.getTimestamp());
                                    ActualizarNoteNota("Descripcion", descripcionText, notaApartado.getTimestamp());
                                    MU.MostrarToast(contexto, "Nota actualizada");
                                    Intent reebot = new Intent(contexto, NotasActivity.class);
                                    reebot.putExtra("INTENT",1);
                                    contexto.finish();
                                    //Quitar animacione
                                    contexto.overridePendingTransition(0, 0);
                                    contexto.startActivity(reebot);
                                    contexto.overridePendingTransition(0, 0);
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                });
                //Inicializamos el listener de click largo
                noteHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        listener.onGastoLongClick(notaApartado);
                        return false;
                    }
                });
            }else{
                //Si se presiona el boton de agregar conseguirá la fecha actual
                noteHolder.tituloText.setText(notaApartado.getNoteNota());
                noteHolder.descripcionText.setText(notaApartado.getNoteDescripcion());
                noteHolder.fechaText.setText(MetodosUtiles.fechaHora(Long.parseLong(notaApartado.getNoteFecha())));
            }
        }
    }

    //TODO: Al darle al boton "listo" sin cambiar nada se sigue mostrando el Toast, lo mismo aplica para viajes
    public void ActualizarNoteNota(String elemento, String ValorActualizado, final String timestamp){
        //Referencia para la BD de forma en que podamos meter una tabla dentro de ella
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        //Referencia a la tabla del child:
        DatabaseReference nodo = database.child(VariablesEstaticas.CurrentUserUID);
        nodo.child("Notas").child(timestamp).child(elemento).setValue(ValorActualizado);
    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    private Notas getNotas(int position) {
        return notas.get(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        TextView tituloText, descripcionText, fechaText;

        public NoteHolder(View itemView) {
            super(itemView);
            //Referenciamos los elementos que se repiten en el listview
            tituloText = itemView.findViewById(R.id.titulo_text);
            descripcionText = itemView.findViewById(R.id.nota_descripcion_text);
            fechaText = itemView.findViewById(R.id.nota_fecha_text);
        }
    }

    public static ArrayList<Notas> ReiniciarNotas(){
        if(notas != null) {
            notas.clear();
            Log.e("test", "notas no es nulo");
        }
        return  notas;
    }

    //Marcamos el listener
    public void setListener(NotasListListener listener){
        this.listener = listener;
    }
}
