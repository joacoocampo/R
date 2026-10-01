package controlador;

import modelo.EscaneoService;
import modelo.IPUtils;
import modelo.ModeloResultados;
import modelo.ResultadoEscaneo;
import modelo.ValidadorIP;
import vista.VentanaPrincipal;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class EscaneoController {

    private final VentanaPrincipal vista;
    private final ModeloResultados modeloTabla;
    private final EscaneoService servicioEscaneo;

    private boolean mostrandoSoloActivos = false;
    private EscaneoWorker workerActual;

    public EscaneoController(VentanaPrincipal vista, ModeloResultados modeloTabla,
                              EscaneoService servicioEscaneo) {
        this.vista = vista;
        this.modeloTabla = modeloTabla;
        this.servicioEscaneo = servicioEscaneo;
        inicializar();
    }

    private void inicializar() {
        configurarValidacionEnVivo();
        vista.getBotonIniciar().addActionListener(e -> iniciarEscaneo());
        vista.getBotonDetener().addActionListener(e -> detenerEscaneo());
        vista.getBotonLimpiar().addActionListener(e -> limpiarTodo());
        vista.getBotonGuardar().addActionListener(e -> guardarResultados());
        vista.getBotonFiltrar().addActionListener(e -> alternarFiltro());
        vista.actualizarEstadoBotones(false);
    }


    private void configurarValidacionEnVivo() {
        DocumentListener validador = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validar(); }
            public void removeUpdate(DocumentEvent e) { validar(); }
            public void changedUpdate(DocumentEvent e) { validar(); }
            private void validar() {
                vista.marcarValidezCampo(vista.getCampoIpInicio(),
                        ValidadorIP.esIpParcialValida(vista.getCampoIpInicio().getText()));
                vista.marcarValidezCampo(vista.getCampoIpFin(),
                        ValidadorIP.esIpParcialValida(vista.getCampoIpFin().getText()));
            }
        };
        vista.getCampoIpInicio().getDocument().addDocumentListener(validador);
        vista.getCampoIpFin().getDocument().addDocumentListener(validador);
    }



    private void iniciarEscaneo() {
        String ipInicioTxt = vista.getCampoIpInicio().getText().trim();
        String ipFinTxt = vista.getCampoIpFin().getText().trim();

        if (!ValidadorIP.esIpCompletaValida(ipInicioTxt) || !ValidadorIP.esIpCompletaValida(ipFinTxt)) {
            vista.mostrarError("Las direcciones IP de inicio y fin deben tener un formato valido,\n" +
                    "por ejemplo: 192.168.1.1");
            return;
        }

        long ipInicioLong = IPUtils.ipATexto(ipInicioTxt);
        long ipFinLong = IPUtils.ipATexto(ipFinTxt);

        if (ipInicioLong > ipFinLong) {
            vista.mostrarError("La IP de inicio debe ser menor o igual que la IP de fin.");
            return;
        }

        long totalDirecciones = ipFinLong - ipInicioLong + 1;
        if (totalDirecciones > 5000) {
            int opcion = vista.mostrarConfirmacion(
                    "El rango contiene " + totalDirecciones + " direcciones y puede tardar mucho tiempo.\n" +
                            "¿Desea continuar de todos modos?",
                    "Rango muy amplio");
            if (opcion != JOptionPane.YES_OPTION) return;
        }

        int timeoutMs;
        int reintentos;
        try {
            timeoutMs = Integer.parseInt(vista.getCampoTimeout().getText().trim());
            if (timeoutMs <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            vista.mostrarError("El tiempo de espera debe ser un numero entero positivo (en milisegundos).");
            return;
        }
        try {
            reintentos = Integer.parseInt(vista.getCampoReintentos().getText().trim());
            if (reintentos < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            vista.mostrarError("El numero de reintentos debe ser un numero entero mayor o igual a 0.");
            return;
        }

        modeloTabla.limpiar();
        vista.actualizarContadorActivos(0);
        vista.actualizarProgreso(0);
        vista.establecerTextoProgreso("Escaneando...");
        vista.actualizarEstadoBotones(true);

        workerActual = new EscaneoWorker(ipInicioTxt, ipFinTxt, timeoutMs, reintentos);
        workerActual.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                vista.actualizarProgreso((Integer) evt.getNewValue());
            }
        });
        workerActual.execute();
    }

    private void detenerEscaneo() {
        if (workerActual != null && !workerActual.isDone()) {
            workerActual.cancel(true);
            vista.establecerTextoProgreso("Deteniendo...");
        }
    }

    private void limpiarTodo() {
        if (workerActual != null && !workerActual.isDone()) {
            detenerEscaneo();
        }
        modeloTabla.limpiar();
        vista.actualizarContadorActivos(0);
        vista.actualizarProgreso(0);
        vista.establecerTextoProgreso("Listo");
        vista.actualizarEstadoBotones(false);
    }

    private void guardarResultados() {
        if (modeloTabla.getRowCount() == 0) {
            vista.mostrarInfo("No hay resultados para guardar.", "Sin datos");
            return;
        }
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar resultados");
        selector.setSelectedFile(new File("resultados_escaneo.csv"));
        int resultado = selector.showSaveDialog(vista);
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        File archivo = selector.getSelectedFile();
        try (PrintWriter escritor = new PrintWriter(new FileWriter(archivo))) {
            escritor.println("IP,Nombre equipo,Activo,Tiempo (ms)");
            for (ResultadoEscaneo r : modeloTabla.getFilas()) {
                escritor.printf("%s,%s,%s,%s%n", r.getIp(), r.getNombreEquipo(),
                        r.isActivo() ? "Si" : "No", r.getTiempoMs());
            }
            vista.mostrarInfo("Resultados guardados correctamente en:\n" + archivo.getAbsolutePath(),
                    "Guardado exitoso");
        } catch (IOException ex) {
            vista.mostrarError("No se pudo guardar el archivo:\n" + ex.getMessage());
        }
    }

    private void alternarFiltro() {
        mostrandoSoloActivos = !mostrandoSoloActivos;
        if (mostrandoSoloActivos) {
            vista.getOrdenador().setRowFilter(RowFilter.regexFilter("true", 2));
            vista.getBotonFiltrar().setText("Mostrar todos");
        } else {
            vista.getOrdenador().setRowFilter(null);
            vista.getBotonFiltrar().setText("Mostrar solo activos");
        }
    }



    private class EscaneoWorker extends SwingWorker<Void, ResultadoEscaneo> {
        private final long inicioLong;
        private final long finLong;
        private final long total;
        private final int timeoutMs;
        private final int reintentos;
        private long procesadas = 0;

        EscaneoWorker(String ipInicio, String ipFin, int timeoutMs, int reintentos) {
            this.inicioLong = IPUtils.ipATexto(ipInicio);
            this.finLong = IPUtils.ipATexto(ipFin);
            this.total = finLong - inicioLong + 1;
            this.timeoutMs = timeoutMs;
            this.reintentos = reintentos;
        }

        @Override
        protected Void doInBackground() {
            for (long actual = inicioLong; actual <= finLong; actual++) {
                if (isCancelled()) break;
                String ip = IPUtils.longAIp(actual);
                ResultadoEscaneo resultado = servicioEscaneo.escanearDireccion(
                        ip, timeoutMs, reintentos, this::isCancelled);
                publish(resultado);
                procesadas++;
                setProgress((int) Math.min(100, (procesadas * 100) / total));
            }
            return null;
        }

        @Override
        protected void process(List<ResultadoEscaneo> chunks) {
            for (ResultadoEscaneo r : chunks) {
                modeloTabla.agregarResultado(r);
            }
            vista.actualizarContadorActivos(modeloTabla.contarActivos());
        }

        @Override
        protected void done() {
            vista.actualizarEstadoBotones(false);
            if (isCancelled()) {
                vista.establecerTextoProgreso("Escaneo detenido");
            } else {
                vista.actualizarProgreso(100);
                vista.establecerTextoProgreso("Escaneo finalizado");
            }
            vista.actualizarContadorActivos(modeloTabla.contarActivos());
        }
    }
}
