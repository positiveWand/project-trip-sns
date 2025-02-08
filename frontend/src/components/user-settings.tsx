import { cn } from '@/lib/utils';
import { ComponentProps, useEffect, useState } from 'react';
import { Separator } from './ui/separator';
import { Form, FormInput } from './ui/form';
import { Label } from '@radix-ui/react-label';
import { InputChangeAction } from './ui/form';
import { Button } from './ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogFooter,
  DialogClose,
} from '@/components/ui/dialog';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog';
import { useForm } from '@/hooks/use-form';
import {
  EMAIL_PATTERN,
  ID_PATTERN,
  MAIN_PAGE,
  MY_PAGE,
  NAME_PATTERN,
  PASSWORD_PATTERN,
} from '@/config';
import { useToast } from '@/hooks/use-toast';
import { useUserSession } from '@/hooks/use-user-session';
import {
  requestDeleteAccount,
  requestUpdatePassword,
  requestUpdateProfile,
} from '@/lib/requests/request-auth';

const UPDATE_USER_FORM = {
  name: {
    defaultValue: '',
    validator: (input: string) => new RegExp(NAME_PATTERN).test(input),
  },
  email: {
    defaultValue: '',
    validator: (input: string) => new RegExp(EMAIL_PATTERN).test(input),
  },
};

export interface UpdateUserForm {
  name: string;
  email: string;
}

export default function UserSettings({ className }: ComponentProps<'div'>) {
  const [sessionIsActive, user] = useUserSession();
  const [formData, setFormData, formIsValid] = useForm<UpdateUserForm>(UPDATE_USER_FORM);
  useEffect(() => {
    if (!user) {
      return;
    }
    setFormData('name', user.name);
    setFormData('email', user.email);
  }, [user]);
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
        description: '이름 또는 이메일 형식이 올바르지 않습니다.',
        variant: 'destructive',
      });
      return;
    }

    const response = await requestUpdateProfile(formData);

    if (!response.success) {
      toast({
        title: response.error,
        description: response.message,
        variant: 'destructive',
      });
      return;
    }

    toast({
      title: '기본정보 변경 성공',
      description: '기본정보가 성공적으로 바뀌었습니다.',
    });

    location.href = MY_PAGE;
  };

  return (
    <div className={className}>
      <h1 className='text-4xl mb-8'>계정 설정</h1>

      <h1 className='text-2xl'>기본정보 바꾸기</h1>
      <Separator className='mt-2 mb-5' />

      <Form onInputChange={inputChangeHandler} onSubmit={submitHandler}>
        <div className='flex flex-col gap-5'>
          <div className='grid gap-2'>
            <Label htmlFor='id'>아이디</Label>
            <FormInput id='id' type='text' name='id' required value={user?.id} disabled />
          </div>
          <div className='grid gap-2'>
            <Label htmlFor='id'>이름</Label>
            <FormInput id='name' type='text' name='name' required value={formData.name} />
          </div>
          <div className='grid gap-2'>
            <Label htmlFor='email'>이메일</Label>
            <FormInput id='email' type='email' name='email' required value={formData.email} />
          </div>
          <div>
            <Button variant='outline' type='submit'>
              기본정보 바꾸기
            </Button>
          </div>
        </div>
      </Form>

      <h1 className='text-2xl mt-8'>비밀번호 바꾸기</h1>
      <Separator className='mt-2 mb-5' />
      <EditPasswordDialog />

      <h1 className='text-2xl mt-8'>회원 탈퇴하기</h1>
      <Separator className='mt-2 mb-5' />
      <DeleteAccountDialog />
    </div>
  );
}

const UPDATE_PASSWORD_FORM = {
  oldpassword: {
    defaultValue: '',
    validator: (input: string) => new RegExp(PASSWORD_PATTERN).test(input),
  },
  newpassword: {
    defaultValue: '',
    validator: (input: string) => new RegExp(PASSWORD_PATTERN).test(input),
  },
};

export interface UpdatePasswordForm {
  oldpassword: string;
  newpassword: string;
}

function EditPasswordDialog() {
  const [open, setOpen] = useState(false);
  const [formData, setFormData, formIsValid] = useForm<UpdatePasswordForm>(UPDATE_PASSWORD_FORM);
  const { toast } = useToast();

  const inputChangeHandler: InputChangeAction = (type, name, event) => {
    setFormData(name, event.target.value);
  };
  const submitHandler = async () => {
    // 유효성 검사
    if (!formIsValid) {
      toast({
        title: '사용자 오류',
        description: '비밀번호 형식이 올바르지 않습니다.',
        variant: 'destructive',
      });
      return;
    }

    // 비밀번호 변경 요청
    const response = await requestUpdatePassword({
      oldPassword: formData.oldpassword,
      newPassword: formData.newpassword,
    });

    // 비밀번호 변경 실패
    if (!response.success) {
      toast({
        title: response.error,
        description: response.message,
        variant: 'destructive',
      });
      return;
    }

    // 비밀번호 변경 성공
    setOpen(false);
    toast({
      title: '비밀번호 변경 성공',
      description: '비밀번호가 성공적으로 바뀌었습니다.',
    });
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant='outline' type='button'>
          비밀번호 바꾸기
        </Button>
      </DialogTrigger>
      <DialogContent className='sm:max-w-[425px]'>
        <DialogHeader>
          <DialogTitle>비밀번호 바꾸기</DialogTitle>
          <DialogDescription>
            기존 비밀번호와 새로운 비밀번호를 입력하고 버튼을 누르세요.
          </DialogDescription>
        </DialogHeader>
        <Form className='grid gap-4 py-4' onInputChange={inputChangeHandler}>
          <div className='grid gap-2'>
            <Label htmlFor='oldpassword'>기존 비밀번호</Label>
            <FormInput id='oldpassword' name='oldpassword' type='password' />
          </div>
          <div className='grid gap-2'>
            <Label htmlFor='newpassword'>새 비밀번호</Label>
            <FormInput id='newpassword' name='newpassword' type='password' />
          </div>
        </Form>
        <DialogFooter>
          <Button type='submit' onClick={submitHandler}>
            변경
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function DeleteAccountDialog() {
  const [open, setOpen] = useState(false);
  const { toast } = useToast();

  const submitHandler: React.MouseEventHandler<HTMLButtonElement> = async (event) => {
    event.preventDefault();

    // 탈퇴 요청
    const response = await requestDeleteAccount();

    // 탈퇴 요청 실패
    if (!response.success) {
      toast({
        title: response.error,
        description: response.message,
        variant: 'destructive',
      });
      return;
    }

    // 탈퇴 요청 성공
    location.href = MAIN_PAGE;
  };

  return (
    <AlertDialog open={open} onOpenChange={setOpen}>
      <AlertDialogTrigger asChild>
        <Button variant='destructive'>회원 탈퇴하기</Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>계정 탈퇴하기</AlertDialogTitle>
          <AlertDialogDescription>
            탈퇴된 계정과 계정 소유의 데이터는 영구적으로 삭제되고 복구될 수 없습니다.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>취소</AlertDialogCancel>
          <AlertDialogAction
            className='bg-destructive hover:bg-destructive/90'
            onClick={submitHandler}
          >
            탈퇴
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
