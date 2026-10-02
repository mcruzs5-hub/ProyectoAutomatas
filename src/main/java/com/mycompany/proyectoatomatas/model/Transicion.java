
package com.mycompany.proyectoatomatas.model;

public class Transicion {

    private Estado origen;
    private Estado destino;
    private String simbolo;

    // Constructor de la transición
    public Transicion(Estado origen, Estado destino, String simbolo) {
        this.origen = origen;
        this.destino = destino;
        this.simbolo = simbolo;
    }

    // Obtener estado de origen
    public Estado getOrigen() {
        return origen;
    }

    // Obtener estado de destino
    public Estado getDestino() {
        return destino;
    }

    // Obtener símbolo de transición
    public String getSimbolo() {
        return simbolo;
    }

    // Modificar símbolo de transición
    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    @Override
    public String toString() {
        return origen + " --" + simbolo + "--> " + destino;
    }
}
