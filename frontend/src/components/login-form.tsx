import { cn } from '@/lib/utils';
import * as React from 'react';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Form, FormInput, InputChangeAction } from '@/components/ui/form';
import { Label } from '@/components/ui/label';
import { useForm } from '@/hooks/use-form';
import { useToast } from '@/hooks/use-toast';
import { SIGNUP_PAGE, ID_PATTERN, PASSWORD_PATTERN, MAIN_PAGE } from '@/config';
import { requestLogin } from '@/lib/requests/request-auth';

const LOGIN_FORM_PATTERN = {
  id: {
    defaultValue: '',
    validator: (input: string) => new RegExp(ID_PATTERN).test(input),
  },
  password: {
    defaultValue: '',
    validator: (input: string) => new RegExp(PASSWORD_PATTERN).test(input),
  },
};

export interface LoginForm {
  id: string;
  password: string;
}

export interface LoginFormProps extends React.ComponentProps<'div'> {}

export function LoginForm({ className, ...props }: LoginFormProps) {
  const [formData, setFormData, formIsValid] = useForm<LoginForm>(LOGIN_FORM_PATTERN);
  const { toast } = useToast();

  const inputChangeHandler: InputChangeAction = (type, name, event) => {
    setFormData(name, event.target.value);
  };
  const submitHandler: React.FormEventHandler<HTMLFormElement> = async (event) => {
    event.preventDefault();
    event.stopPropagation();

    if (!formIsValid) {
      toast({
        title: '사용자 오류',
        description: '아이디 또는 비밀번호 형식이 올바르지 않습니다.',
        variant: 'destructive',
      });
      return;
    }

    // 서버에 로그인 요청
    const response = await requestLogin(formData);

    // 로그인 요청 실패
    if (!response.success) {
      toast({
        title: response.error,
        description: response.message,
        variant: 'destructive',
      });
      return;
    }

    // 로그인 요청 성공 -> 리다이렉트
    location.href = MAIN_PAGE;
  };

  return (
    <div className={cn('flex flex-col gap-6', className)} {...props}>
      <Card>
        <CardHeader>
          <CardTitle className='text-2xl'>로그인</CardTitle>
          <CardDescription>계정 정보를 입력해 로그인하세요.</CardDescription>
        </CardHeader>
        <CardContent>
          <Form onInputChange={inputChangeHandler} onSubmit={submitHandler}>
            <div className='flex flex-col gap-6'>
              <div className='grid gap-2'>
                <Label htmlFor='id'>아이디</Label>
                <FormInput
                  id='id'
                  type='text'
                  name='id'
                  placeholder='아이디 입력...'
                  value={formData.id}
                  required
                />
              </div>
              <div className='grid gap-2'>
                <div className='flex items-center'>
                  <Label htmlFor='password'>비밀번호</Label>
                  {/* <a
                    href='#'
                    className='ml-auto inline-block text-sm underline-offset-4 hover:underline'
                  >
                    비밀번호를 잊으셨나요?
                  </a> */}
                </div>
                <FormInput
                  id='password'
                  type='password'
                  name='password'
                  placeholder='비밀번호 입력...'
                  value={formData.password}
                  required
                />
              </div>

              <Button type='submit' className='w-full'>
                로그인
              </Button>
            </div>
            <div className='mt-4 text-center text-sm'>
              계정이 없으신가요?{' '}
              <a href={SIGNUP_PAGE} className='underline underline-offset-4'>
                회원가입
              </a>
            </div>
          </Form>
        </CardContent>
      </Card>
    </div>
  );
}
