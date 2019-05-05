package aplicacion.android.app.betusto.sackus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MailBoxActivity extends AppCompatActivity implements MensajeListListener {

    private ImageButton nowifibutton;
    //Variable que nos permite vincular con el metodo que se encuentra en MetodosUtiles de VariablesEstaticas para guardar o cargar data
    SharedPreferences sharedPreferences;
    VariablesEstaticas VE = new VariablesEstaticas();
    BaseDeDatos BD = new BaseDeDatos();
    private DatabaseReference Database;
    public android.support.v7.widget.RecyclerView recyclerView;
    public ArrayList<Invitaciones> invitaciones = new ArrayList<>();
    private AdaptadorInvitaciones adaptador;
    MetodosUtiles MU = new MetodosUtiles();
    //MetodosUtiles MandarToast = new MetodosUtiles();
    private Activity mActivity;
    MetodosUtiles MandarToast = new MetodosUtiles();
    private ProgressDialog Progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_box);
        nowifibutton = findViewById(R.id.activity_mail_box_nowifibutton);

        final DetectaConexion CD = new DetectaConexion(this);
        CD.startConexion(nowifibutton);

        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        sharedPreferences = getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences); //Escencial para que no valgan nulos los valores de isLogged y UID

        nowifibutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CD.mensajeNoInternet(MailBoxActivity.this);
            }
        });
        mActivity = MailBoxActivity.this;

        //Aumentamos el margen top del layout para tener espacio para el icono de wifi
      /*  RelativeLayout layout = findViewById(R.id.porfa4);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeParams.setMargins(0, 105, 0, 0);
        layout.setLayoutParams(relativeParams);*/

        //linea view
        LinearLayout linearLayout = findViewById(R.id.activity_mailbox_linea);
        linearLayout.setVisibility(View.GONE);

        //Layout del fondo
        RelativeLayout fondo = findViewById(R.id.activity_fondo);
        fondo.setVisibility(View.VISIBLE);

      /*  //Referenciamos otro layout encargado de poner color amarillo y poner un twxtview
        RelativeLayout banner = findViewById(R.id.activity_mailbox_banner);
        RelativeLayout.LayoutParams bannerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 105);
        //relativeParams.setMargins(0, 205, 0, 0);
        banner.setBackground(ContextCompat.getDrawable(MailBoxActivity.this, R.color.colorHolo));
        //banner.setBackgroundColor(Color.GREEN);
        banner.setGravity(Gravity.CENTER);//Centramos el banner
        TextView text = new TextView(MailBoxActivity.this);//Creamos un textview
        text.setText("MENSAJES");//texto
        text.setGravity(Gravity.CENTER);//lo centramos
        text.setTextColor(Color.WHITE);//color
        text.setTextSize(20);//tamaño
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(0,35,0,0); //coordenadas
        text.setLayoutParams(textParams);
        banner.addView(text);//lo añadimos al relativelayout referenciado
        banner.setLayoutParams(bannerParams);*/

        //Referenciamos al recyclerview y su adaptador
        recyclerView = findViewById(R.id.activity_mailbox_lista);
        //Reverse el recyclerview
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        //GastoTab1Gasto.getInstance().show();
        CargarInvitaciones();
    }

    public void CargarInvitaciones(){
        //MostrarLista
        BD.MostrarListasDelUsuario("Mensajes", null,invitaciones, sharedPreferences, recyclerView, adaptador, null, mActivity, null, null);
    }

    //Por alguna razon, este metodo no tiene los datos actualiados de las variables globales, asi que se le tuvieron que pasar
    //de nuevo
    public void Adaptando(RecyclerView recyclerView, AdaptadorInvitaciones adaptador, Context mActivity){
        this.invitaciones = new ArrayList<>();
        this.mActivity = (Activity) mActivity; //Aparentemente debemos reinicializar las variables si queremos utilizarlas en metodos, y tambien en los metodos de interfaces
        invitaciones = (ArrayList<Invitaciones>)BD.retornarInviraciones.clone();
        //adaptador = new Adaptador(notas, getActivity());
        if(invitaciones != null){
            Log.e("TEST","RETORNAR DATOS NO ES NULO EN ADAPTANDO");
        }
        adaptador = new AdaptadorInvitaciones(invitaciones, mActivity);
        adaptador.setListener(this); //Listeners de los botones de las listas
        if(adaptador != null) {
            recyclerView.setAdapter(adaptador);
        }
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

    @Override
    public void onGastoClick(final Invitaciones invitaciones) {
        //Generamos un alert dialog custom
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mActivity, R.style.BottomOptionsDialogTheme);

        //Es necesario volverlo a reinicializar
        Database = FirebaseDatabase.getInstance().getReference("Usuarios");
        Database.keepSynced(true);
        //Persistencia de variables
        sharedPreferences = this.mActivity.getSharedPreferences(VariablesEstaticas.SHARED_PREFS, Context.MODE_PRIVATE);
        VE.CargarDatos(sharedPreferences);
        final DetectaConexion CD = new DetectaConexion(mActivity);
        View mView = mActivity.getLayoutInflater().inflate(R.layout.activity_responder, null); //Referenciamos el custom layout
        final EditText respuestaEdit = mView.findViewById(R.id.activity_responder_respuesta_edittext);
        Button listoButton =  mView.findViewById(R.id.activity_responder_listobutton);
        TextView dondeText = mView.findViewById(R.id.responder_nombreviaje_text);
        TextView mensajeText = mView.findViewById(R.id.responder_mensaje_text);
        TextView fechaText = mView.findViewById(R.id.responder_fecha_text);
        TextView nombreMandoText = mView.findViewById(R.id.responder_enviadopor_text);
        dondeText.setText(invitaciones.getNoteViaje());
        mensajeText.setText(invitaciones.getNoteMensaje());
        fechaText.setText(invitaciones.getNoteFecha());
        nombreMandoText.setText(invitaciones.getEnviadoPor());
        //Limitar a 2 las lineas que el usuario puede escribir
        MU.LimitarLineasEditText(respuestaEdit, 4);

        //cursor siempre al final
        respuestaEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                respuestaEdit.setSelection(respuestaEdit.getText().length());
            }
        });

        //Eliminar los emojis
        MetodosUtiles EliminaEmoijis = new MetodosUtiles();
        respuestaEdit.setFilters(new InputFilter[]{EliminaEmoijis.filters()});

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
                String respuestaStr = respuestaEdit.getText().toString().trim();
                if(respuestaStr.isEmpty()){
                    respuestaStr="-";
                }
                RevisarUsuario(invitaciones.getEnviadoPor(), respuestaStr, invitaciones.getNoteViaje(), CD);
                dialog.dismiss();
            }
        });
    }

    public void RevisarUsuario(final String usuarioMando, final String respuestaStr, final String Viaje, final DetectaConexion CD){
                        Database.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) //Recorremos cada campo de la tabla Usuarios
                                {
                                    if (snapshot.getValue() != null) { //*No se puede usar equals en null aunque sea string*
                                        Database.child(snapshot.getKey()).child("Usuario").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Log.e("TEST","VALOR DE USUARIOMANDO: "+usuarioMando.substring(12));
                                                if (usuarioMando.substring(12).equals(snapshot.child("Usuario").getValue().toString())) {
                                                    final List<String> UID = new ArrayList<>();
                                                    final List<String> user = new ArrayList<>();
                                                    UID.add(snapshot.getKey());
                                                    user.add(usuarioMando);
                                                    Database.child(VariablesEstaticas.CurrentUserUID).child("Usuario").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if(CD.isConnected()){
                                                                MandarToast.MostrarToast(mActivity,"Mensaje enviado");
                                                            }else{
                                                                MandarToast.MostrarToast(mActivity,"El mensaje se enviará al regresar la conexión");
                                                            }
                                                            BD.AlAñadirNuevaInvitacion(UID, Viaje, respuestaStr, dataSnapshot.getValue().toString(), user);
                                                            UID.clear();
                                                            user.clear();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                //Toast.makeText(RegisterActivity.this, "Ocurrio un error al intentar acceder a la base de datos", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
    }
    @Override
    public void onGastoLongClick(Invitaciones invitaciones) {

    }
}
