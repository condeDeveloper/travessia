package br.com.conde.travessia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A situacao")
class EstadoTest {

    @Test
    @DisplayName("o comeco tem todo mundo e o barco na partida")
    void oComeco() {
        Estado comeco = Estado.comecoDe(3);

        assertThat(comeco.ovelhas()).isEqualTo(3);
        assertThat(comeco.lobos()).isEqualTo(3);
        assertThat(comeco.barcoNaPartida()).isTrue();
        assertThat(comeco.acabou()).isFalse();
    }

    @Test
    @DisplayName("so acabou com a margem de partida vazia E o barco do outro lado")
    void oFim() {
        assertThat(Estado.fimDe(3).acabou()).isTrue();
        assertThat(new Estado(0, 0, true).acabou()).isFalse();
    }

    @Test
    @DisplayName("duas situacoes iguais sao a mesma situacao")
    void igualdade() {
        assertThat(new Estado(2, 1, true)).isEqualTo(new Estado(2, 1, true));
        assertThat(new Estado(2, 1, true)).isNotEqualTo(new Estado(2, 1, false));
    }

    @Test
    @DisplayName("o texto diz onde esta o barco")
    void texto() {
        assertThat(new Estado(2, 1, false).toString()).contains("2 ovelhas", "1 lobos",
                "barco na chegada");
    }

    @Test
    @DisplayName("bicho negativo nao e situacao")
    void bichoNegativo() {
        assertThatThrownBy(() -> new Estado(-1, 0, true))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Estado(0, -1, true))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
