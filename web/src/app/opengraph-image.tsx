import { ImageResponse } from 'next/og';

export const size = {
  width: 1200,
  height: 630,
};

export const contentType = 'image/png';

export default function OpenGraphImage() {
  return new ImageResponse(
    (
      <div
        style={{
          position: 'relative',
          width: '100%',
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'space-between',
          padding: '64px',
          background: 'linear-gradient(135deg, #0f172a 0%, #111827 45%, #1f2937 100%)',
          color: '#f8fafc',
          fontFamily: 'Space Grotesk, system-ui, sans-serif',
          overflow: 'hidden',
        }}
      >
        <div
          style={{
            position: 'absolute',
            inset: 0,
            backgroundImage:
              'radial-gradient(circle at 12% 12%, rgba(56, 189, 248, 0.35), transparent 55%), radial-gradient(circle at 85% 80%, rgba(14, 165, 233, 0.25), transparent 55%)',
          }}
        />

        <div style={{ position: 'relative', display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div
            style={{
              fontSize: 20,
              letterSpacing: '0.45em',
              textTransform: 'uppercase',
              color: '#7dd3fc',
              fontWeight: 600,
            }}
          >
            SpawnerX
          </div>
          <div style={{ fontSize: 64, fontWeight: 700, lineHeight: 1.05 }}>
            Documentação oficial
          </div>
          <div style={{ fontSize: 28, color: '#e2e8f0', maxWidth: 760 }}>
            Controle total de spawners com clareza, performance e configuração avançada.
          </div>
        </div>

        <div style={{ position: 'relative', display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
          <div
            style={{
              padding: '12px 20px',
              borderRadius: 999,
              background: 'rgba(15, 23, 42, 0.6)',
              border: '1px solid rgba(148, 163, 184, 0.35)',
              fontSize: 20,
            }}
          >
            Paper/Bukkit 1.21.x
          </div>
          <div
            style={{
              padding: '12px 20px',
              borderRadius: 999,
              background: 'rgba(56, 189, 248, 0.18)',
              border: '1px solid rgba(56, 189, 248, 0.6)',
              fontSize: 20,
            }}
          >
            v1.0.0
          </div>
        </div>

        <div
          style={{
            position: 'absolute',
            top: 56,
            right: 56,
            width: 180,
            height: 180,
            borderRadius: 36,
            border: '2px solid rgba(56, 189, 248, 0.6)',
            background:
              'linear-gradient(135deg, rgba(56, 189, 248, 0.35) 0%, rgba(14, 116, 144, 0.35) 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: 72,
            fontWeight: 700,
            color: '#e2e8f0',
          }}
        >
          SX
        </div>
      </div>
    ),
    size,
  );
}
