package com.mycompany.proyectoatomatas.view;

import com.mycompany.proyectoatomatas.ProyectoAtomatas;
import com.mycompany.proyectoatomatas.algorithm.ConversorRegex;
import com.mycompany.proyectoatomatas.algorithm.SimuladorAutomata;
import com.mycompany.proyectoatomatas.model.Automata;
import com.mycompany.proyectoatomatas.model.Cadena;
import com.mycompany.proyectoatomatas.model.Estado;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

public class VentanaPrincipal extends JFrame {

    // =====================================================
    // MODELO MATEMÁTICO
    // =====================================================

    private final Automata automata;

    // =====================================================
    // LIENZO GRÁFICO
    // =====================================================

    private PanelAutomata panelAutomata;

    // =====================================================
    // COMPONENTES
    // =====================================================

    private JTextField txtCadena;

    private JLabel lblResultado;

    private JButton btnAgregarEstado;
    private JButton btnSeleccionar;
    private JButton btnTransicion;
    private JButton btnInicial;
    private JButton btnFinal;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnExportar;
    private JButton btnImportar;
    private JButton btnReset;
    private JButton btnSimular;
    private JButton btnConvertirRegex;
    private JButton btnTablaTransicion;

    // =====================================================
    // COLORES
    // =====================================================

    private final Color COLOR_FONDO =
            new Color(241, 245, 249);

    private final Color COLOR_LATERAL =
            new Color(30, 41, 59);

    private final Color COLOR_CUADRO =
            new Color(51, 65, 85);

    private final Color COLOR_TEXTO =
            new Color(15, 23, 42);

    private final Color COLOR_TEXTO_CLARO =
            new Color(248, 250, 252);

    private final Color COLOR_BORDE =
            new Color(148, 163, 184);

    private final Color COLOR_TURQUESA =
            new Color(20, 184, 166);

    private final Color COLOR_AZUL =
            new Color(59, 130, 246);

    private final Color COLOR_MORADO =
            new Color(139, 92, 246);

    private final Color COLOR_NARANJA =
            new Color(249, 115, 22);

    private final Color COLOR_VERDE =
            new Color(34, 197, 94);

    private final Color COLOR_ROJO =
            new Color(239, 68, 68);

    private final Color COLOR_GRIS =
            new Color(100, 116, 139);

    private final Color COLOR_CELESTE =
            new Color(8, 145, 178);

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public VentanaPrincipal() {

        automata =
                new Automata();

        setTitle(
                "Simulador de Autómatas - AFD a Regex | Versión "
                + ProyectoAtomatas.VERSION
        );

        setSize(1200,750) ;
        
        setMinimumSize(
                new Dimension(
                        1000,
                        650
                )
        );
        setExtendedState(
        JFrame.MAXIMIZED_BOTH
        );
        
        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        getContentPane().setBackground(
                COLOR_FONDO
        );

        crearInterfaz();
    }

    // =====================================================
    // CREACIÓN DE LA INTERFAZ
    // =====================================================

    private void crearInterfaz() {

        setLayout(
                new BorderLayout()
        );

        // =================================================
        // CREAR BOTONES
        // =================================================

        btnAgregarEstado =
                new JButton(
                        "Agregar estado"
                );

        btnSeleccionar =
                new JButton(
                        "Seleccionar / Mover"
                );

        btnTransicion =
                new JButton(
                        "Transición"
                );

        btnInicial =
                new JButton(
                        "Inicial"
                );

        btnFinal =
                new JButton(
                        "Final"
                );

        btnEditar =
                new JButton(
                        "Editar"
                );

        btnEliminar =
                new JButton(
                        "Eliminar"
                );
        btnReset =
                new JButton(
                        "Reset"
        );
        btnExportar =
        new JButton(
                "Exportar TXT"
        );

        btnImportar =
        new JButton(
                "Importar TXT"
        );

        btnConvertirRegex =
                new JButton(
                        "Convertir a Regex"
                );

        btnSimular =
                new JButton(
                        "Simular"
                );
        btnTablaTransicion =
        new JButton(
                "Tabla de transición"
        );
       
        
        // =================================================
        // COLORES DE LOS BOTONES
        // =================================================

        estilizarBoton(
                btnAgregarEstado,
                COLOR_TURQUESA
        );
        estilizarBoton(
        btnExportar,
        COLOR_AZUL
        );

        estilizarBoton(
         btnImportar,
        COLOR_TURQUESA
        );

        estilizarBoton(
                btnSeleccionar,
                COLOR_AZUL
        );

        estilizarBoton(
                btnEditar,
                COLOR_GRIS
        );

        estilizarBoton(
                btnEliminar,
                COLOR_ROJO
        );
        
        estilizarBoton(
               btnReset,
               COLOR_CELESTE
        );

        estilizarBoton(
                btnTransicion,
                COLOR_MORADO
        );

        estilizarBoton(
                btnInicial,
                COLOR_NARANJA
        );

        estilizarBoton(
                btnFinal,
                COLOR_VERDE
        );

        estilizarBoton(
                btnConvertirRegex,
                COLOR_CELESTE
        );

        estilizarBoton(
                btnSimular,
                COLOR_TURQUESA
        );
        estilizarBoton(
        btnTablaTransicion,
        COLOR_AZUL
        );

        // =================================================
        // PANEL LATERAL IZQUIERDO
        // =================================================

        JPanel panelLateral =
                new JPanel();

        panelLateral.setLayout(
                new BoxLayout(
                        panelLateral,
                        BoxLayout.Y_AXIS
                )
        );

        panelLateral.setBackground(
                COLOR_LATERAL
        );

        panelLateral.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        15,
                        20,
                        15
                )
        );

        // =================================================
        // TÍTULO DEL PANEL LATERAL
        // =================================================

        JLabel lblHerramientas =
                new JLabel(
                        "HERRAMIENTAS"
                );

        lblHerramientas.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        lblHerramientas.setForeground(
                COLOR_TEXTO_CLARO
        );

        lblHerramientas.setAlignmentX(
                CENTER_ALIGNMENT
        );

        panelLateral.add(
                lblHerramientas
        );

        panelLateral.add(
                Box.createVerticalStrut(
                        20
                )
        );
        // =================================================
        // CUADRO 1 - EDICIÓN
        // =================================================

        JPanel panelEdicion =
                new JPanel(
                        new GridLayout(
                                5,
                                1,
                                0,
                                10
                        )
                );

        panelEdicion.setBackground(
                COLOR_CUADRO
        );

        panelEdicion.setMaximumSize(
                new Dimension(
                        195,
                        285
                )
        );

        panelEdicion.setPreferredSize(
                new Dimension(
                        195,
                        285
                )
        );

        TitledBorder bordeEdicion =
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        " Edición "
                );

        bordeEdicion.setTitleColor(
                COLOR_TEXTO_CLARO
        );

        bordeEdicion.setTitleFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        panelEdicion.setBorder(
                bordeEdicion
        );

        panelEdicion.add(
                btnAgregarEstado
        );

        panelEdicion.add(
                btnSeleccionar
        );

        panelEdicion.add(
                btnEditar
        );

        panelEdicion.add(
                btnEliminar
        );
        panelEdicion.add(
                btnReset
        );
        
        // =================================================
        // CUADRO 2 - AUTÓMATA
        // =================================================

        JPanel panelHerramientasAutomata =
                new JPanel(
                        new GridLayout(
                                5,
                                1,
                                0,
                                10
                        )
                );
       

        panelHerramientasAutomata.setBackground(
                COLOR_CUADRO
        );

        panelHerramientasAutomata.setMaximumSize(
                new Dimension(
                        195,
                        230
                )
        );

        panelHerramientasAutomata.setPreferredSize(
                new Dimension(
                        195,
                        230
                )
        );

        TitledBorder bordeAutomata =
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        " Autómata "
                );

        bordeAutomata.setTitleColor(
                COLOR_TEXTO_CLARO
        );

        bordeAutomata.setTitleFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        panelHerramientasAutomata.setBorder(
                bordeAutomata
        );

        panelHerramientasAutomata.add(
                btnTransicion
        );

        panelHerramientasAutomata.add(
                btnInicial
        );

        panelHerramientasAutomata.add(
                btnFinal
        );

        panelHerramientasAutomata.add(
                btnConvertirRegex
        );
        panelHerramientasAutomata.add(
        btnTablaTransicion
        );       
        
        
        // =================================================
// CUADRO 3 - ARCHIVO
// =================================================

        JPanel panelArchivo =
        new JPanel(
                new GridLayout(
                        2,
                        1,
                        0,
                        10
                )
        );

        panelArchivo.setBackground(
        COLOR_CUADRO
        );

        panelArchivo.setMaximumSize(
        new Dimension(
                195,
                135
               )
        );

        panelArchivo.setPreferredSize(
        new Dimension(
                195,
                135
             )
        );

TitledBorder bordeArchivo =
        BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(
                        COLOR_BORDE
                ),
                " Archivo "
        );

bordeArchivo.setTitleColor(
        COLOR_TEXTO_CLARO
);

bordeArchivo.setTitleFont(
        new Font(
                "Segoe UI",
                Font.BOLD,
                14
        )
);

panelArchivo.setBorder(
        bordeArchivo
);

panelArchivo.add(
        btnExportar
);

panelArchivo.add(
        btnImportar
);

panelArchivo.setAlignmentX(
        CENTER_ALIGNMENT
);
   
        // =================================================
        // AGREGAR LOS DOS CUADROS
        // =================================================

        panelEdicion.setAlignmentX(
                CENTER_ALIGNMENT
        );

        panelHerramientasAutomata.setAlignmentX(
                CENTER_ALIGNMENT
        );

        panelLateral.add(
                panelEdicion
        );

        panelLateral.add(
                Box.createVerticalStrut(
                        20
                )
        );

        panelLateral.add(
                panelHerramientasAutomata
        );
        
        panelLateral.add(
                Box.createVerticalStrut(
                        20
                )
        );

        panelLateral.add(
           panelArchivo
        );

        panelLateral.add(
                Box.createVerticalGlue()
        );
        
        JScrollPane scrollHerramientas = new JScrollPane(panelLateral);

            scrollHerramientas.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            );

            scrollHerramientas.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            );

            add(
                scrollHerramientas,
                BorderLayout.WEST
            );

        // =================================================
        // LIENZO CENTRAL
        // =================================================

        panelAutomata =
                new PanelAutomata(
                        automata
                );

        add(
                panelAutomata,
                BorderLayout.CENTER
        );
        

        // =================================================
        // PANEL INFERIOR
        // =================================================

        JPanel panelInferior =
                new JPanel();

        panelInferior.setBackground(
        new Color(
                112,
                178,
                183
        )
);

        panelInferior.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        JLabel lblCadena =
                new JLabel(
                        "Cadena:"
                );

        lblCadena.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        lblCadena.setForeground(
                COLOR_TEXTO
        );

        txtCadena =
                new JTextField();

        txtCadena.setPreferredSize(
                new Dimension(
                        300,
                        34
                )
        );

        txtCadena.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        txtCadena.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                5,
                                8,
                                5,
                                8
                        )
                )
        );

        lblResultado =
                new JLabel(
                        "Resultado: ---"
                );

        lblResultado.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        lblResultado.setForeground(
                COLOR_TEXTO
        );

        JLabel lblVersion =
                new JLabel(
                        "Versión: "
                        + ProyectoAtomatas.VERSION
                );

        lblVersion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        lblVersion.setForeground(
                new Color(
                        71,
                        85,
                        105
                )
        );

        panelInferior.add(
                lblCadena
        );

        panelInferior.add(
                txtCadena
        );

        panelInferior.add(
                btnSimular
        );

        panelInferior.add(
                lblResultado
        );

        panelInferior.add(
                Box.createHorizontalStrut(
                        20
                )
        );

        panelInferior.add(
                lblVersion
        );

        add(
                panelInferior,
                BorderLayout.SOUTH
        );
        
        

        // =================================================
        // BOTÓN AGREGAR ESTADO
        // =================================================

        btnAgregarEstado.addActionListener(
                e -> {

                    panelAutomata.activarModoAgregar(
                            true
                    );

                    restaurarBotones();

                    btnAgregarEstado.setText(
                            "Agregando..."
                    );
                }
        );

        // =================================================
        // BOTÓN SELECCIONAR / MOVER
        // =================================================

        btnSeleccionar.addActionListener(
                e -> {

                    panelAutomata.activarModoSeleccion();

                    restaurarBotones();
                }
        );

        // =================================================
        // BOTÓN TRANSICIÓN
        // =================================================

        btnTransicion.addActionListener(
                e -> {

                    panelAutomata.activarModoTransicion(
                            true
                    );

                    restaurarBotones();

                    btnTransicion.setText(
                            "Conectando..."
                    );

                    JOptionPane.showMessageDialog(
                            this,
                            "Selecciona el estado de origen y "
                            + "después el estado de destino.",
                            "Crear transición",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
        );

        // =================================================
        // BOTÓN INICIAL
        // =================================================

        btnInicial.addActionListener(
                e -> {

                    panelAutomata.activarModoSeleccion();

                    restaurarBotones();

                    boolean resultado =
                            panelAutomata.marcarInicial();

                    if (!resultado) {

                        mostrarAviso();
                    }
                }
        );

        // =================================================
        // BOTÓN FINAL
        // =================================================

        btnFinal.addActionListener(
                e -> {

                    panelAutomata.activarModoSeleccion();

                    restaurarBotones();

                    boolean resultado =
                            panelAutomata.marcarFinal();

                    if (!resultado) {

                        mostrarAviso();
                    }
                }
        );

        // =================================================
        // BOTÓN EDITAR
        // =================================================

        btnEditar.addActionListener(
                e -> {

                    panelAutomata.activarModoSeleccion();

                    restaurarBotones();

                    boolean resultado =
                            panelAutomata
                                    .editarSeleccionado();

                    if (!resultado) {

                        mostrarAviso();
                    }
                }
        );

        // =================================================
        // BOTÓN ELIMINAR
        // =================================================

        btnEliminar.addActionListener(
                e -> {

                    panelAutomata.activarModoSeleccion();

                    restaurarBotones();

                    if (panelAutomata
                            .getGraph()
                            .getSelectionCount() == 0) {

                        mostrarAviso();

                        return;
                    }

                    int respuesta =
                            JOptionPane.showConfirmDialog(
                                    this,
                                    "¿Deseas eliminar el elemento seleccionado?",
                                    "Confirmar eliminación",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE
                            );

                    if (respuesta
                            == JOptionPane.YES_OPTION) {

                        panelAutomata
                                .eliminarSeleccionado();
                    }
                }
        );
        
        // =================================================
// BOTÓN RESET
// =================================================

btnReset.addActionListener(
        e -> {

            int respuesta =
                    JOptionPane.showConfirmDialog(
                            this,
                            "¿Deseas crear un nuevo autómata?\n"
                            + "Se eliminará todo el autómata actual.",
                            "Resetear autómata",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

            if (respuesta
                    != JOptionPane.YES_OPTION) {

                return;
            }

            panelAutomata.reiniciarAutomata();

            txtCadena.setText("");

            lblResultado.setText(
                    "Resultado: ---"
            );

            lblResultado.setForeground(
                    COLOR_TEXTO
            );

            restaurarBotones();

            JOptionPane.showMessageDialog(
                    this,
                    "El autómata fue reiniciado.\n"
                    + "Puedes comenzar uno nuevo.",
                    "Nuevo autómata",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
);

        // =================================================
        // BOTÓN CONVERTIR A REGEX
        // =================================================

        btnConvertirRegex.addActionListener(
                e -> {

                    panelAutomata.activarModoSeleccion();

                    restaurarBotones();

                    if (automata
                            .getEstadoInicial()
                            == null) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Debes definir un estado inicial.",
                                "Conversión a Regex",
                                JOptionPane.WARNING_MESSAGE
                        );

                        return;
                    }

                    if (automata
                            .getEstadosAceptacion()
                            .isEmpty()) {

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
                                        resultado,
                                        automata,
                                        panelAutomata.obtenerPosicionesEstados()
                                );

                        ventana.setVisible(
                                true
                        );

                    } catch (Exception ex) {

                        JOptionPane.showMessageDialog(
                                this,
                                "No fue posible convertir el autómata.\n"
                                + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
        );
        
        btnTablaTransicion.addActionListener(
        e -> {

            if (automata.getEstados().isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Primero debes crear un autómata.",
                        "Tabla de transición",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            VentanaTablaTransicion ventana =
                    new VentanaTablaTransicion(
                            automata
                    );

            ventana.setVisible(
                    true
            );
        }
);
        
        // =================================================
// BOTÓN EXPORTAR TXT
// =================================================

btnExportar.addActionListener(
        e -> {

            if (automata.getEstados().isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "No hay ningún autómata para exportar.",
                        "Exportar TXT",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            JFileChooser selector =
                    new JFileChooser();

            selector.setDialogTitle(
                    "Guardar autómata"
            );

            selector.setFileFilter(
                    new FileNameExtensionFilter(
                            "Archivo de texto (*.txt)",
                            "txt"
                    )
            );

            selector.setSelectedFile(
                    new File(
                            "automata.txt"
                    )
            );

            int resultado =
                    selector.showSaveDialog(
                            this
                    );

            if (resultado
                    != JFileChooser.APPROVE_OPTION) {

                return;
            }

            File archivo =
                    selector.getSelectedFile();

            if (!archivo.getName()
                    .toLowerCase()
                    .endsWith(".txt")) {

                archivo =
                        new File(
                                archivo.getAbsolutePath()
                                + ".txt"
                        );
            }

            try {

                panelAutomata.exportarTxt(
                        archivo
                );

                JOptionPane.showMessageDialog(
                        this,
                        "El autómata fue exportado correctamente.",
                        "Exportar TXT",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (IOException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "No fue posible guardar el archivo.\n"
                        + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
);
// =================================================
// BOTÓN IMPORTAR TXT
// =================================================

btnImportar.addActionListener(
        e -> {

            JFileChooser selector =
                    new JFileChooser();

            selector.setDialogTitle(
                    "Importar autómata"
            );

            selector.setFileFilter(
                    new FileNameExtensionFilter(
                            "Archivo de texto (*.txt)",
                            "txt"
                    )
            );

            int resultado =
                    selector.showOpenDialog(
                            this
                    );

            if (resultado
                    != JFileChooser.APPROVE_OPTION) {

                return;
            }

            File archivo =
                    selector.getSelectedFile();

            try {

                panelAutomata.importarTxt(
                        archivo
                );

                txtCadena.setText("");

                lblResultado.setText(
                        "Resultado: ---"
                );

                JOptionPane.showMessageDialog(
                        this,
                        "El autómata fue importado correctamente.",
                        "Importar TXT",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "No fue posible importar el archivo.\n"
                        + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
);

        // =================================================
        // BOTÓN SIMULAR
        // =================================================

        btnSimular.addActionListener(
                e -> {

                    panelAutomata.activarModoSeleccion();

                    Cadena cadena =
                            new Cadena(
                                    txtCadena.getText()
                            );

                    if (automata
                            .getEstadoInicial()
                            == null) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Debes marcar un estado inicial.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );

                        return;
                    }

                    SimuladorAutomata simulador =
                            new SimuladorAutomata(
                                    automata
                            );

                    List<SimuladorAutomata.Paso> pasos =
                            simulador.simular(
                                    cadena
                            );

                    // =====================================
                    // NO EXISTE UNA TRANSICIÓN
                    // =====================================

                    if (pasos == null) {

                        panelAutomata
                                .limpiarResaltado();

                        lblResultado.setText(
                                "Resultado: RECHAZADA"
                        );

                        lblResultado.setForeground(
                                COLOR_ROJO
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

                    // =====================================
                    // MOSTRAR ESTADO INICIAL
                    // =====================================

                    panelAutomata.resaltarEstado(
                            automata.getEstadoInicial()
                    );

                    final int[] posicion =
                            {0};

                    Timer timer =
                            new Timer(
                                    1000,
                                    null
                            );

                    timer.addActionListener(
                            evento -> {

                                if (posicion[0]
                                        < pasos.size()) {

                                    SimuladorAutomata.Paso paso =
                                            pasos.get(
                                                    posicion[0]
                                            );

                                    panelAutomata
                                            .resaltarPaso(
                                                    paso.getDestino(),
                                                    paso.getTransicion()
                                            );

                                    posicion[0]++;

                                    return;
                                }

                                timer.stop();

                                Estado estadoFinal =
                                        simulador
                                                .obtenerEstadoFinal(
                                                        cadena,
                                                        pasos
                                                );

                                boolean aceptada =
                                        simulador
                                                .esAceptada(
                                                        estadoFinal
                                                );

                                if (aceptada) {

                                lblResultado.setText(
                                        "Resultado: ACEPTADA"
                                );

                                lblResultado.setForeground(
                                        new Color(0, 128, 0)
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

                                    lblResultado.setForeground(
                                            COLOR_ROJO
                                    );

                                    JOptionPane.showMessageDialog(
                                            this,
                                            "La cadena fue RECHAZADA.",
                                            "Resultado",
                                            JOptionPane.WARNING_MESSAGE
                                    );
                                }
                            }
                    );

                    timer.setInitialDelay(
                            1000
                    );

                    timer.start();
                }
        );
    }
    

    // =====================================================
    // ESTILO DE LOS BOTONES
    // =====================================================

    private void estilizarBoton(
            JButton boton,
            Color color
    ) {

        boton.setBackground(
                color
        );

        boton.setForeground(
                Color.WHITE
        );

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        boton.setFocusPainted(
                false
        );

        boton.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.setOpaque(
                true
        );
    }

    // =====================================================
    // RESTAURAR TEXTOS DE LOS BOTONES
    // =====================================================

    private void restaurarBotones() {

        btnAgregarEstado.setText(
                "Agregar estado"
        );

        btnTransicion.setText(
                "Transición"
        );
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