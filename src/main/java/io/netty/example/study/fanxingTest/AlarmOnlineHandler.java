package io.netty.example.study.fanxingTest;

public class AlarmOnlineHandler extends AlarmServiceImpl {
    @Override
    protected <OnlineTask> void doSendWarning(OnlineTask t) {
        System.out.println("OnlineTask...onlineHandler");
    }
}
