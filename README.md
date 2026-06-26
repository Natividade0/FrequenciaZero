# FrequenciaZero

Projeto Android nativo em Java e XML.

## Prólogo jogável

Esta branch entrega somente os primeiros 90 segundos jogáveis de FREQUÊNCIA ZERO. O foco é provar o game feel: uma abertura cinematográfica, um celular bloqueado dentro da ficção, uma mensagem estranha de HELENA, um arquivo recebido e um gancho final.

Não é um hub completo. Não há mapa explorável, configurações completas, dashboard técnico, lista de transmissões ou minigame de frequência neste PR.

## Polimento direto de arte, motion e áudio

A branch `art/prologue-assets-motion-audio-2` melhora a base do prólogo sem depender do Codex e sem adicionar dependências pesadas.

O que foi reforçado:

- `TitleForestView`: capa mais cinematográfica com chuva, estrada molhada, floresta em camadas, névoa e antena com luz vermelha.
- `LockScreenView`: wallpaper interno mais parecido com celular encontrado dentro do jogo.
- `CorruptedFileArtView`: preview do arquivo com ruído, glitch, torre e moldura de mídia corrompida.
- `MiniWaveView`: waveform com mais vida, duas camadas e partículas de sinal.
- `AudioEngine`: sons em camadas com `ToneGenerator` para mensagem, toque, glitch e impacto grave.
- `ART_PACK_PROLOGUE.md`: documentação do pacote visual e sonoro do prólogo.

## Fluxo implementado

1. Tela inicial cinematográfica com floresta, estrada, antena, névoa e ruído em Canvas.
2. Toque para iniciar com fade/glitch curto.
3. Tela de celular bloqueado com horário 23:17, data 17 MAR, status sem serviço e notificação de HELENA.
4. Conversa com HELENA com mensagens chegando por tempo, indicador de digitação e respostas no rodapé.
5. Primeiro arquivo recebido: `VX_0317_A.raw`.
6. Preview do arquivo com arte corrompida, waveform animada e botão circular de ouvir.
7. Fragmento de áudio: `...você...` / `...voltou...`.
8. Retorno automático para conversa e gancho final.
9. Card `FIM DO PRÓLOGO` com `SALVAR PROGRESSO`.

## Como testar no AndroidIDE

```bash
cd /storage/emulated/0/AndroidIDEProjects/FrequenciaZero
git fetch --all
git reset --hard origin/art/prologue-assets-motion-audio-2
rm -rf .gradle app/build build
```

Depois, usar o botão Build/Run do AndroidIDE.

## Stack

- Android nativo
- Java
- XML
- Material Components
- SharedPreferences
- Canvas customizado
- ToneGenerator
- AndroidIDE
- compileSdk 34
- targetSdk 34
- minSdk 23
- AGP 8.1.4

## O que ainda não existe

- Assets reais em `.webp`
- Áudios reais em `.ogg`
- Hub ECO-0 completo
- Mapa explorável
- Configurações completas
- Log técnico
- Lista de transmissões
- Mecânica de frequência/espectrograma
- Capítulo 1 completo

## Próximos passos

- Testar em aparelho pequeno e ajustar espaçamentos.
- Adicionar assets reais em `drawable-nodpi` depois que o visual base for aprovado.
- Adicionar sons reais em `res/raw` depois que o fluxo do prólogo estiver validado.
- Adicionar variações de resposta de HELENA baseadas na escolha salva.
- Criar a primeira mecânica de restauração no Capítulo 1.
- Expandir Vértice e o arquivo `VX_0317_A.raw` sem quebrar o ritmo cinematográfico.
