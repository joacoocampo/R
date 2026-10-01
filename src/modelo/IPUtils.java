package modelo;

/**
 * Funciones utilitarias para convertir direcciones IPv4 entre texto y numero.
 * Representar la IP como un numero (long) permite recorrer un rango con un
 * simple bucle "for" y compararlas con operadores normales (menor, mayor).
 */
public final class IPUtils {

    private IPUtils() {
        // Clase de utilidades: no se instancia.
    }

    public static long ipATexto(String ip) {
        String[] partes = ip.split("\\.");
        long resultado = 0;
        for (String parte : partes) {
            resultado = (resultado << 8) | Long.parseLong(parte);
        }
        return resultado;
    }

    public static String longAIp(long valor) {
        return String.format("%d.%d.%d.%d",
                (valor >> 24) & 0xFF,
                (valor >> 16) & 0xFF,
                (valor >> 8) & 0xFF,
                valor & 0xFF);
    }
}
