
package com.mycompany.proyectoatomatas.view;

import com.mycompany.proyectoatomatas.model.Automata;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class VentanaPrincipal extends JFrame {

    // MODELO MATEMÁTICO
    private final Automata automata;

    // LIENZO GRÁFICO
    private PanelAutomata panelAutomata;

    // COMPONENTES
    private JTextField txtCadena;

    private JButton btnAgregarEstado;
    private JButton btnSeleccionar;
    private JButton btnTransicion;
    private JButton btnInicial;
    private JButton btnFinal;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnSimular;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public VentanaPrincipal() {

        automata = new Automata();

        setTitle("Simulador de Autómatas - AFD a Regex");

        setSize(1100, 700);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        crearInterfaz();
    }

    // =====================================================
    // CREACIÓN DE LA INTERFAZ
    // =====================================================

    private void crearInterfaz() {

        setLayout(new BorderLayout());

        // PANEL SUPERIOR

        JPanel panelSuperior = new JPanel();

        btnAgregarEstado = new JButton("Agregar estado");
        btnSeleccionar = new JButton("Seleccionar / Mover");
        btnTransicion = new JButton("Transición");
        btnInicial = new JButton("Inicial");
        btnFinal = new JButton("Final");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        panelSuperior.add(btnAgregarEstado);
        panelSuperior.add(btnSeleccionar);
        panelSuperior.add(btnTransicion);
        panelSuperior.add(btnInicial);
        panelSuperior.add(btnFinal);
        panelSuperior.add(btnEditar);
        panelSuperior.add(btnEliminar);

        add(panelSuperior, BorderLayout.NORTH);

        // LIENZO CENTRAL JGRAPHX

        panelAutomata = new PanelAutomata(automata);

        add(panelAutomata, BorderLayout.CENTER);

        // PANEL INFERIOR

        JPanel panelInferior = new JPanel();

        JLabel lblCadena = new JLabel("Cadena:");

        txtCadena = new JTextField();

        txtCadena.setPreferredSize(
                new Dimension(250, 30)
        );

        btnSimular = new JButton("Simular");

        panelInferior.add(lblCadena);
        panelInferior.add(txtCadena);
        panelInferior.add(btnSimular);

        add(panelInferior, BorderLayout.SOUTH);

        // =================================================
        // BOTÓN AGREGAR ESTADOS
        // =================================================

        btnAgregarEstado.addActionListener(e -> {

            panelAutomata.activarModoAgregar(true);

            restaurarBotones();

            btnAgregarEstado.setText("Agregando...");
        });

        // =================================================
        // BOTÓN SELECCIONAR Y MOVER
        // =================================================

        btnSeleccionar.addActionListener(e -> {

            panelAutomata.activarModoSeleccion();

            restaurarBotones();
        });

        // =================================================
        // BOTÓN CREAR TRANSICIONES
        // =================================================

        btnTransicion.addActionListener(e -> {

            panelAutomata.activarModoTransicion(true);

            restaurarBotones();

            btnTransicion.setText("Conectando...");

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona el estado de origen y "
                    + "después el estado de destino.",
                    "Crear transición",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // =================================================
        // BOTÓN ESTADO INICIAL
        // =================================================

        btnInicial.addActionListener(e -> {

            panelAutomata.activarModoSeleccion();

            restaurarBotones();

            boolean resultado = panelAutomata.marcarInicial();

            if (!resultado) {

                mostrarAviso();
            }
        });

        // =================================================
        // BOTÓN ESTADO FINAL
        // =================================================

        btnFinal.addActionListener(e -> {

            panelAutomata.activarModoSeleccion();

            restaurarBotones();

            boolean resultado = panelAutomata.marcarFinal();

            if (!resultado) {

                mostrarAviso();
            }
        });

        // =================================================
        // NUEVO: BOTÓN EDITAR
        // =================================================

        btnEditar.addActionListener(e -> {

            // Activar modo selección
            panelAutomata.activarModoSeleccion();

            restaurarBotones();

            // Editar estado o transición seleccionada
            boolean resultado =
                    panelAutomata.editarSeleccionado();

            if (!resultado) {

                mostrarAviso();
            }
        });

        // =================================================
        // NUEVO: BOTÓN ELIMINAR
        // =================================================

        btnEliminar.addActionListener(e -> {

            panelAutomata.activarModoSeleccion();

            restaurarBotones();

            // Verificar que exista una selección
            if (panelAutomata.getGraph()
                    .getSelectionCount() == 0) {

                mostrarAviso();
                return;
            }

            // Solicitar confirmación
            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseas eliminar el elemento seleccionado?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (respuesta == JOptionPane.YES_OPTION) {

                panelAutomata.eliminarSeleccionado();
            }
        });

        // El botón Simular se programará
        // en la siguiente etapa.
    }

    // =====================================================
    // RESTAURAR TEXTOS DE LOS BOTONES
    // =====================================================

    private void restaurarBotones() {

        btnAgregarEstado.setText("Agregar estado");

        btnTransicion.setText("Transición");
    }

    // =====================================================
    // MOSTRAR AVISO
    // =====================================================

    private void mostrarAviso() {

        JOptionPane.showMessageDialog(
                this,
                "Primero selecciona un estado o transición.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
