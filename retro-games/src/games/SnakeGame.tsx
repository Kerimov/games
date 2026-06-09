import React, { useEffect, useRef, useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { PhoneFrame } from '../components/PhoneFrame';
import { DpadControls } from '../components/DpadControls';
import { RetroColors } from '../theme/colors';

type Dir = 'UP' | 'DOWN' | 'LEFT' | 'RIGHT';
type Pt = { x: number; y: number };
const GW = 14, GH = 16;

export function SnakeGame({ onBack }: { onBack: () => void }) {
  const [snake, setSnake] = useState<Pt[]>([{ x: 7, y: 8 }, { x: 6, y: 8 }, { x: 5, y: 8 }]);
  const [food, setFood] = useState<Pt>({ x: 10, y: 8 });
  const [score, setScore] = useState(0);
  const [gameOver, setGameOver] = useState(false);
  const [paused, setPaused] = useState(false);
  const dir = useRef<Dir>('RIGHT');
  const nextDir = useRef<Dir>('RIGHT');

  const spawn = (body: Pt[]) => {
    const free: Pt[] = [];
    for (let x = 0; x < GW; x++) for (let y = 0; y < GH; y++) {
      if (!body.some((p) => p.x === x && p.y === y)) free.push({ x, y });
    }
    return free[Math.floor(Math.random() * free.length)];
  };

  const reset = () => {
    setSnake([{ x: 7, y: 8 }, { x: 6, y: 8 }, { x: 5, y: 8 }]);
    dir.current = 'RIGHT'; nextDir.current = 'RIGHT';
    setFood({ x: 10, y: 8 }); setScore(0); setGameOver(false); setPaused(false);
  };

  useEffect(() => {
    if (gameOver || paused) return;
    const id = setInterval(() => {
      dir.current = nextDir.current;
      setSnake((prev) => {
        const h = prev[0];
        const nh = {
          UP: { x: h.x, y: h.y - 1 }, DOWN: { x: h.x, y: h.y + 1 },
          LEFT: { x: h.x - 1, y: h.y }, RIGHT: { x: h.x + 1, y: h.y },
        }[dir.current];
        if (nh.x < 0 || nh.x >= GW || nh.y < 0 || nh.y >= GH || prev.some((p) => p.x === nh.x && p.y === nh.y)) {
          setGameOver(true); return prev;
        }
        const ate = nh.x === food.x && nh.y === food.y;
        const next = [nh, ...prev.slice(0, ate ? prev.length : prev.length - 1)];
        if (ate) { setScore((s) => s + 1); setFood(spawn(next)); }
        return next;
      });
    }, 180);
    return () => clearInterval(id);
  }, [gameOver, paused, food]);

  const setDir = (d: Dir, block: Dir) => {
    if (!gameOver && dir.current !== block) nextDir.current = d;
  };

  return (
    <View style={styles.root}>
      <PhoneFrame title="Snake II" score={`Очки: ${score}`}>
        <View style={styles.grid}>
          {Array.from({ length: GH }, (_, y) => (
            <View key={y} style={styles.row}>
              {Array.from({ length: GW }, (_, x) => {
                const si = snake.findIndex((p) => p.x === x && p.y === y);
                const isFood = food.x === x && food.y === y;
                return (
                  <View key={x} style={styles.cell}>
                    {si >= 0 && <View style={[styles.snake, si === 0 && styles.head]} />}
                    {isFood && <View style={styles.food} />}
                  </View>
                );
              })}
            </View>
          ))}
          {gameOver && <View style={styles.overlay} />}
        </View>
      </PhoneFrame>
      <DpadControls
        onUp={() => setDir('UP', 'DOWN')} onDown={() => setDir('DOWN', 'UP')}
        onLeft={() => setDir('LEFT', 'RIGHT')} onRight={() => setDir('RIGHT', 'LEFT')}
        onCenter={() => gameOver ? reset() : setPaused((p) => !p)}
        onBack={onBack}
        centerLabel={gameOver ? '↻' : paused ? '▶' : '❚❚'}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  grid: { flex: 1 },
  row: { flex: 1, flexDirection: 'row' },
  cell: { flex: 1, margin: 0.5, justifyContent: 'center', alignItems: 'center' },
  snake: { width: '85%', height: '85%', backgroundColor: RetroColors.pixelLight },
  head: { backgroundColor: RetroColors.pixel },
  food: { width: '60%', height: '60%', backgroundColor: RetroColors.pixel },
  overlay: { ...StyleSheet.absoluteFillObject, backgroundColor: 'rgba(139,172,15,0.7)' },
});
