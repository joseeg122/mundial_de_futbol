import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Calendario de partidos guardado en una matriz de texto.
 * Columnas: id, grupo, fecha, hora, local, visitante, golesLocal, golesVisitante.
 */
public class Fixture {

    public static final int ID = 0, GRUPO = 1, FECHA = 2, HORA = 3, LOCAL = 4, VISITANTE = 5, GL = 6, GV = 7;
    public static final String SIN_JUGAR = "-";

    private final String[][] partidos;

    private Fixture(String[][] partidos) {
        this.partidos = partidos;
    }

    /** Lee partidos.txt; devuelve null si no se puede leer. */
    public static Fixture cargar(String ruta) {
        Path p = Paths.get(ruta);
        List<String[]> filas = new ArrayList<>();
        try {
            for (String l : Files.readAllLines(p, StandardCharsets.UTF_8)) {
                String[] t = l.split(";");
                if (l.trim().isEmpty() || l.startsWith("#") || t.length < 8) {
                    continue;
                }
                for (int i = 0; i < t.length; i++) {
                    t[i] = t[i].trim();
                }
                filas.add(t);
            }
        } catch (IOException e) {
            System.out.println("No se pudo leer " + ruta + ": " + e.getMessage());
            return null;
        }
        if (filas.isEmpty()) {
            return null;
        }
        return new Fixture(filas.toArray(new String[0][]));
    }

    public int total() {
        return partidos.length;
    }

    /** Fila del partido con ese id (o null si no existe). */
    public String[] partido(int id) {
        for (String[] p : partidos) {
            if (Integer.parseInt(p[ID]) == id) {
                return p;
            }
        }
        return null;
    }

    public boolean existeGrupo(String grupo) {
        for (String[] p : partidos) {
            if (p[GRUPO].equalsIgnoreCase(grupo)) {
                return true;
            }
        }
        return false;
    }

    public boolean jugado(int id) {
        String[] p = partido(id);
        return p != null && !p[GL].equals(SIN_JUGAR);
    }

    public int golesLocal(int id) {
        return Integer.parseInt(partido(id)[GL]);
    }

    public int golesVisita(int id) {
        return Integer.parseInt(partido(id)[GV]);
    }

    public void registrarResultado(int id, int golesLocal, int golesVisita) {
        String[] p = partido(id);
        p[GL] = String.valueOf(golesLocal);
        p[GV] = String.valueOf(golesVisita);
    }

    // ---------------------------------------------------------------
    // Opciones de consulta
    // ---------------------------------------------------------------

    private static String resultado(String[] p) {
        return p[GL].equals(SIN_JUGAR) ? "pendiente" : p[GL] + " - " + p[GV];
    }

    /** Opcion 1: todos los partidos de un grupo. */
    public void mostrarPorGrupo(String grupo) {
        System.out.println("\n  PARTIDOS DEL GRUPO " + grupo.toUpperCase());
        String sep = "+-----+------------+-------+--------------------------------------------+-----------+";
        System.out.println(sep);
        System.out.printf("| %3s | %-10s | %-5s | %-42s | %-9s |%n", "ID", "Fecha", "Hora", "Partido", "Resultado");
        System.out.println(sep);
        for (String[] p : partidos) {
            if (p[GRUPO].equalsIgnoreCase(grupo)) {
                System.out.printf("| %3s | %-10s | %-5s | %-42s | %-9s |%n",
                        p[ID], p[FECHA], p[HORA], p[LOCAL] + " vs " + p[VISITANTE], resultado(p));
            }
        }
        System.out.println(sep);
    }

    /** Opcion 2: hora e integrantes de un partido especifico. */
    public boolean mostrarDetalle(int id) {
        String[] p = partido(id);
        if (p == null) {
            System.out.println("No existe un partido con ID " + id + ".");
            return false;
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
        return true;
    }

    // ---------------------------------------------------------------
    // Persistencia
    // ---------------------------------------------------------------

    public void guardar(String ruta) {
        List<String> lineas = new ArrayList<>();
        lineas.add("# id;grupo;fecha;hora;local;visitante;golesLocal;golesVisitante  ('-' = sin jugar)");
        for (String[] p : partidos) {
            lineas.add(String.join(";", p));
        }
        try {
            Files.write(Paths.get(ruta), lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("No se pudo guardar el fixture: " + e.getMessage());
        }
    }
}
