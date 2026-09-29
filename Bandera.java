import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dibujo de banderas con matrices, escalado en 4 tamanos y lectura/escritura
 * del archivo plano recursos/info_banderas.csv.
 *
 * Formato del archivo (uno por bandera):
 *   Nombre;filas;columnas
 *   <fila 1: un digito de color por celda>
 *   ...
 */


public class Bandera {

    // Factor de escala (cuantas celdas de la base se fusionan en una)
    public static final int GRANDE = 1;
    public static final int MEDIANO = 2;
    public static final int PEQUENO = 3;
    public static final int ICONO = 6;

    public static final int[] FACTORES = {GRANDE, MEDIANO, PEQUENO, ICONO};
    public static final String[] TAMANOS = {
        "Grande (maximo detalle)",
        "Mediano (resolucion intermedia)",
        "Pequeno (baja resolucion)",
        "Icono (miniatura para menus)"
    };

    // Indice = codigo de color del enunciado (0 = transparente)
    private static final String[] COLORES = {
        ConsoleColors.RESET,                        // 0 transparente
        ConsoleColors.RED_BACKGROUND,               // 1 rojo
        ConsoleColors.BLUE_BACKGROUND,              // 2 azul
        ConsoleColors.WHITE_BACKGROUND_BRIGHT,      // 3 blanco
        ConsoleColors.YELLOW_BACKGROUND_BRIGHT,     // 4 amarillo
        ConsoleColors.GREEN_BACKGROUND,             // 5 verde
        ConsoleColors.PURPLE_BACKGROUND,            // 6 morado
        ConsoleColors.CYAN_BACKGROUND,              // 7 cyan
        ConsoleColors.BLACK_BACKGROUND,             // 8 negro
        ConsoleColors.BLACK_BACKGROUND_BRIGHT       // 9 gris
    };

    // ---------------------------------------------------------------
    // Algoritmo de escalado
    // ---------------------------------------------------------------

    /**
     * Reduce la matriz base dividiendola en bloques de factor x factor.
     * Cada bloque se convierte en una celda con el color que mas se repite
     * (si hay empate gana el color del centro del bloque).
     */
    public static int[][] escalar(int[][] base, int factor) {
        if (factor <= 1) {
            return base;
        }
        int filas = Math.max(1, base.length / factor);
        int cols = Math.max(1, base[0].length / factor);
        int[][] resultado = new int[filas][cols];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < cols; j++) {
                resultado[i][j] = colorDominante(base, i * factor, j * factor, factor);
            }
        }
        return resultado;
    }

    private static int colorDominante(int[][] base, int f0, int c0, int factor) {
        int[] cuenta = new int[COLORES.length];
        int fMax = Math.min(f0 + factor, base.length);
        int cMax = Math.min(c0 + factor, base[0].length);
        for (int f = f0; f < fMax; f++) {
            for (int c = c0; c < cMax; c++) {
                cuenta[codigo(base[f][c])]++;
            }
        }
        int fc = Math.min(f0 + factor / 2, base.length - 1);
        int cc = Math.min(c0 + factor / 2, base[0].length - 1);
        int mejor = codigo(base[fc][cc]);
        for (int k = 0; k < cuenta.length; k++) {
            if (cuenta[k] > cuenta[mejor]) {
                mejor = k;
            }
        }
        return mejor;
    }

    private static int codigo(int valor) {
        return (valor < 0 || valor >= COLORES.length) ? 0 : valor;
    }

    // ---------------------------------------------------------------
    // Dibujo en consola
    // ---------------------------------------------------------------

    /** Cada celda se pinta con 2 espacios de fondo de color (queda casi cuadrada). */
    public static void dibujar(int[][] matriz) {
        for (int f = 0; f < matriz.length; f++) {
            StringBuilder linea = new StringBuilder("   ");
            for (int c = 0; c < matriz[f].length; c++) {
                linea.append(COLORES[codigo(matriz[f][c])]).append("  ");
            }
            linea.append(ConsoleColors.RESET);
            System.out.println(linea);
        }
    }

    /** Escala la bandera base con el factor indicado y la dibuja con su titulo. */
    public static void dibujar(String nombre, int[][] base, int factor) {
        int[][] m = escalar(base, factor);
        System.out.println("\n " + nombre + " - " + nombreTamano(factor)
                + " [" + m.length + "x" + m[0].length + "]");
        dibujar(m);
    }

    /** Dibuja la bandera en sus 4 tamanos, uno tras otro. */
    public static void dibujarTodosLosTamanos(String nombre, int[][] base) {
        for (int factor : FACTORES) {
            dibujar(nombre, base, factor);
        }
    }

    private static String nombreTamano(int factor) {
        for (int i = 0; i < FACTORES.length; i++) {
            if (FACTORES[i] == factor) {
                return TAMANOS[i];
            }
        }
        return "factor " + factor;
    }

    // ---------------------------------------------------------------
    // Archivo plano (CSV)
    // ---------------------------------------------------------------

    /** Quita tildes/mayusculas para comparar nombres ("Países Bajos" == "paises bajos"). */
    public static String normalizar(String texto) {
        String t = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return t.replaceAll("\\p{M}", "").trim().toLowerCase();
    }

    public static void guardarCSV(String ruta, Map<String, int[][]> banderas) {
        List<String> lineas = new ArrayList<>();
        for (Map.Entry<String, int[][]> e : banderas.entrySet()) {
            int[][] m = e.getValue();
            lineas.add(e.getKey() + ";" + m.length + ";" + m[0].length);
            for (int f = 0; f < m.length; f++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < m[f].length; c++) {
                    sb.append(m[f][c]);
                }
                lineas.add(sb.toString());
            }
        }
        try {
            Path p = Paths.get(ruta);
            if (p.getParent() != null) {
                Files.createDirectories(p.getParent());
            }
            Files.write(p, lineas, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.out.println("No se pudo guardar " + ruta + ": " + ex.getMessage());
        }
    }

    /** Lee un archivo de banderas; si no existe devuelve un mapa vacio. */
    public static Map<String, int[][]> cargarCSV(String ruta) {
        Map<String, int[][]> mapa = new LinkedHashMap<>();
        Path p = Paths.get(ruta);
        if (!Files.exists(p)) {
            return mapa;
        }
        try {
            List<String> lineas = Files.readAllLines(p, StandardCharsets.UTF_8);
            int i = 0;
            while (i < lineas.size()) {
                String l = lineas.get(i).trim();
                String[] h = l.split(";");
                if (l.isEmpty() || l.startsWith("#") || h.length < 3) {
                    i++;
                    continue;
                }
                String nombre = h[0].trim();
                int filas = Integer.parseInt(h[1].trim());
                int cols = Integer.parseInt(h[2].trim());
                int[][] m = new int[filas][cols];
                for (int f = 0; f < filas; f++) {
                    String fila = lineas.get(i + 1 + f).replaceAll("[^0-9]", "");
                    for (int c = 0; c < cols && c < fila.length(); c++) {
                        m[f][c] = fila.charAt(c) - '0';
                    }
                }
                mapa.put(nombre, m);
                i += filas + 1;
            }
        } catch (IOException | RuntimeException ex) {
            System.out.println("Archivo de banderas con formato invalido (" + ruta + "): " + ex.getMessage());
        }
        return mapa;
    }
}
