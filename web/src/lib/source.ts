import { docs } from 'fumadocs-mdx:collections/server';
import { type InferPageType, loader } from 'fumadocs-core/source';
import { createElement } from 'react';
import type { IconType } from 'react-icons';
import {
  FiBookOpen,
  FiCode,
  FiCompass,
  FiDownloadCloud,
  FiExternalLink,
  FiGrid,
  FiHelpCircle,
  FiLayers,
  FiLifeBuoy,
  FiMousePointer,
  FiSettings,
  FiShield,
  FiShoppingBag,
} from 'react-icons/fi';

const iconMap: Record<string, IconType> = {
  FiBookOpen,
  FiCode,
  FiCompass,
  FiDownloadCloud,
  FiExternalLink,
  FiGrid,
  FiHelpCircle,
  FiLayers,
  FiLifeBuoy,
  FiMousePointer,
  FiSettings,
  FiShield,
  FiShoppingBag,
};

// See https://fumadocs.dev/docs/headless/source-api for more info
export const source = loader({
  baseUrl: '/docs',
  source: docs.toFumadocsSource(),
  icon: (icon) => {
    if (!icon) return undefined;
    const Icon = iconMap[icon];
    return Icon ? createElement(Icon, { className: 'size-4' }) : undefined;
  },
});

export function getPageImage(page: InferPageType<typeof source>) {
  const segments = [...page.slugs, 'image.png'];

  return {
    segments,
    url: `/og/docs/${segments.join('/')}`,
  };
}

export async function getLLMText(page: InferPageType<typeof source>) {
  const processed = await page.data.getText('processed');

  return `# ${page.data.title}

${processed}`;
}
