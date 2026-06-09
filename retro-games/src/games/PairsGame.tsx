import React, { useEffect, useMemo, useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { PhoneFrame } from '../components/PhoneFrame';
import { DpadControls } from '../components/DpadControls';
import { RetroColors } from '../theme/colors';

const SYMBOLS = ['♠', '♥', '♦', '♣', '★', '●', '▲', '■'];
const COLS = 4;

function shuffle<T>(a: T[]) {
  const arr = [...a];
  for (let i = arr.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [arr[i], arr[j]] = [arr[j], arr[i]];
  }
  return arr;
}

export function PairsGame({ onBack }: { onBack: () => void }) {
  const [seed, setSeed] = useState(0);
  const cards = useMemo(() => shuffle([...SYMBOLS, ...SYMBOLS]).map((s, i) => ({ i, s })), [seed]);
  const [selected, setSelected] = useState(-1);
  const [matched, setMatched] = useState<Set<number>>(new Set());
  const [flipped, setFlipped] = useState<Set<number>>(new Set());
  const [cursor, setCursor] = useState(0);
  const [moves, setMoves] = useState(0);
  const [lock, setLock] = useState(false);
  const won = matched.size === cards.length;

  const reset = () => {
    setSeed((s) => s + 1); setSelected(-1); setMatched(new Set());
    setFlipped(new Set()); setCursor(0); setMoves(0); setLock(false);
  };

  const flip = (index: number) => {
    if (lock || matched.has(index) || flipped.has(index)) return;
    if (selected === -1) { setSelected(index); setFlipped((f) => new Set(f).add(index)); }
    else if (selected !== index) {
      const nf = new Set(flipped); nf.add(index); setFlipped(nf); setMoves((m) => m + 1);
      if (cards[selected].s === cards[index].s) {
        setMatched((m) => new Set([...m, selected, index])); setSelected(-1);
      } else setLock(true);
    }
  };

  useEffect(() => {
    if (!lock) return;
    const t = setTimeout(() => {
      setFlipped((f) => { const nf = new Set(f); nf.delete(selected); const last = [...f].pop(); if (last !== undefined) nf.delete(last); return nf; });
      setSelected(-1); setLock(false);
    }, 800);
    return () => clearTimeout(t);
  }, [lock, selected]);

  return (
    <View style={styles.root}>
      <PhoneFrame title="Pairs II" score={`Ходы: ${moves}`}>
        <View style={styles.grid}>
          {cards.map((c, i) => {
            const open = flipped.has(i) || matched.has(i);
            const sel = cursor === i;
            return (
              <TouchableOpacity key={i} onPress={() => flip(i)} style={[styles.card, open && styles.cardOpen, sel && styles.cardSel]}>
                {open && <Text style={styles.sym}>{c.s}</Text>}
              </TouchableOpacity>
            );
          })}
        </View>
      </PhoneFrame>
      <DpadControls
        onUp={() => setCursor((c) => Math.max(0, c - COLS))}
        onDown={() => setCursor((c) => Math.min(cards.length - 1, c + COLS))}
        onLeft={() => setCursor((c) => (c % COLS > 0 ? c - 1 : c))}
        onRight={() => setCursor((c) => (c % COLS < COLS - 1 ? c + 1 : c))}
        onCenter={() => won ? reset() : flip(cursor)}
        onBack={onBack} centerLabel={won ? '↻' : 'OK'}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  grid: { flex: 1, flexDirection: 'row', flexWrap: 'wrap' },
  card: { width: '25%', aspectRatio: 1, padding: 2, backgroundColor: RetroColors.screenDark, borderWidth: 1, borderColor: 'transparent', justifyContent: 'center', alignItems: 'center' },
  cardOpen: { backgroundColor: RetroColors.highlight },
  cardSel: { borderColor: RetroColors.pixel },
  sym: { fontSize: 22, color: RetroColors.pixel, fontWeight: 'bold' },
});
