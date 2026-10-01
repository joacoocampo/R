package modelo;

/**
 * Representa el resultado del escaneo de UNA direccion IP.
 * Es un objeto de datos simple (POJO): no tiene logica, solo guarda valores.
 */
public class ResultadoEscaneo {

    private final String ip;
    private final String nombreEquipo;
    private final boolean activo;
    private final int tiempoMs;

    public ResultadoEscaneo(String ip, String nombreEquipo, boolean activo, int tiempoMs) {
        this.ip = ip;
        this.nombreEquipo = nombreEquipo;
        this.activo = activo;
        this.tiempoMs = tiempoMs;
    }

    public String getIp() {
        return ip;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public boolean isActivo() {
        return activo;
    }

    public int getTiempoMs() {
        return tiempoMs;
    }
}
