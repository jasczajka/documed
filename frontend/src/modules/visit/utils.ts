import { VisitWithDetailsStatus } from 'shared/api/generated/generated.schemas';

export const getVisitStatusLabel = (
  status: VisitWithDetailsStatus,
): {
  label: string;
  color: 'success' | 'warning' | 'error' | 'default' | 'info' | 'indigo';
} => {
  switch (status) {
    case VisitWithDetailsStatus.PLANNED:
      return { label: 'Zaplanowana', color: 'info' };
    case VisitWithDetailsStatus.IN_PROGRESS:
      return { label: 'W trakcie', color: 'warning' };
    case VisitWithDetailsStatus.CLOSED:
      return { label: 'Zakończona', color: 'indigo' };
    case VisitWithDetailsStatus.CANCELLED:
      return { label: 'Anulowana', color: 'error' };
    default:
      return { label: 'Nieznany status', color: 'default' };
  }
};
