package aplicacion.android.app.betusto.sackus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputFilter;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

public class NotasTab1Escribir extends Fragment {
    //Variables del layout
    private FloatingActionButton añadirButton;
    private EditText tituloEdit;
    private EditText descripcionEdit;

    //Metodos qa llamar
    MetodosUtiles MU = new MetodosUtiles();
    BaseDeDatos BD = new BaseDeDatos();
    MetodosUtiles MandarToast = new MetodosUtiles();
    VariablesEstaticas VE = new VariablesEstaticas();

    //Variables de almacenamiento de datos y formato
    private DatabaseReference Database;
    SharedPreferences sharedPreferences;

    //OtrasVariables
    private int detectorErrores = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notas_tab1_escribir, container, false);
        //TODO: Notas y Viajes: Editar y Eliminar
        //TODO: Cambios de Hiram y el otro men
        //Referenciamos
        añadirButton = rootView.findViewById(R.id.fab3);
        tituloEdit = rootView.findViewById(R.id.activity_notas_tab1_indicacion_edittext2);
        descripcionEdit = rootView.findViewById(R.id.activity_notas_tab1_indicacion_edittext);

        //Eliminar los emojis
        tituloEdit.setFilters(new InputFilter[]{MU.filters()});
        descripcionEdit.setFilters(new InputFilter[]{MU.filters()});

        //Persistencia de datos y referencia
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences = this.getActivity().getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);

        //Limitar a 2 las lineas que el usuario puede escribir
        MU.LimitarLineasEditText(tituloEdit, 2);
        MU.LimitarLineasEditText(descripcionEdit, 10);

        añadirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectorErrores = 0;
                String tituloText = tituloEdit.getText().toString().trim();
                String descripcionText = descripcionEdit.getText().toString().trim();
                if(tituloText.isEmpty()){
                    tituloEdit.setError(getResources().getString(R.string.la_nota_necesita_un_titulo));
                    tituloEdit.requestFocus();
                    detectorErrores = 1;
                }
                if(descripcionText.isEmpty()){
                    descripcionEdit.setError(getResources().getString(R.string.la_nota_necesita_contenido));
                    descripcionEdit.requestFocus();
                    detectorErrores = 1;
                }
                if(detectorErrores == 0){
                    BD.AlAñadirApartadoNotas(tituloText, descripcionText);
                    Intent reebot = new Intent(getActivity(), NotasActivity.class);
                    getActivity().finish();
                    //Quitar animacione
                    getActivity().overridePendingTransition(0, 0);
                    getActivity().startActivity(reebot);
                    getActivity().overridePendingTransition(0, 0);
                }
            }
        });
        return rootView;
    }
}
