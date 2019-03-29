package aplicacion.android.app.betusto.sackus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static aplicacion.android.app.betusto.sackus.VariablesEstaticas.SHARED_PREFS;

public class MainMenu extends AppCompatActivity {
    //Los OnDataChange se efectuan solo al final del codigo


    private Button SackButton, GastoButton, ViajeButton, NotasButton;
    private ImageButton OpcionesButton;
    private TextView usuarioBienvenidoText;
    private DatabaseReference Database;
    private ImageButton nowifibutton;
    //Variable que nos permite vincular con el metodo que se encuentra en MetodosUtiles de VariablesEstaticas para guardar o cargar data
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();
    public static final String FINISH_ALERT = "finish_alert"; //Variable para terminar este activity desde otro


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        this.registerReceiver(this.finishAlert, new IntentFilter(FINISH_ALERT)); //Linea de codigo necesaria para que funcione cerrar este activity desde otro
        SackButton = (Button) findViewById(R.id.sackButton);
        GastoButton = (Button) findViewById(R.id.gastoButton);
        ViajeButton = (Button) findViewById(R.id.viajeButton);
        NotasButton = (Button) findViewById(R.id.notasButton);
        usuarioBienvenidoText = (TextView) findViewById(R.id.usuarioTextView);
        OpcionesButton = (ImageButton) findViewById(R.id.activity_main_menu_opcionesButton);
        nowifibutton = findViewById(R.id.activity_main_menu_nowifibutton);

        final DetectaConexion CD = new DetectaConexion(this);
        CD.startConexion(nowifibutton);

        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        sharedPreferences = getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences); //Escencial para que no valgan nulos los valores de isLogged y UID

        nowifibutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CD.mensajeNoInternet(MainMenu.this);
            }
        });

        OpcionesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent opciones = new Intent(MainMenu.this, UsuarioActivity.class);
                startActivity(opciones);
            }
        });
        //Para conseguir el uid del IngresarActivity

        /*SackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent yatemgocuenta = new Intent(MainMenu.this, IngresarActivity.class);
                startActivity(yatemgocuenta);
            }
        });*/


        //BOTONES PRINCIPALES
        //Ir al activity de añadir efectivo
        SackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(MainMenu.this,     AddActivity.class);
                startActivity(add);
            }
        });

        //Ir al fragment de ver y crear viajes
        ViajeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viaje = new Intent(MainMenu.this,     ViajesActivity.class);
                startActivity(viaje);
            }
        });

        //Ir al fragment de escribir y ver notas
        NotasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notas = new Intent(MainMenu.this,     NotasActivity.class);
                startActivity(notas);
            }
        });

        //Ir al fragment de gasto e historial
        GastoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gasto = new Intent(MainMenu.this,     GastoActivity.class);
                startActivity(gasto);
            }
        });
        }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    //BroadcastReceiver encargado de cerrar este activity al ser llamado desde otro
    BroadcastReceiver finishAlert = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            MainMenu.this.finish();
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
        Intent mainmenu = getIntent();
        Bundle b = mainmenu.getExtras();
            if(b!=null){
                VariablesEstaticas.CurrentUserUID =(String) b.get("El UID");
            }else{
                Log.e("TEST", "el bundle esta vacio o no lo detecto");
            }
        Saludos();
    }


    public void Saludos(){
            Database.child(VariablesEstaticas.CurrentUserUID).child("Usuario").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //if necesario por si ya no obtiene el valor del uid por alguna razon, de esta manera no crasheara
                    //Se necesitan volver a cargar los datos porque cada cambio en la base de datos activa este ondatachange
                    VE.CargarDatos(sharedPreferences);
                    if(VariablesEstaticas.CurrentUserUID != null && !VariablesEstaticas.CurrentUserUID.equals("")) {
                    Log.e("TEST", "VALOR: "+VariablesEstaticas.CurrentUserUID);
                    String bienvenida = dataSnapshot.getValue().toString();
                    usuarioBienvenidoText.setText("Saludos\n" + bienvenida);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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

    @Override
    protected void  onStop(){
        super.onStop();
    }
}
