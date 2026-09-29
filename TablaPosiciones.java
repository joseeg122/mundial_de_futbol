import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tabla de posiciones del mundial representada con una matriz:
 * filas = equipos, columnas = PJ, PG, PE, PP, GF, GC, DG, TA, TR, Pts.
 * El contenido es editable y se puede guardar/cargar desde un archivo plano.
 */
public class TablaPosiciones {

    public static final String[] COLUMNAS = {"PJ", "PG", "PE", "PP", "GF", "GC", "DG", "TA", "TR", "Pts"};
    public static final int PJ = 0, PG = 1, PE = 2, PP = 3, GF = 4, GC = 5, DG = 6, TA = 7, TR = 8, PTS = 9;

    /** Columnas que el usuario puede editar (las demas se calculan solas). */
    public static final int[] EDITABLES = {PG, PE, PP, GF, GC, TA, TR};

    public static final int TAM_PAGINA = 12;

    private final String[] equipos;
    private final String[] grupos;
    private final int[][] datos;

    public TablaPosiciones(String[] equipos, String[] grupos) {
        this.equipos = equipos;
        this.grupos = grupos;
        this.datos = new int[equipos.length][COLUMNAS.length];
    }

    public int total() {
        return equipos.length;
    }

    public String getEquipo(int fila) {
        return equipos[fila];
    }

    /** Busca un equipo por nombre (exacto o parcial, sin tildes). Devuelve -1 si no existe. */
    public int buscar(String texto) {
        String t = Bandera.normalizar(texto);
        if (t.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < equipos.length; i++) {
            if (Bandera.normalizar(equipos[i]).equals(t)) {
                return i;
            }
        }
        for (int i = 0; i < equipos.length; i++) {
            if (Bandera.normalizar(equipos[i]).contains(t)) {
                return i;
            }
        }
        return -1;
    }

    // ---------------------------------------------------------------
    // Actualizacion de datos
    // ---------------------------------------------------------------

    /** Recalcula las columnas derivadas de una fila: PJ, DG y Pts. */
    private void recalcular(int fila) {
        int[] d = datos[fila];
        d[PJ] = d[PG] + d[PE] + d[PP];
        d[DG] = d[GF] - d[GC];
        d[PTS] = 3 * d[PG] + d[PE];
    }

    /**
     * Suma (signo = 1) o resta (signo = -1) un resultado a la tabla.
     * Restar sirve para corregir un resultado ya registrado.
     */
    public void aplicarResultado(int local, int visita, int golesLocal, int golesVisita, int signo) {
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

    /** Edita una celda (solo columnas editables) y recalcula PJ, DG y Pts. */
    public boolean editar(int fila, int columna, int valor) {
        boolean permitida = false;
        for (int c : EDITABLES) {
            if (c == columna) {
                permitida = true;
            }
        }
        if (!permitida || valor < 0 || fila < 0 || fila >= datos.length) {
            return false;
        }
        datos[fila][columna] = valor;
        recalcular(fila);
        return true;
    }

    public int getValor(int fila, int columna) {
        return datos[fila][columna];
    }

    // ---------------------------------------------------------------
    // Orden e impresion paginada
    // ---------------------------------------------------------------

    /** Indices de las filas en el orden a mostrar: por grupo o por puntos (Pts, DG, GF). */
    public int[] ordenar(boolean porPuntos) {
        List<Integer> idx = new ArrayList<>();
        for (int i = 0; i < equipos.length; i++) {
            idx.add(i);
        }
        if (porPuntos) {
            idx.sort((a, b) -> {
                if (datos[a][PTS] != datos[b][PTS]) return datos[b][PTS] - datos[a][PTS];
                if (datos[a][DG] != datos[b][DG]) return datos[b][DG] - datos[a][DG];
                if (datos[a][GF] != datos[b][GF]) return datos[b][GF] - datos[a][GF];
                return equipos[a].compareTo(equipos[b]);
            });
        }
        int[] orden = new int[idx.size()];
        for (int i = 0; i < orden.length; i++) {
            orden[i] = idx.get(i);
        }
        return orden;
    }

    public int totalPaginas() {
        return (equipos.length + TAM_PAGINA - 1) / TAM_PAGINA;
    }

    private static String linea(char relleno, char cruce, int[] anchos) {
        StringBuilder sb = new StringBuilder();
        sb.append(cruce);
        for (int a : anchos) {
            for (int i = 0; i < a + 2; i++) {
                sb.append(relleno);
            }
            sb.append(cruce);
        }
        return sb.toString();
    }

    /** Imprime una pagina de la tabla con formato. */
    public void imprimirPagina(int[] orden, int pagina) {
        int[] anchos = {3, 2, 22, 3, 3, 3, 3, 3, 3, 4, 3, 3, 4};
        String sep = linea('-', '+', anchos);
        int inicio = pagina * TAM_PAGINA;
        int fin = Math.min(inicio + TAM_PAGINA, orden.length);

        System.out.println(ConsoleColors.CYAN_BOLD_BRIGHT + sep);
        System.out.printf("| %3s | %2s | %-22s | %3s | %3s | %3s | %3s | %3s | %3s | %4s | %3s | %3s | %4s |%n",
                "#", "Gr", "Equipo", "PJ", "PG", "PE", "PP", "GF", "GC", "DG", "TA", "TR", "Pts");
        System.out.println(sep + ConsoleColors.RESET);
        for (int k = inicio; k < fin; k++) {
            int i = orden[k];
            int[] d = datos[i];
            System.out.printf("| %3d | %2s | %-22s | %3d | %3d | %3d | %3d | %3d | %3d | %+4d | %3d | %3d | %4d |%n",
                    k + 1, grupos[i], equipos[i], d[PJ], d[PG], d[PE], d[PP], d[GF], d[GC], d[DG], d[TA], d[TR], d[PTS]);
        }
        System.out.println(ConsoleColors.CYAN_BOLD_BRIGHT + sep + ConsoleColors.RESET);
    }

    /** Muestra la tabla completa paginada con navegacion por teclado. */
    public void mostrarPaginada(boolean porPuntos) {
        int[] orden = ordenar(porPuntos);
        int pagina = 0;
        int paginas = totalPaginas();
        boolean seguir = true;
        while (seguir) {
            System.out.println("\n  TABLA DE POSICIONES - " + (porPuntos ? "ordenada por puntos" : "por grupos")
                    + "  (pagina " + (pagina + 1) + "/" + paginas + ")");
            imprimirPagina(orden, pagina);
            System.out.print("[S] Siguiente  [A] Anterior  [N] Ir a pagina  [Q] Salir: ");
            String op = ConsoleInput.getString().trim().toUpperCase();
            if (op.equals("S")) {
                pagina = (pagina + 1) % paginas;
            } else if (op.equals("A")) {
                pagina = (pagina - 1 + paginas) % paginas;
            } else if (op.equals("N")) {
                System.out.print("Numero de pagina (1-" + paginas + "): ");
                int n = ConsoleInput.getInt();
                if (n >= 1 && n <= paginas) {
                    pagina = n - 1;
                } else {
                    System.out.println("Pagina invalida.");
                }
            } else if (op.equals("Q")) {
                seguir = false;
            } else {
                System.out.println("Opcion no valida.");
            }
        }
    }

    // ---------------------------------------------------------------
    // Persistencia en archivo plano
    // ---------------------------------------------------------------

    /** Guarda: equipo;grupo;PG;PE;PP;GF;GC;TA;TR */
    public void guardar(String ruta) {
        List<String> lineas = new ArrayList<>();
        lineas.add("# equipo;grupo;PG;PE;PP;GF;GC;TA;TR");
        for (int i = 0; i < equipos.length; i++) {
            int[] d = datos[i];
            lineas.add(equipos[i] + ";" + grupos[i] + ";" + d[PG] + ";" + d[PE] + ";" + d[PP] + ";"
                    + d[GF] + ";" + d[GC] + ";" + d[TA] + ";" + d[TR]);
        }
        try {
            Path p = Paths.get(ruta);
            if (p.getParent() != null) {
                Files.createDirectories(p.getParent());
            }
            Files.write(p, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("No se pudo guardar la tabla: " + e.getMessage());
        }
    }

    /** Carga los valores guardados (si el archivo existe). */
    public void cargar(String ruta) {
        Path p = Paths.get(ruta);
        if (!Files.exists(p)) {
            return;
        }
        try {
            for (String l : Files.readAllLines(p, StandardCharsets.UTF_8)) {
                String[] t = l.split(";");
                if (l.startsWith("#") || t.length < 9) {
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
        } catch (IOException | RuntimeException e) {
            System.out.println("Archivo de tabla invalido, se usan valores en cero: " + e.getMessage());
            for (int[] fila : datos) {
                Arrays.fill(fila, 0);
            }
        }
    }
}
