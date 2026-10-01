package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EscaneoService {

    
    public interface EscaneoCancelable {
        boolean estaCancelado();
    }

    
    public ResultadoEscaneo escanearDireccion(String ip, int timeoutMs, int reintentos,
                                               EscaneoCancelable cancelable) {
        boolean activo = false;
        int tiempoMs = -1;

        for (int intento = 0; intento <= reintentos && !activo; intento++) {
            if (cancelable != null && cancelable.estaCancelado()) break;
            try {
                long t0 = System.currentTimeMillis();
                boolean ok = hacerPing(ip, timeoutMs);
                long t1 = System.currentTimeMillis();
                if (ok) {
                    activo = true;
                    tiempoMs = (int) (t1 - t0);
                }
            } catch (Exception ex) {
                
            }
        }

        String nombre = "Desconocido";
        if (activo) {
            try {
                nombre = resolverNombre(ip);
            } catch (Exception ex) {
                nombre = "Desconocido";
            }
        }

        return new ResultadoEscaneo(ip, nombre, activo, tiempoMs);
    }

    
    public boolean hacerPing(String ip, int timeoutMs) throws IOException, InterruptedException {
        String os = System.getProperty("os.name").toLowerCase();
        List<String> comando = new ArrayList<>();
        comando.add("ping");
        if (os.contains("win")) {
            comando.add("-n"); comando.add("1");
            comando.add("-w"); comando.add(String.valueOf(timeoutMs));
        } else {
            comando.add("-c"); comando.add("1");
            int timeoutSeg = Math.max(1, (int) Math.ceil(timeoutMs / 1000.0));
            comando.add("-W"); comando.add(String.valueOf(timeoutSeg));
        }
        comando.add(ip);

        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.redirectErrorStream(true);
        Process proceso = pb.start();

        
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()))) {
            while (lector.readLine() != null) {
                
            }
        }
        boolean termino = proceso.waitFor(timeoutMs + 2000L, TimeUnit.MILLISECONDS);
        if (!termino) {
            proceso.destroyForcibly();
            return false;
        }
        return proceso.exitValue() == 0;
    }

    
    public String resolverNombre(String ip) {
        try {
            ProcessBuilder pb = new ProcessBuilder("nslookup", ip);
            pb.redirectErrorStream(true);
            Process proceso = pb.start();
            String nombreEncontrado = null;
            try (BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    String l = linea.trim();
                    if (l.toLowerCase().startsWith("name:")) {
                        nombreEncontrado = l.substring(5).trim();
                    }
                }
            }
            proceso.waitFor(3000, TimeUnit.MILLISECONDS);
            if (nombreEncontrado != null && !nombreEncontrado.isEmpty()) {
                return nombreEncontrado;
            }
        } catch (Exception ex) {
            
        }
        try {
            String nombre = InetAddress.getByName(ip).getCanonicalHostName();
            return (nombre != null && !nombre.equals(ip)) ? nombre : "Desconocido";
        } catch (UnknownHostException ex) {
            return "Desconocido";
        }
    }
}
