// index.tsx
import { requireNativeComponent, type ViewStyle, type NativeSyntheticEvent } from 'react-native';

type LoadFinishedEvent = NativeSyntheticEvent<{url: string}>;
type LoadStartedEvent = NativeSyntheticEvent<{ url: string }>;
type RedirectReceivedEvent = NativeSyntheticEvent<{ url: string }>;
type LoadErrorEvent = NativeSyntheticEvent<{ url: string, error: string }>;
type ClosePressedEvent = NativeSyntheticEvent<{ message: string }>;

interface SmaAdWebViewProps {
  zoneId: string;
  userParameter: string;
  style?: ViewStyle;
  onLoadFinished?: (e: LoadFinishedEvent) => void;
  onLoadStarted?: (e: LoadStartedEvent) => void;
  onRedirectReceived?: (e: RedirectReceivedEvent) => void;
  onLoadError?: (e: LoadErrorEvent) => void;
  onClosePressed?: (e: ClosePressedEvent) => void;
}

const ComponentName = 'SmaAdWebView';

const SmaAdWebView = requireNativeComponent<SmaAdWebViewProps>(ComponentName);
export default SmaAdWebView;
