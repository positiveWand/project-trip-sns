import * as React from 'react';

export async function IDENTITY_TRANSFORM<T>(value: T) {
  return value;
}

export function useProxyState<T, U>(
  state: T,
  transform: (state: T) => Promise<U | null>,
  deps: React.DependencyList,
): [U | null, React.Dispatch<React.SetStateAction<U | null>>] {
  const [proxyState, setProxyState] = React.useState<U | null>(null);

  React.useEffect(() => {
    transform(state)
      .then((transformedState) => setProxyState(transformedState))
      .catch(() => setProxyState(null));
  }, [state, ...deps]);

  return [proxyState, setProxyState];
}
