package com.mycompany.proyectoatomatas.model;

import java.util.ArrayList;
import java.util.List;

public class Cadena {

    // Cadena ingresada por el usuario
    private String valor;

    // Constructor vacío
    public Cadena() {
        this.valor = "";
    }

    // Constructor con una cadena
    public Cadena(String valor) {
        setValor(valor);
    }

    // Obtener la cadena
    public String getValor() {
        return valor;
    }

    // Modificar la cadena
    public void setValor(String valor) {

        if (valor == null) {
            this.valor = "";
        } else {
            this.valor = valor;
        }
    }

    // Saber si la cadena está vacía
    public boolean estaVacia() {
        return valor.isEmpty();
    }

    // Obtener la longitud de la cadena
    public int longitud() {

    return valor.codePointCount(
            0,
            valor.length()
    );
}

    // Obtener un símbolo específico
    public String obtenerSimbolo(int posicion) {

        if (posicion < 0 || posicion >= valor.length()) {
            throw new IndexOutOfBoundsException(
                    "Posición inválida en la cadena."
            );
        }

        return String.valueOf(
                valor.charAt(posicion)
        );
    }

    // Convertir la cadena en una lista de símbolos
public List<String> obtenerSimbolos() {

    List<String> simbolos = new ArrayList<>();

    valor.codePoints().forEach(cp -> {
        simbolos.add(
                new String(Character.toChars(cp))
        );
    });

    return simbolos;
}

    @Override
    public String toString() {
        return valor;
    }
}