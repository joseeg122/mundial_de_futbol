import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Tabla de posiciones del mundial representada con una matriz:
 * filas = equipos, columnas = PJ, PG, PE, PP, GF, GC, DG, TA, TR, Pts.
 * El contenido es editable y se guarda/carga en un archivo plano.
 */
public class TablaPosiciones {

    static final String[] COLUMNAS = {"PJ", "PG", "PE", "PP", "GF", "GC", "DG", "TA", "TR", "Pts"};
    static final int PJ = 0, PG = 1, PE = 2, PP = 3, GF = 4, GC = 5, DG = 6, TA = 7, TR = 8, PTS = 9;

    // Columnas que el usuario puede editar (las demas se calculan solas)
    static final int[] EDITABLES = {PG, PE, PP, GF, GC, TA, TR};

    static final int TAM_PAGINA = 12;
    static final int MAX_EQUIPOS = 48;

    static String[] equipos = new String[MAX_EQUIPOS];
    static String[] grupos = new String[MAX_EQUIPOS];
    static int[][] datos = new int[MAX_EQUIPOS][COLUMNAS.length];
    static int total = 0; // cuantos equipos se leyeron del archivo

    // ---------------------------------------------------------------
    // Cadenas: ayudas para alinear texto y comparar nombres
    // ---------------------------------------------------------------

    /** Agrega espacios a la derecha hasta completar el ancho (texto alineado a la izquierda). */
    static String rellenarDerecha(String texto, int ancho) {
        while (texto.length() < ancho) {
            texto = texto + " ";
        }
        return texto;
    }

    /** Agrega espacios a la izquierda hasta completar el ancho (texto alineado a la derecha). */
    static String rellenarIzquierda(String texto, int ancho) {
        while (texto.length() < ancho) {
            texto = " " + texto;
        }
        return texto;
    }

    /** Pasa a minusculas y quita tildes para comparar ("Países Bajos" = "paises bajos"). */
    static String normalizar(String texto) {
        String t = texto.trim().toLowerCase();
        t = t.replace('á', 'a');
        t = t.replace('é', 'e');
        t = t.replace('í', 'i');
        t = t.replace('ó', 'o');
        t = t.replace('ú', 'u');
        t = t.replace('ñ', 'n');
        return t;
    }

    // ---------------------------------------------------------------
    // Equipos
    // ---------------------------------------------------------------

    /** Lee equipos.txt (lineas "grupo;equipo"). Devuelve false si no se pudo leer. */
    static boolean cargarEquipos(String ruta) {
        total = 0;
        try {
            BufferedReader archivo = new BufferedReader(new FileReader(ruta));
            String linea;
            while ((linea = archivo.readLine()) != null && total < MAX_EQUIPOS) {
                String[] t = linea.split(";");
                if (linea.startsWith("#") || t.length < 2) {
                    continue;
                }
                grupos[total] = t[0].trim();
                equipos[total] = t[1].trim();
                total++;
            }
            archivo.close();
        } catch (IOException e) {
            System.out.println("No se pudo leer " + ruta + ". Ejecuta el programa desde la carpeta del proyecto.");
            return false;
        }
        return total > 0;
    }

    /** Busca un equipo por nombre (exacto o parcial, sin tildes). Devuelve -1 si no existe. */
    static int buscar(String texto) {
        String t = normalizar(texto);
        if (t.length() == 0) {
            return -1;
        }
        for (int i = 0; i < total; i++) {
            if (normalizar(equipos[i]).equals(t)) {
                return i;
            }
        }
        for (int i = 0; i < total; i++) {
            if (normalizar(equipos[i]).contains(t)) {
                return i;
            }
        }
        return -1;
    }

    // ---------------------------------------------------------------
    // Actualizacion de datos
    // ---------------------------------------------------------------

    /** Recalcula las columnas derivadas de una fila: PJ, DG y Pts. */
    static void recalcular(int fila) {
        datos[fila][PJ] = datos[fila][PG] + datos[fila][PE] + datos[fila][PP];
        datos[fila][DG] = datos[fila][GF] - datos[fila][GC];
        datos[fila][PTS] = 3 * datos[fila][PG] + datos[fila][PE];
    }

    /**
     * Suma (signo = 1) o resta (signo = -1) un resultado a la tabla.
     * Restar sirve para corregir un resultado ya registrado.
     */
    static void aplicarResultado(int local, int visita, int golesLocal, int golesVisita, int signo) {
        datos[local][GF] += signo * golesLocal;
        datos[local][GC] += signo * golesVisita;
        datos[visita][GF] += signo * golesVisita;
        datos[visita][GC] += signo * golesLocal;
        if (golesLocal > golesVisita) {
            datos[local][PG] += signo;
            datos[visita][PP] += signo;
        } else if (golesLocal < golesVisita) {
            datos[visita][PG] += signo;
            datos[local][PP] += signo;
        } else {
            datos[local][PE] += signo;
            datos[visita][PE] += signo;
        }
        recalcular(local);
        recalcular(visita);
    }

    /** Cambia una celda editable y recalcula PJ, DG y Pts. */
    static void editar(int fila, int columna, int valor) {
        datos[fila][columna] = valor;
        recalcular(fila);
    }

    // ---------------------------------------------------------------
    // Orden e impresion paginada
    // ---------------------------------------------------------------

    /** Dice si el equipo a va antes que el equipo b: mas Pts, luego mas DG, luego mas GF, luego nombre. */
    static boolean vaAntes(int a, int b) {
        if (datos[a][PTS] != datos[b][PTS]) {
            return datos[a][PTS] > datos[b][PTS];
        }
        if (datos[a][DG] != datos[b][DG]) {
            return datos[a][DG] > datos[b][DG];
        }
        if (datos[a][GF] != datos[b][GF]) {
            return datos[a][GF] > datos[b][GF];
        }
        return equipos[a].compareTo(equipos[b]) < 0;
    }

    /** Indices de las filas en el orden a mostrar: por grupo (orden del archivo) o por puntos. */
    static int[] ordenar(boolean porPuntos) {
        int[] orden = new int[total];
        for (int i = 0; i < total; i++) {
            orden[i] = i;
        }
        if (porPuntos) {
            // ordenamiento por seleccion
            for (int i = 0; i < total - 1; i++) {
                int mejor = i;
                for (int j = i + 1; j < total; j++) {
                    if (vaAntes(orden[j], orden[mejor])) {
                        mejor = j;
                    }
                }
                int temporal = orden[i];
                orden[i] = orden[mejor];
                orden[mejor] = temporal;
            }
        }
        return orden;
    }

    static int totalPaginas() {
        return (total + TAM_PAGINA - 1) / TAM_PAGINA;
    }

    /** Linea separadora: +-----+----+ segun el ancho de cada columna. */
    static String linea(int[] anchos) {
        String s = "+";
        for (int k = 0; k < anchos.length; k++) {
            for (int i = 0; i < anchos[k] + 2; i++) {
                s = s + "-";
            }
            s = s + "+";
        }
        return s;
    }

    /** Imprime una fila de la tabla: la columna 2 (equipo) a la izquierda y las demas a la derecha. */
    static void imprimirFila(String[] celdas, int[] anchos) {
        String s = "|";
        for (int k = 0; k < celdas.length; k++) {
            if (k == 2) {
                s = s + " " + rellenarDerecha(celdas[k], anchos[k]) + " |";
            } else {
                s = s + " " + rellenarIzquierda(celdas[k], anchos[k]) + " |";
            }
        }
        System.out.println(s);
    }

    /** Imprime una pagina de la tabla. */
    static void imprimirPagina(int[] orden, int pagina) {
        int[] anchos = {3, 2, 22, 3, 3, 3, 3, 3, 3, 4, 3, 3, 4};
        String sep = linea(anchos);
        String[] titulos = {"#", "Gr", "Equipo", "PJ", "PG", "PE", "PP", "GF", "GC", "DG", "TA", "TR", "Pts"};
        int inicio = pagina * TAM_PAGINA;
        int fin = inicio + TAM_PAGINA;
        if (fin > total) {
            fin = total;
        }

        System.out.println(ConsoleColors.CYAN_BOLD_BRIGHT + sep);
        imprimirFila(titulos, anchos);
        System.out.println(sep + ConsoleColors.RESET);
        for (int k = inicio; k < fin; k++) {
            int i = orden[k];
            String dg = "" + datos[i][DG];
            if (datos[i][DG] >= 0) {
                dg = "+" + dg;
            }
            String[] celdas = {
                "" + (k + 1), grupos[i], equipos[i],
                "" + datos[i][PJ], "" + datos[i][PG], "" + datos[i][PE], "" + datos[i][PP],
                "" + datos[i][GF], "" + datos[i][GC], dg,
                "" + datos[i][TA], "" + datos[i][TR], "" + datos[i][PTS]
            };
            imprimirFila(celdas, anchos);
        }
        System.out.println(ConsoleColors.CYAN_BOLD_BRIGHT + sep + ConsoleColors.RESET);
    }

    /** Muestra la tabla completa paginada con navegacion por teclado. */
    static void mostrarPaginada(boolean porPuntos) {
        int[] orden = ordenar(porPuntos);
        int pagina = 0;
        int paginas = totalPaginas();
        boolean seguir = true;
        while (seguir) {
            String tipo = "por grupos";
            if (porPuntos) {
                tipo = "ordenada por puntos";
            }
            System.out.println("\n  TABLA DE POSICIONES - " + tipo + "  (pagina " + (pagina + 1) + "/" + paginas + ")");
            imprimirPagina(orden, pagina);
            System.out.print("[S] Siguiente  [A] Anterior  [N] Ir a pagina  [Q] Salir: ");
            String op = ConsoleInput.getString().trim().toUpperCase();
            if (op.equals("S")) {
                pagina = (pagina + 1) % paginas;
            } else if (op.equals("A")) {
                pagina = (pagina - 1 + paginas) % paginas;
            } else if (op.equals("N")) {
                pagina = ConsoleInput.leerEntero("Numero de pagina (1-" + paginas + "): ", 1, paginas) - 1;
            } else if (op.equals("Q")) {
                seguir = false;
            } else {
                System.out.println("Opcion no valida.");
            }
        }
    }

    // ---------------------------------------------------------------
    // Archivo plano
    // ---------------------------------------------------------------

    /** Guarda: equipo;grupo;PG;PE;PP;GF;GC;TA;TR */
    static void guardar(String ruta) {
        try {
            PrintWriter salida = new PrintWriter(new FileWriter(ruta));
            salida.println("# equipo;grupo;PG;PE;PP;GF;GC;TA;TR");
            for (int i = 0; i < total; i++) {
                salida.println(equipos[i] + ";" + grupos[i] + ";" + datos[i][PG] + ";" + datos[i][PE] + ";"
                        + datos[i][PP] + ";" + datos[i][GF] + ";" + datos[i][GC] + ";" + datos[i][TA] + ";"
                        + datos[i][TR]);
            }
            salida.close();
        } catch (IOException e) {
            System.out.println("No se pudo guardar la tabla: " + e.getMessage());
        }
    }

    /** Carga los valores guardados (si el archivo existe). */
    static void cargar(String ruta) {
        try {
            BufferedReader archivo = new BufferedReader(new FileReader(ruta));
            String linea;
            while ((linea = archivo.readLine()) != null) {
                String[] t = linea.split(";");
                if (linea.startsWith("#") || t.length < 9) {
                    continue;
                }
                int i = buscar(t[0]);
                if (i < 0) {
                    continue;
                }
                datos[i][PG] = Integer.parseInt(t[2].trim());
                datos[i][PE] = Integer.parseInt(t[3].trim());
                datos[i][PP] = Integer.parseInt(t[4].trim());
                datos[i][GF] = Integer.parseInt(t[5].trim());
                datos[i][GC] = Integer.parseInt(t[6].trim());
                datos[i][TA] = Integer.parseInt(t[7].trim());
                datos[i][TR] = Integer.parseInt(t[8].trim());
                recalcular(i);
            }
            archivo.close();
        } catch (IOException e) {
            // si el archivo no existe todavia, la tabla empieza en cero
        } catch (NumberFormatException e) {
            System.out.println("Archivo de tabla invalido, se usan valores en cero.");
            for (int i = 0; i < total; i++) {
                for (int c = 0; c < COLUMNAS.length; c++) {
                    datos[i][c] = 0;
                }
            }
        }
    }
}
