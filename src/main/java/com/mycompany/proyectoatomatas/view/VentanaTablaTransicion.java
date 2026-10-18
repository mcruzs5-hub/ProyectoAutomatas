package com.mycompany.proyectoatomatas.view;

import com.mycompany.proyectoatomatas.model.Automata;
import com.mycompany.proyectoatomatas.model.Estado;
import com.mycompany.proyectoatomatas.model.Transicion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class VentanaTablaTransicion extends JFrame {

    private final Automata automata;

    private JTable tabla;
    private JLabel lblQ;
    private JLabel lblSigma;
    private JLabel lblDelta;
    private JLabel lblInicial;
    private JLabel lblFinales;

    public VentanaTablaTransicion(
            Automata automata
    ) {

        this.automata = automata;

        configurarVentana();
        crearInterfaz();
        cargarDatos();
    }

    // =====================================================
    // CONFIGURAR VENTANA
    // =====================================================

    private void configurarVentana() {

        setTitle(
                "Tabla de Transiciones"
        );

        setSize(
                700,
                500
        );

        setMinimumSize(
                new Dimension(
                        600,
                        400
                )
        );

        setLocationRelativeTo(
                null
        );

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );
    }

    // =====================================================
    // CREAR INTERFAZ
    // =====================================================

    private void crearInterfaz() {

        // ------------------------------
        // TÍTULO
        // ------------------------------

        JLabel titulo =
                new JLabel(
                        "Tabla de Transiciones",
                        SwingConstants.CENTER
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        titulo.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        10,
                        10,
                        10
                )
        );

        add(
                titulo,
                BorderLayout.NORTH
        );

        // ------------------------------
        // INFORMACIÓN DEL AUTÓMATA
        // ------------------------------

        JPanel panelDatos =
                new JPanel();

        panelDatos.setLayout(
                new javax.swing.BoxLayout(
                        panelDatos,
                        javax.swing.BoxLayout.Y_AXIS
                )
        );

        panelDatos.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                "Quíntupla del AFD"
                        ),
                        BorderFactory.createEmptyBorder(
                                10,
                                15,
                                10,
                                15
                        )
                )
        );

        lblQ = new JLabel();
        lblSigma = new JLabel();
        lblDelta = new JLabel();
        lblInicial = new JLabel();
        lblFinales = new JLabel();

        Font fuenteDatos =
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                );

        lblQ.setFont(fuenteDatos);
        lblSigma.setFont(fuenteDatos);
        lblDelta.setFont(fuenteDatos);
        lblInicial.setFont(fuenteDatos);
        lblFinales.setFont(fuenteDatos);

        panelDatos.add(lblQ);
        panelDatos.add(lblSigma);
        panelDatos.add(lblDelta);
        panelDatos.add(lblInicial);
        panelDatos.add(lblFinales);

        // ------------------------------
        // TABLA
        // ------------------------------

        tabla =
                new JTable();

        tabla.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        tabla.setRowHeight(
                28
        );

        tabla.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                14
                        )
                );

        JScrollPane scrollTabla =
                new JScrollPane(
                        tabla
                );

        // ------------------------------
        // CENTRO
        // ------------------------------

        JPanel centro =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        centro.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        15,
                        15,
                        15
                )
        );

        centro.add(
                panelDatos,
                BorderLayout.NORTH
        );

        centro.add(
                scrollTabla,
                BorderLayout.CENTER
        );

        add(
                centro,
                BorderLayout.CENTER
        );
    }

    // =====================================================
    // CARGAR DATOS
    // =====================================================

    private void cargarDatos() {

        if (automata == null) {
            return;
        }

        List<Estado> estados =
                automata.getEstados();

        List<Transicion> transiciones =
                automata.getTransiciones();

        // =================================================
        // OBTENER ALFABETO
        // =================================================

        Set<String> alfabeto =
                new LinkedHashSet<>();

        for (Transicion transicion
                : transiciones) {

            alfabeto.add(
                    transicion.getSimbolo()
            );
        }

        List<String> simbolos =
                new ArrayList<>(
                        alfabeto
                );

        // =================================================
        // MOSTRAR Q
        // =================================================

        StringBuilder textoQ =
                new StringBuilder(
                        "Q = {"
                );

        for (int i = 0;
                i < estados.size();
                i++) {

            textoQ.append(
                    estados.get(i)
                            .getNombre()
            );

            if (i
                    < estados.size() - 1) {

                textoQ.append(
                        ", "
                );
            }
        }

        textoQ.append(
                "}"
        );

        lblQ.setText(
                textoQ.toString()
        );

        // =================================================
        // MOSTRAR SIGMA
        // =================================================

        StringBuilder textoSigma =
                new StringBuilder(
                        "Σ = {"
                );

        for (int i = 0;
                i < simbolos.size();
                i++) {

            textoSigma.append(
                    simbolos.get(i)
            );

            if (i
                    < simbolos.size() - 1) {

                textoSigma.append(
                        ", "
                );
            }
        }

        textoSigma.append(
                "}"
        );

        lblSigma.setText(
                textoSigma.toString()
        );
        
        // =================================================
        // MOSTRAR FUNCIÓN DE TRANSICIÓN
        // =================================================

        StringBuilder textoDelta =
                new StringBuilder("δ = {");

        for (int i = 0; i < transiciones.size(); i++) {

            Transicion transicion = transiciones.get(i);

            textoDelta.append("(")
                    .append(transicion.getOrigen().getNombre())
                    .append(", ")
                    .append(transicion.getSimbolo())
                    .append(") → ")
                    .append(transicion.getDestino().getNombre());

            if (i < transiciones.size() - 1) {
                textoDelta.append("; ");
            }
        }

        textoDelta.append("}");

        lblDelta.setText(textoDelta.toString());
        
        // =================================================
        // ESTADO INICIAL
        // =================================================

        Estado inicial =
                automata.getEstadoInicial();

        if (inicial != null) {

            lblInicial.setText(
                    "q₀ = "
                    + inicial.getNombre()
            );

        } else {

            lblInicial.setText(
                    "q₀ = No definido"
            );
        }

        // =================================================
        // ESTADOS FINALES
        // =================================================

        StringBuilder textoFinales =
                new StringBuilder(
                        "F = {"
                );

        List<Estado> finales =
                automata.getEstadosAceptacion();

        for (int i = 0;
                i < finales.size();
                i++) {

            textoFinales.append(
                    finales.get(i)
                            .getNombre()
            );

            if (i
                    < finales.size() - 1) {

                textoFinales.append(
                        ", "
                );
            }
        }

        textoFinales.append(
                "}"
        );

        lblFinales.setText(
                textoFinales.toString()
        );

        // =================================================
        // COLUMNAS
        // =================================================

        String[] columnas =
                new String[
                        simbolos.size() + 1
                ];

        columnas[0] =
                "Estado";

        for (int i = 0;
                i < simbolos.size();
                i++) {

            columnas[i + 1] =
                    simbolos.get(i);
        }

        // =================================================
        // FILAS
        // =================================================

        Object[][] datos =
                new Object[
                        estados.size()
                ][
                        simbolos.size() + 1
                ];

        for (int fila = 0;
                fila < estados.size();
                fila++) {

            Estado estado =
                    estados.get(
                            fila
                    );

            String nombreEstado =
                    estado.getNombre();

            if (estado.isInicial()) {

                nombreEstado =
                        "→ " + nombreEstado;
            }

            if (estado.isAceptacion()) {

                nombreEstado =
                        "* " + nombreEstado;
            }

            datos[fila][0] =
                    nombreEstado;

            for (int columna = 0;
                    columna < simbolos.size();
                    columna++) {

                String simbolo =
                        simbolos.get(
                                columna
                        );

                String destino =
                        buscarDestino(
                                estado,
                                simbolo
                        );

                datos[fila][columna + 1] =
                        destino;
            }
        }

        DefaultTableModel modelo =
                new DefaultTableModel(
                        datos,
                        columnas
                ) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {

                return false;
            }
        };

        tabla.setModel(
                modelo
        );
    }

    // =====================================================
    // BUSCAR DESTINO
    // =====================================================

    private String buscarDestino(
            Estado origen,
            String simbolo
    ) {

        for (Transicion transicion
                : automata.getTransiciones()) {

            if (transicion.getOrigen()
                    == origen
                    && transicion.getSimbolo()
                            .equals(simbolo)) {

                return transicion
                        .getDestino()
                        .getNombre();
            }
        }

        return "—";
    }
}