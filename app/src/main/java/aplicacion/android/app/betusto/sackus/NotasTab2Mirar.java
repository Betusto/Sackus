package aplicacion.android.app.betusto.sackus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class NotasTab2Mirar extends Fragment implements  NotasListListener{
    public android.support.v7.widget.RecyclerView recyclerView;
    public ArrayList<Notas> notasApartado = new ArrayList<>();
    private AdaptadorNotas adaptadorNotas;
    MetodosUtiles MU = new MetodosUtiles();
    BaseDeDatos BD = new BaseDeDatos();
    private DatabaseReference Database;
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();
    private Activity mActivity;
    MetodosUtiles MandarToast = new MetodosUtiles();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notas_tab2_mirar, container, false);

        //linea view
        LinearLayout linearLayout = rootView.findViewById(R.id.activity_notas_tab2_mirar_linea);
        linearLayout.setVisibility(rootView.GONE);

        //Persistencia de datos y referencia
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences = this.getActivity().getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);

        //Referenciamos al recyclerview y su adaptador
        recyclerView = rootView.findViewById(R.id.activity_notas_tab2_lista);
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

    public void CargarNotas(){
        //MostrarLista
        BD.MostrarListasDelUsuario("Notas", null, null, sharedPreferences, recyclerView,null,null, mActivity, null, null, notasApartado, adaptadorNotas);
    }

    //Por alguna razon, este metodo no tiene los datos actualiados de las variables globales, asi que se le tuvieron que pasar
    //de nuevo
    public void Adaptando(RecyclerView recyclerView, AdaptadorNotas adaptador, Context mActivity) {
        this.notasApartado = new ArrayList<>();
        this.mActivity = (Activity) mActivity; //Aparentemente debemos reinicializar las variables si queremos utilizarlas en metodos, y tambien en los metodos de interfaces
        notasApartado = (ArrayList<Notas>) BD.retornarNotas.clone();
        //adaptador = new Adaptador(notas, getActivity());
        if (notasApartado != null) {
            Log.e("TEST", "RETORNAR DATOS NO ES NULO EN ADAPTANDO");
        }
        adaptador = new AdaptadorNotas(notasApartado, mActivity);
        adaptador.setListener(this); //Listeners de los botones de las listas
        if (adaptador != null) {
            recyclerView.setAdapter(adaptador);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onGastoClick(final Notas notas) {
    }

  /*  public void MostrarNota(final String Titulo, final String Cuerpo, String Timestamp, final EditText TituloEdit, final EditText CuerpoEdit){
        //Referencia para la BD de forma en que podamos meter una tabla dentro de ella
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        //Referencia a la tabla del child:
        final DatabaseReference TablaHija = database.child(VariablesEstaticas.CurrentUserUID);
        TablaHija.child("Notas").child(Timestamp).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if necesario por si ya no obtiene el valor del uid por alguna razon, de esta manera no crasheara
                //Se necesitan volver a cargar los datos porque cada cambio en la base de datos activa este ondatachange
                VE.CargarDatos(sharedPreferences);
                if(VariablesEstaticas.CurrentUserUID != null && !VariablesEstaticas.CurrentUserUID.equals("")) {
                    if (dataSnapshot.getValue() != null){
                        String titulo = dataSnapshot.child("Titulo").getValue().toString();
                        String cuerpo = dataSnapshot.child("Descripcion").getValue().toString();
                        TituloEdit.setText(titulo);
                        CuerpoEdit.setText(cuerpo);
                        //Log.e("TEST","Valor de elemento: "+elemento.replace("$", ""));
                        if(elemento.contains("$0.")){
                            //Si el elemento empieza con 0 deberemos quitar ese 0
                            editText.setText(elemento.replace("$0", ""));
                        }else{
                            editText.setText(elemento.replace("$", ""));
                        }
                        //Log.e("TEST",editText.getText().toString());
                        if (elemento.contains("$")) {
                            GastoTab2Historial.precioStrOld = editText.getText().toString();
                        } else {
                            GastoTab2Historial.gastasteStrOld = editText.getText().toString();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    }*/

    @Override
    public void onGastoLongClick(Notas notas) {

    }
}
