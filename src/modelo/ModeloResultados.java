package modelo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModeloResultados extends AbstractTableModel {

    private final String[] columnas = {"IP", "Nombre equipo", "Activo", "Tiempo (ms)"};
    private final List<ResultadoEscaneo> filas = new ArrayList<>();

    public void agregarResultado(ResultadoEscaneo resultado) {
        filas.add(resultado);
        fireTableRowsInserted(filas.size() - 1, filas.size() - 1);
    }

    public void limpiar() {
        int n = filas.size();
        filas.clear();
        if (n > 0) {
            fireTableRowsDeleted(0, n - 1);
        }
    }

    public int contarActivos() {
        int contador = 0;
        for (ResultadoEscaneo r : filas) {
            if (r.isActivo()) contador++;
        }
        return contador;
    }

    
    public List<ResultadoEscaneo> getFilas() {
        return Collections.unmodifiableList(filas);
    }

    @Override
    public int getRowCount() {
        return filas.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int columna) {
        return columnas[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        ResultadoEscaneo r = filas.get(fila);
        switch (columna) {
            case 0: return r.getIp();
            case 1: return r.getNombreEquipo();
            case 2: return r.isActivo();
            case 3: return r.getTiempoMs();
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columna) {
        switch (columna) {
            case 2: return Boolean.class;
            case 3: return Integer.class;
            default: return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }
}
