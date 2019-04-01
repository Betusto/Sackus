package aplicacion.android.app.betusto.sackus;

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
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
    public static ArrayList<Notas> notas = new ArrayList<>();
    private Adaptador adaptador;
    public View rootView;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.gasto_tab2_historial, container, false);

        //Aumentamos el margen top del layout para tener espacio para el icono de wifi
        RelativeLayout layout = rootView.findViewById(R.id.porfa);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeParams.setMargins(0, 200, 0, 0);
        layout.setLayoutParams(relativeParams);

        //Referenciamos al recyclerview y su adaptador
        recyclerView = rootView.findViewById(R.id.activity_gasto_tab2_lista);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adaptador = new Adaptador(notas, getActivity());
        recyclerView.setAdapter(adaptador);
        return rootView;
    }

    public void onResume(){
        super.onResume();
        CargarNotas();
    }

    public void CargarNotas(){
        //for(int i = 0; i < 12; i++){
        //    notas.add(new Notas("hola", new Date().getTime()));
        //}
        adaptador = new Adaptador(notas, getActivity());
        recyclerView.setAdapter(adaptador);
        //GastoTab2Historial.recyclerView.setAdapter(adaptador);
        //adaptador.notifyDataSetChanged();
    }

    public void AlAñadirNuevaNota(){
        notas.add(new Notas("Holi", new Date().getTime()));
        Log.e("TEST", "La cantidad es: "+notas.size());
        if(adaptador != null){
            adaptador.notifyDataSetChanged();
        }
    }


}
