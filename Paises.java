import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Ficha de cada pais: capital, participaciones en mundiales y 11 titulares.
 * Columnas del archivo: equipo;capital;participaciones;jugador1,jugador2,...,jugador11
 */
public class Paises {

    static final int MAX = 48;
    static final int TITULARES = 11;

    static String[] nombres = new String[MAX];
    static String[] capitales = new String[MAX];
    static int[] participaciones = new int[MAX];
    static String[][] jugadores = new String[MAX][TITULARES];
    static int total = 0;

    /** Lee paises.txt. Devuelve false si no se pudo leer o esta vacio. */
    static boolean cargar(String ruta) {
        total = 0;
        try {
            BufferedReader archivo = new BufferedReader(new FileReader(ruta));
            String linea;
            while ((linea = archivo.readLine()) != null && total < MAX) {
                String[] t = linea.split(";");
                if (linea.startsWith("#") || t.length < 4) {
                    continue;
                }
                String[] nombresJugadores = t[3].split(",");
                if (nombresJugadores.length < TITULARES) {
                    continue;
                }
                nombres[total] = t[0].trim();
                capitales[total] = t[1].trim();
                participaciones[total] = Integer.parseInt(t[2].trim());
                for (int j = 0; j < TITULARES; j++) {
                    jugadores[total][j] = nombresJugadores[j].trim();
                }
                total++;
            }
            archivo.close();
        } catch (IOException e) {
            System.out.println("No se pudo leer " + ruta);
            return false;
        } catch (NumberFormatException e) {
            System.out.println("Archivo de paises invalido: " + ruta);
            return false;
        }
        return total > 0;
    }

    /** Lista numerada de los paises en 3 columnas. */
    static void listar() {
        System.out.println("\n  --- PAISES ---");
        for (int i = 0; i < total; i++) {
            String item = "  " + TablaPosiciones.rellenarIzquierda("" + (i + 1), 2) + ". " + nombres[i];
            if (i % 3 == 2 || i == total - 1) {
                System.out.println(item);
            } else {
                System.out.print(TablaPosiciones.rellenarDerecha(item, 32));
            }
        }
    }

    /** Muestra la capital, las participaciones y los 11 titulares del pais (1..total). */
    static void mostrar(int numero) {
        int i = numero - 1;
        System.out.println("\n  ==============================================");
        System.out.println("   " + nombres[i].toUpperCase());
        System.out.println("  ==============================================");
        System.out.println("   Capital         : " + capitales[i]);
        System.out.println("   Mundiales       : " + participaciones[i] + " (incluye el de 2026)");
        System.out.println("   Once titular    :");
        for (int j = 0; j < TITULARES; j++) {
            System.out.println("     " + TablaPosiciones.rellenarIzquierda("" + (j + 1), 2) + ". " + jugadores[i][j]);
        }
        System.out.println("  ==============================================");
    }
}
