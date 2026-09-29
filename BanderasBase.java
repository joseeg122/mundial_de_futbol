import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Definicion (pixel art) de las banderas de nuestro grupo en la resolucion
 * maxima (GRANDE): matriz de 18 filas x 24 columnas.
 *
 * Codigos de color:
 * 0 Transparente | 1 Rojo | 2 Azul | 3 Blanco | 4 Amarillo | 5 Verde
 * 6 Morado | 7 Cyan | 8 Negro | 9 Gris
 */
public class BanderasBase {

    public static final int FILAS = 18;
    public static final int COLS = 24;

    public static final int ROJO = 1, AZUL = 2, BLANCO = 3, AMARILLO = 4, VERDE = 5;

    /** Devuelve todas las banderas de nuestro grupo (nombre -> matriz). */
    public static Map<String, int[][]> todas() {
        Map<String, int[][]> m = new LinkedHashMap<>();
        m.put("Croacia", croacia());
        m.put("Qatar", qatar());
        m.put("Uruguay", uruguay());
        m.put("Senegal", senegal());
        m.put("Nueva Zelanda", nuevaZelanda());
        m.put("Australia", australia());
        m.put("Paises Bajos", paisesBajos());
        return m;
    }

    // ---------------------------------------------------------------
    // Utilidades para dibujar sobre la matriz
    // ---------------------------------------------------------------

    private static int[][] nueva(int fondo) {
        int[][] m = new int[FILAS][COLS];
        rect(m, 0, 0, FILAS - 1, COLS - 1, fondo);
        return m;
    }

    /** Rellena el rectangulo [f1..f2] x [c1..c2] con un color. */
    private static void rect(int[][] m, int f1, int c1, int f2, int c2, int color) {
        for (int f = f1; f <= f2; f++) {
            for (int c = c1; c <= c2; c++) {
                m[f][c] = color;
            }
        }
    }

    /** Franjas horizontales de igual alto con los colores dados. */
    private static void franjasHorizontales(int[][] m, int... colores) {
        int alto = FILAS / colores.length;
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLS; c++) {
                m[f][c] = colores[Math.min(f / alto, colores.length - 1)];
            }
        }
    }

    /** Dibuja una "estrella" en forma de cruz 3x3 centrada en (f, c). */
    private static void estrellaCruz(int[][] m, int f, int c, int color) {
        m[f][c] = color;
        m[f - 1][c] = color;
        m[f + 1][c] = color;
        m[f][c - 1] = color;
        m[f][c + 1] = color;
    }

    /** Copia un patron de texto (digitos) en la matriz a partir de (f0, c0). */
    private static void patron(int[][] m, int f0, int c0, String... filas) {
        for (int i = 0; i < filas.length; i++) {
            for (int j = 0; j < filas[i].length(); j++) {
                char ch = filas[i].charAt(j);
                if (ch != '.') {
                    m[f0 + i][c0 + j] = ch - '0';
                }
            }
        }
    }

    /** Union Jack en la esquina superior izquierda (canton de 9 filas x 12 columnas). */
    private static void unionJack(int[][] m) {
        patron(m, 0, 0,
                "132231132231",
                "313231132313",
                "231331133132",
                "333331133333",
                "111111111111",
                "333331133333",
                "231331133132",
                "313231132313",
                "132231132231");
    }

    // ---------------------------------------------------------------
    // Banderas
    // ---------------------------------------------------------------

    /** Croacia: rojo, blanco y azul con escudo de cuadros rojos y blancos. */
    private static int[][] croacia() {
        int[][] m = nueva(BLANCO);
        franjasHorizontales(m, ROJO, BLANCO, AZUL);
        // corona
        rect(m, 3, 8, 3, 15, AZUL);
        m[2][8] = AZUL; m[2][10] = AZUL; m[2][13] = AZUL; m[2][15] = AZUL;
        // borde blanco del escudo
        rect(m, 4, 8, 13, 15, BLANCO);
        // tablero 4 x 3 de cuadros de 2 x 2 (empieza en rojo)
        for (int f = 5; f <= 12; f++) {
            for (int c = 9; c <= 14; c++) {
                boolean rojo = ((f - 5) / 2 + (c - 9) / 2) % 2 == 0;
                m[f][c] = rojo ? ROJO : BLANCO;
            }
        }
        return m;
    }

    /** Qatar: granate (se usa rojo como sustituto) con franja blanca dentada de 9 puntas. */
    private static int[][] qatar() {
        int[][] m = nueva(ROJO);
        for (int f = 0; f < FILAS; f++) {
            int ancho = (f % 2 == 0) ? 6 : 9; // diente del zigzag
            rect(m, f, 0, f, ancho - 1, BLANCO);
        }
        return m;
    }

    /** Uruguay: 9 franjas blancas y azules, canton blanco con el Sol de Mayo. */
    private static int[][] uruguay() {
        int[][] m = nueva(BLANCO);
        for (int f = 0; f < FILAS; f++) {
            int color = ((f / 2) % 2 == 0) ? BLANCO : AZUL;
            rect(m, f, 0, f, COLS - 1, color);
        }
        rect(m, 0, 0, 7, 7, BLANCO);          // canton
        rect(m, 1, 1, 6, 6, AMARILLO);        // sol
        m[1][1] = BLANCO; m[1][6] = BLANCO;   // esquinas recortadas
        m[6][1] = BLANCO; m[6][6] = BLANCO;
        return m;
    }

    /** Senegal: verde, amarillo y rojo verticales con estrella verde. */
    private static int[][] senegal() {
        int[][] m = nueva(AMARILLO);
        rect(m, 0, 0, FILAS - 1, 7, VERDE);
        rect(m, 0, 8, FILAS - 1, 15, AMARILLO);
        rect(m, 0, 16, FILAS - 1, 23, ROJO);
        patron(m, 5, 9,
                "..55..",
                "..55..",
                "555555",
                ".5555.",
                ".5555.",
                "55..55",
                "5....5");
        return m;
    }

    /** Nueva Zelanda: fondo azul, Union Jack y 4 estrellas rojas (Cruz del Sur). */
    private static int[][] nuevaZelanda() {
        int[][] m = nueva(AZUL);
        unionJack(m);
        estrellaCruz(m, 3, 18, ROJO);
        estrellaCruz(m, 8, 15, ROJO);
        estrellaCruz(m, 7, 21, ROJO);
        estrellaCruz(m, 14, 18, ROJO);
        return m;
    }

    /** Australia: fondo azul, Union Jack, estrella de la Commonwealth y Cruz del Sur. */
    private static int[][] australia() {
        int[][] m = nueva(AZUL);
        unionJack(m);
        patron(m, 11, 3,
                "..3..",
                ".333.",
                "33333",
                ".333.",
                "..3..");
        estrellaCruz(m, 3, 18, BLANCO);
        estrellaCruz(m, 8, 15, BLANCO);
        estrellaCruz(m, 7, 21, BLANCO);
        estrellaCruz(m, 14, 18, BLANCO);
        m[11][20] = BLANCO; // estrella pequena
        return m;
    }

    /** Paises Bajos: rojo, blanco y azul horizontales. */
    private static int[][] paisesBajos() {
        int[][] m = nueva(BLANCO);
        franjasHorizontales(m, ROJO, BLANCO, AZUL);
        return m;
    }
}
