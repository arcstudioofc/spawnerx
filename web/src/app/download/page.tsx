'use client';

import { useEffect } from 'react';

const downloadUrl = '/downloads/SpawnerX-1.0.0.jar';

export default function DownloadPage() {
  useEffect(() => {
    const timer = setTimeout(() => {
      window.location.href = downloadUrl;
    }, 300);
    return () => clearTimeout(timer);
  }, []);

  return (
    <div className="mx-auto flex w-full max-w-3xl flex-1 flex-col items-center justify-center px-6 py-20 text-center">
      <p className="text-sm uppercase tracking-[0.24em] text-fd-muted-foreground">SpawnerX</p>
      <h1 className="mt-4 text-3xl font-semibold text-fd-foreground">Seu download vai iniciar</h1>
      <p className="mt-3 text-base text-fd-muted-foreground">
        Se o download nao comecar automaticamente, use o botao abaixo.
      </p>
      <a
        href={downloadUrl}
        download
        className="mt-6 inline-flex items-center justify-center rounded-full bg-fd-primary px-6 py-3 text-sm font-semibold text-fd-primary-foreground shadow-lg shadow-fd-primary/30 transition hover:translate-y-[-1px]"
      >
        Baixar SpawnerX v1.0.0
      </a>
    </div>
  );
}
