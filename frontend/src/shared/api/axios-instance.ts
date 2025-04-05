import Axios, { AxiosError, AxiosRequestConfig } from 'axios';

export const AXIOS_INSTANCE = Axios.create({
  baseURL: import.meta.env.VITE_APP_API_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const customInstance = <T>(config: AxiosRequestConfig): Promise<T> => {
  const source = Axios.CancelToken.source();

  const promise = AXIOS_INSTANCE({
    ...config,
    cancelToken: source.token,
  }).then(({ data }) => data);

  // @ts-expect-error - We're adding cancel method to promise
  promise.cancel = () => {
    source.cancel('Query was cancelled by React Query');
  };

  return promise;
};

export type ErrorType<Error> = AxiosError<Error>;
