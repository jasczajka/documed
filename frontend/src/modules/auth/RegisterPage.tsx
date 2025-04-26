import { FC, useState } from 'react';
import { useNavigate } from 'react-router';
import { useAuth } from 'shared/hooks/useAuth';
import { useSitemap } from 'shared/hooks/useSitemap';
import { DocuMedLogo } from 'shared/icons/DocuMedLogo';
import { OTPInput } from './components/OtpInput';
import { RegisterForm, FormData as RegisterFormData } from './components/RegisterForm';

enum RegisterFormStep {
  REGISTER = 'REGISTER',
  OTP = 'OTP',
}

export const RegisterPage: FC = () => {
  const [currentStep, setCurrentStep] = useState<RegisterFormStep>(RegisterFormStep.OTP);
  const [registeredEmail, setRegisteredEmail] = useState('');
  const {
    requestRegister,
    confirmRegistration,
    requestRegisterError,
    confirmRegisterError,
    loading,
  } = useAuth();
  const navigate = useNavigate();
  const sitemap = useSitemap();

  const handleRegisterSubmit = async (data: RegisterFormData) => {
    try {
      await requestRegister({
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        pesel: data.pesel,
        password: data.password,
        confirmPassword: data.confirmPassword,
        phoneNumber: data.phoneNumber,
        address: data.address,
        birthdate: data.birthdate,
      });
      setRegisteredEmail(data.email);
      setCurrentStep(RegisterFormStep.OTP);
    } catch (error) {
      console.error('Registration error:', error);
    }
  };

  const handleOTPSubmit = async (otp: string) => {
    try {
      await confirmRegistration({ email: registeredEmail, otp });
      navigate(sitemap.main);
    } catch (error) {
      console.error('OTP verification error:', error);
    }
  };

  return (
    <main className="min-h-full-device flex h-full w-full min-w-[1440px] flex-col items-center justify-center pb-10">
      <DocuMedLogo className="text-primary w-[170px] pt-2 pb-10" />

      {currentStep === RegisterFormStep.REGISTER ? (
        <RegisterForm
          onSubmit={handleRegisterSubmit}
          error={requestRegisterError?.message}
          loading={loading}
        />
      ) : (
        <OTPInput
          email={registeredEmail}
          onSubmit={handleOTPSubmit}
          error={confirmRegisterError?.message}
          loading={loading}
        />
      )}
    </main>
  );
};

export default RegisterPage;
