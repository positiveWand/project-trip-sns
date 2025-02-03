export interface SuccessResponse<T> {
  success: true;
  body: T;
}

export type BasicErrorType = 'CLIENT_ERROR' | 'NETWORK_ERROR' | 'SERVER_ERROR';
export interface ErrorResponse<T> {
  success: false;
  error: {
    type: T | BasicErrorType;
    message: string;
  };
}

export type Response<T, U> = SuccessResponse<T> | ErrorResponse<U>;

export interface PathParams {
  [key: string]: string | number;
}

export class UrlBuilder {
  #url;
  #pathVariable: PathParams;
  #serachParam: URLSearchParams;

  constructor(url: string) {
    this.#url = url;
    this.#pathVariable = {};
    this.#serachParam = new URLSearchParams();
  }

  setPathVariable(name: string, value: string | number): UrlBuilder {
    this.#pathVariable[name] = value;
    return this;
  }

  setSearchParam(name: string, value: string | number): UrlBuilder {
    this.#serachParam.set(name, value + '');
    return this;
  }

  build(): string {
    let result = '';
    result += this.#url;
    for (const pv in this.#pathVariable) {
      result.replace(`{${pv}}`, this.#pathVariable[pv] + '');
    }

    result += this.#serachParam.toString();

    return result;
  }
}

export async function request(url: string, options: RequestInit) {
  try {
    const response = await fetch(url, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    });

    return response;
  } catch (error) {
    console.log(`[요청 오류] ${error}`);
  }
}
