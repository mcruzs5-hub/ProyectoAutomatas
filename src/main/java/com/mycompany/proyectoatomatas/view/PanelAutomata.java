
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
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

import javax.swing.JOptionPane;

public class PanelAutomata extends mxGraphComponent {

    // =====================================================
    // 1. ATRIBUTOS
    // =====================================================

    private final mxGraph grafo;
    private final Automata automata;

    // Relación entre elementos gráficos y matemáticos
    private final Map<Object, Estado> mapaEstados;

    private final Map<Object, Transicion> mapaTransiciones;

    private int contadorEstados = 0;

    // Modos de funcionamiento
    private boolean modoAgregar = false;
    private boolean modoTransicion = false;

    // Indicador triangular del estado inicial
    private Object indicadorInicial = null;

    // Origen temporal de una transición
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

        // Configuración del grafo
        grafo.setCellsEditable(false);
        grafo.setCellsResizable(false);
        grafo.setAllowDanglingEdges(false);
        grafo.setCellsMovable(true);
        grafo.setCellsSelectable(true);

        // Evitar desconexiones accidentales
        grafo.setCellsDisconnectable(false);
        grafo.setCellsBendable(false);

        // Configurar autotransiciones
        grafo.getStylesheet()
                .getDefaultEdgeStyle()
                .put(
                        mxConstants.STYLE_LOOP,
                        mxEdgeStyle.Loop
                );

        setConnectable(false);

        // Fondo blanco
        getViewport().setOpaque(true);
        getViewport().setBackground(Color.WHITE);
        getGraphControl().setBackground(Color.WHITE);

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

        getGraphControl().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }

                Object celda = getCellAt(
                        e.getX(),
                        e.getY()
                );

                // MODO CREAR TRANSICIONES
                if (modoTransicion) {

                    Object estadoCelda =
                            obtenerCeldaEstado(celda);

                    if (estadoCelda != null) {

                        procesarTransicion(estadoCelda);
                    }

                    return;
                }

                // MODO AGREGAR ESTADOS
                if (!modoAgregar) {
                    return;
                }

                // Evitar agregar encima de otro elemento
                if (celda != null) {
                    return;
                }

                double escala =
                        grafo.getView().getScale();

                double x = e.getX() / escala
                        - grafo.getView()
                                .getTranslate().getX();

                double y = e.getY() / escala
                        - grafo.getView()
                                .getTranslate().getY();

                crearEstado(x, y);
            }
        });
    }

    // =====================================================
    // 5. IDENTIFICAR CELDA DE ESTADO
    // =====================================================

    private Object obtenerCeldaEstado(Object celda) {

        Object actual = celda;

        while (actual != null) {

            if (mapaEstados.containsKey(actual)) {
                return actual;
            }

            actual = grafo.getModel().getParent(actual);
        }

        return null;
    }

    // =====================================================
    // 6. CREAR ESTADOS
    // =====================================================

    private void crearEstado(double x, double y) {

        String nombre = "q" + contadorEstados;

        Estado estado = new Estado(nombre);

        Object parent = grafo.getDefaultParent();

        grafo.getModel().beginUpdate();

        try {

            Object celda = grafo.insertVertex(
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

            mapaEstados.put(celda, estado);

            automata.agregarEstado(estado);

            contadorEstados++;

        } finally {

            grafo.getModel().endUpdate();
        }
    }

    // =====================================================
    // 7. OBTENER ESTADO SELECCIONADO
    // =====================================================

    public Estado getEstadoSeleccionado() {

        Object celda = obtenerCeldaEstado(
                grafo.getSelectionCell()
        );

        return mapaEstados.get(celda);
    }

    // =====================================================
    // 8. MARCAR ESTADO INICIAL
    // =====================================================

    public boolean marcarInicial() {

        Object celda = obtenerCeldaEstado(
                grafo.getSelectionCell()
        );

        Estado seleccionado = mapaEstados.get(celda);

        if (seleccionado == null) {
            return false;
        }

        grafo.getModel().beginUpdate();

        try {

            // Eliminar indicador anterior
            if (indicadorInicial != null) {

                grafo.removeCells(
                        new Object[]{indicadorInicial}
                );

                indicadorInicial = null;
            }

            // Actualizar modelo matemático
            automata.setEstadoInicial(seleccionado);

            // Crear triángulo externo
            indicadorInicial = grafo.insertVertex(
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

            grafo.getModel().endUpdate();
        }

        grafo.refresh();

        return true;
    }

    // =====================================================
    // 9. MARCAR O DESMARCAR ESTADO FINAL
    // =====================================================

    public boolean marcarFinal() {

        Object celda = obtenerCeldaEstado(
                grafo.getSelectionCell()
        );

        Estado seleccionado = mapaEstados.get(celda);

        if (seleccionado == null) {
            return false;
        }

        seleccionado.setAceptacion(
                !seleccionado.isAceptacion()
        );

        grafo.getModel().beginUpdate();

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

            grafo.getModel().endUpdate();
        }

        grafo.refresh();

        return true;
    }

    // =====================================================
    // 10. VALIDAR SÍMBOLOS
    // =====================================================

private boolean simboloValido(String simbolo) {

    if (simbolo == null || simbolo.isEmpty()) {
        return false;
    }

    // No permitimos epsilon como transición consumible de un AFD
    if (simbolo.equals("ε")) {
        return false;
    }

    // Aceptar exactamente un símbolo Unicode
    return simbolo.codePointCount(
            0,
            simbolo.length()
    ) == 1;
}
    // =====================================================
    // 11. CREAR TRANSICIONES
    // =====================================================

    private void procesarTransicion(Object celda) {

        // PRIMER CLIC: ORIGEN
        if (origenTransicion == null) {

            origenTransicion = celda;

            Estado origen = mapaEstados.get(celda);

            System.out.println(
                    "Origen seleccionado: "
                    + origen.getNombre()
            );

            return;
        }

        // SEGUNDO CLIC: DESTINO
        Object destinoCelda = celda;

        Estado origen = mapaEstados.get(origenTransicion);
        Estado destino = mapaEstados.get(destinoCelda);

        String simbolo = JOptionPane.showInputDialog(
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

        simbolo = simbolo.trim();

        // Validar símbolo
        if (!simboloValido(simbolo)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un único símbolo válido.\n"
                    + "Un AFD no utiliza transiciones epsilon."
            );

            origenTransicion = null;
            return;
        }

        // Evitar transiciones duplicadas
        for (Transicion t : automata.getTransiciones()) {

            if (t.getOrigen() == origen
                    && t.getSimbolo().equals(simbolo)) {

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

        // Crear transición matemática
        Transicion nueva = new Transicion(
                origen,
                destino,
                simbolo
        );

        boolean esBucle =
                origenTransicion == destinoCelda;

        Object parent = grafo.getDefaultParent();

        grafo.getModel().beginUpdate();

        try {

            // Estilo general
            String estilo =
                    "strokeColor=#000000;"
                    + "strokeWidth=2;"
                    + "endArrow=classic;"
                    + "endFill=1;"
                    + "fontSize=14;"
                    + "fontColor=#000000;"
                    + "rounded=0;";

            // Autotransición superior
            if (esBucle) {

                estilo +=
                        "loopStyle=loopEdgeStyle;"
                        + "direction=north;"
                        + "segment=30;"
                        + "rounded=1;";
            }

            // Crear arista gráfica
            Object arista = grafo.insertEdge(
                    parent,
                    null,
                    simbolo,
                    origenTransicion,
                    destinoCelda,
                    estilo
            );

            // Relacionar arista y transición matemática
            mapaTransiciones.put(arista, nueva);

            automata.agregarTransicion(nueva);

            // NUEVO: separar las flechas opuestas
            if (!esBucle) {

                organizarTransicionesOpuestas(
                        arista,
                        nueva
                );
            }

        } finally {

            grafo.getModel().endUpdate();
        }

        origenTransicion = null;

        grafo.refresh();
    }

    // =====================================================
    // 12. ORGANIZAR TRANSICIONES DE IDA Y VUELTA
    // =====================================================

    private void organizarTransicionesOpuestas(
            Object aristaNueva,
            Transicion nueva) {

        for (Map.Entry<Object, Transicion> entrada
                : mapaTransiciones.entrySet()) {

            Object otraArista = entrada.getKey();

            Transicion otra = entrada.getValue();

            // No comparar consigo misma
            if (otraArista == aristaNueva) {
                continue;
            }

            // Detectar transición inversa
            boolean esInversa =
                    otra.getOrigen() == nueva.getDestino()
                    && otra.getDestino() == nueva.getOrigen();

            if (esInversa
                    && nueva.getOrigen() != nueva.getDestino()) {

                // Separar ambas flechas
                curvarArista(otraArista);
                curvarArista(aristaNueva);

                break;
            }
        }
    }

    // =====================================================
    // 13. CURVAR UNA ARISTA
    // =====================================================

    private void curvarArista(Object arista) {

        // Obtener origen y destino gráficos
        Object origen = grafo.getModel()
                .getTerminal(arista, true);

        Object destino = grafo.getModel()
                .getTerminal(arista, false);

        mxGeometry geoOrigen =
                grafo.getModel().getGeometry(origen);

        mxGeometry geoDestino =
                grafo.getModel().getGeometry(destino);

        // Centros de los dos estados
        double x1 = geoOrigen.getX()
                + geoOrigen.getWidth() / 2;

        double y1 = geoOrigen.getY()
                + geoOrigen.getHeight() / 2;

        double x2 = geoDestino.getX()
                + geoDestino.getWidth() / 2;

        double y2 = geoDestino.getY()
                + geoDestino.getHeight() / 2;

        // Vector de dirección
        double dx = x2 - x1;
        double dy = y2 - y1;

        double distancia = Math.hypot(dx, dy);

        if (distancia < 1) {
            return;
        }

        // Punto medio entre los dos estados
        double medioX = (x1 + x2) / 2;
        double medioY = (y1 + y2) / 2;

        // Separación perpendicular a la dirección
        double separacion = 42;

        // La dirección inversa cambia el signo del vector.
        // Por eso una flecha queda arriba y la otra abajo.
        double controlX =
                medioX - (dy / distancia) * separacion;

        double controlY =
                medioY + (dx / distancia) * separacion;

        // Copiar geometría de la arista
        mxGeometry geometria = (mxGeometry)
                grafo.getModel()
                        .getGeometry(arista).clone();

        // Aplicar punto de control
        geometria.setPoints(
                Collections.singletonList(
                        new mxPoint(controlX, controlY)
                )
        );

        // Permitir que JGraphX respete el punto de control
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

        grafo.getModel().setGeometry(
                arista,
                geometria
        );
    }

    // =====================================================
    // 14. EDITAR ESTADOS Y TRANSICIONES
    // =====================================================

    public boolean editarSeleccionado() {

        Object celda = grafo.getSelectionCell();

        if (celda == null) {
            return false;
        }

        Object celdaEstado = obtenerCeldaEstado(celda);

        // EDITAR ESTADO
        if (celdaEstado != null) {

            Estado estado = mapaEstados.get(celdaEstado);

            String nuevoNombre = JOptionPane.showInputDialog(
                    this,
                    "Nuevo nombre del estado:",
                    estado.getNombre()
            );

            if (nuevoNombre == null) {
                return true;
            }

            nuevoNombre = nuevoNombre.trim();

            if (nuevoNombre.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "El nombre no puede estar vacío."
                );

                return true;
            }

            // Evitar nombres repetidos
            for (Estado otro : automata.getEstados()) {

                if (otro != estado
                        && otro.getNombre().equals(nuevoNombre)) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Ya existe un estado con ese nombre."
                    );

                    return true;
                }
            }

            estado.setNombre(nuevoNombre);

            grafo.getModel().beginUpdate();

            try {

                grafo.getModel().setValue(
                        celdaEstado,
                        nuevoNombre
                );

            } finally {

                grafo.getModel().endUpdate();
            }

            grafo.refresh();

            return true;
        }

        // EDITAR TRANSICIÓN
        Transicion transicion =
                mapaTransiciones.get(celda);

        if (transicion != null) {

            String nuevoSimbolo = JOptionPane.showInputDialog(
                    this,
                    "Nuevo símbolo:",
                    transicion.getSimbolo()
            );

            if (nuevoSimbolo == null) {
                return true;
            }

            nuevoSimbolo = nuevoSimbolo.trim();

            if (!simboloValido(nuevoSimbolo)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ingrese un único símbolo válido."
                );

                return true;
            }

            // Conservar determinismo
            for (Transicion otra : automata.getTransiciones()) {

                if (otra != transicion
                        && otra.getOrigen() == transicion.getOrigen()
                        && otra.getSimbolo().equals(nuevoSimbolo)) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Ya existe ese símbolo desde el mismo estado."
                    );

                    return true;
                }
            }

            transicion.setSimbolo(nuevoSimbolo);

            grafo.getModel().beginUpdate();

            try {

                grafo.getModel().setValue(
                        celda,
                        nuevoSimbolo
                );

            } finally {

                grafo.getModel().endUpdate();
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

        Object[] seleccionados = grafo.getSelectionCells();

        if (seleccionados.length == 0) {
            return false;
        }

        Set<Object> estadosEliminar =
                Collections.newSetFromMap(
                        new IdentityHashMap<Object, Boolean>()
                );

        Set<Object> aristasEliminar =
                Collections.newSetFromMap(
                        new IdentityHashMap<Object, Boolean>()
                );

        // Identificar selección
        for (Object celda : seleccionados) {

            Object estadoCelda =
                    obtenerCeldaEstado(celda);

            if (estadoCelda != null) {

                estadosEliminar.add(estadoCelda);

            } else if (mapaTransiciones.containsKey(celda)) {

                aristasEliminar.add(celda);
            }
        }

        // Buscar transiciones relacionadas con estados eliminados
        for (Map.Entry<Object, Transicion> entrada
                : mapaTransiciones.entrySet()) {

            Transicion t = entrada.getValue();

            for (Object celdaEstado : estadosEliminar) {

                Estado estado = mapaEstados.get(celdaEstado);

                if (t.getOrigen() == estado
                        || t.getDestino() == estado) {

                    aristasEliminar.add(entrada.getKey());
                }
            }
        }

        if (estadosEliminar.isEmpty()
                && aristasEliminar.isEmpty()) {

            return false;
        }

        // Eliminar transiciones matemáticas
        for (Object arista : aristasEliminar) {

            Transicion t = mapaTransiciones.remove(arista);

            if (t != null) {

                automata.getTransiciones().remove(t);
            }
        }

        // Eliminar estados matemáticos
        for (Object celdaEstado : estadosEliminar) {

            Estado estado = mapaEstados.remove(celdaEstado);

            if (estado != null) {

                if (automata.getEstadoInicial() == estado) {

                    automata.setEstadoInicial(null);
                    indicadorInicial = null;
                }

                automata.getEstados().remove(estado);
            }
        }

        // Eliminar elementos gráficos
        Set<Object> graficos =
                Collections.newSetFromMap(
                        new IdentityHashMap<Object, Boolean>()
                );

        graficos.addAll(estadosEliminar);
        graficos.addAll(aristasEliminar);

        grafo.getModel().beginUpdate();

        try {

            grafo.removeCells(
                    graficos.toArray(),
                    true
            );

        } finally {

            grafo.getModel().endUpdate();
        }

        origenTransicion = null;

        grafo.clearSelection();
        grafo.refresh();

        return true;
    }
    
    public Object obtenerCeldaEstadoVisual(Estado estado) {

    for (Map.Entry<Object, Estado> entrada
            : mapaEstados.entrySet()) {

        if (entrada.getValue() == estado) {
            return entrada.getKey();
        }
    }

    return null;
}

public Object obtenerCeldaTransicionVisual(
        Transicion transicion
) {

    for (Map.Entry<Object, Transicion> entrada
            : mapaTransiciones.entrySet()) {

        if (entrada.getValue() == transicion) {
            return entrada.getKey();
        }
    }

    return null;
}

public void limpiarResaltado() {

    grafo.getModel().beginUpdate();

    try {

        // Restaurar estados
        for (Map.Entry<Object, Estado> entrada
                : mapaEstados.entrySet()) {

            Estado estado = entrada.getValue();

            String color;

            if (estado.isInicial()) {
                color = "#75C9CF";
            } else {
                color = "#FFFFFF";
            }

            grafo.setCellStyles(
                    mxConstants.STYLE_FILLCOLOR,
                    color,
                    new Object[]{entrada.getKey()}
            );

            grafo.setCellStyles(
                    mxConstants.STYLE_STROKECOLOR,
                    "#000000",
                    new Object[]{entrada.getKey()}
            );

            grafo.setCellStyles(
                    mxConstants.STYLE_STROKEWIDTH,
                    "2",
                    new Object[]{entrada.getKey()}
            );
        }

        // Restaurar transiciones
        for (Object arista : mapaTransiciones.keySet()) {

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

        grafo.getModel().endUpdate();
    }

    grafo.refresh();
}
    public void resaltarEstado(Estado estado) {

    limpiarResaltado();

    Object celda =
            obtenerCeldaEstadoVisual(estado);

    if (celda == null) {
        return;
    }

    grafo.getModel().beginUpdate();

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

        grafo.getModel().endUpdate();
    }

    grafo.refresh();
}
    public void resaltarPaso(
        Estado destino,
        Transicion transicion
) {

    limpiarResaltado();

    Object celdaEstado =
            obtenerCeldaEstadoVisual(destino);

    Object celdaTransicion =
            obtenerCeldaTransicionVisual(transicion);

    grafo.getModel().beginUpdate();

    try {

        // Flecha utilizada
        if (celdaTransicion != null) {

            grafo.setCellStyles(
                    mxConstants.STYLE_STROKECOLOR,
                    "#F44336",
                    new Object[]{celdaTransicion}
            );

            grafo.setCellStyles(
                    mxConstants.STYLE_STROKEWIDTH,
                    "4",
                    new Object[]{celdaTransicion}
            );
        }

        // Estado actual
        if (celdaEstado != null) {

            grafo.setCellStyles(
                    mxConstants.STYLE_FILLCOLOR,
                    "#FFF59D",
                    new Object[]{celdaEstado}
            );

            grafo.setCellStyles(
                    mxConstants.STYLE_STROKECOLOR,
                    "#FF9800",
                    new Object[]{celdaEstado}
            );

            grafo.setCellStyles(
                    mxConstants.STYLE_STROKEWIDTH,
                    "3",
                    new Object[]{celdaEstado}
            );
        }

    } finally {

        grafo.getModel().endUpdate();
    }

    grafo.refresh();
}
}
