import React, { useState } from 'react';
import { StatusBar } from 'expo-status-bar';
import { SafeAreaView, StyleSheet } from 'react-native';
import { GameMenu, GameId } from './src/screens/GameMenu';
import { SnakeGame } from './src/games/SnakeGame';
import { SpaceImpactGame } from './src/games/SpaceImpactGame';
import { PairsGame } from './src/games/PairsGame';
import { BantumiGame } from './src/games/BantumiGame';
import { RacingGame } from './src/games/RacingGame';
import { BounceGame } from './src/games/BounceGame';
import { RetroColors } from './src/theme/colors';

export default function App() {
  const [screen, setScreen] = useState<GameId | 'menu'>('menu');

  const back = () => setScreen('menu');

  return (
    <SafeAreaView style={styles.root}>
      <StatusBar style="light" />
      {screen === 'menu' && <GameMenu onSelect={setScreen} />}
      {screen === 'snake' && <SnakeGame onBack={back} />}
      {screen === 'spaceimpact' && <SpaceImpactGame onBack={back} />}
      {screen === 'pairs' && <PairsGame onBack={back} />}
      {screen === 'bantumi' && <BantumiGame onBack={back} />}
      {screen === 'racing' && <RacingGame onBack={back} />}
      {screen === 'bounce' && <BounceGame onBack={back} />}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: RetroColors.phoneBody },
});
