import { differenceInYears } from 'date-fns';
export const getAgeFromBirthDate = (birthdate: Date): number => {
  return differenceInYears(new Date(), birthdate);
};
