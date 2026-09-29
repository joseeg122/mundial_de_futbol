import java.util.Scanner;

/**
 * Lectura estandarizada por teclado (misma API del ConsoleInput del profesor:
 * getInt, getFloat, getString, refreshScanner).
 */
public class ConsoleInput {

    public static Scanner sc = new Scanner(System.in);

    public static int getInt() {
        int temp = 0;
        try {
            temp = sc.nextInt();
            sc.nextLine();
        } catch (Exception e) {
            refreshScanner();
            System.out.println("Error de lectura de numero entero");
        }
        return temp;
    }

    public static float getFloat() {
        float temp = 0;
        try {
            temp = sc.nextFloat();
            sc.nextLine();
        } catch (Exception e) {
            refreshScanner();
            System.out.println("Error de lectura de numero flotante");
        }
        return temp;
    }

    public static String getString() {
        String temp = "";
        try {
            temp = sc.nextLine();
        } catch (Exception e) {
            refreshScanner();
            System.out.println("Error de lectura de cadena");
        }
        return temp;
    }

    public static void refreshScanner() {
        sc = new Scanner(System.in);
    }
}
