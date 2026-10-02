package com.mycompany.proyectoatomatas.view;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

import com.mycompany.proyectoatomatas.algorithm.ConversorRegex.AristaRegex;
import com.mycompany.proyectoatomatas.algorithm.ConversorRegex.PasoEliminacion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PanelRegex extends mxGraphComponent {

    private final mxGraph grafo;

    public PanelRegex() {

        super(new mxGraph());

        grafo = getGraph();

        grafo.setCellsEditable(false);
        grafo.setCellsMovable(false);
        grafo.setCellsResizable(false);
        grafo.setAllowDanglingEdges(false);
        grafo.setCellsDisconnectable(false);
        grafo.setCellsBendable(false);

        setConnectable(false);

        getViewport().setOpaque(true);
        getViewport().setBackground(Color.WHITE);
        getGraphControl().setBackground(Color.WHITE);
    }

    // =====================================================
    // MOSTRAR UN PASO
    // =====================================================

    public void mostrarPaso(PasoEliminacion paso) {

        Object parent = grafo.getDefaultParent();

        List<String> estadosOrdenados =
                ordenarEstados(paso.getEstados());

        Set<String> clavesAristas =
                new HashSet<>();

        for (AristaRegex arista : paso.getAristas()) {
            clavesAristas.add(
                    arista.getOrigen() + "->" + arista.getDestino()
            );
        }

        grafo.getModel().beginUpdate();

        try {

            // Limpiar dibujo anterior
            Object[] anteriores =
                    grafo.getChildCells(parent, true, true);

            if (anteriores != null && anteriores.length > 0) {
                grafo.removeCells(anteriores);
            }

            Map<String, Object> celdas =
                    new LinkedHashMap<>();

            int anchoPanel = Math.max(getWidth(), 1100);
            int altoPanel = Math.max(getHeight(), 500);

            double yCentro = altoPanel / 2.0 - 30;

            double xInicio = 60;
            double xFin = anchoPanel - 180;

            int cantidad = estadosOrdenados.size();

            double separacion =
                    (cantidad > 1)
                            ? (xFin - xInicio) / (cantidad - 1)
                            : 0;

            // =============================================
            // DIBUJAR ESTADOS
            // =============================================
            for (int i = 0; i < estadosOrdenados.size(); i++) {

                String nombre = estadosOrdenados.get(i);

                double x = xInicio + (i * separacion);
                double y = yCentro;

                String estilo =
                        "shape=ellipse;"
                        + "fillColor=#FFFFFF;"
                        + "strokeColor=#222222;"
                        + "strokeWidth=2;"
                        + "fontSize=15;"
                        + "fontColor=#6D4C41;";

                if (nombre.equals("q_start")) {
                    estilo =
                            "shape=ellipse;"
                            + "fillColor=#E8F5E9;"
                            + "strokeColor=#2E7D32;"
                            + "strokeWidth=3;"
                            + "fontSize=15;"
                            + "fontColor=#6D4C41;";
                }

                if (nombre.equals("q_accept")) {
                    estilo =
                            "shape=doubleEllipse;"
                            + "fillColor=#E3F2FD;"
                            + "strokeColor=#1565C0;"
                            + "strokeWidth=3;"
                            + "fontSize=15;"
                            + "fontColor=#6D4C41;";
                }

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

                celdas.put(nombre, celda);
            }

            // =============================================
            // DIBUJAR ARISTAS
            // =============================================
            for (AristaRegex arista : paso.getAristas()) {

                Object origen = celdas.get(arista.getOrigen());
                Object destino = celdas.get(arista.getDestino());

                if (origen == null || destino == null) {
                    continue;
                }

                boolean esLoop =
                        arista.getOrigen().equals(arista.getDestino());

                String estilo =
                        "strokeColor=#222222;"
                        + "strokeWidth=2;"
                        + "endArrow=classic;"
                        + "endFill=1;"
                        + "fontSize=14;"
                        + "fontColor=#000000;"
                        + "labelBackgroundColor=#FFFFFF;"
                        + "rounded=1;";

                if (esLoop) {
                    estilo +=
                            "loopStyle=loopEdgeStyle;"
                            + "orthogonalLoop=1;"
                            + "direction=north;"
                            + "segment=40;";
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

                // Aplicar curva si NO es loop
                if (!esLoop) {

                    boolean tieneInversa =
                            clavesAristas.contains(
                                    arista.getDestino()
                                    + "->"
                                    + arista.getOrigen()
                            );

                    double offset;

                    if (tieneInversa) {
                        offset =
                                arista.getOrigen()
                                        .compareTo(arista.getDestino()) < 0
                                        ? -60
                                        : 60;
                    } else {
                        offset = -35;
                    }

                    curvarArista(
                            aristaVisual,
                            origen,
                            destino,
                            offset
                    );
                }
            }

        } finally {

            grafo.getModel().endUpdate();
        }

        grafo.refresh();
    }

    // =====================================================
    // ORDENAR ESTADOS PARA QUE SE VEAN BONITOS
    // q_start - q0 - q1 - q2 - q_accept
    // =====================================================

    private List<String> ordenarEstados(List<String> originales) {

        List<String> ordenados =
                new ArrayList<>(originales);

        ordenados.sort((a, b) -> {

            if (a.equals("q_start")) return -1;
            if (b.equals("q_start")) return 1;

            if (a.equals("q_accept")) return 1;
            if (b.equals("q_accept")) return -1;

            return Integer.compare(
                    extraerNumeroEstado(a),
                    extraerNumeroEstado(b)
            );
        });

        return ordenados;
    }

    private int extraerNumeroEstado(String nombre) {

        try {

            if (nombre.startsWith("q")) {
                return Integer.parseInt(nombre.substring(1));
            }

        } catch (NumberFormatException ex) {
            // Si no tiene número, lo manda al final
        }

        return Integer.MAX_VALUE;
    }

    // =====================================================
    // CURVAR UNA ARISTA PARA QUE NO SE VEA TODO EN LÍNEA
    // =====================================================

    private void curvarArista(
            Object arista,
            Object origen,
            Object destino,
            double offsetY
    ) {

        mxGeometry geoOrigen =
                grafo.getModel().getGeometry(origen);

        mxGeometry geoDestino =
                grafo.getModel().getGeometry(destino);

        if (geoOrigen == null || geoDestino == null) {
            return;
        }

        double x1 =
                geoOrigen.getX() + geoOrigen.getWidth() / 2.0;

        double y1 =
                geoOrigen.getY() + geoOrigen.getHeight() / 2.0;

        double x2 =
                geoDestino.getX() + geoDestino.getWidth() / 2.0;

        double y2 =
                geoDestino.getY() + geoDestino.getHeight() / 2.0;

        double medioX = (x1 + x2) / 2.0;
        double medioY = (y1 + y2) / 2.0 + offsetY;

        mxGeometry geometria =
                (mxGeometry) grafo.getModel()
                        .getGeometry(arista)
                        .clone();

        geometria.setPoints(
                Collections.singletonList(
                        new mxPoint(medioX, medioY)
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

        grafo.getModel().setGeometry(
                arista,
                geometria
        );
    }
}