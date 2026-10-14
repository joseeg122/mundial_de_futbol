import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleInput {

    static Scanner sc = new Scanner(System.in);

    /** Lee una linea; si se cierra la entrada, termina el programa. */
    static String getString() {
        try {
            return sc.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada finalizada. Saliendo del programa.");
            System.exit(0);
            return "";
        }
    }

    /** Pide un entero entre min y max; repite la pregunta hasta que sea valido. */
    static int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            try {
                int valor = Integer.parseInt(getString().trim());
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.println("  Valor invalido. Debe estar entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Debes escribir un numero entero (no letras ni vacio).");
            }
        }
    }
}
