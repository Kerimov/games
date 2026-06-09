import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { RetroColors } from '../theme/colors';

type Props = {
  title: string;
  score?: string;
  children: React.ReactNode;
};

export function PhoneFrame({ title, score = '', children }: Props) {
  return (
    <View style={styles.phone}>
      <Text style={styles.brand}>RETRO</Text>
      <View style={styles.screenBorder}>
        <View style={styles.screen}>
          {(title || score) ? (
            <View style={styles.header}>
              <Text style={styles.headerText}>{title}</Text>
              <Text style={styles.headerText}>{score}</Text>
            </View>
          ) : null}
          <View style={styles.content}>{children}</View>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  phone: { flex: 1, backgroundColor: RetroColors.phoneBody, padding: 16, paddingTop: 48 },
  brand: { color: RetroColors.textOnPhone, fontFamily: 'monospace', fontWeight: 'bold', fontSize: 18, textAlign: 'center', marginBottom: 8 },
  screenBorder: { flex: 1, borderWidth: 3, borderColor: RetroColors.phoneBorder, borderRadius: 4 },
  screen: { flex: 1, backgroundColor: RetroColors.screenBg, padding: 8 },
  header: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 4 },
  headerText: { fontFamily: 'monospace', fontWeight: 'bold', fontSize: 12, color: RetroColors.pixel },
  content: { flex: 1 },
});
