package vista;

import modelo.ModeloResultados;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;


public class VentanaPrincipal extends JFrame {

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

    private final JTable tabla;
    private final TableRowSorter<ModeloResultados> ordenador;

    public VentanaPrincipal(ModeloResultados modeloTabla) {
        super("Escaner de Red");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 520);
        setMinimumSize(new Dimension(620, 400));
        setLocationRelativeTo(null);

        tabla = new JTable(modeloTabla);
        ordenador = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(ordenador);

        setLayout(new BorderLayout(8, 8));
        add(construirPanelSuperior(), BorderLayout.NORTH);
        add(construirPanelTabla(), BorderLayout.CENTER);
        add(construirPanelInferior(), BorderLayout.SOUTH);
    }

    private JPanel construirPanelSuperior() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 4, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] etiquetas = {"IP de inicio:", "IP de fin:", "Tiempo de espera (ms):", "Número de reintentos:"};
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

    public void marcarValidezCampo(JTextField campo, boolean valido) {
        campo.setBackground(valido ? Color.WHITE : new Color(255, 214, 214));
    }

    public void actualizarEstadoBotones(boolean escaneando) {
        botonIniciar.setEnabled(!escaneando);
        botonDetener.setEnabled(escaneando);
        botonGuardar.setEnabled(!escaneando);
        campoIpInicio.setEnabled(!escaneando);
        campoIpFin.setEnabled(!escaneando);
        campoTimeout.setEnabled(!escaneando);
        campoReintentos.setEnabled(!escaneando);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de validacion", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarInfo(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    public int mostrarConfirmacion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    public void actualizarContadorActivos(int cantidad) {
        etiquetaActivos.setText("Equipos activos: " + cantidad);
    }

    public void actualizarProgreso(int valor) {
        barraProgreso.setValue(valor);
    }

    public void establecerTextoProgreso(String texto) {
        barraProgreso.setString(texto);
    }


    public JTextField getCampoIpInicio() { return campoIpInicio; }
    public JTextField getCampoIpFin() { return campoIpFin; }
    public JTextField getCampoTimeout() { return campoTimeout; }
    public JTextField getCampoReintentos() { return campoReintentos; }

    public JButton getBotonIniciar() { return botonIniciar; }
    public JButton getBotonDetener() { return botonDetener; }
    public JButton getBotonLimpiar() { return botonLimpiar; }
    public JButton getBotonGuardar() { return botonGuardar; }
    public JButton getBotonFiltrar() { return botonFiltrar; }

    public JTable getTabla() { return tabla; }
    public TableRowSorter<ModeloResultados> getOrdenador() { return ordenador; }
}
