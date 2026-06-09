import React, { useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { PhoneFrame } from '../components/PhoneFrame';
import { DpadControls } from '../components/DpadControls';
import { RetroColors } from '../theme/colors';

export const GAMES = [
  { id: 'snake', name: 'Snake II', desc: 'Классическая змейка' },
  { id: 'spaceimpact', name: 'Space Impact', desc: 'Космический шутер' },
  { id: 'pairs', name: 'Pairs II', desc: 'Найди пары' },
  { id: 'bantumi', name: 'Bantumi', desc: 'Африканские шашки' },
  { id: 'racing', name: 'Racing', desc: 'Гонки на выживание' },
  { id: 'bounce', name: 'Bounce', desc: 'Прыгающий мячик' },
] as const;

export type GameId = (typeof GAMES)[number]['id'];

type Props = { onSelect: (id: GameId) => void };

export function GameMenu({ onSelect }: Props) {
  const [idx, setIdx] = useState(0);

  return (
    <View style={styles.root}>
      <PhoneFrame title="Игры" score={`${idx + 1}/${GAMES.length}`}>
        <View style={styles.list}>
          {GAMES.map((g, i) => (
            <TouchableOpacity
              key={g.id}
              onPress={() => { setIdx(i); onSelect(g.id); }}
              style={[styles.item, i === idx && styles.itemSel]}
            >
              <Text style={[styles.itemText, i === idx && styles.itemTextSel]}>
                {i === idx ? `► ${g.name}` : `  ${g.name}`}
              </Text>
            </TouchableOpacity>
          ))}
          <Text style={styles.desc}>{GAMES[idx].desc}</Text>
        </View>
      </PhoneFrame>
      <DpadControls
        onUp={() => setIdx((v) => Math.max(0, v - 1))}
        onDown={() => setIdx((v) => Math.min(GAMES.length - 1, v + 1))}
        onCenter={() => onSelect(GAMES[idx].id)}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  list: { flex: 1, gap: 2 },
  item: { paddingHorizontal: 8, paddingVertical: 6, backgroundColor: 'rgba(139,172,15,0.3)' },
  itemSel: { backgroundColor: RetroColors.highlight },
  itemText: { fontFamily: 'monospace', fontSize: 14, color: RetroColors.pixelLight },
  itemTextSel: { color: RetroColors.pixel, fontWeight: 'bold' },
  desc: { marginTop: 'auto', textAlign: 'center', fontFamily: 'monospace', fontSize: 12, color: RetroColors.pixel, paddingBottom: 8 },
});
