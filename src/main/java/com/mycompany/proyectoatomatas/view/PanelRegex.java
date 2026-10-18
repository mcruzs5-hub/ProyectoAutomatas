package com.mycompany.proyectoatomatas.view;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

import com.mycompany.proyectoatomatas.algorithm.ConversorRegex.AristaRegex;
import com.mycompany.proyectoatomatas.algorithm.ConversorRegex.PasoEliminacion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class PanelRegex extends mxGraphComponent {

    private final mxGraph grafo;

    /*
     * Posiciones que el usuario eligió en PanelAutomata.
     * De esta forma la conversión conserva la distribución
     * visual del AFD original.
     */
    private final Map<String, Point2D.Double> posicionesOriginales;

    private PasoEliminacion pasoActual;

    private boolean ajusteAutomatico = true;

    private static final double ZOOM_MINIMO = 0.25;
    private static final double ZOOM_MAXIMO = 3.00;
    private static final double FACTOR_ZOOM = 1.15;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public PanelRegex(
            Map<String, Point2D.Double> posicionesOriginales
    ) {

        super(new mxGraph());

        grafo = getGraph();

        this.posicionesOriginales =
                posicionesOriginales != null
                        ? new LinkedHashMap<>(posicionesOriginales)
                        : new LinkedHashMap<>();

        grafo.setCellsEditable(false);
        grafo.setCellsMovable(false);
        grafo.setCellsResizable(false);
        grafo.setAllowDanglingEdges(false);
        grafo.setCellsDisconnectable(false);
        grafo.setCellsBendable(false);

        setConnectable(false);
        setCenterZoom(true);

        getViewport().setOpaque(true);
        getViewport().setBackground(Color.WHITE);
        getGraphControl().setBackground(Color.WHITE);

        configurarZoom();
        configurarRedimensionamiento();
    }

    // =====================================================
    // MOSTRAR UN PASO
    // =====================================================

    public void mostrarPaso(
            PasoEliminacion paso
    ) {

        this.pasoActual = paso;

        Object parent =
                grafo.getDefaultParent();

        List<String> estadosOrdenados =
                ordenarEstados(
                        paso.getEstados()
                );

        Set<String> clavesAristas =
                new HashSet<>();

        for (AristaRegex arista
                : paso.getAristas()) {

            clavesAristas.add(
                    arista.getOrigen()
                    + "->"
                    + arista.getDestino()
            );
        }

        grafo.getModel().beginUpdate();

        try {

            // =================================================
            // LIMPIAR DIBUJO ANTERIOR
            // =================================================

            Object[] anteriores =
                    grafo.getChildCells(
                            parent,
                            true,
                            true
                    );

            if (anteriores != null
                    && anteriores.length > 0) {

                grafo.removeCells(
                        anteriores
                );
            }

            Map<String, Object> celdas =
                    new LinkedHashMap<>();

            // =================================================
            // DATOS PARA q_start Y q_accept
            // =================================================

            double minimoX =
                    posicionesOriginales
                            .values()
                            .stream()
                            .mapToDouble(
                                    p -> p.x
                            )
                            .min()
                            .orElse(300);

            double maximoX =
                    posicionesOriginales
                            .values()
                            .stream()
                            .mapToDouble(
                                    p -> p.x
                            )
                            .max()
                            .orElse(700);

            double promedioY =
                    posicionesOriginales
                            .values()
                            .stream()
                            .mapToDouble(
                                    p -> p.y
                            )
                            .average()
                            .orElse(260);

            // =================================================
            // DIBUJAR ESTADOS
            // =================================================

            for (String nombre
                    : estadosOrdenados) {

                double x;
                double y;

                Point2D.Double posicion =
                        posicionesOriginales.get(
                                nombre
                        );

                if (posicion != null) {

                    /*
                     * Estado original:
                     * conservar exactamente su ubicación.
                     */
                    x = posicion.x;
                    y = posicion.y - 80;

                } else if (
                        "q_start".equals(nombre)
                ) {

                    /*
                     * Estado auxiliar inicial:
                     * se coloca a la izquierda del AFD.
                     */
                    x = minimoX - 170;
                    y = promedioY - 80;

                    
                } else if (
                        "q_accept".equals(nombre)
                ) {

                    /*
                     * Estado auxiliar final:
                     * se coloca a la derecha del AFD.
                     */
                    x = maximoX + 170;
                    y = promedioY - 80;

                } else {

                    /*
                     * Respaldo por si apareciera algún estado
                     * sin posición registrada.
                     */
                    x = 400;
                    y = 260;
                }

                String estilo =
                        estiloEstado(
                                nombre
                        );

                Object celda =
                        grafo.insertVertex(
                                parent,
                                null,
                                nombre,
                                x,
                                y,
                                100,
                                60,
                                estilo
                        );

                celdas.put(
                        nombre,
                        celda
                );
            }

            // =================================================
            // DIBUJAR ARISTAS
            // =================================================

            for (AristaRegex arista
                    : paso.getAristas()) {

                Object origen =
                        celdas.get(
                                arista.getOrigen()
                        );

                Object destino =
                        celdas.get(
                                arista.getDestino()
                        );

                if (origen == null
                        || destino == null) {

                    continue;
                }

                boolean esLoop =
                        arista.getOrigen()
                                .equals(
                                        arista.getDestino()
                                );

                String estilo =
                        "strokeColor=#222222;"
                        + "strokeWidth=2;"
                        + "endArrow=classic;"
                        + "endFill=1;"
                        + "fontSize=14;"
                        + "fontColor=#000000;"
                        + "labelBackgroundColor=#FFFFFF;"
                        + "rounded=0;";

                if (esLoop) {

                    estilo +=
                            "loopStyle=loopEdgeStyle;"
                            + "orthogonalLoop=1;"
                            + "direction=north;"
                            + "segment=45;";
                }

                Object aristaVisual =
                        grafo.insertEdge(
                                parent,
                                null,
                                arista.getExpresion(),
                                origen,
                                destino,
                                estilo
                        );

                if (!esLoop) {

                    boolean tieneInversa =
                            clavesAristas.contains(
                                    arista.getDestino()
                                    + "->"
                                    + arista.getOrigen()
                            );

                    double alturaCurva;

                    if (tieneInversa) {

                        /*
                         * Estilo JFLAP:
                         * ida por un lado y regreso por el otro.
                         */
                        alturaCurva =
                                arista.getOrigen()
                                        .compareTo(
                                                arista.getDestino()
                                        ) < 0
                                        ? -48
                                        : 48;

                    } else {

                        /*
                         * Una sola transición usa un arco
                         * ligero para evitar líneas rígidas.
                         */
                        alturaCurva = -22;
                    }

                    curvarAristaSuave(
                            aristaVisual,
                            origen,
                            destino,
                            alturaCurva
                    );
                }
            }

        } finally {

            grafo.getModel().endUpdate();
        }

        grafo.refresh();

        SwingUtilities.invokeLater(() -> {

            ajustarDiagramaAVentana();
        });
    }

    // =====================================================
    // ESTILO DE LOS ESTADOS
    // =====================================================

    private String estiloEstado(
            String nombre
    ) {

        if ("q_start".equals(nombre)) {

            return
                    "shape=ellipse;"
                    + "fillColor=#E8F5E9;"
                    + "strokeColor=#2E7D32;"
                    + "strokeWidth=3;"
                    + "fontSize=15;"
                    + "fontColor=#6D4C41;";
        }

        if ("q_accept".equals(nombre)) {

            return
                    "shape=doubleEllipse;"
                    + "fillColor=#E3F2FD;"
                    + "strokeColor=#1565C0;"
                    + "strokeWidth=3;"
                    + "fontSize=15;"
                    + "fontColor=#6D4C41;";
        }

        return
                "shape=ellipse;"
                + "fillColor=#FFFFFF;"
                + "strokeColor=#222222;"
                + "strokeWidth=2;"
                + "fontSize=15;"
                + "fontColor=#6D4C41;";
    }

    // =====================================================
    // ORDENAR ESTADOS
    // =====================================================

    private List<String> ordenarEstados(
            List<String> originales
    ) {

        List<String> ordenados =
                new ArrayList<>(
                        originales
                );

        ordenados.sort((a, b) -> {

            if ("q_start".equals(a)) {
                return -1;
            }

            if ("q_start".equals(b)) {
                return 1;
            }

            if ("q_accept".equals(a)) {
                return 1;
            }

            if ("q_accept".equals(b)) {
                return -1;
            }

            return Integer.compare(
                    extraerNumeroEstado(a),
                    extraerNumeroEstado(b)
            );
        });

        return ordenados;
    }

    private int extraerNumeroEstado(
            String nombre
    ) {

        try {

            if (nombre.startsWith("q")) {

                return Integer.parseInt(
                        nombre.substring(1)
                );
            }

        } catch (NumberFormatException ex) {

            // Si el estado fue renombrado,
            // se conserva al final del orden lógico.
        }

        return Integer.MAX_VALUE;
    }

    // =====================================================
    // CURVA SUAVE TIPO JFLAP
    // =====================================================

    private void curvarAristaSuave(
            Object arista,
            Object origen,
            Object destino,
            double altura
    ) {

        mxGeometry geoOrigen =
                grafo.getModel()
                        .getGeometry(origen);

        mxGeometry geoDestino =
                grafo.getModel()
                        .getGeometry(destino);

        if (geoOrigen == null
                || geoDestino == null) {

            return;
        }

        double x1 =
                geoOrigen.getX()
                + geoOrigen.getWidth() / 2.0;

        double y1 =
                geoOrigen.getY()
                + geoOrigen.getHeight() / 2.0;

        double x2 =
                geoDestino.getX()
                + geoDestino.getWidth() / 2.0;

        double y2 =
                geoDestino.getY()
                + geoDestino.getHeight() / 2.0;

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

        double perpendicularX =
                -dy / distancia;

        double perpendicularY =
                dx / distancia;

        /*
         * Varios puntos intermedios generan un arco
         * progresivo y mucho más suave que un solo
         * punto de control.
         */
        List<mxPoint> puntos =
                new ArrayList<>();

        double[] porcentajes =
                {
                    0.18,
                    0.32,
                    0.50,
                    0.68,
                    0.82
                };

        for (double t
                : porcentajes) {

            /*
             * Seno:
             * 0 en los extremos y máximo en el centro.
             */
            double factor =
                    Math.sin(
                            Math.PI * t
                    );

            double puntoX =
                    x1
                    + dx * t
                    + perpendicularX
                    * altura
                    * factor;

            double puntoY =
                    y1
                    + dy * t
                    + perpendicularY
                    * altura
                    * factor;

            puntos.add(
                    new mxPoint(
                            puntoX,
                            puntoY
                    )
            );
        }

        mxGeometry geometria =
                (mxGeometry)
                grafo.getModel()
                        .getGeometry(arista)
                        .clone();

        geometria.setPoints(
                puntos
        );

        grafo.getModel()
                .setGeometry(
                        arista,
                        geometria
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
    }

    // =====================================================
    // AJUSTAR DIAGRAMA A LA VENTANA
    // =====================================================

    public void ajustarDiagramaAVentana() {

    if (grafo.getGraphBounds() == null) {
        return;
    }

    Dimension disponible =
            getViewport().getExtentSize();

    if (disponible.width <= 0
            || disponible.height <= 0) {
        return;
    }

    // =====================================================
    // 1. REINICIAR TRASLACIÓN
    // =====================================================

    grafo.getView().setTranslate(
            new mxPoint(0, 0)
    );

    grafo.refresh();

    double escalaActual =
            grafo.getView().getScale();

    if (escalaActual <= 0) {
        escalaActual = 1;
    }

    // =====================================================
    // 2. OBTENER TAMAÑO LÓGICO DEL GRAFO
    // =====================================================

    double anchoGrafo =
            grafo.getGraphBounds().getWidth()
            / escalaActual;

    double altoGrafo =
            grafo.getGraphBounds().getHeight()
            / escalaActual;

    if (anchoGrafo <= 0
            || altoGrafo <= 0) {
        return;
    }

    // =====================================================
    // 3. CALCULAR ESCALA PARA QUE TODO QUEPA
    // =====================================================

    double margenHorizontal = 180;
    double margenVertical = 140;

    double anchoDisponible =
            disponible.width
            - margenHorizontal;

    double altoDisponible =
            disponible.height
            - margenVertical;

    double escalaX =
            anchoDisponible
            / anchoGrafo;

    double escalaY =
            altoDisponible
            / altoGrafo;

    double nuevaEscala =
            Math.min(
                    escalaX,
                    escalaY
            );

    nuevaEscala =
            Math.max(
                    ZOOM_MINIMO,
                    Math.min(
                            nuevaEscala,
                            1.50
                    )
            );

    grafo.getView().setScale(
            nuevaEscala
    );

    grafo.refresh();

    // =====================================================
    // 4. OBTENER BOUNDS YA ESCALADOS
    // =====================================================

    com.mxgraph.util.mxRectangle bounds =
        grafo.getGraphBounds();
    // =====================================================
    // 5. CALCULAR CENTRO REAL
    // =====================================================

    double espacioX =
            disponible.width
            - bounds.getWidth();

    double espacioY =
            disponible.height
            - bounds.getHeight();

    double desplazamientoXPixeles =
            (espacioX / 2.0)
            - bounds.getX();

    double desplazamientoYPixeles =
            (espacioY / 2.0)
            - bounds.getY();

    // La traslación de mxGraph trabaja en unidades
    // anteriores a la escala.
    double trasladarX =
            desplazamientoXPixeles
            / nuevaEscala;

    double trasladarY =
            desplazamientoYPixeles
            / nuevaEscala;

    // =====================================================
    // 6. APLICAR CENTRADO
    // =====================================================

    grafo.getView().setTranslate(
            new mxPoint(
                    trasladarX,
                    trasladarY
            )
    );

    grafo.refresh();
}

    // =====================================================
    // REDIMENSIONAMIENTO AUTOMÁTICO
    // =====================================================

    private void configurarRedimensionamiento() {

        addComponentListener(
                new ComponentAdapter() {

                    @Override
                    public void componentResized(
                            ComponentEvent e
                    ) {

                        if (!ajusteAutomatico) {
                            return;
                        }

                        SwingUtilities.invokeLater(() -> {

                            if (pasoActual != null) {

                                ajustarDiagramaAVentana();
                            }
                        });
                    }
                }
        );
    }

    // =====================================================
    // CONFIGURAR ZOOM
    // =====================================================

    private void configurarZoom() {

        // CTRL + rueda del mouse
        addMouseWheelListener(
                (MouseWheelEvent e) -> {

                    if ((e.getModifiersEx()
                            & InputEvent.CTRL_DOWN_MASK)
                            == 0) {

                        return;
                    }

                    e.consume();

                    ajusteAutomatico = false;

                    if (e.getWheelRotation() < 0) {

                        acercar();

                    } else {

                        alejar();
                    }
                }
        );

        JComponent componente =
                getGraphControl();

        int condicion =
                JComponent
                        .WHEN_IN_FOCUSED_WINDOW;

        // CTRL +
        componente
                .getInputMap(condicion)
                .put(
                        KeyStroke.getKeyStroke(
                                KeyEvent.VK_PLUS,
                                InputEvent.CTRL_DOWN_MASK
                        ),
                        "zoomIn"
                );

        // CTRL = también funciona como CTRL +
        componente
                .getInputMap(condicion)
                .put(
                        KeyStroke.getKeyStroke(
                                KeyEvent.VK_EQUALS,
                                InputEvent.CTRL_DOWN_MASK
                        ),
                        "zoomIn"
                );

        // CTRL -
        componente
                .getInputMap(condicion)
                .put(
                        KeyStroke.getKeyStroke(
                                KeyEvent.VK_MINUS,
                                InputEvent.CTRL_DOWN_MASK
                        ),
                        "zoomOut"
                );

        // CTRL 0
        componente
                .getInputMap(condicion)
                .put(
                        KeyStroke.getKeyStroke(
                                KeyEvent.VK_0,
                                InputEvent.CTRL_DOWN_MASK
                        ),
                        "zoomReset"
                );

        componente
                .getActionMap()
                .put(
                        "zoomIn",
                        new AbstractAction() {

                            @Override
                            public void actionPerformed(
                                    java.awt.event.ActionEvent e
                            ) {

                                ajusteAutomatico = false;

                                acercar();
                            }
                        }
                );

        componente
                .getActionMap()
                .put(
                        "zoomOut",
                        new AbstractAction() {

                            @Override
                            public void actionPerformed(
                                    java.awt.event.ActionEvent e
                            ) {

                                ajusteAutomatico = false;

                                alejar();
                            }
                        }
                );

        componente
                .getActionMap()
                .put(
                        "zoomReset",
                        new AbstractAction() {

                            @Override
                            public void actionPerformed(
                                    java.awt.event.ActionEvent e
                            ) {

                                ajusteAutomatico = true;

                                ajustarDiagramaAVentana();
                            }
                        }
                );
    }

    // =====================================================
    // ACERCAR
    // =====================================================

    private void acercar() {

        double escala =
                grafo.getView()
                        .getScale();

        escala *= FACTOR_ZOOM;

        escala =
                Math.min(
                        escala,
                        ZOOM_MAXIMO
                );

        zoomTo(
                escala,
                true
        );
    }

    // =====================================================
    // ALEJAR
    // =====================================================

    private void alejar() {

        double escala =
                grafo.getView()
                        .getScale();

        escala /= FACTOR_ZOOM;

        escala =
                Math.max(
                        escala,
                        ZOOM_MINIMO
                );

        zoomTo(
                escala,
                true
        );
    }
}
