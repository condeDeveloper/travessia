package br.com.conde.travessia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A travessia")
class TravessiaTest {

    @Test
    @DisplayName("o caso classico: tres pares num barco de dois, em onze viagens")
    void oClassico() {
        Travessia travessia = new Travessia(3, 2, Regra.SO_AS_MARGENS);

        assertThat(travessia.existe()).isTrue();
        assertThat(travessia.viagens()).isEqualTo(11);
    }

    @ParameterizedTest(name = "com {0} pares e barco de 2")
    @ValueSource(ints = {4, 5, 6, 7, 8, 9, 10})
    @DisplayName("a partir de quatro pares, o barco de dois nao atravessa ninguem")
    void oBarcoDeDoisTemLimite(int pares) {
        Travessia travessia = new Travessia(pares, 2, Regra.SO_AS_MARGENS);

        assertThat(travessia.existe()).isFalse();
        assertThat(travessia.viagens()).isEqualTo(-1);
        assertThat(travessia.caminho()).isEmpty();
    }

    @ParameterizedTest(name = "com {0} pares")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8})
    @DisplayName("com barco de um ninguem atravessa, nem um par sozinho")
    void oBarcoDeUmNuncaFunciona(int pares) {
        assertThat(new Travessia(pares, 1, Regra.SO_AS_MARGENS).existe()).isFalse();
    }

    @ParameterizedTest(name = "com {0} pares")
    @ValueSource(ints = {1, 2, 3, 5, 8, 13, 20})
    @DisplayName("com barco de quatro, todo grupo atravessa")
    void oBarcoDeQuatroSempreFunciona(int pares) {
        assertThat(new Travessia(pares, 4, Regra.SO_AS_MARGENS).existe()).isTrue();
    }

    @ParameterizedTest(name = "barco de {0} atravessa ate {1} pares")
    @CsvSource({"2, 3", "3, 5"})
    @DisplayName("cada barco pequeno tem um limite exato")
    void oLimiteDeCadaBarco(int barco, int ultimo) {
        assertThat(new Travessia(ultimo, barco, Regra.SO_AS_MARGENS).existe()).isTrue();
        assertThat(new Travessia(ultimo + 1, barco, Regra.SO_AS_MARGENS).existe()).isFalse();
    }

    @Test
    @DisplayName("o caminho tem uma situacao a mais que o numero de viagens")
    void oTamanhoDoCaminho() {
        Travessia travessia = new Travessia(3, 2, Regra.SO_AS_MARGENS);

        assertThat(travessia.caminho()).hasSize(travessia.viagens() + 1);
    }

    @Test
    @DisplayName("cada passo do caminho e uma viagem de verdade, e o fim e o fim")
    void oCaminhoEhLegitimo() {
        for (int pares = 1; pares <= 6; pares++) {
            for (int barco = 2; barco <= 5; barco++) {
                Travessia travessia = new Travessia(pares, barco, Regra.SO_AS_MARGENS);
                List<Estado> caminho = travessia.caminho();
                if (caminho.isEmpty()) {
                    continue;
                }
                assertThat(caminho.get(0)).isEqualTo(Estado.comecoDe(pares));
                assertThat(caminho.get(caminho.size() - 1).acabou()).isTrue();
                for (int passo = 1; passo < caminho.size(); passo++) {
                    assertThat(travessia.saidasDe(caminho.get(passo - 1)))
                            .as("%d pares, barco %d, passo %d", pares, barco, passo)
                            .contains(caminho.get(passo));
                }
            }
        }
    }

    @Test
    @DisplayName("toda viagem troca o barco de lado e leva de um ate a capacidade")
    void todaViagemEhUmaViagem() {
        Travessia travessia = new Travessia(4, 3, Regra.SO_AS_MARGENS);

        for (int ovelhas = 0; ovelhas <= 4; ovelhas++) {
            for (int lobos = 0; lobos <= 4; lobos++) {
                for (boolean lado : new boolean[]{true, false}) {
                    Estado estado = new Estado(ovelhas, lobos, lado);
                    for (Estado proximo : travessia.saidasDe(estado)) {
                        int carga = Math.abs(proximo.ovelhas() - estado.ovelhas())
                                + Math.abs(proximo.lobos() - estado.lobos());
                        assertThat(proximo.barcoNaPartida()).isNotEqualTo(lado);
                        assertThat(carga).isBetween(1, 3);
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("nenhuma viagem leva a uma situacao que a regra proibe")
    void nenhumaViagemLevaAoProibido() {
        for (int pares = 1; pares <= 6; pares++) {
            Travessia travessia = new Travessia(pares, 3, Regra.SO_AS_MARGENS);
            for (int ovelhas = 0; ovelhas <= pares; ovelhas++) {
                for (int lobos = 0; lobos <= pares; lobos++) {
                    for (boolean lado : new boolean[]{true, false}) {
                        for (Estado proximo : travessia.saidasDe(new Estado(ovelhas, lobos, lado))) {
                            assertThat(Regra.SO_AS_MARGENS.margensEmPaz(proximo, pares)).isTrue();
                        }
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("nao existe beco sem saida: de toda situacao alcancavel da para sair")
    void semBecoSemSaida() {
        for (int pares = 1; pares <= 8; pares++) {
            for (int barco = 1; barco <= 6; barco++) {
                assertThat(new Travessia(pares, barco, Regra.SO_AS_MARGENS).becosSemSaida())
                        .as("%d pares, barco %d", pares, barco)
                        .isEmpty();
            }
        }
    }

    @Test
    @DisplayName("quando nao ha travessia, a parte alcancavel do problema encolhe")
    void semTravessiaOProblemaEncolhe() {
        Travessia tres = new Travessia(3, 2, Regra.SO_AS_MARGENS);
        Travessia quatro = new Travessia(4, 2, Regra.SO_AS_MARGENS);

        assertThat(quatro.estadosSeguros()).isGreaterThan(tres.estadosSeguros());
        assertThat(quatro.estadosAlcancados()).isLessThan(tres.estadosAlcancados());
        assertThat(tres.estadosAlcancados()).isEqualTo(16);
        assertThat(quatro.estadosAlcancados()).isEqualTo(11);
    }

    @Test
    @DisplayName("as situacoes alcancadas sao sempre seguras, e nem toda segura e alcancada")
    void alcancadasSaoSeguras() {
        Travessia travessia = new Travessia(3, 2, Regra.SO_AS_MARGENS);

        assertThat(travessia.estadosAlcancados()).isLessThan(travessia.estadosSeguros());
        assertThat(travessia.segurosInalcancaveis())
                .hasSize(travessia.estadosSeguros() - travessia.estadosAlcancados())
                .contains(new Estado(0, 0, true));
    }

    @Test
    @DisplayName("grupo vazio ou barco de zero lugares nao sao travessia")
    void entradaInvalida() {
        assertThatThrownBy(() -> new Travessia(0, 2, Regra.SO_AS_MARGENS))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("bicho nenhum");
        assertThatThrownBy(() -> new Travessia(3, 0, Regra.SO_AS_MARGENS))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("pelo menos um");
    }
}
