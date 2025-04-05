export const isValidPESEL = (pesel: string): boolean => {
  if (!pesel || pesel.length !== 11) return false;

  const weights = [1, 3, 7, 9, 1, 3, 7, 9, 1, 3];
  let sum = 0;

  for (let i = 0; i < 10; i++) {
    sum += parseInt(pesel.charAt(i)) * weights[i];
  }

  const checksum = (10 - (sum % 10)) % 10;
  return checksum === parseInt(pesel.charAt(10));
};

export const getBirthDateFromPESEL = (pesel: string): Date | null => {
  // if (!isValidPESEL(pesel)) return null;

  let year = parseInt(pesel.substring(0, 2));
  const month = parseInt(pesel.substring(2, 4));
  const day = parseInt(pesel.substring(4, 6));

  if (month >= 81 && month <= 92) {
    year += 1800;
  } else if (month >= 1 && month <= 12) {
    year += 1900;
  } else if (month >= 21 && month <= 32) {
    year += 2000;
  } else if (month >= 41 && month <= 52) {
    year += 2100;
  } else if (month >= 61 && month <= 72) {
    year += 2200;
  }

  const adjustedMonth = (month - 1) % 20;

  return new Date(year, adjustedMonth, day);
};
