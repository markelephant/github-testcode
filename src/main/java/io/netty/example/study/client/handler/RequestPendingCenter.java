package io.netty.example.study.client.handler;

import io.netty.example.study.common.Operation;
import io.netty.example.study.common.OperationResult;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RequestPendingCenter {
    private Map<Long,OperationResultFutuee> map = new ConcurrentHashMap<>();

    public void add(Long streamId,OperationResultFutuee futuee){
        this.map.put(streamId,futuee);
    }

    public void set(Long streamId, Operation operationResult){
        OperationResultFutuee operationResultFutuee = this.map.get(streamId);
        if(Objects.nonNull(operationResultFutuee)){
            operationResultFutuee.setSuccess(operationResult);
            this.map.remove(streamId);
        }
    }

}
