import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Calendario de partidos guardado en una matriz de texto.
 * Columnas: id, grupo, fecha, hora, local, visitante, golesLocal, golesVisitante.
 */
public class Fixture {

    static final int ID = 0, GRUPO = 1, FECHA = 2, HORA = 3, LOCAL = 4, VISITANTE = 5, GL = 6, GV = 7;
    static final String SIN_JUGAR = "-";
    static final int MAX_PARTIDOS = 200;

    static String[][] partidos = new String[MAX_PARTIDOS][8];
    static int total = 0; // cuantos partidos se leyeron del archivo

    /** Lee partidos.txt. Devuelve false si no se pudo leer o esta vacio. */
    static boolean cargar(String ruta) {
        total = 0;
        try {
            BufferedReader archivo = new BufferedReader(new FileReader(ruta));
            String linea;
            while ((linea = archivo.readLine()) != null && total < MAX_PARTIDOS) {
                String[] t = linea.split(";");
                if (linea.trim().length() == 0 || linea.startsWith("#") || t.length < 8) {
                    continue;
                }
                for (int i = 0; i < 8; i++) {
                    partidos[total][i] = t[i].trim();
                }
                total++;
            }
            archivo.close();
        } catch (IOException e) {
            System.out.println("No se pudo leer " + ruta + ": " + e.getMessage());
            return false;
        }
        return total > 0;
    }

    /** Fila del partido con ese id (o null si no existe). */
    static String[] partido(int id) {
        for (int i = 0; i < total; i++) {
            if (Integer.parseInt(partidos[i][ID]) == id) {
                return partidos[i];
            }
        }
        return null;
    }

    static boolean existeGrupo(String grupo) {
        for (int i = 0; i < total; i++) {
            if (partidos[i][GRUPO].equalsIgnoreCase(grupo)) {
                return true;
            }
        }
        return false;
    }

    static boolean jugado(int id) {
        String[] p = partido(id);
        return p != null && !p[GL].equals(SIN_JUGAR);
    }

    static int golesLocal(int id) {
        return Integer.parseInt(partido(id)[GL]);
    }

    static int golesVisita(int id) {
        return Integer.parseInt(partido(id)[GV]);
    }

    static void registrarResultado(int id, int golesLocal, int golesVisita) {
        String[] p = partido(id);
        p[GL] = "" + golesLocal;
        p[GV] = "" + golesVisita;
    }

    // ---------------------------------------------------------------
    // Opciones de consulta
    // ---------------------------------------------------------------

    static String resultado(String[] p) {
        if (p[GL].equals(SIN_JUGAR)) {
            return "pendiente";
        }
        return p[GL] + " - " + p[GV];
    }

    /** Muestra todos los partidos de un grupo. */
    static void mostrarPorGrupo(String grupo) {
        String sep = "+-----+------------+-------+--------------------------------------------+-----------+";
        System.out.println("\n  PARTIDOS DEL GRUPO " + grupo.toUpperCase());
        System.out.println(sep);
        System.out.println("| " + TablaPosiciones.rellenarIzquierda("ID", 3)
                + " | " + TablaPosiciones.rellenarDerecha("Fecha", 10)
                + " | " + TablaPosiciones.rellenarDerecha("Hora", 5)
                + " | " + TablaPosiciones.rellenarDerecha("Partido", 42)
                + " | " + TablaPosiciones.rellenarDerecha("Resultado", 9) + " |");
        System.out.println(sep);
        for (int i = 0; i < total; i++) {
            String[] p = partidos[i];
            if (p[GRUPO].equalsIgnoreCase(grupo)) {
                System.out.println("| " + TablaPosiciones.rellenarIzquierda(p[ID], 3)
                        + " | " + TablaPosiciones.rellenarDerecha(p[FECHA], 10)
                        + " | " + TablaPosiciones.rellenarDerecha(p[HORA], 5)
                        + " | " + TablaPosiciones.rellenarDerecha(p[LOCAL] + " vs " + p[VISITANTE], 42)
                        + " | " + TablaPosiciones.rellenarDerecha(resultado(p), 9) + " |");
            }
        }
        System.out.println(sep);
    }

    /** Muestra la hora e integrantes de un partido especifico. */
    static void mostrarDetalle(int id) {
        String[] p = partido(id);
        if (p == null) {
            System.out.println("No existe un partido con ID " + id + ".");
            return;
        }
        int jornada = ((id - 1) % 6) / 2 + 1;
        System.out.println("\n  ==============================================");
        System.out.println("   PARTIDO #" + p[ID] + "  -  Grupo " + p[GRUPO] + "  (Jornada " + jornada + ")");
        System.out.println("  ==============================================");
        System.out.println("   Fecha       : " + p[FECHA]);
        System.out.println("   Hora        : " + p[HORA]);
        System.out.println("   Integrantes : " + p[LOCAL] + "  vs  " + p[VISITANTE]);
        System.out.println("   Resultado   : " + resultado(p));
        System.out.println("  ==============================================");
    }

    // ---------------------------------------------------------------
    // Archivo plano
    // ---------------------------------------------------------------

    static void guardar(String ruta) {
        try {
            PrintWriter salida = new PrintWriter(new FileWriter(ruta));
            salida.println("# id;grupo;fecha;hora;local;visitante;golesLocal;golesVisitante  ('-' = sin jugar)");
            for (int i = 0; i < total; i++) {
                String linea = partidos[i][0];
                for (int c = 1; c < 8; c++) {
                    linea = linea + ";" + partidos[i][c];
                }
                salida.println(linea);
            }
            salida.close();
        } catch (IOException e) {
            System.out.println("No se pudo guardar el fixture: " + e.getMessage());
        }
    }
}
