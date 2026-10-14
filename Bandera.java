import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Banderas segun el "Estandar para banderas":
 * - Todas las banderas estan en recursos/Flags.csv: una matriz de 480 filas x 15 columnas.
 * - Cada bandera ocupa 10 filas x 15 columnas (48 banderas en total).
 * - Cada celda es un digito de color: 1 amarillo, 2 naranja, 3 rojo, 4 morado, 5 azul,
 *   6 verde, 7 blanco, 8 negro, 9 cafe.
 * - Un switch de 48 casos dice en que fila de la matriz empieza cada pais.
 */
public class Bandera {

    static final int TOTAL = 48;
    static final int FILAS_BANDERA = 10;
    static final int COLS_BANDERA = 15;
    static final int FILAS_CSV = TOTAL * FILAS_BANDERA;

    static final String[] PAISES = {
        "Inglaterra", "Espana", "Francia", "Cabo Verde", "Arabia Saudita",
        "Corea del Sur", "Congo RD", "Ecuador", "Estados Unidos", "Argentina",
        "Brasil", "Canada", "Costa de Marfil", "Jordania", "Alemania",
        "Japon", "Colombia", "Belgica", "Turquia", "Sudafrica",
        "Chequia", "Suiza", "Portugal", "Egipto", "Paraguay",
        "Escocia", "Haiti", "Argelia", "Mexico", "Marruecos",
        "Austria", "Noruega", "Bosnia y Herzegovina", "Tunez", "Croacia",
        "Paises Bajos", "Uruguay", "Qatar", "Australia", "Nueva Zelanda",
        "Senegal", "Ghana", "Panama", "Irak", "Suecia",
        "Curazao", "Iran", "Uzbekistan"
    };

    // Cada caracter del CSV ('1'..'9') corresponde a una posicion (0..8) de este arreglo
    static final String[] COLORES = {
        ConsoleColors.YELLOW_BACKGROUND,   // '1'
        ConsoleColors.ORANGE_BACKGROUND,   // '2'
        ConsoleColors.RED_BACKGROUND,      // '3'
        ConsoleColors.PURPLE_BACKGROUND,   // '4'
        ConsoleColors.BLUE_BACKGROUND,     // '5'
        ConsoleColors.GREEN_BACKGROUND,    // '6'
        ConsoleColors.WHITE_BACKGROUND,    // '7'
        ConsoleColors.BLACK_BACKGROUND,    // '8'
        ConsoleColors.BROWN_BACKGROUND     // '9'
    };

    // Tamanos: {filas, columnas}. Todos mantienen la proporcion 2:3 de la bandera original.
    static final String[] TAMANOS = {
        "Grande (maximo detalle)",
        "Mediano (resolucion intermedia)",
        "Pequeno (baja resolucion)",
        "Icono (miniatura para menus)"
    };
    static final int[][] MEDIDAS = {
        {10, 15},  // Grande (tamano original)
        {6, 9},    // Mediano
        {4, 6},    // Pequeno
        {2, 3}     // Icono
    };
    static final int ICONO = 3;

    // ---------------------------------------------------------------
    // Switch de banderas: fila del CSV (desde 1) donde empieza cada pais
    // ---------------------------------------------------------------

    static int filaInicio(int pais) {
        int fila = 0;
        switch (pais) {
            case 1:  fila = 61;  break; // Inglaterra
            case 2:  fila = 71;  break; // Espana
            case 3:  fila = 81;  break; // Francia
            case 4:  fila = 91;  break; // Cabo Verde
            case 5:  fila = 271; break; // Arabia Saudita
            case 6:  fila = 101; break; // Corea del Sur
            case 7:  fila = 111; break; // Congo RD
            case 8:  fila = 121; break; // Ecuador
            case 9:  fila = 341; break; // Estados Unidos
            case 10: fila = 291; break; // Argentina
            case 11: fila = 321; break; // Brasil
            case 12: fila = 381; break; // Canada
            case 13: fila = 301; break; // Costa de Marfil
            case 14: fila = 371; break; // Jordania
            case 15: fila = 131; break; // Alemania
            case 16: fila = 161; break; // Japon
            case 17: fila = 191; break; // Colombia
            case 18: fila = 141; break; // Belgica
            case 19: fila = 181; break; // Turquia
            case 20: fila = 171; break; // Sudafrica
            case 21: fila = 151; break; // Chequia
            case 22: fila = 221; break; // Suiza
            case 23: fila = 241; break; // Portugal
            case 24: fila = 231; break; // Egipto
            case 25: fila = 211; break; // Paraguay
            case 26: fila = 201; break; // Escocia
            case 27: fila = 251; break; // Haiti
            case 28: fila = 261; break; // Argelia
            case 29: fila = 11;  break; // Mexico
            case 30: fila = 21;  break; // Marruecos
            case 31: fila = 1;   break; // Austria
            case 32: fila = 31;  break; // Noruega
            case 33: fila = 41;  break; // Bosnia y Herzegovina
            case 34: fila = 51;  break; // Tunez
            case 35: fila = 281; break; // Croacia
            case 36: fila = 311; break; // Paises Bajos
            case 37: fila = 351; break; // Uruguay
            case 38: fila = 331; break; // Qatar
            case 39: fila = 391; break; // Australia
            case 40: fila = 401; break; // Nueva Zelanda
            case 41: fila = 361; break; // Senegal
            case 42: fila = 471; break; // Ghana
            case 43: fila = 411; break; // Panama
            case 44: fila = 461; break; // Irak
            case 45: fila = 431; break; // Suecia
            case 46: fila = 421; break; // Curazao
            case 47: fila = 441; break; // Iran
            case 48: fila = 451; break; // Uzbekistan
        }
        return fila;
    }

    // ---------------------------------------------------------------
    // Archivo plano (CSV) <-> matriz de caracteres
    // ---------------------------------------------------------------

    /** Lee el CSV en una matriz char[480][15]. Devuelve null si no se pudo leer. */
    static char[][] cargarCSV(String ruta) {
        char[][] matriz = new char[FILAS_CSV][COLS_BANDERA];
        try {
            BufferedReader archivo = new BufferedReader(new FileReader(ruta));
            String linea;
            int fila = 0;
            while ((linea = archivo.readLine()) != null && fila < matriz.length) {
                if (fila == 0 && linea.startsWith("﻿")) {
                    linea = linea.substring(1); // algunos editores ponen una marca invisible al inicio
                }
                String[] columnas = linea.split(";");
                for (int columna = 0; columna < columnas.length && columna < COLS_BANDERA; columna++) {
                    if (columnas[columna].trim().length() > 0) {
                        matriz[fila][columna] = columnas[columna].trim().charAt(0);
                    }
                }
                fila++;
            }
            archivo.close();
        } catch (IOException e) {
            return null;
        }
        return matriz;
    }

    static void guardarCSV(String ruta, char[][] matriz) {
        try {
            PrintWriter salida = new PrintWriter(new FileWriter(ruta));
            for (int f = 0; f < matriz.length; f++) {
                String linea = "";
                for (int c = 0; c < matriz[f].length; c++) {
                    char valor = matriz[f][c];
                    if (valor == 0) {
                        valor = '0';
                    }
                    linea = linea + valor + ";";
                }
                salida.println(linea);
            }
            salida.close();
        } catch (IOException e) {
            System.out.println("No se pudo guardar " + ruta + ": " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Dibujo en consola
    // ---------------------------------------------------------------

    /**
     * Dibuja la bandera que empieza en filaInicio en el tamano indicado (0..3).
     * Para cada celda del tamano elegido toma la celda del centro del bloque
     * que le corresponde en la bandera original de 10x15.
     */
    static void dibujar(String nombre, char[][] matriz, int filaInicio, int tamano) {
        int filasDestino = MEDIDAS[tamano][0];
        int colsDestino = MEDIDAS[tamano][1];
        System.out.println("\n " + nombre + " - " + TAMANOS[tamano] + " [" + filasDestino + "x" + colsDestino + "]");
        for (int f = 0; f < filasDestino; f++) {
            int filaOrigen = filaInicio - 1 + (2 * f + 1) * FILAS_BANDERA / (2 * filasDestino);
            System.out.print("   ");
            for (int c = 0; c < colsDestino; c++) {
                int colOrigen = (2 * c + 1) * COLS_BANDERA / (2 * colsDestino);
                char color = matriz[filaOrigen][colOrigen];
                if (color >= '1' && color <= '9') {
                    System.out.print(COLORES[color - '1'] + "   ");
                }
                System.out.print(ConsoleColors.RESET);
            }
            System.out.println();
        }
    }

    /** Dibuja la bandera en sus 4 tamanos, uno tras otro. */
    static void dibujarTodosLosTamanos(String nombre, char[][] matriz, int filaInicio) {
        for (int t = 0; t < MEDIDAS.length; t++) {
            dibujar(nombre, matriz, filaInicio, t);
        }
    }
}
