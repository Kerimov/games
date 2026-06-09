import React, { useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { PhoneFrame } from '../components/PhoneFrame';
import { DpadControls } from '../components/DpadControls';
import { RetroColors } from '../theme/colors';

const init = () => Array.from({ length: 14 }, (_, i) => (i === 6 || i === 13 ? 0 : 4));

export function BantumiGame({ onBack }: { onBack: () => void }) {
  const [pits, setPits] = useState(init);
  const [cursor, setCursor] = useState(0);
  const [turn, setTurn] = useState(0);
  const [msg, setMsg] = useState('Ваш ход');

  const opp = (p: number) => 12 - p;

  const checkEnd = (p: number[]) => {
    const pt = p.slice(0, 6).reduce((a, b) => a + b, 0);
    const ot = p.slice(7, 13).reduce((a, b) => a + b, 0);
    if (pt === 0 || ot === 0) {
      const np = [...p]; np[6] += pt; np[13] += ot;
      for (let i = 0; i < 6; i++) np[i] = 0;
      for (let i = 7; i < 13; i++) np[i] = 0;
      setPits(np);
      setMsg(np[6] > np[13] ? 'Вы победили!' : np[6] < np[13] ? 'Вы проиграли' : 'Ничья');
      setTurn(-1);
      return true;
    }
    return false;
  };

  const sow = (start: number, player: boolean, from?: number[]) => {
    const p = [...(from ?? pits)];
    let stones = p[start]; p[start] = 0;
    let pit = start, last = -1;
    while (stones > 0) {
      pit = (pit + 1) % 14;
      if (pit === 6 || pit === 13) continue;
      p[pit]++; stones--; last = pit;
    }
    if (player && last >= 0 && last <= 5 && p[last] === 1) {
      const o = opp(last);
      if (p[o] > 0) { p[6] += p[last] + p[o]; p[last] = 0; p[o] = 0; }
    } else if (!player && last >= 7 && last <= 12 && p[last] === 1) {
      const o = opp(last);
      if (p[o] > 0) { p[13] += p[last] + p[o]; p[last] = 0; p[o] = 0; }
    }
    setPits(p);
    return p;
  };

  const ai = (from: number[]) => {
    const opts = [7, 8, 9, 10, 11, 12].filter((i) => from[i] > 0);
    if (!opts.length) return;
    const best = opts.reduce((a, b) => (from[a] >= from[b] ? a : b));
    const np = sow(best, false, from);
    if (!checkEnd(np)) { setTurn(0); setMsg('Ваш ход'); }
  };

  const play = (pit: number) => {
    if (turn !== 0 || pit < 0 || pit > 5 || pits[pit] === 0) return;
    const np = sow(pit, true, pits);
    if (checkEnd(np)) return;
    setTurn(1); setMsg('Ход соперника');
    setTimeout(() => ai(np), 500);
  };

  const reset = () => { setPits(init()); setCursor(0); setTurn(0); setMsg('Ваш ход'); };

  return (
    <View style={styles.root}>
      <PhoneFrame title="Bantumi" score={`${pits[6]} : ${pits[13]}`}>
        <Text style={styles.msg}>{msg}</Text>
        <View style={styles.row}>
          {[12, 11, 10, 9, 8, 7].map((i) => (
            <View key={i} style={styles.pit}><Text style={styles.num}>{pits[i]}</Text></View>
          ))}
        </View>
        <View style={styles.stores}>
          <View style={styles.store}><Text style={styles.num}>{pits[13]}</Text></View>
          <View style={styles.store}><Text style={styles.num}>{pits[6]}</Text></View>
        </View>
        <View style={styles.row}>
          {[0, 1, 2, 3, 4, 5].map((i) => (
            <TouchableOpacity key={i} onPress={() => play(i)} style={[styles.pit, styles.pitP, cursor === i && styles.pitSel]}>
              <Text style={styles.num}>{pits[i]}</Text>
            </TouchableOpacity>
          ))}
        </View>
      </PhoneFrame>
      <DpadControls
        onLeft={() => setCursor((c) => Math.max(0, c - 1))}
        onRight={() => setCursor((c) => Math.min(5, c + 1))}
        onCenter={() => turn === -1 ? reset() : play(cursor)}
        onBack={onBack} centerLabel={turn === -1 ? '↻' : 'OK'}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  msg: { textAlign: 'center', fontFamily: 'monospace', color: RetroColors.pixel, marginBottom: 4 },
  row: { flexDirection: 'row', justifyContent: 'space-between', marginVertical: 4 },
  pit: { flex: 1, margin: 2, aspectRatio: 1.2, backgroundColor: RetroColors.screenDark, borderRadius: 20, justifyContent: 'center', alignItems: 'center' },
  pitP: { backgroundColor: 'rgba(48,98,48,0.5)' },
  pitSel: { backgroundColor: RetroColors.highlight },
  stores: { flexDirection: 'row', justifyContent: 'space-between', marginVertical: 4 },
  store: { width: '16%', paddingVertical: 8, backgroundColor: RetroColors.screenDark, borderRadius: 8, alignItems: 'center' },
  num: { fontFamily: 'monospace', fontWeight: 'bold', color: RetroColors.pixel, fontSize: 16 },
});
