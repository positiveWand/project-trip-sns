import * as React from 'react';

type Validator = (input: string) => boolean;

export function useForm<T extends Record<string, any>>(formPattern: {
  [name: string]: {
    defaultValue: any;
    validator: Validator;
  };
}): [T, (name: string, value: string) => void, boolean, Record<string, boolean>] {
  // 초기값
  const initValue = React.useMemo(() => {
    const mapper: Record<string, any> = {};
    for (const name in formPattern) {
      mapper[name] = formPattern[name].defaultValue;
    }

    return mapper;
  }, []);
  // 유효성 검사 함수
  const validator = React.useMemo(() => {
    const mapper: Record<string, Validator> = {};
    for (const name in formPattern) {
      mapper[name] = formPattern[name].validator;
    }

    return mapper;
  }, []);

  const [data, setData] = React.useState<Record<string, any>>(initValue);
  const isValid = React.useMemo(
    () => Object.entries(validator).every(([name, test]) => test(data[name])),
    [data],
  );
  const testResults = React.useMemo(() => {
    const results: { [name: string]: boolean } = {};
    for (const name in formPattern) {
      results[name] = validator[name](data[name]);
      // console.log(name, results[name]);
    }

    return results;
  }, [data]);

  const setFormData = (name: string, value: any) => {
    if (name === undefined) {
      return;
    }
    if (!(name in data)) {
      return;
    }

    // console.log(name, data[name], value);
    data[name] = value;
    setData({ ...data });
  };

  // [Form 데이터, Form 데이터 setter, Form 유효 여부, Form 필드별 유효 여부]
  return [data as T, setFormData, isValid, testResults];
}
