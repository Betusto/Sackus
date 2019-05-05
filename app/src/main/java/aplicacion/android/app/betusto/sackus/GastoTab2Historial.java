package aplicacion.android.app.betusto.sackus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Helper;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.text.InputFilter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class GastoTab2Historial extends Fragment implements ListListener{
    public android.support.v7.widget.RecyclerView recyclerView;
    public ArrayList<Gastos> notas = new ArrayList<>();
    private Adaptador adaptador;
    public View rootView;
    MetodosUtiles MU = new MetodosUtiles();
    BaseDeDatos BD = new BaseDeDatos();
    private DatabaseReference Database;
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();
    //MetodosUtiles MandarToast = new MetodosUtiles();
    private Activity mActivity;
    private String gastasteStr, precioStr;
    MetodosUtiles MandarToast = new MetodosUtiles();
    private static DecimalFormat df2 = new DecimalFormat("0.00"); //Para usar solo dos decimales
    private ProgressDialog Progress;
    public static String gastasteStrOld, precioStrOld;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.gasto_tab2_historial, container, false);

     /*   //Aumentamos el margen top del layout para tener espacio para el icono de wifi
        RelativeLayout layout = rootView.findViewById(R.id.porfa);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeParams.setMargins(0, 205, 0, 0);
        layout.setLayoutParams(relativeParams);*/

        //linea view
        LinearLayout linearLayout = rootView.findViewById(R.id.activity_gasto_tab2_historial_linea);
        linearLayout.setVisibility(rootView.GONE);

    /*    //Referenciamos otro layout encargado de poner color amarillo y poner un twxtview
       RelativeLayout banner = rootView.findViewById(R.id.activity_gasto_tab2_historial_banner);
        RelativeLayout.LayoutParams bannerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 510);//205
        //relativeParams.setMargins(0, 205, 0, 0);
        banner.setBackground(ContextCompat.getDrawable(getActivity(), R.color.colorHolo));
        //banner.setBackgroundColor(Color.GREEN);
        banner.setGravity(Gravity.CENTER);//Centramos el banner
        TextView text = new TextView(getActivity());//Creamos un textview
        text.setText(getResources().getString(R.string.lista_de_gastos_may));//texto
        text.setGravity(Gravity.CENTER);//lo centramos
        text.setTextColor(Color.WHITE);//color
        text.setTextSize(20);//tamaño
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(0,110,0,0); //coordenadas
        text.setLayoutParams(textParams);
        banner.addView(text);//lo añadimos al relativelayout referenciado
        banner.setLayoutParams(bannerParams);*/


        //Persistencia de datos y referencia
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences = this.getActivity().getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);

        //Referenciamos al recyclerview y su adaptador
        recyclerView = rootView.findViewById(R.id.activity_gasto_tab2_lista);
        //Reverse el recyclerview
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        //GastoTab1Gasto.getInstance().show();
        mActivity = this.getActivity();
        CargarNotas();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    public void CargarNotas(){
        //MostrarLista
        BD.MostrarListasDelUsuario("Gastos", notas, null, sharedPreferences, recyclerView,null,adaptador, mActivity, null, null);
    }

    //Por alguna razon, este metodo no tiene los datos actualiados de las variables globales, asi que se le tuvieron que pasar
    //de nuevo
    public void Adaptando(RecyclerView recyclerView, Adaptador adaptador, Context mActivity){
        this.notas = new ArrayList<>();
        this.mActivity = (Activity) mActivity; //Aparentemente debemos reinicializar las variables si queremos utilizarlas en metodos, y tambien en los metodos de interfaces
        notas = (ArrayList<Gastos>)BD.retornarGastos.clone();
        //adaptador = new Adaptador(notas, getActivity());
        if(notas != null){
            Log.e("TEST","RETORNAR DATOS NO ES NULO EN ADAPTANDO");
        }
        adaptador = new Adaptador(notas, mActivity);
        adaptador.setListener(this); //Listeners de los botones de las listas
        if(adaptador != null) {
            recyclerView.setAdapter(adaptador);
        }
    }

    //Metodos implementados del Listlistener
    @Override
    public void onGastoClick(final Gastos gastos) {

        //Generamos un alert dialog custom
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mActivity, R.style.BottomOptionsDialogTheme);

        //Es necesario volverlo a reinicializar
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences = this.mActivity.getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);

        View mView = mActivity.getLayoutInflater().inflate(R.layout.activity_editar, null); //Referenciamos el custom layout
        final EditText gastasteEdit = mView.findViewById(R.id.activity_editar_gastaste_edittext);
        final EditText precioEdit =  mView.findViewById(R.id.activity_editar_precio_edittext);
        Button listoButton =  mView.findViewById(R.id.activity_editar_listobutton);
        //Limitar a 2 las lineas que el usuario puede escribir
        MU.LimitarLineasEditText(gastasteEdit, 2);
        //Constante simbolo de peso
        MU.EstiloDinero(precioEdit);

        //SetTexts en los edits
        BD.MostarElementoEditText("Gastos",gastos.getTimeStamp(),"NombreDelGasto",gastasteEdit,sharedPreferences);
        BD.MostarElementoEditText("Gastos",gastos.getTimeStamp(),"Gasto",precioEdit,sharedPreferences);
        //cursor siempre al final
        gastasteEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gastasteEdit.setSelection(gastasteEdit.getText().length());
            }
        });

        //Eliminar los emojis
        MetodosUtiles EliminaEmoijis = new MetodosUtiles();
        gastasteEdit.setFilters(new InputFilter[]{EliminaEmoijis.filters()});

        Progress = new ProgressDialog(mActivity);
        //No se puedan cancelar los progress
        Progress.setCancelable(false);
        Progress.setCanceledOnTouchOutside(false);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
       listoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gastasteStr = gastasteEdit.getText().toString().trim();
                precioStr =   precioEdit.getText().toString().trim();
                //Verificamos que el usuario no haya dejado campos sin escribir
                //Si lo que se escribio es diferente entonces el boton tomara accion
                if(!precioStr.equals(precioStrOld) || !gastasteStr.equals(gastasteStrOld)) {
                        Progress.setMessage("Añadiendo, por favor espere");
                        Progress.show();
                        Calcular(gastasteEdit, precioEdit, gastos); //Se encarga de calcular y guardar el historial en la bd
                }
                dialog.dismiss();
            }
        });
    }

    //Metodo encargado de modificar
    public void Calcular(final EditText gastasteEdit, final EditText precioEdit, final Gastos gastos){
        Database.child(VariablesEstaticas.CurrentUserUID).child("EfectivoTotal").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                //Ocultar keyboard, debe esperar un segundo y medio para que lo cierre bien
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                                    double dineroAntiguo = Double.parseDouble(gastos.getNoteGasto().replace("$",""));
                                    if(dineroAntiguo >= dineroeditDouble){
                                        double diferencia = dineroAntiguo - dineroeditDouble;
                                        Database.child(VariablesEstaticas.CurrentUserUID).child("EfectivoTotal").setValue("$" + df2.format(efectivoTotal + diferencia));
                                    }else{
                                        double diferencia = dineroAntiguo - dineroeditDouble;
                                        Database.child(VariablesEstaticas.CurrentUserUID).child("EfectivoTotal").setValue("$" + df2.format(efectivoTotal + diferencia));
                                    }
                                    mActivity.finish();
                                    gastasteEdit.getText().clear();
                                    precioEdit.getText().clear(); //limpiamos caja de texto
                                    MandarToast.MostrarToast(mActivity, "El efectivo total se actualizó exitosamente");
                                    Adaptador adaptador = new Adaptador(notas, mActivity);
                                    adaptador.ReiniciarNotas();
                                    notas.clear();
                                    Intent reebot = new Intent(mActivity, GastoActivity.class);
                                    reebot.putExtra("INTENT",1);
                                    //Engañar al usuario, no pude encontrar una mejor solucion, el recycler no se actualiza
                                    //hasta que se cambie de activity
                                    //VINCULO
                                    double double00 = Double.parseDouble(precioStr.replace("$",""));
                                    precioStr = "$" + df2.format(double00);
                                    //Actualizamos
                                    BD.ActualizarNota("Gastos", "NombreDelGasto",gastasteStr, gastos);
                                    BD.ActualizarNota("Gastos", "Gasto",precioStr, gastos);
                                    //Quitar animaciones
                                    mActivity.overridePendingTransition(0, 0);
                                    mActivity.startActivity(reebot);
                                    mActivity.overridePendingTransition(0, 0);
                                } else {
                                    MandarToast.MostrarToast(mActivity, "No se puede agregar el gasto por falta de dinero");
                                }
                            } else {
                                precioEdit.setError("Se requiere un monto valido");
                                precioEdit.requestFocus();
                                //MANUAL TECNICO, MANUAL DE USUARIO, NO EMULADOR ADENTRO DEL CD, EJECUTABLE APK, EN EL CD MANUALES Y APK
                                //ENTREGAR 9 Y 16
                            }
                        }else{
                            MandarToast.MostrarToast(mActivity, "Se necesita efectivo para añadir nuevo gasto");
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

    @Override
    public void onGastoLongClick(final Gastos gastos) {
        //Es necesario volverlo a reinicializar
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences = this.mActivity.getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.BottomOptionsDialogThemeWhite);
        builder.setPositiveButton(
                "ELIMINAR\nY REVERTIR",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MandarToast.MostrarToast(mActivity, "Gasto eliminado");
                        Adaptador adaptador = new Adaptador(notas, mActivity);
                        adaptador.ReiniciarNotas();
                        notas.clear();
                        //Para empezar en el segundo tab
                        final Intent reebot = new Intent(mActivity, GastoActivity.class);
                        reebot.putExtra("INTENT",1);
                        //Engañar al usuario, no pude encontrar una mejor solucion, el recycler no se actualiza
                        //hasta que se cambie de activity
                        //VINCULO
                        Database.child(VariablesEstaticas.CurrentUserUID).child("EfectivoTotal").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                mActivity.finish();
                                Double Efectivo = Double.parseDouble(dataSnapshot.getValue().toString().replace("$", ""));
                                Double NuevoEfectivo = Efectivo + Double.parseDouble(gastos.getNoteGasto().replace("$", "")); //Sumamos el efectivo actual con el del gasto
                            Database.child(VariablesEstaticas.CurrentUserUID).child("EfectivoTotal").setValue("$" + df2.format(NuevoEfectivo));
                                BD.EliminarNota("Gastos",gastos); //Eliminamos el gasto
                                //Quitar animaciones
                                mActivity.overridePendingTransition(0, 0);
                                mActivity.startActivity(reebot); //Abrimos el activity de nuevo
                                mActivity.overridePendingTransition(0, 0);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                });
        builder.setNegativeButton(
                "ELIMINAR\nGASTO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mActivity.finish();
                        MandarToast.MostrarToast(mActivity, "Gasto eliminado");
                        Adaptador adaptador = new Adaptador(notas, mActivity);
                        adaptador.ReiniciarNotas();
                        notas.clear();
                        //Para empezar en el segundo tab
                        Intent reebot = new Intent(mActivity, GastoActivity.class);
                        reebot.putExtra("INTENT",1);
                        //Engañar al usuario, no pude encontrar una mejor solucion, el recycler no se actualiza
                        //hasta que se cambie de activity
                        //VINCULO
                        BD.EliminarNota("Gastos",gastos);
                        //Quitar animaciones
                        mActivity.overridePendingTransition(0, 0);
                        mActivity.startActivity(reebot);
                        mActivity.overridePendingTransition(0, 0);
                    }
                });

        builder.setNeutralButton(
                "CANCELAR",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                });
        //Centrar Titulo
        TextView title = new TextView(mActivity);
        title.setText("¿Eliminar gasto?");
        //title.setBackgroundColor(Color.BLACK);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        //title.setTextColor(Color.WHITE);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        builder.setCustomTitle(title);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
        //Esto es necesario para que sea compatible con todos los celulares
        final float inPixels= mActivity.getResources().getDimension(R.dimen.dimen_entry_in_dp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(inPixels)); //Modificamos la altura

        //centrar los botones
        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button btnNeutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;
        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);
        btnNeutral.setLayoutParams(layoutParams);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED); //Hacemos el boton de eliminar de color rojo
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED); //Hacemos el boton de eliminar de color rojo
    }
}
