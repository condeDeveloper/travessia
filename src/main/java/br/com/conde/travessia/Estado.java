package br.com.conde.travessia;

/**
 * Onde todo mundo esta.
 *
 * <p>So e preciso guardar o que ficou na margem de partida: o resto esta na
 * outra margem. O barco esta de um lado ou do outro, e isso faz parte do estado,
 * porque a mesma distribuicao de bichos com o barco do outro lado e outra
 * situacao.
 */
public record Estado(int ovelhas, int lobos, boolean barcoNaPartida) {

    public Estado {
        if (ovelhas < 0 || lobos < 0) {
            throw new IllegalArgumentException("nao da para ter menos de zero bicho na margem");
        }
    }

    public static Estado comecoDe(int quantos) {
        return new Estado(quantos, quantos, true);
    }

    public static Estado fimDe(int quantos) {
        return new Estado(0, 0, false);
    }

    public boolean acabou() {
        return ovelhas == 0 && lobos == 0 && !barcoNaPartida;
    }

    @Override
    public String toString() {
        return "partida: %d ovelhas e %d lobos, barco %s"
                .formatted(ovelhas, lobos, barcoNaPartida ? "na partida" : "na chegada");
    }
}
