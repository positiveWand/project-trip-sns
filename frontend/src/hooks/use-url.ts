import * as React from 'react';

function subscribeUrlParam(onUrlChange: () => void) {
  window.addEventListener('popstate', onUrlChange);
  window.addEventListener('changeurl', onUrlChange);

  return () => {
    window.removeEventListener('popstate', onUrlChange);
    window.removeEventListener('changeurl', onUrlChange);
  };
}

export function useUrlSearchParamString() {
  return React.useSyncExternalStore(subscribeUrlParam, () => {
    return window.location.search;
  });
}

export function useUrlPathString() {
  return React.useSyncExternalStore(subscribeUrlParam, () => {
    return window.location.pathname;
  });
}

export function useChangeUrl(path: string, keepSearchParam: boolean) {
  return React.useCallback(() => {
    window.history.pushState({}, '', path + (keepSearchParam ? window.location.search : ''));
    window.dispatchEvent(new Event('changeurl'));
  }, []);
}

export function useUrlSearchParam(
  key: string,
  defaultValue: string | undefined,
): [string | undefined, (value: string) => void] {
  const searchParamString = useUrlSearchParamString();

  const value = React.useMemo(() => {
    const urlParams = new URLSearchParams(searchParamString);
    return urlParams.has(key) ? (urlParams.get(key) as string) : defaultValue;
  }, [searchParamString]);

  const setter = React.useCallback(
    (value: string) => {
      const urlParams = new URLSearchParams(window.location.search);

      if (value) {
        urlParams.set(key, value);
      } else {
        urlParams.delete(key);
      }

      window.history.pushState(
        {},
        '',
        urlParams.size == 0
          ? window.location.origin + window.location.pathname
          : '?' + urlParams.toString(),
      );

      window.dispatchEvent(new Event('changeurl'));
    },
    [key],
  );

  return [value, setter];
}

export function useAllUrlSearchParam(key: string): [string[], (values: string[]) => void] {
  const searchParamString = useUrlSearchParamString();

  const values = React.useMemo(() => {
    const urlParams = new URLSearchParams(searchParamString);
    return urlParams.getAll(key);
  }, [searchParamString]);

  const setter = React.useCallback(
    (values: string[]) => {
      const urlParams = new URLSearchParams(window.location.search);

      urlParams.delete(key);
      for (const value of values) {
        urlParams.append(key, value);
      }

      window.history.pushState(
        {},
        '',
        urlParams.size == 0
          ? window.location.origin + window.location.pathname
          : '?' + urlParams.toString(),
      );

      window.dispatchEvent(new Event('changeurl'));
    },
    [key],
  );

  return [values, setter];
}

export function useUrlPathParam(
  pathPattern: string,
): [
  Record<string, string | undefined>,
  (key: string, value: number | string | null | undefined) => void,
] {
  const pathString = useUrlPathString();

  const keys = React.useMemo(() => {
    const keys: { key: string; index: number }[] = [];
    pathPattern.split('/').forEach((value, index) => {
      if (!value.startsWith(':')) {
        return;
      }

      keys.push({
        key: value.slice(1),
        index: index,
      });
    });

    return keys;
  }, [pathPattern]);

  const entries = React.useMemo<Record<string, string | undefined>>(() => {
    const currentPath = pathString.split('/');

    const entries: Record<string, string | undefined> = {};
    keys.forEach(({ key, index }) => {
      if (index >= currentPath.length) {
        entries[key] = undefined;
      } else {
        entries[key] = currentPath[index];
      }
    });
    return entries;
  }, [pathString, keys]);

  const setter = React.useCallback(
    (key: string, value: number | string | null | undefined) => {
      const path: string[] = [];
      let flag = false;
      pathPattern.split('/').forEach((subpath) => {
        if (flag) return;

        if (!subpath.startsWith(':')) {
          path.push(subpath);
          return;
        }

        if (subpath.slice(1) == key && value) {
          path.push(value.toString());
          return;
        }

        if (entries[subpath.slice(1)]) {
          path.push(entries[subpath.slice(1)]!);
          return;
        }

        flag = true;
      });

      window.history.pushState({}, '', path.join('/') + window.location.search);
      window.dispatchEvent(new Event('changeurl'));
    },
    [pathPattern, entries],
  );

  return [entries, setter];
}
