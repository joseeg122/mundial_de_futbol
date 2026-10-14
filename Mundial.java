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

    public static void main(String[] args) {
        if (!cargarDatos()) {
            return;
        }

        mostrarBanner();

        int opcion;
        do {
            mostrarMenu();
            opcion = ConsoleInput.leerEntero("  Elige una opcion: ", 0, OPCIONES.length);
            switch (opcion) {
                case 1:
                    menuBanderas();
                    break;
                case 2:
                    TablaPosiciones.mostrarPaginada(false);
                    break;
                case 3:
                    TablaPosiciones.mostrarPaginada(true);
                    break;
                case 4:
                    verGrupo();
                    break;
                case 5:
                    Fixture.mostrarDetalle(pedirIdPartido());
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
            }
        } while (opcion != 0);

        guardarTodo();
        System.out.println("\n  Datos guardados. Hasta la proxima!");
    }

    static void imprimirArte(String[] arte, String color, String sangria) {
        for (int i = 0; i < arte.length; i++) {
            System.out.println(color + sangria + arte[i] + ConsoleColors.RESET);
        }
    }

    static void mostrarBanner() {
        System.out.println();
        imprimirArte(BANNER_MUNDIAL, ConsoleColors.YELLOW_BOLD_BRIGHT, "  ");
        imprimirArte(BANNER_FUTBOL, ConsoleColors.GREEN_BOLD_BRIGHT, "  ");
        imprimirArte(BALON, ConsoleColors.WHITE_BOLD, "      ");
        System.out.println(ConsoleColors.CYAN_BOLD_BRIGHT + "        ~ Copa del Mundo 2026 en consola ~" + ConsoleColors.RESET);
    }

    static int pedirIdPartido() {
        return ConsoleInput.leerEntero("\n  ID del partido (1-" + Fixture.total + "): ", 1, Fixture.total);
    }

    static void verGrupo() {
        String grupo;
        do {
            System.out.print("\n  Grupo (A-L): ");
            grupo = ConsoleInput.getString().trim();
            if (!Fixture.existeGrupo(grupo)) {
                System.out.println("  Ese grupo no existe.");
            }
        } while (!Fixture.existeGrupo(grupo));
        Fixture.mostrarPorGrupo(grupo);
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

        // Equipos y tabla de posiciones
        if (!TablaPosiciones.cargarEquipos(ARCHIVO_EQUIPOS)) {
            return false;
        }
        TablaPosiciones.cargar(ARCHIVO_TABLA);

        // Fixture
        if (!Fixture.cargar(ARCHIVO_PARTIDOS)) {
            System.out.println("No se pudo cargar " + ARCHIVO_PARTIDOS);
            return false;
        }
        return true;
    }

    static void guardarTodo() {
        TablaPosiciones.guardar(ARCHIVO_TABLA);
        Fixture.guardar(ARCHIVO_PARTIDOS);
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
            // rellenarDerecha completa con espacios para que el borde quede alineado
            String texto = "  " + (i + 1) + ". " + OPCIONES[i];
            System.out.println(c + "  |" + r + TablaPosiciones.rellenarDerecha(texto, 50) + c + "|" + r);
        }
        System.out.println(c + "  |" + r + TablaPosiciones.rellenarDerecha("  0. Guardar y salir", 50) + c + "|" + r);
        System.out.println(linea);
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
            opcion = ConsoleInput.leerEntero("  Elige una bandera: ", 0, total + 1);

            if (opcion >= 1 && opcion <= total) {
                // Una bandera: elegir el tamano
                String nombre = Bandera.PAISES[opcion - 1];
                int fila = Bandera.filaInicio(opcion);
                System.out.println("\n  Tamano de " + nombre + ":");
                for (int i = 0; i < Bandera.TAMANOS.length; i++) {
                    System.out.println("  " + (i + 1) + ". " + Bandera.TAMANOS[i]);
                }
                System.out.println("  " + (Bandera.TAMANOS.length + 1) + ". Los 4 tamanos");
                int t = ConsoleInput.leerEntero("  Elige: ", 1, Bandera.TAMANOS.length + 1);
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
        int id = pedirIdPartido();
        Fixture.mostrarDetalle(id);
        String[] p = Fixture.partido(id);
        int local = TablaPosiciones.buscar(p[Fixture.LOCAL]);
        int visita = TablaPosiciones.buscar(p[Fixture.VISITANTE]);
        if (local < 0 || visita < 0) {
            System.out.println("  Los equipos de este partido no estan en la tabla.");
            return;
        }
        int gl = ConsoleInput.leerEntero("  Goles de " + p[Fixture.LOCAL] + ": ", 0, 99);
        int gv = ConsoleInput.leerEntero("  Goles de " + p[Fixture.VISITANTE] + ": ", 0, 99);
        if (Fixture.jugado(id)) {
            // corrige: quita el resultado anterior antes de aplicar el nuevo
            TablaPosiciones.aplicarResultado(local, visita, Fixture.golesLocal(id), Fixture.golesVisita(id), -1);
        }
        TablaPosiciones.aplicarResultado(local, visita, gl, gv, 1);
        Fixture.registrarResultado(id, gl, gv);
        guardarTodo();
        System.out.println("  Resultado registrado: " + p[Fixture.LOCAL] + " " + gl + " - " + gv + " " + p[Fixture.VISITANTE]);
    }

    static void editarTabla() {
        System.out.print("\n  Nombre del equipo a editar (ej: Uruguay): ");
        int fila = TablaPosiciones.buscar(ConsoleInput.getString());
        if (fila < 0) {
            System.out.println("  Equipo no encontrado.");
            return;
        }
        System.out.println("  Editando: " + TablaPosiciones.equipos[fila]);
        for (int i = 0; i < TablaPosiciones.EDITABLES.length; i++) {
            int col = TablaPosiciones.EDITABLES[i];
            System.out.println("  " + (i + 1) + ". " + TablaPosiciones.COLUMNAS[col]
                    + "  (actual: " + TablaPosiciones.datos[fila][col] + ")");
        }
        int op = ConsoleInput.leerEntero("  Columna a cambiar (0 = cancelar): ", 0, TablaPosiciones.EDITABLES.length);
        if (op == 0) {
            return;
        }
        int col = TablaPosiciones.EDITABLES[op - 1];
        int valor = ConsoleInput.leerEntero("  Nuevo valor para " + TablaPosiciones.COLUMNAS[col] + ": ", 0, 999);
        TablaPosiciones.editar(fila, col, valor);
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
