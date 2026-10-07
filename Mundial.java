import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Mundial de Futbol en Consola - Taller Arreglos y Matrices.
 * Paso 1: banderas (matrices + escalado)  | Paso 2: tabla de posiciones
 * Paso 3: fixture de partidos              | Paso 4: archivos planos + ASCII art
 */
public class Mundial {

    static final String DIR = "recursos/";
    static final String ARCHIVO_BANDERAS = DIR + "Flags.csv";
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

    // Opciones del menu principal: la posicion en el arreglo + 1 es el numero de la opcion
    static final String[] OPCIONES = {
        "Ver banderas (4 tamanos)",
        "Tabla de posiciones (por grupos)",
        "Tabla de posiciones (ordenada por puntos)",
        "Fixture: ver partidos por grupo",
        "Fixture: hora e integrantes de un partido",
        "Registrar resultado de un partido",
        "Editar datos de la tabla",
        "Importar banderas de otro archivo (CSV)",
    };

    static char[][] banderas; // matriz 480 x 15 leida de Flags.csv (48 banderas de 10 x 15)
    static TablaPosiciones tabla;
    static Fixture fixture;

    public static void main(String[] args) {
        if (!cargarDatos()) {
            return;
        }

        // Banner con ASCII art
        System.out.println();
        for (int i = 0; i < BANNER_MUNDIAL.length; i++) {
            System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "  " + BANNER_MUNDIAL[i] + ConsoleColors.RESET);
        }
        for (int i = 0; i < BANNER_FUTBOL.length; i++) {
            System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "  " + BANNER_FUTBOL[i] + ConsoleColors.RESET);
        }
        for (int i = 0; i < BALON.length; i++) {
            System.out.println(ConsoleColors.WHITE_BOLD + "      " + BALON[i] + ConsoleColors.RESET);
        }
        System.out.println(ConsoleColors.CYAN_BOLD_BRIGHT + "        ~ Copa del Mundo 2026 en consola ~" + ConsoleColors.RESET);

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("  Elige una opcion: ", 0, OPCIONES.length);
            switch (opcion) {
                case 1:
                    menuBanderas();
                    break;
                case 2:
                    tabla.mostrarPaginada(false);
                    break;
                case 3:
                    tabla.mostrarPaginada(true);
                    break;
                case 4:
                    String grupo;
                    do {
                        System.out.print("\n  Grupo (A-L): ");
                        grupo = ConsoleInput.getString().trim();
                        if (!fixture.existeGrupo(grupo)) {
                            System.out.println("  Ese grupo no existe.");
                        }
                    } while (!fixture.existeGrupo(grupo));
                    fixture.mostrarPorGrupo(grupo);
                    break;
                case 5:
                    int id = leerEntero("\n  ID del partido (1-" + fixture.total() + "): ", 1, fixture.total());
                    fixture.mostrarDetalle(id);
                    break;
                case 6:
                    registrarResultado();
                    break;
                case 7:
                    editarTabla();
                    break;
                case 8:
                    importarBanderas();
                    break;
                default:
                    break;
            }
        } while (opcion != 0);

        guardarTodo();
        System.out.println("\n  Datos guardados. Hasta la proxima!");
    }

    // ---------------------------------------------------------------
    // Carga y guardado (Paso 4: archivos planos)
    // ---------------------------------------------------------------

    static boolean cargarDatos() {
        // Banderas: matriz leida del CSV del estandar
        banderas = Bandera.cargarCSV(ARCHIVO_BANDERAS);
        if (banderas == null) {
            System.out.println("No se pudo leer " + ARCHIVO_BANDERAS + ". Ejecuta el programa desde la carpeta del proyecto.");
            return false;
        }

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
    }

    // ---------------------------------------------------------------
    // Menu con ASCII art (Paso 4)
    // ---------------------------------------------------------------

    static void mostrarMenu() {
        String c = ConsoleColors.CYAN_BOLD_BRIGHT;
        String r = ConsoleColors.RESET;
        String linea = c + "  +--------------------------------------------------+" + r;

        System.out.println();
        System.out.println(linea);
        System.out.println(c + "  |" + r + "               M E N U   P R I N C I P A L        " + c + "|" + r);
        System.out.println(linea);
        for (int i = 0; i < OPCIONES.length; i++) {
            // %-45s rellena con espacios a la derecha para que el borde quede alineado
            System.out.println(c + "  |" + r + String.format("  %d. %-45s", i + 1, OPCIONES[i]) + c + "|" + r);
        }
        System.out.println(c + "  |" + r + String.format("  0. %-45s", "Guardar y salir") + c + "|" + r);
        System.out.println(linea);
    }

    // ---------------------------------------------------------------
    // Lectura validada
    // ---------------------------------------------------------------

    static int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            Integer valor = ConsoleInput.leerEnteroONull();
            if (valor == null) {
                System.out.println("  Debes escribir un numero entero (no letras ni vacio).");
            } else if (valor < min || valor > max) {
                System.out.println("  Valor invalido. Debe estar entre " + min + " y " + max + ".");
            } else {
                return valor;
            }
        }
    }

    // ---------------------------------------------------------------
    // Paso 1: banderas
    // ---------------------------------------------------------------

    static void menuBanderas() {
        int total = Bandera.TOTAL;
        int opcion;
        do {
            System.out.println("\n  --- BANDERAS ---");
            for (int i = 0; i < total; i++) {
                System.out.println("  " + (i + 1) + ". " + Bandera.PAISES[i]);
            }
            System.out.println("  " + (total + 1) + ". Ver todas (tamano Icono, comparacion)");
            System.out.println("  0. Volver");
            opcion = leerEntero("  Elige una bandera: ", 0, total + 1);

            if (opcion >= 1 && opcion <= total) {
                // Una bandera: elegir el tamano
                String nombre = Bandera.PAISES[opcion - 1];
                int fila = Bandera.filaInicio(opcion);
                System.out.println("\n  Tamano de " + nombre + ":");
                for (int i = 0; i < Bandera.TAMANOS.length; i++) {
                    System.out.println("  " + (i + 1) + ". " + Bandera.TAMANOS[i]);
                }
                System.out.println("  " + (Bandera.TAMANOS.length + 1) + ". Los 4 tamanos");
                int t = leerEntero("  Elige: ", 1, Bandera.TAMANOS.length + 1);
                if (t <= Bandera.TAMANOS.length) {
                    Bandera.dibujar(nombre, banderas, fila, t - 1);
                } else {
                    Bandera.dibujarTodosLosTamanos(nombre, banderas, fila);
                }
            } else if (opcion == total + 1) {
                for (int i = 1; i <= total; i++) {
                    Bandera.dibujar(Bandera.PAISES[i - 1], banderas, Bandera.filaInicio(i), Bandera.ICONO);
                }
            }
        } while (opcion != 0);
    }

    // ---------------------------------------------------------------
    // Paso 2 y 3: resultados y edicion de la tabla
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
        System.out.print("\n  Ruta del Flags.csv de un companero (ej: recursos/otro_grupo.csv): ");
        String ruta = ConsoleInput.getString().trim();
        char[][] otra = Bandera.cargarCSV(ruta);
        if (otra == null) {
            System.out.println("  No se pudo leer ese archivo.");
            return;
        }
        // Copia cada bandera que el otro archivo tenga dibujada (10 filas por bandera)
        int importadas = 0;
        for (int pais = 1; pais <= Bandera.TOTAL; pais++) {
            int inicio = Bandera.filaInicio(pais) - 1;
            if (otra[inicio][0] >= '1' && otra[inicio][0] <= '9') {
                for (int f = inicio; f < inicio + Bandera.FILAS_BANDERA; f++) {
                    banderas[f] = otra[f];
                }
                importadas++;
            }
        }
        if (importadas == 0) {
            System.out.println("  No se encontraron banderas en ese archivo.");
            return;
        }
        Bandera.guardarCSV(ARCHIVO_BANDERAS, banderas);
        System.out.println("  Banderas importadas: " + importadas + ". Ya se ven en el menu de banderas.");
    }
}
