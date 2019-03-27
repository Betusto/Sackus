package aplicacion.android.app.betusto.sackus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Controlador de tabs ModificandoActivity

public class ModificandoActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TextView OlvideContraseña;
    private ImageButton nowifibutton;
    private Button CambiarContraseña;

    //Variable que nos permite vincular con el metodo que se encuentra en MetodosUtiles de VariablesEstaticas para guardar o cargar data
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private DatabaseReference Database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificand);
        OlvideContraseña = findViewById(R.id.activity_modificando_olvidepassword);
        nowifibutton = findViewById(R.id.activity_modficando_nowifibutton);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        //Para que los botones funcionen en el fragment
        OlvideContraseña.bringToFront();
        nowifibutton.bringToFront();


        final DetectaConexion CD = new DetectaConexion(this);
        final MetodosUtiles MandarToast = new MetodosUtiles();

        nowifibutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CD.mensajeNoInternet(ModificandoActivity.this);
            }
        });

        //Ir al activity de cambiar contraseña
        OlvideContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OlvideContraseña.setText(Html.fromHtml(getString(R.string.Olvidé_mi_contraseña))); //subrayar texto
                Log.e("TEST", ""+VariablesEstaticas.Locked);
                MetodosUtiles Palabra = new MetodosUtiles();
                if(VariablesEstaticas.Locked == false) { //Si no esta locked
                    VariablesEstaticas.Locked = true;
                    if (CD.isConnected()) {
                        Palabra.Subrayar(OlvideContraseña); //Manda a llamar al metodo que dejara de subrayar el texto
                        Intent olvidepassword = new Intent(ModificandoActivity.this, OlvidePassword.class); //Abre el activity
                        startActivity(olvidepassword);
                        VariablesEstaticas.Locked = false; //Para poder volver a entrar al boton
                    } else {
                        MandarToast.MostrarToast(ModificandoActivity.this,"Se necesita conexión a internet para acceder a " +
                                "esta opción");
                        Palabra.Subrayar(OlvideContraseña); //Manda a llamar al metodo que dejara de subrayar el texto
                        VariablesEstaticas.Locked = false; //Para volver a entrar al boton
                    }
                }
            }
        });

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //Mantener sincronizada la base de datos
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences= getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //Nombres de los tabs
        tabLayout.getTabAt(0).setText("CAMBIAR CONTRASEÑA");
        tabLayout.getTabAt(1).setText("CAMBIAR CORREO");
        tabLayout.getTabAt(2).setText("CAMBIAR NOMBRE");

        CD.startConexion(nowifibutton);
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    ModificandoTab1Password Tab1 = new ModificandoTab1Password();
                    return Tab1;
                case 1:
                    ModificandoTab2Correo Tab2 = new ModificandoTab2Correo();
                    return Tab2;
                case 2:
                    ModificandoTab3Usuario Tab3 = new ModificandoTab3Usuario();
                    return Tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}

