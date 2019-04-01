package aplicacion.android.app.betusto.sackus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static aplicacion.android.app.betusto.sackus.VariablesEstaticas.SHARED_PREFS;

//OnDataChanges solo se efectuan al final del codigo, revisar si en OnStart o en OnCreate se ejecutan primero, hay que tener precaucion con los metodos que tengan ondatachange

public class RegisterActivity extends AppCompatActivity {
    //Referenciamos a la base de datos
    private EditText Correo;
    private EditText Usuario;
    private EditText Contraseña;
    private Button Registrar;
    private Button YaTengoCuenta;
    private int detectorDeErroresEmail = 0, detectorDeErroresPassword = 0, detectorDeErroresUsuario = 0;
    public String usuarioStr, correoStr, contraseñaStr;
    private ProgressDialog Progress;
    private ImageButton nowifibutton;
    private DatabaseReference Database;
    //Variable para indicar si el nombre de usuario existe dentro de la tabla Usuarios
    private boolean ExisteUsuario = false;
    private boolean ExisteCorreo = false;
    private boolean noMasDataChanges = false;
    private List CantidadDeSnapshots = new ArrayList();
    //Variable que nos permite vincular con el metodo que se encuentra en MetodosUtiles de VariablesEstaticas para guardar o cargar data
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Sincronizar variables
        Correo = (EditText) findViewById(R.id.correo);
        Usuario = (EditText) findViewById(R.id.usuario);
        Contraseña = (EditText) findViewById(R.id.password);
        Registrar = (Button) findViewById(R.id.registrarbutton);
        YaTengoCuenta = (Button) findViewById(R.id.tengocuentabutton);
        nowifibutton = findViewById(R.id.activity_register_nowifibutton);
        Progress = new ProgressDialog(this);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);



        //No se puedan cancelar los progress
        Progress.setCancelable(false);
        Progress.setCanceledOnTouchOutside(false);

        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        sharedPreferences = getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);


        //Eliminar los emojis
        MetodosUtiles EliminaEmoijis = new MetodosUtiles();
        Usuario.setFilters(new InputFilter[]{EliminaEmoijis.filters()});
        Correo.setFilters(new InputFilter[]{EliminaEmoijis.filters()});

        final DetectaConexion CD = new DetectaConexion(this);
        CD.startConexion(nowifibutton);

        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        //Boton para regresar al login
        YaTengoCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nowifibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CD.mensajeNoInternet(RegisterActivity.this);
            }
        });
    }

    private void registrarUsuario() {
        //Al oprimir el boton de registrar tomamos los textos que haya escrito el usuario
        usuarioStr = Usuario.getText().toString().trim();
        correoStr = Correo.getText().toString().toLowerCase().trim();
        contraseñaStr = Contraseña.getText().toString().trim();
        //Verificamos que el usuario no haya dejado campos sin escribir
        Validar(usuarioStr, correoStr, contraseñaStr);
        if (detectorDeErroresEmail == 0 && detectorDeErroresPassword == 0) {
        Progress.setMessage("Registrando, por favor espere");
        Progress.show();
        CheckUsuarioExiste(); //Se encarga de revisar si el usuario existe en la base de datos
        }
    }


    private void logearUsuarioParaBD(final String uid) {
        GettersDeUsuarios g = new GettersDeUsuarios(usuarioStr, correoStr, contraseñaStr, "true", "$0"); //Usuario se logea al crear la cuenta HAY QUE CREAR UN FALSE AL CERRAR SESION
            Database.child(uid).setValue(g);
            LogeoExitoso(uid);
        }

public void LogeoExitoso(String UID){
    Progress.dismiss();
    Toast.makeText(RegisterActivity.this, "Usuario generado en BD", Toast.LENGTH_SHORT).show();
    Intent mainmenu = new Intent(RegisterActivity.this, MainMenu.class);
    mainmenu.putExtra("El UID",UID);
    startActivity(mainmenu);
    Intent ingresar = new Intent(IngresarActivity.FINISH_ALERT); //Vinculamos la variable de IngresarActivity acá
    RegisterActivity.this.sendBroadcast(ingresar); //Llamamos al Broadcast para que lo cierre
    //VE.CuentaAbiertaGuardarUID(true, UID); //isLogged ahora es true
    VE.GuardarDatos(sharedPreferences, true, UID); //Guardamos los cambios, la variable islogged y el uid
    //VariablesEstaticas.isLoged = true;
    finish(); //Terminamos registeractivity
    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
}

    @Override
    protected void onStart() {
        super.onStart();
    }


    //Metodo para validar los campos a ingresar del usuario
    private void Validar(String usuarioStr, String correoStr, String contraseñaStr){

        if(usuarioStr.isEmpty()){
            Usuario.setError("Nombre de usuario requerido");
            Usuario.requestFocus();
            detectorDeErroresUsuario++;
        }else{
            detectorDeErroresUsuario = 0;
        }

        if(correoStr.isEmpty()){
            Correo.setError("Correo requerido");
            Correo.requestFocus();
            detectorDeErroresEmail++;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(correoStr).matches()){
            Correo.setError("Ingrese un email valido");
            Correo.requestFocus();
            detectorDeErroresEmail++;
        }else{
            detectorDeErroresEmail = 0;
        }

        if(contraseñaStr.isEmpty()){
            Contraseña.setError("Contraseña requerida");
            Contraseña.requestFocus();
            detectorDeErroresPassword++;
        }else if(contraseñaStr.length() < 6){
            Contraseña.setError("La contraseña es muy corta");
            Contraseña.requestFocus();
            detectorDeErroresPassword++;
        }else{
            detectorDeErroresPassword = 0;
        }
    }

    //Revisar conexion internet
    @Override
    protected void onResume() {
        //vuelve visible o invisible el boton
        DetectaConexion CD = new DetectaConexion(this);
        CD.ConexionPorSegundos(nowifibutton);
        super.onResume();
    }

    //Detiene la busqueda de conexion a internet si se detiene la ventana
    @Override
    protected void onPause() {
        DetectaConexion CD = new DetectaConexion(this);
        CD.DetenerContador();
        super.onPause();
    }

    public void CheckUsuarioExiste(){
        ExisteUsuario = false;
        ExisteCorreo = false;
        //Marcamos los setters
        Database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("TEST2", "TODO BIEN");
                final int CantidadTotalDeSnaps = (int) dataSnapshot.getChildrenCount();
                if(dataSnapshot.getValue() != null) { //*No se puede usar equals en null aunque sea string*
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) //Recorremos cada campo de la tabla Usuarios
                    {
                        Database.child(snapshot.getKey()).child("Usuario").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                CantidadDeSnapshots.add(snapshot.child("Usuario").getValue().toString());
                                GetterVerificador g2 = snapshot.getValue(GetterVerificador.class);
                                g2.setUsuarioVerificador(usuarioStr);
                                g2.setCorreoVerificador(correoStr);


                                if (g2.getUsuarioVerificador().equals(snapshot.child("Usuario").getValue().toString())){
                                        ExisteUsuario = true;
                                        detectorDeErroresUsuario = 0;
                                        detectorDeErroresUsuario++;
                                        Log.e("TEST2", "VALOR DE DETECTORDEERRORESUSUARIO EN DATACHANGE: " + detectorDeErroresUsuario + " el snapshot fue: " + snapshot.child("Usuario").getValue().toString());
                                    }else {
                                    if (ExisteUsuario == false) {
                                        detectorDeErroresUsuario = 0;
                                    }
                                }
                                  if(g2.getCorreoVerificador().equals(snapshot.child("Correo").getValue().toString())){
                                        ExisteCorreo = true;
                                        detectorDeErroresEmail = 0;
                                      detectorDeErroresEmail++;
                                  }else{
                                      if(ExisteCorreo == false){
                                        detectorDeErroresEmail = 0;
                                    }
                                }
                                if(CantidadTotalDeSnaps == CantidadDeSnapshots.size()) { //Ultimo ciclo del for ejecutar lo siguiente
                                    if (detectorDeErroresEmail == 0 && detectorDeErroresPassword == 0 && detectorDeErroresUsuario == 0) {
                                            CantidadDeSnapshots.clear();
                                            Progress.dismiss();
                                            noMasDataChanges = true;
                                            logearUsuarioParaBD(VariablesEstaticas.UID);
                                    }else{
                                        if(noMasDataChanges == false) {
                                            CantidadDeSnapshots.clear();
                                            Progress.dismiss();
                                            if(detectorDeErroresUsuario != 0) {
                                                Usuario.requestFocus();
                                                Usuario.setError("El nombre de usuario ingresado ya existe");
                                            }
                                            if(detectorDeErroresEmail != 0){
                                                Correo.requestFocus();
                                                Correo.setError("El correo ingresado ya existe");
                                            }
                                        }else{
                                            CantidadDeSnapshots.clear();
                                            Progress.dismiss();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //Toast.makeText(RegisterActivity.this, "Ocurrio un error al intentar acceder a la base de datos", Toast.LENGTH_SHORT).show();
                            }
                        });
                        //Si no hay ningun usuario en la base de datos
                        }
                        }else{
                    Progress.dismiss();
                    logearUsuarioParaBD(VariablesEstaticas.UID);
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegisterActivity.this, "Ocurrio un error al intentar acceder a la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
        Log.e("TEST2", "VALOR DE DETECTORDEERRORESUSUARIO ANTES DE RETORNAR: "+detectorDeErroresUsuario);
    }
}
