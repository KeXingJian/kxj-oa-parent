package com.kxj.process.service;

public interface MessageService {

    void pushPendingMessage(Long processId, Long userId, String taskId);
}
