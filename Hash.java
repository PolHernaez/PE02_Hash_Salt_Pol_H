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
    int usuarisTotals = 0;

    String usuari[] = new String[3];
    String contrasenya[] = new String[3];
    String hashUsuari[] = new String[3];
    String saltUsuari[] = new String[3];

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
                            "5. Verificar l'integritat dels missatges.\n6. Registre d'usuaris amb salt\n7. Iniciar sessió\n8. Porves");
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
                    registreUsuari();
                    break;
                case 7:
                    IniciarSessio();
                    break;
                case 8:
                    provesPractica();
                    break;
                case 9:
                    System.out.println("...");
                    break;
                default:
                    break;
            }
        } while (menu != 9);
    }

    public void provesPractica() {
        System.out.println(usuari[0] + ": " + hashUsuari[0]);
        System.out.println(usuari[1] + ": " + hashUsuari[1]);
        if (!hashUsuari[0].equals(hashUsuari[1])) {
            System.out.println("ELS HASHES SÓN DIFERENTS!");
        } else {
            System.out.println("Són iguals");
        }

    }

    public void IniciarSessio() {
        String usuariIniciar = "";
        String contrasenyaIniciar = "";
        if (usuarisTotals == 0) {
            System.out.println("No hi han usuaris");
        } else {
            System.out.println("INICI DE SESSIÓ");
            usuariIniciar = llegirString("USUARI: ");

            contrasenyaIniciar = llegirString("POSA LA CONTRASSENYA: ");
            int indexUser = buscarUsuari(usuariIniciar);
            if (indexUser != 4) {
                String salt = saltUsuari[indexUser];
                String hash = hashUsuari[indexUser];
                //String nouHash = calcularHash("salt inventat" + contrasenyaIniciar);
                String nouHash = calcularHash(saltUsuari[indexUser] + contrasenyaIniciar);
                if (hashUsuari[indexUser].equals(nouHash)) {
                    System.out.println("Inici de sessió completat, benvingut " + usuari[indexUser]);
                } else {
                    System.out.println("Contrassenya incorrecte");
                }
            }
        }

    }

    public int buscarUsuari(String user) {

        for (int i = 0; i < usuarisTotals; i++) {
            if (usuari[i].equals(user)) {
                return i;
            }

        }
        System.out.println("No s'ha trobat cap usuari que coincideixi");
        return 4;
    }

    public void registreUsuari() {

        if (usuarisTotals == 3) {
            System.out.println("Màxim d'usuaris creats");
        } else {
            usuari[usuarisTotals] = llegirString("USUARI: ");

            String pass = llegirString("CREA UNA CONTRASSENYA: ");
            saltUsuari[usuarisTotals] = crearSalt();
            hashUsuari[usuarisTotals] = calcularHash(saltUsuari[usuarisTotals] + pass);

            usuarisTotals++;

        }
    }

    public String crearSalt() {
        char[] array = {
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z',
                '9', '8', '7', '6', '5', '4', '3', '2', '1', '0'
        };
        // un numeor de vegades aleatori s'afegeix una lletra aleatoria
        String salt = "";
        int num = (int) (Math.random() * 20) + 4;
        for (int i = 0; i < num; i++) {
            int caracterSeleccionat = (int) (Math.random() * array.length);
            char caracter = array[caracterSeleccionat];
            salt = salt + caracter;
        }
        return salt;
    }

    public void verificarIntegritat() {
        try {
            // S'escriu el missatge i el guarda a missatge.txt
            String missatge = llegirString("Escriu el missatge:");
            Files.writeString(Path.of("missatge.txt"), missatge); // genera el document txt

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
                opcio = llegirInt("Quin document vols comparar?\n 1 = missatgeDolent.txt\n 2 = missatge.txt\n");

            } while (opcio < 1 || opcio > 2);
            switch (opcio) {
                case 1:
                    triat = "missatgeDolent.txt";
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
            // 6. Mira si coincideixen
            if (hashRebut.equals(hashGuardat)) {
                System.out.println("COINCIDEIXEN!");
            } else {
                System.out.println("NO COINCIDEIXEN, ALGUNA COSA NO HA ANAT BÉ :[");
            }
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
            MessageDigest sha512 = MessageDigest.getInstance("SHA-256");

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
