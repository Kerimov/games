import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { RetroColors } from '../theme/colors';

type Props = {
  onUp?: () => void;
  onDown?: () => void;
  onLeft?: () => void;
  onRight?: () => void;
  onCenter?: () => void;
  onBack?: () => void;
  centerLabel?: string;
  showDpad?: boolean;
};

function Btn({ label, onPress, size = 48 }: { label: string; onPress?: () => void; size?: number }) {
  return (
    <TouchableOpacity onPress={onPress} style={[styles.btn, { width: size, height: size, borderRadius: size / 2 }]}>
      <Text style={styles.btnText}>{label}</Text>
    </TouchableOpacity>
  );
}

export function DpadControls({
  onUp, onDown, onLeft, onRight, onCenter, onBack,
  centerLabel = 'OK', showDpad = true,
}: Props) {
  return (
    <View style={styles.wrap}>
      {showDpad && (
        <>
          <Btn label="▲" onPress={onUp} />
          <View style={styles.row}>
            <Btn label="◀" onPress={onLeft} />
            <Btn label={centerLabel} onPress={onCenter} size={56} />
            <Btn label="▶" onPress={onRight} />
          </View>
          <Btn label="▼" onPress={onDown} />
        </>
      )}
      <TouchableOpacity onPress={onBack} style={styles.backBtn}>
        <Text style={styles.btnText}>Назад</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: { alignItems: 'center', paddingVertical: 12, backgroundColor: RetroColors.phoneBody, gap: 8 },
  row: { flexDirection: 'row', alignItems: 'center', gap: 16 },
  btn: { backgroundColor: RetroColors.buttonBg, alignItems: 'center', justifyContent: 'center' },
  backBtn: { backgroundColor: RetroColors.buttonBg, paddingHorizontal: 20, paddingVertical: 10, borderRadius: 20, marginTop: 8 },
  btnText: { color: RetroColors.textOnPhone, fontFamily: 'monospace', fontSize: 14 },
});
