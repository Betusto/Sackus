package aplicacion.android.app.betusto.sackus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.UUID;

public class UsuarioActivity extends AppCompatActivity {

    private ImageButton nowifibutton;
    private Button cerrarsesion;
    private Button modificarcuenta;
    private DatabaseReference Database;
    private TextView UsuarioTextView;
    private ImageButton Eliminarbutton;
    BaseDeDatos BD = new BaseDeDatos();
    //Variable que nos permite vincular con el metodo que se encuentra en MetodosUtiles de VariablesEstaticas para guardar o cargar data
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();
    private String ContraseñaReal;
    private int detectorDeErroresPassword = 0;
    private ProgressDialog Progress;
    private String contraseñaStr;
    //Edittext de la contraseña a ingresar
    EditText input;
    MetodosUtiles MU = new MetodosUtiles();

    //Para cambiar de idioma
    private Spinner mLanguage;
    private Button btnSubmit;
    private String idiomaApp;
    private ArrayAdapter<String> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        //Persistencia de datos y referencia
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences= getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);

        nowifibutton = findViewById(R.id.activity_usuario_nowifibutton);
        cerrarsesion = findViewById(R.id.activity_usuario_cerrarsesionbutton);
        modificarcuenta = findViewById(R.id.activity_usuario_modificarcuentabutton);
        UsuarioTextView = findViewById(R.id.activity_usuario_titulo);
        Eliminarbutton = findViewById(R.id.activity_usuario_eliminarbutton);
        Progress = new ProgressDialog(this);

        //Necesario para mostrar la conexión al inicio
        final DetectaConexion CD = new DetectaConexion(this);
        CD.startConexion(nowifibutton);

        nowifibutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CD.mensajeNoInternet(UsuarioActivity.this);
            }
        });

        //Ir al activity de modificar datos
        modificarcuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modify = new Intent(UsuarioActivity.this,     ModificandoActivity.class);
                startActivity(modify);

            }
        });

        //Cambiar idioma
        mLanguage = (Spinner) findViewById(R.id.spLanguage);
        btnSubmit = (Button) findViewById(R.id.cambiar_idioma);
        mAdapter = new ArrayAdapter<String>(UsuarioActivity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.language_option));
        mLanguage.setAdapter(mAdapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(UsuarioActivity.this,
                        "Idioma: "+ String.valueOf(mLanguage.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
                if(String.valueOf(mLanguage.getSelectedItem()).equals("English")){
                    idiomaApp = "en";
                    setAppLocale(idiomaApp);
                    Intent cerrarMainMenu = new Intent(MainMenu.FINISH_ALERT);
                    UsuarioActivity.this.sendBroadcast(cerrarMainMenu);
                    startActivity(new Intent(UsuarioActivity.this,     MainMenu.class));
                    finish();
                }
                else {
                    idiomaApp = "es";
                    setAppLocale(idiomaApp);
                    Intent cerrarMainMenu = new Intent(MainMenu.FINISH_ALERT);
                    UsuarioActivity.this.sendBroadcast(cerrarMainMenu);
                    startActivity(new Intent(UsuarioActivity.this,     MainMenu.class));
                    finish();
                }
            }

        });

        //Eliminar cuenta
        Eliminarbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UsuarioActivity.this);
                builder.setTitle(getResources().getString(R.string.desea_eliminar_su_cuenta));
                builder.setMessage(getResources().getString(R.string.aviso_eliminacion_de_cuenta));
                builder.setCancelable(true);
                //Hacemos el icono de color rojo
                Drawable icon = ContextCompat.getDrawable(UsuarioActivity.this,android.R.drawable.ic_dialog_alert);
                icon.setColorFilter(new
                        PorterDuffColorFilter(Color.RED,PorterDuff.Mode.MULTIPLY));
                builder.setIcon(icon); //R.color.colorButtonRed
                //Boton eliminar
                builder.setPositiveButton(
                        getResources().getString(R.string.eliminar_mayus),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AlertDialog.Builder buildersecundario = new AlertDialog.Builder(UsuarioActivity.this);
                                buildersecundario.setTitle(getResources().getString(R.string.eliminar_cuenta));
                                buildersecundario.setMessage(getResources().getString(R.string.escriba_su_contraseña_para_eliminar_cuenta));

                                //EditText que incluiremos para poder escribir la contraseña
                                input = new EditText(UsuarioActivity.this);
                                //Estilo contraseña
                                input.setInputType(InputType.TYPE_CLASS_TEXT |
                                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                input.setSelection(input.getText().length());
                                input.setHint(getResources().getString(R.string.contraseña)); //Para indicar al usuario que debe escribir su contraseña
                                buildersecundario.setView(input);
                                buildersecundario.setPositiveButton(getResources().getString(R.string.eliminar_mayus), null);
                                buildersecundario.setNegativeButton(getResources().getString(R.string.cancelar_mayus), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //Se cancela el proceso
                                        dialog.cancel();
                                    }
                                });
                                //Mostramos la ventana de ingresar contraseña

                                final AlertDialog alertsecundario = buildersecundario.create();
                                alertsecundario.setOnShowListener( new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface arg0) {
                                        alertsecundario.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED); //Hacemos el boton de eliminar de color rojo

                                        alertsecundario.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                contraseñaStr = input.getText().toString(); //Contraseña escrita por el usuario
                                                //Validar si la contraseña escrita es correcta
                                                detectorDeErroresPassword = BD.ValidarContraseña(contraseñaStr, detectorDeErroresPassword, input, getResources().getString(R.string.contraseña_requerida));
                                                if (detectorDeErroresPassword == 0) {
                                                    Progress.setMessage(getResources().getString(R.string.eliminando_espere));
                                                    Progress.show();
                                                    RevisarMatchPassowrd(); //Metodo encargado de ver si lo que se escribio coincide
                                                }
                                            }
                                        });


                                    }
                                });
                                alertsecundario.show();

                            }
                        });
                //Boton no
                builder.setNegativeButton(
                        "NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                final AlertDialog alertPrincipal = builder.create();
                alertPrincipal.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        alertPrincipal.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED); //Hacemos el boton de eliminar de color rojo
                    }
                });
                //Mostramos el la ventana que nos pregunta si queremos eliminar la cuenta
                alertPrincipal.show();
            }
        });

        //Cerrar sesion
        cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UsuarioActivity.this);
                builder.setPositiveButton(
                        getResources().getString(R.string.si_mayus),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent cerrarMainMenu = new Intent(MainMenu.FINISH_ALERT); //Vinculamos la variable de IngresarActivity acá
                                UsuarioActivity.this.sendBroadcast(cerrarMainMenu); //Llamamos al Broadcast para que lo cierre
                                Database.child(VariablesEstaticas.CurrentUserUID).child("CuentaAbierta").setValue("false"); //Cuenta ahora está cerrada
                                VE.GuardarDatos(sharedPreferences, false, ""); //Guardamos los cambios, la variable islogged y el uid ahora tienen valores default
                                Intent ingresar = new Intent(UsuarioActivity.this, IngresarActivity.class);
                                startActivity(ingresar);
                                finish(); //Terminamos
                            }
                        });
                builder.setNegativeButton(
                                "NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }

                        });
                //Centrar Titulo
                TextView title = new TextView(UsuarioActivity.this);
                title.setText(getResources().getString(R.string.deseas_cerrar_sesion));
                //title.setBackgroundColor(Color.BLACK);
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                //title.setTextColor(Color.WHITE);
                title.setTextColor(Color.BLACK);
                title.setTextSize(20);
                builder.setCustomTitle(title);

                AlertDialog dialog = builder.create();
                dialog.show();
                //centrar los botones
                Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
                layoutParams.weight = 10;
                btnPositive.setLayoutParams(layoutParams);
                btnNegative.setLayoutParams(layoutParams);


            }
        });

    }


    public void RevisarMatchPassowrd(){
        Database.child(VariablesEstaticas.CurrentUserUID).child("Contraseña").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ContraseñaReal = dataSnapshot.getValue().toString(); //Conseguimos el valor real de la contraseña del usuario
                if(!contraseñaStr.equals(ContraseñaReal)){
                    input.setError(getResources().getString(R.string.la_contraseña_no_es_correcta));
                    input.requestFocus();
                }else{
                    Intent cerrarMainMenu = new Intent(MainMenu.FINISH_ALERT); //Vinculamos la variable de IngresarActivity acá
                    UsuarioActivity.this.sendBroadcast(cerrarMainMenu); //Llamamos al Broadcast para que lo cierre
                    Database.child(VariablesEstaticas.CurrentUserUID).removeValue(); //Eliminamos de la database
                    MU.MostrarToast(UsuarioActivity.this, getResources().getString(R.string.la_cuenta_fue_eliminada));
                    VE.GuardarDatos(sharedPreferences, false, ""); //Guardamos los cambios, la variable islogged y el uid ahora tienen valores
                    Intent ingresar = new Intent(UsuarioActivity.this, IngresarActivity.class);
                    startActivity(ingresar);
                    finish(); //Terminamos
                }
                Progress.dismiss();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    protected  void onStart(){
        BD.MostrarElementoUsuarioActual(Database, "Usuario", UsuarioTextView, sharedPreferences); //Escribir el nombre del usuario en el textview
        super.onStart();
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

    private void setAppLocale(String localeCode) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            conf.locale = new Locale(localeCode.toLowerCase());
        }
        res.updateConfiguration(conf, dm);
    }
}