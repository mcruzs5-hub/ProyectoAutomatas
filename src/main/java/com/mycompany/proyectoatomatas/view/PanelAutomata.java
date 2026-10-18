package com.mycompany.proyectoatomatas.view;

// LIBRERÍAS JGRAPHX
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.model.mxGeometry;

// MODELO MATEMÁTICO
import com.mycompany.proyectoatomatas.model.Automata;
import com.mycompany.proyectoatomatas.model.Estado;
import com.mycompany.proyectoatomatas.model.Transicion;

// LIBRERÍAS JAVA
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;

public class PanelAutomata extends mxGraphComponent {

    // =====================================================
    // 1. ATRIBUTOS
    // =====================================================

    private final mxGraph grafo;
    private final Automata automata;

    private final Map<Object, Estado> mapaEstados;
    private final Map<Object, Transicion> mapaTransiciones;

    private int contadorEstados = 0;

    private boolean modoAgregar = false;
    private boolean modoTransicion = false;

    private Object indicadorInicial = null;
    private Object origenTransicion = null;

    // =====================================================
    // 2. CONSTRUCTOR
    // =====================================================

    public PanelAutomata(Automata automata) {

        super(new mxGraph());

        this.automata = automata;
        this.grafo = getGraph();

        mapaEstados = new IdentityHashMap<>();
        mapaTransiciones = new IdentityHashMap<>();

        grafo.setCellsEditable(false);
        grafo.setCellsResizable(false);
        grafo.setAllowDanglingEdges(false);
        grafo.setCellsMovable(true);
        grafo.setCellsSelectable(true);

        grafo.setCellsDisconnectable(false);
        grafo.setCellsBendable(false);

        grafo.getStylesheet()
                .getDefaultEdgeStyle()
                .put(
                        mxConstants.STYLE_LOOP,
                        mxEdgeStyle.Loop
                );

        setConnectable(false);

        // =====================================================
        // FONDO DEL LIENZO DEL AUTÓMATA
        // =====================================================

        Color fondoLienzo =
                new Color(186, 206, 224);

        getViewport().setOpaque(true);

        getViewport().setBackground(
                fondoLienzo
        );

        getGraphControl().setBackground(
                fondoLienzo
        );

        configurarMouse();
    }

    // =====================================================
    // 3. MODOS DE FUNCIONAMIENTO
    // =====================================================

    public void activarModoAgregar(boolean activar) {

        modoAgregar = activar;

        if (activar) {
            modoTransicion = false;
            origenTransicion = null;
        }

        actualizarMovimiento();
    }

    public void activarModoTransicion(boolean activar) {

        modoTransicion = activar;

        if (activar) {
            modoAgregar = false;
        }

        origenTransicion = null;

        actualizarMovimiento();
    }

    public void activarModoSeleccion() {

        modoAgregar = false;
        modoTransicion = false;
        origenTransicion = null;

        actualizarMovimiento();
    }

    private void actualizarMovimiento() {

        grafo.setCellsMovable(
                !modoAgregar && !modoTransicion
        );
    }

    // =====================================================
    // 4. EVENTOS DEL MOUSE
    // =====================================================

    private void configurarMouse() {

        getGraphControl().addMouseListener(
                new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton()
                        != MouseEvent.BUTTON1) {

                    return;
                }

                Object celda =
                        getCellAt(
                                e.getX(),
                                e.getY()
                        );

                if (modoTransicion) {

                    Object estadoCelda =
                            obtenerCeldaEstado(
                                    celda
                            );

                    if (estadoCelda != null) {

                        procesarTransicion(
                                estadoCelda
                        );
                    }

                    return;
                }

                if (!modoAgregar) {
                    return;
                }

                if (celda != null) {
                    return;
                }

                double escala =
                        grafo.getView()
                                .getScale();

                double x =
                        e.getX() / escala
                        - grafo.getView()
                                .getTranslate()
                                .getX();

                double y =
                        e.getY() / escala
                        - grafo.getView()
                                .getTranslate()
                                .getY();

                crearEstado(
                        x,
                        y
                );
            }
        });
    }
    
    private Object buscarAristaMismoOrigenDestino(
        Estado origen,
        Estado destino
) {

    for (Map.Entry<Object, Transicion> entrada
            : mapaTransiciones.entrySet()) {

        Transicion transicion =
                entrada.getValue();

        if (transicion.getOrigen() == origen
                && transicion.getDestino() == destino) {

            return entrada.getKey();
        }
    }

    return null;
}

    // =====================================================
    // 5. IDENTIFICAR CELDA DE ESTADO
    // =====================================================

    private Object obtenerCeldaEstado(
            Object celda
    ) {

        Object actual = celda;

        while (actual != null) {

            if (mapaEstados.containsKey(actual)) {

                return actual;
            }

            actual =
                    grafo.getModel()
                            .getParent(actual);
        }

        return null;
    }

    // =====================================================
    // 6. CREAR ESTADOS
    // =====================================================

    private void crearEstado(
            double x,
            double y
    ) {

        String nombre =
                "q" + contadorEstados;

        Estado estado =
                new Estado(
                        nombre
                );

        Object parent =
                grafo.getDefaultParent();

        grafo.getModel()
                .beginUpdate();

        try {

            Object celda =
                    grafo.insertVertex(
                            parent,
                            null,
                            nombre,
                            x - 30,
                            y - 30,
                            60,
                            60,
                            "shape=ellipse;"
                            + "perimeter=ellipsePerimeter;"
                            + "fillColor=#FFFFFF;"
                            + "strokeColor=#000000;"
                            + "strokeWidth=2;"
                            + "fontColor=#000000;"
                            + "fontSize=14;"
                            + "align=center;"
                            + "verticalAlign=middle;"
                    );

            mapaEstados.put(
                    celda,
                    estado
            );

            automata.agregarEstado(
                    estado
            );

            contadorEstados++;

        } finally {

            grafo.getModel()
                    .endUpdate();
        }
    }

    // =====================================================
    // 7. OBTENER ESTADO SELECCIONADO
    // =====================================================

    public Estado getEstadoSeleccionado() {

        Object celda =
                obtenerCeldaEstado(
                        grafo.getSelectionCell()
                );

        return mapaEstados.get(
                celda
        );
    }

    // =====================================================
    // 8. MARCAR ESTADO INICIAL
    // =====================================================

    public boolean marcarInicial() {

        Object celda =
                obtenerCeldaEstado(
                        grafo.getSelectionCell()
                );

        Estado seleccionado =
                mapaEstados.get(
                        celda
                );

        if (seleccionado == null) {
            return false;
        }

        grafo.getModel()
                .beginUpdate();

        try {

            if (indicadorInicial != null) {

                grafo.removeCells(
                        new Object[]{
                            indicadorInicial
                        }
                );

                indicadorInicial = null;
            }

            automata.setEstadoInicial(
                    seleccionado
            );

            indicadorInicial =
                    grafo.insertVertex(
                            celda,
                            null,
                            "",
                            -25,
                            9,
                            25,
                            42,
                            "shape=triangle;"
                            + "direction=east;"
                            + "fillColor=#FFFFFF;"
                            + "strokeColor=#000000;"
                            + "strokeWidth=1.5;"
                            + "movable=0;"
                            + "resizable=0;"
                            + "connectable=0;"
                            + "selectable=0;"
                    );

        } finally {

            grafo.getModel()
                    .endUpdate();
        }

        grafo.refresh();

        return true;
    }

    // =====================================================
    // 9. MARCAR O DESMARCAR ESTADO FINAL
    // =====================================================

    public boolean marcarFinal() {

        Object celda =
                obtenerCeldaEstado(
                        grafo.getSelectionCell()
                );

        Estado seleccionado =
                mapaEstados.get(
                        celda
                );

        if (seleccionado == null) {
            return false;
        }

        seleccionado.setAceptacion(
                !seleccionado.isAceptacion()
        );

        grafo.getModel()
                .beginUpdate();

        try {

            if (seleccionado.isAceptacion()) {

                grafo.setCellStyles(
                        mxConstants.STYLE_SHAPE,
                        mxConstants.SHAPE_DOUBLE_ELLIPSE,
                        new Object[]{celda}
                );

            } else {

                grafo.setCellStyles(
                        mxConstants.STYLE_SHAPE,
                        mxConstants.SHAPE_ELLIPSE,
                        new Object[]{celda}
                );
            }

        } finally {

            grafo.getModel()
                    .endUpdate();
        }

        grafo.refresh();

        return true;
    }

    // =====================================================
    // 10. VALIDAR SÍMBOLOS
    // =====================================================

    private boolean simboloValido(
            String simbolo
    ) {

        if (simbolo == null
                || simbolo.isEmpty()) {

            return false;
        }

        if (simbolo.equals("ε")) {
            return false;
        }

        return simbolo.codePointCount(
                0,
                simbolo.length()
        ) == 1;
    }

    // =====================================================
    // 11. CREAR TRANSICIONES
    // =====================================================

    private void procesarTransicion(
            Object celda
    ) {

        // PRIMER CLIC: ORIGEN
        if (origenTransicion == null) {

            origenTransicion =
                    celda;

            Estado origen =
                    mapaEstados.get(
                            celda
                    );

            System.out.println(
                    "Origen seleccionado: "
                    + origen.getNombre()
            );

            return;
        }

        // SEGUNDO CLIC: DESTINO
        Object destinoCelda =
                celda;

        Estado origen =
                mapaEstados.get(
                        origenTransicion
                );

        Estado destino =
                mapaEstados.get(
                        destinoCelda
                );

        String simbolo =
                JOptionPane.showInputDialog(
                        this,
                        "Transición: "
                        + origen.getNombre()
                        + " → "
                        + destino.getNombre()
                        + "\nIngrese el símbolo:",
                        "Nueva transición",
                        JOptionPane.QUESTION_MESSAGE
                );

        if (simbolo == null) {

            origenTransicion = null;
            return;
        }

        simbolo =
                simbolo.trim();

        if (!simboloValido(simbolo)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un único símbolo válido.\n"
                    + "Un AFD no utiliza transiciones epsilon."
            );

            origenTransicion = null;
            return;
        }

        for (Transicion t
                : automata.getTransiciones()) {

            if (t.getOrigen() == origen
                    && t.getSimbolo()
                            .equals(simbolo)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ya existe una transición con el símbolo "
                        + simbolo
                        + " desde "
                        + origen.getNombre()
                );

                origenTransicion = null;
                return;
            }
        }

        Transicion nueva =
                new Transicion(
                        origen,
                        destino,
                        simbolo
                );

        boolean esBucle =
                origenTransicion
                == destinoCelda;

        Object parent =
                grafo.getDefaultParent();

        grafo.getModel()
                .beginUpdate();

        try {

            // =================================================
            // ESTILO GENERAL DE TRANSICIONES
            // =================================================

            String estilo =
                    "strokeColor=#000000;"
                    + "strokeWidth=2;"
                    + "endArrow=classic;"
                    + "endFill=1;"
                    + "fontSize=14;"
                    + "fontColor=#000000;"
                    + "labelBackgroundColor=#BACEE0;"
                    + "verticalLabelPosition=top;"
                    + "verticalAlign=bottom;"
                    + "spacingBottom=10;"
                    + "rounded=1;";

            // =================================================
            // SI ES AUTOTRANSICIÓN
            // =================================================

            if (esBucle) {

    estilo +=
            "loopStyle=loopEdgeStyle;"
            + "direction=north;"
            + "segment=45;"
            + "rounded=1;"
            + "arcSize=100;"
            + "orthogonalLoop=1;";
}

            // =================================================
            // CREAR ARISTA
            // =================================================

            Object aristaExistente =
        buscarAristaMismoOrigenDestino(
                origen,
                destino
        );

if (aristaExistente != null) {

    // Ya existe una flecha entre esos estados.
    // Solo agregamos el símbolo visualmente.

    String etiquetaActual =
            String.valueOf(
                    grafo.getModel()
                            .getValue(
                                    aristaExistente
                            )
            );

    if (!etiquetaActual.contains(simbolo)) {

        String nuevaEtiqueta =
                etiquetaActual
                + ", "
                + simbolo;

        grafo.getModel()
                .setValue(
                        aristaExistente,
                        nuevaEtiqueta
                );
    }

    // Matemáticamente sí agregamos una transición nueva
    automata.agregarTransicion(
            nueva
    );

} else {

    // No existe flecha todavía, crearla normalmente

    Object arista =
            grafo.insertEdge(
                    parent,
                    null,
                    simbolo,
                    origenTransicion,
                    destinoCelda,
                    estilo
            );

    mapaTransiciones.put(
            arista,
            nueva
    );

    automata.agregarTransicion(
            nueva
    );

    if (!esBucle) {

        organizarTransicionesOpuestas(
                arista,
                nueva
        );
    }
}

            // =================================================
            // HACER EL BUCLE MÁS REDONDO
            // =================================================
        } finally {

            grafo.getModel()
                    .endUpdate();
        }

        origenTransicion = null;

        grafo.refresh();
    }

        // =====================================================
    // 12. ORGANIZAR TRANSICIONES DE IDA Y VUELTA
    // =====================================================

    private void organizarTransicionesOpuestas(
            Object aristaNueva,
            Transicion nueva
    ) {

        for (Map.Entry<Object, Transicion> entrada
                : mapaTransiciones.entrySet()) {

            Object otraArista =
                    entrada.getKey();

            Transicion otra =
                    entrada.getValue();

            if (otraArista == aristaNueva) {
                continue;
            }

            boolean esInversa =
                    otra.getOrigen()
                    == nueva.getDestino()
                    && otra.getDestino()
                    == nueva.getOrigen();

            if (esInversa
                    && nueva.getOrigen()
                    != nueva.getDestino()) {

                curvarArista(
                        otraArista
                );

                curvarArista(
                        aristaNueva
                );

                break;
            }
        }
    }

    // =====================================================
    // 13. CURVAR UNA ARISTA
    // =====================================================

    private void curvarArista(
            Object arista
    ) {

        Object origen =
                grafo.getModel()
                        .getTerminal(
                                arista,
                                true
                        );

        Object destino =
                grafo.getModel()
                        .getTerminal(
                                arista,
                                false
                        );

        mxGeometry geoOrigen =
                grafo.getModel()
                        .getGeometry(
                                origen
                        );

        mxGeometry geoDestino =
                grafo.getModel()
                        .getGeometry(
                                destino
                        );

        double x1 =
                geoOrigen.getX()
                + geoOrigen.getWidth()
                / 2;

        double y1 =
                geoOrigen.getY()
                + geoOrigen.getHeight()
                / 2;

        double x2 =
                geoDestino.getX()
                + geoDestino.getWidth()
                / 2;

        double y2 =
                geoDestino.getY()
                + geoDestino.getHeight()
                / 2;

        double dx =
                x2 - x1;

        double dy =
                y2 - y1;

        double distancia =
                Math.hypot(
                        dx,
                        dy
                );

        if (distancia < 1) {
            return;
        }

        double medioX =
                (x1 + x2)
                / 2;

        double medioY =
                (y1 + y2)
                / 2;

        double separacion = 42;

        double controlX =
                medioX
                - (dy / distancia)
                * separacion;

        double controlY =
                medioY
                + (dx / distancia)
                * separacion;

        mxGeometry geometria =
                (mxGeometry)
                grafo.getModel()
                        .getGeometry(
                                arista
                        )
                        .clone();

        geometria.setPoints(
                Collections.singletonList(
                        new mxPoint(
                                controlX,
                                controlY
                        )
                )
        );

        grafo.setCellStyles(
                "noEdgeStyle",
                "1",
                new Object[]{arista}
        );

        grafo.setCellStyles(
                "curved",
                "1",
                new Object[]{arista}
        );

        grafo.getModel()
                .setGeometry(
                        arista,
                        geometria
                );
    }

    // =====================================================
    // 14. EDITAR ESTADOS Y TRANSICIONES
    // =====================================================

    public boolean editarSeleccionado() {

        Object celda =
                grafo.getSelectionCell();

        if (celda == null) {
            return false;
        }

        Object celdaEstado =
                obtenerCeldaEstado(
                        celda
                );

        // EDITAR ESTADO
        if (celdaEstado != null) {

            Estado estado =
                    mapaEstados.get(
                            celdaEstado
                    );

            String nuevoNombre =
                    JOptionPane.showInputDialog(
                            this,
                            "Nuevo nombre del estado:",
                            estado.getNombre()
                    );

            if (nuevoNombre == null) {
                return true;
            }

            nuevoNombre =
                    nuevoNombre.trim();

            if (nuevoNombre.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "El nombre no puede estar vacío."
                );

                return true;
            }

            for (Estado otro
                    : automata.getEstados()) {

                if (otro != estado
                        && otro.getNombre()
                                .equals(nuevoNombre)) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Ya existe un estado con ese nombre."
                    );

                    return true;
                }
            }

            estado.setNombre(
                    nuevoNombre
            );

            grafo.getModel()
                    .beginUpdate();

            try {

                grafo.getModel()
                        .setValue(
                                celdaEstado,
                                nuevoNombre
                        );

            } finally {

                grafo.getModel()
                        .endUpdate();
            }

            grafo.refresh();

            return true;
        }

        // EDITAR TRANSICIÓN
        Transicion transicion =
                mapaTransiciones.get(
                        celda
                );

        if (transicion != null) {

            String nuevoSimbolo =
                    JOptionPane.showInputDialog(
                            this,
                            "Nuevo símbolo:",
                            transicion.getSimbolo()
                    );

            if (nuevoSimbolo == null) {
                return true;
            }

            nuevoSimbolo =
                    nuevoSimbolo.trim();

            if (!simboloValido(
                    nuevoSimbolo
            )) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ingrese un único símbolo válido."
                );

                return true;
            }

            for (Transicion otra
                    : automata.getTransiciones()) {

                if (otra != transicion
                        && otra.getOrigen()
                        == transicion.getOrigen()
                        && otra.getSimbolo()
                                .equals(nuevoSimbolo)) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Ya existe ese símbolo desde el mismo estado."
                    );

                    return true;
                }
            }

            transicion.setSimbolo(
                    nuevoSimbolo
            );

            grafo.getModel()
                    .beginUpdate();

            try {

                grafo.getModel()
                        .setValue(
                                celda,
                                nuevoSimbolo
                        );

            } finally {

                grafo.getModel()
                        .endUpdate();
            }

            grafo.refresh();

            return true;
        }

        return false;
    }

    // =====================================================
    // 15. ELIMINAR ELEMENTOS
    // =====================================================

    public boolean eliminarSeleccionado() {

        Object[] seleccionados =
                grafo.getSelectionCells();

        if (seleccionados.length == 0) {
            return false;
        }

        Set<Object> estadosEliminar =
                Collections.newSetFromMap(
                        new IdentityHashMap<
                                Object,
                                Boolean
                                >()
                );

        Set<Object> aristasEliminar =
                Collections.newSetFromMap(
                        new IdentityHashMap<
                                Object,
                                Boolean
                                >()
                );

        for (Object celda
                : seleccionados) {

            Object estadoCelda =
                    obtenerCeldaEstado(
                            celda
                    );

            if (estadoCelda != null) {

                estadosEliminar.add(
                        estadoCelda
                );

            } else if (
                    mapaTransiciones
                            .containsKey(celda)
            ) {

                aristasEliminar.add(
                        celda
                );
            }
        }

        for (Map.Entry<Object, Transicion> entrada
                : mapaTransiciones.entrySet()) {

            Transicion t =
                    entrada.getValue();

            for (Object celdaEstado
                    : estadosEliminar) {

                Estado estado =
                        mapaEstados.get(
                                celdaEstado
                        );

                if (t.getOrigen()
                        == estado
                        || t.getDestino()
                        == estado) {

                    aristasEliminar.add(
                            entrada.getKey()
                    );
                }
            }
        }

        if (estadosEliminar.isEmpty()
                && aristasEliminar.isEmpty()) {

            return false;
        }

        for (Object arista
                : aristasEliminar) {

            Transicion t =
                    mapaTransiciones.remove(
                            arista
                    );

            if (t != null) {

                automata.getTransiciones()
                        .remove(
                                t
                        );
            }
        }

        for (Object celdaEstado
                : estadosEliminar) {

            Estado estado =
                    mapaEstados.remove(
                            celdaEstado
                    );

            if (estado != null) {

                if (automata.getEstadoInicial()
                        == estado) {

                    automata.setEstadoInicial(
                            null
                    );

                    indicadorInicial = null;
                }

                automata.getEstados()
                        .remove(
                                estado
                        );
            }
        }

        Set<Object> graficos =
                Collections.newSetFromMap(
                        new IdentityHashMap<
                                Object,
                                Boolean
                                >()
                );

        graficos.addAll(
                estadosEliminar
        );

        graficos.addAll(
                aristasEliminar
        );

        grafo.getModel()
                .beginUpdate();

        try {

            grafo.removeCells(
                    graficos.toArray(),
                    true
            );

        } finally {

            grafo.getModel()
                    .endUpdate();
        }

        origenTransicion = null;

        grafo.clearSelection();

        grafo.refresh();

        return true;
    }

    // =====================================================
    // OBTENER CELDA VISUAL DE ESTADO
    // =====================================================

    public Object obtenerCeldaEstadoVisual(
            Estado estado
    ) {

        for (Map.Entry<Object, Estado> entrada
                : mapaEstados.entrySet()) {

            if (entrada.getValue()
                    == estado) {

                return entrada.getKey();
            }
        }

        return null;
    }

    // =====================================================
    // OBTENER CELDA VISUAL DE TRANSICIÓN
    // =====================================================

    public Object obtenerCeldaTransicionVisual(
            Transicion transicion
    ) {

        for (Map.Entry<Object, Transicion> entrada
                : mapaTransiciones.entrySet()) {

            if (entrada.getValue()
                    == transicion) {

                return entrada.getKey();
            }
        }

        return null;
    }

    // =====================================================
    // LIMPIAR RESALTADO
    // =====================================================

    public void limpiarResaltado() {

        grafo.getModel()
                .beginUpdate();

        try {

            for (Map.Entry<Object, Estado> entrada
                    : mapaEstados.entrySet()) {

                Estado estado =
                        entrada.getValue();

                String color;

                if (estado.isInicial()) {

                    color =
                            "#75C9CF";

                } else {

                    color =
                            "#FFFFFF";
                }

                grafo.setCellStyles(
                        mxConstants.STYLE_FILLCOLOR,
                        color,
                        new Object[]{
                            entrada.getKey()
                        }
                );

                grafo.setCellStyles(
                        mxConstants.STYLE_STROKECOLOR,
                        "#000000",
                        new Object[]{
                            entrada.getKey()
                        }
                );

                grafo.setCellStyles(
                        mxConstants.STYLE_STROKEWIDTH,
                        "2",
                        new Object[]{
                            entrada.getKey()
                        }
                );
            }

            for (Object arista
                    : mapaTransiciones.keySet()) {

                grafo.setCellStyles(
                        mxConstants.STYLE_STROKECOLOR,
                        "#000000",
                        new Object[]{arista}
                );

                grafo.setCellStyles(
                        mxConstants.STYLE_STROKEWIDTH,
                        "2",
                        new Object[]{arista}
                );
            }

        } finally {

            grafo.getModel()
                    .endUpdate();
        }

        grafo.refresh();
    }

    // =====================================================
    // RESALTAR ESTADO
    // =====================================================

    public void resaltarEstado(
            Estado estado
    ) {

        limpiarResaltado();

        Object celda =
                obtenerCeldaEstadoVisual(
                        estado
                );

        if (celda == null) {
            return;
        }

        grafo.getModel()
                .beginUpdate();

        try {

            grafo.setCellStyles(
                    mxConstants.STYLE_FILLCOLOR,
                    "#FFF59D",
                    new Object[]{celda}
            );

            grafo.setCellStyles(
                    mxConstants.STYLE_STROKECOLOR,
                    "#FF9800",
                    new Object[]{celda}
            );

            grafo.setCellStyles(
                    mxConstants.STYLE_STROKEWIDTH,
                    "3",
                    new Object[]{celda}
            );

        } finally {

            grafo.getModel()
                    .endUpdate();
        }

        grafo.refresh();
    }

    // =====================================================
    // RESALTAR PASO
    // =====================================================

    public void resaltarPaso(
            Estado destino,
            Transicion transicion
    ) {

        limpiarResaltado();

        Object celdaEstado =
                obtenerCeldaEstadoVisual(
                        destino
                );

        Object celdaTransicion =
                obtenerCeldaTransicionVisual(
                        transicion
                );

        grafo.getModel()
                .beginUpdate();

        try {

            if (celdaTransicion != null) {

                grafo.setCellStyles(
                        mxConstants.STYLE_STROKECOLOR,
                        "#F44336",
                        new Object[]{
                            celdaTransicion
                        }
                );

                grafo.setCellStyles(
                        mxConstants.STYLE_STROKEWIDTH,
                        "4",
                        new Object[]{
                            celdaTransicion
                        }
                );
            }

            if (celdaEstado != null) {

                grafo.setCellStyles(
                        mxConstants.STYLE_FILLCOLOR,
                        "#FFF59D",
                        new Object[]{
                            celdaEstado
                        }
                );

                grafo.setCellStyles(
                        mxConstants.STYLE_STROKECOLOR,
                        "#FF9800",
                        new Object[]{
                            celdaEstado
                        }
                );

                grafo.setCellStyles(
                        mxConstants.STYLE_STROKEWIDTH,
                        "3",
                        new Object[]{
                            celdaEstado
                        }
                );
            }

        } finally {

            grafo.getModel()
                    .endUpdate();
        }

        grafo.refresh();
    }

    // =====================================================
    // OBTENER POSICIONES ACTUALES DE LOS ESTADOS
    // =====================================================

    public Map<String, Point2D.Double> obtenerPosicionesEstados() {

        Map<String, Point2D.Double> posiciones =
                new LinkedHashMap<>();

        for (Map.Entry<Object, Estado> entrada
                : mapaEstados.entrySet()) {

            Object celda =
                    entrada.getKey();

            Estado estado =
                    entrada.getValue();

            mxGeometry geometria =
                    grafo.getModel()
                            .getGeometry(celda);

            if (geometria == null) {
                continue;
            }

            posiciones.put(
                    estado.getNombre(),
                    new Point2D.Double(
                            geometria.getX(),
                            geometria.getY()
                    )
            );
        }

        return posiciones;
    }
    // =====================================================
// REINICIAR AUTÓMATA
// =====================================================

public void reiniciarAutomata() {

    grafo.getModel().beginUpdate();

    try {

        // Quitar estado inicial del modelo
        automata.setEstadoInicial(
                null
        );

        // Vaciar transiciones y estados
        automata.getTransiciones()
                .clear();

        automata.getEstados()
                .clear();

        // Vaciar relaciones gráficas
        mapaTransiciones.clear();
        mapaEstados.clear();

        // Borrar todo del lienzo
        Object parent =
                grafo.getDefaultParent();

        Object[] elementos =
                grafo.getChildCells(
                        parent,
                        true,
                        true
                );

        grafo.removeCells(
                elementos,
                true
        );

        // Reiniciar variables
        indicadorInicial = null;
        origenTransicion = null;

        contadorEstados = 0;

        modoAgregar = false;
        modoTransicion = false;

        grafo.clearSelection();

    } finally {

        grafo.getModel().endUpdate();
    }

    grafo.setCellsMovable(
            true
    );

    grafo.refresh();
}
// =====================================================
// EXPORTAR AUTÓMATA A TXT
// =====================================================

public void exportarTxt(
        File archivo
) throws IOException {

    Properties datos =
            new Properties();

    datos.setProperty(
            "formato",
            "AUTOMATA_TXT_V1"
    );

    datos.setProperty(
            "cantidadEstados",
            String.valueOf(
                    automata.getEstados().size()
            )
    );

    datos.setProperty(
            "cantidadTransiciones",
            String.valueOf(
                    automata.getTransiciones().size()
            )
    );

    Map<String, Point2D.Double> posiciones =
            obtenerPosicionesEstados();

    // =================================================
    // GUARDAR ESTADOS
    // =================================================

    for (int i = 0;
            i < automata.getEstados().size();
            i++) {

        Estado estado =
                automata.getEstados()
                        .get(i);

        Point2D.Double posicion =
                posiciones.get(
                        estado.getNombre()
                );

        double x = 0;
        double y = 0;

        if (posicion != null) {

            x = posicion.x;
            y = posicion.y;
        }

        String prefijo =
                "estado." + i + ".";

        datos.setProperty(
                prefijo + "nombre",
                estado.getNombre()
        );

        datos.setProperty(
                prefijo + "x",
                String.valueOf(x)
        );

        datos.setProperty(
                prefijo + "y",
                String.valueOf(y)
        );

        datos.setProperty(
                prefijo + "inicial",
                String.valueOf(
                        estado.isInicial()
                )
        );

        datos.setProperty(
                prefijo + "final",
                String.valueOf(
                        estado.isAceptacion()
                )
        );
    }

    // =================================================
    // GUARDAR TRANSICIONES
    // =================================================

    for (int i = 0;
            i < automata.getTransiciones().size();
            i++) {

        Transicion transicion =
                automata.getTransiciones()
                        .get(i);

        String prefijo =
                "transicion." + i + ".";

        datos.setProperty(
                prefijo + "origen",
                transicion.getOrigen()
                        .getNombre()
        );

        datos.setProperty(
                prefijo + "destino",
                transicion.getDestino()
                        .getNombre()
        );

        datos.setProperty(
                prefijo + "simbolo",
                transicion.getSimbolo()
        );
    }

    // =================================================
    // ESCRIBIR ARCHIVO
    // =================================================

    try (
            BufferedWriter escritor =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(
                                            archivo
                                    ),
                                    StandardCharsets.UTF_8
                            )
                    )
    ) {

        datos.store(
                escritor,
                "Archivo generado por el Simulador de Autómatas"
        );
    }
}
// =====================================================
// IMPORTAR AUTÓMATA DESDE TXT
// =====================================================

public void importarTxt(File archivo) throws IOException {

    Properties datos = new Properties();

    try (
            BufferedReader lector =
                    new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(archivo),
                                    StandardCharsets.UTF_8
                            )
                    )
    ) {
        datos.load(lector);
    }

    String formato =
            datos.getProperty("formato");

    if (!"AUTOMATA_TXT_V1".equals(formato)) {
        throw new IOException(
                "El archivo no corresponde a un autómata válido."
        );
    }

    // Limpiar el autómata actual
    reiniciarAutomata();

    int cantidadEstados =
            Integer.parseInt(
                    datos.getProperty(
                            "cantidadEstados",
                            "0"
                    )
            );

    int cantidadTransiciones =
            Integer.parseInt(
                    datos.getProperty(
                            "cantidadTransiciones",
                            "0"
                    )
            );

    Map<String, Estado> estadosPorNombre =
            new HashMap<>();

    Map<String, Object> celdasPorNombre =
            new HashMap<>();

    Object parent =
            grafo.getDefaultParent();

    grafo.getModel().beginUpdate();

    try {

        // =================================================
        // RECREAR ESTADOS
        // =================================================

        for (int i = 0;
                i < cantidadEstados;
                i++) {

            String prefijo =
                    "estado." + i + ".";

            String nombre =
                    datos.getProperty(
                            prefijo + "nombre"
                    );

            double x =
                    Double.parseDouble(
                            datos.getProperty(
                                    prefijo + "x",
                                    "0"
                            )
                    );

            double y =
                    Double.parseDouble(
                            datos.getProperty(
                                    prefijo + "y",
                                    "0"
                            )
                    );

            boolean inicial =
                    Boolean.parseBoolean(
                            datos.getProperty(
                                    prefijo + "inicial",
                                    "false"
                            )
                    );

            boolean esFinal =
                    Boolean.parseBoolean(
                            datos.getProperty(
                                    prefijo + "final",
                                    "false"
                            )
                    );

            Estado estado =
                    new Estado(nombre);

            estado.setAceptacion(
                    esFinal
            );

            String forma =
                    esFinal
                    ? mxConstants.SHAPE_DOUBLE_ELLIPSE
                    : mxConstants.SHAPE_ELLIPSE;

            Object celda =
                    grafo.insertVertex(
                            parent,
                            null,
                            nombre,
                            x,
                            y,
                            60,
                            60,
                            "shape=" + forma + ";"
                            + "perimeter=ellipsePerimeter;"
                            + "fillColor=#FFFFFF;"
                            + "strokeColor=#000000;"
                            + "strokeWidth=2;"
                            + "fontColor=#000000;"
                            + "fontSize=14;"
                            + "align=center;"
                            + "verticalAlign=middle;"
                    );

            mapaEstados.put(
                    celda,
                    estado
            );

            automata.agregarEstado(
                    estado
            );

            estadosPorNombre.put(
                    nombre,
                    estado
            );

            celdasPorNombre.put(
                    nombre,
                    celda
            );

            if (inicial) {

                automata.setEstadoInicial(
                        estado
                );

                indicadorInicial =
                        grafo.insertVertex(
                                celda,
                                null,
                                "",
                                -25,
                                9,
                                25,
                                42,
                                "shape=triangle;"
                                + "direction=east;"
                                + "fillColor=#FFFFFF;"
                                + "strokeColor=#000000;"
                                + "strokeWidth=1.5;"
                                + "movable=0;"
                                + "resizable=0;"
                                + "connectable=0;"
                                + "selectable=0;"
                        );
            }
        }

        // =================================================
        // RECREAR TRANSICIONES
        // =================================================

        for (int i = 0;
                i < cantidadTransiciones;
                i++) {

            String prefijo =
                    "transicion." + i + ".";

            String nombreOrigen =
                    datos.getProperty(
                            prefijo + "origen"
                    );

            String nombreDestino =
                    datos.getProperty(
                            prefijo + "destino"
                    );

            String simbolo =
                    datos.getProperty(
                            prefijo + "simbolo"
                    );

            Estado origen =
                    estadosPorNombre.get(
                            nombreOrigen
                    );

            Estado destino =
                    estadosPorNombre.get(
                            nombreDestino
                    );

            Object celdaOrigen =
                    celdasPorNombre.get(
                            nombreOrigen
                    );

            Object celdaDestino =
                    celdasPorNombre.get(
                            nombreDestino
                    );

            if (origen == null
                    || destino == null
                    || celdaOrigen == null
                    || celdaDestino == null) {

                continue;
            }

            Transicion transicion =
                    new Transicion(
                            origen,
                            destino,
                            simbolo
                    );

            boolean esBucle =
                    origen == destino;

            String estilo =
                    "strokeColor=#000000;"
                    + "strokeWidth=2;"
                    + "endArrow=classic;"
                    + "endFill=1;"
                    + "fontSize=14;"
                    + "fontColor=#000000;"
                    + "labelBackgroundColor=#BACEE0;"
                    + "verticalLabelPosition=top;"
                    + "verticalAlign=bottom;"
                    + "spacingBottom=10;"
                    + "rounded=1;";

            if (esBucle) {

                estilo +=
                        "loopStyle=loopEdgeStyle;"
                        + "direction=north;"
                        + "segment=45;"
                        + "rounded=1;"
                        + "arcSize=100;"
                        + "orthogonalLoop=1;";
            }

            Object arista =
                    grafo.insertEdge(
                            parent,
                            null,
                            simbolo,
                            celdaOrigen,
                            celdaDestino,
                            estilo
                    );

            mapaTransiciones.put(
                    arista,
                    transicion
            );

            automata.agregarTransicion(
                    transicion
            );

            if (!esBucle) {

                organizarTransicionesOpuestas(
                        arista,
                        transicion
                );
            }
        }

        contadorEstados =
                cantidadEstados;

    } finally {

        grafo.getModel().endUpdate();
    }

    grafo.refresh();
}

}