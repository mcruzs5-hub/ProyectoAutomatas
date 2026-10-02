package com.mycompany.proyectoatomatas.view;

import com.mycompany.proyectoatomatas.algorithm
        .ConversorRegex.PasoEliminacion;

import com.mycompany.proyectoatomatas.algorithm
        .ConversorRegex.ResultadoConversion;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class VentanaConversionRegex extends JFrame {

    private final ResultadoConversion resultado;

    private final PanelRegex panelRegex;

    private final JLabel lblPaso;

    private final JTextArea txtResultado;

    private final JButton btnAnimar;

    private Timer timer;

    private int posicion = 0;

    public VentanaConversionRegex(
            ResultadoConversion resultado
    ) {

        this.resultado = resultado;

        setTitle(
                "Conversión de AFD a Expresión Regular"
        );

        setSize(1100, 750);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setLayout(new BorderLayout());

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

        superior.add(lblPaso);
        superior.add(btnAnimar);

        add(
                superior,
                BorderLayout.NORTH
        );

        // =================================================
        // PANEL CENTRAL
        // =================================================

        panelRegex =
                new PanelRegex();

        add(
                panelRegex,
                BorderLayout.CENTER
        );

        // =================================================
        // RESULTADO INFERIOR
        // =================================================

        //txtResultado = new JTextArea();
        //txtResultado.setEditable(false);
        //txtResultado.setLineWrap(true);
        //txtResultado.setWrapStyleWord(true);
        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        txtResultado.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 16));
        txtResultado.setBackground(new java.awt.Color(248, 249, 250));
        
        txtResultado.setPreferredSize(
                new Dimension(
                        1000,
                        100
                )
        );

        JScrollPane scroll =
                new JScrollPane(
                        txtResultado
                );

        add(
                scroll,
                BorderLayout.SOUTH
        );

        // Mostrar normalización inicialmente
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

        btnAnimar.addActionListener(
                e -> iniciarAnimacion()
        );
    }

    // =====================================================
    // ANIMACIÓN
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
                                    pasos.get(posicion);

                            panelRegex.mostrarPaso(
                                    paso
                            );

                            lblPaso.setText(
                                    paso.getDescripcion()
                            );

                            posicion++;
                        }
                );

        timer.setInitialDelay(500);

        timer.start();
    }

    // =====================================================
    // RESULTADO FINAL
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
}