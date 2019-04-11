package aplicacion.android.app.betusto.sackus;

import android.annotation.SuppressLint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ResourceBundle;

public class ModificandoTab1Password extends Fragment {
    private ImageButton Visibilidad1;
    private ImageButton Visibilidad2;
    private ImageButton Visibilidad3;
    private EditText password;
    private EditText passwordNuevo;
    private EditText confirmarPassword;
    private boolean visible1 = false; private boolean visible2 = false; private boolean visible3 = false;
    private ProgressDialog Progress;
    private DatabaseReference Database;
    private Button CambiarContraseña;
    MetodosUtiles MU = new MetodosUtiles();
    BaseDeDatos BD = new BaseDeDatos();
    //Detectores de error
    private int detectorDeErroresPassword = 0, detectorDeErroresPasswordNuevo = 0, detectorDeErroresConfirmarPassword = 0;
    //Strings a usar en la base de datos
    public String passwordStr; public String passwordNuevoStr; public String confirmarPassowrdStr;
    public String ContraseñaReal;

    //Variable que nos permite vincular con el metodo que se encuentra en MetodosUtiles de VariablesEstaticas para guardar o cargar data
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.modificando_tab1_password, container, false);
        Visibilidad1 = rootView.findViewById(R.id.activity_modificando_tab1_visibility1);
        Visibilidad2 = rootView.findViewById(R.id.activity_modificando_tab1_visibility2);
        Visibilidad3 = rootView.findViewById(R.id.activity_modificando_tab1_visibility3);
        password =  rootView.findViewById(R.id.activity_modificando_tab1_passwordactualtext);
        passwordNuevo = rootView.findViewById(R.id.activity_modificando_tab1_passwordnuevotext);
        confirmarPassword = rootView.findViewById(R.id.activity_modificando_tab1_confirmarpasswordtext);
        Progress = new ProgressDialog(getActivity());
        CambiarContraseña = rootView.findViewById(R.id.activity_modificando_tab1_cambiarpasswordbutton);

        //No se puedan cancelar los progress
        Progress.setCancelable(false);
        Progress.setCanceledOnTouchOutside(false);
        //Sincronizamos
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences= this.getActivity().getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);


        //Se encargan de mostrar las imagenes de los ojos
        Visibilidad1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible1 = MU.OjosContraseñasMultiples(password, Visibilidad1, visible1);
            }
        });

        Visibilidad2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible2 = MU.OjosContraseñasMultiples(passwordNuevo, Visibilidad2, visible2);
            }
        });


        Visibilidad3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible3 = MU.OjosContraseñasMultiples(confirmarPassword, Visibilidad3, visible3);
            }
        });

        CambiarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordStr = password.getText().toString().trim();
                passwordNuevoStr = passwordNuevo.getText().toString().trim();
                confirmarPassowrdStr = confirmarPassword.getText().toString().trim();
                detectorDeErroresPassword = BD.ValidarContraseña(passwordStr, detectorDeErroresPassword, password, getResources().getString(R.string.se_requiere_contraseña));
                detectorDeErroresPasswordNuevo = BD.ValidarContraseña(passwordNuevoStr, detectorDeErroresPasswordNuevo, passwordNuevo, getResources().getString(R.string.se_requiere_contraseña_nueva));
                detectorDeErroresConfirmarPassword = BD.ValidarContraseña(confirmarPassowrdStr, detectorDeErroresConfirmarPassword, confirmarPassword,
                        getResources().getString(R.string.se_requiere_confirmar_contraseña));
                if (detectorDeErroresPassword == 0 && detectorDeErroresPasswordNuevo == 0 && detectorDeErroresConfirmarPassword == 0) {
                    Progress.setMessage(getResources().getString(R.string.modificando_espere));
                    Progress.show();
                    RevisarMatchPassowrd(); //Metodo encargado de ver si lo que se escribio coincide
                }
            }
        });

        return rootView;
    }

    @Override
    public  void onStart(){
        super.onStart();
    }

    public void RevisarMatchPassowrd(){
        Database.child(VariablesEstaticas.CurrentUserUID).child("Contraseña").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ContraseñaReal = dataSnapshot.getValue().toString(); //Conseguimos el valor real de la contraseña del usuario
                if(!passwordStr.equals(ContraseñaReal)){
                    password.setError(getResources().getString(R.string.la_contraseña_no_es_correcta));
                    password.requestFocus();
                }
                if(!passwordNuevoStr.equals(confirmarPassowrdStr)){
                    confirmarPassword.setError(getResources().getString(R.string.la_contraseña_nueva_no_concuerda));
                    confirmarPassword.requestFocus();
                }
                //Si se escribio correctamente:
                if(passwordStr.equals(ContraseñaReal) && passwordNuevoStr.equals(confirmarPassowrdStr)){
                    Database.child(VariablesEstaticas.CurrentUserUID).child("Contraseña").setValue(passwordNuevoStr); //Actualizamos la database
                    MU.MostrarToast(getActivity(), getResources().getString(R.string.la_contraseña_se_cambio_exitosamente));
                    //Limpiamos cajas de texto
                    password.getText().clear();
                    passwordNuevo.getText().clear();
                    confirmarPassword.getText().clear();
                }
                Progress.dismiss();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onResume() {
        //Necesario si nos movemos entre las tabs (al moverse entre las tabs se pierde el estado)
        MU.OjosContraseñasOnResumeMultiples(password, Visibilidad1, visible1);
        MU.OjosContraseñasOnResumeMultiples(passwordNuevo, Visibilidad2, visible2);
        MU.OjosContraseñasOnResumeMultiples(confirmarPassword, Visibilidad3, visible3);
        super.onResume();
    }
}