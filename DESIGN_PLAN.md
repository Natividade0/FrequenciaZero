# DESIGN_PLAN.md

## Inspiração de UX

A referência de aplicativos de investigação por mensagens orienta o ritmo: o jogador abre o app e encontra uma conversa que parece viva, íntima e urgente. A inspiração está no uso de mensagens curtas, arquivos recebidos, logs automáticos e uma sensação de que o telefone faz parte da ficção. O objetivo é criar a mesma tensão de interface cotidiana contaminada por mistério.

## O que será diferente

Frequência Zero terá identidade própria. A interface não copia Duskwood, não usa seus padrões visuais e não depende de escolhas narrativas A/B/C como eixo central. A assinatura será técnica, analógica e ritualística: transmissões, espectrogramas, frequência 03.17, logs de rádio, zonas mortas e uma paleta escura com âmbar queimado, verde mineral e vermelho de emergência. A conversa existe, mas a mecânica principal é restaurar sinais.

## Arquitetura

- Java como linguagem única.
- XML para todos os layouts.
- ViewBinding habilitado no Gradle.
- SharedPreferences para progresso, variáveis ocultas, log técnico, frequência e capturas.
- RecyclerView para conversa, transmissões e log.
- Material Design 3 para navegação, botões, chips e superfícies.
- MediaPlayer para feedback sonoro de interface, com geração complementar de ruído quando necessário.
- Views customizadas em Canvas apenas onde a interface precisa parecer instrumento, como o espectrograma.

## Fluxo

1. Boot silencioso registra o início do app no log.
2. A primeira tela abre em Mensagens, já dentro da conversa com HELENA.
3. HELENA envia a sequência inicial sobre Elias, Vértice e o protocolo.
4. O jogador escolhe uma das respostas textuais: Confirmado, Que frequência?, Por que eu esqueceria?
5. A resposta altera pequenas variáveis ocultas, sem revelar números ao jogador.
6. A aba Transmissões mostra VX_0317_A.raw com origem, duração e status.
7. Ao abrir a transmissão, o jogador entra em Restauração.
8. Na restauração, slider e espectrograma substituem escolhas de roteiro.
9. O botão Capturar Amostra só habilita perto de 03.17.
10. Ao capturar, surge o fragmento "...você voltou..." e o log registra a amostra.

## Telas

### Mensagens

Interface inspirada em apps modernos de mensagens, mas com identidade investigativa. Bolhas discretas, remetente HELENA, timestamp técnico, respostas como chips pequenos e não como botões gigantes. A conversa deve parecer uma comunicação interceptada, não tutorial.

### Transmissões

Lista de arquivos em RecyclerView. Cada item mostra nome, origem, duração e status. VX_0317_A.raw é a primeira peça jogável. A lista deve parecer uma caixa de evidências compacta, com metadados claros e visual publicável.

### Restauração

Tela principal de mecânica. Deve parecer uma ferramenta técnica real: espectrograma, ruído, indicador de frequência, slider e botão Capturar Amostra. Sem escolhas A/B/C. A tensão vem do ajuste fino e do som, não de explicar regras.

### Log

Registro automático em formato técnico: boot, mensagens, áudios, transmissões, frequências e capturas. Deve reforçar que o aplicativo está observando tudo, com entradas concisas e datadas.

### Mapa

Ainda não explorável. Mostra apenas Cidade Vértice, Zona Morta, Antena e Rádio Âncora como pontos bloqueados/monitorados. A tela precisa parecer parte do produto, não placeholder.

### Configurações

Som, resetar progresso e créditos. Simples, escura, com Material Design 3 e sem linguagem escolar.

## Experiência

A primeira abertura deve parecer um thriller profissional rodando dentro do celular. A interface deve ser contida, densa, imersiva e sem aparência de protótipo. Onde houver dúvida entre comum e imersivo, a escolha será sempre pelo imersivo: textos curtos, microinterações, som discreto, logs automáticos e telas que parecem ferramentas de investigação já em uso.
