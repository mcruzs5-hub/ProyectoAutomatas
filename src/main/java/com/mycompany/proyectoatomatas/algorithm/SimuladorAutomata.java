package com.mycompany.proyectoatomatas.algorithm;

import com.mycompany.proyectoatomatas.model.Automata;
import com.mycompany.proyectoatomatas.model.Cadena;
import com.mycompany.proyectoatomatas.model.Estado;
import com.mycompany.proyectoatomatas.model.Transicion;

import java.util.ArrayList;
import java.util.List;

public class SimuladorAutomata {

    private final Automata automata;

    public SimuladorAutomata(Automata automata) {
        this.automata = automata;
    }

    public static class Paso {

        private final Estado origen;
        private final Estado destino;
        private final Transicion transicion;
        private final String simbolo;

        public Paso(
                Estado origen,
                Estado destino,
                Transicion transicion,
                String simbolo
        ) {

            this.origen = origen;
            this.destino = destino;
            this.transicion = transicion;
            this.simbolo = simbolo;
        }

        public Estado getOrigen() {
            return origen;
        }

        public Estado getDestino() {
            return destino;
        }

        public Transicion getTransicion() {
            return transicion;
        }

        public String getSimbolo() {
            return simbolo;
        }
    }

    public List<Paso> simular(Cadena cadena) {

        if (automata.getEstadoInicial() == null) {
            return null;
        }

        List<Paso> pasos = new ArrayList<>();

        Estado actual = automata.getEstadoInicial();

        for (String simbolo : cadena.obtenerSimbolos()) {

            Transicion encontrada = null;

            for (Transicion transicion
                    : automata.getTransiciones()) {

                if (transicion.getOrigen() == actual
                        && transicion.getSimbolo()
                                .equals(simbolo)) {

                    encontrada = transicion;
                    break;
                }
            }

            // No existe transición para continuar
            if (encontrada == null) {
                return null;
            }

            Estado siguiente =
                    encontrada.getDestino();

            pasos.add(
                    new Paso(
                            actual,
                            siguiente,
                            encontrada,
                            simbolo
                    )
            );

            actual = siguiente;
        }

        return pasos;
    }

    public Estado obtenerEstadoFinal(
            Cadena cadena,
            List<Paso> pasos
    ) {

        if (automata.getEstadoInicial() == null) {
            return null;
        }

        // Cadena vacía
        if (cadena.estaVacia()) {
            return automata.getEstadoInicial();
        }

        if (pasos == null || pasos.isEmpty()) {
            return null;
        }

        return pasos.get(
                pasos.size() - 1
        ).getDestino();
    }

    public boolean esAceptada(Estado estadoFinal) {

        return estadoFinal != null
                && estadoFinal.isAceptacion();
    }
}