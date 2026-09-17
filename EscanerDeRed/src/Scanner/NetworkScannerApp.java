package Scanner;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.*;
import java.util.regex.*;

public class NetworkScannerApp extends JFrame {


    private final JTextField campoIpInicio = new JTextField("192.168.1.1");
    private final JTextField campoIpFin = new JTextField("192.168.1.254");
    private final JTextField campoTimeout = new JTextField("1000");
    private final JTextField campoReintentos = new JTextField("1");

    private final JButton botonIniciar = new JButton("Iniciar escaneo");
    private final JButton botonDetener = new JButton("Detener escaneo");
    private final JButton botonLimpiar = new JButton("Limpiar");
    private final JButton botonGuardar = new JButton("Guardar resultados");
    private final JButton botonFiltrar = new JButton("Mostrar solo activos");

    private final JProgressBar barraProgreso = new JProgressBar(0, 100);
    private final JLabel etiquetaActivos = new JLabel("Equipos activos: 0");

    private final ModeloResultados modeloTabla = new ModeloResultados();
    private final JTable tabla = new JTable(modeloTabla);
    private final TableRowSorter<ModeloResultados> ordenador = new TableRowSorter<>(modeloTabla);

    private boolean mostrandoSoloActivos = false;
    private EscaneoWorker workerActual = null;

    private static final Pattern PATRON_IP = Pattern.compile(
            "^(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])(\\." +
            "(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}$");

    public NetworkScannerApp() {
        super("Escaner de Red");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 520);
        setMinimumSize(new Dimension(620, 400));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(8, 8));
        add(construirPanelSuperior(), BorderLayout.NORTH);
        add(construirPanelTabla(), BorderLayout.CENTER);
        add(construirPanelInferior(), BorderLayout.SOUTH);

        configurarValidacionEnVivo();
        configurarEventosBotones();

        tabla.setRowSorter(ordenador);
        actualizarEstadoBotones(false);
    }



    private JPanel construirPanelSuperior() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 4, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] etiquetas = {"IP de inicio:", "IP de fin:", "Tiempo de espera:", "Número de reintentos:"};
        JTextField[] campos = {campoIpInicio, campoIpFin, campoTimeout, campoReintentos};

        for (int i = 0; i < etiquetas.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            JLabel lbl = new JLabel(etiquetas[i]);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            panel.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 1;
            panel.add(campos[i], gbc);
        }
        return panel;
    }

    private JScrollPane construirPanelTabla() {
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(22);
        tabla.setAutoCreateRowSorter(false);
        tabla.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(2).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(3).setCellRenderer(centrado);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new EmptyBorder(0, 10, 0, 10));
        return scroll;
    }

    private JPanel construirPanelInferior() {
        JPanel contenedor = new JPanel(new BorderLayout(4, 4));
        contenedor.setBorder(new EmptyBorder(4, 10, 10, 10));

        barraProgreso.setStringPainted(true);
        barraProgreso.setString("Listo");
        contenedor.add(barraProgreso, BorderLayout.NORTH);

        etiquetaActivos.setBorder(new EmptyBorder(4, 2, 4, 2));
        contenedor.add(etiquetaActivos, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridLayout(1, 5, 6, 0));
        panelBotones.add(botonIniciar);
        panelBotones.add(botonDetener);
        panelBotones.add(botonLimpiar);
        panelBotones.add(botonGuardar);
        panelBotones.add(botonFiltrar);
        contenedor.add(panelBotones, BorderLayout.SOUTH);

        return contenedor;
    }


    private void configurarValidacionEnVivo() {
        DocumentListener validador = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validar(); }
            public void removeUpdate(DocumentEvent e) { validar(); }
            public void changedUpdate(DocumentEvent e) { validar(); }
            private void validar() {
                marcarValidez(campoIpInicio, esIpParcialValida(campoIpInicio.getText()));
                marcarValidez(campoIpFin, esIpParcialValida(campoIpFin.getText()));
            }
        };
        campoIpInicio.getDocument().addDocumentListener(validador);
        campoIpFin.getDocument().addDocumentListener(validador);
    }

  private boolean esIpParcialValida(String texto) {
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

    private void marcarValidez(JTextField campo, boolean valido) {
        campo.setBackground(valido ? Color.WHITE : new Color(255, 214, 214));
    }

    private boolean esIpCompletaValida(String texto) {
        return PATRON_IP.matcher(texto.trim()).matches();
    }


    private void configurarEventosBotones() {
        botonIniciar.addActionListener(e -> iniciarEscaneo());
        botonDetener.addActionListener(e -> detenerEscaneo());
        botonLimpiar.addActionListener(e -> limpiarTodo());
        botonGuardar.addActionListener(e -> guardarResultados());
        botonFiltrar.addActionListener(e -> alternarFiltro());
    }

    private void iniciarEscaneo() {
        String ipInicioTxt = campoIpInicio.getText().trim();
        String ipFinTxt = campoIpFin.getText().trim();
        if (!esIpCompletaValida(ipInicioTxt) || !esIpCompletaValida(ipFinTxt)) {
            mostrarError("Las direcciones IP de inicio y fin deben tener un formato valido,\n" +
                    "por ejemplo: 192.168.1.1");
            return;
        }

        long ipInicioLong = ipATexto(ipInicioTxt);
        long ipFinLong = ipATexto(ipFinTxt);

        if (ipInicioLong > ipFinLong) {
            mostrarError("La IP de inicio debe ser menor o igual que la IP de fin.");
            return;
        }

        long totalDirecciones = ipFinLong - ipInicioLong + 1;
        if (totalDirecciones > 5000) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "El rango contiene " + totalDirecciones + " direcciones y puede tardar mucho tiempo.\n" +
                            "¿Desea continuar de todos modos?",
                    "Rango muy amplio", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opcion != JOptionPane.YES_OPTION) return;
        }

        int timeoutMs;
        int reintentos;
        try {
            timeoutMs = Integer.parseInt(campoTimeout.getText().trim());
            if (timeoutMs <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            mostrarError("El tiempo de espera debe ser un numero entero positivo (en milisegundos).");
            return;
        }
        try {
            reintentos = Integer.parseInt(campoReintentos.getText().trim());
            if (reintentos < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            mostrarError("El numero de reintentos debe ser un numero entero mayor o igual a 0.");
            return;
        }

        modeloTabla.limpiar();
        etiquetaActivos.setText("Equipos activos: 0");
        barraProgreso.setValue(0);
        barraProgreso.setString("Escaneando...");
        actualizarEstadoBotones(true);

        workerActual = new EscaneoWorker(ipInicioTxt, ipFinTxt, timeoutMs, reintentos);
        workerActual.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                barraProgreso.setValue((Integer) evt.getNewValue());
            }
        });
        workerActual.execute();
    
    }
    private void detenerEscaneo() {
        if (workerActual != null && !workerActual.isDone()) {
            workerActual.cancel(true);
            barraProgreso.setString("Deteniendo...");
        }	
    }

    private void limpiarTodo() {
        if (workerActual != null && !workerActual.isDone()) {
            detenerEscaneo();
        }
        modeloTabla.limpiar();
        etiquetaActivos.setText("Equipos activos: 0");
        barraProgreso.setValue(0);
        barraProgreso.setString("Listo");
        actualizarEstadoBotones(false);
    }

    private void guardarResultados() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay resultados para guardar.",
                    "Sin datos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar resultados");
        selector.setSelectedFile(new File("resultados_escaneo.csv"));
        int resultado = selector.showSaveDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        File archivo = selector.getSelectedFile();
        try (PrintWriter escritor = new PrintWriter(new FileWriter(archivo))) {
            escritor.println("IP,Nombre equipo,Activo,Tiempo (ms)");
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                Object ip = modeloTabla.getValueAt(i, 0);
                Object nombre = modeloTabla.getValueAt(i, 1);
                Object activo = modeloTabla.getValueAt(i, 2);
                Object tiempo = modeloTabla.getValueAt(i, 3);
                escritor.printf("%s,%s,%s,%s%n", ip, nombre,
                        Boolean.TRUE.equals(activo) ? "Si" : "No", tiempo);
            }
            JOptionPane.showMessageDialog(this, "Resultados guardados correctamente en:\n" + archivo.getAbsolutePath(),
                    "Guardado exitoso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            mostrarError("No se pudo guardar el archivo:\n" + ex.getMessage());
        }
    }
    
    private void alternarFiltro() {
        mostrandoSoloActivos = !mostrandoSoloActivos;
        if (mostrandoSoloActivos) {
            ordenador.setRowFilter(RowFilter.regexFilter("true", 2));
            botonFiltrar.setText("Mostrar todos");
        } else {
            ordenador.setRowFilter(null);
            botonFiltrar.setText("Mostrar solo activos");
        }
    }

    private void actualizarEstadoBotones(boolean escaneando) {
        botonIniciar.setEnabled(!escaneando);
        botonDetener.setEnabled(escaneando);
        botonGuardar.setEnabled(!escaneando);
        campoIpInicio.setEnabled(!escaneando);
        campoIpFin.setEnabled(!escaneando);
        campoTimeout.setEnabled(!escaneando);
        campoReintentos.setEnabled(!escaneando);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de validacion", JOptionPane.ERROR_MESSAGE);
    }
}
        