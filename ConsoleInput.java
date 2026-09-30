import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleInput {

    public static Scanner sc = new Scanner(System.in);

    /** Lee una linea; si se cierra la entrada, termina el programa. */
    public static String getString() {
        try {
            return sc.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada finalizada. Saliendo del programa.");
            System.exit(0);
            return "";
        } catch (IllegalStateException e) {
            refreshScanner();
            System.out.println("Error de lectura de cadena");
            return "";
        }
    }

    /** Lee un entero; devuelve null si el texto no es un entero valido. */
    public static Integer leerEnteroONull() {
        String t = getString().trim();
        try {
            return Integer.valueOf(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int getInt() {
        Integer v = leerEnteroONull();
        if (v == null) {
            System.out.println("Error de lectura de numero entero");
            return 0;
        }
        return v;
    }

    public static float getFloat() {
        String t = getString().trim().replace(',', '.');
        try {
            return Float.parseFloat(t);
        } catch (NumberFormatException e) {
            System.out.println("Error de lectura de numero flotante");
            return 0;
        }
    }

    public static void refreshScanner() {
        sc = new Scanner(System.in);
    }
}