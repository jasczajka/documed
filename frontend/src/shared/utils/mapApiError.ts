export type ApiError = {
  status: number;
  message: string;
  code?: string;
} | null;

export const mapApiError = (error: any): ApiError => {
  if (!error) {
    return null;
  }

  if (!error.response) {
    return {
      status: 0,
      message: 'Problem z połączeniem. Sprawdź swoje połączenie internetowe.',
      code: 'NETWORK_ERROR',
    };
  }

  const { status, data } = error.response;

  switch (status) {
    case 400:
      return {
        status,
        message: data.message || 'Nieprawidłowe żądanie',
        code: data.code || 'BAD_REQUEST',
      };
    case 401:
      return {
        status,
        message: data.message || 'Brak autoryzacji',
        code: data.code || 'UNAUTHORIZED',
      };
    case 402:
      return {
        status,
        message: data.message || 'Wymagana płatność',
        code: data.code || 'PAYMENT_REQUIRED',
      };
    case 403:
      return {
        status,
        message: data.message || 'Brak dostępu',
        code: data.code || 'FORBIDDEN',
      };
    case 404:
      return {
        status,
        message: data.message || 'Nie znaleziono zasobu',
        code: data.code || 'NOT_FOUND',
      };
    case 405:
      return {
        status,
        message: data.message || 'Metoda niedozwolona',
        code: data.code || 'METHOD_NOT_ALLOWED',
      };
    case 406:
      return {
        status,
        message: data.message || 'Nieakceptowalne żądanie',
        code: data.code || 'NOT_ACCEPTABLE',
      };
    case 408:
      return {
        status,
        message: data.message || 'Przekroczono czas żądania',
        code: data.code || 'REQUEST_TIMEOUT',
      };
    case 409:
      return {
        status,
        message: data.message || 'Konflikt',
        code: data.code || 'CONFLICT',
      };
    case 410:
      return {
        status,
        message: data.message || 'Zasób niedostępny',
        code: data.code || 'GONE',
      };
    case 422:
      return {
        status,
        message: data.message || 'Nieprzetwarzalne żądanie',
        code: data.code || 'UNPROCESSABLE_ENTITY',
      };
    case 429:
      return {
        status,
        message: data.message || 'Zbyt wiele żądań',
        code: data.code || 'TOO_MANY_REQUESTS',
      };

    case 500:
      return {
        status,
        message: data.message || 'Wewnętrzny błąd serwera',
        code: data.code || 'INTERNAL_SERVER_ERROR',
      };
    case 501:
      return {
        status,
        message: data.message || 'Nie zaimplementowano',
        code: data.code || 'NOT_IMPLEMENTED',
      };
    case 502:
      return {
        status,
        message: data.message || 'Błąd bramy',
        code: data.code || 'BAD_GATEWAY',
      };
    case 503:
      return {
        status,
        message: data.message || 'Serwis niedostępny',
        code: data.code || 'SERVICE_UNAVAILABLE',
      };
    case 504:
      return {
        status,
        message: data.message || 'Przekroczony czas bramy',
        code: data.code || 'GATEWAY_TIMEOUT',
      };

    default:
      return {
        status,
        message: data.message || `Nieznany błąd (status ${status})`,
        code: data.code || 'UNKNOWN_ERROR',
      };
  }
};
