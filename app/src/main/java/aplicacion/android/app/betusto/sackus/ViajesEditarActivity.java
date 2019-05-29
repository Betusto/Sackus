package aplicacion.android.app.betusto.sackus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.DecimalFormat;

public class ViajesEditarActivity extends AppCompatActivity {
    //Textviews que tomarán datos de la base de datos para mostrar
    private EditText mostrarNombreViajeEdit, mostrarFechaInicioEdit, mostrarFechaRegresoEdit, mostrarDineroQueLlevasEdit,
            mostrarDineroHotelEdit, mostrarDineroTransporteEdit, mostrarDineroComidaEdit, mostrarDineroBaratijasEdit, mostrarAcompañante1Edit,
            mostrarAcompañante2Edit, mostrarAcompañante3Edit, mostrarAcompañante4Edit, mostrarAcompañante5Edit;
    //Textviews que sirven para indicar lo que se muestra
    private TextView efectivototalText, NombreViajeText, FechaInicioText, FechaRegresoText, DineroQueLlevasText,
            DineroHotelText, DineroTransporteText, DineroComidaText, DineroBaratijasText, AcompañantesText;
    //Botones
    private Button listoButton, regresarButton;
    private DatabaseReference Database;
    private ImageButton nowifibutton;
    //Variable que nos permite vincular con el metodo que se encuentra en MetodosUtiles de VariablesEstaticas para guardar o cargar data
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();
    BaseDeDatos BD = new BaseDeDatos();
    private String TimeStampViaje;
    MetodosUtiles MU = new MetodosUtiles();
    //Strings originales
    private String nombreViajeOriginal, fechaInicioOriginal, fechaRegresoOriginal, dineroQueLlevasOriginal,
            dineroHotelOriginal, dineroTransporteOriginal, dineroComidaOriginal, dineroBaratijasOriginal;
    private static DecimalFormat df2 = new DecimalFormat("0.00"); //Para usar solo dos decimales
    private Double sumaTotal = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes_editar);
        nowifibutton = findViewById(R.id.activity_viajes_editar_nowifibutton);
        efectivototalText = findViewById(R.id.activity_viajes_editar_efectivo_total_text);
        mostrarNombreViajeEdit =findViewById(R.id.activity_viajes_editar_mostrar_nombreviajetext);
        mostrarFechaInicioEdit = findViewById(R.id.activity_viajes_editar_mostrar_fechainiciotext);
        mostrarFechaRegresoEdit = findViewById(R.id.activity_viajes_editar_mostrar_fecharegresotext);
        mostrarDineroQueLlevasEdit = findViewById(R.id.activity_viajes_editar_mostrar_dinerototaltext);
        mostrarDineroHotelEdit = findViewById(R.id.activity_viajes_editar_mostrar_dinerohoteltext);
        mostrarDineroTransporteEdit = findViewById(R.id.activity_viajes_editar_mostrar_dinerotransportetext);
        mostrarDineroComidaEdit = findViewById(R.id.activity_viajes_editar_mostrar_dinerocomidatext);
        mostrarDineroBaratijasEdit = findViewById(R.id.activity_viajes_editar_mostrar_dinerobaratijastext);
        NombreViajeText = findViewById(R.id.activity_viajes_editar_nombreviajetext);
        FechaInicioText =findViewById(R.id.activity_viajes_editar_fechainiciotext);
        FechaRegresoText = findViewById(R.id.activity_viajes_editar_fecharegresotext);
        DineroQueLlevasText = findViewById(R.id.activity_viajes_editar_dinerototaltext);
        DineroHotelText = findViewById(R.id.activity_viajes_editar_dinerohoteltext);
        DineroTransporteText =findViewById(R.id.activity_viajes_editar_dinerotransportetext);
        DineroComidaText = findViewById(R.id.activity_viajes_editar_dinerocomidatext);
        DineroBaratijasText = findViewById(R.id.activity_viajes_editar_dinerobaratijastext);
        listoButton = findViewById(R.id.activity_viajes_editar_listobutton);
        regresarButton = findViewById(R.id.activity_viajes_editar_regresarbutton);

        final DetectaConexion CD = new DetectaConexion(this);
        CD.startConexion(nowifibutton);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            TimeStampViaje = (String) b.get("TIMESTAMP");
        }

        //Eliminar los emojis
        mostrarNombreViajeEdit.setFilters(new InputFilter[]{MU.filters()});

        //Constante simbolo de peso
        MU.EstiloDinero(mostrarDineroQueLlevasEdit);
        MU.EstiloDinero(mostrarDineroHotelEdit);
        MU.EstiloDinero(mostrarDineroTransporteEdit);
        MU.EstiloDinero(mostrarDineroBaratijasEdit);
        MU.EstiloDinero(mostrarDineroComidaEdit);

        mostrarFechaInicioEdit.setFocusable(false);
        mostrarFechaRegresoEdit.setFocusable(false);


        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        sharedPreferences = getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences); //Escencial para que no valgan nulos los valores de isLogged y UID

        nowifibutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CD.mensajeNoInternet(ViajesEditarActivity.this);
            }
        });

        //TODO: RESOLVER PROBLEMA DE QUE SE PUEDE ESCRIBIR EN EL PRIMER CARACTER DE LOS EDITTEXT QUE TIENEN EL SIGNO DE PESO
        mostrarDineroQueLlevasEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TEST","ENTRO A EDITTEXT");
                if(mostrarDineroQueLlevasEdit.getSelectionStart() == 0)
                mostrarDineroQueLlevasEdit.setSelection(1);
            }
        }
        );

        mostrarFechaInicioEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MU.MostrarDatePickerEdit(ViajesEditarActivity.this, mostrarFechaInicioEdit, "");
            }
        }
        );

        mostrarFechaRegresoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MU.MostrarDatePickerEdit(ViajesEditarActivity.this, mostrarFechaRegresoEdit, "");
            }
        }
        );

        listoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectarErrores();
            }
        });

        regresarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //Llamamos a los metodos que colocaran los textos desde la base de datos
        BD.MostrarElementoUsuarioActualConComplemento(Database, "EfectivoTotal", "Efectivo Total: ", efectivototalText, sharedPreferences);
        MostrarElementoViajesEdit("Viaje",mostrarNombreViajeEdit);
        MostrarElementoViajesEdit("DineroDedicado",mostrarDineroQueLlevasEdit);
        MostrarElementoViajesEdit("FechaInicio",mostrarFechaInicioEdit);
        MostrarElementoViajesEdit("FechaRegreso",mostrarFechaRegresoEdit);
        MostrarElementoViajesEdit("InvertirEnHotel",mostrarDineroHotelEdit);
        MostrarElementoViajesEdit("InvertirEnTransporte",mostrarDineroTransporteEdit);
        MostrarElementoViajesEdit("InvertirEnComida",mostrarDineroComidaEdit);
        MostrarElementoViajesEdit("InvertirEnBaratijas",mostrarDineroBaratijasEdit);
    }

    public void AplicarCambios(String textoEscrito, String textoOriginal, String elemento){
        //Referencia para la BD de forma en que podamos meter una tabla dentro de ella
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        //Referencia a la tabla del child:
        DatabaseReference viajesDB = database.child(VariablesEstaticas.CurrentUserUID);
        if(!textoEscrito.equals(textoOriginal)){
            switch (elemento){
                case "Viaje":
                    viajesDB.child("Viajes").child(TimeStampViaje).child(elemento).setValue(textoEscrito);
                    break;
                case "DineroDedicado":
                    double double00 = Double.parseDouble(textoEscrito.replace("$",""));
                    textoEscrito = "$" + df2.format(double00);
                    //Guardamos los campos
                    textoEscrito = "Dinero dedicado: " + textoEscrito;
                    viajesDB.child("Viajes").child(TimeStampViaje).child(elemento).setValue(textoEscrito);
                    break;
                case "FechaInicio":
                    textoEscrito = "Fecha de inicio: " + textoEscrito;
                    viajesDB.child("Viajes").child(TimeStampViaje).child(elemento).setValue(textoEscrito);
                    break;
                case "FechaRegreso":
                    textoEscrito = "Fecha de regreso: " + textoEscrito;
                    viajesDB.child("Viajes").child(TimeStampViaje).child(elemento).setValue(textoEscrito);
                    break;
                default:
                    if(!textoEscrito.equals("$")){
                        double double01 = Double.parseDouble(textoEscrito.replace("$",""));
                        textoEscrito = "$" + df2.format(double01);
                    }
                    viajesDB.child("Viajes").child(TimeStampViaje).child(elemento).setValue(textoEscrito);
                    break;
            }
        }
    }

    //Metodo encargado de sumar las inversiones para poder validar luego si sobrepasan el limite indicado
    public void IrSumando(String suma){
        if(Double.parseDouble(suma.replace("$","")) != Double.NaN && Double.parseDouble(suma.replace("$",""))
                != 0.00){
            double sumaDouble = Double.parseDouble(suma.replace("$", "")); //Conseguimos el monto que se escribio
            if (sumaDouble != 0.00 && sumaDouble != Double.NaN) { //no valores nulos
                sumaTotal=sumaTotal+sumaDouble;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent viajesver = new Intent(ViajesEditarActivity.this,ViajesVerActivity.class);
        viajesver.putExtra("TIMESTAMP", TimeStampViaje);
        ViajesEditarActivity.this.overridePendingTransition(0, 0);
        ViajesEditarActivity.this.startActivity(viajesver);
        ViajesEditarActivity.this.overridePendingTransition(0, 0);
        super.onBackPressed();
    }

    public void detectarErrores(){
        sumaTotal = 0.00;
        int detectarErrores=0;
        String dondeStr = mostrarNombreViajeEdit.getText().toString().trim();
        String  dinerollevasStr = mostrarDineroQueLlevasEdit.getText().toString().trim();
        String invertirHotelStr = mostrarDineroHotelEdit.getText().toString().trim();
        String invertirTransporteStr = mostrarDineroTransporteEdit.getText().toString().trim();
        String invertirComidaStr = mostrarDineroComidaEdit.getText().toString().trim();
        String invertirBaratijasStr = mostrarDineroBaratijasEdit.getText().toString().trim();
        String fechaInicioStr = mostrarFechaInicioEdit.getText().toString().trim();
        String fechaRegresoStr = mostrarFechaRegresoEdit.getText().toString().trim();
        if(dondeStr.isEmpty()){
            mostrarNombreViajeEdit.setError("Se necesita nombre del lugar a visitar");
            mostrarNombreViajeEdit.requestFocus();
            detectarErrores = 1;
        }
        if(dinerollevasStr.equals("$") || dinerollevasStr.equals("$.") || dinerollevasStr.isEmpty()){
            mostrarDineroQueLlevasEdit.setError("Se necesita un monto de dinero");
            mostrarDineroQueLlevasEdit.requestFocus();
            detectarErrores = 1;
        }
        if(detectarErrores == 0){
            //Revisar si escribio algo en los campos no obligatorios
            if (invertirHotelStr.equals("$") || invertirHotelStr.equals("$.") || invertirHotelStr.isEmpty()) {
                invertirHotelStr = "$";
            } else {
                IrSumando(invertirHotelStr);
            }
            if (invertirTransporteStr.equals("$") || invertirTransporteStr.equals("$.") || invertirTransporteStr.isEmpty()) {
                invertirTransporteStr = "$";
            } else {
                IrSumando(invertirTransporteStr);
            }
            if (invertirComidaStr.equals("$") || invertirComidaStr.equals("$.") || invertirComidaStr.isEmpty()) {
                invertirComidaStr = "$";
            } else {
                IrSumando(invertirComidaStr);
            }
            if (invertirBaratijasStr.equals("$") || invertirBaratijasStr.equals("$.") || invertirBaratijasStr.isEmpty()) {
                invertirBaratijasStr = "$";
            } else {
                IrSumando(invertirBaratijasStr);
            }
            if (fechaInicioStr.isEmpty()) {
                fechaInicioStr = "-";
            }
            if (fechaRegresoStr.isEmpty()) {
                fechaRegresoStr = "-";
            }
            double dineroLlevasDoub = Double.parseDouble(dinerollevasStr.replace("$", "")); //Conseguimos el monto que se escribio
            if(sumaTotal>dineroLlevasDoub){
                MU.MostrarToast(ViajesEditarActivity.this, "La suma de los montos a invertir sobrepasan al dinero total que llevas");
            }else{
                AplicarCambios(dondeStr, nombreViajeOriginal, "Viaje");
                AplicarCambios(dinerollevasStr, dineroQueLlevasOriginal, "DineroDedicado");
                AplicarCambios(invertirHotelStr, dineroHotelOriginal, "InvertirEnHotel");
                AplicarCambios(invertirTransporteStr, dineroTransporteOriginal, "InvertirEnTransporte");
                AplicarCambios(invertirComidaStr, dineroComidaOriginal, "InvertirEnComida");
                AplicarCambios(invertirBaratijasStr, dineroBaratijasOriginal, "InvertirEnBaratijas");
                AplicarCambios(fechaInicioStr, fechaInicioOriginal, "FechaInicio");
                AplicarCambios(fechaRegresoStr, fechaRegresoOriginal, "FechaRegreso");
                MU.MostrarToast(ViajesEditarActivity.this, "Viaje actualizado");
                sumaTotal=0.00;
                finish();
                Intent viajesver = new Intent(ViajesEditarActivity.this,ViajesVerActivity.class);
                viajesver.putExtra("TIMESTAMP", TimeStampViaje);
                ViajesEditarActivity.this.overridePendingTransition(0, 0);
                ViajesEditarActivity.this.startActivity(viajesver);
                ViajesEditarActivity.this.overridePendingTransition(0, 0);
                //TODO: Algunas animaciones quitadas son inecesarias, posibilidad de elimnar notas, viajes y mensajes(?
            }
        }
    }

    public void MostrarElementoViajesEdit(final String ElementoAConseguir, final EditText editText){
        Database.child(VariablesEstaticas.CurrentUserUID).child("Viajes").child(TimeStampViaje).child(ElementoAConseguir).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if necesario por si ya no obtiene el valor del uid por alguna razon, de esta manera no crasheara
                //Se necesitan volver a cargar los datos porque cada cambio en la base de datos activa este ondatachange
                VE.CargarDatos(sharedPreferences);
                if(VariablesEstaticas.CurrentUserUID != null && !VariablesEstaticas.CurrentUserUID.equals("")) {
                    String resultado = dataSnapshot.getValue().toString();
                    //Para saber si es necesario mostrar o no
                    switch(ElementoAConseguir){
                        case "Viaje":
                            editText.setText(resultado);
                             nombreViajeOriginal = resultado;
                            break;
                        /*case "Compartir":
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
                            break;*/
                        case "DineroDedicado":
                            editText.setText(resultado.substring(17));
                            dineroQueLlevasOriginal = resultado;
                            break;
                        case "FechaInicio":
                            if(!resultado.substring(17).equals("-")){
                                editText.setText(resultado.substring(17));
                            }else{
                                editText.setText("");
                            }
                            fechaInicioOriginal = resultado;
                            break;
                        case "FechaRegreso":
                            if(!resultado.substring(18).equals("-")){
                                editText.setText(resultado.substring(18));
                            }else{
                                editText.setText("");
                            }
                            fechaRegresoOriginal = resultado;
                            break;
                        default:
                            //Para las inversiones
                            if(resultado.equals("$")){
                                editText.setText("");
                            }else {
                                editText.setText(resultado);
                            }
                            switch(ElementoAConseguir){
                                case "InvertirEnHotel":
                                    dineroHotelOriginal = resultado;
                                    break;
                                case "InvertirEnTransporte":
                                    dineroTransporteOriginal = resultado;
                                    break;
                                case "InvertirEnComida":
                                    dineroComidaOriginal = resultado;
                                    break;
                                case "InvertirEnBaratijas":
                                    dineroBaratijasOriginal = resultado;
                                    break;
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
