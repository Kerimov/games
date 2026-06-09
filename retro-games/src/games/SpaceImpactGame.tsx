import React, { useEffect, useRef, useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { PhoneFrame } from '../components/PhoneFrame';
import { DpadControls } from '../components/DpadControls';
import { RetroColors } from '../theme/colors';

type E = { x: number; y: number; w: number; h: number; id: number };
let eid = 0;

export function SpaceImpactGame({ onBack }: { onBack: () => void }) {
  const [playerY, setPlayerY] = useState(55);
  const [bullets, setBullets] = useState<E[]>([]);
  const [enemies, setEnemies] = useState<E[]>([]);
  const [eBullets, setEBullets] = useState<E[]>([]);
  const [score, setScore] = useState(0);
  const [lives, setLives] = useState(3);
  const [gameOver, setGameOver] = useState(false);
  const tick = useRef(0);

  const reset = () => {
    setPlayerY(55); setBullets([]); setEnemies([]); setEBullets([]);
    setScore(0); setLives(3); setGameOver(false); tick.current = 0;
  };

  useEffect(() => {
    if (gameOver) return;
    const id = setInterval(() => {
      tick.current++;
      setBullets((b) => b.map((x) => ({ ...x, x: x.x + 6 })).filter((x) => x.x < 160));
      setEnemies((e) => {
        let next = e.map((x) => ({ ...x, x: x.x - 1.5 })).filter((x) => x.x > -20);
        if (tick.current % 60 === 0 && next.length < 6)
          next.push({ x: 160, y: Math.random() * 104, w: 12, h: 12, id: eid++ });
        return next;
      });
      if (tick.current % 45 === 0) {
        setEnemies((cur) => {
          if (cur.length) setEBullets((eb) => [...eb, { x: cur[0].x, y: cur[0].y + 4, w: 4, h: 2, id: eid++ }]);
          return cur;
        });
      }
      setEBullets((eb) => eb.map((x) => ({ ...x, x: x.x - 4 })).filter((x) => x.x > -10));

      const px = 12, ph = 10;
      setBullets((b) => {
        setEnemies((en) => {
          const hit = new Set<number>();
          b.forEach((bl) => en.forEach((e) => {
            if (bl.x < e.x + e.w && bl.x + bl.w > e.x && bl.y < e.y + e.h && bl.y + bl.h > e.y) {
              hit.add(e.id); setScore((s) => s + 10);
            }
          }));
          return en.filter((e) => !hit.has(e.id));
        });
        return b;
      });

      setEBullets((eb) => {
        const left = eb.filter((b) => {
          const hit = b.x < px + 14 && b.x + b.w > px && b.y < playerY + ph && b.y + b.h > playerY;
          if (hit) setLives((l) => { if (l <= 1) setGameOver(true); return l - 1; });
          return !hit;
        });
        return left;
      });
      setEnemies((en) => {
        const left = en.filter((e) => {
          const hit = e.x < px + 14 && e.x + e.w > px && e.y < playerY + ph && e.y + e.h > playerY;
          if (hit) setLives((l) => { if (l <= 1) setGameOver(true); return l - 1; });
          return !hit;
        });
        return left;
      });
    }, 33);
    return () => clearInterval(id);
  }, [gameOver, playerY]);

  const pct = (v: number, max: number) => `${(v / max) * 100}%`;

  return (
    <View style={styles.root}>
      <PhoneFrame title="Space Impact" score={`★${score} ♥${lives}`}>
        <View style={styles.field}>
          <View style={[styles.player, { top: pct(playerY, 120) }]} />
          {bullets.map((b) => <View key={b.id} style={[styles.bullet, { left: pct(b.x, 160), top: pct(b.y, 120), width: pct(b.w, 160), height: pct(b.h, 120) }]} />)}
          {enemies.map((e) => <View key={e.id} style={[styles.enemy, { left: pct(e.x, 160), top: pct(e.y, 120), width: pct(e.w, 160), height: pct(e.h, 120) }]} />)}
          {eBullets.map((b) => <View key={b.id} style={[styles.bullet, { left: pct(b.x, 160), top: pct(b.y, 120), width: pct(b.w, 160), height: pct(b.h, 120) }]} />)}
          {gameOver && <View style={styles.overlay} />}
        </View>
      </PhoneFrame>
      <DpadControls
        onUp={() => !gameOver && setPlayerY((y) => Math.max(0, y - 8))}
        onDown={() => !gameOver && setPlayerY((y) => Math.min(110, y + 8))}
        onCenter={() => gameOver ? reset() : setBullets((b) => [...b, { x: 26, y: playerY + 3, w: 6, h: 3, id: eid++ }])}
        onBack={onBack} centerLabel={gameOver ? '↻' : '🔫'}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  field: { flex: 1, position: 'relative' },
  player: { position: 'absolute', left: '7.5%', width: '8.75%', height: '8.3%', backgroundColor: RetroColors.pixel },
  bullet: { position: 'absolute', backgroundColor: RetroColors.pixel },
  enemy: { position: 'absolute', backgroundColor: RetroColors.pixelLight },
  overlay: { ...StyleSheet.absoluteFillObject, backgroundColor: 'rgba(139,172,15,0.7)' },
});
