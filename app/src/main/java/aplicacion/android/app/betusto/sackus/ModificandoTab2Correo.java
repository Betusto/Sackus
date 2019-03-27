package aplicacion.android.app.betusto.sackus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.support.v4.app.Fragment;
import com.google.firebase.database.ValueEventListener;

public class ModificandoTab2Correo extends Fragment {
    private ImageButton Visibilidad;
    private EditText confirmarPassword;
    private EditText correoNuevo;
    private TextView correoActual;
    private ProgressDialog Progress;
    private DatabaseReference Database;
    final MetodosUtiles MU = new MetodosUtiles();
    final BaseDeDatos BD = new BaseDeDatos();
    private Button CambiarCorreo;
    //Detectores de error
    private int detectorDeErroresPassword = 0, detectorDeErroresCorreoNuevo = 0;
    //Strings a usar en la base de datos
    public String correoNuevoStr, passwordCorreoStr;
    public String ContraseñaReal;

    //Variable que nos permite vincular con el metodo que se encuentra en MetodosUtiles de VariablesEstaticas para guardar o cargar data
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.modificando_tab2_correo, container, false);
        Visibilidad = rootView.findViewById(R.id.activity_modificando_tab2_visibility);
        confirmarPassword =  rootView.findViewById(R.id.activity_modificando_tab2_confirmarpasswordtext);
        correoNuevo = rootView.findViewById(R.id.activity_modificando_tab2_correonuevotext);
        correoActual = rootView.findViewById(R.id.activity_modificando_tab2_mostrarcorreo);
        Progress = new ProgressDialog(getActivity());
        CambiarCorreo = rootView.findViewById(R.id.activity_modificando_tab2_cambiarcorreobutton);


        //No se puedan cancelar los progress
        Progress.setCancelable(false);
        Progress.setCanceledOnTouchOutside(false);

        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences= this.getActivity().getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);

        //Eliminar los emojis
        MetodosUtiles EliminaEmoijis = new MetodosUtiles();
        correoNuevo.setFilters(new InputFilter[]{EliminaEmoijis.filters()});


        Visibilidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MU.OjosContraseñas(confirmarPassword, Visibilidad);
            }
        });

        CambiarCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correoNuevoStr = correoNuevo.getText().toString().toLowerCase().trim();
                passwordCorreoStr = confirmarPassword.getText().toString().trim();
                detectorDeErroresPassword = BD.ValidarContraseña(passwordCorreoStr, detectorDeErroresPassword, confirmarPassword, "Se requiere contraseña");
                detectorDeErroresCorreoNuevo = BD.ValidarCorreo(correoNuevoStr, detectorDeErroresCorreoNuevo, correoNuevo, "Se requiere" +
                        " el nuevo correo");
                if (detectorDeErroresPassword == 0 && detectorDeErroresCorreoNuevo == 0) {
                    Progress.setMessage("Modificando, por favor espere");
                    Progress.show();
                    RevisarMatchCorreo(); //Metodo encargado de ver si lo que se escribio coincide
                }
            }
        });

        return rootView;
    }

    public void RevisarMatchCorreo(){
        Database.child(VariablesEstaticas.CurrentUserUID).child("Contraseña").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ContraseñaReal = dataSnapshot.getValue().toString(); //Conseguimos el valor real de la contraseña del usuario
                //Si se escribio correctamente:
                if(passwordCorreoStr.equals(ContraseñaReal)){
                    Database.child(VariablesEstaticas.CurrentUserUID).child("Correo").setValue(correoNuevoStr); //Actualizamos la database
                    MU.MostrarToast(getActivity(), "El correo se cambió exitosamente");
                    //Limpiamos cajas de texto
                    correoNuevo.getText().clear();
                    confirmarPassword.getText().clear();
                }else{
                    confirmarPassword.setError("La contraseña actual escrita no es la correcta");
                    confirmarPassword.requestFocus();
                }
                Progress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public  void onStart(){
        BD.MostrarElementoUsuarioActual(Database, "Correo", correoActual, sharedPreferences); //Escribir el correo en el textview
        super.onStart();
    }

    @Override
    public void onResume() {
        //Necesario si nos movemos entre las tabs (al moverse entre las tabs se pierde el estado)
        MU.OjosContraseñasOnResume(confirmarPassword, Visibilidad);
        super.onResume();
    }

}
