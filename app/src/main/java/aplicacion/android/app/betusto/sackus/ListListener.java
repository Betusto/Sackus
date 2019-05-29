package aplicacion.android.app.betusto.sackus;

//Interfaz encargadade marcar los listeners de los listviews
public interface ListListener {
    /**
     * click en la nota.
     *
     * @param gastos: note item
     */
    void onGastoClick(Gastos gastos);

    /**
     * call largo en la nota.
     *
     * @param gastos : item
     */
    void onGastoLongClick(Gastos gastos);
}

interface MensajeListListener{
    /**
     * click en la nota.
     *
     * @param invitaciones: note item
     */
    void onGastoClick(Invitaciones invitaciones);

    /**
     * call largo en la nota.
     *
     * @param invitaciones : item
     */
    void onGastoLongClick(Invitaciones invitaciones);
}

interface ViajeListListener{
    /**
     * click en la nota.
     *
     * @param viajes: note item
     */
    void onGastoClick(Viajes viajes);

    /**
     * call largo en la nota.
     *
     * @param viajes : item
     */
    void onGastoLongClick(Viajes viajes);
}

interface NotasListListener{
    /**
     * click en la nota.
     *
     * @param notas: note item
     */
    void onGastoClick(Notas notas);

    /**
     * call largo en la nota.
     *
     * @param notas : item
     */
    void onGastoLongClick(Notas notas);
}
