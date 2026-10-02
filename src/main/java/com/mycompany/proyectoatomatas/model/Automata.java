
package com.mycompany.proyectoatomatas.model;

import java.util.ArrayList;
import java.util.List;

public class Automata {

    // Conjunto de estados Q
    private final List<Estado> estados;

    // Función de transición
    private final List<Transicion> transiciones;

    // Estado inicial q0
    private Estado estadoInicial;

    // Constructor
    public Automata() {
        estados = new ArrayList<>();
        transiciones = new ArrayList<>();
    }

    // Agregar un nuevo estado
    public void agregarEstado(Estado estado) {
        estados.add(estado);
    }

    // Agregar una nueva transición
    public void agregarTransicion(Transicion transicion) {
        transiciones.add(transicion);
    }

    // Obtener todos los estados
    public List<Estado> getEstados() {
        return estados;
    }

    // Obtener todas las transiciones
    public List<Transicion> getTransiciones() {
        return transiciones;
    }

    // Obtener el estado inicial
    public Estado getEstadoInicial() {
        return estadoInicial;
    }

    // Establecer el estado inicial
    public void setEstadoInicial(Estado nuevoInicial) {

        // Quitar la propiedad al estado inicial anterior
        if (estadoInicial != null) {
            estadoInicial.setInicial(false);
        }

        // Asignar el nuevo estado inicial
        estadoInicial = nuevoInicial;

        if (nuevoInicial != null) {
            nuevoInicial.setInicial(true);
        }
    }

    // Obtener todos los estados de aceptación
    public List<Estado> getEstadosAceptacion() {

        List<Estado> finales = new ArrayList<>();

        for (Estado estado : estados) {

            if (estado.isAceptacion()) {
                finales.add(estado);
            }
        }

        return finales;
    }
}
