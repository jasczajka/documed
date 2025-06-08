import dayjs from 'dayjs';

export const getYearAgoAsDateString = () => {
  return dayjs().subtract(1, 'year').format('YYYY-MM-DD');
};
