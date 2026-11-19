# Territory Predictive Insights — UBS

Global Solution 2º/2026 · Programação Orientada a Objetos (Java) · Tema CitizenTech · FIAP

Plataforma de monitoramento territorial para uma **UBS (Unidade Básica de Saúde)**. O sistema lê sensores simulados de **atendimentos, fila e estoque de medicamentos**, gera previsões simples (média móvel e comparação de períodos) e emite **alertas em três níveis** (ATENÇÃO, ALERTA, CRÍTICO), sempre explicando o porquê.

> Recorte escolhido: **demanda por serviços** (picos de procura em uma UBS).

---

## Integrantes

| Nome | RM | Turma |
|---|---|---|
| Raul Bridi Albano | 567772 | CCPB |
| Ernandes da Silva Jesus | 566611 | CCPB |
| Leandro Filippini Aguiar Alves | 568157 | CCPB |

---

## Público beneficiado

- **Quem sofre o problema:** moradores atendidos pela UBS, que enfrentam filas longas e risco de falta de medicamentos nos dias de pico de procura.
- **Quem decide:** a coordenação e a equipe da UBS.
- **Decisão que o sistema apoia:** o que fazer *hoje* diante da demanda prevista, como reforçar o atendimento na recepção, remanejar profissionais ou antecipar a reposição de estoque. O sistema indica o risco e a razão; a decisão continua sendo das pessoas.

---

## Estrutura do projeto

```
src/br/com/territorypredictive/
├── model/      ComponenteTerritorial (abstrata), PainelInformativoUBS, DadosTerritorio
├── sensor/     Sensor (interface), SensorAtendimentos, SensorFila, SensorEstoque
├── previsao/   EstrategiaPrevisao (abstrata), PrevisaoMediaMovel, PrevisaoComparacao
├── alerta/     NivelAlerta (enum), Alerta, AvaliadorAlertas
└── main/       SistemaMonitoramento (menu e loop principal)
```

| Conceito de POO | Onde está |
|---|---|
| Classe abstrata | `ComponenteTerritorial` (método abstrato `verificarStatus()`), concreta: `PainelInformativoUBS` |
| Interface | `Sensor`, implementada por `SensorAtendimentos`, `SensorFila` e `SensorEstoque` |
| Encapsulamento | `DadosTerritorio`: atributos privados, setters com validação, limiares protegidos por código de acesso |
| Herança | `EstrategiaPrevisao` → `PrevisaoMediaMovel` e `PrevisaoComparacao` (`prever()` sobrescrito, `super()` nos construtores) |
| Alertas | `AvaliadorAlertas` compara previsões e leituras com os limiares e devolve um `Alerta` com o nível e a explicação |

Observação: os arquivos estão organizados em **pacotes**, seguindo o modelo usado em aula, e não soltos em uma única pasta.

---

## Como compilar e rodar

**Requisito:** Java 11 ou superior (testado com JDK 21). Não há dependências externas.

### Pelo terminal (Windows, CMD, na pasta raiz do projeto)

```
chcp 65001
dir /s /b src\*.java > fontes.txt
javac -encoding UTF-8 -d bin @fontes.txt
java -cp bin br.com.territorypredictive.main.SistemaMonitoramento
```

O `chcp 65001` serve para os acentos aparecerem corretamente no CMD.

### Pelo terminal (Linux ou macOS)

```
find src -name "*.java" > fontes.txt
javac -encoding UTF-8 -d bin @fontes.txt
java -cp bin br.com.territorypredictive.main.SistemaMonitoramento
```

### Pelo Eclipse

1. *File > Import > Existing Projects into Workspace* e selecionar a pasta do projeto.
2. Se o Eclipse reclamar do JRE: *Project > Properties > Java Build Path > Libraries* e trocar o *JRE System Library* por um JDK 11 ou superior instalado.
3. Botão direito em `SistemaMonitoramento.java` > *Run As > Java Application*.

---

## Usando o sistema

Ao iniciar, o sistema carrega 6 leituras de cada sensor. Menu:

| Opção | O que faz |
|---|---|
| 1 | Verifica os sensores: faz uma nova leitura e mostra o nível de cada uma |
| 2 | Roda as duas previsões e emite os alertas, explicando cada um |
| 3 | Gerencia os dados do território (população, região, limiares) |
| 4 | Simula as próximas leituras (1 a 100 por sensor) e já roda previsões e alertas |
| 5 | Exibe o status completo (painel, dados, sensores e histórico) |
| 6 | Simula falha ou reparo de um sensor |
| 7 | Simula 24 horas (**bônus**): uma leitura por hora de cada sensor, mostrando os alertas ao longo do dia |
| 0 | Sai |

**Código de acesso de demonstração** para alterar limiares: `ubs2026`.

### Roteiro para testar

1. **Opção 2** logo no início: as leituras são poucas, então os alertas aparecem como NORMAL.
2. **Opção 4** com 30 leituras: as leituras tendem a subir (simulam o pico de procura), e aparecem ATENÇÃO e ALERTA. Com 80 ou mais, vários sensores chegam a CRÍTICO.
3. **Opção 6**, falha na Fila, e depois a **opção 1**: o sensor com falha não gera leitura e não entra no histórico.
4. **Opção 3 > 4** com código errado: acesso negado e limiares mantidos. Com o código certo e limiares fora de ordem (ex.: 50, 40, 80), a alteração é rejeitada com mensagem.
5. **Opção 3 > 2** com população negativa: rejeitada, e o valor anterior é mantido.
6. **Opção 7** (bônus): mostra as 24 horas com o nível de cada sensor hora a hora, detalha o motivo na primeira vez em que um sensor sobe de nível e termina com um resumo. Por usar leituras aleatórias, o resultado muda a cada execução.

---

## Alertas

Cada leitura ou previsão é comparada com os limiares do sensor:

| Nível | Quando |
|---|---|
| NORMAL | valor abaixo do limiar de ATENÇÃO |
| ATENÇÃO | valor ≥ limiar de atenção |
| ALERTA | valor ≥ limiar de alerta |
| CRÍTICO | valor ≥ limiar crítico |

Limiares iniciais (podem ser alterados com o código de acesso):

| Sensor | Atenção | Alerta | Crítico |
|---|---|---|---|
| Atendimentos (atendimentos/hora) | 50 | 80 | 110 |
| Fila (pessoas) | 30 | 60 | 100 |
| Estoque (% consumido do estoque semanal) | 60 | 80 | 95 |

Toda mensagem de alerta informa **qual sensor**, **qual estratégia** gerou o valor, **qual valor** e **qual limite** foi atingido. Exemplo:

```
(!!) [ALERTA] Fila (Média Móvel): 62,0 pessoas na fila - atingiu o limite de ALERTA (60,0).
```

---

## Decisões da equipe

- **Recorte UBS**, com três sensores coerentes com a demanda por serviços: atendimentos, fila e estoque.
- **Estoque medido como "% consumido"**, e não "% restante". Assim os três sensores alertam quando o valor está **alto**, e a lógica de alertas fica única.
- **Leituras simuladas com passeio aleatório** (cada leitura varia em torno da anterior, com leve tendência de alta), em vez de números soltos, para as previsões terem uma curva realista.
- **Mínimo de leituras:** a média móvel usa 5 leituras e a comparação precisa de 6 (dois períodos de 3). Abaixo disso o sistema **não prevê** e diz o motivo.
- **Sensor com falha não entra no histórico:** a leitura inválida (-1) é descartada antes de chegar às previsões.
- **Limiares ligados ao `getTipo()` do sensor** ("Atendimentos", "Fila", "Estoque"), para o `DadosTerritorio` não depender do pacote `sensor`.
- **Só os limiares exigem código de acesso**, como pede o enunciado. População e região são protegidas por validação.
- **Bônus escolhido: simulação de 24 horas** (opção 7 do menu). Em cada hora vale o nível mais grave entre as duas previsões, para o alerta não depender de uma estratégia só.
- **Nenhum dado pessoal:** o território é descrito só por região e equipamento.

---

## Reflexão crítica

**Quando o sistema deve se calar?** Previsão com poucos dados engana. Por isso a média móvel só roda com pelo menos 5 leituras e a comparação de períodos com pelo menos 6. Abaixo disso, o sistema avisa que não há leituras suficientes em vez de mostrar um número. Pelo mesmo motivo, leituras de um sensor com falha são descartadas: um valor errado dentro da média produziria um alerta falso, e um alerta falso faz a equipe deixar de confiar nos verdadeiros.

**O que os dados não representam?** Os valores são simulados e há um sensor por indicador para a UBS inteira. Uma fila de 60 pessoas não diz quanto cada uma espera, nem quem é urgência e quem é consulta de rotina. O estoque em "%" não diz qual medicamento está acabando. Também não aparecem surtos, campanhas de vacinação, feriados ou quem desistiu de procurar a UBS por causa da fila. A previsão por média e comparação não enxerga sazonalidade: serve para indicar tendência de curto prazo, não para prever a demanda real.

**Por que o alerta apoia a decisão da pessoa, em vez de decidir por ela?** O sistema conhece números, mas não conhece o contexto da UBS: quem está de plantão, se há uma campanha marcada, se a fila é de um dia atípico. Por isso cada alerta mostra o sensor, o valor e o limite atingido: a equipe pode checar o raciocínio e discordar. Quem decide reforçar a recepção, remanejar profissionais ou antecipar a reposição é a coordenação. O limiar também é ajustável, mas só com o código de acesso, para que mudar o critério seja uma decisão consciente e não um acidente.

---

## Divisão de tarefas

| Integrante | Responsabilidade | Arquivos |
|---|---|---|
| Raul | Interface `Sensor` e os três sensores | `sensor/*` |
| Leandro | Estratégias de previsão (média móvel e comparação de períodos) | `previsao/*` |
| Ernandes | Estruturação do projeto (pacotes e repositório), classe abstrata, painel e dados do território | `model/*` |
| Ernandes | Menu principal e alertas (todos os integrantes entendem o funcionamento) | `main/*`, `alerta/*` |

O histórico completo de commits de cada integrante está no repositório.

---

## Uso de IA

**Onde usamos:**

- Gemini: mentoria no início do projeto, com um prompt de aprendizado que guiou o enunciado passo a passo.
- Claude: tirar dúvidas e explicar conceitos, revisão do código que escrevemos, fornecer ajuda no pacote `alerta`, além de ajuda na criação final do menu (SistemaMonitoramento) e montar o rascunho do README.

**Como revisamos:** compilamos e executamos o projeto inteiro, testamos pelo menu cada validação e cada nível de alerta, e cada integrante revisou a classe da sua parte. Cada um consegue explicar o que fez: Raul os sensores, Leandro as previsões e Ernandes a estrutura do projeto e os dados do território. Todos conseguem explicar o funcionamento geral do menu principal.

---

## Limitações e próximos passos

- Leituras simuladas com `Math.random()`: o sistema não usa dados reais de uma UBS.
- As leituras ficam só em memória e se perdem ao fechar o programa.
- **Bônus implementado:** simulação de 24 horas, na opção 7 do menu (um bônus, como pede o enunciado). Ela continua a partir do histórico e do estado atual dos sensores, e ao final atualiza o painel da recepção.
- Ideia para continuar: salvar o histórico de leituras em arquivo e recarregá-lo ao iniciar.
