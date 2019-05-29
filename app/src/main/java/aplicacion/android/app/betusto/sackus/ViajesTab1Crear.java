package aplicacion.android.app.betusto.sackus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
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
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViajesTab1Crear extends Fragment {
    //Variables del layout
    private TextView efectivototalText;
    private TextView fechaInicioText;
    private TextView fechaRegresoText;
    private FloatingActionButton añadirButton;
    private EditText dondeEdit;
    private EditText dineroLlevasEdit;
    private EditText invertirHotelEdit;
    private EditText invertirTransporteEdit;
    private EditText invertirComidaEdit;
    private EditText invertirBaratijasEdit;
    private ImageButton compartirViajeButton;
    private ImageButton infoCompartirButton;
    private Button fechaInicioButton;
    private Button fechaRegresoButton;
    private LinearLayout linearLayout;
    private TextView acompañantesText;
    private EditText mensajeEdit;
    //EditText del inflater
    private EditText acompañanteEdit;

    //Metodos qa llamar
    MetodosUtiles MU = new MetodosUtiles();
    BaseDeDatos BD = new BaseDeDatos();
    MetodosUtiles MandarToast = new MetodosUtiles();
    VariablesEstaticas VE = new VariablesEstaticas();

    //Variables de almacenamiento de datos y formato
    private DatabaseReference Database;
    SharedPreferences sharedPreferences;
    private static DecimalFormat df2 = new DecimalFormat("0.00"); //Para usar solo dos decimales

    //Otras variables
    private int detectorDeErrores = 0;
    private String dondeStr, dinerollevasStr, invertirHotelStr, invertirTransporteStr, invertirComidaStr, invertirBaratijasStr, fechaInicioStr,
    fechaRegresoStr;
    private ProgressDialog Progress;
    private int controlDeEditTexts = 0;
    private ArrayList<EditText> acompañantesEditList = new ArrayList<>();
    private List CantidadDeSnapshots = new ArrayList();
    private boolean ExisteCorreo = false;
    public String NombreDelUsuario;
    //private List<Boolean> Existe = new ArrayList(){};
    //private List<Integer> cantidadErrores = new ArrayList(){};
    //private int[] values = new int[]{1, 2, 3 };
    private boolean[] existe = new boolean[]{false, false, false, false, false};
    private int helper;
    private Double sumaTotal = 0.00;
    private List<String> UIDS = new ArrayList<>();
    private List<String> NombresEscritos = new ArrayList<>();
    private int i; //Variable del for
    boolean flag=true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.viajes_tab1_crear, container, false);
        //Referenciamos
        Progress = new ProgressDialog(getActivity());
        añadirButton = rootView.findViewById(R.id.fab2);
        efectivototalText = rootView.findViewById(R.id.activity_viajes_tab1_efectivo_total_text);
        dondeEdit = rootView.findViewById(R.id.activity_viajes_tab1_nombreviaje_edittext);
        dineroLlevasEdit = rootView.findViewById(R.id.activity_viajes_tab1_dineroquellevas_edittext);
        invertirHotelEdit = rootView.findViewById(R.id.activity_viajes_tab1_dineroainvertirenhotel_edittext);
        invertirTransporteEdit = rootView.findViewById(R.id.activity_viajes_tab1_dineroainvertirentransporte_edittext);
        invertirComidaEdit = rootView.findViewById(R.id.activity_viajes_tab1_dineroainvertirencomida_edittext);
        invertirBaratijasEdit = rootView.findViewById(R.id.activity_viajes_tab1_dineroainvertirenbaratijas_edittext);
        compartirViajeButton = rootView.findViewById(R.id.activity_viajes_tab1_añadiracompañante_button);
        infoCompartirButton = rootView.findViewById(R.id.activity_viajes_tab1_info_button);
        fechaInicioButton = rootView.findViewById(R.id.activity_viajes_tab1_seleccionarfechainiciobutton);
        fechaRegresoButton = rootView.findViewById(R.id.activity_viajes_tab1_seleccionarfecharegresobutton);
        fechaInicioText = rootView.findViewById(R.id.activity_viajes_tab1_fechainiciotext);
        fechaRegresoText = rootView.findViewById(R.id.activity_viajes_tab1_fechafinaltext);
        linearLayout = rootView.findViewById(R.id.activity_viajes_tab1_linearlayout);
        acompañantesText = rootView.findViewById(R.id.activity_viajes_tab1_indicacion2_textview);
        mensajeEdit = rootView.findViewById(R.id.activity_viajes_tab1_mensaje_edittext);

        //Eliminar los emojis
        dondeEdit.setFilters(new InputFilter[]{MU.filters()});
        mensajeEdit.setFilters(new InputFilter[]{MU.filters()});

        //No se puedan cancelar los progress
        Progress.setCancelable(false);
        Progress.setCanceledOnTouchOutside(false);

        //Persistencia de datos y referencia
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences = this.getActivity().getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);

        //Limitar a 2 las lineas que el usuario puede escribir
        MU.LimitarLineasEditText(dondeEdit, 2);
        MU.LimitarLineasEditText(mensajeEdit, 4);

        //Constante simbolo de peso
        MU.EstiloDinero(dineroLlevasEdit);
        MU.EstiloDinero(invertirBaratijasEdit);
        MU.EstiloDinero(invertirComidaEdit);
        MU.EstiloDinero(invertirHotelEdit);
        MU.EstiloDinero(invertirTransporteEdit);

        añadirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validar();
            }
        });

        compartirViajeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrearEditText();
            }
        });

        infoCompartirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dialogo
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Compartir viaje");
                builder.setMessage("Si tienes pensado viajar con alguien que tenga también la aplicación Sackus, puedes invitarlos a que compartan" +
                        "el viaje y el dinero que llevan contigo. ¡Puedes compartir un viaje con hasta 5 personas!");
                builder.setPositiveButton("OK", null);
                AlertDialog dialog = builder.show();
                //Align del dialogo
                TextView messageView = dialog.findViewById(android.R.id.message);
                messageView.setGravity(Gravity.FILL);

            }
        });

        fechaInicioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mostrar Calendario
                /*if(fechaInicioText.getText().toString().substring(17).replace("\n", "").isEmpty()) {
                    MU.MostrarToast(getActivity(), "si esta vacio man");
                }*/
                MU.MostrarDatePicker(getActivity(), fechaInicioText, "Fecha de inicio: ");
            }
        });

        fechaRegresoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mostrar Calendario
                MU.MostrarDatePicker(getActivity(), fechaRegresoText, "Fecha de regreso: ");
            }
        });
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        BD.MostrarElementoUsuarioActualConComplemento(Database, "EfectivoTotal", "Efectivo Total: ", efectivototalText, sharedPreferences);
    }

    public void Validar() {
        sumaTotal=0.00;
        detectorDeErrores=0;
        dondeStr = dondeEdit.getText().toString().trim();
        dinerollevasStr = dineroLlevasEdit.getText().toString().trim();
        invertirHotelStr = invertirHotelEdit.getText().toString().trim();
        invertirTransporteStr = invertirTransporteEdit.getText().toString().trim();
        invertirComidaStr = invertirComidaEdit.getText().toString().trim();
        invertirBaratijasStr = invertirBaratijasEdit.getText().toString().trim();
        fechaInicioStr = fechaInicioText.getText().toString().substring(17).replace("\n", "").trim();
        fechaRegresoStr = fechaRegresoText.getText().toString().substring(18).replace("\n", "").trim();
        //do {
        //    BD.GetElementoUsuarioActual(Database, "Usuario", sharedPreferences);
        //}while(NombreDelUsuario == null);
        if(dondeStr.isEmpty()){
            dondeEdit.setError("Se necesita nombre del lugar a visitar");
            dondeEdit.requestFocus();
            detectorDeErrores = 1;
        }
        if(dinerollevasStr.equals("$") || dinerollevasStr.equals("$.") || dinerollevasStr.isEmpty()){
            dineroLlevasEdit.setError("Se necesita un monto de dinero");
            dineroLlevasEdit.requestFocus();
            detectorDeErrores = 1;
        }
        if(detectorDeErrores == 0) {
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
                MU.MostrarToast(getActivity(), "La suma de los montos a invertir sobrepasan al dinero total que llevas");
                detectorDeErrores=1;
            }
            if (VariablesEstaticas.Locked == false) {
                VariablesEstaticas.Locked = true;
                if (detectorDeErrores == 0){
                    if (controlDeEditTexts != 0) {
                        ExisteCorreo = false;
                        existe[0] = false;
                        existe[1] = false;
                        existe[2] = false;
                        existe[3] = false;
                        existe[4] = false;
                        detectorDeErrores = 0;
                        helper = 0;
                        Database.child(VariablesEstaticas.CurrentUserUID).child("Usuario").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //if necesario por si ya no obtiene el valor del uid por alguna razon, de esta manera no crasheara
                                //Se necesitan volver a cargar los datos porque cada cambio en la base de datos activa este ondatachange
                                VE.CargarDatos(sharedPreferences);
                                if (VariablesEstaticas.CurrentUserUID != null && !VariablesEstaticas.CurrentUserUID.equals("")) {
                                    String resultado = dataSnapshot.getValue().toString();
                                    NombreDelUsuario = resultado; //Conseguimos el nombre de usuario del usuario actual para evitar errores si se agrega el mismo
                                    Log.e("TEST", "Valor de UsuarioActual: " + NombreDelUsuario);
                                    for (i = 0; i < acompañantesEditList.size(); i++) { //Recorremos el arraylist
                                        if (acompañantesEditList.get(i).getText().toString().trim().isEmpty()) {
                                            acompañantesEditList.get(i).setError("Se requiere nombre de usuario del acompañante");
                                            acompañantesEditList.get(i).requestFocus();
                                            Log.e("TEST", "Acompañante " + i + " empty");
                                            detectorDeErrores = 1;
                                        } else if (acompañantesEditList.get(i).getText().toString().trim().equals(NombreDelUsuario)) {
                                            acompañantesEditList.get(i).setError("No es necesario agregarte como acompañante");
                                            acompañantesEditList.get(i).requestFocus();
                                            detectorDeErrores = 1;
                                        } else if (!acompañantesEditList.get(i).getText().toString().trim().isEmpty()) {
                                            DetectarSiSeRepiteElementos();
                                            if (detectorDeErrores != 1) {
                                                validarUsuariosCorrectos(acompañantesEditList.get(i).getText().toString().trim(), i);
                                            }
                                        }
                                    }
                                    VariablesEstaticas.Locked = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    } else {
                        AgregarViaje();
                    }
            }else{
                    VariablesEstaticas.Locked = false;
                }
        }else{
                VariablesEstaticas.Locked = false;
            }
        }else{
            VariablesEstaticas.Locked = false;
        }
        }

        //Metodo encargado de revisar que no se repitan nombres de usuarios
        public void DetectarSiSeRepiteElementos(){
            Set<String> set=new HashSet<>();
            for(int i=0;i<acompañantesEditList.size();i++){
                flag=set.add(acompañantesEditList.get(i).getText().toString().trim());
                if(!(flag)){
                    acompañantesEditList.get(i).setError("Repetiste nombre de usuario");
                    acompañantesEditList.get(i).requestFocus();
                    detectorDeErrores = 1;
                    flag=true;
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



       /* if(){

        }
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
*/

    public void validarUsuariosCorrectos(final String usuarioEscrito, final int i){
        Database.child(VariablesEstaticas.CurrentUserUID).child("Usuario").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if necesario por si ya no obtiene el valor del uid por alguna razon, de esta manera no crasheara
                //Se necesitan volver a cargar los datos porque cada cambio en la base de datos activa este ondatachange
                VE.CargarDatos(sharedPreferences);
                if (VariablesEstaticas.CurrentUserUID != null && !VariablesEstaticas.CurrentUserUID.equals("")) {
                    String resultado = dataSnapshot.getValue().toString();
                    NombreDelUsuario = resultado; //Conseguimos el nombre de usuario del usuario actual para evitar errores si se agrega el mismo
                    Log.e("TEST", "Valor de UsuarioActual: " + NombreDelUsuario);
                    //Marcamos los setters
                    Database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final int CantidadTotalDeSnaps = (int) dataSnapshot.getChildrenCount();
                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) //Recorremos cada campo de la tabla Usuarios
                            {
                                if (snapshot.getValue() != null) { //*No se puede usar equals en null aunque sea string*
                                    Database.child(snapshot.getKey()).child("Usuario").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.e("test", "valor de ExisteCorreo: " + ExisteCorreo);
                                                    CantidadDeSnapshots.add(snapshot.child("Usuario").getValue().toString());
                                                    if (usuarioEscrito.equals(snapshot.child("Usuario").getValue().toString())) {
                                                        UIDS.add(snapshot.getKey()); //Añadimos UID del usuario para mandarle la solicitud
                                                        NombresEscritos.add(usuarioEscrito);
                                                        existe[i] = true;
                                                        helper++;
                                                        //detectorDeErrores = 0;
                                                        acompañantesEditList.get(i).setError(null);
                                                        acompañantesEditList.get(i).clearFocus();
                                                        Log.e("TEST", "ELEMENTO " + i + " no tiene errores");
                                                        //UsuarioEncontrado = snapshot.child("Usuario").getValue().toString();
                                                    } else {
                                                        if (existe[i] == false) {
                                                            Log.e("TEST", "ELEMENTO " + i + " no tiene errore entro siendo true");
                                                            //  detectorDeErrores = 1;
                                                        }
                                                    }
                                                    Log.e("test", "termino una vuelta, valor de totalsnaps: " + CantidadTotalDeSnaps + " valor actual: " + CantidadDeSnapshots.size());
                                                    if (CantidadTotalDeSnaps == CantidadDeSnapshots.size()) {//Ultimo ciclo del for ejecutar lo siguiente
                                                        Log.e("TEST", "SOLO DEBE ENTRAR 5 VECES, VECES: " + i);
                                                        if (existe[i] == true) {
                                                            CantidadDeSnapshots.clear();
                                                            acompañantesEditList.get(i).setError(null);
                                                            acompañantesEditList.get(i).clearFocus();
                                                            Log.e("test", "Elemento " + i + " termino en true");
                                                            Log.e("nuevoarreglo","valor de helper: "+helper);
                                                            //boolean[] nuevoArreglo;
                                                            //nuevoArreglo = Arrays.copyOf(existe, helper);
                                                            //
                                                            //boolean revisarTrue = MetodosUtiles.areAllTrue(nuevoArreglo);
                                                            //Log.e("nuevoarreglo", "Vuelta "+i+" "+Arrays.toString(nuevoArreglo));

                                                            //if (revisarTrue == true) {

                                                            // Ultima vuelta del for
                                                            if(acompañantesEditList.size() == helper) {
                                                                AgregarViaje();
                                                            }
                                                            //}
                                                        } else {
                                                            Log.e("test", "Elemento " + i + " termino en false");
                                                            if (i == 0) {
                                                                CantidadDeSnapshots.clear();
                                                                Log.e("TEST", "SE DETECTO UN ERROR en elemento: " + i);
                                                                acompañantesEditList.get(i).requestFocus();
                                                                acompañantesEditList.get(i).setError("El usuario que escribió no se encuentra registrado");
                                                            }
                                                            if (i == 1) {
                                                                CantidadDeSnapshots.clear();
                                                                Log.e("TEST", "SE DETECTO UN ERROR en elemento: " + i);
                                                                acompañantesEditList.get(i).requestFocus();
                                                                acompañantesEditList.get(i).setError("El usuario que escribió no se encuentra registrado");
                                                            }
                                                            if (i == 2) {
                                                                CantidadDeSnapshots.clear();
                                                                Log.e("TEST", "SE DETECTO UN ERROR en elemento: " + i);
                                                                acompañantesEditList.get(i).requestFocus();
                                                                acompañantesEditList.get(i).setError("El usuario que escribió no se encuentra registrado");
                                                            }
                                                            if (i == 3) {
                                                                CantidadDeSnapshots.clear();
                                                                Log.e("TEST", "SE DETECTO UN ERROR en elemento: " + i);
                                                                acompañantesEditList.get(i).requestFocus();
                                                                acompañantesEditList.get(i).setError("El usuario que escribió no se encuentra registrado");
                                                            }
                                                            if (i == 4) {
                                                                CantidadDeSnapshots.clear();
                                                                Log.e("TEST", "SE DETECTO UN ERROR en elemento: " + i);
                                                                acompañantesEditList.get(i).requestFocus();
                                                                acompañantesEditList.get(i).setError("El usuario que escribió no se encuentra registrado");
                                                            }
                                                        }

                                                    }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            //Toast.makeText(RegisterActivity.this, "Ocurrio un error al intentar acceder a la base de datos", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }else{
                                    Log.e("TEST","snapshot es nulo");
                                    VariablesEstaticas.Locked = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }else{
                    VariablesEstaticas.Locked = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void AgregarViaje() {
        sumaTotal=0.00;
        String compartir = "false";
        Log.e("nuevoarreglo","Paso el ondatachange con exito");
        helper=0;
        List<String> Invitados = new ArrayList(Arrays.asList("-","-","-","-","-"));
        Log.e("PRUEBA","Valor de Invitados: "+Invitados);
        Log.e("PRUEBA","Valor de NombresEscritos: "+NombresEscritos);
        Collections.copy(Invitados, NombresEscritos);
        if(detectorDeErrores == 0) {
            Progress.setMessage("Añadiendo, por favor espere");
            Progress.show();
            DetectaConexion CD = new DetectaConexion(this.getActivity());
            if(controlDeEditTexts != 0) {
                compartir = "true";
                if (CD.isConnected()) {
                    MandarToast.MostrarToast(getActivity(), "Mensaje enviado");
                } else {
                    MandarToast.MostrarToast(getActivity(), "El mensaje se enviará al regresar la conexión");
                }
                String Mensaje = mensajeEdit.getText().toString().trim();
                if (Mensaje.isEmpty()) {
                    Mensaje = "-";
                }
                BD.AlAñadirNuevaInvitacion(UIDS, dondeStr, Mensaje, NombreDelUsuario, NombresEscritos);
                NombresEscritos.clear();
                UIDS.clear();
                VariablesEstaticas.Locked = false;
            }
            //Añadimos el viaje
            BD.AlAñadirNuevoViaje(dondeStr, dinerollevasStr, invertirHotelStr, invertirTransporteStr,
                    invertirComidaStr, invertirBaratijasStr, fechaInicioStr, fechaRegresoStr, compartir, Invitados);
            Invitados.clear();
            /*if(!fechaInicioText.getText().toString().substring(17).isEmpty() || fechaInicioText.getText().toString().substring(17) != null &&
            !fechaRegresoText.getText().toString().substring(18).isEmpty() || fechaRegresoText.getText().toString().substring(18) != null){
                String fechaInicioStr = fechaInicioText.getText().toString().substring(17).replace("\n", "");
                String fechaRegresoStr = fechaRegresoText.getText().toString().substring(18).replace("\n","");
                if(fechaInicioStr.contains("/") && fechaRegresoStr.contains("/")){
                    VariablesEstaticas.Locked = false;
                    BD.AlAñadirNuevoViaje(dondeStr, dinerollevasStr, fechaInicioStr, fechaRegresoStr, compartir);
                }else{
                    VariablesEstaticas.Locked = false;
                    BD.AlAñadirNuevoViaje(dondeStr, dinerollevasStr, "-", "-", compartir);
                }
            }*/
            VariablesEstaticas.Locked = false;
            MandarToast.MostrarToast(getActivity(), "Viaje añadido");
            Intent reebot = new Intent(getActivity(), ViajesActivity.class);
            //Engañar al usuario, no pude encontrar una mejor solucion, el recycler no se actualiza
            //hasta que se cambie de activity
            getActivity().finish();
            //Quitar animacione
            getActivity().overridePendingTransition(0, 0);
            getActivity().startActivity(reebot);
            getActivity().overridePendingTransition(0, 0);
            Progress.dismiss();
        }
        VariablesEstaticas.Locked = false;
    }

    public void AgregandoViajeBD(){

    }

    //Metodo creado para insertar un edittext y un boton
    public void CrearEditText() {
        if (controlDeEditTexts <= 4) { //Solo se permiten maximo 5 botones
            if (controlDeEditTexts == 0) { //Se hace visible el textview si se agrega un elemento
                acompañantesText.setVisibility(View.VISIBLE);
                mensajeEdit.setVisibility(View.VISIBLE);
            }
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.instanciar_pal, null);
            //Añadimos el boton y el edittext
            linearLayout.addView(rowView, linearLayout.getChildCount() - 1);
            controlDeEditTexts++;
            acompañanteEdit= rowView.findViewById(R.id.instanciar_pal_amigo_edittext);
            acompañantesEditList.add(acompañanteEdit); //Agregará los edittexts para poderlos validar luego
            Button eliminarBtn = rowView.findViewById(R.id.instanciar_pal_eliminar_button);
            eliminarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearLayout.removeView((View) v.getParent());
                    controlDeEditTexts--;
                    acompañantesEditList.remove(v.getParent()); //Removemos del arreglo de editlist el edittext eliminado
                    if (controlDeEditTexts == 0) {
                        acompañantesText.setVisibility(View.GONE); //Se hace invisible el textview si ya no quedan elementos
                        mensajeEdit.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}
