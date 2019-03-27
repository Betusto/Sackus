package aplicacion.android.app.betusto.sackus;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

import static aplicacion.android.app.betusto.sackus.VariablesEstaticas.SHARED_PREFS;

public class IngresarActivity extends AppCompatActivity {

    private EditText Usuario;
    private EditText Contraseña;
    private Button Ingresar;
    private Button CrearCuenta;
    private Button OlvideContraseña;
    private ImageButton nowifibutton;
    private ProgressDialog Progress;

    private int detectorDeErroresEmail = 0, detectorDeErroresPassword = 0;
    private String NuevoPassword;
    public static final String FINISH_ALERT = "finish_alert"; //Variable para terminar este activity desde otro

    private DatabaseReference Database;
    public int Vueltas = 0;
    //Variable que nos ayuda a conseguir el valor del UID segun el correo
    private String UIDDB;
    private boolean ExisteUsuario = false;
    private boolean ExisteCorreo = false;
    private boolean noMasDataChanges = false;
    private List CantidadDeSnapshots = new ArrayList();
    public String email, password;
    //Variable que nos permite vincular con el metodo que se encuentra en MetodosUtiles de VariablesEstaticas para guardar o cargar data
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar);
        this.registerReceiver(this.finishAlert, new IntentFilter(FINISH_ALERT)); //Linea de codigo necesaria para que funcione cerrar este activity desde otro
        //Persistencia de datos y referencia
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences= getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);

        if(!VariablesEstaticas.isLoged) { //Si el usuario no esta ingresado
            //Referenciamos el Auth y los datos del usuario
            Usuario = (EditText) findViewById(R.id.usuario);
            Contraseña = (EditText) findViewById(R.id.password);
            CrearCuenta = (Button) findViewById(R.id.crearbutton);
            Ingresar = (Button) findViewById(R.id.ingresarbutton);
            OlvideContraseña = (Button) findViewById(R.id.olvidebutton);
            nowifibutton = findViewById(R.id.activity_ingresar_nowifibutton);
            Progress = new ProgressDialog(this);

            //Eliminar los emojis
            MetodosUtiles EliminaEmoijis = new MetodosUtiles();
            Usuario.setFilters(new InputFilter[]{EliminaEmoijis.filters()});


            final DetectaConexion CD = new DetectaConexion(this);
            final MetodosUtiles MandarToast = new MetodosUtiles();
            CD.startConexion(nowifibutton);

            //No se puedan cancelar los progress
            Progress.setCancelable(false);
            Progress.setCanceledOnTouchOutside(false);

            //Efectua el metodo de Logear
            Ingresar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vueltas = 0;
                    Logear();
                }
            });

            //Ir al activity de registrar
            CrearCuenta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent registrar = new Intent(IngresarActivity.this, RegisterActivity.class);
                    startActivity(registrar);
                    //finish();
                }
            });

            //Ir al activity de cambiar contraseña
            OlvideContraseña.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(VariablesEstaticas.Locked == false) { //Si no esta locked
                        VariablesEstaticas.Locked = true;
                        if (CD.isConnected()) { //Si esta conectado a internet
                            Intent olvidepassword = new Intent(IngresarActivity.this, OlvidePassword.class); //Abre el activity
                            startActivity(olvidepassword);
                            VariablesEstaticas.Locked = false; //Para poder volver a entrar al boton
                        } else {    //Si no esta conectado a internet
                            MandarToast.MostrarToast(IngresarActivity.this,"Se necesita conexión a internet para acceder a " +
                                    "esta opción");
                            VariablesEstaticas.Locked = false; //Para volver a entrar al boton
                        }
                    }
                }
            });

            nowifibutton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    CD.mensajeNoInternet(IngresarActivity.this);
                }
            });

        }else{ //Si el usuario está ingresado
            Intent MainMenu = new Intent(IngresarActivity.this, MainMenu.class);
            startActivity(MainMenu);
            finish();
        }
    }

    //BroadcastReceiver encargado de cerrar este activity al ser llamado desde otro
    BroadcastReceiver finishAlert = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            IngresarActivity.this.finish();
        }
    };

    //unregistredReciver necesario para no crashear
    @Override
    public void onDestroy() {

        super.onDestroy();
        this.unregisterReceiver(finishAlert);
    }


    //Al iniciar el activity se ejecutara lo siguiente
    @Override
    protected void onStart() {
        super.onStart();
    }

    //Metodo para logear al usuario
    private void Logear() {
        email = Usuario.getText().toString().trim();
        password = Contraseña.getText().toString().trim();
        Validar(email, password);
        if (detectorDeErroresEmail == 0 && detectorDeErroresPassword == 0) {
            Progress.setMessage("Entrnado, por favor espere");
            Progress.show();
            RevisarBD();
        }
    }

    public void LogeoExitosoOffline(String UID){
        Toast.makeText(IngresarActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
        //VE.CuentaAbiertaGuardarUID(true, UID);
        VE.GuardarDatos(sharedPreferences, true, UID);
        //VariablesEstaticas.isLoged = true;
        Log.e("TEST", "VA A ENTRAR AL MAINMENU WTF");
        Intent mainmenu = new Intent(IngresarActivity.this, MainMenu.class);
        mainmenu.putExtra("El UID",UID);
        startActivity(mainmenu);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }



    //Metodo para validar los campos a ingresar del usuario
    private void Validar(String correoStr, String contraseñaStr){
        if(correoStr.isEmpty()){
            Usuario.setError("Correo requerido");
            Usuario.requestFocus();
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

    public void RevisarBD(){
        ExisteUsuario = false;
        ExisteCorreo = false;
        //Marcamos los setters
        Database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("TEST2", "TODO BIEN");
                final int CantidadTotalDeSnaps = (int) dataSnapshot.getChildrenCount();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) //Recorremos cada campo de la tabla Usuarios
                {
                    if(snapshot.getValue() != null) { //*No se puede usar equals en null aunque sea string*
                        Database.child(snapshot.getKey()).child("Correo").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                CantidadDeSnapshots.add(snapshot.child("Correo").getValue().toString());
                                GetterVerificador g2 = snapshot.getValue(GetterVerificador.class);
                                g2.setCorreoVerificador(email);
                                if (g2.getCorreoVerificador().equals(snapshot.child("Correo").getValue().toString())){
                                    NuevoPassword = snapshot.child("Contraseña").getValue().toString();
                                    UIDDB = snapshot.getKey();
                                    Database.child(UIDDB).child("CuentaAbierta").setValue("true");
                                    ExisteUsuario = true;
                                    detectorDeErroresEmail = 0;
                                    Log.e("TEST2", "VALOR DE DETECTORDEERRORESUSUARIO EN DATACHANGE: " + detectorDeErroresEmail + " el snapshot fue: " + snapshot.child("Correo").getValue().toString());
                                }else if(g2.getCorreoVerificador().toLowerCase().equals(snapshot.child("Correo").getValue().toString())){
                                    NuevoPassword = snapshot.child("Contraseña").getValue().toString();
                                    UIDDB = snapshot.getKey();
                                    Database.child(UIDDB).child("CuentaAbierta").setValue("true");
                                    ExisteUsuario = true;
                                    detectorDeErroresEmail = 0;
                                    Log.e("TEST2", "VALOR DE DETECTORDEERRORESUSUARIO EN DATACHANGE: " + detectorDeErroresEmail + " el snapshot fue: " + snapshot.child("Correo").getValue().toString());
                                }else{
                                    if (ExisteUsuario == false) {
                                        detectorDeErroresEmail = 0;
                                        detectorDeErroresEmail++;
                                    }
                                }
                                if(CantidadTotalDeSnaps == CantidadDeSnapshots.size()) { //Ultimo ciclo del for ejecutar lo siguiente
                                    Log.e("TEST", "Valor de NuevoPassword: "+NuevoPassword);
                                    if (detectorDeErroresEmail == 0 && detectorDeErroresPassword == 0 && NuevoPassword.equals(password)) {
                                        CantidadDeSnapshots.clear();
                                        Progress.dismiss();
                                        noMasDataChanges = true;
                                        //aqui va lo de cargar mainmenu
                                        //VariablesEstaticas.TEMPuid.add(UUID.randomUUID().toString().replace("-", ""));
                                        //logearUsuarioParaBD((String) VariablesEstaticas.TEMPuid.get(VariablesEstaticas.localDBControl));
                                        LogeoExitosoOffline(UIDDB);
                                    }else{
                                        if(noMasDataChanges == false) {
                                            CantidadDeSnapshots.clear();
                                            Progress.dismiss();
                                            //Aqui va si fracaso buscando por correo
                                            Database.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Log.e("TEST2", "TODO BIEN");
                                                    final int CantidadTotalDeSnaps = (int) dataSnapshot.getChildrenCount();
                                                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) //Recorremos cada campo de la tabla Usuarios
                                                    {
                                                        if (snapshot.getValue() != null) { //*No se puede usar equals en null aunque sea string*
                                                            Database.child(snapshot.getKey()).child("Usuario").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    CantidadDeSnapshots.add(snapshot.child("Usuario").getValue().toString());
                                                                    GetterVerificador g2 = snapshot.getValue(GetterVerificador.class);
                                                                    g2.setCorreoVerificador(email);
                                                                    Log.e("TEST", "Valor de g2.setCorreoVerificador: "+g2.getCorreoVerificador());
                                                                    Log.e("TEST", "Valor de snapshot Usuario: "+snapshot.child("Usuario").getValue().toString());


                                                                    if (g2.getCorreoVerificador().equals(snapshot.child("Usuario").getValue().toString())) {
                                                                        NuevoPassword = snapshot.child("Contraseña").getValue().toString();
                                                                        UIDDB = snapshot.getKey();
                                                                        Database.child(UIDDB).child("CuentaAbierta").setValue("true");
                                                                        ExisteUsuario = true;
                                                                        detectorDeErroresEmail = 0;
                                                                        Log.e("TEST2", "VALOR DE DETECTORDEERRORESUSUARIO EN DATACHANGE: " + detectorDeErroresEmail + " el snapshot fue: " + snapshot.child("Usuario").getValue().toString());
                                                                    } else {
                                                                        if (ExisteUsuario == false) {
                                                                            detectorDeErroresEmail = 0;
                                                                            detectorDeErroresEmail++;
                                                                        }
                                                                    }

                                                                    if (CantidadTotalDeSnaps == CantidadDeSnapshots.size()) { //Ultimo ciclo del for ejecutar lo siguiente
                                                                        Log.e("TEST", "Valor de NuevoPassword: "+NuevoPassword);
                                                                        if (detectorDeErroresEmail == 0 && detectorDeErroresPassword == 0 && NuevoPassword.equals(password)) {
                                                                            CantidadDeSnapshots.clear();
                                                                            Progress.dismiss();
                                                                            noMasDataChanges = true;

                                                                            //aqui va lo de cargar mainmenu
                                                                            //VariablesEstaticas.TEMPuid.add(UUID.randomUUID().toString().replace("-", ""));
                                                                            //logearUsuarioParaBD((String) VariablesEstaticas.TEMPuid.get(VariablesEstaticas.localDBControl));
                                                                            LogeoExitosoOffline(UIDDB);
                                                                        } else {
                                                                            if (noMasDataChanges == false) {
                                                                                CantidadDeSnapshots.clear();
                                                                                Progress.dismiss();
                                                                                Toast.makeText(IngresarActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    }
                                                                }


                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                    //Toast.makeText(RegisterActivity.this, "Ocurrio un error al intentar acceder a la base de datos", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }@Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    //Toast.makeText(RegisterActivity.this, "Ocurrio un error al intentar acceder a la base de datos", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }else{
                                            CantidadDeSnapshots.clear();
                                            Progress.dismiss();
                                            Toast.makeText(IngresarActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //Toast.makeText(RegisterActivity.this, "Ocurrio un error al intentar acceder a la base de datos", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(IngresarActivity.this, "Ocurrio un error al intentar acceder a la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}