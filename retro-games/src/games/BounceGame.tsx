import React, { useEffect, useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { PhoneFrame } from '../components/PhoneFrame';
import { DpadControls } from '../components/DpadControls';
import { RetroColors } from '../theme/colors';

const PLATFORMS = [
  { x: 0, y: 152, w: 120 }, { x: 0, y: 130, w: 40 }, { x: 70, y: 110, w: 50 },
  { x: 10, y: 90, w: 35 }, { x: 60, y: 70, w: 45 }, { x: 5, y: 50, w: 30 },
  { x: 55, y: 30, w: 50 }, { x: 20, y: 12, w: 30 },
];
const RINGS = [[15, 122], [85, 102], [25, 82], [75, 62], [15, 42], [70, 22]] as const;
const SW = 120, SH = 160, BR = 5;

export function BounceGame({ onBack }: { onBack: () => void }) {
  const [bx, setBx] = useState(20);
  const [by, setBy] = useState(140);
  const [vx, setVx] = useState(0);
  const [vy, setVy] = useState(0);
  const [ground, setGround] = useState(false);
  const [collected, setCollected] = useState<Set<number>>(new Set());
  const [score, setScore] = useState(0);
  const [gameOver, setGameOver] = useState(false);
  const [won, setWon] = useState(false);

  const reset = () => {
    setBx(20); setBy(140); setVx(0); setVy(0); setGround(false);
    setCollected(new Set()); setScore(0); setGameOver(false); setWon(false);
  };

  useEffect(() => {
    if (gameOver || won) return;
    const id = setInterval(() => {
      setVy((v) => v + 0.25);
      setBx((x) => {
        let nx = x + vx;
        if (nx < BR) nx = BR;
        if (nx > SW - BR) nx = SW - BR;
        return nx;
      });
      setBy((y) => {
        let ny = y + vy;
        let ng = false;
        if (ny > SH - BR) {
          ny = SH - BR;
          setVy((v) => (Math.abs(v * 0.6) < 1 ? 0 : -v * 0.6));
          setVx((v) => v * 0.9);
          ng = true;
        }
        PLATFORMS.forEach((p) => {
          if (bx + BR > p.x && bx - BR < p.x + p.w && ny + BR >= p.y && ny + BR <= p.y + 6) {
            ny = p.y - BR; setVy(0); ng = true;
          }
        });
        setGround(ng);
        if (ny < -10) setGameOver(true);
        return ny;
      });
      RINGS.forEach(([rx, ry], i) => {
        if (!collected.has(i)) {
          const dx = bx - rx, dy = by - ry;
          if (dx * dx + dy * dy < 100) {
            setCollected((c) => new Set(c).add(i));
            setScore((s) => s + 50);
          }
        }
      });
    }, 16);
    return () => clearInterval(id);
  }, [gameOver, won, vx, vy, bx, collected]);

  useEffect(() => {
    if (collected.size === RINGS.length) setWon(true);
  }, [collected]);

  const pct = (v: number, max: number) => `${(v / max) * 100}%`;

  return (
    <View style={styles.root}>
      <PhoneFrame title="Bounce" score={`★${score}`}>
        <View style={styles.field}>
          {PLATFORMS.map((p, i) => (
            <View key={i} style={[styles.plat, { left: pct(p.x, SW), top: pct(p.y, SH), width: pct(p.w, SW) }]} />
          ))}
          {RINGS.map(([rx, ry], i) => !collected.has(i) && (
            <View key={i} style={[styles.ring, { left: pct(rx, SW), top: pct(ry, SH) }]} />
          ))}
          <View style={[styles.ball, { left: pct(bx, SW), top: pct(by, SH) }]} />
          {(gameOver || won) && <View style={styles.overlay} />}
        </View>
      </PhoneFrame>
      <DpadControls
        onLeft={() => !gameOver && !won && setVx(-2.5)}
        onRight={() => !gameOver && !won && setVx(2.5)}
        onCenter={() => (gameOver || won) ? reset() : ground && setVy(-5.5)}
        onBack={onBack} centerLabel={gameOver || won ? '↻' : '↑'}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  field: { flex: 1, position: 'relative' },
  plat: { position: 'absolute', height: 4, backgroundColor: RetroColors.pixelLight },
  ring: { position: 'absolute', width: 12, height: 12, borderRadius: 6, borderWidth: 2, borderColor: RetroColors.pixel, marginLeft: -6, marginTop: -6 },
  ball: { position: 'absolute', width: 10, height: 10, borderRadius: 5, backgroundColor: RetroColors.pixel, marginLeft: -5, marginTop: -5 },
  overlay: { ...StyleSheet.absoluteFillObject, backgroundColor: 'rgba(139,172,15,0.7)' },
});
