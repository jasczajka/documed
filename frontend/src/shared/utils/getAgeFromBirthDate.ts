import { differenceInYears } from 'date-fns';
export const getAge = (birthdate: Date): number => {
  return differenceInYears(new Date(), birthdate);
};
