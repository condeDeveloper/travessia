package br.com.conde.travessia;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A travessia do rio, varrida inteira.
 *
 * <p>Sao {@code quantos} ovelhas e {@code quantos} lobos numa margem, um barco
 * que leva de uma ate {@code capacidade} bichos e nunca atravessa vazio, e a
 * regra de que os lobos nao podem superar as ovelhas onde houver ovelha.
 *
 * <p>O espaco de situacoes e pequeno e finito, entao nada aqui e tentativa: a
 * busca em largura visita tudo que da para alcancar e devolve o menor numero de
 * viagens, ou a informacao de que nao existe travessia nenhuma. "Nao consegui"
 * e "nao da" sao respostas diferentes, e so a varredura completa separa as duas.
 */
public final class Travessia {

    private final int quantos;
    private final int capacidade;
    private final Regra regra;
    private final Map<Estado, Integer> viagensAte = new HashMap<>();
    private final List<Estado> ordemDeDescoberta = new ArrayList<>();

    public Travessia(int quantos, int capacidade, Regra regra) {
        if (quantos < 1) {
            throw new IllegalArgumentException("sem bicho nenhum nao ha travessia");
        }
        if (capacidade < 1) {
            throw new IllegalArgumentException("o barco precisa levar pelo menos um");
        }
        this.quantos = quantos;
        this.capacidade = capacidade;
        this.regra = regra;

        Estado comeco = Estado.comecoDe(quantos);
        viagensAte.put(comeco, 0);
        ordemDeDescoberta.add(comeco);
        Deque<Estado> fila = new ArrayDeque<>();
        fila.add(comeco);

        while (!fila.isEmpty()) {
            Estado atual = fila.removeFirst();
            for (Estado proximo : saidasDe(atual)) {
                if (!viagensAte.containsKey(proximo)) {
                    viagensAte.put(proximo, viagensAte.get(atual) + 1);
                    ordemDeDescoberta.add(proximo);
                    fila.addLast(proximo);
                }
            }
        }
    }

    public int quantos() {
        return quantos;
    }

    public int capacidade() {
        return capacidade;
    }

    public Regra regra() {
        return regra;
    }

    public boolean existe() {
        return viagensAte.containsKey(Estado.fimDe(quantos));
    }

    /** O menor numero de viagens, ou -1 se nao houver travessia. */
    public int viagens() {
        return viagensAte.getOrDefault(Estado.fimDe(quantos), -1);
    }

    /** Quantas situacoes a travessia consegue alcancar partindo do comeco. */
    public int estadosAlcancados() {
        return viagensAte.size();
    }

    /** Quantas situacoes sao seguras, alcancaveis ou nao. */
    public int estadosSeguros() {
        int seguros = 0;
        for (int ovelhas = 0; ovelhas <= quantos; ovelhas++) {
            for (int lobos = 0; lobos <= quantos; lobos++) {
                for (boolean lado : new boolean[]{true, false}) {
                    if (regra.margensEmPaz(new Estado(ovelhas, lobos, lado), quantos)) {
                        seguros++;
                    }
                }
            }
        }
        return seguros;
    }

    /** As situacoes seguras que nenhuma sequencia de viagens alcanca. */
    public List<Estado> segurosInalcancaveis() {
        List<Estado> saida = new ArrayList<>();
        for (int ovelhas = 0; ovelhas <= quantos; ovelhas++) {
            for (int lobos = 0; lobos <= quantos; lobos++) {
                for (boolean lado : new boolean[]{true, false}) {
                    Estado estado = new Estado(ovelhas, lobos, lado);
                    if (regra.margensEmPaz(estado, quantos) && !viagensAte.containsKey(estado)) {
                        saida.add(estado);
                    }
                }
            }
        }
        return saida;
    }

    /** Situacoes alcancaveis de onde nao se sai para lugar nenhum novo nem para o fim. */
    public List<Estado> becosSemSaida() {
        List<Estado> saida = new ArrayList<>();
        for (Estado estado : ordemDeDescoberta) {
            if (!estado.acabou() && saidasDe(estado).isEmpty()) {
                saida.add(estado);
            }
        }
        return saida;
    }

    /** O caminho mais curto do comeco ao fim, viagem por viagem. */
    public List<Estado> caminho() {
        if (!existe()) {
            return List.of();
        }
        List<Estado> deTras = new ArrayList<>();
        Estado atual = Estado.fimDe(quantos);
        deTras.add(atual);
        while (viagensAte.get(atual) > 0) {
            int faltam = viagensAte.get(atual) - 1;
            Estado anterior = null;
            for (Estado candidato : ordemDeDescoberta) {
                if (viagensAte.get(candidato) == faltam && saidasDe(candidato).contains(atual)) {
                    anterior = candidato;
                    break;
                }
            }
            if (anterior == null) {
                throw new IllegalStateException("o caminho de volta se perdeu em " + atual);
            }
            deTras.add(anterior);
            atual = anterior;
        }
        java.util.Collections.reverse(deTras);
        return deTras;
    }

    /** As situacoes a que uma viagem leva, ja filtradas pela regra. */
    public List<Estado> saidasDe(Estado estado) {
        List<Estado> saida = new ArrayList<>();
        int ovelhasNoLado = estado.barcoNaPartida() ? estado.ovelhas() : quantos - estado.ovelhas();
        int lobosNoLado = estado.barcoNaPartida() ? estado.lobos() : quantos - estado.lobos();

        for (int ovelhas = 0; ovelhas <= Math.min(ovelhasNoLado, capacidade); ovelhas++) {
            for (int lobos = 0; lobos <= Math.min(lobosNoLado, capacidade); lobos++) {
                int carga = ovelhas + lobos;
                if (carga < 1 || carga > capacidade || !regra.cargaEmPaz(ovelhas, lobos)) {
                    continue;
                }
                int sinal = estado.barcoNaPartida() ? -1 : 1;
                Estado proximo = new Estado(estado.ovelhas() + sinal * ovelhas,
                        estado.lobos() + sinal * lobos, !estado.barcoNaPartida());
                if (regra.margensEmPaz(proximo, quantos)) {
                    saida.add(proximo);
                }
            }
        }
        return saida;
    }
}
