package com.mycompany.proyectoatomatas.algorithm;

import com.mycompany.proyectoatomatas.model.Automata;
import com.mycompany.proyectoatomatas.model.Estado;
import com.mycompany.proyectoatomatas.model.Transicion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConversorRegex {

    private static final String VACIO = "∅";
    private static final String EPSILON = "ε";

    private final Automata automata;

    public ConversorRegex(Automata automata) {
        this.automata = automata;
    }

    // =====================================================
    // CLASE PARA REPRESENTAR UNA ARISTA REGEX
    // =====================================================

    public static class AristaRegex {

        private final String origen;
        private final String destino;
        private final String expresion;

        public AristaRegex(
                String origen,
                String destino,
                String expresion
        ) {

            this.origen = origen;
            this.destino = destino;
            this.expresion = expresion;
        }

        public String getOrigen() {
            return origen;
        }

        public String getDestino() {
            return destino;
        }

        public String getExpresion() {
            return expresion;
        }
    }

    // =====================================================
    // REPRESENTAR UN PASO DEL ALGORITMO
    // =====================================================

    public static class PasoEliminacion {

        private final String descripcion;
        private final List<String> estados;
        private final List<AristaRegex> aristas;

        public PasoEliminacion(
                String descripcion,
                List<String> estados,
                List<AristaRegex> aristas
        ) {

            this.descripcion = descripcion;
            this.estados = estados;
            this.aristas = aristas;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public List<String> getEstados() {
            return estados;
        }

        public List<AristaRegex> getAristas() {
            return aristas;
        }
    }

    // =====================================================
    // RESULTADO COMPLETO
    // =====================================================

    public static class ResultadoConversion {

        private final List<PasoEliminacion> pasos;
        private final String expresionRegular;

        public ResultadoConversion(
                List<PasoEliminacion> pasos,
                String expresionRegular
        ) {

            this.pasos = pasos;
            this.expresionRegular = expresionRegular;
        }

        public List<PasoEliminacion> getPasos() {
            return pasos;
        }

        public String getExpresionRegular() {
            return expresionRegular;
        }
    }

    // =====================================================
    // CONVERSIÓN PRINCIPAL
    // =====================================================

    public ResultadoConversion convertir() {

        if (automata.getEstadoInicial() == null) {
            throw new IllegalStateException(
                    "El autómata no tiene estado inicial."
            );
        }

        if (automata.getEstadosAceptacion().isEmpty()) {
            throw new IllegalStateException(
                    "El autómata no tiene estados de aceptación."
            );
        }

        final String qStart = "q_start";
        final String qAccept = "q_accept";

        List<String> estados = new ArrayList<>();

        estados.add(qStart);

        for (Estado estado : automata.getEstados()) {
            estados.add(estado.getNombre());
        }

        estados.add(qAccept);

        Map<String, Map<String, String>> regex =
                new LinkedHashMap<>();

        inicializarMatriz(regex, estados);

        // =================================================
        // PASO 1: NORMALIZACIÓN
        // q_start --ε--> estado inicial
        // =================================================

        agregarExpresion(
                regex,
                qStart,
                automata.getEstadoInicial().getNombre(),
                EPSILON
        );

        // Copiar las transiciones originales
        for (Transicion transicion : automata.getTransiciones()) {

            agregarExpresion(
                    regex,
                    transicion.getOrigen().getNombre(),
                    transicion.getDestino().getNombre(),
                    transicion.getSimbolo()
            );
        }

        // Estados finales --ε--> q_accept
        for (Estado estadoFinal
                : automata.getEstadosAceptacion()) {

            agregarExpresion(
                    regex,
                    estadoFinal.getNombre(),
                    qAccept,
                    EPSILON
            );
        }

        List<PasoEliminacion> pasos =
                new ArrayList<>();

        pasos.add(
                crearPaso(
                        "Paso 1 - Normalización: "
                        + "se agregaron q_start y q_accept.",
                        estados,
                        regex
                )
        );

        // =================================================
        // PASO 2: ELIMINACIÓN ITERATIVA
        // =================================================

        List<String> estadosEliminar =
                new ArrayList<>();

        for (Estado estado : automata.getEstados()) {
            estadosEliminar.add(estado.getNombre());
        }

        for (String eliminado : estadosEliminar) {

            eliminarEstado(
                    eliminado,
                    estados,
                    regex
            );

            estados.remove(eliminado);

            pasos.add(
                    crearPaso(
                            "Se eliminó el estado "
                            + eliminado
                            + " y se recalcularon las aristas.",
                            estados,
                            regex
                    )
            );
        }

        String resultado =
                obtenerExpresion(
                        regex,
                        qStart,
                        qAccept
                );

        return new ResultadoConversion(
                pasos,
                resultado
        );
    }

    // =====================================================
    // INICIALIZAR MATRIZ
    // =====================================================

    private void inicializarMatriz(
            Map<String, Map<String, String>> regex,
            List<String> estados
    ) {

        for (String origen : estados) {

            Map<String, String> destinos =
                    new LinkedHashMap<>();

            for (String destino : estados) {
                destinos.put(destino, VACIO);
            }

            regex.put(origen, destinos);
        }
    }

    // =====================================================
    // ELIMINAR ESTADO
    // =====================================================

    private void eliminarEstado(
            String eliminado,
            List<String> estados,
            Map<String, Map<String, String>> regex
    ) {

        List<String> restantes =
                new ArrayList<>(estados);

        restantes.remove(eliminado);

        for (String i : restantes) {

            for (String k : restantes) {

                String rik =
                        obtenerExpresion(regex, i, k);

                String rij =
                        obtenerExpresion(
                                regex,
                                i,
                                eliminado
                        );

                String rjj =
                        obtenerExpresion(
                                regex,
                                eliminado,
                                eliminado
                        );

                String rjk =
                        obtenerExpresion(
                                regex,
                                eliminado,
                                k
                        );

                // Fórmula:
                // R_ik' =
                // R_ik ∪ (R_ij · (R_jj)* · R_jk)

                String nuevoCamino =
                        concatenar(
                                rij,
                                estrella(rjj),
                                rjk
                        );

                String nuevaExpresion =
                        unir(
                                rik,
                                nuevoCamino
                        );

                regex.get(i).put(
                        k,
                        nuevaExpresion
                );
            }
        }

        regex.remove(eliminado);

        for (Map<String, String> destinos
                : regex.values()) {

            destinos.remove(eliminado);
        }
    }

    // =====================================================
    // AGREGAR EXPRESIÓN A UNA ARISTA
    // =====================================================

    private void agregarExpresion(
            Map<String, Map<String, String>> regex,
            String origen,
            String destino,
            String expresion
    ) {

        String actual =
                obtenerExpresion(
                        regex,
                        origen,
                        destino
                );

        regex.get(origen).put(
                destino,
                unir(actual, expresion)
        );
    }

    // =====================================================
    // OBTENER EXPRESIÓN
    // =====================================================

    private String obtenerExpresion(
            Map<String, Map<String, String>> regex,
            String origen,
            String destino
    ) {

        if (!regex.containsKey(origen)) {
            return VACIO;
        }

        String resultado =
                regex.get(origen).get(destino);

        if (resultado == null) {
            return VACIO;
        }

        return resultado;
    }

    // =====================================================
    // UNIÓN
    // =====================================================

    private String unir(String a, String b) {

        if (VACIO.equals(a)) {
            return b;
        }

        if (VACIO.equals(b)) {
            return a;
        }

        if (a.equals(b)) {
            return a;
        }

        return "(" + a + "∪" + b + ")";
    }

    // =====================================================
    // CONCATENACIÓN
    // =====================================================

    private String concatenar(String... expresiones) {

        StringBuilder resultado =
                new StringBuilder();

        for (String expresion : expresiones) {

            if (VACIO.equals(expresion)) {
                return VACIO;
            }

            if (EPSILON.equals(expresion)) {
                continue;
            }

            resultado.append(expresion);
        }

        if (resultado.length() == 0) {
            return EPSILON;
        }

        return resultado.toString();
    }

    // =====================================================
    // ESTRELLA DE KLEENE
    // =====================================================

    private String estrella(String expresion) {

        if (VACIO.equals(expresion)
                || EPSILON.equals(expresion)) {

            return EPSILON;
        }

        if (expresion.length() == 1) {
            return expresion + "*";
        }

        return "(" + expresion + ")*";
    }

    // =====================================================
    // CREAR FOTO DEL PASO ACTUAL
    // =====================================================

    private PasoEliminacion crearPaso(
            String descripcion,
            List<String> estados,
            Map<String, Map<String, String>> regex
    ) {

        List<AristaRegex> aristas =
                new ArrayList<>();

        for (String origen : estados) {

            for (String destino : estados) {

                String expresion =
                        obtenerExpresion(
                                regex,
                                origen,
                                destino
                        );

                if (!VACIO.equals(expresion)) {

                    aristas.add(
                            new AristaRegex(
                                    origen,
                                    destino,
                                    expresion
                            )
                    );
                }
            }
        }

        return new PasoEliminacion(
                descripcion,
                new ArrayList<>(estados),
                aristas
        );
    }
}