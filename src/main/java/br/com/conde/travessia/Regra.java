package br.com.conde.travessia;

/**
 * O que conta como situacao segura.
 *
 * <p>O enunciado do problema costuma dizer apenas "os lobos nunca podem ficar em
 * maior numero que as ovelhas". Ele nao diz se isso vale tambem DENTRO do barco,
 * e essa omissao nao e um detalhe: ela muda quais travessias existem.
 *
 * <p>Por isso as duas leituras estao aqui, e nenhuma e tratada como a certa. O
 * que o repositorio mede e quanto a escolha custa.
 */
public enum Regra {

    /** So as duas margens precisam estar em paz; o barco e lugar de passagem. */
    SO_AS_MARGENS("so as margens"),

    /** O barco tambem e um lugar, e la dentro vale a mesma regra. */
    AS_MARGENS_E_O_BARCO("as margens e o barco");

    private final String rotulo;

    Regra(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    /** Um grupo esta em paz se nao ha lobo nenhum ou se as ovelhas nao estao em menor numero. */
    public static boolean emPaz(int ovelhas, int lobos) {
        return ovelhas == 0 || ovelhas >= lobos;
    }

    /** As duas margens, com o total de cada bicho conhecido. */
    public boolean margensEmPaz(Estado estado, int quantos) {
        return emPaz(estado.ovelhas(), estado.lobos())
                && emPaz(quantos - estado.ovelhas(), quantos - estado.lobos());
    }

    /** A carga do barco, que so importa na leitura mais exigente. */
    public boolean cargaEmPaz(int ovelhas, int lobos) {
        return this == SO_AS_MARGENS || emPaz(ovelhas, lobos);
    }
}
