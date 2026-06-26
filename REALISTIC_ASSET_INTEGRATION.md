# Integração de assets realistas do prólogo

Esta branch substitui a direção visual baseada em Canvas por uma direção baseada em imagens reais geradas para o jogo.

## Decisão

O Figma não é obrigatório para esta etapa.

O jogo deve usar imagens reais em `drawable-nodpi` como base visual e manter XML/Java apenas para interação, animação leve e fluxo.

## Assets aprovados

Nomes finais esperados no Android:

```text
app/src/main/res/drawable-nodpi/bg_title_main.webp
app/src/main/res/drawable-nodpi/bg_lockscreen.webp
app/src/main/res/drawable-nodpi/bg_chat_helena.webp
app/src/main/res/drawable-nodpi/bg_file_vx0317a.webp
```

## Como cada imagem entra no jogo

### bg_title_main.webp

Tela inicial cinematográfica.

Uso:
- fundo principal da abertura;
- já traz estrada, chuva, torre, logo e clima de produto real;
- o toque em qualquer lugar continua abrindo a lockscreen.

### bg_lockscreen.webp

Tela de celular bloqueado.

Uso:
- fundo principal do lockscreen;
- já traz horário, chuva, notificação da HELENA e atmosfera realista;
- a área da notificação vira um hotspot clicável transparente.

### bg_chat_helena.webp

Conversa com HELENA.

Uso:
- referência visual principal da conversa;
- chat deve seguir o mesmo estilo: bolhas escuras, avatar realista, fundo de torre, escolhas premium;
- enquanto a versão funcional completa não for refeita, pode ser usada como cena visual-base.

### bg_file_vx0317a.webp

Arquivo VX_0317_A.raw.

Uso:
- tela base do arquivo recebido;
- já traz thumbnail, waveform, status danificado e botão ouvir;
- o botão OUVIR vira hotspot clicável transparente.

## Regra de implementação

Não voltar para visual de Canvas como arte principal.

Canvas só pode ser usado para:

- chuva leve;
- glitch;
- ruído;
- pequenos efeitos sobre imagem.

A arte principal agora é imagem realista.

## Próximo passo técnico

1. Adicionar os arquivos `.webp` em `app/src/main/res/drawable-nodpi/`.
2. Atualizar `activity_main.xml` para usar `ImageView` full screen por cena.
3. Transformar botões visíveis em hotspots transparentes onde a imagem já contém UI.
4. Manter `AppActivity.java` controlando o fluxo do prólogo.
5. Depois, reconstruir a conversa dinâmica por cima do mesmo estilo visual.
