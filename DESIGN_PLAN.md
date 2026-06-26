# DESIGN_PLAN.md

## Inspiração de UX

A referência de investigação por mensagens orienta o ritmo: o jogador toca no app e sente que entrou em um telefone comprometido, com conversas urgentes, arquivos interceptados, registros técnicos e sinais que parecem reagir ao toque. A inspiração está na imersão de uma interface cotidiana contaminada por suspense, não em copiar telas, personagens, paleta ou estrutura de outro jogo.

## O que será diferente

FREQUÊNCIA ZERO tem identidade própria: rádio, espectrograma, frequência 03.17, Vértice, zona morta, antena e logs de recuperação. A conversa com HELENA é o gatilho narrativo, mas a mecânica principal é restaurar transmissões. A paleta evita o azul padrão e usa preto profundo, verde mineral, âmbar queimado e vermelho de emergência. O resultado deve parecer um sistema de investigação cinematográfico, acessível e perturbador, não um app de chat simples.

## Arquitetura

- Java como linguagem única.
- XML para todos os layouts.
- ViewBinding habilitado no Gradle, mantendo compatibilidade com AndroidIDE.
- SharedPreferences para progresso, variáveis ocultas, frequência, captura e log técnico.
- RecyclerView para mensagens, transmissões e registro técnico.
- Material Components / Material Design 3 para superfícies, chips, switches e botões discretos.
- SeekBar nativo para sintonia, evitando dependência de Material Slider.
- ToneGenerator para feedback sonoro interno, sem arquivos externos.
- Views customizadas em Canvas para título, avatar, capa de arquivo, mapa, onda e espectrograma.

## Fluxo

1. O app abre em uma tela de título cinematográfica com floresta, antena e a marca FREQUÊNCIA ZERO.
2. Ao tocar, o jogador entra no ECO-0, um hub de recuperação de transmissões.
3. O hub mostra HELENA online, módulos de mensagens, arquivos, chamada e mapa, além da última transmissão.
4. A conversa inicial traz as mensagens de HELENA sobre Elias, Vértice, o arquivo bruto e o protocolo.
5. As respostas "Confirmado.", "Que frequência?", "Por que eu esqueceria?" e "Qual protocolo?" alteram variáveis ocultas.
6. Transmissões lista VX_0317_A.raw e outros arquivos danificados com origem, duração e status.
7. O detalhe de VX_0317_A.raw abre a ferramenta de restauração.
8. Na restauração, o jogador ajusta o SeekBar até 03.17, observando o espectrograma e ouvindo ruído/glitch.
9. Capturar Amostra só habilita perto de 03.17.
10. A captura revela "...você voltou..." e grava o evento no log.

## Telas

### Título

Primeira impressão cinematográfica: fundo escuro, antena, floresta, interferência e chamada para iniciar. Deve parecer capa jogável, não splash genérica.

### ECO-0

Hub diegético com status instável, cards compactos e última transmissão. Serve como tela inicial depois do título, mantendo a sensação de telefone investigativo.

### Mensagens

Interface inspirada em aplicativos modernos de mensagens, mas com identidade de FREQUÊNCIA ZERO. Bolhas discretas, HELENA como contato, respostas pequenas e sem botões gigantes de escolha.

### Transmissões

Lista de arquivos em RecyclerView. Cada item mostra nome, origem, duração e status. VX_0317_A.raw é a primeira peça jogável; os demais reforçam o acervo corrompido.

### Arquivo

Detalhe do arquivo com imagem Canvas própria, metadados e ação para abrir a ferramenta. A tela deve parecer evidência, não formulário administrativo.

### Restauração

Mecânica principal. Espectrograma, ruído, frequência, SeekBar e Capturar Amostra criam o momento jogável. Sem escolhas A/B/C.

### Amostra Capturada

Tela curta de impacto: onda sonora, mensagem parcial e o fragmento "...você voltou...".

### Log

Registro automático de boot, mensagens, transmissões, frequências, capturas, mapa e chamada. Tudo em linguagem técnica concisa.

### Mapa

Ainda não explorável. Mostra Cidade Vértice, Zona Morta, Antena e Rádio Âncora como pontos monitorados.

### Configurações

Som, progresso e créditos em uma tela escura e simples, mantendo o tom do produto.

### Chamada

Teaser visual com HELENA e a frase sobre Vértice. Funciona como promessa narrativa, não como sistema completo de ligação.

## Experiência

A primeira abertura deve fazer o jogador esquecer que está em um build de AndroidIDE. O app precisa soar, animar e responder como um thriller mobile real: visual escuro publicável, textos curtos, controles compreensíveis, microinterações e progressão até a primeira amostra restaurada. Sempre que houver dúvida entre uma interface comum e uma interface mais imersiva, a opção escolhida será a imersiva.
