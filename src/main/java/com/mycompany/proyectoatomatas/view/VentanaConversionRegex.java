package com.mycompany.proyectoatomatas.view;

import com.mycompany.proyectoatomatas.algorithm.VerificadorEquivalencia;

import com.mycompany.proyectoatomatas.algorithm
        .ConversorRegex.PasoEliminacion;

import com.mycompany.proyectoatomatas.algorithm
        .ConversorRegex.ResultadoConversion;

import com.mycompany.proyectoatomatas.model.Automata;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Point2D;

import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

public class VentanaConversionRegex extends JFrame {

    // =====================================================
    // DATOS PRINCIPALES
    // =====================================================

    private final ResultadoConversion resultado;
    private final Automata automata;

    // =====================================================
    // COMPONENTES DE CONVERSIÓN
    // =====================================================

    private final PanelRegex panelRegex;
    private final JLabel lblPaso;
    private final JTextArea txtResultado;
    private final JButton btnAnimar;

    // =====================================================
    // COMPONENTES DE VERIFICACIÓN
    // =====================================================

    private final JTextField txtCadenaPrueba;
    private final JButton btnVerificar;
    private final JLabel lblVerificacion;

    // =====================================================
    // ANIMACIÓN
    // =====================================================

    private Timer timer;
    private int posicion = 0;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public VentanaConversionRegex(
            ResultadoConversion resultado,
            Automata automata,
            Map<String, Point2D.Double> posicionesEstados
    ) {

        this.resultado = resultado;
        this.automata = automata;

        setTitle(
                "Conversión de AFD a Expresión Regular"
        );

        setSize(
                1100,
                800
        );
        
        setExtendedState(
        JFrame.MAXIMIZED_BOTH);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setLayout(
                new BorderLayout()
        );

        // =================================================
        // PANEL SUPERIOR
        // =================================================

        JPanel superior =
                new JPanel();

        lblPaso =
                new JLabel(
                        "Preparado para iniciar conversión"
                );

        btnAnimar =
                new JButton(
                        "Iniciar conversión"
                );

        superior.add(
                lblPaso
        );

        superior.add(
                btnAnimar
        );

        add(
                superior,
                BorderLayout.NORTH
        );

        // =================================================
        // PANEL CENTRAL
        // =================================================

        panelRegex =
                new PanelRegex(
                        posicionesEstados
                );

        add(
                panelRegex,
                BorderLayout.CENTER
        );

        // =================================================
        // RESULTADO DE LA EXPRESIÓN REGULAR
        // =================================================

        txtResultado =
                new JTextArea();

        txtResultado.setEditable(false);

        txtResultado.setLineWrap(true);

        txtResultado.setWrapStyleWord(true);

        txtResultado.setFont(
        new java.awt.Font(
                "SansSerif",
                java.awt.Font.PLAIN,
                16
            )
        );

        txtResultado.setPreferredSize(
                new Dimension(
                        1000,
                        100
                )
        );

        JScrollPane scrollResultado =
                new JScrollPane(
                        txtResultado
                );

        // =================================================
        // PANEL DE VERIFICACIÓN
        // =================================================

        JPanel panelVerificacion =
                new JPanel();

        JLabel lblCadena =
                new JLabel(
                        "Cadena de prueba:"
                );

        txtCadenaPrueba =
                new JTextField();

        txtCadenaPrueba.setPreferredSize(
                new Dimension(
                        250,
                        30
                )
        );

        btnVerificar =
                new JButton(
                        "Verificar equivalencia"
                );

        lblVerificacion =
                new JLabel(
                        "Resultado: ---"
                );

        panelVerificacion.add(
                lblCadena
        );

        panelVerificacion.add(
                txtCadenaPrueba
        );

        panelVerificacion.add(
                btnVerificar
        );

        panelVerificacion.add(
                lblVerificacion
        );

        // =================================================
        // PANEL INFERIOR COMPLETO
        // =================================================

        JPanel panelInferior =
                new JPanel(
                        new BorderLayout()
                );

        panelInferior.add(
                scrollResultado,
                BorderLayout.CENTER
        );

        panelInferior.add(
                panelVerificacion,
                BorderLayout.SOUTH
        );

        add(
                panelInferior,
                BorderLayout.SOUTH
        );

        // =================================================
        // MOSTRAR NORMALIZACIÓN INICIAL
        // =================================================

        List<PasoEliminacion> pasos =
                resultado.getPasos();

        if (!pasos.isEmpty()) {

            panelRegex.mostrarPaso(
                    pasos.get(0)
            );

            lblPaso.setText(
                    pasos.get(0)
                            .getDescripcion()
            );
        }

        // =================================================
        // EVENTOS
        // =================================================

        btnAnimar.addActionListener(
                e -> iniciarAnimacion()
        );

        btnVerificar.addActionListener(
                e -> verificarEquivalencia()
        );
    }

    // =====================================================
    // ANIMACIÓN DE CONVERSIÓN
    // =====================================================

    private void iniciarAnimacion() {

        btnAnimar.setEnabled(false);

        posicion = 0;

        List<PasoEliminacion> pasos =
                resultado.getPasos();

        timer =
                new Timer(
                        1800,
                        e -> {

                            if (posicion
                                    >= pasos.size()) {

                                timer.stop();

                                mostrarResultadoFinal();

                                btnAnimar.setEnabled(true);

                                return;
                            }

                            PasoEliminacion paso =
                                    pasos.get(
                                            posicion
                                    );

                            panelRegex.mostrarPaso(
                                    paso
                            );

                            lblPaso.setText(
                                    paso.getDescripcion()
                            );

                            posicion++;
                        }
                );

        timer.setInitialDelay(
                500
        );

        timer.start();
    }

    // =====================================================
    // RESULTADO FINAL DE LA CONVERSIÓN
    // =====================================================

    private void mostrarResultadoFinal() {

        String regex =
                resultado.getExpresionRegular();

        txtResultado.setText(
                "EXPRESIÓN REGULAR RESULTANTE:\n\n"
                + regex
        );

        JOptionPane.showMessageDialog(
                this,
                "Conversión finalizada.\n\n"
                + "Expresión Regular:\n"
                + regex,
                "Resultado Final",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // VERIFICACIÓN DE EQUIVALENCIA
    // =====================================================

    private void verificarEquivalencia() {

        String cadena =
                txtCadenaPrueba.getText();

        String regex =
                resultado.getExpresionRegular();

        try {

            VerificadorEquivalencia verificador =
                    new VerificadorEquivalencia(
                            automata,
                            regex
                    );

            VerificadorEquivalencia.Resultado r =
                    verificador.verificar(
                            cadena
                    );

            String resultadoAFD;

            if (r.isAceptadaAFD()) {

                resultadoAFD =
                        "ACEPTADA";

            } else {

                resultadoAFD =
                        "RECHAZADA";
            }

            String resultadoRegex;

            if (r.isAceptadaRegex()) {

                resultadoRegex =
                        "ACEPTADA";

            } else {

                resultadoRegex =
                        "RECHAZADA";
            }

            // =============================================
            // LOS DOS RESULTADOS COINCIDEN
            // =============================================

            if (r.isCoinciden()) {

                lblVerificacion.setText(
                        "Resultado: COINCIDEN ✓"
                );

                JOptionPane.showMessageDialog(
                        this,
                        "VERIFICACIÓN CORRECTA\n\n"
                        + "Cadena: "
                        + mostrarCadena(cadena)
                        + "\n\n"
                        + "AFD: "
                        + resultadoAFD
                        + "\n"
                        + "Regex: "
                        + resultadoRegex
                        + "\n\n"
                        + "Los resultados coinciden.",
                        "Verificación de Equivalencia",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                // =========================================
                // LOS RESULTADOS NO COINCIDEN
                // =========================================

                lblVerificacion.setText(
                        "Resultado: NO COINCIDEN ✗"
                );

                JOptionPane.showMessageDialog(
                        this,
                        "LOS RESULTADOS NO COINCIDEN\n\n"
                        + "Cadena: "
                        + mostrarCadena(cadena)
                        + "\n\n"
                        + "AFD: "
                        + resultadoAFD
                        + "\n"
                        + "Regex: "
                        + resultadoRegex
                        + "\n\n"
                        + "Existe una diferencia entre "
                        + "el AFD y la expresión regular.",
                        "Verificación de Equivalencia",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible verificar "
                    + "la equivalencia.\n\n"
                    + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // MOSTRAR LA CADENA VACÍA
    // =====================================================

    private String mostrarCadena(
            String cadena
    ) {

        if (cadena == null
                || cadena.isEmpty()) {

            return "ε (cadena vacía)";
        }

        return cadena;
    }
}