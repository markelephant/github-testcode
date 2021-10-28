package io.netty.example.study.fanxingTest;

public class AlarmOfflineHandler extends AlarmServiceImpl {
    @Override
    protected <OfflineTask> void doSendWarning(OfflineTask t) {

        System.out.println("OfflineTask   ...offline");

    }
}
