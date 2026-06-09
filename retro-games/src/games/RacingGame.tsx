import React, { useEffect, useRef, useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { PhoneFrame } from '../components/PhoneFrame';
import { DpadControls } from '../components/DpadControls';
import { RetroColors } from '../theme/colors';

type Obs = { lane: number; y: number; id: number };
let oid = 0;

export function RacingGame({ onBack }: { onBack: () => void }) {
  const [lane, setLane] = useState(1);
  const [obs, setObs] = useState<Obs[]>([]);
  const [score, setScore] = useState(0);
  const [speed, setSpeed] = useState(2);
  const [gameOver, setGameOver] = useState(false);
  const tick = useRef(0);

  const reset = () => { setLane(1); setObs([]); setScore(0); setSpeed(2); setGameOver(false); tick.current = 0; };

  useEffect(() => {
    if (gameOver) return;
    const id = setInterval(() => {
      tick.current++; setScore((s) => s + 1);
      setObs((o) => {
        let next = o.map((x) => ({ ...x, y: x.y + speed })).filter((x) => x.y < 170);
        if (tick.current % 40 === 0) next.push({ lane: Math.floor(Math.random() * 3), y: -20, id: oid++ });
        next.forEach((x) => {
          if (x.lane === lane && x.y > 120 && x.y < 150) setGameOver(true);
        });
        return next;
      });
      if (tick.current % 200 === 0) setSpeed((s) => Math.min(6, s + 0.3));
    }, 33);
    return () => clearInterval(id);
  }, [gameOver, lane, speed]);

  return (
    <View style={styles.root}>
      <PhoneFrame title="Racing" score={`Дист: ${score}`}>
        <View style={styles.road}>
          {[0, 1, 2].map((l) => <View key={l} style={[styles.laneLine, { left: `${33.33 * (l + 1)}%` }]} />)}
          {obs.map((o) => (
            <View key={o.id} style={[styles.car, { left: `${o.lane * 33.33 + 4}%`, top: o.y }]} />
          ))}
          <View style={[styles.player, { left: `${lane * 33.33 + 6}%` }]} />
          {gameOver && <View style={styles.overlay} />}
        </View>
      </PhoneFrame>
      <DpadControls
        onLeft={() => !gameOver && setLane((l) => Math.max(0, l - 1))}
        onRight={() => !gameOver && setLane((l) => Math.min(2, l + 1))}
        onCenter={() => gameOver && reset()}
        onBack={onBack} centerLabel={gameOver ? '↻' : '—'}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  road: { flex: 1, position: 'relative' },
  laneLine: { position: 'absolute', top: 0, bottom: 0, width: 2, backgroundColor: 'rgba(48,98,48,0.3)' },
  car: { position: 'absolute', width: '26%', height: 18, backgroundColor: RetroColors.pixel },
  player: { position: 'absolute', bottom: 8, width: '22%', height: 28, backgroundColor: RetroColors.pixelLight },
  overlay: { ...StyleSheet.absoluteFillObject, backgroundColor: 'rgba(139,172,15,0.7)' },
});
