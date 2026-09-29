# Plano de Correção: Radar Chart Dinâmico e Navegação de Pilares

## Causa Raiz Identificada
1. **Pilar não aparecendo no Radar:** O componente `RadarLabelsOverlay` em `RadarChart.kt` possuía índices rígidos de `0` a `6` (`pillars[0]` até `pillars[6]`). Como o novo pilar `PURPOSE` foi inserido na 8ª posição (`pillars[7]`), ele nunca era renderizado.
2. **Fechamento/Crash do App ao sair da configuração:** Ao desativar qualquer pilar na tela de configuração (restando menos de 7 pilares), a tela inicial (`DashboardScreen`) tentava recompor o radar chamando `pillars[6]`. Por ser uma lista menor que 7 elementos, ocorria um `IndexOutOfBoundsException` que causava o encerramento do app. Além disso, faltava o `BackHandler` explícito na tela de personalização para o botão voltar físico/gesto do Android.

---

## Solução Proposta

### 1. Dinamização Total do `RadarChart.kt`
- Substituir o overlay fixo `pillars[0]`..`pillars[6]` por um posicionamento verdadeiramente dinâmico e flexível baseado no ângulo polar de cada vértice (`angle = -Math.PI / 2 + (i * 2 * Math.PI / count)`).
- Calcular o alinhamento e deslocamento de cada badge para qualquer quantidade de pilares (de 2 a 8 pilares ativos simultaneamente).
- Todos os pilares ativos, incluindo **Propósito & Existencialismo** (`PURPOSE`), terão seus vértices, linhas e badges posicionados perfeitamente em volta do gráfico de radar.

### 2. Adição de `BackHandler` em `PillarsCustomizationScreen.kt`
- Incluir `BackHandler { onNavigateBack() }` para garantir suporte total ao gesto e botão físico de voltar do Android, mantendo a transição segura para o Dashboard.

### 3. Validação com Testes e Compilação
- Adicionar teste unitário validando a renderização do radar com 8, 7 e menos de 7 pilares.
- Executar `compile_applet` e testes unitários via Gradle para assegurar ausência de crashes e estabilidade absoluta.
