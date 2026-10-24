package br.com.conde.travessia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prende os numeros que estao escritos no README. Se uma conta mudar, um destes
 * testes quebra e o texto tem que ser reescrito junto.
 */
@DisplayName("Os numeros do README")
class MedidasTest {

    @ParameterizedTest(name = "{0} pares, barco {1}: {2} viagens")
    @CsvSource({
            " 1, 2,  1", " 2, 2,  5", " 3, 2, 11",
            " 1, 3,  1", " 2, 3,  3", " 3, 3,  5", " 4, 3,  9", " 5, 3, 11",
            " 3, 4,  3", " 6, 4,  9", "12, 4, 21",
            " 4, 5,  3", " 7, 5,  7", "12, 5, 17",
            " 6, 6,  5", " 7, 6,  5", "12, 6, 11",
    })
    @DisplayName("tabela BARCOS")
    void tabelaBarcos(int pares, int barco, int viagens) {
        assertThat(new Travessia(pares, barco, Regra.SO_AS_MARGENS).viagens()).isEqualTo(viagens);
    }

    @Test
    @DisplayName("com barco de seis, metade dos pares a mais sai de graca")
    void oBarcoDeSeisRepete() {
        int[] viagens = new int[13];
        for (int pares = 1; pares <= 12; pares++) {
            viagens[pares] = new Travessia(pares, 6, Regra.SO_AS_MARGENS).viagens();
        }

        assertThat(viagens).containsExactly(0, 1, 1, 1, 3, 3, 5, 5, 7, 7, 9, 9, 11);
        int deGraca = 0;
        for (int pares = 2; pares <= 12; pares++) {
            if (viagens[pares] == viagens[pares - 1]) {
                deGraca++;
            }
        }
        assertThat(deGraca).isEqualTo(6);
    }

    @Test
    @DisplayName("com barco de quatro, cada par a mais custa exatamente duas viagens")
    void oBarcoDeQuatroCresceDeDois() {
        for (int pares = 4; pares <= 12; pares++) {
            int aqui = new Travessia(pares, 4, Regra.SO_AS_MARGENS).viagens();
            int antes = new Travessia(pares - 1, 4, Regra.SO_AS_MARGENS).viagens();

            assertThat(aqui - antes).as("de %d para %d pares", pares - 1, pares).isEqualTo(2);
            assertThat(aqui).isEqualTo(2 * pares - 3);
        }
    }

    @ParameterizedTest(name = "{0} pares, barco {1}")
    @CsvSource({
            "1, 2,  8,  8,  6,  2",
            "2, 2, 18, 14, 12,  2",
            "3, 2, 32, 20, 16,  4",
            "4, 2, 50, 26, 11, 15",
            "8, 2,162, 50, 19, 31",
            "5, 3, 72, 32, 28,  4",
            "6, 3, 98, 38, 17, 21",
    })
    @DisplayName("tabela ESTADOS")
    void tabelaEstados(int pares, int barco, int possiveis, int seguras, int alcancadas,
                       int inalcancaveis) {
        Travessia travessia = new Travessia(pares, barco, Regra.SO_AS_MARGENS);

        assertThat(2 * (pares + 1) * (pares + 1)).isEqualTo(possiveis);
        assertThat(travessia.estadosSeguros()).isEqualTo(seguras);
        assertThat(travessia.estadosAlcancados()).isEqualTo(alcancadas);
        assertThat(travessia.segurosInalcancaveis()).hasSize(inalcancaveis);
    }

    @Test
    @DisplayName("as 7.774 viagens permitidas sao as mesmas nas duas leituras do enunciado")
    void asViagensPermitidas() {
        long solta = 0;
        long presa = 0;
        for (int pares = 1; pares <= 12; pares++) {
            for (int barco = 1; barco <= 6; barco++) {
                solta += Medidor.contaViagens(new Travessia(pares, barco, Regra.SO_AS_MARGENS));
                presa += Medidor.contaViagens(
                        new Travessia(pares, barco, Regra.AS_MARGENS_E_O_BARCO));
            }
        }

        assertThat(solta).isEqualTo(7774);
        assertThat(presa).isEqualTo(solta);
    }

    @Test
    @DisplayName("as tabelas rodam inteiras sem estourar")
    void asTabelasRodam() {
        Medidor.barcos();
        Medidor.viagens();
        Medidor.estados();
    }
}
