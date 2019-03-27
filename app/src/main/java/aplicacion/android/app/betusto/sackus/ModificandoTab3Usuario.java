package aplicacion.android.app.betusto.sackus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.ValueEventListener;

public class ModificandoTab3Usuario extends Fragment {
    private ImageButton Visibilidad;
    private EditText password;
    private EditText confirmarPassword;
    private EditText usuarioNuevo;
    private TextView usuarioActual;
    private ProgressDialog Progress;
    private DatabaseReference Database;
    final MetodosUtiles MU = new MetodosUtiles();
    final BaseDeDatos BD = new BaseDeDatos();
    private Button CambiarUsuario;
    //Detectores de error
    private int detectorDeErroresPassword = 0, detectorDeErroresNombreNuevo = 0;
    //Strings a usar en la base de datos
    public String usuarioNuevoStr, passwordUsuarioStr;
    public String ContraseñaReal;

    //Variable que nos permite vincular con el metodo que se encuentra en MetodosUtiles de VariablesEstaticas para guardar o cargar data
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.modificando_tab3_usuario, container, false);
        Visibilidad = rootView.findViewById(R.id.activity_modificando_tab3_visibility);
        confirmarPassword =  rootView.findViewById(R.id.activity_modificando_tab3_confirmarpasswordtext);
        usuarioNuevo = rootView.findViewById(R.id.activity_modificando_tab3_usuarionuevotext);
        usuarioActual = rootView.findViewById(R.id.activity_modificando_tab3_mostrarusuario);
        Progress = new ProgressDialog(getActivity());
        CambiarUsuario = rootView.findViewById(R.id.activity_modificando_tab3_cambiarusuariobutton);


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
        usuarioNuevo.setFilters(new InputFilter[]{EliminaEmoijis.filters()});

        Visibilidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MU.OjosContraseñas(confirmarPassword, Visibilidad);
            }
        });

        CambiarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuarioNuevoStr = usuarioNuevo.getText().toString().trim();
                passwordUsuarioStr = confirmarPassword.getText().toString().trim();
                detectorDeErroresPassword = BD.ValidarContraseña(passwordUsuarioStr, detectorDeErroresPassword, confirmarPassword, "Se requiere contraseña");
                detectorDeErroresNombreNuevo = BD.ValidarUsuario(usuarioNuevoStr, detectorDeErroresNombreNuevo, usuarioNuevo, "Se requiere" +
                        " el nuevo nombre de usuario");
                if (detectorDeErroresPassword == 0 && detectorDeErroresNombreNuevo == 0) {
                    Progress.setMessage("Modificando, por favor espere");
                    Progress.show();
                    RevisarMatchUsuario(); //Metodo encargado de ver si lo que se escribio coincide
                }
            }
        });

        return rootView;
    }

    public void RevisarMatchUsuario(){
        Database.child(VariablesEstaticas.CurrentUserUID).child("Contraseña").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ContraseñaReal = dataSnapshot.getValue().toString(); //Conseguimos el valor real de la contraseña del usuario
                //Si se escribio correctamente:
                if(passwordUsuarioStr.equals(ContraseñaReal)){
                    Database.child(VariablesEstaticas.CurrentUserUID).child("Usuario").setValue(usuarioNuevoStr); //Actualizamos la database
                    MU.MostrarToast(getActivity(), "El nombre de usuario se cambió exitosamente");
                    //Limpiamos cajas de texto
                    usuarioNuevo.getText().clear();
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
        BD.MostrarElementoUsuarioActual(Database, "Usuario", usuarioActual, sharedPreferences); //Escribir el nombre del usuario en el textview
        super.onStart();
    }


    @Override
    public void onResume() {
        //Necesario si nos movemos entre las tabs (al moverse entre las tabs se pierde el estado)
        MU.OjosContraseñasOnResume(confirmarPassword, Visibilidad);
        super.onResume();
    }
}
