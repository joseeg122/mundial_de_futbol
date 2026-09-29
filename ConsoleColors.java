/**
 * Colores ANSI para la consola (mismos nombres que el ConsoleColors de referencia).
 * Si tu repo ya tiene el archivo completo del profesor, puedes reemplazar este.
 */
public class ConsoleColors {

    public static final String RESET = "\033[0m";

    // Texto
    public static final String RED_BOLD_BRIGHT = "\033[1;91m";
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m";
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";
    public static final String WHITE_BOLD = "\033[1;37m";
    public static final String BLACK_BRIGHT = "\033[0;90m";

    // Fondos
    public static final String BLACK_BACKGROUND = "\033[40m";
    public static final String RED_BACKGROUND = "\033[41m";
    public static final String GREEN_BACKGROUND = "\033[42m";
    public static final String YELLOW_BACKGROUND = "\033[43m";
    public static final String BLUE_BACKGROUND = "\033[44m";
    public static final String PURPLE_BACKGROUND = "\033[45m";
    public static final String CYAN_BACKGROUND = "\033[46m";
    public static final String WHITE_BACKGROUND = "\033[47m";

    // Fondos de alta intensidad
    public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";
    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";
    public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m";
}
