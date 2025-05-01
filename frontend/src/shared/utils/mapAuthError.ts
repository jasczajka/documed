import { ApiError } from 'shared/utils/mapApiError';

export const mapAuthError = (error: any): ApiError => {
  if (!error) {
    return null;
  }
  if (!error.response) {
    return {
      status: 0,
      message: 'Problem z połączeniem. Sprawdź swoje połączenie internetowe.',
    };
  }

  const { status, data } = error.response;

  switch (status) {
    case 400:
      return {
        status,
        message: data.message || 'Nieprawidłowe dane logowania',
        code: 'INVALID_CREDENTIALS',
      };
    case 401:
      return {
        status,
        message: data.message || 'Nieprawidłowe dane logowania',
        code: 'UNAUTHORIZED',
      };
    case 403:
      return {
        status,
        message: data.message || 'Brak uprawnień do wykonania tej operacji',
        code: 'FORBIDDEN',
      };
    case 404:
      return {
        status,
        message: data.message || 'Użytkownik nie istnieje',
        code: 'NOT_FOUND',
      };
    case 410:
      return {
        status,
        message: data.message || 'Kod weryfikacyjny jest nieaktywny',
        code: 'GONE',
      };
    case 429:
      return {
        status,
        message: 'Zbyt wiele prób.',
        code: 'RATE_LIMITED',
      };
    case 409:
      return {
        status,
        message: 'Istnieje już użytkownik z podanym numerem PESEL lub adresem e-mail.',
        code: 'CONFLICT',
      };
    case 500:
    default:
      return {
        status,
        message: data.message || 'Wewnętrzny błąd serwera',
        code: 'SERVER_ERROR',
      };
  }
};
