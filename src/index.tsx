import {
  requireNativeComponent,
  UIManager,
  Platform,
  type ViewStyle,
} from 'react-native';

const LINKING_ERROR =
  `The package 'smaad-rn-sdk' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

type SmaAdWebProps = {
  color: string;
  style: ViewStyle;
};

const ComponentName = 'SmaAdWebView';

export const SmaAdWebView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<SmaAdWebProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
