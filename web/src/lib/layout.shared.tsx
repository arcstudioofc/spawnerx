import type { BaseLayoutProps } from 'fumadocs-ui/layouts/shared';

const navTitle = 'SpawnerX';
const navUrl = '/docs';

export function homeOptions(): BaseLayoutProps {
  return {
    nav: {
      title: navTitle,
      url: navUrl,
      transparentMode: 'top',
    },
  };
}

export function docsOptions(): BaseLayoutProps {
  return {
    nav: {
      title: navTitle,
      url: navUrl,
    },
  };
}
