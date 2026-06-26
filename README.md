# FrequenciaZero

Projeto Android nativo em Java e XML.

## Prólogo jogável

Esta branch entrega somente os primeiros 90 segundos jogáveis de FREQUÊNCIA ZERO. O foco é provar o game feel: uma abertura cinematográfica, um celular bloqueado dentro da ficção, uma mensagem estranha de HELENA, um arquivo recebido e um gancho final.

Não é um hub completo. Não há mapa explorável, configurações completas, dashboard técnico, lista de transmissões ou minigame de frequência neste PR.

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
git checkout rebuild/prologue-game-feel
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

- Hub ECO-0 completo
- Mapa explorável
- Configurações completas
- Log técnico
- Lista de transmissões
- Mecânica de frequência/espectrograma
- Capítulo 1 completo

## Próximos passos

- Testar em aparelho pequeno e ajustar espaçamentos.
- Adicionar variações de resposta de HELENA baseadas na escolha salva.
- Criar a primeira mecânica de restauração no Capítulo 1.
- Expandir Vértice e o arquivo `VX_0317_A.raw` sem quebrar o ritmo cinematográfico.
