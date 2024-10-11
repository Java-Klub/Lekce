
Object[] obarvi(Character[] pole) {
    var obarvenePole = new String[pole.length];
    for (int i = 0; i < pole.length; i++) {
        obarvenePole[i] = switch (pole[i]) {
            case '1' -> "\033[32m1\033[0m"; 
            case '2' -> "\033[36m2\033[0m"; 
            case '3' -> "\033[35m3\033[0m"; 
            case '4', '5', '6', '7', '8' -> "\033[31m" + pole[i] + "\033[0m"; 
            case '?' -> "\033[37m?\033[0m"; 
            default -> "" + pole[i];    
        };
    }
    return obarvenePole;
}

void main() {

    var pocetZivotu = 3;
    var pocetMin = 6;

    var velikost = 6;
    var pocetPolicek = velikost * velikost;
    var zbyvaOdkryt = pocetPolicek - pocetMin - 4 * velikost + 4; //okraje a miny hráč neodkrývá

    var minovePole = new Character[pocetPolicek];
    Arrays.fill(minovePole, '0');

    //umístíme miny náhodně do hracího pole (kromě okrajů)
    var nahoda = new Random();
    while (pocetMin > 0) {
        var i = velikost * nahoda.nextInt(1, velikost - 1) + nahoda.nextInt(1, velikost - 1);
        if (minovePole[i] != '*') {
            minovePole[i] = '*';
            pocetMin--;
            for (var okoli : new int[]{-velikost - 1, -velikost, -velikost + 1, -1, 1, velikost - 1, velikost, velikost + 1}) {
                if (minovePole[i + okoli] != '*') {
                    minovePole[i + okoli]++;
                }
            }
        }
    }
    //nahradíme 0 prázdnými políčky
    for (int i = 0; i < pocetPolicek; i++) {
        if (minovePole[i] == '0') {
            minovePole[i] = ' ';
        }
    }

    var herniPole = new Character[pocetPolicek];
    Arrays.fill(herniPole, '?');

    //před začátkem hry odkryjeme okraje
    for (int i = 0; i < velikost; i++) {
        //horní okraj
        herniPole[i] = minovePole[i];
        //dolní okraj
        herniPole[pocetPolicek - i - 1] = minovePole[pocetPolicek - i - 1];
        //levý okraj
        herniPole[velikost * i] = minovePole[velikost * i];
        //pravý okraj
        herniPole[velikost * i + velikost - 1] = minovePole[velikost * i + velikost - 1];
    }

    var klavesnice = new Scanner(System.in);

    //tady začíná hlavní herní cyklus
    while (true) {
        println("zbyvající počet životů: " + pocetZivotu);

        //vykreslíme minové pole
        println("""
             A   B   C   D   E   F
           +---+---+---+---+---+---+
         1 | %s | %s | %s | %s | %s | %s |  1
           +---+---+---+---+---+---+
         2 | %s | %s | %s | %s | %s | %s |  2
           +---+---+---+---+---+---+
         3 | %s | %s | %s | %s | %s | %s |  3
           +---+---+---+---+---+---+
         4 | %s | %s | %s | %s | %s | %s |  4
           +---+---+---+---+---+---+
         5 | %s | %s | %s | %s | %s | %s |  5
           +---+---+---+---+---+---+
         6 | %s | %s | %s | %s | %s | %s |  6
           +---+---+---+---+---+---+
             A   B   C   D   E   F
        """.formatted(obarvi(herniPole)));

        //zkontrolujeme, jestli už není vše hotovo
        if (zbyvaOdkryt == 0) {
            println("konec hry, vše je odkryto");
            return;
        }
        if (pocetZivotu == 0) {
            println("konec hry, došli ti životy");
            return;
        }

        //zeptáme se hráče na souřadnice
        println("zadej souřadnice kam si stoupneš (písmeno a číslo):");
        try {
            var souradnice = klavesnice.next("[A-Fa-f][1-6]");
            var i = souradnice.toUpperCase().charAt(0) - 'A' + velikost * (souradnice.charAt(1) - '1');
            //odkryjeme souřadnice
            if (herniPole[i] == '?' || herniPole[i] == '*') {
                herniPole[i] = minovePole[i];
                if (minovePole[i] == '*') {
                    println("boooom! tady byla mina");
                    pocetZivotu--;
                    if (pocetZivotu == 0) {
                        //konec hry, zobrazíme miny
                        herniPole = minovePole;
                    }
                } else {
                    zbyvaOdkryt--;
                    if (zbyvaOdkryt == 0) {
                        //pokud je vše odkryto, zobrazíme miny
                        herniPole = minovePole;
                    }
                }
            }
        } catch (Exception e) {
            println("špatně zadané souřadnice, zkus to znovu");
            klavesnice.nextLine();
        }
    }
}
