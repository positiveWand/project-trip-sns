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
import { Label } from '@/components/ui/label';
import { useForm } from '@/hooks/use-form';
import { useToast } from '@/hooks/use-toast';
import {
  MAIN_PAGE,
  ID_PATTERN,
  PASSWORD_PATTERN,
  EMAIL_PATTERN,
  NAME_PATTERN,
  LOGIN_PAGE,
} from '@/config';
import { Form, FormInput, InputChangeAction } from '@/components/ui/form';
import { requestSignup } from '@/lib/requests/request-auth';

const SIGNUP_FORM_PATTERN = {
  name: {
    defaultValue: '',
    validator: (input: string) => new RegExp(NAME_PATTERN).test(input),
  },
  id: {
    defaultValue: '',
    validator: (input: string) => new RegExp(ID_PATTERN).test(input),
  },
  email: {
    defaultValue: '',
    validator: (input: string) => new RegExp(EMAIL_PATTERN).test(input),
  },
  password: {
    defaultValue: '',
    validator: (input: string) => new RegExp(PASSWORD_PATTERN).test(input),
  },
  repassword: {
    defaultValue: '',
    validator: (input: string) => new RegExp(PASSWORD_PATTERN).test(input),
  },
};

export interface SignupForm {
  name: string;
  email: string;
  id: string;
  password: string;
  repassword: string;
}

export interface SignupFormProps extends React.ComponentProps<'div'> {}

export function SignupForm({ className, ...props }: SignupFormProps) {
  const [formData, setFormData, formIsValid] = useForm<SignupForm>(SIGNUP_FORM_PATTERN);
  const { toast } = useToast();

  const inputChangeHandler: InputChangeAction = (type, name, event) => {
    setFormData(name, event.target.value);
  };
  const submitHandler: React.FormEventHandler<HTMLFormElement> = async (event) => {
    event.preventDefault();
    event.stopPropagation();

    // alert(JSON.stringify(testResults));
    if (!formIsValid) {
      toast({
        title: '사용자 오류',
        description: '아이디 또는 비밀번호 형식이 올바르지 않습니다.',
        variant: 'destructive',
      });
      return;
    }
    if (formData.password != formData.repassword) {
      toast({
        title: '사용자 오류',
        description: '비밀번호가 일치하지 않습니다.',
        variant: 'destructive',
      });
      return;
    }

    // 서버에 회원가입 요청
    const response = await requestSignup(formData);

    // 회원가입 요청 실패
    if (!response.success) {
      toast({
        title: response.error,
        description: response.message,
        variant: 'destructive',
      });
      return;
    }

    // 회원가입 요청 성공 -> 리다이렉트
    location.href = LOGIN_PAGE;
  };

  return (
    <div className={cn('flex flex-col gap-6', className)} {...props}>
      <Card>
        <CardHeader>
          <CardTitle className='text-2xl'>회원가입</CardTitle>
          <CardDescription>계정 정보를 입력해 회원가입하세요.</CardDescription>
        </CardHeader>
        <CardContent>
          <Form onInputChange={inputChangeHandler} onSubmit={submitHandler}>
            <div className='flex flex-col gap-6'>
              <div className='grid gap-2'>
                <Label htmlFor='name'>이름</Label>
                <FormInput
                  id='name'
                  type='text'
                  placeholder='사용자 이름을 입력하세요'
                  name='name'
                  required
                />
              </div>

              <div className='grid gap-2'>
                <Label htmlFor='email'>이메일</Label>
                <FormInput
                  id='email'
                  type='email'
                  name='email'
                  placeholder='이메일을 입력하세요'
                  required
                />
              </div>

              <div className='grid gap-2'>
                <Label htmlFor='id'>아이디</Label>
                <FormInput
                  id='id'
                  type='text'
                  placeholder='영문자 또는 숫자 8~16자'
                  name='id'
                  required
                />
              </div>

              <div className='grid gap-2'>
                <Label htmlFor='password'>비밀번호</Label>
                <FormInput
                  id='password'
                  type='password'
                  name='password'
                  placeholder='영문자, 숫자, 특수문자 최소 1자씩 포함해 8~24자'
                  required
                />
                <FormInput
                  id='repassword'
                  type='password'
                  name='repassword'
                  placeholder='비밀번호를 재입력하세요'
                  required
                />
              </div>

              <Button type='submit' className='w-full'>
                회원가입
              </Button>
            </div>
          </Form>
        </CardContent>
      </Card>
    </div>
  );
}
