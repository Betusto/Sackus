package aplicacion.android.app.betusto.sackus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorInvitaciones extends RecyclerView.Adapter<AdaptadorInvitaciones.NoteHolder> {
    private static ArrayList<Invitaciones> invitaciones;
    private Context context;
    public static int isPressed = 0;
    private MensajeListListener listener;

    public AdaptadorInvitaciones(ArrayList<Invitaciones> invitaciones, Context context) {
        this.invitaciones = invitaciones;
        this.context = context;
        Log.e("TEST", "Notas tamaño: "+invitaciones+" Context: "+context);
    }

    @Override
    public AdaptadorInvitaciones.NoteHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_invitaciones, viewGroup, false);
        return new AdaptadorInvitaciones.NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(AdaptadorInvitaciones.NoteHolder noteHolder, int position) {
        final Invitaciones invitacion = getInvitaciones(position);
        if (invitacion != null) {
            if(isPressed == 0) {
                noteHolder.nombreViajeText.setText(invitacion.getNoteViaje());
                noteHolder.mensajeText.setText(invitacion.getNoteMensaje());
                noteHolder.fechaText.setText(invitacion.getNoteFecha());
                noteHolder.enviadoPorText.setText(invitacion.getEnviadoPor());
                //Inicializamos el listener de click
                noteHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onGastoClick(invitacion);
                    }
                });
                //Inicializamos el listener de click largo
                noteHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        listener.onGastoLongClick(invitacion);
                        return false;
                    }
                });
            }else{
                //Si se presiona el boton de agregar conseguirá la fecha actual
                noteHolder.nombreViajeText.setText(invitacion.getNoteViaje());
                noteHolder.mensajeText.setText(invitacion.getNoteMensaje());
                noteHolder.fechaText.setText(MetodosUtiles.fechaHora(Long.parseLong(invitacion.getNoteFecha())));
                noteHolder.enviadoPorText.setText(invitacion.getEnviadoPor());
            }
        }
    }

    @Override
    public int getItemCount() {
        Log.e("TEST", "COUNT: "+invitaciones.size() );
        return invitaciones.size();
    }

    private Invitaciones getInvitaciones(int position) {
        return invitaciones.get(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        TextView nombreViajeText, mensajeText, fechaText, enviadoPorText;

        public NoteHolder(View itemView) {
            super(itemView);
            //Referenciamos los elementos que se repiten en el listview
            nombreViajeText = itemView.findViewById(R.id.nombreviaje_text);
            mensajeText = itemView.findViewById(R.id.mensaje_text);
            fechaText = itemView.findViewById(R.id.fecha_text);
            enviadoPorText = itemView.findViewById(R.id.enviadopor_text);
        }
    }

    //Marcamos el listener
    public void setListener(MensajeListListener listener){
        this.listener = listener;
    }
}
