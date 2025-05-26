import { FC, useState } from 'react';
import { useCancelPlannedVisit } from 'shared/api/generated/visit-controller/visit-controller';
import ConfirmationModal from 'shared/components/ConfirmationModal/ConfirmationModal';

interface CancelVisitModalProps {
  visitId: number;
  onClose: () => void;
  onSuccess: () => void;
}

const CancelVisitModal: FC<CancelVisitModalProps> = ({ visitId, onClose, onSuccess }) => {
  const [loading, setLoading] = useState(false);
  const { mutateAsync: cancelVisit } = useCancelPlannedVisit();

  const handleConfirm = async () => {
    try {
      setLoading(true);
      await cancelVisit({ id: visitId });
      onSuccess();
      onClose();
    } finally {
      setLoading(false);
    }
  };

  return <ConfirmationModal onConfirm={handleConfirm} onCancel={onClose} loading={loading} />;
};

export default CancelVisitModal;
