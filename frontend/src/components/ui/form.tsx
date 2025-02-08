import * as React from 'react';

import { Input } from '@/components/ui/input';

export type InputChangeAction = (
  type: React.HTMLInputTypeAttribute | undefined,
  name: string,
  event: React.ChangeEvent<HTMLInputElement>,
) => void;

const FormContext = React.createContext<InputChangeAction>(() => {});
const FormProvider = FormContext.Provider;

export interface FormProps extends React.ComponentProps<'form'> {
  onInputChange: InputChangeAction;
}

const Form = React.forwardRef<HTMLFormElement, FormProps>(
  ({ className, children, onInputChange, ...props }, ref) => {
    return (
      <FormProvider value={onInputChange}>
        <form className={className} ref={ref} {...props}>
          {children}
        </form>
      </FormProvider>
    );
  },
);

const FormInput = React.forwardRef<HTMLInputElement, React.ComponentProps<'input'>>(
  ({ className, type, name, onChange, ...props }, ref) => {
    const inputChangeAction = React.useContext(FormContext);

    onChange = (event) => {
      if (name === undefined) {
        return;
      }

      inputChangeAction(type, name, event);
    };

    return (
      <Input
        type={type}
        name={name}
        className={className}
        ref={ref}
        onChange={onChange}
        {...props}
      />
    );
  },
);

export { Form, FormInput };
