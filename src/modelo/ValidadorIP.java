package modelo;

import java.util.regex.Pattern;


public final class ValidadorIP {

    private static final Pattern PATRON_IP = Pattern.compile(
            "^(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])(\\." +
            "(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}$");

    private ValidadorIP() {
        
    }

    
    public static boolean esIpCompletaValida(String texto) {
        return PATRON_IP.matcher(texto.trim()).matches();
    }

    
    public static boolean esIpParcialValida(String texto) {
        if (texto.isEmpty()) return true;
        if (!texto.matches("[0-9.]*")) return false;

        String[] partes = texto.split("\\.", -1);
        if (partes.length > 4) return false;

        for (String parte : partes) {
            if (parte.isEmpty()) continue;
            if (parte.length() > 3) return false;
            int valor;
            try {
                valor = Integer.parseInt(parte);
            } catch (NumberFormatException ex) {
                return false;
            }
            if (valor < 0 || valor > 255) return false;
        }
        return true;
    }
}
