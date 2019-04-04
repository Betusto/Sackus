package aplicacion.android.app.betusto.sackus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GastoTab1Gasto extends Fragment {
    private ProgressDialog Progress;
    private TextView efectivototalText;
    private FloatingActionButton añadirButton;
    private EditText gastasteEdit;
    private EditText precioEdit;
    MetodosUtiles MU = new MetodosUtiles();
    BaseDeDatos BD = new BaseDeDatos();
    private DatabaseReference Database;
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();
    private int detectorDeErroresGastaste = 0, detectorDeErroresPrecio = 0;
    private String gastasteStr, precioStr;

    private static GastoTab1Gasto instance = null;



    private static DecimalFormat df2 = new DecimalFormat("0.00"); //Para usar solo dos decimales

    MetodosUtiles MandarToast = new MetodosUtiles();
    GastoTab2Historial ElementoDelOtroTab = new GastoTab2Historial();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gasto_tab1_gasto, container, false);
        instance = GastoTab1Gasto.this;
        añadirButton = rootView.findViewById(R.id.fab1);
        efectivototalText =  rootView.findViewById(R.id.activity_gasto_tab1_efectivo_total_text);
        gastasteEdit = rootView.findViewById(R.id.activity_gasto_tab1_gastaste_edittext);
        precioEdit = rootView.findViewById(R.id.activity_gasto_tab1_precio_edittext);
        Progress = new ProgressDialog(getActivity());



        //Eliminar los emojis
        MetodosUtiles EliminaEmoijis = new MetodosUtiles();
        gastasteEdit.setFilters(new InputFilter[]{EliminaEmoijis.filters()});

        //No se puedan cancelar los progress
        Progress.setCancelable(false);
        Progress.setCanceledOnTouchOutside(false);

        //Persistencia de datos y referencia
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences= this.getActivity().getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);

        //Limitar a 2 las lineas que el usuario puede escribir
        MU.LimitarLineasEditText(gastasteEdit, 2);
        //Constante simbolo de peso
        MU.EstiloDinero(precioEdit);

        //cursor siempre al final
        gastasteEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gastasteEdit.setSelection(gastasteEdit.getText().length());
            }
        });


        //Restar al monto
        añadirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestarMontoAñadirHistorial();
            }
        });

        return rootView;
    }


    //Al iniciar el activity se ejecutara lo siguiente
    @Override
    public void onStart() {
        super.onStart();
        //Mostrar el efectivo
        BD.MostrarElementosUsuarioActualConSlash(Database, "EfectivoTotal", "Efectivo Total:", efectivototalText, sharedPreferences);
    }

    public void RestarMontoAñadirHistorial(){
        gastasteStr = gastasteEdit.getText().toString().trim();
        precioStr =   precioEdit.getText().toString().trim();
        //Verificamos que el usuario no haya dejado campos sin escribir
        Validar();
        if (detectorDeErroresGastaste == 0 && detectorDeErroresPrecio == 0) {
            Progress.setMessage("Añadiendo, por favor espere");
            Progress.show();
            Calcular(); //Se encarga de calcular y guardar el historial en la bd
        }
    }
    public void Calcular(){
                Database.child(VariablesEstaticas.CurrentUserUID).child("EfectivoTotal").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        //Ocultar keyboard, debe esperar un segundo y medio para que lo cierre bien
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(precioEdit.getWindowToken(), 0);
                        imm.hideSoftInputFromWindow(gastasteEdit.getWindowToken(), 0);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                        if (Double.parseDouble(dataSnapshot.getValue().toString().replace("$", "")) != Double.NaN && Double.parseDouble(dataSnapshot.getValue().toString().replace("$", "")) != 0.00) {
                        double efectivoTotal = Double.parseDouble(dataSnapshot.getValue().toString().replace("$", "")); //Conseguimos la cantidad actual y la convertimos a int
                        double dineroeditDouble = Double.parseDouble(precioEdit.getText().toString().replace("$", "")); //Conseguimos el monto que se escribio
                        if (dineroeditDouble != 0.00 && dineroeditDouble != Double.NaN) { //no valores nulos
                            if (efectivoTotal - dineroeditDouble >= 0) {
                                //Actualizamos la cantidad de dinero
                                Database.child(VariablesEstaticas.CurrentUserUID).child("EfectivoTotal").setValue("$" + df2.format(efectivoTotal - dineroeditDouble));
                                gastasteEdit.getText().clear();
                                precioEdit.getText().clear(); //limpiamos caja de texto
                                MandarToast.MostrarToast(getActivity(), "El efectivo total se actualizó exitosamente");
                                //VINCULO
                                BD.AlAñadirNuevaNota(gastasteStr,precioStr);
                                Intent reebot = new Intent(getActivity(), GastoActivity.class);
                                //Engañar al usuario, no pude encontrar una mejor solucion, el recycler no se actualiza
                                //hasta que se cambie de activity
                                getActivity().finish();
                                //Quitar animacione
                                getActivity().overridePendingTransition(0, 0);
                                getActivity().startActivity(reebot);
                                getActivity().overridePendingTransition(0, 0);
                            } else {
                                MandarToast.MostrarToast(getActivity(), "No se puede agregar el gasto por falta de dinero");
                            }
                        } else {
                            precioEdit.setError("Se requiere un monto valido");
                            precioEdit.requestFocus();
                        }
                    }else{
                            MandarToast.MostrarToast(getActivity(), "Se necesita efectivo para añadir nuevo gasto");
                        }
                        Progress.dismiss();
                            }
                        }, 1500);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

    }


    public void Validar(){
        if(precioStr.equals("$") || precioStr.equals("$.") || precioStr.isEmpty()) { //Si no dejo el campo de texto vacio
            precioEdit.setError("Se requiere un monto valido");
            precioEdit.requestFocus();
            detectorDeErroresPrecio++;
        }else{
            detectorDeErroresPrecio = 0;
        }
        if(gastasteStr.isEmpty()){
            gastasteEdit.setError("Se requiere nombre del gasto");
            gastasteEdit.requestFocus();
            detectorDeErroresGastaste++;
        }else{
            detectorDeErroresGastaste = 0;
        }
    }





    public void onResume(){
        super.onResume();
    }
}
