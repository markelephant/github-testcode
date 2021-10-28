package io.netty.example.study.fanxingTest;

public abstract class AlarmServiceImpl implements AlarmService {
    @Override
    public  <T>  void sendWarning(T t) {
        this.doSendWarning(t);
    }

    protected abstract <T> void doSendWarning(T t);


    public static void main(String[] args) {

    }
}
