
package com.mycompany.proyectoatomatas;

import com.mycompany.proyectoatomatas.view.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class ProyectoAtomatas {

    // Versión actual del proyecto
    public static final String VERSION = "2.01.10.2026";

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            VentanaPrincipal ventana = new VentanaPrincipal();

            ventana.setVisible(true);
        });
    }
}