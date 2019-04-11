package aplicacion.android.app.betusto.sackus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.solver.widgets.Helper;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class GastoTab2Historial extends Fragment {
    public Context Tab2 = getActivity();
    public android.support.v7.widget.RecyclerView recyclerView;
    public ArrayList<Gastos> notas = new ArrayList<>();
    private Adaptador adaptador;
    public View rootView;
    MetodosUtiles MU = new MetodosUtiles();
    BaseDeDatos BD = new BaseDeDatos();
    private DatabaseReference Database;
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();
    MetodosUtiles MandarToast = new MetodosUtiles();
    private Activity mActivity;
    private int test = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.gasto_tab2_historial, container, false);

        //Aumentamos el margen top del layout para tener espacio para el icono de wifi
        RelativeLayout layout = rootView.findViewById(R.id.porfa);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeParams.setMargins(0, 205, 0, 0);
        layout.setLayoutParams(relativeParams);

        //linea view
        LinearLayout linearLayout = rootView.findViewById(R.id.activity_gasto_tab2_historial_linea);
        linearLayout.setVisibility(rootView.GONE);

        //Referenciamos otro layout encargado de poner color amarillo y poner un twxtview
       RelativeLayout banner = rootView.findViewById(R.id.activity_gasto_tab2_historial_banner);
        RelativeLayout.LayoutParams bannerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 205);
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
        banner.setLayoutParams(bannerParams);


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
        //MandarToast.MostrarToast();
        Log.e("TEST","VA A CARGAR LAS NOTAS");
        BD.MostrarListasDelUsuario("Gastos", notas, sharedPreferences, recyclerView, adaptador, mActivity);
    }

    //Por alguna razon, este metodo no tiene los datos actualiados de las variables globales, asi que se le tuvieron que pasar
    //de nuevo
    public void Adaptando(RecyclerView recyclerView, Adaptador adaptador, Context mActivity){
        this.notas = new ArrayList<>();
        notas = (ArrayList<Gastos>)BD.retornarGastos.clone();
        //adaptador = new Adaptador(notas, getActivity());
        if(notas != null){
            Log.e("TEST","RETORNAR DATOS NO ES NULO EN ADAPTANDO");
        }
        adaptador = new Adaptador(notas, mActivity);
        if(adaptador != null) {
            recyclerView.setAdapter(adaptador);
        }
    }
}
