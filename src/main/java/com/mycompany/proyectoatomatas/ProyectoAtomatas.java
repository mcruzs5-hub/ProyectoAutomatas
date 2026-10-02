
package com.mycompany.proyectoatomatas;

import com.mycompany.proyectoatomatas.view.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class ProyectoAtomatas {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            VentanaPrincipal ventana = new VentanaPrincipal();

            ventana.setVisible(true);
        });
    }
}
