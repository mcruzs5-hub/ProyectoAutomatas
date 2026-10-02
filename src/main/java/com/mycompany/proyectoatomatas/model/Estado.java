
package com.mycompany.proyectoatomatas.model;

public class Estado {

    // Atributos del estado
    private String nombre;
    private boolean inicial;
    private boolean aceptacion;

    // CONSTRUCTOR
    public Estado(String nombre) {

        this.nombre = nombre;
        this.inicial = false;
        this.aceptacion = false;
    }

    // OBTENER NOMBRE DEL ESTADO
    public String getNombre() {

        return nombre;
    }

    // NUEVO: MODIFICAR NOMBRE DEL ESTADO
    public void setNombre(String nombre) {

        this.nombre = nombre;
    }

    // VERIFICAR SI ES ESTADO INICIAL
    public boolean isInicial() {

        return inicial;
    }

    // MODIFICAR ESTADO INICIAL
    public void setInicial(boolean inicial) {

        this.inicial = inicial;
    }

    // VERIFICAR SI ES ESTADO DE ACEPTACIÓN
    public boolean isAceptacion() {

        return aceptacion;
    }

    // MODIFICAR ESTADO DE ACEPTACIÓN
    public void setAceptacion(boolean aceptacion) {

        this.aceptacion = aceptacion;
    }

    // REPRESENTACIÓN DEL ESTADO COMO TEXTO
    @Override
    public String toString() {

        return nombre;
    }
}
