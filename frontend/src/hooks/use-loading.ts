import * as React from 'react';

export function useLoading(...loadings: boolean[]) {
  return React.useMemo(() => loadings.every((loading) => loading), [loadings]);
}
