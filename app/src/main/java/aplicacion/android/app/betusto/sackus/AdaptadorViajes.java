package aplicacion.android.app.betusto.sackus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorViajes extends RecyclerView.Adapter<AdaptadorViajes.NoteHolder> {
    private static ArrayList<Viajes> viajes;
    private Context context;
    public static int isPressed = 0;
    private ViajeListListener listener;

    public AdaptadorViajes(ArrayList<Viajes> viajes, Context context) {
        this.viajes = viajes;
        this.context = context;
        Log.e("TEST", "Notas tamaño: "+viajes+" Context: "+context);
    }

    @Override
    public AdaptadorViajes.NoteHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_viajes, viewGroup, false);
        return new AdaptadorViajes.NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(AdaptadorViajes.NoteHolder noteHolder, int position) {
        final Viajes viaje = getViajes(position);
        if (viaje != null) {
            if(isPressed == 0) {
                noteHolder.nombreViajeText.setText(viaje.getNoteViaje());
                noteHolder.dineroDedicadoText.setText(viaje.getNoteDinero());
                noteHolder.fechaInicioText.setText(viaje.getNoteFechaInicio());
                noteHolder.fechaRegresoText.setText(viaje.getNoteFechaRegreso());
                //Inicializamos el listener de click
                noteHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onGastoClick(viaje);
                    }
                });
                //Inicializamos el listener de click largo
                noteHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        listener.onGastoLongClick(viaje);
                        return false;
                    }
                });
            }else{
                //Si se presiona el boton de agregar conseguirá la fecha actual
                noteHolder.nombreViajeText.setText(viaje.getNoteViaje());
                noteHolder.dineroDedicadoText.setText(viaje.getNoteDinero());
                noteHolder.fechaInicioText.setText(viaje.getNoteFechaInicio());
                noteHolder.fechaRegresoText.setText(viaje.getNoteFechaRegreso());
            }

            if(viaje.getNoteCompartido().equals("true")){
                noteHolder.compartidoImage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        Log.e("TEST", "COUNT: "+viajes.size() );
        return viajes.size();
    }

    private Viajes getViajes(int position) {
        return viajes.get(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        TextView nombreViajeText, dineroDedicadoText, fechaInicioText, fechaRegresoText;
        ImageView compartidoImage;

        public NoteHolder(View itemView) {
            super(itemView);
            //Referenciamos los elementos que se repiten en el listview
            nombreViajeText = itemView.findViewById(R.id.viaje_text);
            dineroDedicadoText = itemView.findViewById(R.id.dinerodedicado_text);
            fechaInicioText = itemView.findViewById(R.id.fechainicio_text);
            fechaRegresoText = itemView.findViewById(R.id.fecharegreso_text);
            compartidoImage = itemView.findViewById(R.id.viaje_image);
        }
    }

    //Marcamos el listener
    public void setListener(ViajeListListener listener){
        this.listener = listener;
    }
}