import controlador.EscaneoController;
import modelo.EscaneoService;
import modelo.ModeloResultados;
import vista.VentanaPrincipal;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            ModeloResultados modeloTabla = new ModeloResultados();
            EscaneoService servicioEscaneo = new EscaneoService();

            VentanaPrincipal vista = new VentanaPrincipal(modeloTabla);
            new EscaneoController(vista, modeloTabla, servicioEscaneo);

            vista.setVisible(true);
        });
    }
}
