# travessia

A travessia do rio do zero em Java 21: `n` ovelhas e `n` lobos numa margem, um
barco que leva de uma até `b` cabeças e nunca atravessa vazio, e a regra de que
os lobos não podem estar em maior número que as ovelhas onde houver ovelha.

O espaço de situações é pequeno e finito, então aqui ninguém tenta: a busca em
largura visita tudo que dá para alcançar e devolve o menor número de viagens, ou
a informação de que **não existe travessia nenhuma**. "Não consegui" e "não dá"
são respostas diferentes, e só a varredura completa separa as duas.

```
mvn -B -ntp verify
mvn -B -ntp exec:java -Dexec.args="barcos"   # (ou viagens, estados)
```

## O barco decide quem atravessa

Viagens mínimas, ou um traço quando não existe travessia:

| pares | barco 1 | barco 2 | barco 3 | barco 4 | barco 5 | barco 6 |
|---:|---:|---:|---:|---:|---:|---:|
| 1 | - | 1 | 1 | 1 | 1 | 1 |
| 2 | - | 5 | 3 | 1 | 1 | 1 |
| **3** | - | **11** | 5 | 3 | 3 | 1 |
| 4 | - | **-** | 9 | 5 | 3 | 3 |
| 5 | - | - | 11 | 7 | 5 | 3 |
| 6 | - | - | **-** | 9 | 7 | 5 |
| 7 | - | - | - | 11 | 7 | 5 |
| 8 | - | - | - | 13 | 9 | 7 |
| 9 | - | - | - | 15 | 11 | 7 |
| 10 | - | - | - | 17 | 13 | 9 |
| 11 | - | - | - | 19 | 15 | 9 |
| 12 | - | - | - | 21 | 17 | 11 |

O caso que todo mundo conhece é a linha de três pares com barco de dois, e as
**11 viagens** dele. A linha debaixo é a parte que a brincadeira não conta: com
quatro pares e o mesmo barco, não existe solução. Não é difícil, não é longa:
não existe.

```
barco 1: nenhum grupo atravessa
barco 2: até 3 pares
barco 3: até 5 pares
barco 4: todos os 12 testados
barco 5: todos os 12 testados
barco 6: todos os 12 testados
```

O barco de uma cabeça é o caso mais curioso: ele não atravessa **nem um par
sozinho**. Quem vai precisa voltar para buscar o outro, e aí está de volta ao
começo.

## As onze viagens

```
 0  partida 3/3   chegada 0/0   barco na partida
 1  partida 3/1   chegada 0/2   barco na chegada
 2  partida 3/2   chegada 0/1   barco na partida
 3  partida 3/0   chegada 0/3   barco na chegada
 4  partida 3/1   chegada 0/2   barco na partida
 5  partida 1/1   chegada 2/2   barco na chegada
 6  partida 2/2   chegada 1/1   barco na partida
 7  partida 0/2   chegada 3/1   barco na chegada
 8  partida 0/3   chegada 3/0   barco na partida
 9  partida 0/1   chegada 3/2   barco na chegada
10  partida 0/2   chegada 3/1   barco na partida
11  partida 0/0   chegada 3/3   barco na chegada
```

Nos passos 5 e 6 o barco leva uma ovelha e um lobo e traz uma ovelha e um lobo
de volta. Parece desperdício e não é: é a única maneira de trocar a composição
das duas margens sem deixar ninguém em desvantagem.

## Quanto custa cada par a mais

| barco | viagens, de 1 até 12 pares |
|---:|---|
| 2 | 1 5 11 - - - - - - - - - |
| 3 | 1 3 5 9 11 - - - - - - - |
| **4** | **1 1 3 5 7 9 11 13 15 17 19 21** |
| 5 | 1 1 3 3 5 7 7 9 11 13 15 17 |
| **6** | **1 1 1 3 3 5 5 7 7 9 9 11** |

Com barco de quatro, cada par a mais custa exatamente duas viagens, de ponta a
ponta: `2n − 3`. Com barco de seis, não: a linha anda aos pares, e **seis dos
onze pares a mais saem de graça**. Acrescentar o sétimo par a um grupo de seis
não custa viagem nenhuma, porque a viagem que já existia tinha lugar sobrando.

## Quando não dá, o problema encolhe

| pares | barco | situações possíveis | seguras | alcançadas | inalcançáveis | travessia |
|---:|---:|---:|---:|---:|---:|---|
| 1 | 2 | 8 | 8 | 6 | 2 | existe |
| 2 | 2 | 18 | 14 | 12 | 2 | existe |
| **3** | 2 | 32 | 20 | **16** | 4 | existe |
| **4** | 2 | 50 | 26 | **11** | 15 | **não dá** |
| 5 | 2 | 72 | 32 | 13 | 19 | não dá |
| 8 | 2 | 162 | 50 | 19 | 31 | não dá |
| 5 | 3 | 72 | 32 | 28 | 4 | existe |
| 6 | 3 | 98 | 38 | 17 | 21 | não dá |

A coluna das seguras cresce com o grupo, como se espera. A das alcançadas não:
de três para quatro pares, com barco de dois, ela **cai de 16 para 11**. O grupo
ficou maior e o problema ficou menor, porque a partir do quarto par a travessia
tranca cedo e a maior parte das situações seguras passa a ser inatingível.

E não há beco sem saída em lugar nenhum: nas 48 combinações medidas, **zero**
situações alcançáveis de onde não se pode sair. O barco acabou de chegar com
alguém, e esse alguém sempre pode voltar. O que impede a travessia não é ficar
preso: é andar em círculo dentro de uma parte pequena demais do problema.

## A omissão do enunciado que não muda nada

O enunciado diz que os lobos não podem superar as ovelhas, e não diz se isso
vale também **dentro do barco**. As duas leituras estão implementadas:

| regra | respostas iguais | viagens permitidas |
|---|---:|---:|
| só as margens | 72 de 72 | 7.774 |
| as margens e o barco | 72 de 72 | 7.774 |

Eu esperava que a leitura exigente cortasse caminhos e mudasse algum limite. Ela
não corta: nas 72 combinações de grupo e barco, as duas permitem exatamente as
mesmas 7.774 viagens. O motivo aparece quando se olha uma viagem qualquer: a
carga sai de uma margem que estava em paz e chega numa margem que precisa ficar
em paz, e essas duas exigências já cobrem o que o barco carrega.

## O que as medidas me corrigiram

1. **"Com barco de uma cabeça pelo menos um par atravessa."** Nenhum grupo
   atravessa, nem o de um par. Quem vai tem que voltar para buscar o outro.
2. **"Se não deu com quatro pares, faltou achar o caminho."** Das 26 situações
   seguras desse caso, só 11 são alcançáveis, e nenhuma delas é o fim.
3. **"Grupo maior, problema maior."** Com barco de dois, o grupo de quatro pares
   tem menos situações alcançáveis que o de três: 11 contra 16.
4. **"Cada par a mais custa duas viagens."** Só com barco de quatro. Com barco
   de seis, seis dos onze pares a mais não custam viagem nenhuma.
5. **"O enunciado precisa dizer se a regra vale dentro do barco."** Nas 72
   combinações medidas, a leitura exigente não proíbe uma única viagem das
   7.774 que a frouxa permite.

## Como está organizado

- `Estado`: quem ficou na margem de partida e de que lado está o barco.
- `Regra`: as duas leituras do enunciado, e o que conta como situação segura.
- `Travessia`: a busca em largura completa, de onde saem as viagens mínimas, as
  situações alcançáveis, as seguras inatingíveis e o caminho mais curto.
- `Medidor`: as três tabelas acima, rodando no CI nos três sistemas.

77 testes.
