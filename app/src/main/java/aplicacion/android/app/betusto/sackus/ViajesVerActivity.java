package aplicacion.android.app.betusto.sackus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViajesVerActivity extends AppCompatActivity {
    //Textviews que tomarán datos de la base de datos para mostrar
    private TextView efectivototalText, mostrarNombreViajeText, mostrarFechaInicioText, mostrarFechaRegresoText, mostrarDineroQueLlevasText,
    mostrarDineroHotelText, mostrarDineroTransporteText, mostrarDineroComidaText, mostrarDineroBaratijasText, mostrarAcompañante1Text,
    mostrarAcompañante2Text, mostrarAcompañante3Text, mostrarAcompañante4Text, mostrarAcompañante5Text;
    //Textviews que sirven para indicar lo que se muestra
    private TextView NombreViajeText, FechaInicioText, FechaRegresoText, DineroQueLlevasText,
            DineroHotelText, DineroTransporteText, DineroComidaText, DineroBaratijasText, AcompañantesText;
    //Imageviews que indican si se acepto o no la invitacion
    private ImageView mostrarEstado1Image, mostrarEstado2Image, mostrarEstado3Image, mostrarEstado4Image, mostrarEstado5Image;
    //Lineas negras
    private LinearLayout lineaEfectivoTotal, lineaNombreViaje, lineaFechaInicio, lineaFechaRegreso, lineaDineroTotal, lineaDineroHotel,
    lineaDineroTransporte, lineaDineroComida, lineaDineroBaratijas, lineaAcompañantes;
    //Botones
    private Button listoButton, editarButton;
    private DatabaseReference Database;
    private ImageButton nowifibutton;
    //Variable que nos permite vincular con el metodo que se encuentra en MetodosUtiles de VariablesEstaticas para guardar o cargar data
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();
    BaseDeDatos BD = new BaseDeDatos();
    private String TimeStampViaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes_ver);
        nowifibutton = findViewById(R.id.activity_viajes_ver_nowifibutton);
        efectivototalText = findViewById(R.id.activity_viajes_ver_efectivo_total_text);
        mostrarNombreViajeText = findViewById(R.id.activity_viajes_ver_mostrar_nombreviajetext);
        mostrarFechaInicioText = findViewById(R.id.activity_viajes_ver_mostrar_fechainiciotext);
        mostrarFechaRegresoText = findViewById(R.id.activity_viajes_ver_mostrar_fecharegresotext);
        mostrarDineroQueLlevasText = findViewById(R.id.activity_viajes_ver_mostrar_dinerototaltext);
        mostrarDineroHotelText = findViewById(R.id.activity_viajes_ver_mostrar_dinerohoteltext);
        mostrarDineroTransporteText = findViewById(R.id.activity_viajes_ver_mostrar_dinerotransportetext);
        mostrarDineroComidaText = findViewById(R.id.activity_viajes_ver_mostrar_dinerocomidatext);
        mostrarDineroBaratijasText = findViewById(R.id.activity_viajes_ver_mostrar_dinerobaratijastext);
        mostrarAcompañante1Text = findViewById(R.id.activity_viajes_ver_mostrar_acompañante1text);
        mostrarAcompañante2Text = findViewById(R.id.activity_viajes_ver_mostrar_acompañante2text);
        mostrarAcompañante3Text = findViewById(R.id.activity_viajes_ver_mostrar_acompañante3text);
        mostrarAcompañante4Text = findViewById(R.id.activity_viajes_ver_mostrar_acompañante4text);
        mostrarAcompañante5Text = findViewById(R.id.activity_viajes_ver_mostrar_acompañante5text);
        NombreViajeText = findViewById(R.id.activity_viajes_ver_nombreviajetext);
        FechaInicioText = findViewById(R.id.activity_viajes_ver_fechainiciotext);
        FechaRegresoText = findViewById(R.id.activity_viajes_ver_fecharegresotext);
        DineroQueLlevasText = findViewById(R.id.activity_viajes_ver_dinerototaltext);
        DineroHotelText = findViewById(R.id.activity_viajes_ver_dinerohoteltext);
        DineroTransporteText = findViewById(R.id.activity_viajes_ver_dinerotransportetext);
        DineroComidaText = findViewById(R.id.activity_viajes_ver_dinerocomidatext);
        DineroBaratijasText = findViewById(R.id.activity_viajes_ver_dinerobaratijastext);
        AcompañantesText = findViewById(R.id.activity_viajes_ver_acompañantestext);
        mostrarEstado1Image = findViewById(R.id.activity_viajes_ver_mostrar_estadoImage1);
        mostrarEstado2Image = findViewById(R.id.activity_viajes_ver_mostrar_estadoImage2);
        mostrarEstado3Image = findViewById(R.id.activity_viajes_ver_mostrar_estadoImage3);
        mostrarEstado4Image = findViewById(R.id.activity_viajes_ver_mostrar_estadoImage4);
        mostrarEstado5Image = findViewById(R.id.activity_viajes_ver_mostrar_estadoImage5);
        lineaEfectivoTotal = findViewById(R.id.activity_viajes_ver_lineaEfectivoTotal);
        lineaNombreViaje = findViewById(R.id.activity_viajes_ver_lineaNombreViaje);
        lineaFechaInicio = findViewById(R.id.activity_viajes_ver_lineaFechaInicio);
        lineaFechaRegreso = findViewById(R.id.activity_viajes_ver_lineaFechaRegreso);
        lineaDineroTotal = findViewById(R.id.activity_viajes_ver_lineaDineroTotal);
        lineaDineroHotel = findViewById(R.id.activity_viajes_ver_lineaDineroHotel);
        lineaDineroTransporte = findViewById(R.id.activity_viajes_ver_lineaDineroTransporte);
        lineaDineroComida = findViewById(R.id.activity_viajes_ver_lineaDineroComida);
        lineaDineroBaratijas = findViewById(R.id.activity_viajes_ver_lineaDineroBaratijas);
        lineaAcompañantes = findViewById(R.id.activity_viajes_ver_lineaAcompañantes);
        listoButton = findViewById(R.id.activity_viajes_ver_listobutton);
        editarButton = findViewById(R.id.activity_viajes_ver_editarbutton);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();

        if (b != null) {
            TimeStampViaje = (String) b.get("TIMESTAMP");
        }

        final DetectaConexion CD = new DetectaConexion(this);
        CD.startConexion(nowifibutton);

        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        sharedPreferences = getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences); //Escencial para que no valgan nulos los valores de isLogged y UID

        nowifibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CD.mensajeNoInternet(ViajesVerActivity.this);
            }
        });

        listoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent viajestab2 = new Intent(ViajesVerActivity.this,ViajesActivity.class);
                viajestab2.putExtra("INTENT",1);
                ViajesVerActivity.this.overridePendingTransition(0, 0);
                ViajesVerActivity.this.startActivity(viajestab2);
                ViajesVerActivity.this.overridePendingTransition(0, 0);
            }
        });

        editarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TimeStampViaje != null){
                    finish();
                Intent viajeseditar = new Intent(ViajesVerActivity.this,ViajesEditarActivity.class);
                viajeseditar.putExtra("TIMESTAMP", TimeStampViaje);
                startActivity(viajeseditar);            }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent viajestab2 = new Intent(ViajesVerActivity.this,ViajesActivity.class);
        viajestab2.putExtra("INTENT",1);
        ViajesVerActivity.this.overridePendingTransition(0, 0);
        ViajesVerActivity.this.startActivity(viajestab2);
        ViajesVerActivity.this.overridePendingTransition(0, 0);
        super.onBackPressed();
    }

    @Override
    public void onStart(){
        super.onStart();
        //Llamamos a los metodos que colocaran los textos desde la base de datos
        BD.MostrarElementoUsuarioActualConComplemento(Database, "EfectivoTotal", "Efectivo Total: ", efectivototalText, sharedPreferences);
        MostrarElementoViajes("Viaje",mostrarNombreViajeText, NombreViajeText, lineaNombreViaje);
        MostrarElementoViajes("Compartir",null, null, null);
        MostrarElementoViajes("DineroDedicado",mostrarDineroQueLlevasText, null, null);
        MostrarElementoViajes("FechaInicio",mostrarFechaInicioText, FechaInicioText, lineaFechaInicio);
        MostrarElementoViajes("FechaRegreso",mostrarFechaRegresoText, FechaRegresoText, lineaFechaRegreso);
        MostrarElementoViajes("Invitados",null, null, null);
        MostrarElementoViajes("InvertirEnHotel",mostrarDineroHotelText, DineroHotelText, lineaDineroHotel);
        MostrarElementoViajes("InvertirEnTransporte",mostrarDineroTransporteText, DineroTransporteText, lineaDineroTransporte);
        MostrarElementoViajes("InvertirEnComida",mostrarDineroComidaText, DineroComidaText, lineaDineroComida);
        MostrarElementoViajes("InvertirEnBaratijas",mostrarDineroBaratijasText, DineroBaratijasText, lineaDineroBaratijas);
    }

    //Metodo para conseguir algun child de viajes
    public void MostrarElementoViajes(final String ElementoAConseguir, final TextView textView, final TextView textViewPadre, final LinearLayout linearLayout){
            Database.child(VariablesEstaticas.CurrentUserUID).child("Viajes").child(TimeStampViaje).child(ElementoAConseguir).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //if necesario por si ya no obtiene el valor del uid por alguna razon, de esta manera no crasheara
                    //Se necesitan volver a cargar los datos porque cada cambio en la base de datos activa este ondatachange
                    VE.CargarDatos(sharedPreferences);
                    if(VariablesEstaticas.CurrentUserUID != null && !VariablesEstaticas.CurrentUserUID.equals("")) {
                        String resultado = dataSnapshot.getValue().toString();
                        //Para saber si es necesario mostrar o no
                        //TODO: Al darclick en la carta o la palomita te diga que rollo
                        switch(ElementoAConseguir){
                            case "Viaje":
                                textView.setText(resultado);
                                break;
                            case "Compartir":
                                if(resultado.equals("false")){
                                    AcompañantesText.setVisibility(View.GONE);
                                    mostrarAcompañante1Text.setVisibility(View.GONE);
                                    mostrarAcompañante2Text.setVisibility(View.GONE);
                                    mostrarAcompañante3Text.setVisibility(View.GONE);
                                    mostrarAcompañante4Text.setVisibility(View.GONE);
                                    mostrarAcompañante5Text.setVisibility(View.GONE);
                                    mostrarEstado1Image.setVisibility(View.GONE);
                                    mostrarEstado2Image.setVisibility(View.GONE);
                                    mostrarEstado3Image.setVisibility(View.GONE);
                                    mostrarEstado4Image.setVisibility(View.GONE);
                                    mostrarEstado5Image.setVisibility(View.GONE);
                                    lineaAcompañantes.setVisibility(View.GONE);
                                }
                                break;
                            case "DineroDedicado":
                                textView.setText(resultado.substring(17));
                                break;
                            case "FechaInicio":
                                if(!resultado.substring(17).equals("-")){
                                    textView.setText(resultado.substring(17));
                                }else{
                                    linearLayout.setVisibility(View.GONE);
                                    textView.setVisibility(View.GONE);
                                    textViewPadre.setVisibility(View.GONE);
                                }
                                break;
                            case "FechaRegreso":
                                if(!resultado.substring(18).equals("-")){
                                    textView.setText(resultado.substring(18));
                                }else{
                                    linearLayout.setVisibility(View.GONE);
                                    textView.setVisibility(View.GONE);
                                    textViewPadre.setVisibility(View.GONE);
                                }
                                break;
                            case "Invitados":
                                Database.child(VariablesEstaticas.CurrentUserUID).child("Viajes").child(TimeStampViaje).child("Invitados").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) //Recorremos cada campo de la tabla Usuarios
                                        {
                                            switch (snapshot.getKey()){
                                                case "Invitado1":
                                                    if(snapshot.child("Usuario").getValue().toString().equals("-")){
                                                        mostrarAcompañante1Text.setVisibility(View.GONE);
                                                        mostrarEstado1Image.setVisibility(View.GONE);
                                                    }else{
                                                        mostrarAcompañante1Text.setText(snapshot.child("Usuario").getValue().toString());
                                                        if(snapshot.child("SolicitudAceptada").getValue().toString().equals("true")){
                                                            mostrarEstado1Image.setImageResource(R.mipmap.ic_check);
                                                        }
                                                    }
                                                    break;
                                                case "Invitado2":
                                                    if(snapshot.child("Usuario").getValue().toString().equals("-")){
                                                        mostrarAcompañante2Text.setVisibility(View.GONE);
                                                        mostrarEstado2Image.setVisibility(View.GONE);
                                                    }else{
                                                        mostrarAcompañante2Text.setText(snapshot.child("Usuario").getValue().toString());
                                                        if(snapshot.child("SolicitudAceptada").getValue().toString().equals("true")){
                                                            mostrarEstado2Image.setImageResource(R.mipmap.ic_check);
                                                        }
                                                    }
                                                    break;
                                                case "Invitado3":
                                                    if(snapshot.child("Usuario").getValue().toString().equals("-")){
                                                        mostrarAcompañante3Text.setVisibility(View.GONE);
                                                        mostrarEstado3Image.setVisibility(View.GONE);
                                                    }else{
                                                        mostrarAcompañante3Text.setText(snapshot.child("Usuario").getValue().toString());
                                                        if(snapshot.child("SolicitudAceptada").getValue().toString().equals("true")){
                                                            mostrarEstado3Image.setImageResource(R.mipmap.ic_check);
                                                        }
                                                    }
                                                    break;
                                                case "Invitado4":
                                                    if(snapshot.child("Usuario").getValue().toString().equals("-")){
                                                        mostrarAcompañante4Text.setVisibility(View.GONE);
                                                        mostrarEstado4Image.setVisibility(View.GONE);
                                                    }else{
                                                        mostrarAcompañante4Text.setText(snapshot.child("Usuario").getValue().toString());
                                                        if(snapshot.child("SolicitudAceptada").getValue().toString().equals("true")){
                                                            mostrarEstado4Image.setImageResource(R.mipmap.ic_check);
                                                        }
                                                    }
                                                    break;
                                                case "Invitado5":
                                                    if(snapshot.child("Usuario").getValue().toString().equals("-")){
                                                        mostrarAcompañante5Text.setVisibility(View.GONE);
                                                        mostrarEstado5Image.setVisibility(View.GONE);
                                                    }else{
                                                        mostrarAcompañante5Text.setText(snapshot.child("Usuario").getValue().toString());
                                                        if(snapshot.child("SolicitudAceptada").getValue().toString().equals("true")){
                                                            mostrarEstado5Image.setImageResource(R.mipmap.ic_check);
                                                        }
                                                    }
                                                    break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                break;
                           default:
                               //Para las inversiones
                               if(resultado.equals("$")){
                                   linearLayout.setVisibility(View.GONE);
                                   textView.setVisibility(View.GONE);
                                   textViewPadre.setVisibility(View.GONE);
                               }else {
                                   textView.setText(resultado);
                               }
                               break;
                        }
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
}