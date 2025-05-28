import { VisitStatus } from 'shared/api/generated/generated.schemas';

export const getVisitStatusLabel = (
  status: VisitStatus,
): { label: string; color: 'success' | 'warning' | 'error' | 'default' | 'info' } => {
  switch (status) {
    case VisitStatus.PLANNED:
      return { label: 'Zaplanowana', color: 'info' };
    case VisitStatus.IN_PROGRESS:
      return { label: 'W trakcie', color: 'warning' };
    case VisitStatus.CLOSED:
      return { label: 'Zakończona', color: 'success' };
    case VisitStatus.CANCELLED:
      return { label: 'Anulowana', color: 'error' };
    default:
      return { label: 'Nieznany status', color: 'default' };
  }
};
