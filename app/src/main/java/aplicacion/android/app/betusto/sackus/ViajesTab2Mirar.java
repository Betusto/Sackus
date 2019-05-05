package aplicacion.android.app.betusto.sackus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ViajesTab2Mirar extends Fragment implements ViajeListListener{
    public android.support.v7.widget.RecyclerView recyclerView;
    public ArrayList<Viajes> viajes = new ArrayList<>();
    private AdaptadorViajes adaptadorViajes;
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
        View rootView = inflater.inflate(R.layout.viajes_tab2_ver, container, false);

        //ocultamos linea view
        LinearLayout linearLayout = rootView.findViewById(R.id.activity_viajes_linea);
        linearLayout.setVisibility(rootView.GONE);

        //Persistencia de datos y referencia
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences = this.getActivity().getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);

        //Referenciamos al recyclerview y su adaptador
        recyclerView = rootView.findViewById(R.id.activity_viajes_tab2_lista);
        //Reverse el recyclerview
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        mActivity = this.getActivity();
        CargarViajes();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    public void CargarViajes(){
        //MostrarLista
        BD.MostrarListasDelUsuario("Viajes", null, null, sharedPreferences, recyclerView,null,null, mActivity, viajes, adaptadorViajes);
    }

    //Por alguna razon, este metodo no tiene los datos actualiados de las variables globales, asi que se le tuvieron que pasar
    //de nuevo
    public void Adaptando(RecyclerView recyclerView, AdaptadorViajes adaptador, Context mActivity){
        this.viajes = new ArrayList<>();
        this.mActivity = (Activity) mActivity; //Aparentemente debemos reinicializar las variables si queremos utilizarlas en metodos, y tambien en los metodos de interfaces
        viajes = (ArrayList<Viajes>)BD.retornarViajes.clone();
        //adaptador = new Adaptador(notas, getActivity());
        if(viajes != null){
            Log.e("TEST","RETORNAR DATOS NO ES NULO EN ADAPTANDO");
        }
        adaptador = new AdaptadorViajes(viajes, mActivity);
        adaptador.setListener(this); //Listeners de los botones de las listas
        if(adaptador != null) {
            recyclerView.setAdapter(adaptador);
        }
    }

    @Override
    public void onGastoClick(Viajes viajes) {

    }

    @Override
    public void onGastoLongClick(Viajes viajes) {

    }
}
