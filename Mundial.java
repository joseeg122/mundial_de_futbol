import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mundial de Futbol en Consola - Taller Arreglos y Matrices.
 * Paso 1: banderas (matrices + escalado)  | Paso 2: tabla de posiciones
 * Paso 3: fixture de partidos              | Paso 4: archivos planos + ASCII art
 */
public class Mundial {

    static final String DIR = "recursos/";
    static final String ARCHIVO_BANDERAS = DIR + "info_banderas.csv";
    static final String ARCHIVO_EQUIPOS = DIR + "equipos.txt";
    static final String ARCHIVO_TABLA = DIR + "tabla_posiciones.txt";
    static final String ARCHIVO_PARTIDOS = DIR + "partidos.txt";

    static final String[] BANNER_MUNDIAL = {
        " __  __ _   _ _   _ ____ ___    _    _",
        "|  \\/  | | | | \\ | |  _ \\_ _|  / \\  | |",
        "| |\\/| | | | |  \\| | | | | |  / _ \\ | |",
        "| |  | | |_| | |\\  | |_| | | / ___ \\| |___",
        "|_|  |_|\\___/|_| \\_|____/___/_/   \\_\\_____|",
    };

    static final String[] BANNER_FUTBOL = {
        " _____ _   _ _____ ____   ___  _",
        "|  ___| | | |_   _| __ ) / _ \\| |",
        "| |_  | | | | | | |  _ \\| | | | |",
        "|  _| | |_| | | | | |_) | |_| | |___",
        "|_|    \\___/  |_| |____/ \\___/|_____|",
    };

    static final String[] BALON = {
        "      _.--\"\"\"--._",
        "    .'  o  /\\  o '.",
        "   /  \\  /  \\  /  \\",
        "  |  --( ()  () )-- |",
        "   \\  /  \\  /  \\  /",
        "    '.  o \\/ o  .'",
        "      `-._____.-'"
    };

    static Map<String, int[][]> banderas = new LinkedHashMap<>();
    static TablaPosiciones tabla;
    static Fixture fixture;

    public static void main(String[] args) {
        if (!cargarDatos()) {
            return;
        }
        mostrarBanner();
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("  Elige una opcion: ", 0, 8);
            switch (opcion) {
                case 1: menuBanderas(); break;
                case 2: tabla.mostrarPaginada(false); break;
                case 3: tabla.mostrarPaginada(true); break;
                case 4: verPartidosPorGrupo(); break;
                case 5: verDetallePartido(); break;
                case 6: registrarResultado(); break;
                case 7: editarTabla(); break;
                case 8: importarBanderas(); break;
                default: break;
            }
        } while (opcion != 0);
        guardarTodo();
        System.out.println("\n  Datos guardados. Hasta la proxima!");
    }

    // ---------------------------------------------------------------
    // Carga y guardado (Paso 4: archivos planos)
    // ---------------------------------------------------------------

    static boolean cargarDatos() {
        // Banderas: primero las nuestras y luego las del archivo (que puede traer las de otros equipos)
        banderas.putAll(BanderasBase.todas());
        banderas.putAll(Bandera.cargarCSV(ARCHIVO_BANDERAS));
        Bandera.guardarCSV(ARCHIVO_BANDERAS, banderas);

        // Equipos
        List<String> nombres = new ArrayList<>();
        List<String> grupos = new ArrayList<>();
        try {
            for (String l : Files.readAllLines(Paths.get(ARCHIVO_EQUIPOS), StandardCharsets.UTF_8)) {
                String[] t = l.split(";");
                if (l.startsWith("#") || t.length < 2) {
                    continue;
                }
                grupos.add(t[0].trim());
                nombres.add(t[1].trim());
            }
        } catch (IOException e) {
            System.out.println("No se pudo leer " + ARCHIVO_EQUIPOS + ". Ejecuta el programa desde la carpeta del proyecto.");
            return false;
        }
        tabla = new TablaPosiciones(nombres.toArray(new String[0]), grupos.toArray(new String[0]));
        tabla.cargar(ARCHIVO_TABLA);

        // Fixture
        fixture = Fixture.cargar(ARCHIVO_PARTIDOS);
        if (fixture == null) {
            System.out.println("No se pudo cargar " + ARCHIVO_PARTIDOS);
            return false;
        }
        return true;
    }

    static void guardarTodo() {
        tabla.guardar(ARCHIVO_TABLA);
        fixture.guardar(ARCHIVO_PARTIDOS);
        Bandera.guardarCSV(ARCHIVO_BANDERAS, banderas);
    }

    // ---------------------------------------------------------------
    // Menu con ASCII art (Paso 4)
    // ---------------------------------------------------------------

    static void mostrarBanner() {
        System.out.println();
        for (String l : BANNER_MUNDIAL) {
            System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "  " + l + ConsoleColors.RESET);
        }
        for (String l : BANNER_FUTBOL) {
            System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "  " + l + ConsoleColors.RESET);
        }
        for (String l : BALON) {
            System.out.println(ConsoleColors.WHITE_BOLD + "      " + l + ConsoleColors.RESET);
        }
        System.out.println(ConsoleColors.CYAN_BOLD_BRIGHT + "        ~ Copa del Mundo 2026 en consola ~" + ConsoleColors.RESET);
    }

    static void mostrarMenu() {
        String c = ConsoleColors.CYAN_BOLD_BRIGHT;
        String r = ConsoleColors.RESET;
        System.out.println();
        System.out.println(c + "  +--------------------------------------------------+" + r);
        System.out.println(c + "  |" + r + "               M E N U   P R I N C I P A L        " + c + "|" + r);
        System.out.println(c + "  +--------------------------------------------------+" + r);
        System.out.println(c + "  |" + r + "  1. Ver banderas (4 tamanos)                     " + c + "|" + r);
        System.out.println(c + "  |" + r + "  2. Tabla de posiciones (por grupos)             " + c + "|" + r);
        System.out.println(c + "  |" + r + "  3. Tabla de posiciones (ordenada por puntos)    " + c + "|" + r);
        System.out.println(c + "  |" + r + "  4. Fixture: ver partidos por grupo              " + c + "|" + r);
        System.out.println(c + "  |" + r + "  5. Fixture: hora e integrantes de un partido    " + c + "|" + r);
        System.out.println(c + "  |" + r + "  6. Registrar resultado de un partido            " + c + "|" + r);
        System.out.println(c + "  |" + r + "  7. Editar datos de la tabla                     " + c + "|" + r);
        System.out.println(c + "  |" + r + "  8. Importar banderas de otro archivo (CSV)      " + c + "|" + r);
        System.out.println(c + "  |" + r + "  0. Guardar y salir                              " + c + "|" + r);
        System.out.println(c + "  +--------------------------------------------------+" + r);
    }

    // ---------------------------------------------------------------
    // Lectura validada
    // ---------------------------------------------------------------

    static int leerEntero(String mensaje, int min, int max) {
        int valor;
        boolean valido;
        do {
            System.out.print(mensaje);
            valor = ConsoleInput.getInt();
            valido = valor >= min && valor <= max;
            if (!valido) {
                System.out.println("  Valor invalido. Debe estar entre " + min + " y " + max + ".");
            }
        } while (!valido);
        return valor;
    }

    // ---------------------------------------------------------------
    // Paso 1: banderas
    // ---------------------------------------------------------------

    static void menuBanderas() {
        String[] nombres = banderas.keySet().toArray(new String[0]);
        int opcion;
        do {
            System.out.println("\n  --- BANDERAS ---");
            for (int i = 0; i < nombres.length; i++) {
                System.out.println("  " + (i + 1) + ". " + nombres[i]);
            }
            System.out.println("  " + (nombres.length + 1) + ". Ver todas (tamano Icono, comparacion)");
            System.out.println("  0. Volver");
            opcion = leerEntero("  Elige una bandera: ", 0, nombres.length + 1);
            if (opcion >= 1 && opcion <= nombres.length) {
                mostrarBandera(nombres[opcion - 1]);
            } else if (opcion == nombres.length + 1) {
                for (String n : nombres) {
                    Bandera.dibujar(n, banderas.get(n), Bandera.ICONO);
                }
            }
        } while (opcion != 0);
    }

    static void mostrarBandera(String nombre) {
        System.out.println("\n  Tamano de " + nombre + ":");
        for (int i = 0; i < Bandera.TAMANOS.length; i++) {
            System.out.println("  " + (i + 1) + ". " + Bandera.TAMANOS[i]);
        }
        System.out.println("  " + (Bandera.TAMANOS.length + 1) + ". Los 4 tamanos");
        int t = leerEntero("  Elige: ", 1, Bandera.TAMANOS.length + 1);
        int[][] base = banderas.get(nombre);
        if (t <= Bandera.TAMANOS.length) {
            Bandera.dibujar(nombre, base, Bandera.FACTORES[t - 1]);
        } else {
            Bandera.dibujarTodosLosTamanos(nombre, base);
        }
    }

    // ---------------------------------------------------------------
    // Paso 3: fixture
    // ---------------------------------------------------------------

    static void verPartidosPorGrupo() {
        String grupo;
        do {
            System.out.print("\n  Grupo (A-L): ");
            grupo = ConsoleInput.getString().trim();
            if (!fixture.existeGrupo(grupo)) {
                System.out.println("  Ese grupo no existe.");
            }
        } while (!fixture.existeGrupo(grupo));
        fixture.mostrarPorGrupo(grupo);
    }

    static void verDetallePartido() {
        int id = leerEntero("\n  ID del partido (1-" + fixture.total() + "): ", 1, fixture.total());
        fixture.mostrarDetalle(id);
    }

    // ---------------------------------------------------------------
    // Paso 2: edicion de la tabla
    // ---------------------------------------------------------------

    static void registrarResultado() {
        int id = leerEntero("\n  ID del partido (1-" + fixture.total() + "): ", 1, fixture.total());
        fixture.mostrarDetalle(id);
        String[] p = fixture.partido(id);
        int local = tabla.buscar(p[Fixture.LOCAL]);
        int visita = tabla.buscar(p[Fixture.VISITANTE]);
        if (local < 0 || visita < 0) {
            System.out.println("  Los equipos de este partido no estan en la tabla.");
            return;
        }
        int gl = leerEntero("  Goles de " + p[Fixture.LOCAL] + ": ", 0, 99);
        int gv = leerEntero("  Goles de " + p[Fixture.VISITANTE] + ": ", 0, 99);
        if (fixture.jugado(id)) {
            // corrige: quita el resultado anterior antes de aplicar el nuevo
            tabla.aplicarResultado(local, visita, fixture.golesLocal(id), fixture.golesVisita(id), -1);
        }
        tabla.aplicarResultado(local, visita, gl, gv, 1);
        fixture.registrarResultado(id, gl, gv);
        guardarTodo();
        System.out.println("  Resultado registrado: " + p[Fixture.LOCAL] + " " + gl + " - " + gv + " " + p[Fixture.VISITANTE]);
    }

    static void editarTabla() {
        System.out.print("\n  Nombre del equipo a editar (ej: Uruguay): ");
        int fila = tabla.buscar(ConsoleInput.getString());
        if (fila < 0) {
            System.out.println("  Equipo no encontrado.");
            return;
        }
        System.out.println("  Editando: " + tabla.getEquipo(fila));
        for (int i = 0; i < TablaPosiciones.EDITABLES.length; i++) {
            int col = TablaPosiciones.EDITABLES[i];
            System.out.println("  " + (i + 1) + ". " + TablaPosiciones.COLUMNAS[col]
                    + "  (actual: " + tabla.getValor(fila, col) + ")");
        }
        int op = leerEntero("  Columna a cambiar (0 = cancelar): ", 0, TablaPosiciones.EDITABLES.length);
        if (op == 0) {
            return;
        }
        int col = TablaPosiciones.EDITABLES[op - 1];
        int valor = leerEntero("  Nuevo valor para " + TablaPosiciones.COLUMNAS[col] + ": ", 0, 999);
        tabla.editar(fila, col, valor);
        guardarTodo();
        System.out.println("  Tabla actualizada (PJ, DG y Pts se recalculan solos).");
    }

    // ---------------------------------------------------------------
    // Paso 4: colaboracion global
    // ---------------------------------------------------------------

    static void importarBanderas() {
        System.out.print("\n  Ruta del archivo CSV de un companero (ej: recursos/otro_grupo.csv): ");
        String ruta = ConsoleInput.getString().trim();
        Map<String, int[][]> nuevas = Bandera.cargarCSV(ruta);
        if (nuevas.isEmpty()) {
            System.out.println("  No se encontraron banderas en ese archivo.");
            return;
        }
        banderas.putAll(nuevas);
        Bandera.guardarCSV(ARCHIVO_BANDERAS, banderas);
        System.out.println("  Banderas importadas: " + nuevas.size() + ". Ya aparecen en el menu de banderas.");
    }
}
