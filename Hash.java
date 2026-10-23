import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Hash {
    public static void main(String[] args) {
        Hash p = new Hash();
        p.principal();
    }

    Scanner sc = new Scanner(System.in);

    public void principal() {
        int menu = 0;
        do {
            System.out.println(
                    "1. Introduir un missatge per teclat i mostrar-ne el hash.\r\n" +
                            "2. Introduir dos missatges i comprovar si els seus hashes coincideixen.\r\n" +
                            "3. Calcular el hash d'un missatge, modificar-ne el contingut i tornar-lo a calcular per observar-ne les diferències.\r\n"
                            +
                            "4. Calcular diverses vegades el hash d'un mateix missatge i comprovar si el resultat es manté.\r\n"
                            +
                            "5. Verificar l'integritat dels missatges.\n6. Sortir");
            menu = llegirInt("X. Escull una opcio: ");
            switch (menu) {
                case 1:
                    missatgeTeclatHash(1);
                    break;
                case 2:
                    comprovarConcideixen();

                    break;
                case 3:
                    calcularModificar();
                    break;
                case 4:
                    int vegades = 0;
                    do {
                        vegades = llegirInt("Quantes vegades vols repetir? ");
                    } while (vegades < 0 || vegades > 10);
                    missatgeTeclatHash(vegades);
                    break;
                case 5:
                    verificarIntegritat();
                    break;
                case 6:
                    System.out.println("...");
                    break;
                default:
                    break;
            }
        } while (menu != 5);
    }

    public void verificarIntegritat() {
        try {
            // S'escriu el missatge i el guarda a missatge.txt
            String missatge = llegirString("Escriu el missatge:");
            Files.writeString(Path.of("missatge.txt"), missatge);

            // 2. Calcula el hash i el guarda a missatge.hash
            String hashOriginal = calcularHash(missatge);
            Files.writeString(Path.of("missatge.hash"), hashOriginal);

            System.out.println("Missatge i hash guardats");
            // 3. S'ha rebut el missatge
            System.out.println("S'ha rebut el missatge!");
            // 4. Escollir a llegir
            int opcio = 0;
            String triat = "";
            do {
                opcio = llegirInt("Quin document vols llegir?\n 1 = missatge.hash\n 2 = missatge.txt");
            } while (opcio < 1 || opcio > 2);
            switch (opcio) {
                case 1:
                    triat = "missatge.hash";
                    break;

                case 2:
                    triat = "missatge.txt";
                    break;
            }
            String rebut = Files.readString(Path.of(triat));
            // 5. Calcula el hash del que ha rebut i el compara amb el original
            String hashRebut = calcularHash(rebut);
            String hashGuardat = Files.readString(Path.of("missatge.hash"));

            System.out.println("Hash original: " + hashGuardat);
            System.out.println("Hash rebut:    " + hashRebut);
        } catch (IOException e) {
            System.out.println("Error amb els fitxers: " + e.getMessage());
        }
    }

    public void calcularModificar() {
        String m = llegirString("MISSATGE INICIAL:");
        String hash = calcularHash(m);
        System.out.println("HASH INICIAL: " + hash);
        // Triem una posició aleatòria del missatge i en canviem la lletra
        int pos = (int) (Math.random() * m.length());
        char nou = (char) ('a' + (int) (Math.random() * 26));

        // Si per casualitat és el mateix caràcter, en forcem un de diferent
        if (nou == m.charAt(pos)) {
            nou = (nou == 'z') ? 'a' : (char) (nou + 1);
        }
        char constructor[] = new char[hash.length()];
        String nouHash = "";
        for (int i = 0; i < hash.length(); i++) {
            char lletra = hash.charAt(i);
            if (pos != i) {
                constructor[i] = lletra;
            } else {
                constructor[i] = nou;
            }
            nouHash = nouHash + constructor[i];
        }
        System.out.println("HASH MODIFICAT: " + nouHash);
        String modificat = m.substring(0, pos) + nou + m.substring(pos + 1); // de hash a lletra
        System.out.println("MISSATGE MODIFICAT: " + modificat);
        // de hash a lletra
    }

    public void comprovarConcideixen() {
        String m1 = llegirString("Missatge 1: ");
        String m2 = llegirString("Missatge 2: ");
        if (calcularHash(m1).equals(calcularHash(m2))) {
            System.out.println("Tenen el mateix Hash (" + calcularHash(m1) + ")");
        } else {
            System.out.println("Tenen el Hash diferent");
        }
    }

    public void missatgeTeclatHash(int times) {
        String m = llegirString("Introdueix el teu missatge");
        String primer = calcularHash(m);
        boolean iguals = true;

        for (int i = 0; i < times; i++) {
            String hash = calcularHash(m);
            System.out.println("Hash: " + hash);
            if (!hash.equals(primer)) {
                iguals = false;
            }
        }

        if (iguals) {
            System.out.println("El resultat es manté: tots els hashes són iguals");
        } else {
            System.out.println("El resultat ha canviat");
        }
    }

    public String calcularHash(String missatge) {
        try {
            byte[] bytesIn = missatge.getBytes(StandardCharsets.UTF_8);

            // Instanciem la classe per fer el hash
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");

            // Calculem el hash
            byte[] hashResult = sha512.digest(bytesIn);

            // Convertim el hash a hexadecimal per mostrar-lo
            return HexFormat.of().withUpperCase().formatHex(hashResult);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Algorisme no disponible: " + e.getMessage());
            return "";
        }
    }

    public int llegirInt(String m) {
        int text = 0;
        boolean error = false;
        do {
            try {
                System.out.print(m);
                text = sc.nextInt();
                sc.nextLine();
                error = false;
            } catch (InputMismatchException e) {
                System.out.println("Error, escriu numeros");
                error = true;
                sc.next();
            } catch (Exception e) {
                System.out.println("Error desconegut, torna");
                error = true;
                sc.next();
            }
        } while (error);
        return text;
    }

    public String llegirString(String m) {
        String text = "";
        boolean error = false;
        do {
            try {
                System.out.println(m);
                text = sc.nextLine();
                error = false;
            } catch (InputMismatchException e) {
                System.out.println("Error, escriu lletres o numeros");
                error = true;
                sc.next();
            } catch (Exception e) {
                System.out.println("Error desconegut, torna");
                error = true;
                sc.next();
            }
        } while (error);
        return text;
    }

}
