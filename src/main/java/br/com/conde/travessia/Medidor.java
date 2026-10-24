package br.com.conde.travessia;

import java.util.List;
import java.util.Locale;

/**
 * As tabelas do README. Uso:
 * {@code mvn exec:java -Dexec.args="barcos"} (ou {@code viagens},
 * {@code estados}).
 */
public final class Medidor {

    private static final int MAIOR_GRUPO = 12;
    private static final int MAIOR_BARCO = 6;
    private static final String SEM = "nao da";

    private Medidor() {
    }

    public static void main(String[] args) {
        String tabela = args.length > 0 ? args[0] : "tudo";
        boolean tudo = tabela.equals("tudo");
        if (tudo || tabela.equals("barcos")) {
            barcos();
        }
        if (tudo || tabela.equals("viagens")) {
            viagens();
        }
        if (tudo || tabela.equals("estados")) {
            estados();
        }
    }

    /** Quais travessias existem, por tamanho do grupo e do barco. */
    public static void barcos() {
        System.out.println();
        System.out.println("BARCOS  (viagens minimas, ou SEM quando nao existe travessia)");
        System.out.printf(Locale.ROOT, "%-8s", "pares");
        for (int barco = 1; barco <= MAIOR_BARCO; barco++) {
            System.out.printf(Locale.ROOT, "%10s", "barco " + barco);
        }
        System.out.println();
        for (int pares = 1; pares <= MAIOR_GRUPO; pares++) {
            System.out.printf(Locale.ROOT, "%-8d", pares);
            for (int barco = 1; barco <= MAIOR_BARCO; barco++) {
                Travessia travessia = new Travessia(pares, barco, Regra.SO_AS_MARGENS);
                System.out.printf(Locale.ROOT, "%10s",
                        travessia.existe() ? String.valueOf(travessia.viagens()) : SEM);
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("ate onde cada barco chega:");
        for (int barco = 1; barco <= MAIOR_BARCO; barco++) {
            int ultimo = 0;
            for (int pares = 1; pares <= MAIOR_GRUPO; pares++) {
                if (!new Travessia(pares, barco, Regra.SO_AS_MARGENS).existe()) {
                    break;
                }
                ultimo = pares;
            }
            String ate = ultimo == 0 ? "nenhum grupo atravessa"
                    : ultimo == MAIOR_GRUPO ? "todos os " + MAIOR_GRUPO + " testados"
                    : "ate " + ultimo + " pares";
            System.out.printf(Locale.ROOT, "  barco %d: %s%n", barco, ate);
        }
        System.out.println();
        System.out.println("e o que muda se o enunciado tambem mandar na carga do barco:");
        System.out.printf(Locale.ROOT, "%-30s %18s %20s%n",
                "regra", "respostas iguais", "viagens permitidas");
        for (Regra regra : Regra.values()) {
            int iguais = 0;
            int comparadas = 0;
            long viagensPermitidas = 0;
            for (int pares = 1; pares <= MAIOR_GRUPO; pares++) {
                for (int barco = 1; barco <= MAIOR_BARCO; barco++) {
                    Travessia solta = new Travessia(pares, barco, Regra.SO_AS_MARGENS);
                    Travessia esta = new Travessia(pares, barco, regra);
                    viagensPermitidas += contaViagens(esta);
                    comparadas++;
                    if (solta.viagens() == esta.viagens()) {
                        iguais++;
                    }
                }
            }
            System.out.printf(Locale.ROOT, "%-30s %18s %20d%n", regra.rotulo(),
                    iguais + " de " + comparadas, viagensPermitidas);
        }
        System.out.println();
    }

    /** O caminho mais curto, e como o numero de viagens cresce. */
    public static void viagens() {
        Travessia classica = new Travessia(3, 2, Regra.SO_AS_MARGENS);
        System.out.println();
        System.out.println("VIAGENS  (3 pares num barco de 2: as 11 viagens, uma por linha)");
        List<Estado> caminho = classica.caminho();
        for (int passo = 0; passo < caminho.size(); passo++) {
            Estado estado = caminho.get(passo);
            System.out.printf(Locale.ROOT, "%2d  partida %d/%d   chegada %d/%d   barco na %s%n",
                    passo, estado.ovelhas(), estado.lobos(),
                    3 - estado.ovelhas(), 3 - estado.lobos(),
                    estado.barcoNaPartida() ? "partida" : "chegada");
        }
        System.out.println();
        System.out.println("quanto custa cada par a mais, por tamanho de barco:");
        System.out.printf(Locale.ROOT, "%-10s %44s%n", "barco",
                "viagens, de 1 ate " + MAIOR_GRUPO + " pares");
        for (int barco = 2; barco <= MAIOR_BARCO; barco++) {
            StringBuilder linha = new StringBuilder();
            for (int pares = 1; pares <= MAIOR_GRUPO; pares++) {
                Travessia travessia = new Travessia(pares, barco, Regra.SO_AS_MARGENS);
                linha.append(travessia.existe() ? String.valueOf(travessia.viagens()) : "-");
                linha.append(' ');
            }
            System.out.printf(Locale.ROOT, "%-10d %44s%n", barco, linha.toString().trim());
        }
        System.out.println();
    }

    /** Quantas situacoes seguras a travessia alcanca de fato. */
    public static void estados() {
        System.out.println();
        System.out.println("ESTADOS  (situacoes seguras contra situacoes que da para alcancar)");
        System.out.printf(Locale.ROOT, "%-8s %8s %12s %12s %14s %14s %10s%n",
                "pares", "barco", "possiveis", "seguras", "alcancadas", "inalcancaveis",
                "travessia");
        for (int barco = 2; barco <= 3; barco++) {
            for (int pares = 1; pares <= 8; pares++) {
                Travessia travessia = new Travessia(pares, barco, Regra.SO_AS_MARGENS);
                System.out.printf(Locale.ROOT, "%-8d %8d %12d %12d %14d %14d %10s%n",
                        pares, barco, 2 * (pares + 1) * (pares + 1), travessia.estadosSeguros(),
                        travessia.estadosAlcancados(), travessia.segurosInalcancaveis().size(),
                        travessia.existe() ? "existe" : SEM);
            }
        }
        System.out.println();
        System.out.println("quando nao ha travessia, nao e que falte um caminho: a parte");
        System.out.println("alcancavel do problema encolhe e para de crescer com o grupo.");
        System.out.println();
        int becos = 0;
        for (int pares = 1; pares <= 8; pares++) {
            for (int barco = 1; barco <= MAIOR_BARCO; barco++) {
                becos += new Travessia(pares, barco, Regra.SO_AS_MARGENS).becosSemSaida().size();
            }
        }
        System.out.printf(Locale.ROOT,
                "becos sem saida nas %d combinacoes medidas: %d%n", 8 * MAIOR_BARCO, becos);
        System.out.println("de toda situacao alcancavel sempre da para sair, porque o barco");
        System.out.println("acabou de chegar com alguem que pode voltar.");
        System.out.println();
    }

    /** Quantas viagens diferentes a regra permite, somando todas as situacoes seguras. */
    public static int contaViagens(Travessia travessia) {
        int total = 0;
        for (int ovelhas = 0; ovelhas <= travessia.quantos(); ovelhas++) {
            for (int lobos = 0; lobos <= travessia.quantos(); lobos++) {
                for (boolean lado : new boolean[]{true, false}) {
                    Estado estado = new Estado(ovelhas, lobos, lado);
                    if (travessia.regra().margensEmPaz(estado, travessia.quantos())) {
                        total += travessia.saidasDe(estado).size();
                    }
                }
            }
        }
        return total;
    }
}
