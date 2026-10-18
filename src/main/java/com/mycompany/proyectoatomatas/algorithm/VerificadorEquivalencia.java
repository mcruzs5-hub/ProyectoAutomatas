package com.mycompany.proyectoatomatas.algorithm;

import com.mycompany.proyectoatomatas.model.Automata;
import com.mycompany.proyectoatomatas.model.Cadena;
import com.mycompany.proyectoatomatas.model.Estado;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class VerificadorEquivalencia {

    private final Automata automata;
    private final String regexMatematica;

    public VerificadorEquivalencia(
            Automata automata,
            String regexMatematica
    ) {
        this.automata = automata;
        this.regexMatematica = regexMatematica;
    }

    public static class Resultado {

        private final boolean aceptadaAFD;
        private final boolean aceptadaRegex;
        private final boolean coinciden;
        private final String regexJava;

        public Resultado(
                boolean aceptadaAFD,
                boolean aceptadaRegex,
                boolean coinciden,
                String regexJava
        ) {
            this.aceptadaAFD = aceptadaAFD;
            this.aceptadaRegex = aceptadaRegex;
            this.coinciden = coinciden;
            this.regexJava = regexJava;
        }

        public boolean isAceptadaAFD() {
            return aceptadaAFD;
        }

        public boolean isAceptadaRegex() {
            return aceptadaRegex;
        }

        public boolean isCoinciden() {
            return coinciden;
        }

        public String getRegexJava() {
            return regexJava;
        }
    }

    public Resultado verificar(String texto) {

        boolean resultadoAFD =
                evaluarAFD(texto);

        String regexJava =
                convertirARegexJava(
                        regexMatematica
                );

        boolean resultadoRegex =
                evaluarRegexJava(
                        texto,
                        regexJava
                );

        return new Resultado(
                resultadoAFD,
                resultadoRegex,
                resultadoAFD == resultadoRegex,
                regexJava
        );
    }

    private boolean evaluarAFD(String texto) {

        Cadena cadena =
                new Cadena(texto);

        SimuladorAutomata simulador =
                new SimuladorAutomata(
                        automata
                );

        List<SimuladorAutomata.Paso> pasos =
                simulador.simular(cadena);

        if (pasos == null) {
            return false;
        }

        Estado estadoFinal =
                simulador.obtenerEstadoFinal(
                        cadena,
                        pasos
                );

        return simulador.esAceptada(
                estadoFinal
        );
    }

    private String convertirARegexJava(
            String regex
    ) {

        if (regex == null
                || regex.isBlank()) {

            return "(?!)";
        }

        String resultado = regex;

        resultado =
                resultado.replace(
                        "∪",
                        "|"
                );

        resultado =
                resultado.replace(
                        "ε",
                        ""
                );

        resultado =
                resultado.replace(
                        "∅",
                        "(?!)"
                );

        return resultado;
    }

    private boolean evaluarRegexJava(
            String texto,
            String regexJava
    ) {

        try {

            Pattern patron =
                    Pattern.compile(
                            "^(?:"
                            + regexJava
                            + ")$"
                    );

            return patron
                    .matcher(texto)
                    .matches();

        } catch (PatternSyntaxException ex) {

            throw new IllegalStateException(
                    "La expresión regular generada "
                    + "no pudo ser interpretada por Java.\n\n"
                    + "Regex matemática:\n"
                    + regexMatematica
                    + "\n\nRegex Java:\n"
                    + regexJava
                    + "\n\nDetalle:\n"
                    + ex.getDescription()
            );
        }
    }
}