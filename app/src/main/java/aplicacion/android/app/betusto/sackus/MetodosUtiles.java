package aplicacion.android.app.betusto.sackus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MetodosUtiles {

    //Elimina emojis
    public InputFilter filters(){
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    int type = Character.getType(source.charAt(i));
                    //System.out.println("Type : " + type);
                    if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                        return "";
                    }
                }
                return null;
            }
        };
        return filter;
    }


    //Limites de digitos y decimales
    public InputFilter limite = new InputFilter() {
        final int maxDigitsBeforeDecimalPoint=10;
        final int maxDigitsAfterDecimalPoint=2;

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            StringBuilder builder = new StringBuilder(dest);
            builder.replace(dstart, dend, source
                    .subSequence(start, end).toString());
            if (!builder.toString().replace("$", "").matches( //el replace es para que no tome en cuenta el signo de peso
                    "(([1-9]{1})([0-9]{0,"+(maxDigitsBeforeDecimalPoint-1)+"})?)?(\\.[0-9]{0,"+maxDigitsAfterDecimalPoint+"})?"

            )) {
                if(source.length()==0)
                    return dest.subSequence(dstart, dend);
                return "";
            }

            return null;

        }
    };

    //Metodo para subrayar un textview por medio segundo
    public void Subrayar(final TextView text){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                text.setText(text.getResources().getString(R.string.No_Subrayado)); //termina de subrayar
            }
        }, 500);
    }


    private Toast ToastMessage;
   public void MostrarToast(Context context, String Texto){
       //Para no repetir toasts
       if (ToastMessage != null) {
           ToastMessage.cancel();
       }
           ToastMessage = Toast.makeText(context, Texto, Toast.LENGTH_LONG);
           ToastMessage.show();

   }

   private boolean estaVisible = false;
    public void OjosContraseñas(EditText contraseñaTexto, ImageButton VisibilidadOjos){
        if(estaVisible){ //Los caracteres se veran
            contraseñaTexto.setTransformationMethod(PasswordTransformationMethod.getInstance());
            contraseñaTexto.setSelection(contraseñaTexto.getText().length()); //Necesario para que el cursor se ponga hasta el final del texto
            VisibilidadOjos.setImageResource(R.mipmap.ic_notvisible); //Agregamos la imagen
            estaVisible = false; //La proxima vez que se le oprima hará el efecto contrario
        }else{ //Los caracteres permaneceran ocultos
            contraseñaTexto.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            contraseñaTexto.setSelection(contraseñaTexto.getText().length()); //Necesario para que el cursor se ponga hasta el final del texto
            VisibilidadOjos.setImageResource(R.mipmap.ic_visible); //Agregamos la imagen
            estaVisible = true; //La proxima vez que se le oprima hará el efecto contrario
        }
    }

    public void OjosContraseñasOnResume(EditText contraseñaTexto, ImageButton VisibilidadOjos){
        if(estaVisible){
            contraseñaTexto.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            contraseñaTexto.setSelection(contraseñaTexto.getText().length()); //Necesario para que el cursor se ponga hasta el final del texto
            VisibilidadOjos.setImageResource(R.mipmap.ic_visible); //Agregamos la imagen
        }else{
            contraseñaTexto.setTransformationMethod(PasswordTransformationMethod.getInstance());
            contraseñaTexto.setSelection(contraseñaTexto.getText().length()); //Necesario para que el cursor se ponga hasta el final del texto
            VisibilidadOjos.setImageResource(R.mipmap.ic_notvisible); //Agregamos la imagen
        }
    }

    //Los sigueintes dos metodos son necesarios cuando tienes multiples botones con ojos
    public void OjosContraseñasOnResumeMultiples(EditText contraseñaTexto, ImageButton VisibilidadOjos, boolean visible){
        if(visible){
            contraseñaTexto.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            contraseñaTexto.setSelection(contraseñaTexto.getText().length()); //Necesario para que el cursor se ponga hasta el final del texto
            VisibilidadOjos.setImageResource(R.mipmap.ic_visible); //Agregamos la imagen
        }else{
            contraseñaTexto.setTransformationMethod(PasswordTransformationMethod.getInstance());
            contraseñaTexto.setSelection(contraseñaTexto.getText().length()); //Necesario para que el cursor se ponga hasta el final del texto
            VisibilidadOjos.setImageResource(R.mipmap.ic_notvisible); //Agregamos la imagen
        }
    }
    public boolean OjosContraseñasMultiples(EditText contraseñaTexto, ImageButton VisibilidadOjos, boolean visible){
        if(visible){ //Los caracteres se veran
            contraseñaTexto.setTransformationMethod(PasswordTransformationMethod.getInstance());
            contraseñaTexto.setSelection(contraseñaTexto.getText().length()); //Necesario para que el cursor se ponga hasta el final del texto
            VisibilidadOjos.setImageResource(R.mipmap.ic_notvisible); //Agregamos la imagen
            visible = false;
            return visible;
        }else{ //Los caracteres permaneceran ocultos
            contraseñaTexto.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            contraseñaTexto.setSelection(contraseñaTexto.getText().length()); //Necesario para que el cursor se ponga hasta el final del texto
            VisibilidadOjos.setImageResource(R.mipmap.ic_visible); //Agregamos la imagen
            visible = true; //La proxima vez que se le oprima hará el efecto contrario
            return visible;
        }
    }

    //Metodo que nos ayuda tener una cierta cantidad de lineas de texto en nuestros edittext
    public void LimitarLineasEditText(final EditText editText, final int LineasMaximas){
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            //Se ejecuta cada que se presione una tecla e impide que puedas escribir algo mas
            @Override
            public void afterTextChanged(Editable s) {
                if(editText.getLayout() != null) { //Para evitar que crashee
                    if (editText.getLayout().getLineCount() > LineasMaximas) {
                        editText.setText(editText.getText().delete(editText.getText().length() - 1, editText.getText().length()));
                        editText.setSelection(editText.getText().length());

                    }
                }
                //editText.getText().insert(editText.getSelectionStart(), "");
                //if (editText.getSelectionStart() == editText.getText().length()) {
                //}
            }
        });
    }

    //Metodo para poner el simbolo de dinero y tener maximo 10 digitos con 2 decimales
    public void EstiloDinero(final EditText editText){
        //Constante simbolo de peso
        Selection.setSelection(editText.getText(), editText.getText().length());

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("$")){
                    editText.setText("$"+s.toString());
                    Selection.setSelection(editText.getText(), editText.getText().length());

                }

            }
        });

        //Elimina la posibiliad de poder poner mas de dos decimales y mas de 10 digitos de precio
        editText.setFilters(new InputFilter[] {new MetodosUtiles().limite});
    }

    //Metodo para conseguir la fecha actual
    public static String fechaHora(long time) {
        DateFormat format = new SimpleDateFormat("EEE, dd 'de' MMM 'del' yyyy 'a las' hh:mm aaa");
        return format.format(new Date(time));
    }

    public void MostrarDatePicker(Context context, final TextView textView, final String complemento){
        //Conseguir fecha actual completa
        Calendar calendario = Calendar.getInstance();
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH);
        int año = calendario.get(Calendar.YEAR);
        //DateFormat.getInstance(DateFormat.DAY_OF_WEEK_IN_MONTH_FIELD).format(calendario.getTime());
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Formato completo de la fecha

                //String fecha = DateFormat.getDateInstance(DateFormat.FULL).format(calendario.getTime());
                String fecha = dayOfMonth + "/" + (month + 1)  + "/" + year;
                textView.setText(complemento + fecha+"\n"); //Escribe la fecha
                // fechaInicioText.setText("Fecha de inicio:\n"+fecha); //Escribe la fecha
            }
        }, año, mes, dia);
        datePickerDialog.show();
    }
}


//Clase para detectar si hay internet
class DetectaConexion{
    Context context;

    public DetectaConexion(Context context){
        this.context = context;
    }

    public boolean isConnected(){
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivity != null){
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if(info != null){
                if(info.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }


    //Variables para revisar la conexion a internet
    Handler h = new Handler();
    Runnable runnable;
    public void ConexionPorSegundos(final ImageButton nowifibutton){
        h.postDelayed( runnable = new Runnable() {
            public void run() {
                if(isConnected()) {
                    nowifibutton.setEnabled(false);
                    nowifibutton.setVisibility(View.GONE);
                }else{
                    nowifibutton.setEnabled(true);
                    nowifibutton.setVisibility(View.VISIBLE);
                }

                h.postDelayed(runnable, 500);
            }
        }, 500);
    }

    public void DetenerContador(){
        h.removeCallbacks(runnable);
    }

    //Solo se ejecuta una vez
    public void startConexion(ImageButton nowifibutton){
        if(isConnected()){
            nowifibutton.setEnabled(false);
            nowifibutton.setVisibility(View.GONE);
        }else{
            nowifibutton.setEnabled(true);
            nowifibutton.setVisibility(View.VISIBLE);
        }
    }

    //Mensaje al oprimir el boton de no internet
    public void mensajeNoInternet(Context Activity){
        //Dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
        builder.setTitle("No hay conexión a internet");
        builder.setMessage("Es posible que"+" " + "algunas opciones no funcionen correctamente. Por seguridad de datos mantenga la aplicación abierta u oculta hasta"+" " +
                        "que vuelva a entrar la conexión.");
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.show();

        //Align del dialogo
        TextView messageView = dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.FILL);
    }
}

//clase que se encarga de manejar las variables estaticas
class VariablesEstaticas extends  android.app.Application{
    //Persistencia de datos
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CURRENT_USER_UID = "currentUserUID";
    public static final String IS_LOGGED = "isLogged";
    //Cantidad total que puede tener el usuario
    public static final double MAXIMO = 9999999999.99;

    public static String CurrentUserUID; //Valor del current user UID //VARIABLE QUE DEBE GUARDARSE HASTA CUANDO SE CIERRA LA APP
    public static boolean isLoged = false; //VARIABLE QUE DEBE GUARDARSE HASTA CUANDO SE CIERRA LA APP
    //Listas para guardar los valores temporales para luego ser almacenadas a la base de datos y al auth cuando vuelva internet
    //static para que su valor se guarde, y final para que la lista no cambie, lo que contiene si puede cambiar
    public String UID = UUID.randomUUID().toString().replace("-", ""); //Generador
    //Variable estatica que tomará un control del uid que se vaya a guardar en la base de datos local
    public static boolean Locked = false;
    //Metodo que nos ayudara a guardar las variables desde la memoria del telefono
    public void GuardarDatos(SharedPreferences sharedPreferences, boolean estado, String uid){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(VariablesEstaticas.IS_LOGGED, estado);
        editor.putString(VariablesEstaticas.CURRENT_USER_UID, uid);
        editor.apply();
    }

    //Metodo que nos ayudara a cargar las variables desde la memoria del telefono
    public void CargarDatos(SharedPreferences sharedPreferences){
        VariablesEstaticas.CurrentUserUID = sharedPreferences.getString(VariablesEstaticas.CURRENT_USER_UID, "");
        VariablesEstaticas.isLoged = sharedPreferences.getBoolean(VariablesEstaticas.IS_LOGGED, false);
        Log.e("TEST","CurrentUser "+VariablesEstaticas.CurrentUserUID + " isLoged "+VariablesEstaticas.isLoged);
    }


}

class BaseDeDatos{
    private static DecimalFormat df2 = new DecimalFormat("0.00"); //Para usar solo dos decimales
    VariablesEstaticas VE = new VariablesEstaticas();
    //Metodo encargado de mostrar algun elemento de la base de datos relacionado al usuario actual
    public void MostrarElementoUsuarioActual(DatabaseReference Database, String ElementoAConseguir, final TextView textView, final SharedPreferences sharedPreferences){
        Log.e("TEST", "CurrentUserUID en Mostrar: "+VariablesEstaticas.CurrentUserUID);
        Database.child(VariablesEstaticas.CurrentUserUID).child(ElementoAConseguir).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if necesario por si ya no obtiene el valor del uid por alguna razon, de esta manera no crasheara
                //Se necesitan volver a cargar los datos porque cada cambio en la base de datos activa este ondatachange
                VE.CargarDatos(sharedPreferences);
                if(VariablesEstaticas.CurrentUserUID != null && !VariablesEstaticas.CurrentUserUID.equals("")) {
                String elemento = dataSnapshot.getValue().toString();
                    textView.setText(elemento);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void MostrarElementoUsuarioActualConComplemento(DatabaseReference Database, String ElementoAConseguir, final String Complemento, final TextView textView, final SharedPreferences sharedPreferences) {
        Database.child(VariablesEstaticas.CurrentUserUID).child(ElementoAConseguir).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if necesario por si ya no obtiene el valor del uid por alguna razon, de esta manera no crasheara
                //Se necesitan volver a cargar los datos porque cada cambio en la base de datos activa este ondatachange
                VE.CargarDatos(sharedPreferences);
                if (VariablesEstaticas.CurrentUserUID != null && !VariablesEstaticas.CurrentUserUID.equals("")) {
                    String elemento = dataSnapshot.getValue().toString();
                    textView.setText(Complemento + elemento);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void MostarElementoEditText(String ElementoPadre, String Estampa, String ElementoAConseguir, final EditText editText, final SharedPreferences sharedPreferences){
        //Referencia para la BD de forma en que podamos meter una tabla dentro de ella
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        //Referencia a la tabla del child:
        final DatabaseReference TablaHija = database.child(VariablesEstaticas.CurrentUserUID);
        TablaHija.child(ElementoPadre).child(Estampa).child(ElementoAConseguir).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if necesario por si ya no obtiene el valor del uid por alguna razon, de esta manera no crasheara
                //Se necesitan volver a cargar los datos porque cada cambio en la base de datos activa este ondatachange
                VE.CargarDatos(sharedPreferences);
                if(VariablesEstaticas.CurrentUserUID != null && !VariablesEstaticas.CurrentUserUID.equals("")) {
                    if (dataSnapshot.getValue() != null){
                        String elemento = dataSnapshot.getValue().toString();
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

    public void MostrarElementosUsuarioActualConSlash(DatabaseReference Database, String ElementoAConseguir, final String Coplemento, final TextView textView, final SharedPreferences sharedPreferences){
        Database.child(VariablesEstaticas.CurrentUserUID).child(ElementoAConseguir).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if necesario por si ya no obtiene el valor del uid por alguna razon, de esta manera no crasheara
                //Se necesitan volver a cargar los datos porque cada cambio en la base de datos activa este ondatachange
                VE.CargarDatos(sharedPreferences);
                if(VariablesEstaticas.CurrentUserUID != null && !VariablesEstaticas.CurrentUserUID.equals("")) {
                    String resultado = dataSnapshot.getValue().toString();
                    textView.setText(Coplemento+"\n" + resultado);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    ArrayList<Gastos>notas = new ArrayList<>();
    //Metodo para mostrar las listas
    public static ArrayList<Gastos> retornarGastos = new ArrayList<>();
    public static ArrayList<Invitaciones> retornarInviraciones = new ArrayList<>();
    public static ArrayList<Viajes> retornarViajes = new ArrayList<>();
    public void MostrarListasDelUsuario(final String ElementoPadre, final ArrayList<Gastos> notas, final ArrayList<Invitaciones> invitaciones,
                                        final  SharedPreferences sharedPreferences, final RecyclerView recyclerView, final AdaptadorInvitaciones adaptadorInvitaciones,
                                        final Adaptador adaptador
    , final Context mActivity, final ArrayList<Viajes> viajes, final AdaptadorViajes adaptadorViajes){
        retornarGastos.add(new Gastos("","","",""));
        retornarGastos.clear();
        if(notas != null) {
            this.notas.clear();
            this.notas = Adaptador.ReiniciarNotas();
        }
        //Referencia para la BD de forma en que podamos meter una tabla dentro de ella
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        //Referencia a la tabla del child:
        final DatabaseReference TablaHija = database.child(VariablesEstaticas.CurrentUserUID);
        final List CantidadDeSnapshots = new ArrayList();
        TablaHija.child(ElementoPadre).addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                VE.CargarDatos(sharedPreferences);
                //notas.clear();
                //if necesario por si ya no obtiene el valor del uid por alguna razon, de esta manera no crasheara
                //Se necesitan volver a cargar los datos porque cada cambio en la base de datos activa este ondatachange
                if (VariablesEstaticas.CurrentUserUID != null && !VariablesEstaticas.CurrentUserUID.equals("")) {
                    final int CantidadTotalDeSnaps = (int) dataSnapshot.getChildrenCount();
                    if (dataSnapshot.getValue() != null) { //*No se puede usar equals en null aunque sea string*
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) //Recorremos cada campo de la tabla
                        {
                            switch (ElementoPadre) {
                                case "Gastos":
                                        //Conseguimos los valores de la tabla
                                    CantidadDeSnapshots.add(snapshot.child("NombreDelGasto"));
                                    String GastoNota = snapshot.child("NombreDelGasto").getValue().toString();
                                    String Fecha = snapshot.child("Fecha").getValue().toString();
                                    String Precio = snapshot.child("Gasto").getValue().toString();
                                    //Para conseguir su referencia
                                    String TimeStamp = snapshot.child("TimeStamp").getValue().toString();
                                    if(notas != null) {
                                        notas.add(new Gastos(GastoNota, Fecha, Precio, TimeStamp));
                                        if (CantidadDeSnapshots.size() == CantidadTotalDeSnaps) {
                                            //Collections.copy(retornarGastos,notas);
                                            //retornarGastos.addAll(notas); //le pasamos todos los elementos conseguidos
                                            GastoTab2Historial Historial = new GastoTab2Historial();
                                            retornarGastos = (ArrayList<Gastos>) notas.clone();
                                            Log.e("TEST", "CANTIDAD DE NOTAS: " + retornarGastos.size());
                                            CantidadDeSnapshots.clear();
                                            Historial.Adaptando(recyclerView, adaptador, mActivity);
                                        }
                                    }
                                    break;
                                case "Viajes":
                                    //Conseguimos los valores de la tabla
                                    CantidadDeSnapshots.add(snapshot.child("NombreViaje"));
                                    String Viaje = snapshot.child("Viaje").getValue().toString();
                                    String DineroDedicado = snapshot.child("DineroDedicado").getValue().toString();
                                    String FechaInicio = snapshot.child("FechaInicio").getValue().toString();
                                    String FechaRegreso = snapshot.child("FechaRegreso").getValue().toString();
                                    String Compartir = snapshot.child("Compartir").getValue().toString();
                                    //Para conseguir su referencia
                                    String TimeStampViajes = snapshot.child("TimeStamp").getValue().toString();
                                    if(viajes != null) {
                                        viajes.add(new Viajes(Viaje, DineroDedicado, FechaInicio, FechaRegreso, Compartir, TimeStampViajes));
                                        if (CantidadDeSnapshots.size() == CantidadTotalDeSnaps) {
                                            ViajesTab2Mirar Mirar = new ViajesTab2Mirar();
                                            retornarViajes = (ArrayList<Viajes>) viajes.clone();
                                            CantidadDeSnapshots.clear();
                                            Mirar.Adaptando(recyclerView, adaptadorViajes, mActivity);
                                        }
                                    }
                                    break;
                                case "Notas":
                                    break;
                                case "Mensajes":
                                    //Conseguimos los valores de la tabla
                                    CantidadDeSnapshots.add(snapshot.child("NombreViaje"));
                                    String NombreViaje = snapshot.child("NombreViaje").getValue().toString();
                                    String FechaMensajes = snapshot.child("Fecha").getValue().toString();
                                    String Texto = snapshot.child("Texto").getValue().toString();
                                    String EnviadoPor = snapshot.child("UsuarioMando").getValue().toString();
                                    //Para conseguir su referencia
                                    String TimeStampMensajes = snapshot.child("TimeStamp").getValue().toString();
                                    if(invitaciones!=null) {
                                        invitaciones.add(new Invitaciones(NombreViaje, Texto, FechaMensajes, EnviadoPor, TimeStampMensajes));
                                        if (CantidadDeSnapshots.size() == CantidadTotalDeSnaps) {
                                            //Collections.copy(retornarGastos,notas);
                                            //retornarGastos.addAll(notas); //le pasamos todos los elementos conseguidos
                                            MailBoxActivity Mail = new MailBoxActivity();
                                            retornarInviraciones = (ArrayList<Invitaciones>) invitaciones.clone();
                                            Log.e("TEST", "CANTIDAD DE NOTAS: " + retornarInviraciones.size());
                                            CantidadDeSnapshots.clear();
                                            Mail.Adaptando(recyclerView, adaptadorInvitaciones, mActivity);
                                        }
                                    }
                                    break;
                            }
                            //notas.clear();
                        }
                    }else{
                        Log.e("TEST", "DATASNAP ES NULO");
                    }
                }else{
                    Log.e("TEST", "Current user es nulo");
                }
            }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //Toast.makeText(RegisterActivity.this, "Ocurrio un error al intentar acceder a la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void AlAñadirNuevaNota(String NombreDelGasto, String Precio){
        //clearData(); //limpiar textviews
        String ID = GenerarTimeStamp(); //ID unico basado en el tiempo en el que se consiguió
        Adaptador.isPressed = 1; //se presionó
        String fecha = MetodosUtiles.fechaHora(new Date().getTime()); //Fecha actual
        Adaptador.isPressed = 0;
        //Referencia para la BD de forma en que podamos meter una tabla dentro de ella
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        //Referencia a la tabla del child:
        DatabaseReference gastosDB = database.child(VariablesEstaticas.CurrentUserUID);
        //Lo convertimos a dos numeros
        double double00 = Double.parseDouble(Precio.replace("$",""));
        Precio = "$" + df2.format(double00);
        //Guardamos los campos
        GastosGetters g = new GastosGetters(NombreDelGasto, fecha, Precio, ID);
        gastosDB.child("Gastos").child(ID).setValue(g);
    }

    public void AlAñadirNuevaInvitacion(List<String> UIDS, String dondeStr, String Mensaje, String NombreDelUsuario, List<String> UsuarioEscrito){
        String ID = GenerarTimeStamp(); //ID unico basado en el tiempo en el que se consiguió
        AdaptadorInvitaciones.isPressed = 1; //se presionó
        String fecha = MetodosUtiles.fechaHora(new Date().getTime()); //Fecha actual
        AdaptadorInvitaciones.isPressed = 0;
        //Referencia para la BD de forma en que podamos meter una tabla dentro de ella
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        //Referencia a la tabla del child:
        for(int i = 0; i < UIDS.size(); i++) {
            Log.e("test", "entro a añadir");
            Log.e("test","valor timestamp: "+ID);
            DatabaseReference invitacionDB = database.child(UIDS.get(i));
            InvitacionesGetters g = new InvitacionesGetters(dondeStr, NombreDelUsuario,UsuarioEscrito.get(i), fecha, Mensaje, ID);
            invitacionDB.child("Mensajes").child(ID).setValue(g);
        }
    }

    public void AlAñadirNuevoViaje(String dondeStr, String dinerollevasStr, String fechaInicioStr, String fechaRegresoStr, String compartir){
        String ID = GenerarTimeStamp(); //ID unico basado en el tiempo en el que se consiguió
        //Referencia para la BD de forma en que podamos meter una tabla dentro de ella
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        //Referencia a la tabla del child:
        DatabaseReference viajesDB = database.child(VariablesEstaticas.CurrentUserUID);
        //Lo convertimos a dos numeros
        double double00 = Double.parseDouble(dinerollevasStr.replace("$",""));
        dinerollevasStr = "$" + df2.format(double00);
        //Guardamos los campos
        dinerollevasStr = "Dinero dedicado: " + dinerollevasStr;
        fechaInicioStr = "Fecha de inicio: " + fechaInicioStr;
        fechaRegresoStr = "Fecha de regreso: " + fechaRegresoStr;
        ViajesGetters g = new ViajesGetters(dondeStr, dinerollevasStr, fechaInicioStr, fechaRegresoStr, compartir, ID);
        viajesDB.child("Viajes").child(ID).setValue(g);
    }

    public void ActualizarNota(String ElementoPadre, String ElementoHijo, String ValorActualizado, final Gastos gastos){
        //Referencia para la BD de forma en que podamos meter una tabla dentro de ella
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        //Referencia a la tabla del child:
        DatabaseReference nodo = database.child(VariablesEstaticas.CurrentUserUID);
        nodo.child(ElementoPadre).child(gastos.getTimeStamp()).child(ElementoHijo).setValue(ValorActualizado);
    }

    public void EliminarNota(String ElementoPadre, final Gastos gastos){
        //Referencia para la BD de forma en que podamos meter una tabla dentro de ella
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        //Referencia a la tabla del child:
        DatabaseReference nodo = database.child(VariablesEstaticas.CurrentUserUID);
        nodo.child(ElementoPadre).child(gastos.getTimeStamp()).removeValue();
    }


    public int ValidarContraseña(String contraseñaStr, int detectorDeErroresPassword, EditText contraseña, String mensaje){
        if(contraseñaStr.isEmpty()){
            contraseña.setError(mensaje);
            contraseña.requestFocus();
            detectorDeErroresPassword++;
        }else if(contraseñaStr.length() < 6){
            contraseña.setError("La contraseña es muy corta");
            contraseña.requestFocus();
            detectorDeErroresPassword++;
        }else{
            detectorDeErroresPassword = 0;
        }
      return detectorDeErroresPassword;
    }

    public int ValidarCorreo(String correoStr, int detectorDeErroresCorreo, EditText correo, String mensaje){
        if(correoStr.isEmpty()){
            correo.setError(mensaje/*"Correo requerido"*/);
            correo.requestFocus();
            detectorDeErroresCorreo++;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(correoStr).matches()){
            correo.setError("Ingrese un email valido");
            correo.requestFocus();
            detectorDeErroresCorreo++;
        }else{
            detectorDeErroresCorreo = 0;
        }
        return detectorDeErroresCorreo;
    }

    public int ValidarUsuario(String usuarioStr, int detectorDeErroresUsuario, EditText usuario, String mensaje){
        if(usuarioStr.isEmpty()){
            usuario.setError(mensaje);
            usuario.requestFocus();
            detectorDeErroresUsuario++;
        }else{
            detectorDeErroresUsuario = 0;
        }
        return detectorDeErroresUsuario;
    }

    //Metodo encargado de generar una ficha unica para cada gasto/viaje/nota basada en el tiempo
    public String GenerarTimeStamp(){
        Date date= new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        String timeStr = ts+"";
        timeStr = timeStr.replace("."," ");
        return  timeStr;
    }
}