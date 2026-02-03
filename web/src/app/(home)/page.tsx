import Link from 'next/link';

const features = [
  {
    title: 'Menu de Interação',
    description: 'Shift + botão direito para informações instantâneas.',
  },
  {
    title: 'Quebra Controlada',
    description: 'Regras claras de ferramenta, silk touch e chance.',
  },
  {
    title: 'Sistema de Explosões',
    description: 'Defina se explode e ainda dropa spawner.',
  },
  {
    title: 'Raridades Configuráveis',
    description: 'Cores e níveis por tipo de mob.',
  },
  {
    title: 'Locale e Mensagens',
    description: 'Textos externos e fáceis de editar.',
  },
];

const quickStart = [
  {
    title: 'Instale o plugin',
    body: 'Coloque o SpawnerX.jar em plugins/ e reinicie o servidor.',
  },
  {
    title: 'Configure',
    body: 'Ajuste config.yml e locale/pt_BR.yml conforme a sua base.',
  },
  {
    title: 'Aplique',
    body: 'Use /spawnerx reload para recarregar sem reiniciar.',
  },
];

export default function HomePage() {
  return (
    <div className="flex flex-1 flex-col">
      <section className="relative overflow-hidden">
        <div className="spx-hero-grid absolute inset-0" aria-hidden />
        <div className="absolute -top-24 right-8 h-48 w-48 rounded-full bg-fd-primary/15 blur-3xl animate-[spx-float_8s_ease-in-out_infinite]" />
        <div className="relative mx-auto flex w-full max-w-6xl flex-col gap-10 px-6 pb-20 pt-16 sm:px-10">
          <div className="inline-flex w-fit items-center gap-3 rounded-full border border-fd-border bg-fd-card/80 px-4 py-2 text-xs uppercase tracking-[0.24em] text-fd-muted-foreground animate-[spx-fade-up_0.6s_ease_forwards]">
            <span className="font-semibold text-fd-primary">SpawnerX</span>
            <span>Paper 1.21.x</span>
            <span>v1.0.0</span>
          </div>

          <div className="max-w-3xl">
            <h1 className="text-4xl font-semibold leading-tight text-fd-foreground sm:text-6xl animate-[spx-fade-up_0.8s_ease_forwards]">
              Controle total de spawners com foco em clareza e performance.
            </h1>
            <p className="mt-4 text-lg text-fd-muted-foreground animate-[spx-fade-up_0.9s_ease_forwards]">
              Documentação oficial para o SpawnerX — um plugin completo para gerenciamento de spawners
              em servidores Bukkit/Paper.
            </p>
            <p className="mt-2 text-sm uppercase tracking-[0.18em] text-fd-muted-foreground animate-[spx-fade-up_1s_ease_forwards]">
              Documentação oficial do SpawnerX para servidores Paper/Bukkit.
            </p>
          </div>

          <div className="flex flex-col gap-3 sm:flex-row animate-[spx-fade-up_1.1s_ease_forwards]">
            <Link
              href="/docs"
              className="inline-flex items-center justify-center rounded-full bg-fd-primary px-6 py-3 text-sm font-semibold text-fd-primary-foreground shadow-lg shadow-fd-primary/30 transition hover:translate-y-1px"
            >
              Documentação
            </Link>
            <Link
              href="/download"
              className="inline-flex items-center justify-center rounded-full border border-fd-border bg-fd-card px-6 py-3 text-sm font-semibold text-fd-foreground transition hover:border-fd-primary/60"
            >
              Instalação
            </Link>
          </div>

          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {features.map((feature) => (
              <div
                key={feature.title}
                className="rounded-2xl border border-fd-border bg-fd-card/90 p-5 shadow-sm backdrop-blur transition hover:-translate-y-1 hover:border-fd-primary/40"
              >
                <div className="flex items-center justify-between">
                  <h3 className="text-base font-semibold text-fd-foreground">{feature.title}</h3>
                  <span className="text-[11px] uppercase tracking-[0.2em] text-fd-muted-foreground">
                    Base
                  </span>
                </div>
                <p className="mt-3 text-sm text-fd-muted-foreground">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="mx-auto w-full max-w-6xl px-6 pb-20 sm:px-10">
        <div className="grid gap-10 lg:grid-cols-[1.1fr_1fr]">
          <div className="space-y-4">
            <h2 className="text-3xl font-semibold text-fd-foreground">Início rápido</h2>
            <p className="text-base text-fd-muted-foreground">
              Em poucos minutos você já está com o plugin instalado, configurado e pronto para uso.
            </p>
          </div>
          <div className="grid gap-4">
            {quickStart.map((step, index) => (
              <div
                key={step.title}
                className="flex gap-4 rounded-2xl border border-fd-border bg-fd-card/90 p-5"
              >
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-fd-primary/10 text-sm font-semibold text-fd-primary">
                  {index + 1}
                </div>
                  <div>
                    <h3 className="text-base font-semibold text-fd-foreground">{step.title}</h3>
                    <p className="text-sm text-fd-muted-foreground">{step.body}</p>
                  </div>
                </div>
              ))}
          </div>
        </div>
      </section>
    </div>
  );
}
