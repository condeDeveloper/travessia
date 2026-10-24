package br.com.conde.travessia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("A regra do enunciado")
class RegraTest {

    @ParameterizedTest(name = "{0} ovelhas e {1} lobos: em paz? {2}")
    @CsvSource({
            "0, 0, true",
            "0, 5, true",
            "1, 0, true",
            "1, 1, true",
            "1, 2, false",
            "3, 3, true",
            "2, 3, false",
    })
    @DisplayName("sem ovelha nao ha problema, e com ovelha elas nao podem estar em menor numero")
    void emPaz(int ovelhas, int lobos, boolean esperado) {
        assertThat(Regra.emPaz(ovelhas, lobos)).isEqualTo(esperado);
    }

    @Test
    @DisplayName("a margem de la tambem conta, e e o que sobra")
    void asDuasMargens() {
        // a margem de partida esta em paz, mas a de chegada fica com 1 ovelha e 2 lobos
        assertThat(Regra.SO_AS_MARGENS.margensEmPaz(new Estado(2, 1, true), 3)).isFalse();
        assertThat(Regra.SO_AS_MARGENS.margensEmPaz(new Estado(1, 2, true), 3)).isFalse();
        assertThat(Regra.SO_AS_MARGENS.margensEmPaz(new Estado(3, 3, true), 3)).isTrue();
        assertThat(Regra.SO_AS_MARGENS.margensEmPaz(new Estado(0, 2, true), 3)).isTrue();
        assertThat(Regra.SO_AS_MARGENS.margensEmPaz(new Estado(3, 1, true), 3)).isTrue();
    }

    @Test
    @DisplayName("a leitura frouxa nao olha a carga do barco; a exigente olha")
    void aCargaDoBarco() {
        assertThat(Regra.SO_AS_MARGENS.cargaEmPaz(1, 3)).isTrue();
        assertThat(Regra.AS_MARGENS_E_O_BARCO.cargaEmPaz(1, 3)).isFalse();
        assertThat(Regra.AS_MARGENS_E_O_BARCO.cargaEmPaz(0, 3)).isTrue();
        assertThat(Regra.AS_MARGENS_E_O_BARCO.cargaEmPaz(2, 2)).isTrue();
    }

    @Test
    @DisplayName("mesmo assim, a leitura exigente nao proibe uma unica viagem que a outra permita")
    void aExigenteNaoProibeNada() {
        for (int pares = 1; pares <= 8; pares++) {
            for (int barco = 1; barco <= 6; barco++) {
                Travessia solta = new Travessia(pares, barco, Regra.SO_AS_MARGENS);
                Travessia presa = new Travessia(pares, barco, Regra.AS_MARGENS_E_O_BARCO);

                assertThat(Medidor.contaViagens(presa))
                        .as("%d pares, barco %d", pares, barco)
                        .isEqualTo(Medidor.contaViagens(solta));
                assertThat(presa.viagens()).isEqualTo(solta.viagens());
            }
        }
    }

    @Test
    @DisplayName("cada leitura tem um rotulo para a tabela")
    void rotulos() {
        assertThat(Regra.SO_AS_MARGENS.rotulo()).isEqualTo("so as margens");
        assertThat(Regra.AS_MARGENS_E_O_BARCO.rotulo()).isEqualTo("as margens e o barco");
    }
}
