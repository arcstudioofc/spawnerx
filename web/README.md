# SpawnerX Web

Site e documentação oficial do SpawnerX, construídos com Next.js + Fumadocs.

## Requisitos

- Node.js >= 24
- pnpm >= 10.9.0

## Rodando localmente

```bash
pnpm install
pnpm -C web dev
```

Abra `http://localhost:3000` no navegador.

## Estrutura rápida

- `web/content/docs` — páginas MDX da documentação.
- `web/content/docs/meta.json` — ordem do menu e categorias.
- `web/public/images` — imagens usadas nos docs.
- `web/public/videos` — vídeos usados nos docs.
- `web/src/lib/source.ts` — loader do Fumadocs + mapeamento de ícones.
- `web/src/app/og/docs/[...slug]/route.tsx` — Open Graph dinâmico das páginas de docs.
- `web/src/app/opengraph-image.tsx` — Open Graph padrão do site.

## Ícones no menu

Cada página MDX pode definir `icon` no frontmatter, por exemplo:

```mdx
---
title: Instalação
description: Instalação completa, primeira execução e verificação do SpawnerX.
icon: FiDownloadCloud
---
```

Os ícones são resolvidos via `react-icons` no `web/src/lib/source.ts`.
