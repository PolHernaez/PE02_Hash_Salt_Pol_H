import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Scanner;

public class Hash01 {

    public static void main(String[] args)
            throws NoSuchAlgorithmException {

        Scanner sc = new Scanner(System.in);

        String textIn = "";

        System.out.print("Entra text: ");

        while (textIn.isEmpty()) {
            textIn = sc.nextLine();
        }

        // Convertim l'string a un array de bytes
        byte[] bytesIn = textIn.getBytes(StandardCharsets.UTF_8);

        // Instanciem la classe per fer el hash
        MessageDigest sha512 = MessageDigest.getInstance("SHA-512");

        // Calculem el hash
        byte[] hashResult = sha512.digest(bytesIn);

        // Convertim el hash a hexadecimal per mostrar-lo
        String textOut = HexFormat.of().withUpperCase()
                .formatHex(hashResult);

        System.out.println("Hash: " + textOut);

        sc.close();
    }
}