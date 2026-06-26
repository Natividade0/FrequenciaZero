# Pack de Arte e Áudio — Prólogo

Este arquivo define o pacote visual e sonoro do prólogo de FREQUÊNCIA ZERO.

## Objetivo

O prólogo precisa parecer um jogo mobile de suspense, não um app comum. Como o projeto ainda deve compilar no AndroidIDE sem assets binários obrigatórios, a primeira entrega usa Canvas customizado e ToneGenerator em camadas.

## Arte implementada em Canvas

### TitleForestView

Uso: tela inicial `FREQUÊNCIA ZERO`.

Elementos visuais:

- céu escuro cinematográfico;
- chuva animada;
- estrada molhada com reflexo;
- floresta em camadas;
- colinas ao fundo;
- antena distante com luz vermelha pulsando;
- névoa em movimento;
- vinheta escura.

Função emocional:

> Fazer o jogador sentir que está entrando em uma cidade isolada e perigosa.

### LockScreenView

Uso: tela bloqueada e textura de fundo do chat.

Elementos visuais:

- wallpaper escuro com antena e floresta;
- chuva fina;
- brilho de vidro de celular;
- scanline suave;
- vinheta para foco no centro.

Função emocional:

> Fazer a tela parecer um celular encontrado dentro do jogo.

### CorruptedFileArtView

Uso: preview do arquivo `VX_0317_A.raw`.

Elementos visuais:

- torre de transmissão;
- floresta;
- ruído digital;
- blocos de glitch;
- moldura de mídia corrompida;
- luz vermelha instável.

Função emocional:

> Fazer o arquivo recebido parecer perigoso e importante.

### MiniWaveView

Uso: waveform do arquivo.

Elementos visuais:

- waveform com duas camadas;
- linha central;
- partículas de sinal;
- mudança de energia quando o áudio é reproduzido.

Função emocional:

> Dar sensação de áudio vivo, não apenas desenho estático.

## Áudio implementado

### AudioEngine

Uso atual: feedback sonoro sem arquivos externos.

Sons:

- toque curto de interface;
- notificação de mensagem com cauda;
- glitch em três tons;
- tom grave para impacto em `...voltou...`.

Limitação:

Ainda não há arquivos `.ogg` reais em `res/raw`. Esta entrega melhora o som usando `ToneGenerator`, mas o próximo passo ideal é adicionar sons reais.

## Assets reais recomendados depois

Quando for adicionar arte real, usar nomes fixos:

```text
app/src/main/res/drawable-nodpi/bg_title_forest.webp
app/src/main/res/drawable-nodpi/bg_lock_forest.webp
app/src/main/res/drawable-nodpi/avatar_helena.webp
app/src/main/res/drawable-nodpi/thumb_vx0317a_corrupted.webp
```

Quando for adicionar áudio real:

```text
app/src/main/res/raw/sfx_tap.ogg
app/src/main/res/raw/sfx_message.ogg
app/src/main/res/raw/sfx_glitch.ogg
app/src/main/res/raw/sfx_low_reveal.ogg
app/src/main/res/raw/amb_prologue_rain.ogg
```

## Critério visual

A arte deve passar no teste:

> Ao abrir o jogo, a primeira reação precisa ser: isso parece um suspense mobile.

Se parecer menu, dashboard, sistema técnico ou app tutorial, deve ser refeito.
