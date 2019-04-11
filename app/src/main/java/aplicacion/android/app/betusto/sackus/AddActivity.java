package aplicacion.android.app.betusto.sackus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class AddActivity extends AppCompatActivity {

    private ImageButton wifibutton;
    private ImageButton añadirbutton;
    private  TextView mostrartotaltext;
    private EditText dineroedit;

    private DatabaseReference Database;
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();
    BaseDeDatos BD = new BaseDeDatos();
    MetodosUtiles MU = new MetodosUtiles();



    private static DecimalFormat df2 = new DecimalFormat("0.00"); //Para usar solo dos decimales

    MetodosUtiles MandarToast = new MetodosUtiles();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        wifibutton = findViewById(R.id.activity_add_nowifibutton);
        añadirbutton = findViewById(R.id.activity_add_agregar_button);
        mostrartotaltext = findViewById(R.id.activity_add_efectivo_total_text);
        dineroedit = findViewById(R.id.activity_add_monto_edittext);

        //Persistencia de datos y referencia
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences= getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);


        final DetectaConexion CD = new DetectaConexion(this);
        CD.startConexion(wifibutton);

        //Constante simbolo de peso
        MU.EstiloDinero(dineroedit);

        //Agregar monto
        añadirbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database.child(VariablesEstaticas.CurrentUserUID).child("EfectivoTotal").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dineroedit.getText().toString().equals("$") && !dineroedit.getText().toString().equals("$.") && !dineroedit.getText().toString().isEmpty()) { //Si no dejo el campo de texto vacio
                            double efectivoTotal = Double.parseDouble(dataSnapshot.getValue().toString().replace("$", "")); //Conseguimos la cantidad actual y la convertimos a int
                            double dineroeditDouble = Double.parseDouble(dineroedit.getText().toString().replace("$", "")); //Conseguimos el monto que se escribio
                            if (dineroeditDouble != 0.00 && dineroeditDouble != Double.NaN) { //no valores nulos
                                if (dineroeditDouble + efectivoTotal <= VariablesEstaticas.MAXIMO) {
                                    //Actualizamos la cantidad de dinero
                                    Database.child(VariablesEstaticas.CurrentUserUID).child("EfectivoTotal").setValue("$" + df2.format(dineroeditDouble + efectivoTotal));
                                    dineroedit.getText().clear(); //limpiamos caja de texto
                                    MandarToast.MostrarToast(AddActivity.this, getResources().getString(R.string.efectivo_se_actualizo));
                                } else {
                                    MandarToast.MostrarToast(AddActivity.this, "No se puede agregar tanto efectivo");
                                    dineroedit.getText().clear(); //limpiamos caja de texto
                                }
                            } else {
                                dineroedit.setError(getResources().getString(R.string.se_requiere_monto_valido));
                                dineroedit.requestFocus();
                            }
                        }else{
                            dineroedit.setError(getResources().getString(R.string.se_requiere_monto_valido));
                            dineroedit.requestFocus();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

    }

    //Al iniciar el activity se ejecutara lo siguiente
    @Override
    protected void onStart() {
        super.onStart();
        //Mostrar el efectivo
        BD.MostrarElementosUsuarioActualConSlash(Database, "EfectivoTotal", getResources().getString(R.string.efectivo_total_2), mostrartotaltext, sharedPreferences);
    }


    //Revisar conexion internet
    @Override
    protected void onResume() {
        //vuelve visible o invisible el boton
        DetectaConexion CD = new DetectaConexion(this);
        CD.ConexionPorSegundos(wifibutton);
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
