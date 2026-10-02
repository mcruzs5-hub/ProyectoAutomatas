
package com.mycompany.proyectoatomatas.view;

import com.mycompany.proyectoatomatas.model.Automata;
import com.mycompany.proyectoatomatas.algorithm.ConversorRegex;
import java.awt.BorderLayout;
import java.awt.Dimension;
import com.mycompany.proyectoatomatas.ProyectoAtomatas;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import com.mycompany.proyectoatomatas.algorithm.SimuladorAutomata;
import com.mycompany.proyectoatomatas.model.Cadena;
import com.mycompany.proyectoatomatas.model.Estado;

import java.util.List;

import javax.swing.Timer;

public class VentanaPrincipal extends JFrame {

    // MODELO MATEMÁTICO
    private final Automata automata;

    // LIENZO GRÁFICO
    private PanelAutomata panelAutomata;

    // COMPONENTES
    private JTextField txtCadena;
    
    private JLabel lblResultado;
    private JButton btnAgregarEstado;
    private JButton btnSeleccionar;
    private JButton btnTransicion;
    private JButton btnInicial;
    private JButton btnFinal;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnSimular;
    private JButton btnConvertirRegex;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public VentanaPrincipal() {

        automata = new Automata();

        setTitle(
        "Simulador de Autómatas - AFD a Regex | Versión "
        + ProyectoAtomatas.VERSION);

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
        btnConvertirRegex = new JButton("Convertir a Regex");

        panelSuperior.add(btnAgregarEstado);
        panelSuperior.add(btnSeleccionar);
        panelSuperior.add(btnTransicion);
        panelSuperior.add(btnInicial);
        panelSuperior.add(btnFinal);
        panelSuperior.add(btnEditar);
        panelSuperior.add(btnEliminar);
        panelSuperior.add(btnConvertirRegex);

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
        // =================================================
// BOTÓN CONVERTIR A REGEX
// =================================================

btnConvertirRegex.addActionListener(e -> {

    panelAutomata.activarModoSeleccion();

    restaurarBotones();

    // Validar estado inicial
    if (automata.getEstadoInicial() == null) {

        JOptionPane.showMessageDialog(
                this,
                "Debes definir un estado inicial.",
                "Conversión a Regex",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    // Validar estados finales
    if (automata.getEstadosAceptacion().isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Debes definir al menos un estado final.",
                "Conversión a Regex",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    try {

        ConversorRegex conversor =
                new ConversorRegex(
                        automata
                );

        ConversorRegex.ResultadoConversion resultado =
                conversor.convertir();

        VentanaConversionRegex ventana =
                new VentanaConversionRegex(
                        resultado
                );

        ventana.setVisible(true);

    } catch (Exception ex) {

        JOptionPane.showMessageDialog(
                this,
                "No fue posible convertir el autómata.\n"
                + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    });
        btnSimular = new JButton("Simular");
        lblResultado = new JLabel("Resultado: ---");
        JLabel lblVersion = new JLabel("Versión: " + ProyectoAtomatas.VERSION);
        panelInferior.add(lblCadena);
        panelInferior.add(txtCadena);
        panelInferior.add(btnSimular);
        panelInferior.add(lblResultado);
        panelInferior.add(lblVersion);

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

        //Botón Simular }
        btnSimular.addActionListener(e -> {

    // Salir del modo agregar/transición
    panelAutomata.activarModoSeleccion();

    // Obtener texto escrito
    Cadena cadena =
            new Cadena(txtCadena.getText());

    // Validar estado inicial
    if (automata.getEstadoInicial() == null) {

        JOptionPane.showMessageDialog(
                this,
                "Debes marcar un estado inicial.",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );

        return;
    }

    // Crear simulador
    SimuladorAutomata simulador =
            new SimuladorAutomata(automata);

    // Obtener recorrido
    List<SimuladorAutomata.Paso> pasos =
            simulador.simular(cadena);

    // Si no existe alguna transición necesaria
    if (pasos == null) {

        panelAutomata.limpiarResaltado();

        lblResultado.setText(
                "Resultado: RECHAZADA"
        );

        JOptionPane.showMessageDialog(
                this,
                "Cadena RECHAZADA.\n"
                + "No existe una transición válida "
                + "para alguno de los símbolos.",
                "Resultado",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    // Mostrar estado inicial primero
    panelAutomata.resaltarEstado(
            automata.getEstadoInicial()
    );

    final int[] posicion = {0};

    // 1 segundo entre cada paso
    Timer timer = new Timer(1000, null);

    timer.addActionListener(evento -> {

        // Todavía quedan transiciones
        if (posicion[0] < pasos.size()) {

            SimuladorAutomata.Paso paso =
                    pasos.get(posicion[0]);

            panelAutomata.resaltarPaso(
                    paso.getDestino(),
                    paso.getTransicion()
            );

            posicion[0]++;

            return;
        }

        // Terminó la cadena
        timer.stop();

        Estado estadoFinal =
                simulador.obtenerEstadoFinal(
                        cadena,
                        pasos
                );

        boolean aceptada =
                simulador.esAceptada(
                        estadoFinal
                );

        if (aceptada) {

            lblResultado.setText(
                    "Resultado: ACEPTADA"
            );

            JOptionPane.showMessageDialog(
                    this,
                    "La cadena fue ACEPTADA.",
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {

            lblResultado.setText(
                    "Resultado: RECHAZADA"
            );

            JOptionPane.showMessageDialog(
                    this,
                    "La cadena fue RECHAZADA.",
                    "Resultado",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    });

    // Primero muestra q0 durante 1 segundo
    timer.setInitialDelay(1000);

    timer.start();
});
        
        
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
